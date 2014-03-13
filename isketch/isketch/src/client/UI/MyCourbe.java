package client.UI;

import java.awt.Color;
import java.awt.geom.Point2D;
/**
 * 
 * @author david & quentin
 * Classe permettant contenir les données pour faire une courbe de bezier
 */
public class MyCourbe implements MyDraw{
	private Point2D p1;
	private Point2D p2;
	private Point2D p3;
	private Point2D p4;
	private Color color;
	private int stroke;
	
	public MyCourbe(Point2D p1, Point2D p2, Point2D p3, Point2D p4,Color c, int s) {
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		this.p4 = p4;
		color = c;
		stroke = s;
	}
	
	public Point2D getP1() {
		return p1;
	}

	public Point2D getP2() {
		return p2;
	}
	
	public Point2D getP3() {
		return p3;
	}

	public Point2D getP4() {
		return p4;
	}

	public Color getColor() {
		return color;
	}

	public int getStroke() {
		return stroke;
	}
}
