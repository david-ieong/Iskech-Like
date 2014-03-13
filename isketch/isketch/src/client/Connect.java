package client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
/**
 * 
 * @author david & quentin
 * Classe permettant la connexion a la socket
 */
public class Connect {

	private Modele mod;
	
	public Connect(Modele mod) {
		this.mod = mod;
		connection();
	}
	
	private void connection() {
		Socket sock;
		TraiterInput t;
		try {
			sock = new Socket(mod.getAdresse(),mod.getPort());
			
			BufferedReader inchan = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			OutputStreamWriter outchan = new OutputStreamWriter(new DataOutputStream(sock.getOutputStream()));
			mod.setInchan(inchan);
			mod.setOutchan(outchan);
			mod.setIsConnected(true);
			t = new TraiterInput(mod);
			t.start();
		} catch (UnknownHostException e) {
			System.err.println("Erreur lors de la connection");
		} catch (IOException e) {
			System.err.println("Erreur lors de la connection");
		} catch (Exception e) {
			System.err.println("Erreur lors de la connection");
		}
	}
}
