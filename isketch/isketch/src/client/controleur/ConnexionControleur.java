package client.controleur;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextField;

import client.Connect;
import client.Modele;
/**
 * 
 * @author david & quentin
 * Classe permettant des gerer les event venant de la petite fenetre de connexion
 */
public class ConnexionControleur implements ActionListener{

	private Modele mod;
	private JFrame frame;
	private JComponent[] compo;

	public ConnexionControleur(Modele mod, JFrame frame, JComponent[] compo) {
		this.mod = mod;
		this.frame = frame;
		this.compo = compo;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		JButton buttonSrc;
		JTextField fieldSrc;
		if (src.getClass().equals(JButton.class)) {
			buttonSrc = (JButton) src;
			if (buttonSrc == compo[0]) {
				try {
					mod.setAdresse(((JTextField)compo[4]).getText());
					if (!mod.isConnected()) {
						new Connect(mod);
					}
					if (mod.isConnected()) {
						mod.setUserName(((JTextField) compo[2]).getText());
						mod.getOutchan().write("CONNECT/" + mod.getUserName() + "/\n");
						mod.getOutchan().flush();
						frame.dispose();		
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} else if (buttonSrc == compo[1]) {
				frame.dispose();
			} else if (buttonSrc == compo[3]) {
				try {
					mod.setAdresse(((JTextField)compo[4]).getText());
					if (!mod.isConnected()) {
						new Connect(mod);
					}
					if (mod.isConnected()) {
						mod.setIsSpectactor(true);
						mod.setUserName(((JTextField) compo[2]).getText());
						mod.getOutchan().write("SPECTACTOR/" + mod.getUserName() + "/\n");
						mod.getOutchan().flush();
						frame.dispose();		
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}  else if (src.getClass().equals(JTextField.class)) {
			fieldSrc = (JTextField) src;
			if (fieldSrc == compo[2]) {
				mod.setUserName(fieldSrc.getText());
			}
		}
	}

}
