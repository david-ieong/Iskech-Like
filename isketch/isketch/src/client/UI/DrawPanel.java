package client.UI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * 
 * @author david & quentin
 * Classe represantant la zone de dessin
 */
public class DrawPanel extends JPanel{

	private static final long serialVersionUID = 1L;
	private List<MyDraw> listLine;
	public DrawPanel() {
		super();
		setBackground(Color.white);
		setPreferredSize(new Dimension(700, 450));
		this.setBorder(BorderFactory.createLineBorder(Color.black));
		this.listLine = new ArrayList<MyDraw>(); 
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D gprim = (Graphics2D)g;
		MyLine line;
		MyCourbe courbe;
		CubicCurve2D curve;
		MyDraw draw;
		gprim.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		if (null != listLine && listLine.size() != 0) {
			for (int i = 0; i< listLine.size(); i++) {
				draw = listLine.get(i);
				if (draw instanceof MyLine) {
					line = (MyLine) draw;
					gprim.setStroke(new BasicStroke(line.getStroke(), BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
					gprim.setColor(line.getColor());
					gprim.draw(new Line2D.Double(line.getP1(), line.getP2()));
				} else if (draw instanceof MyCourbe) {
					courbe = (MyCourbe) draw;
					gprim.setStroke(new BasicStroke(courbe.getStroke()));
					gprim.setColor(courbe.getColor());
					curve = new CubicCurve2D.Double();
					curve.setCurve(courbe.getP1().getX(),
							courbe.getP1().getY(),
							courbe.getP2().getX(),
							courbe.getP2().getY(),
							courbe.getP3().getX(),
							courbe.getP3().getY(),
							courbe.getP4().getX(),
							courbe.getP4().getY());
					gprim.draw(curve);
				}
			}
		}
	}

	public void addLine(MyDraw draw) {
		listLine.add(draw);
	}

	public void clearList() {
		listLine = new ArrayList<MyDraw>();
	}

}
