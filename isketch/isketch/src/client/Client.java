package client;

/**
 * 
 * @author david & quentin
 * Classe main client qui demarre la connexion a la socket
 */
public class Client {
	private static Modele mod;

	public static void main(String args[]) {
		String adresse = "127.0.1.1";
		int port = 2013;
		String userName = "Anonymous";
		for (int i = 0 ; i< args.length ; i++) {
			switch (args[i]) {
			case "-port" :
				port = Integer.parseInt(args[i+1]);
				i++;
				break;
			case "-user" :
				userName = args[i+1];
				i++;
				break;
			default :
				System.err.println("Option inconnue : "+args[i]);
			}
		}
		mod = new Modele();
		mod.setAdresse(adresse);
		mod.setPort(port);
		mod.setUserName(userName);

		new FrameLauncher(mod);
	} // end main
}
