package client.controleur;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JMenuItem;

import client.Modele;
import client.UI.ConnexionFrame;
import client.UI.LoginFrame;

/**
 * 
 * @author david & quentin
 * Classe controleur du menu Bar
 */
public class MenuControleur implements ActionListener{

	private JComponent[] component;
	private Modele mod;
	private ConnexionFrame connexion;
	private LoginFrame login;
	
	public MenuControleur(Modele mod, JComponent[] component,
				ConnexionFrame connexion, LoginFrame login) {
		this.component = component;
		this.mod = mod;
		this.connexion = connexion;
		this.login = login;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		JMenuItem menuItem;
		if (src.getClass().equals(JMenuItem.class)) {
			menuItem = (JMenuItem) e.getSource();
			if (menuItem == component[0]) {
				connexion.setLocationRelativeTo(null);
				connexion.setVisible(true);
			}
			else if (menuItem == component[1]) {
				String str = "EXIT/" + mod.getUserName() + "/\n";
				try {
					if (mod.isConnected()) {
						System.out.println("Je quitte !!!");
						System.out.println(str);
						mod.getOutchan().write(str);
						mod.getOutchan().flush();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				System.exit(0);
			} else if (menuItem == component[2]) {
				login.setLocationRelativeTo(null);
				login.setVisible(true);
			}
		}
	}

}
