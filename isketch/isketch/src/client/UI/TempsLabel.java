package client.UI;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;

/**
 * 
 * @author david & quentin
 * Classe pour faire le timer
 */
public class TempsLabel extends JLabel {

	private static final long serialVersionUID = 1L;
	private Timer timer;
	private long secondes;
	private boolean valide;

	public TempsLabel(){
		super("");
		secondes = 0;
		valide = false;
		timer = new Timer();
		timer.schedule(new TimerTask(){

			public void run() {
				if (valide) {
					secondes -= 1;
					setText(timeToText(secondes));
					if (secondes == 0) {
						setValide(false);
					}
				}
			}

		},0,1000);
		initialiser();
	}
	
	public void initialiser(){
		this.secondes = 120;
		this.setText(timeToText(secondes));
	}

	private String timeToText(long secondes){
		long min, sec;
		sec = secondes % 60;
		min = (secondes / 60) % 60;

		String sMin, sSec;
		sMin = timeFormat(min);
		sSec = timeFormat(sec);

		return "Timer : " + sMin + ":" + sSec;

	}

	private String timeFormat(long timeComposant){
		if(timeComposant<10)
			return "0"+new Long(timeComposant).toString();
		else{
			return new Long(timeComposant).toString();
		}
	}

	public void setSeconde (int seconde) {
		this.secondes = seconde;
	}
	
	public void setValide(boolean valide) {
		this.valide = valide;
	}
}