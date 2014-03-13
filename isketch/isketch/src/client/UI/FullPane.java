package client.UI;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.border.Border;

import client.Modele;
import client.Observable;
import client.Observateur;
import client.controleur.FullPaneControleur;
/**
 * 
 * @author david & quentin
 * Panel qui est dans la fenetre principal contient tout les components
 */
public class FullPane extends JPanel implements Observateur{

	private static final long serialVersionUID = 1L;
	private JTextArea userArea, chatArea, guessArea;
	private JTextField chatField, guessField, sizeField;
	private JLabel chatLabel, guessLabel, sizeLabel;
	private DrawPanel drawPanel;
	private JPanel colorPane, colorChooserPanel;
	private Modele mod;
	private JScrollPane scrollChat, scrollGuess;
	private Border raisedbevel, loweredbevel;
	private JComponent[] compo;
	private FullPaneControleur controleur;
	private JSlider sizeSlide;
	private JButton colorOk, clearPanel, specMode, passButton, chatButton;
	private JButton cheatButton, guessButton;
	private JColorChooser colorChooser;
	private JComponent componentColor;
	private TempsLabel tempsLabel;
	private JToggleButton bezierButton;
	private Color background = new Color(225,225,225);

	public FullPane(Modele mod) {
		super();
		this.mod = mod;
		this.setBackground(background);
		this.setSize(1015, 675);
		this.setLayout(null);
		createComponent();
		addComponent();
	}

	/**
	 * Methode permettant de creer les components
	 */
	private void createComponent() {
		/* Création des objets */ 
		raisedbevel = BorderFactory.createRaisedBevelBorder();
		loweredbevel = BorderFactory.createLoweredBevelBorder();
		userArea = new JTextArea();
		drawPanel = new DrawPanel();
		chatArea = new JTextArea();
		guessArea = new JTextArea();
		chatField = new JTextField();
		guessField = new JTextField();
		chatLabel = new JLabel("Chat");
		guessLabel = new JLabel("Guess");
		scrollChat = new JScrollPane (chatArea,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollGuess = new JScrollPane (guessArea,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		compo = new JComponent[20];
		controleur = new FullPaneControleur(mod, compo);
		colorPane = new JPanel();
		colorOk = new JButton("Valider couleur");
		clearPanel = new JButton("Clear Panel");
		sizeLabel = new JLabel("S");
		sizeField = new JTextField();
		sizeSlide = new JSlider(JSlider.HORIZONTAL, 1, 20, 1);
		colorChooser = new JColorChooser(Color.BLACK);
		colorChooserPanel = new JPanel();
		componentColor = colorChooser.getChooserPanels()[1];
		tempsLabel = new TempsLabel();
		specMode = new JButton("Spec Mode");
		bezierButton = new JToggleButton("Bezier");
		passButton = new JButton("Passer");
		chatButton = new JButton("Send");
		guessButton = new JButton("Guess");
		cheatButton = new JButton("Report Cheat");
	}

	/**
	 * Methode ajoutant les compoents dans le panel et les positionne
	 */
	private void addComponent() {
		/* On positionne les différents composant */
		userArea.setBounds(5, 5, 130,300);
		userArea.setEditable(false);
		userArea.setBorder(BorderFactory.createCompoundBorder(
				raisedbevel, loweredbevel));
		userArea.setFont(new Font("Dialog",Font.BOLD,12));
		drawPanel.setBounds(140, 5, 650, 400);
		specMode.setBounds(5, 310, 130, 30);
		specMode.setEnabled(false);
		cheatButton.setBounds(5, 345, 130, 30);
		cheatButton.setEnabled(false);
		
		/* Chat */
		chatLabel.setBounds(150, 408, 100,20);
		chatArea.setLineWrap(true);
		chatArea.setEditable(false);
		chatArea.setFont(new Font("Dialog",Font.BOLD,12));
		scrollChat.setBounds(140, 430, 340, 150);
		scrollChat.setBorder(BorderFactory.createCompoundBorder(
				raisedbevel, loweredbevel));
		chatField.setBounds(140, 585, 265, 30);
		chatField.setBorder(BorderFactory.createCompoundBorder(
				raisedbevel, loweredbevel));
		chatField.setFont(new Font("Dialog",Font.BOLD,15));
		chatField.setEnabled(false);
		chatButton.setBounds(410, 585, 70, 30);
		chatButton.setEnabled(false);
		
		/* Guess */
		guessLabel.setBounds(500, 408, 100,20);
		guessArea.setLineWrap(true);
		guessArea.setEditable(false);
		guessArea.setFont(new Font("Dialog",Font.BOLD,12));
		scrollGuess.setBounds(485, 430, 305, 150);
		scrollGuess.setBorder(BorderFactory.createCompoundBorder(
				raisedbevel, loweredbevel));
		guessField.setBounds(485, 585, 220, 30);
		guessField.setBorder(BorderFactory.createCompoundBorder(
				raisedbevel, loweredbevel));
		guessField.setFont(new Font("Dialog",Font.BOLD,15));
		guessField.setEnabled(false);
		guessButton.setBounds(710,585,80, 30);
		guessButton.setEnabled(false);
		
		componentColor.setBounds(0, 0, 450, 200);
		componentColor.setBackground(background);
		colorChooserPanel.setLayout(null);
		colorChooserPanel.add(componentColor);
		colorChooserPanel.setBounds(790, 0, 220,200);
		
		sizeLabel.setBounds(800, 200, 10, 20);
		sizeSlide.setBounds(820, 200, 150, 20);
		sizeSlide.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		sizeSlide.setMinorTickSpacing(1);
		sizeSlide.setMajorTickSpacing(5);
		sizeSlide.setPaintTicks(true);
		sizeSlide.setEnabled(false);
		sizeField.setBounds(975, 200, 28, 22);
		sizeField.setText(String.valueOf(sizeSlide.getMinimum()));
		sizeField.setEnabled(false);
		
		colorOk.setBounds(795, 230, 150, 30);
		colorOk.setEnabled(false);
		clearPanel.setBounds(795, 265, 150, 30);
		clearPanel.setEnabled(false);
		colorPane.setBounds(950, 230, 55, 65);
		colorPane.setBackground(Color.black);
		
		bezierButton.setBounds(795, 300, 130, 30);
		bezierButton.setEnabled(false);
		passButton.setBounds(795, 335, 130, 30);
		passButton.setEnabled(false);
		tempsLabel.setBounds(795, 365, 150, 40);
		tempsLabel.setFont(new Font("Dialog",Font.BOLD,15));
		
		/* on ajoute un listenner adapter et on lui donne un controleur */
		drawPanel.addMouseListener(controleur);
		drawPanel.addMouseMotionListener(controleur);
		chatField.addActionListener(controleur);
		guessField.addActionListener(controleur);
		colorOk.addActionListener(controleur);
		clearPanel.addActionListener(controleur);
		sizeSlide.addChangeListener(controleur);
		sizeField.addActionListener(controleur);
		componentColor.addMouseListener(controleur);
		specMode.addActionListener(controleur);
		bezierButton.addActionListener(controleur);
		passButton.addActionListener(controleur);
		guessButton.addActionListener(controleur);
		chatButton.addActionListener(controleur);
		cheatButton.addActionListener(controleur);
		
		/* on ajoute les components dans le panel */
		this.add(userArea);
		this.add(drawPanel);
		this.add(chatField);
		this.add(guessField);
		this.add(chatLabel);
		this.add(guessLabel);
		this.add(scrollChat);
		this.add(scrollGuess);
		this.add(colorPane);
		this.add(colorOk);
		this.add(clearPanel);
		this.add(sizeLabel);
		this.add(sizeField);
		this.add(sizeSlide);
		this.add(colorChooser);
		this.add(colorChooserPanel);
		this.add(tempsLabel);
		this.add(specMode);
		this.add(bezierButton);
		this.add(passButton);
		this.add(chatButton);
		this.add(guessButton);
		this.add(cheatButton);
		
		/* Ajoute les component dans le tableau utilise dans le controleur */
		compo[0] = userArea;
		compo[1] = chatField;
		compo[2] = chatArea;
		compo[3] = drawPanel;
		compo[4] = sizeSlide;
		compo[5] = sizeField;
		compo[6] = colorChooserPanel;
		compo[7] = colorChooser;
		compo[8] = componentColor;
		compo[9] = specMode;
		compo[10] = bezierButton;
		compo[11] = passButton;
		compo[12] = colorPane;
		compo[13] = colorOk;
		compo[14] = clearPanel;
		compo[15] = guessField;
		compo[16] = guessArea;
		compo[17] = chatButton;
		compo[18] = guessButton;
		compo[19] = cheatButton;
	}

	
	/**
	 * methode notifie qui modifie les component du panel quand il le faut
	 */
	@Override
	public void notifie(Observable o) {
		String line;
		MyLine myLine;
		MyCourbe myCourbe;
		String userList;
		if (mod.isNewLine()) {
			myLine = new MyLine(mod.getP1(), mod.getP2(), mod.getPenColor(), mod.getStroke());
			drawPanel.addLine(myLine);
			drawPanel.repaint();
			mod.setNewLine(false);
		}
		if (mod.isNewCourbe()) {
			myCourbe = new MyCourbe(
					mod.getP1(),
					mod.getP2(),
					mod.getP3(),
					mod.getP4(),
					mod.getPenColor(), mod.getStroke());
			drawPanel.addLine(myCourbe);
			drawPanel.repaint();
			mod.setIsNewCourbe(false);
		}		
		if (mod.isGuess()) {
			line = mod.getCurrentLine();
			guessArea.setText(guessArea.getText() + "\n" + line);
			guessArea.setCaretPosition(guessArea.getDocument().getLength());
			mod.setIsGuess(false);
		}
		if (mod.isNewInfo()) {
			line = mod.getCurrentLine();
			chatArea.setText(chatArea.getText() + "\n" + line);
			chatArea.setCaretPosition(chatArea.getDocument().getLength());
			mod.setIsNewInfo(false);			
		}
		if (mod.isNewUser()) {
			userList = "";
			for (String s : mod.getUserList()) {
				userList += s + "\n";
			}
			userArea.setText(userList);
			mod.setIsNewUser(false);
		}
		if (mod.isReset()) {
			drawPanel.clearList();
			drawPanel.repaint();
			mod.setIsReset(false);
		}
		if (mod.isNewRound()) {
			drawPanel.clearList();
			drawPanel.repaint();
			tempsLabel.initialiser();
			tempsLabel.setValide(true);
			sizeSlide.setValue(1);
			sizeField.setText("1");
			colorChooser.setColor(Color.black);
			mod.setIsNewRound(false);
		}
		if (mod.isNewTimeout()) {
			tempsLabel.setSeconde(mod.getTimeout());
			mod.setNewTimeout(false);
		}
		if (!mod.isSpectactor()) {
			activerComponentPourConnecter(true);
			if (mod.isDrawing()) {
				activerComponentPourDessiner(true);
				cheatButton.setEnabled(false);
			} else {
				activerComponentPourDessiner(false);
				bezierButton.setSelected(false);
				cheatButton.setEnabled(true);
			}
		} else {
			activerComponentPourConnecter(false);
		}
		
		
	}
	
	private void activerComponentPourConnecter(boolean b) {
		specMode.setEnabled(b);
		guessField.setEnabled(b);
		guessButton.setEnabled(b);
		chatField.setEnabled(b);
		chatButton.setEnabled(b);
	}
	private void activerComponentPourDessiner(boolean b) {
		passButton.setEnabled(b);
		bezierButton.setEnabled(b);
		colorOk.setEnabled(b);
		sizeSlide.setEnabled(b);
		sizeField.setEnabled(b);
		drawPanel.setEnabled(b);
		clearPanel.setEnabled(b);
	}
	public JPanel getDrawPanel() {
		return drawPanel;
	}

}
