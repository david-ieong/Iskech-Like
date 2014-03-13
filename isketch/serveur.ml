(*
  ocamlc -o serveur -vmthread -custom unix.cma threads.cma str.cma serveur.ml -cclib -lthreads -cclib -lunix
*)
exception Fin ;;

(************** Fonction utile avec varbiable global ***********)
let fullPeople = Condition.create ();;
let motTrouver = Condition.create ();;
let allFind = Condition.create ();;
let partie = Mutex.create();;
let loginFile = Mutex.create();;
let broadcast = "BROADCAST" ;;

let gen_num = let c = ref 0 in (fun () -> incr c; !c) ;;

let my_input_line  fd = 
  let s = " "  and  r = ref "" in
  (try 
     while (ThreadUnix.read fd s 0 1 > 0) && s.[0] <> '\n' do r := !r ^s done ;
   with 
     read -> ();
  );
  !r ;;

let parse string = 
  let tabArg = Array.make 13 "" in 
  let i  = ref 0 in 
  let currentWord = ref "" in
  let size = ref 0 in 
  while (!i <  String.length string) do
    let char = string.[!i] in 
    if char = '/' then (
      tabArg.(!size) <- !currentWord;
      currentWord := "";
      i := !i + 1;
      size := !size + 1
    )  
    else if char = '\\' then (
      currentWord := !currentWord^ (String.make 1 char) ^(String.make 1 string.[!i+1]);
      i := !i + 2
    ) 
    else ( 
      currentWord := !currentWord^(String.make 1 char);
      i := !i + 1
    )
  done;
  tabArg;;

let debug string = 
  ignore (ThreadUnix.write Unix.stdin string 0 (String.length string));;

let timer temps cond= 
  Thread.delay temps;
  Condition.signal cond;;

let readFile file :string array= 
  let tabRes = ref [||] in 
  let f = open_in file in 
  try 
    while true do 
      let input_line = input_line f in 
      tabRes := Array.append !tabRes [|input_line|];
    done;
    !tabRes;
  with
    End_of_file -> (
      close_in f; 
      !tabRes
    )

let parseFile file =
  let r = Str.regexp ";" in 
  let listRes = ref [] in 
  let line = ref "" in 
  let listTmp = ref [] in
  try
    let f = open_in file in 
    while true do 
      line := input_line f;
      listTmp := Str.split r !line;
      listRes := (List.nth !listTmp 0, 
		  List.nth !listTmp 1, false)::!listRes;
    done;
    !listRes;
  with 
    |End_of_file -> (!listRes)
    |_ -> !listRes;;
      
let appendFile user password = 
  Mutex.lock loginFile;
  let chan = open_out_gen [Open_append;Open_creat] 0 "login" in
  output_string chan (Printf.sprintf "%s;%s;000000;000000;000000;\n" user password);
  flush chan;
  close_out chan;
  Mutex.unlock loginFile;;

let update_wins name win_lose =
  Mutex.lock loginFile;
  let listTmp = ref [] in 
  let line = ref "" in
  let nb = ref 0 in
  let inchan = open_in "login" in
  let nbZero = ref "000000" in
  begin
    try
      while true do 
	line := input_line inchan;
        listTmp := Str.split (Str.regexp ";") !line;
	if name = List.nth !listTmp 0 then  (
	  let pos = pos_in inchan - (String.length !line) - 1 in 
	  if win_lose then 
	    nb := int_of_string (List.nth !listTmp 3)
	  else
	    nb := int_of_string (List.nth !listTmp 4);
	  let outchan = open_out_gen [Open_wronly] 0 "login" in
	  seek_out outchan pos;
	  let total = (string_of_int (!nb+1)) in
	  nbZero := Str.string_after !nbZero (String.length total);
	  if win_lose then 
	    output_string outchan (Printf.sprintf "%s;%s;%s;%s;%s;\n" 
				     (List.nth !listTmp 0) (List.nth !listTmp 1)
				     (List.nth !listTmp 2) (!nbZero^total) (List.nth !listTmp 4))
	  else
	    output_string outchan (Printf.sprintf "%s;%s;%s;%s;%s;\n" 
				     (List.nth !listTmp 0) (List.nth !listTmp 1)
				     (List.nth !listTmp 2) (List.nth !listTmp 3) (!nbZero^total));
	  flush outchan;
	  close_out outchan;
	  raise Fin;
	)
      done;
      close_in inchan
    with
    | Fin -> close_in inchan;
    | End_of_file -> Printf.printf "TRACE : user %s is not registred\n%!" name
    | Failure "int_of_string" -> Printf.printf "TRACE.exn : malformed logins file\n%!";
  end;
  Mutex.unlock loginFile;;

let update_score name score =
  Mutex.lock loginFile;
  let listTmp = ref [] in 
  let line = ref "" in
  let nb = ref 0 in
  let inchan = open_in "login" in
  let nbZero = ref "000000" in
  begin
    try
      while true do 
	line := input_line inchan;
        listTmp := Str.split (Str.regexp ";") !line;
	if name = List.nth !listTmp 0 then  (
	  let pos = pos_in inchan - (String.length !line) - 1 in
	  nb := int_of_string (List.nth !listTmp 2);
	  let outchan = open_out_gen [Open_wronly] 0 "login" in
	  seek_out outchan pos;
	  let total = (string_of_int (!nb+score)) in
	  nbZero := Str.string_after !nbZero (String.length total);
	  output_string outchan (Printf.sprintf "%s;%s;%s;%s;%s;\n" 
				   (List.nth !listTmp 0) (List.nth !listTmp 1)
				   (!nbZero^total) (List.nth !listTmp 3) (List.nth !listTmp 4));
	  flush outchan;
	  close_out outchan;
	  raise Fin;
	)
      done;
      close_in inchan
    with
    | Fin -> close_in inchan;
    | End_of_file -> Printf.printf "TRACE : user %s is not registred\n%!" name
    | Failure "int_of_string" -> Printf.printf "TRACE.exn : malformed logins file\n%!";
  end;
  Mutex.unlock loginFile;;

(************************Classe Connexion *****************)
class virtual connexion (sd:Unix.file_descr) (sa : Unix.sockaddr) b = 
object (self) 
  val s_descr = sd
  val s_addr = sa
  val mutable numero = 0
  val mutable score = 0 
  val mutable user_name = ""
  val mutable connected = false
  val mutable haveDraw = false
  val mutable haveFound = false
  val mutable inGame = false
  val mutable registred = false
    
  initializer 
    numero <- gen_num();
    Printf.printf "TRACE.connexion : objet traitant %d créé\n" numero ;
    print_newline();

  method start () =  Thread.create (fun x -> self#run x ;) ()    
  method getDescr () = s_descr
  method getScore () = score
  method setScore newScore  = score <- newScore
  method addScore newScore = score <- score + newScore
  method getUserName () = user_name
  method setUserName str = user_name <- str
  method haveDraw () = haveDraw
  method setHaveDraw b = haveDraw <- b
  method haveFound () = haveFound
  method setHaveFound b = haveFound <- b
  method setConnected b = connected <- b 
  method isConnected () = connected
  method isInGame () = inGame
  method setIsInGame b = inGame <- b
  method isRegistred () = registred
  method setRegistred b = registred <- b
  method virtual stop :unit -> unit 
  method virtual run : unit -> unit
end ;;

(***********************Classe Partie ********************************)
class partie serveur = 
object(self)
  val serv = serveur
  val mutable drawer = ""
  val mutable drawer_descr = Unix.stdin
  val mutable round = 0
  val mutable nb_player = 0 
  val mutable whoFind = [||]
  val mutable tabJoueur:connexion array = [||]

    
  method start () = 
    let t2 = ref (Thread.create (fun _ -> ()) ()) in 
    while true do 
      Condition.wait fullPeople partie;
      Mutex.unlock partie;
      self#initGame();
      while round <> nb_player && round < nb_player do
	self#initRound ();
	serv#writeToAll broadcast "2 minutes pour trouver le mot" Unix.stdin;
	let t1 =  Thread.create (fun _ -> timer (120.0 -. (serv#getTimeout())) motTrouver) () in
	Condition.wait motTrouver partie;
	nb_player <- serv#getSize();
	Mutex.unlock partie;
	whoFind <- serv#getTabFinder();
	if serv#getSize() <> serv#getNbFinder() then (
	  if whoFind.(1) <> "" then 
	    serv#writeToAll broadcast "Activation du timeout !!!" Unix.stdin;
	  let timeoutString = "Round Finish in "^ (string_of_int (int_of_float (serv#getTimeout())))^" secondes" in
	  serv#writeTimeout ();
	  serv#writeToAll broadcast timeoutString Unix.stdin;
	  t2 :=  Thread.create (fun _ -> timer (serv#getTimeout()) allFind) ();
	  Condition.wait allFind partie;
	  Mutex.unlock partie;
	);
	nb_player <- serv#getSize();
	whoFind <- serv#getTabFinder();
	self#roundFinish whoFind;
	if round <> nb_player then (
	  serv#writeToAll broadcast "New Round in 10 secondes" Unix.stdin;
	  Thread.delay 10.0;
	);
	(try 
	   Thread.kill t1;
	 with 
	     Failure e -> debug "Thread already dead\n";
	);
	(try 
	   Thread.kill !t2;
	 with
	     Failure e -> debug "Thread already dead\n";
	);
      done;
      self#gameOver();
    done
      
  method private initGame () = 
    debug "J'init la partie\n";
    nb_player <- serv#getSize();
    serv#resetAllScore ();
    round <- 0;
    serv#writeToAll broadcast "New Game Starting" Unix.stdin;
    debug "Fin init partie\n";
    
  method private initRound () =
    debug "Debut Init Round\n";
    tabJoueur <- [||];
    tabJoueur <- serv#getAllClients();
    round <- round + 1; 
    serv#setNewWord ();
    serv#setR "0";
    serv#setG "0";
    serv#setB "0";
    serv#setStroke "1";
    serv#setCountScore true;
    serv#setCheatNumber 3;
    serv#setAllNotFound ();
    serv#resetCommandeList();
    serv#clearFinder();
    self#select_drawer;
    whoFind <- [||];
    serv#writeRound drawer_descr;
    serv#broadcastToUser "Tu es le dessinateur" drawer_descr;
    debug "Fin init round\n"

  method private select_drawer = 
    debug "Choix du dessinateur\n";
    let found = ref false in 
    let i = ref 0 in
    while not !found do
      i := Random.int (Array.length tabJoueur);
      let joueur = tabJoueur.(!i) in
      if not (joueur#haveDraw()) && joueur#getUserName() <> "" && joueur#isInGame() then 
	begin
	  serv#addFinder drawer;
	  drawer <- joueur#getUserName();
	  drawer_descr <- joueur#getDescr();
	  joueur#setHaveDraw true;
	  serv#setCurrentDrawer drawer;
	  found := true 
	end
    done;
    debug "Fin du choix du dessinateur\n";
    
  method private roundFinish (tabFinder:string array) = 
    let mot = serv#getWordToFind() in 
    let name = ref "" in
    let score = ref 10 in 
    let scoreDess = ref 0 in 
    if serv#getCountScore () then (
      for i = 1 to serv#getNbFinder() -1 do
	name := tabFinder.(i) ;
	serv#setUserScore !name !score;
	update_score !name !score;
	if i = 1 then scoreDess := !scoreDess + 10
	else scoreDess := !scoreDess + 1;
	if !score <> 5 then 
	  score := !score - 1;
      done;
      if !scoreDess > 15 then scoreDess := 15;
      serv#setUserScore drawer !scoreDess;
      update_score drawer !scoreDess;
      self#updateRegisterStat tabFinder.(1);
      serv#writeToAll broadcast "Round terminé !!!" Unix.stdin;
      serv#writeEndRound (tabFinder.(1)) mot;
      let strScoreDes = "Score du dessinateur : " ^ (string_of_int !scoreDess) ^ ""  in
      serv#writeToAll broadcast  strScoreDes Unix.stdin;
      serv#writeScore ();
      
    ) else (
      serv#writeToAll broadcast "Round terminé !!!" Unix.stdin;
      serv#writeToAll broadcast "Un joueur a triché !!!" Unix.stdin;
    )

  method private updateRegisterStat winner =
    let client = ref tabJoueur.(0) in
    for i = 0 to Array.length tabJoueur -1 do 
      client := tabJoueur.(i);
      if !client#isRegistred()  then 
	if !client#getUserName() = drawer && Array.length whoFind = serv#getSize() then
	  update_wins drawer true
	else if !client#getUserName() = winner  then
	  update_wins winner true
	else
	  update_wins (!client#getUserName()) false;
    done

  method private gameOver () = 
    let clients = serv#getAllClients() in 
    let scoreString = ref "Score Final\n" in
    let client = ref clients.(0) in
    serv#setAllNotDraw();
    serv#writeToAll broadcast "Partie terminée !!!!!!" Unix.stdin;
    debug "Partie Fini !!!!!!!!!!\n";
    for i = 0 to Array.length clients -1 do
      client := clients.(i);
      if !client#getUserName() <> "" && !client#isInGame() then
	scoreString := !scoreString ^ "BROADCAST : " ^ !client#getUserName() ^
	  " " ^ (string_of_int (!client#getScore())) ^ "\n";
    done;
    serv#writeToAll broadcast !scoreString Unix.stdin;
end;;

(************* Classe Server ********************)
class virtual server port n file time= 
object(self)
  val port_num = port
  val nb_pending = n 
  val sock = ThreadUnix.socket Unix.PF_INET Unix.SOCK_STREAM 0
  val sockets = Array.make n ("", Unix.stdin)
  val timeout = time
  val tabFinder = Array.make n ""
  val mutable tabMot = [||]
  val mutable clients:connexion list = []
  val mutable commandeList:string list = [] 
  val mutable spectactorList:connexion list = []
  val mutable loginList = []
  val mutable size = 0
  val mutable currentDrawer = ""
  val mutable findThis = ""
  val mutable nb_Finder = 0
  val mutable cheatNumber = 3
  val mutable r = "0"
  val mutable g = "0" 
  val mutable b = "0"
  val mutable stroke = "1"
  val mutable countScore = true

  method start () = 
    let sock_addr = Unix.ADDR_INET(Unix.inet_addr_any, port_num) in 
    Unix.bind sock sock_addr;
    Unix.listen sock nb_pending;
    ignore (Thread.create (fun _ -> (new partie self)#start()) ());
    tabMot <- readFile file;
    loginList <- parseFile "login";
    while true do 
      let (service_sock, client_sock_addr) = ThreadUnix.accept sock in
      self#treat service_sock client_sock_addr;
    done;

  method setLoginList list = loginList <- list
  method getLoginList () = loginList
  method addLoginList elem = loginList <- elem::loginList
  method getCountScore () = countScore
  method setCountScore b = countScore <- b
  method getCheatNumber() = cheatNumber
  method setCheatNumber number = cheatNumber <- number 
  method getTimeout () = timeout
  method setR value = r <- value
  method setG value = g <- value
  method setB value = b <- value
  method setStroke value = stroke <- value
  method getR () = r
  method getG () = g
  method getB () = b
  method getStroke () = stroke
  method getSize () = size    
  method incr () = size <- size + 1
  method decr () = size <- size - 1 
  method getClient i = List.find i clients
  method getAllClients () = Array.of_list clients
  method getSpectactor () = spectactorList
  method addSpectactor spec = spectactorList <- spec::spectactorList 
  method full () = size = nb_pending 
  method setCurrentDrawer user = currentDrawer <- user
  method getCurrentDrawer () = currentDrawer
  method setNewWord () = 
    let i = Random.int (Array.length tabMot) in 
    findThis <- tabMot.(i); 

  method getWordToFind () = findThis
    
  method getNbFinder () = nb_Finder
  method getTabFinder () = tabFinder
  method addFinder user = 
    tabFinder.(nb_Finder) <- user;
    nb_Finder <- nb_Finder + 1
      
  method clearFinder () = 
    for i = 0 to Array.length tabFinder -1 do
      if tabFinder.(i) <> "" then 
	tabFinder.(i) <- ""
    done;
    nb_Finder <- 0
					  
  method getCommandeList () = 
    List.rev commandeList
  method addToCommande string = 
    commandeList <- string::commandeList;
  method resetCommandeList () = commandeList <- []

  method virtual removeClient : connexion -> unit
  method virtual removeSpectactor : connexion -> unit
  method virtual setAllNotFound : unit -> unit 
  method virtual setAllNotDraw : unit -> unit
  method virtual broadcastToUser : string -> Unix.file_descr -> unit
  method virtual broadcastToNotUser : string -> Unix.file_descr -> unit
  method virtual resetAllScore : unit -> unit 
  method virtual setUserScore : string -> int -> unit 
  method virtual treat : Unix.file_descr -> Unix.sockaddr -> unit  
  method virtual notifyConnected : string -> Unix.file_descr -> bool -> unit
  method virtual add : string -> Unix.file_descr -> connexion -> bool -> bool
  method virtual getUserName : Unix.file_descr -> string
  method virtual writeToAll : string -> string -> Unix.file_descr -> unit 
  method virtual disconnectUser : Unix.file_descr -> unit 
  method virtual writeLine : string -> string -> string -> string -> unit 
  method virtual writeGuess : string -> string -> unit 
  method virtual writeRound  : Unix.file_descr -> unit 
  method virtual writeScore : unit -> unit
  method virtual writeAllUser : unit -> unit
  method virtual writeClear : unit -> unit
  method virtual writeCourbe : string -> string -> string -> string -> string -> string -> string -> string -> unit
  method virtual nextTurn : connexion -> unit 
  method virtual writeEndRound : string -> string -> unit
  method virtual cheat : string -> unit
  method virtual login : string -> string -> connexion -> unit
  method virtual register : string -> string -> connexion -> unit 
  method virtual modifyList : string -> bool -> (string * string * bool)list
  method virtual writeTimeout : unit -> unit 
  method virtual writeFound : string -> unit 
  method virtual writeAccessDenied : Unix.file_descr -> string -> unit 
end;;


(*************** Classe connexion_maj extends connexion ***************)
class connexion_maj sd sa b serveur = 
object(self) 
  inherit connexion sd sa b
  val serv = serveur

  method stop () =
    if user_name <> "" && inGame then (
      serv#disconnectUser s_descr;
      serv#setLoginList (serv#modifyList user_name false);
    );
    Printf.printf "TRACE.connexion : fin objet traitant %d\n" numero ;
    print_newline () ;
    serv#removeClient self;
    serv#removeSpectactor self;
    if user_name = serv#getCurrentDrawer() || serv#getSize() = 1 then ( 
      Condition.signal motTrouver;
      if serv#getNbFinder() = 1 then (
	Thread.delay 2.0;
	Condition.signal allFind;
      )
    );
    (try 
       Unix.close s_descr;
     with
     |close -> ();
    ); 
    serv#writeAllUser();

  method run () = 
    try 
      let result = ref "" in
      let commande = ref "" in
      while true do
	result := "";
        let ligne =  my_input_line s_descr in
	let tabArg = parse ligne in
	commande := tabArg.(0);
	(match !commande with 
	  |"CONNECT" -> 
	    if connected then 
	      result := "Already connected\n"
	    else if (serv#add tabArg.(1) sd self true) then (
	      Mutex.lock partie;
	      user_name <- serv#getUserName sd;
	      connected <- true;
	      inGame <- true;
	      registred <- false;
	      serv#notifyConnected user_name s_descr true;
	      serv#writeAllUser ();
	      Mutex.unlock partie;
	      if serv#full() then 
		Condition.signal fullPeople;
	    ) 
	    else result := "No more place retry in few minutes\n";
	  | "SPECTACTOR" ->  
	    ignore (serv#add tabArg.(1) sd self false);
	    serv#notifyConnected (user_name^" as Spectactor") s_descr true;
	  | "LOGIN" -> 
	    if not connected then
	      serv#login tabArg.(1) tabArg.(2) self;
	  | "REGISTER" -> 
	    if not connected then  
	      serv#register tabArg.(1) tabArg.(2) self;
	  | "EXIT" -> 
	    if not connected then 
	      result := "Il faut etre connecté\n"
	    else
	      if tabArg.(1) = user_name then
		self#stop()
	      else
		result := "Impossible de quitter quelqu'un d'autre\n"
	  |"GUESS" -> 
	    if not inGame  then 
	      result := "Tu ne peux pas deviner le mot !!!\n"
	    else if serv#getCurrentDrawer() <> user_name then (
	      if self#haveFound() then 
		result := "Tu as déjà trouvé le mot !!!\n"
	      else  if serv#getWordToFind() = tabArg.(1) then (
		self#setHaveFound true;
		serv#addFinder user_name;
		serv#writeFound user_name;
		if serv#getNbFinder () = serv#getSize() then (
		  serv#writeToAll broadcast
		    "Tous le monde a trouvé le mot" Unix.stdin;
		  Condition.signal motTrouver;
		  Thread.delay 1.0;
		  Condition.signal allFind;
		) else if serv#getNbFinder() = 2 then (
		  debug "Jenvoie mot trouver\n";
		  Condition.signal motTrouver
		)
	      )
	      else 
		serv#writeGuess user_name tabArg.(1)
	    )
	    else
	      result := "Tu es le dessinateur !!!"
	  | "TALK" -> serv#writeToAll user_name tabArg.(1) s_descr
	  | "SET_COLOR" -> 
	      serv#setR tabArg.(1);
	      serv#setG tabArg.(2);
	      serv#setB tabArg.(3);
	  | "SET_SIZE" -> serv#setStroke tabArg.(1)
	  | "SET_LINE" -> serv#writeLine tabArg.(1) tabArg.(2) tabArg.(3) tabArg.(4)
	  | "SET_COURBE" -> 
	    serv#writeCourbe tabArg.(1) tabArg.(2) tabArg.(3) tabArg.(4) tabArg.(5) tabArg.(6) tabArg.(7) tabArg.(8)
	  | "CLEAR_COMMANDE" -> serv#writeClear()
	  | "PASS" -> serv#nextTurn self
	  | "CHEAT" ->  (serv#cheat tabArg.(1));
	  | _ ->  raise Fin
	);
	if (ligne = "") or (ligne = "\013") then raise Fin;
	if !result <> "" then 
	  serv#broadcastToNotUser !result (self#getDescr());
      done
    with
	Fin  -> self#stop()
      | exn  -> print_string (Printexc.to_string exn) ; print_newline() 
end ;;


(*****************Classe server_maj extends server **********************)
class server_maj port n file time= 
object(self) 
  inherit server port n file time
  method treat s sa = 
    let client = new connexion_maj s sa true (self:> server_maj) in 
    clients <- client::clients;
    ignore (client#start())

  method add str descr con b=
    let name = ref str in
    let placerQuestion = ref true in
    let noMorePlace = ref false in
    let i = ref 0 in
    let numeroName = ref 1 in
    if b then (
      if  size = nb_pending then (
	placerQuestion := false;
	noMorePlace := true;
      );
      if con#getUserName() <> "" then 
	self#removeSpectactor con;
      
      name:= (self#checkExist !name "_");
      
      while (!placerQuestion) do  
	let (a, _)  =  sockets.(!i) in
	if a = !name && !placerQuestion then
	  (
	    name := str^"_"^(string_of_int !numeroName);
	    i := 0;
	    numeroName := !numeroName + 1
	  )
	else if (a = "" && !i = size ) then 
	  placerQuestion := false		
	else
	  i := !i + 1
      done;
      
      if !noMorePlace then (
	not !noMorePlace
      )
      else if !placerQuestion = false then (
	sockets.(size) <- (!name, descr);
	self#incr();
	if size = nb_pending then 
	  Condition.signal fullPeople;
	not !placerQuestion
      )
      else (
	not !placerQuestion
      )
    )
    else (
      let findUser = ref true in
      let i = ref 0 in 
      let name = ref str in
      let numeroSpec = ref 1 in
      let sizeSpec = List.length spectactorList in
      if con#isInGame() then (
	self#disconnectUser descr;
	if con#getUserName() = self#getCurrentDrawer() || self#getSize() = 1 then ( 
	  Condition.signal motTrouver;
	  if self#getNbFinder() = 1 then (
	    Thread.delay 1.0;
	    Condition.signal allFind;
	  )
	);
      );
      
      name := self#checkExist !name "#";
      
      while !findUser do
	if !i = sizeSpec  then (
	  findUser := false;
	)
	else if !findUser then 
	  let conTmp  =  List.nth spectactorList !i in
	  if conTmp#getUserName() = !name then (
	    name := !name^"#"^(string_of_int !numeroSpec);
	    i := 0;
	    numeroSpec := !numeroSpec + 1
	  ) 		
	  else (
	    i := !i + 1
	  );
      done;
      if (con#isRegistred ()) then 
	loginList <- self#modifyList str false; 
      con#setUserName !name;
      con#setRegistred false;
      self#addSpectactor con;       
      con#setIsInGame false;
      con#setConnected false;
      self#writeAllUser();
      List.iter (fun x -> ignore (ThreadUnix.write descr x 0 (String.length x))) (self#getCommandeList());
      true;
    )

  method private checkExist name symbole = 
    let nameTmp = ref name in 
    let number = ref 0 in 
    let rec f list = 
      match list with 
      |[] -> !nameTmp
      |(u, _, _)::y -> 
	if (u = !nameTmp || u = "BROADCAST") then (
	  nameTmp := name ^ symbole ^ (string_of_int !number);
	  number := !number + 1;
	  f loginList
	) else 
	  f y;
    in
    f loginList
      

  method login user pwd con = 
    let exist = ref false in 
    let connected = ref false in 
    (try 
       List.iter (fun x -> match x with 
       |(u, password, b) -> (
	 if (u = user || user = "BROADCAST") && pwd = password then (
	   connected := b;
	   raise Fin;
	 )
       )
       ) (self#getLoginList());
       exist := false;
     with 
       Fin -> exist := true;
    );
    if not(!exist) then (
      self#writeAccessDenied (con#getDescr()) "ACCESS DENIED, YOU WILL BE KICK";
      Thread.delay 2.0;
      con#stop();
    ) else (
      if !connected then 
	self#broadcastToNotUser "ALREADY CONECTED !!!" (con#getDescr())
      else (
	if (self#addLogin user con) then (
	  loginList <- (self#modifyList user true);
	  con#setRegistred true;
	  self#removeSpectactor con;
	  con#setUserName user;
	  con#setConnected true;
	  con#setIsInGame true;
	  self#notifyConnected user (con#getDescr()) true;
	  self#writeAllUser ();
	)
	else (
	  self#writeAccessDenied (con#getDescr()) "ACCESS DENIED TO MANY PEOPLE !!!";
	)
      )
    )

  method private addLogin user con =
    let cond = ref true in
    if size = nb_pending then (
      cond := false;
    )
    else (
      sockets.(size) <- (user, con#getDescr());
      self#incr();
      if size = nb_pending then 
	Condition.signal fullPeople;
      cond:= true;
    );
    !cond
      
  method register user pwd con = 
    let exist = ref false in 
    (try 
       List.iter (fun x -> match x with 
       |(u, _, b) -> 
	 if u = user || user = "BROADCAST" then 
	   raise Fin;
       ) (self#getLoginList());
       exist := false;
     with 
     |Fin -> exist := true;
    );
    if !exist then (
      self#writeAccessDenied (con#getDescr()) "ACCESS DENIED, YOU WILL BE KICK";
      Thread.delay 2.0;
      con#stop();
    ) else (
      self#addLoginList (user, pwd, true);
      appendFile user pwd;
      if (self#addLogin user con) then (
	con#setRegistred true;
	self#removeSpectactor con;
	con#setUserName user;
	con#setConnected true;
	con#setIsInGame true;
	self#notifyConnected user (con#getDescr()) true;
	self#writeAllUser ();
      ) else (
	self#writeAccessDenied (con#getDescr()) "ACCESS DENIED TO MANY PEOPLE !!!" ;
      )	 
    )

  method broadcastToNotUser str descr = 
    let string = "LISTEN/BROADCAST/"^str^"/\n" in
    ignore (ThreadUnix.write descr string 0 (String.length string));

  method writeAccessDenied descr str= 
    let string = "ACCESSDENIED/"^str^"/\n" in
    ignore (ThreadUnix.write descr string 0 (String.length string));

  method modifyList user bool = 
    let rec f list acc = 
      match list with 
      |[] -> acc
      |((u, p, _) as x)::y -> 
	if (u = user) then (
	  let xprim = (u, p, bool) in
	  xprim::y@acc
	)
	else
	  f y (x::acc);
    in
    f loginList []

  method removeClient client = 
    let rec remove l fl = 
      match l with 
	[] -> fl
      |x::y -> 
	if x = client then 
	  fl@y
	else
	  remove y (x::fl)
    in
    clients <- remove clients [];

  method removeSpectactor spectactor = 
    let rec remove l fl = 
      match l with 
	[] -> fl
      |x::y -> 
	if x = spectactor then 
	  fl@y
	else
	  remove y (x::fl)
    in
    spectactorList <- remove spectactorList [];
    
  method getUserName descr = 
    let res = ref "" in
    try  
      for i = 0 to size do 
	let (a, s) = sockets.(i) in
	if s = descr then (
	  res := a;
	  raise Fin;
	)
      done;
      !res;
    with 
      Fin -> !res

  method notifyConnected str descr b = 
    let string = ref "" in 
    if b then string := "CONNECTED/"^str^"/\n"
    else  string := "EXITED/"^str^"/\n";
    for i = 0 to size-1 do
      let (_ , s) = sockets.(i) in
      if b && descr = s then (
	let welcome = "WELCOME/"^str^"/\n" in
	ignore (ThreadUnix.write s welcome 0 (String.length welcome));)
      else
	ignore (ThreadUnix.write s !string 0 (String.length !string));
    done;
    List.iter (fun x -> if x#getDescr() = descr then 
	let welcome = "WELCOME/"^str^"/\n" in
	ignore (ThreadUnix.write (x#getDescr()) welcome 0 (String.length welcome))
      else
	ignore (ThreadUnix.write (x#getDescr()) !string 0 (String.length !string)))
      (self#getSpectactor());
    
  method writeToAll user str descr =
    let string = "LISTEN/"^user^"/"^str^"/\n" in
    for i = 0 to size-1 do
      let (u , s) = sockets.(i) in
      ignore (ThreadUnix.write s string 0 (String.length string));
    done;
    List.iter (fun x -> ignore (ThreadUnix.write (x#getDescr()) string 0 (String.length string))) (self#getSpectactor());

  method disconnectUser descr = 
    let user = ref "" in
    (try
       for i = 0 to size -1 do 
	 let (u, s) = sockets.(i) in 
	 if s = descr then (
	   user := u;
	   for j = i+1 to size -1 do 
	     let sock = sockets.(j) in
	     sockets.(j-1) <- sock;
	   done;
	   sockets.(size-1) <- ("", Unix.stdin);
	   self#decr();
	   raise Fin;
	 )
       done;
     with 
       Fin -> self#notifyConnected !user Unix.stdin false;
    );

  method setAllNotFound () =
    List.iter (fun x -> x#setHaveFound false) clients;
    nb_Finder <- 0

  method setAllNotDraw () = 
    List.iter (fun x -> x#setHaveDraw false) clients

  method broadcastToUser str descr = 
    let string = "LISTEN/"^broadcast^"/"^str^"/\n" in
    try 
      for i = 0 to size-1 do
	let (_, s) = sockets.(i) in
	if s = descr then (
	  ignore (ThreadUnix.write s string 0 (String.length string));
	  raise Fin;
	)
      done
    with 
      Fin -> () 

  method writeLine x1 y1 x2 y2 = 
    let string = "LINE/"^x1^"/" ^y1^"/" ^x2^"/" ^y2^"/" ^r^
      "/" ^g^"/" ^b^"/" ^stroke^"/\n" in
    self#addToCommande string;
    for i = 0 to size-1 do
      let (_ , s) = sockets.(i) in
      ignore (ThreadUnix.write s string 0 (String.length string));
    done;
    List.iter (fun x -> ignore (ThreadUnix.write (x#getDescr()) string 0 (String.length string))) (self#getSpectactor());

  method writeCourbe x1 y1 x2 y2 x3 y3 x4 y4 = 
    let string = "COURBE/"^x1^"/" ^y1^"/" ^x2^"/" ^y2^"/"^x3^"/" ^y3^"/" ^x4^"/" ^y4^"/" ^r^"/" ^g^"/" ^b^"/" ^stroke^"/\n" in
    self#addToCommande string;
    for i = 0 to size-1 do
      let (_ , s) = sockets.(i) in
      ignore (ThreadUnix.write s string 0 (String.length string));
    done;
    debug string;
    List.iter (fun x -> ignore (ThreadUnix.write (x#getDescr()) string 0 (String.length string))) (self#getSpectactor());
    
  method writeGuess user str = 
    let string = "GUESSED/"^user^"/"^str^"/\n" in
    for i = 0 to size-1 do
      let (_, s) = sockets.(i) in
      ignore (ThreadUnix.write s string 0 (String.length string));
    done
      
  method writeRound descr = 
    let string = ref "NEW_ROUND/" in
    for i = 0 to size-1 do
      let (_, s) = sockets.(i) in
      if s <> descr then 
	string := !string ^ "guess/"^ self#getCurrentDrawer() ^"/\n"
      else
	string := !string ^ "draw/"^ self#getCurrentDrawer() ^ "/"^ findThis^"/\n";
      debug !string;
      ignore (ThreadUnix.write s !string 0 (String.length !string));
      string := "NEW_ROUND/";
    done;
    List.iter (fun x -> ignore (ThreadUnix.write (x#getDescr()) !string 0 (String.length !string))) (self#getSpectactor());

  method writeScore () = 
    let string = ref "SCORE/" in
    List.iter (fun x -> 
      if x#isInGame () then(
	let user = x#getUserName() in 
	let score = string_of_int (x#getScore()) in
	string := !string^user^"/"^score^"/")) clients;
    string := !string ^ "\n";
    for i = 0 to size-1 do
      let (_, s) = sockets.(i) in
      ignore (ThreadUnix.write s !string 0 (String.length !string));
    done;
    List.iter (fun x -> ignore (ThreadUnix.write (x#getDescr()) !string 0 (String.length !string))) (self#getSpectactor());

  method writeAllUser () =
    let string = ref "USER/" in 
    let descrAll = ref [] in
    for i = 0 to size-1 do
      let (u, s) = sockets.(i) in
      string := !string ^ u ^ "/";
      descrAll := s::!descrAll 
    done;
    List.iter (fun x -> 
      string := !string ^ x#getUserName() ^ " (Spec)/";
      descrAll := x#getDescr()::!descrAll) spectactorList; 
    string := !string ^ "\n";
    List.iter (fun x -> ignore (ThreadUnix.write x !string 0 (String.length !string))) !descrAll;
    
  method writeClear () = 
    self#resetCommandeList();
    let string = "CLEAR/\n" in
    for i = 0 to size-1 do
      let (_, s) = sockets.(i) in
      ignore (ThreadUnix.write s string 0 (String.length string));
    done;
    List.iter (fun x -> ignore (ThreadUnix.write (x#getDescr()) string 0 (String.length string))) (self#getSpectactor());

  method resetAllScore () = 
    List.iter (fun x -> x#setScore 0) clients

  method setUserScore user score = 
    try 
      let client = List.find (fun x -> x#getUserName() = user) clients in 
      client#addScore score;
    with 
      Not_found -> ()

  method nextTurn con = 
    if con#getUserName() = self#getCurrentDrawer() || self#getSize() = 1 then ( 
      Condition.signal motTrouver;
      if self#getNbFinder() = 1 then (
	Thread.delay 1.0;
	Condition.signal allFind;
      )
    )
  method writeFound  user = 
    let string = "WORD_FOUND/" ^ user ^ "/\n" in
    for i = 0 to size-1 do
      let (_, s) = sockets.(i) in
      ignore (ThreadUnix.write s string 0 (String.length string));
    done;
    List.iter (fun x -> ignore (ThreadUnix.write (x#getDescr()) string 0 (String.length string))) (self#getSpectactor());

  method writeTimeout () = 
    let string = "WORD_FOUND_TIMEOUT/"^ (string_of_float timeout) ^ "/\n" in
    for i = 0 to size-1 do
      let (_, s) = sockets.(i) in
      ignore (ThreadUnix.write s string 0 (String.length string));
    done;
    List.iter (fun x -> ignore (ThreadUnix.write (x#getDescr()) string 0 (String.length string))) (self#getSpectactor());

  method writeEndRound user word = 
    let string = "END_ROUND/"^user^"/"^word^"/\n" in
    for i = 0 to size-1 do
      let (_, s) = sockets.(i) in
      ignore (ThreadUnix.write s string 0 (String.length string));
    done;
    List.iter (fun x -> ignore (ThreadUnix.write (x#getDescr()) string 0 (String.length string))) (self#getSpectactor());

  method cheat user = 
    if user = self#getCurrentDrawer() then (
      self#setCheatNumber (self#getCheatNumber() - 1);
      if self#getCheatNumber() = 0 then (
	Condition.signal motTrouver;
	Thread.delay 1.0;
	Condition.signal allFind;
      )
    )
end ;;


(***************** Stat HTTP **************)

let header = 
"HTTP/1.1 200 OK
Content-type:text/html


<!DOCTYPE html><html><head><title>Isketch Stat</title></head>
<body>
<h1 align=\"center\">Statistique</h1>\n
<style>
table
{
    border-collapse: collapse; 	
}
tr { border: 1px solid black; }
td { border: 1px solid black; }
th { width:100px; border: 1px solid black; }
</style>\n"

let footer =
  "</body></html>\n"

let print_stats ()=
  let f = open_in "login" in 
  let r = Str.regexp ";" in 
  let line = ref "" in 
  let listTmp = ref [] in
  let string = ref "" in 
  string :=  "<div align=\"center\"><table><tr><th>Login</th><th>Nb Victoires</th><th>Nb Defaites</th><th>Score</th></tr>\n";
  (
    try
      while true do 
	line := input_line f;
	listTmp := Str.split r !line;
	string := !string ^ (Printf.sprintf "<tr><td>%s</td><td>%d</td><td>%d</td><td>%d</td></tr>\n"
			       (List.nth !listTmp 0) (int_of_string (List.nth !listTmp 3))
			       (int_of_string (List.nth !listTmp 4)) (int_of_string (List.nth !listTmp 2)));
      done;
    with 
      End_of_file -> (close_in f);
  );
  string := !string ^"</table></div>\n" ;
  !string

let html_page () =
  header^
    (print_stats ())^
    footer

let stat_server () =
  let sock = ThreadUnix.socket Unix.PF_INET Unix.SOCK_STREAM 0 
  and port = 2092 in
  begin
    Unix.setsockopt sock Unix.SO_REUSEADDR true;
    Unix.bind sock (Unix.ADDR_INET(Unix.inet_addr_any, port));
    Unix.listen sock 3
  end;
  while true do
    try
      let (s_desc, _) = ThreadUnix.accept sock in
      ignore (Thread.create 
		(fun () ->
		  let html = html_page () in
                  ignore (ThreadUnix.write s_desc html 0 (String.length html));
                  Unix.close s_desc;
		)() )
    with
    | e -> print_endline (Printexc.to_string e);
  done  

(****************FIN STAT ******************)
    
(***************** Main ******************)
let main () = 
  Random.self_init();
  let max = ref 10 
  and timeout = ref 30.0
  and port = ref 2013 
  and file = ref "dico" in
  let speclist = [("-max", Arg.Set_int max, "Max Players Before Game start");
		  ("-timeout", Arg.Set_float timeout, "Timeout after a player find the word");
		  ("-port", Arg.Set_int port, "Port number");
		  ("-dico", Arg.Set_string file, "Dictionnary File");
	      	 ] in
  let usage_msg = "Isketch Server usage : " in
  Arg.parse speclist (fun anon -> print_endline ("Anonymous argument: " ^ anon)) usage_msg;
  ignore (Thread.create stat_server ());
  let t1 = Thread.create (fun _ -> (new server_maj !port !max !file !timeout)#start()) () in
  Thread.join t1;;

main();;
