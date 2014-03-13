package client.controleur;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import client.Connect;
import client.Modele;

/**
 * 
 * @author david & quentin
 * Classe controleur de la fenetre login
 */
public class LoginControleur implements ActionListener{

	private Modele mod;
	private JComponent[] compo;
	private JFrame frame;

	public LoginControleur(Modele mod, JFrame frame, JComponent[] compo) {
		this.mod = mod;
		this.compo = compo;
		this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		JCheckBox checkSrc;
		JButton buttonSrc;
		String password, checkPassword;
		if (source.getClass().equals(JButton.class)) {
			buttonSrc = (JButton) source;
			if (buttonSrc == compo[0]) {
				try {
					mod.setAdresse(((JTextField)compo[8]).getText());
					if (!mod.isConnected()) {
						new Connect(mod);
					}
					if (mod.isConnected()) {
						mod.setUserName(((JTextField) compo[5]).getText());
						password = ((JTextField) compo[6]).getText();
						mod.getOutchan().write("LOGIN/" + mod.getUserName() + "/" + password + "/\n");
						mod.getOutchan().flush();
						frame.dispose();		
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} else if (buttonSrc == compo[1]) {
				try {
					if (!mod.isConnected()) {
						new Connect(mod);
					}
					if (mod.isConnected()) {
						mod.setUserName(((JTextField) compo[5]).getText());
						password = ((JTextField) compo[6]).getText();
						checkPassword = ((JTextField) compo[7]).getText();
						if (password.equals(checkPassword)) {
							mod.getOutchan().write("REGISTER/" + mod.getUserName() + "/" + password + "/\n");
							mod.getOutchan().flush();
							frame.dispose();
						} else {
							final JFrame frameFail = new JFrame("Error");
							frameFail.setSize(200,60);
							frameFail.setLayout(new GridLayout(2,1));
							JLabel label = new JLabel("Password not the same !!!");
							frameFail.add(label);
							JButton bouton = new JButton("OK");
							bouton.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									frameFail.dispose();
								}
							});
							frameFail.add(bouton);
							frameFail.setLocationRelativeTo(null);
							frameFail.setVisible(true);
						}
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} else if (buttonSrc == compo[2]) {
				frame.dispose();
			}
		} else if (source.getClass().equals(JCheckBox.class)) {
			checkSrc = (JCheckBox) source;
			if (checkSrc == compo[3]) {
				if (checkSrc.isSelected()) {
					((JTextField) compo[4]).setEditable(true);
				} else 
					((JTextField) compo[4]).setEditable(false);

			}
		}
	}
}
