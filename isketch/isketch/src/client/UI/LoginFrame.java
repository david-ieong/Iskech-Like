package client.UI;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import client.Modele;
import client.controleur.LoginControleur;

/**
 * 
 * @author david & quentin
 * Classe de la fenetre de login/register
 */
public class LoginFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField loginField;
	private JPasswordField passwordField;
	private JPasswordField checkPasswordField;
	private JTextField portField;
	private JTextField adresseField;
	private JButton loginButton, registerButton, cancelButton;
	private JLabel loginLabel, passwordLabel, checkPasswordLabel, portLabel, adresseLabel;
	private JCheckBox signInCheckBox;
	private Modele mod;
	private LoginControleur controleur;
	private JComponent[] compo;

	/**
	 * Create the frame.
	 */
	public LoginFrame(Modele mod) {
		super();
		setLocationRelativeTo(null);
		this.mod = mod;
		this.compo = new JComponent[9];
		this.controleur = new LoginControleur(mod, this, compo);
		addComponent();
	}
	
	private void addComponent() {
		setTitle("Login/Register");
		setBounds(100, 100, 301, 243);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		loginButton = new JButton("Login");
		loginButton.setBounds(22, 149, 117, 25);
		contentPane.add(loginButton);
		
		registerButton = new JButton("Register");
		registerButton.setBounds(151, 149, 117, 25);
		contentPane.add(registerButton);
		
		cancelButton = new JButton("Cancel");
		cancelButton.setBounds(90, 181, 117, 25);
		contentPane.add(cancelButton);
		
		loginLabel = new JLabel("Login ");
		loginLabel.setBounds(12, 12, 70, 15);
		contentPane.add(loginLabel);
		
		
		passwordLabel = new JLabel("Password");
		passwordLabel.setBounds(12, 38, 70, 15);
		contentPane.add(passwordLabel);
		
		checkPasswordLabel = new JLabel("Check Pwd");
		checkPasswordLabel.setBounds(12, 65, 86, 15);
		contentPane.add(checkPasswordLabel);
		
		loginField = new JTextField();
		loginField.setBounds(93, 10, 114, 19);
		contentPane.add(loginField);
		loginField.setColumns(10);
		
		passwordField = new JPasswordField();
		passwordField.setBounds(93, 36, 114, 19);
		contentPane.add(passwordField);
		
		checkPasswordField = new JPasswordField();
		checkPasswordField.setBounds(93, 63, 114, 19);
		checkPasswordField.setEditable(false);
		contentPane.add(checkPasswordField);
		
		signInCheckBox = new JCheckBox("Sign In");
		signInCheckBox.setBounds(215, 61, 129, 23);
		contentPane.add(signInCheckBox);
		
		portLabel = new JLabel("Port");
		portLabel.setBounds(12, 92, 70, 15);
		contentPane.add(portLabel);
		
		portField = new JTextField();
		portField.setBounds(93, 92, 114, 19);
		contentPane.add(portField);
		portField.setText(String.valueOf(mod.getPort()));
		portField.setEditable(false);
		portField.setColumns(10);
		
		adresseLabel = new JLabel("Adresse");
		adresseLabel.setBounds(12, 122, 70, 15);
		contentPane.add(adresseLabel);
		
		adresseField = new JTextField();
		adresseField.setBounds(93, 118, 114, 19);
		contentPane.add(adresseField);
		adresseField.setText(mod.getAdresse());
		adresseField.setEditable(true);
		adresseField.setColumns(10);
		
		loginButton.addActionListener(controleur);
		registerButton.addActionListener(controleur);
		cancelButton.addActionListener(controleur);
		signInCheckBox.addActionListener(controleur);
		
		compo[0] = loginButton;
		compo[1] = registerButton;
		compo[2] = cancelButton;
		compo[3] = signInCheckBox;
		compo[4] = checkPasswordField;
		compo[5] = loginField;
		compo[6] = passwordField;
		compo[7] = checkPasswordField;
		compo[8] = adresseField;
	}
}
