package client;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class Modele.
 *
 * @author david & quentin
 * The Class Modele contenant les données utilent aux vues et aux controleurs.
 */
public class Modele implements Observable{

	/** The obs. */
	private ArrayList<Observateur> obs;

	/** The inchan. */
	private BufferedReader inchan;

	/** The outchan. */
	private OutputStreamWriter outchan;

	/** The user name. */
	private String userName;

	/** The user name parse. */
	private String userNameParse;

	/** The adresse. */
	private String adresse;

	/** The current line. */
	private String currentLine;
	
	/** The drawer name. */
	private String drawerName;

	/** The port. */
	private int port;
	
	/** The timeout. */
	private int timeout;
	
	/** The user list. */
	private ArrayList<String> userList;
	
	/** The pen color. */
	private Color penColor;
	
	/** The p1. */
	private Point2D p1;
	
	/** The p2. */
	private Point2D p2;
	
	/** The p3. */
	private Point2D p3;
	
	/** The p4. */
	private Point2D p4;
	
	/** The stroke. */
	private int stroke;
	
	/** The is new line. */
	private boolean isNewLine;
	
	/** The is guess. */
	private boolean isGuess;
	
	/** The is drawing. */
	private boolean isDrawing;
	
	/** The is new info. */
	private boolean isNewInfo;
	
	/** The is new round. */
	private boolean isNewRound;
	
	/** The is new user. */
	private boolean isNewUser;
	
	/** The is reset. */
	private boolean isReset;
	
	/** The is spectactor. */
	private boolean isSpectactor;
	
	/** The is bezier. */
	private boolean isBezier;
	
	/** The is new courbe. */
	private boolean isNewCourbe;
	
	/** The is connected. */
	private boolean isConnected;
	
	/** The is new timeout. */
	private boolean isNewTimeout;
	
	/**
	 * Instantiates a new modele.
	 *
	 */
	public Modele () {
		this.obs = new ArrayList<Observateur>();
		this.userList = new ArrayList<String>();
		this.penColor = Color.black;
		this.userName = "";
		this.currentLine = "";
		this.drawerName = "";
		this.p1 = new Point2D.Double();
		this.p2 = new Point2D.Double();
		this.p3 = new Point2D.Double();
		this.p4 = new Point2D.Double();
		this.stroke = 1;
		this.isNewLine = false;
		this.isGuess = false;
		this.isDrawing = false;
		this.isNewInfo = false;
		this.isNewRound = false;
		this.isNewUser = false;
		this.isReset = false;
		this.isSpectactor = true;
		this.isBezier = false;
		this.isNewCourbe = false;
		this.isConnected = false;
		this.isNewTimeout = false;
		this.timeout = 30;
	}

	/**
	 * Parses the line.
	 *
	 * @param str the str
	 * @return the string[]
	 */
	public ArrayList<String> parseLine(String str) {
		ArrayList<String> tabRes = new ArrayList<String>();
		String currentWord = "";
		for (int i = 0; i< str.length(); i ++ ) {
			if (str.charAt(i) == '/') {
				tabRes.add(currentWord);
				currentWord = "";
			} else if (str.charAt(i) == '\\') {
				if (str.charAt(i + 1) == '/') {
					currentWord += "/";
				} else {
					currentWord += "\\\\";
				}
				i++;
			} else {
				currentWord += str.charAt(i);
			}
		}
		tabRes.add(currentWord);
		return tabRes;
	}

	/**
	 * Parses the user name.
	 *
	 * @param userName the user name
	 * @return the string
	 */
	public String parseUserName(String userName) {
		String res = "";
		for (int i = 0; i < userName.length(); i++) {
			if (userName.charAt(i) == '\\') {
				if (userName.charAt(i+1) == '\\') 
					res += "\\";
				else
					res += "/";
				i++;
			} else {
				res += userName.charAt(i);
			}
		}
		return res;
	}
	
	/**
	 * Gets the p1.
	 *
	 * @return the p1
	 */
	public Point2D getP1() {
		return p1;
	}

	/**
	 * Sets the p1.
	 *
	 * @param p1 the new p1
	 */
	public void setP1(Point2D p1) {
		this.p1 = p1;
	}

	/**
	 * Gets the p2.
	 *
	 * @return the p2
	 */
	public Point2D getP2() {
		return p2;
	}

	/**
	 * Sets the p2.
	 *
	 * @param p2 the new p2
	 */
	public void setP2(Point2D p2) {
		this.p2 = p2;
	}
	
	/**
	 * Gets the p3.
	 *
	 * @return the p3
	 */
	public Point2D getP3() {
		return p3;
	}
	
	/**
	 * Sets the p3.
	 *
	 * @param p the new p3
	 */
	public void setP3(Point2D p) {
		this.p3 = p;
	}
	
	/**
	 * Gets the p4.
	 *
	 * @return the p4
	 */
	public Point2D getP4() {
		return p4;
	}
	
	/**
	 * Sets the p4.
	 *
	 * @param p the new p4
	 */
	public void setP4(Point2D p) {
		this.p4 = p;
	}

	/**
	 * Gets the stroke.
	 *
	 * @return the stroke
	 */
	public int getStroke() {
		return stroke;
	}
	
	/**
	 * Sets the stroke.
	 *
	 * @param stroke the new stroke
	 */
	public void setStroke(int stroke) {
		this.stroke = stroke;
	}
	/**
	 * Gets the pen color.
	 *
	 * @return the pen color
	 */
	public Color getPenColor() {
		return penColor;
	}

	/**
	 * Sets the pen color.
	 *
	 * @param penColor the new pen color
	 */
	public void setPenColor(Color penColor) {
		this.penColor = penColor;
	}
	/**
	 * Gets the user list.
	 *
	 * @return the user list
	 */
	public ArrayList<String> getUserList() {
		return userList;
	}

	/**
	 * Sets the user list.
	 *
	 * @param userList the new user list
	 */
	public void setUserList(ArrayList<String> userList) {
		this.userList = userList;
	}
	
	/**
	 * Gets the user name parse.
	 *
	 * @return the user name parse
	 */
	public String getUserNameParse() {
		return userNameParse;
	}

	/**
	 * Sets the user name parse.
	 *
	 * @param userNameParse the new user name parse
	 */
	public void setUserNameParse(String userNameParse) {
		this.userNameParse = userNameParse;
	}

	/**
	 * Gets the current line.
	 *
	 * @return the current line
	 */
	public String getCurrentLine() {
		return currentLine;
	}

	/**
	 * Sets the current line.
	 *
	 * @param line the new current line
	 */
	public void setCurrentLine(String line) {
		this.currentLine = line;
	}
	
	/**
	 * Gets the drawer name.
	 *
	 * @return the drawer name
	 */
	public String getDrawerName() {
		return drawerName;
	}

	/**
	 * Sets the drawer name.
	 *
	 * @param drawerName the new drawer name
	 */
	public void setDrawerName(String drawerName) {
		this.drawerName = drawerName;
	}

	/**
	 * Gets the adresse.
	 *
	 * @return the adresse
	 */
	public String getAdresse() {
		return adresse;
	}

	/**
	 * Sets the adresse.
	 *
	 * @param adresse the new adresse
	 */
	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}

	/**
	 * Gets the port.
	 *
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Sets the port.
	 *
	 * @param port the new port
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Gets the user name.
	 *
	 * @return the user name
	 */
	public String getUserName(){
		return userName;
	}

	/**
	 * Sets the user name.
	 *
	 * @param userName the new user name
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Gets the outchan.
	 *
	 * @return the outchan
	 */
	public OutputStreamWriter getOutchan() {
		return outchan;
	}


	/**
	 * Sets the outchan.
	 *
	 * @param outchan the new outchan
	 */
	public void setOutchan(OutputStreamWriter outchan) {
		this.outchan = outchan;
	}

	/**
	 * Gets the inchan.
	 *
	 * @return the inchan
	 */
	public BufferedReader getInchan() {
		return inchan;
	}

	/**
	 * Sets the inchan.
	 *
	 * @param inchan the new inchan
	 */
	public void setInchan(BufferedReader inchan) {
		this.inchan = inchan;
	}
	
	/**
	 * Checks if is new line.
	 *
	 * @return true, if is new line
	 */
	public boolean isNewLine() {
		return isNewLine;
	}
	
	/**
	 * Sets the new line.
	 *
	 * @param b the new new line
	 */
	public void setNewLine(boolean b) {
		isNewLine = b;
	}
	
	/**
	 * Checks if is guess.
	 *
	 * @return true, if is guess
	 */
	public boolean isGuess() {
		return isGuess;
	}
	
	/**
	 * Sets the checks if is guess.
	 *
	 * @param b the new checks if is guess
	 */
	public void setIsGuess(boolean b) {
		isGuess = b;
	}
	
	/**
	 * Checks if is drawing.
	 *
	 * @return true, if is drawing
	 */
	public boolean isDrawing() {
		return isDrawing;
	}
	
	/**
	 * Sets the checks if is drawing.
	 *
	 * @param b the new checks if is drawing
	 */
	public void setIsDrawing(boolean b) {
		isDrawing = b;		
	}
	
	/**
	 * Checks if is new info.
	 *
	 * @return true, if is new info
	 */
	public boolean isNewInfo() {
		return isNewInfo;
	}
	
	/**
	 * Sets the checks if is new info.
	 *
	 * @param b the new checks if is new info
	 */
	public void setIsNewInfo(boolean b) {
		isNewInfo = b;
	}
	
	/**
	 * Checks if is new round.
	 *
	 * @return true, if is new round
	 */
	public boolean isNewRound() {
		return isNewRound;
	}

	/**
	 * Sets the checks if is new round.
	 *
	 * @param b the new checks if is new round
	 */
	public void setIsNewRound(boolean b) {
		isNewRound = b;
	}
	
	/**
	 * Checks if is new user.
	 *
	 * @return true, if is new user
	 */
	public boolean isNewUser() {
		return isNewUser;
	}

	/**
	 * Sets the new user.
	 *
	 * @param isNewUser the new new user
	 */
	public void setIsNewUser(boolean isNewUser) {
		this.isNewUser = isNewUser;
	}
	
	/**
	 * Checks if is reset.
	 *
	 * @return true, if is reset
	 */
	public boolean isReset() {
		return isReset;
	}

	/**
	 * Sets the checks if is reset.
	 *
	 * @param isReset the new checks if is reset
	 */
	public void setIsReset(boolean isReset) {
		this.isReset = isReset;
	}
	
	/**
	 * Checks if is spectactor.
	 *
	 * @return true, if is spectactor
	 */
	public boolean isSpectactor() {
		return isSpectactor;
	}
	
	/**
	 * Sets the checks if is spectactor.
	 *
	 * @param b the new checks if is spectactor
	 */
	public void setIsSpectactor(boolean b) {
		this.isSpectactor = b;
	}
	
	/**
	 * Checks if is bezier.
	 *
	 * @return true, if is bezier
	 */
	public boolean isBezier() {
		return isBezier;
	}
	
	/**
	 * Sets the checks if is bezier.
	 *
	 * @param b the new checks if is bezier
	 */
	public void setIsBezier(boolean b) {
		this.isBezier = b;
	}
	
	/**
	 * Checks if is new courbe.
	 *
	 * @return true, if is new courbe
	 */
	public boolean isNewCourbe() {
		return isNewCourbe;
	}
	
	/**
	 * Sets the checks if is new courbe.
	 *
	 * @param b the new checks if is new courbe
	 */
	public void setIsNewCourbe(boolean b) {
		this.isNewCourbe = b;
	}
	
	/**
	 * Checks if is connected.
	 *
	 * @return true, if is connected
	 */
	public boolean isConnected() {
		return isConnected;
	}
	
	/**
	 * Sets the checks if is connected.
	 *
	 * @param b the new checks if is connected
	 */
	public void setIsConnected(boolean b) {
		this.isConnected = b;
	}
	
	/**
	 * Checks if is new timeout.
	 *
	 * @return true, if is new timeout
	 */
	public boolean isNewTimeout() {
		return isNewTimeout;
	}

	/**
	 * Sets the new timeout.
	 *
	 * @param isNewTimeout the new new timeout
	 */
	public void setNewTimeout(boolean isNewTimeout) {
		this.isNewTimeout = isNewTimeout;
	}
	
	
	/**
	 * Gets the timeout.
	 *
	 * @return the timeout
	 */
	public int getTimeout() {
		return timeout;
	}
	
	/**
	 * Sets the timeout.
	 *
	 * @param timeout the new timeout
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	/* (non-Javadoc)
	 * @see client.Observable#ajout_Observateur(client.Observateur)
	 */
	@Override
	public void ajout_Observateur(Observateur o) {
		obs.add(o);
	}

	/* (non-Javadoc)
	 * @see client.Observable#supp_Observateur(client.Observateur)
	 */
	@Override
	public void supp_Observateur(Observateur o) {
		obs.remove(o);
	}

	/* (non-Javadoc)
	 * @see client.Observable#notifieTousObservateur()
	 */
	@Override
	public void notifieTousObservateur() {
		for (Observateur o : obs) {
			o.notifie(this);
		}
	}	
}