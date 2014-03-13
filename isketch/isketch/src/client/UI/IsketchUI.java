package client.UI;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JFrame;

import client.Modele;


/**
 *
 * @author david & quentin
 * Classe qui creer la fenetre principale
 */
public class IsketchUI extends JFrame {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The full pane. */
	private FullPane fullPane;	
	
	/** The menu bar. */
	private MonMenu menuBar;
	
	/** The mod. */
	private Modele mod;
	
	private ConnexionFrame connexionFrame;
	
	private LoginFrame loginFrame;
	
	/**
	 * Instantiates a new isketch ui.
	 *
	 * @param nom the nom
	 * @param mod the mod
	 */
	public IsketchUI (String nom, Modele mod) {
		super(nom);
		this.mod = mod;
		this.connexionFrame = new ConnexionFrame(mod);
		this.loginFrame = new LoginFrame(mod);
		this.setBackground(new Color(217,217,217));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(1015, 675);
		this.setResizable(false);
		addComponent();
	}
		
	/**
	 * Adds the component.
	 */
	private void addComponent() {
		this.setLayout(new GridLayout(1, 1));
		fullPane = new FullPane(mod);
		menuBar = new MonMenu(mod, connexionFrame, loginFrame);
		mod.ajout_Observateur(fullPane);
		mod.ajout_Observateur(connexionFrame);
		this.setJMenuBar(menuBar);
		this.add(fullPane);
	}
}
