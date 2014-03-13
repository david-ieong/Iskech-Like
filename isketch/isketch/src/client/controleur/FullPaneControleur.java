package client.controleur;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import client.Modele;

/**
 * 
 * @author david & quentin
 * Controleur de la fenetre principale
 */
public class FullPaneControleur implements ActionListener, ChangeListener, MouseListener, MouseMotionListener{

	private JComponent[] compo;
	private Modele mod;
	private Point2D p1;
	private Point2D p2;
	private Point2D p3;
	private Point2D p4;
	private int cptClick;

	public FullPaneControleur(Modele mod, JComponent[] component) {
		this.compo = component;
		this.mod = mod;
		this.p1 = new Point2D.Double();
		this.p2 = new Point2D.Double();
		this.p3 = new Point2D.Double();
		this.p4 = new Point2D.Double();
		cptClick = 0;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		JTextField fieldSrc;
		JButton buttonSrc;
		JToggleButton toggleSrc;
		String str;
		if (src.getClass().equals(JButton.class)) { 
			// on regarde si celui qui a generer l'event est de type JButton
			// si oui on le cast 
			buttonSrc = (JButton) src;
			if (buttonSrc == compo[13]) { // bouton pour valider la couleur
				mod.setPenColor(((JColorChooser) compo[7]).getColor());
				try { 
					// dans ce try on envoie au server la nouvelle couleur
					mod.getOutchan().write("SET_COLOR/"+ mod.getPenColor().getRed()
							+ "/" + mod.getPenColor().getGreen()
							+ "/"+ mod.getPenColor().getBlue()+ "/\r\n");
					mod.getOutchan().flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} else if (buttonSrc == compo[14]) { //button clear Panel
				try { 
					mod.getOutchan().write("CLEAR_COMMANDE/\r\n");
					mod.getOutchan().flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}				
			} else if (buttonSrc == compo[9]) { // bouton spec mode
				mod.setIsSpectactor(true);
				try {
					mod.getOutchan().write("SPECTACTOR/"+ mod.getUserName()+"/\n");
					mod.getOutchan().flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} else if (buttonSrc == compo[11]) { // bouton passer
				try {
					mod.setIsDrawing(false);
					mod.getOutchan().write("PASS/\r\n");
					mod.getOutchan().flush();
					mod.notifieTousObservateur();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} else if (buttonSrc == compo[17]) { // bouton send
				str = ((JTextField)compo[1]).getText();
				if (!str.equals("")) {
					try {
						mod.getOutchan().write("TALK/"+ str + "/\r\n");
						mod.getOutchan().flush();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				((JTextField)compo[1]).setText("");
			} else if (buttonSrc == compo[18]) { // bouton guess
				str = ((JTextField)compo[15]).getText();
				if (!str.equals("")) {
					try {
						mod.getOutchan().write("GUESS/"+ str + "/\r\n");
						mod.getOutchan().flush();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				((JTextField)compo[15]).setText("");
			} else if (buttonSrc == compo[19]) { // bouton report cheat
				try {
					mod.getOutchan().write("CHEAT/"+ mod.getDrawerName() +"/\n");
					mod.getOutchan().flush();
					((JButton) compo[19]).setEnabled(false);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}  else if (src.getClass().equals(JTextField.class)) {
			fieldSrc = (JTextField) src;
			if (fieldSrc == compo[1]) {
				str = fieldSrc.getText();
				if (!str.equals("")) {
					try {
						mod.getOutchan().write("TALK/"+ str + "/\r\n");
						mod.getOutchan().flush();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				fieldSrc.setText("");
			} else if (fieldSrc == compo[5]) {
				JSlider slide = (JSlider) compo[4];
				slide.setValue(Integer.parseInt(fieldSrc.getText()));
				try {
					mod.getOutchan().write("SET_SIZE/"+ slide.getValue() + "/\r\n");
					mod.getOutchan().flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				mod.setStroke(slide.getValue());
			} else if (fieldSrc == compo[15]) {
				str = fieldSrc.getText();
				if (!str.equals("")) {
					try {
						mod.getOutchan().write("GUESS/"+ str + "/\r\n");
						mod.getOutchan().flush();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				fieldSrc.setText("");
			}
		} else if (src.getClass().equals(JToggleButton.class)) {
			toggleSrc = (JToggleButton) src;
			if (toggleSrc == compo[10]) {
				if (toggleSrc.isSelected()) {
					mod.setIsBezier(true);
					cptClick = 0;
				} else {
					mod.setIsBezier(false);
					cptClick = 0;
				}
			}
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		Object src = e.getSource();
		JSlider slideSrc;
		JTextField field;
		if (src.getClass().equals(JSlider.class)) {
			slideSrc = (JSlider) src;
			if (slideSrc == compo[4] && !slideSrc.getValueIsAdjusting()) {
				field = (JTextField) compo[5];
				field.setText(String.valueOf(slideSrc.getValue()));
				try {
					mod.getOutchan().write("SET_SIZE/"+ slideSrc.getValue() + "/\r\n");
					mod.getOutchan().flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				mod.setStroke(slideSrc.getValue());
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (mod.isDrawing() && mod.isBezier()) {
			if (cptClick == 0) {
				p1 = e.getPoint();
				cptClick++;
			} else if (cptClick == 1) {
				p2 = e.getPoint();
				cptClick++;
			} else if (cptClick == 2) {
				p3 = e.getPoint();
				cptClick++;
			} else if (cptClick == 3) {
				p4 = e.getPoint();
				try {
					mod.getOutchan().write(
							"SET_COURBE/"+p1.getX()+"/"
									+p1.getY()+"/"
									+p2.getX()+"/"
									+p2.getY()+"/"
									+p3.getX()+"/"
									+p3.getY()+"/"
									+p4.getX()+"/"
									+p4.getY()+"/\r\n");
					mod.getOutchan().flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				cptClick = 0;
			}
		}
	}
	@Override
	public void mousePressed(MouseEvent e) {
		if (mod.isDrawing() && !mod.isBezier())
			p1 = e.getPoint();	
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		if (mod.isDrawing() && !mod.isBezier()) {
			p2 = e.getPoint();
			try {
				mod.getOutchan().write("SET_LINE/"+p1.getX()+"/"+p1.getY()+"/"+p2.getX()+"/"+p2.getY()+ "/\r\n");
				mod.getOutchan().flush();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}
	@Override
	public void mouseExited(MouseEvent e) {
		if ((JComponent)e.getSource() == compo[8]) {
			JColorChooser colorChooser = (JColorChooser) compo[7];
			mod.setPenColor(colorChooser.getColor());
			((JPanel) compo[12]).setBackground(colorChooser.getColor());
		}
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		if (mod.isDrawing() && !mod.isBezier()) {
			p2 = e.getPoint();
			try {
				mod.getOutchan().write("SET_LINE/"+p1.getX()+"/"+p1.getY()+"/"+p2.getX()+"/"+p2.getY()+ "/\r\n");
				mod.getOutchan().flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			p1 = p2;
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}
}
