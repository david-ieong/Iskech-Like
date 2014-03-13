package client.UI;


import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import client.Modele;
import client.controleur.MenuControleur;

/**
 * 
 * @author david & quentin
 * Classe du menu de la fenetre
 */
public class MonMenu extends JMenuBar{

	private static final long serialVersionUID = 1L;
	private JComponent[] component;
	private MenuControleur menuControleur;
	private Modele mod;
	private JMenuItem connexion, exit, login;
	private ConnexionFrame connexionFrame;
	private LoginFrame loginFrame;
	
	public MonMenu(Modele mod, ConnexionFrame connexionFrame, LoginFrame loginFrame) {
		super();
		this.mod = mod;
		this.connexionFrame = connexionFrame;
		this.loginFrame = loginFrame;
		addMenu();
	}
	
	private void addMenu() {
		component = new JComponent[3];
		JMenu menu = new JMenu("File");
		this.add(menu);
		menuControleur = new MenuControleur(mod, component,
				connexionFrame, loginFrame);
		connexion = new JMenuItem("Guest/Spectactor");
		exit = new JMenuItem("Exit");
		login = new JMenuItem("Login/Register");
		connexion.addActionListener(menuControleur);
		exit.addActionListener(menuControleur);
		login.addActionListener(menuControleur);
		menu.add(connexion);
		menu.add(login);
		menu.add(exit);
		component[0] = connexion;
		component[1] = exit;
		component[2] = login;
	}
}
