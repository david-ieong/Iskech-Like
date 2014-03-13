package client.UI;

import java.awt.Color;
import java.awt.geom.Point2D;

/**
 * 
 * @author david & quentin
 * Classe permettant de contenir les donnees pour faire une ligne
 */
public class MyLine implements MyDraw{

	private Point2D p1;
	private Point2D p2;
	private Color color;
	private int stroke;
	
	public MyLine(Point2D p1, Point2D p2,Color c, int s) {
		this.p1 = p1;
		this.p2 = p2;
		color = c;
		stroke = s;
	}
	
	public Point2D getP1() {
		return p1;
	}

	public Point2D getP2() {
		return p2;
	}

	public Color getColor() {
		return color;
	}

	public int getStroke() {
		return stroke;
	}
}
