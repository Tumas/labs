package com.github.tumas.chaoslabs;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.github.tumas.chaoslabs.helper.ExtendedCanvas;

@SuppressWarnings("serial")
public class SierpinskyTask extends Frame {
	public SierpinskyTask() {
		super("Sierpinsky Triangle");
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){System.exit(0);};
		});
		
		setSize(600, 400);
		add("Center", new SierpinskyCanvas());
		setVisible(true);
	}
	
	public static void main(String[] args){
		new SierpinskyTask();
	}
}

@SuppressWarnings("serial")
class SierpinskyCanvas extends ExtendedCanvas {
	
	@Override
	public int iY(float y){return maxY - Math.round(y);}
	
	public void paint(Graphics g){
		initCanvasInfo();
		int minXY = Math.min(maxX, maxY);
		
		float side = 0.95F * minXY;
		float sideHalf = 0.5F * side;
		float h = sideHalf * (float)Math.sqrt(3);
		
		float xA = xCenter - sideHalf, yA = yCenter - h * 0.5F;
		float xB = xCenter + sideHalf, yB = yA;
		float xC = xCenter, yC = yCenter + h * 0.5F;
		
		g.drawLine(iX(xA), iY(yA), iX(xB), iY(yB));
		g.drawLine(iX(xB), iY(yB), iX(xC), iY(yC));
		g.drawLine(iX(xC), iY(yC), iX(xA), iY(yA));
	
		drawSierpinsky(g, 5, xA, yA, xB, yB, xC, yC);
	}
	
	public void drawSierpinsky(Graphics g, int iteration, float xA, float yA, float xB, float yB, float xC, float yC){
		float xA1, xB1, xC1;
		float yA1, yB1, yC1;
		
		if (iteration > 0){
			xA1 = (xA + xB) / 2;
			yA1 = (yA + yB) / 2;
			xB1 = (xB + xC) / 2;
			yB1 = (yB + yC) / 2;
			xC1 = (xC + xA) / 2;
			yC1 = (yC + yA) / 2;

			g.drawLine(iX(xA1), iY(yA1), iX(xB1), iY(yB1));
			g.drawLine(iX(xB1), iY(yB1), iX(xC1), iY(yC1));
			g.drawLine(iX(xC1), iY(yC1), iX(xA1), iY(yA1));
		
			drawSierpinsky(g, iteration - 1, xC1, yC1, xB1, yB1, xC, yC);
			drawSierpinsky(g, iteration - 1, xA, yA, xA1, yA1, xC1, yC1);
			drawSierpinsky(g, iteration - 1, xA1, yA1, xB, yB, xB1, yB1);
		}
	}
}