package client.UI;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import client.Modele;
import client.Observable;
import client.Observateur;
import client.controleur.ConnexionControleur;
/**
 * 
 * @author david & quentin
 *	
 * Classe permettant d'avoir la petite fenetre de connexion
 */
public class ConnexionFrame extends JFrame implements Observateur{

	private static final long serialVersionUID = 1L;
	private JComponent[] compo;
	private JPanel contentPane;
	private JTextField userNameField;
	private JTextField adresseField;
	private JTextField portField;
	private Modele mod;
	private ConnexionControleur controleur;
	private JButton connexionButton, cancelButton, spectactorButton;
	private JLabel userNameLabel, portLabel, adresseLabel;

	/**
	 * Create the frame.
	 */
	public ConnexionFrame(Modele mod) {
		this.mod = mod;
		setTitle("Connexion");
		setBounds(100, 100, 281, 201);
		compo = new JComponent[5];
		controleur = new ConnexionControleur(mod, this, compo);
		addComponent();
	}
	
	private void addComponent() {
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		connexionButton = new JButton("Connexion");
		connexionButton.addActionListener(controleur);
		connexionButton.setBounds(12, 106, 117, 25);
		contentPane.add(connexionButton);
		
		userNameLabel = new JLabel("User name :");
		userNameLabel.setBounds(12, 12, 99, 15);
		contentPane.add(userNameLabel);
		
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(controleur);
		cancelButton.setBounds(78, 143, 117, 25);
		contentPane.add(cancelButton);
		
		userNameField = new JTextField(mod.getUserName());
		userNameField.setBounds(129, 12, 114, 19);
		contentPane.add(userNameField);
		userNameField.setColumns(10);
		userNameField.addActionListener(controleur);
		
		portLabel = new JLabel("Port");
		portLabel.setBounds(12, 39, 70, 15);
		contentPane.add(portLabel);
		
		adresseLabel = new JLabel("Adresse");
		adresseLabel.setBounds(12, 66, 70, 15);
		contentPane.add(adresseLabel);
		
		adresseField = new JTextField(mod.getAdresse());
		adresseField.setBounds(129, 63, 114, 19);
		contentPane.add(adresseField);
		adresseField.setEditable(true);
		adresseField.setColumns(10);
		
		portField = new JTextField(String.valueOf(mod.getPort()));
		portField.setBounds(129, 37, 114, 19);
		contentPane.add(portField);
		portField.setEditable(false);
		portField.setColumns(10);
	
		spectactorButton = new JButton("Spectactor");
		spectactorButton.setBounds(143, 106, 117, 25);
		spectactorButton.addActionListener(controleur);
		contentPane.add(spectactorButton);
		
		compo[0] = connexionButton;
		compo[1] = cancelButton;
		compo[2] = userNameField;
		compo[3] = spectactorButton;
		compo[4] = adresseField;
	}

	@Override
	public void notifie(Observable o) {
		if (mod.isSpectactor()) {
			connexionButton.setEnabled(true);
			spectactorButton.setEnabled(false);	
		} else  {
			connexionButton.setEnabled(false);
			spectactorButton.setEnabled(true);	
		}
	}
}
