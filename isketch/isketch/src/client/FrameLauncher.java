package client;

import client.UI.IsketchUI;

/**
 * 
 * @author david & quentin 
 * Classe qui lance la fenêtre
 */
public class FrameLauncher {	
	public FrameLauncher(Modele mod){	
		IsketchUI fen = new IsketchUI("Isketch", mod);
		fen.setLocationRelativeTo(null);
		fen.setVisible(true);
	}	
}
