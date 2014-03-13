package client;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * 
 * @author david & quentin
 * Classe permettant de gerer les inputs venant du serveur 
 */
public class TraiterInput extends Thread{

	private Modele mod;
	public TraiterInput(Modele mod) {
		this.mod = mod;
	}
	
	@Override
	public void run() {
		String commande;
		List<String> tabRes;
		String line;
		while (true) {	
			try {
				line = mod.getInchan().readLine();
				if (line != null && !line.equals("")) {
					tabRes = mod.parseLine(line);
					if (tabRes.size() != 0) {
						commande = tabRes.get(0);
						if (commande.equals("CONNECTED")) {
							mod.setCurrentLine("Connected " + mod.parseUserName(
									tabRes.get(1)) + " !!!");
							mod.setIsNewInfo(true);
							mod.setIsDrawing(false);
						} else if (commande.equals("WELCOME")) {
							mod.setUserName(tabRes.get(1));
							mod.setUserNameParse(mod.parseUserName(tabRes.get(1)));
							mod.setCurrentLine("Bienvenue " 
									+ mod.getUserNameParse() 
									+ " !!!");
							mod.setIsNewInfo(true);
							mod.setIsDrawing(false);
							mod.setIsSpectactor((mod.getUserName().contains("as Spectactor")));
						} else if (commande.equals("EXITED")) {
							mod.setCurrentLine("Exited " 
									+ mod.parseUserName(tabRes.get(1)) + " !!!");
							mod.setIsNewInfo(true);
							if (mod.getUserName().equals(tabRes.get(1)))
								mod.setIsSpectactor(true);
						} else if (commande.equals("LISTEN")){
							if (!mod.isSpectactor() 
									&& mod.getUserNameParse()
									.equals(mod.parseUserName(tabRes.get(1)))) {
								mod.setCurrentLine("me : " + tabRes.get(2));
							} else {
								mod.setCurrentLine(
										mod.parseUserName(tabRes.get(1)) 
										+ " : " + tabRes.get(2));
							}
							if (tabRes.get(2).equals("YOU WILL BE KICK !!!"))
								mod.setIsConnected(false);
							mod.setIsNewInfo(true);
						} else if (commande.equals("LINE")){
							mod.setP1(new Point2D.Double(
									Double.parseDouble(tabRes.get(1)),
									Double.parseDouble(tabRes.get(2))));
							mod.setP2(new Point2D.Double(
									Double.parseDouble(tabRes.get(3)),
									Double.parseDouble(tabRes.get(4))));
							mod.setPenColor(new Color(
									Integer.parseInt(tabRes.get(5)),
									Integer.parseInt(tabRes.get(6)),
									Integer.parseInt(tabRes.get(7))));
							mod.setStroke(Integer.parseInt(tabRes.get(8)));
							mod.setNewLine(true);
						} else if (commande.equals("COURBE")) {
							mod.setP1(new Point2D.Double(
									Double.parseDouble(tabRes.get(1)), 
									Double.parseDouble(tabRes.get(2))));
							mod.setP2(new Point2D.Double(
									Double.parseDouble(tabRes.get(3)), 
									Double.parseDouble(tabRes.get(4))));
							mod.setP3(new Point2D.Double(
									Double.parseDouble(tabRes.get(5)),
									Double.parseDouble(tabRes.get(6))));
							mod.setP4(new Point2D.Double(
									Double.parseDouble(tabRes.get(7)), 
									Double.parseDouble(tabRes.get(8))));						
							mod.setPenColor(new Color(
									Integer.parseInt(tabRes.get(9)), 
									Integer.parseInt(tabRes.get(10)), 
									Integer.parseInt(tabRes.get(11))));
							mod.setStroke(Integer.parseInt(tabRes.get(12)));
							mod.setIsNewCourbe(true);
						} else if (commande.equals("GUESSED")) {
							if (!mod.isSpectactor() 
									&& mod.getUserNameParse()
									.equals(mod.parseUserName(tabRes.get(1)))) {
								mod.setCurrentLine("me : " + tabRes.get(2));
							} else {
								mod.setCurrentLine(mod.parseUserName(
										tabRes.get(1)) + " : " + tabRes.get(2));
							}
							mod.setIsGuess(true);
						} else if (commande.equals("NEW_ROUND")) {
							if (tabRes.get(1).equals("draw") || tabRes.get(1).equals("dessinateur")) {
								mod.setIsDrawing(true);
								mod.setCurrentLine("\nBROADCAST : "
										+ "New Round !!!\nBROADCAST : "
										+ "Dessine le mot \"" + tabRes.get(3) + "\"");
							} else {
								mod.setIsDrawing(false);
								mod.setCurrentLine("\nBROADCAST : "
										+ "New Round !!!\nBROADCAST : "
										+ "Devine le mot !!!");
							}
							mod.setDrawerName(tabRes.get(2));
							mod.setIsNewRound(true);
							mod.setIsNewInfo(true);
						} else if (commande.equals("WORD_FOUND")) {
							if (mod.parseUserName(tabRes.get(1)).equals(mod.getUserNameParse())) {
								mod.setCurrentLine("\nBROADCAST : "
										+ "Vous avez trouver le mot !!!");
							} else {
								mod.setCurrentLine("\nBROADCAST : "
										+ tabRes.get(1) + " à trouver le mot !!!");
							}	
							mod.setIsNewInfo(true);
						} else if (commande.equals("WORD_FOUND_TIMEOUT")) {
							mod.setTimeout((int)Double.parseDouble(tabRes.get(1)));
							mod.setNewTimeout(true);
						} else if (commande.equals("SCORE")) {
							for (int i = 1 ; i < tabRes.size() -1 ; i++) {
								if (i % 2 == 0) {
									mod.setCurrentLine(mod.getCurrentLine() 
											+ " " + tabRes.get(i));
								} else {
									mod.setCurrentLine(mod.getCurrentLine() 
											+ "\nBROADCAST : " 
											+ mod.parseUserName(tabRes.get(i)));
								}
							}
							mod.setIsNewInfo(true);
						} else if (commande.equals("USER")) {
							mod.setUserList(new ArrayList<String>());
							for(int i = 1; i < tabRes.size() -1; i++) {
								if ((tabRes.get(i)).equals(mod.getUserName())) {
									mod.getUserList().add(mod.parseUserName(tabRes.get(i)));								
								} else {
									mod.getUserList().add(0 , mod.parseUserName(tabRes.get(i)));
								}
							}
							mod.setIsNewUser(true);
						} else if (commande.equals("CLEAR")) {
							mod.setIsReset(true);
						} else if (commande.equals("END_ROUND")) {
							if (tabRes.get(1).equals("")) {
								mod.setCurrentLine("\nBROADCAST : Le mot etait \"" + tabRes.get(2) 
										+"\"!!!\nBroadcast : Personne n'a trouvé !!!" );
							} else {
								mod.setCurrentLine("\nBROADCAST : Le mot etait \"" + tabRes.get(2) 
										+"\"!!! \nBroadcast : And the winner is " 
										+ mod.parseUserName(tabRes.get(1)) + " !!!");
							}
							mod.setIsNewInfo(true);
							mod.setIsDrawing(false);
						} else if (commande.equals("ACCESSDENIED")) {
							mod.setIsConnected(false);
							mod.setCurrentLine(tabRes.get(1));
							mod.setIsNewInfo(true);	
						} else if (line != "" || line != "\n") {
							mod.setIsNewInfo(true);
							mod.setCurrentLine(line);
						}
						mod.notifieTousObservateur();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
