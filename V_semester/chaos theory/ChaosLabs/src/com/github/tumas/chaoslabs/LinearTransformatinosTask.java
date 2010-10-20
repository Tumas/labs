package com.github.tumas.chaoslabs;

import java.awt.AlphaComposite;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import com.github.tumas.chaoslabs.helper.ExtendedCanvas;

@SuppressWarnings("serial")
public class LinearTransformatinosTask extends Frame {
	public LinearTransformatinosTask(){
		super("Sierpinsky Triangle");
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){System.exit(0);};
		});
		
		setSize(800, 500);
		BikeCanvas bk = new BikeCanvas();
		add(bk);
		setVisible(true);
		
		bk.animate();
	}
	
	public static void main(String[] args){
		new LinearTransformatinosTask();
	}
}

@SuppressWarnings("serial")
class BikeCanvas extends ExtendedCanvas {
	Bike bike1 = new Bike(100, 200);
	int xLimit = 650;
	
	public void drawFullRectangle(Graphics g, FullRectangle r){
	    g.drawLine(iX(r.points[0].x), iY(r.points[0].y), iX(r.points[1].x), iY(r.points[1].y));
		g.drawLine(iX(r.points[1].x), iY(r.points[1].y), iX(r.points[2].x), iY(r.points[2].y));
		g.drawLine(iX(r.points[2].x), iY(r.points[2].y), iX(r.points[3].x), iY(r.points[3].y));
		g.drawLine(iX(r.points[3].x), iY(r.points[3].y), iX(r.points[0].x), iY(r.points[0].y));
	}
	
	public void drawBike(Graphics g, Bike b){
		drawFullRectangle(g, b.wheels[0]);
		drawFullRectangle(g, b.wheels[1]);
		
		g.drawLine(iX(b.rearFork.x1), iY(b.rearFork.y1), iX(b.rearFork.x2), iY(b.rearFork.y2));
		g.drawLine(iX(b.topFork.x1), iY(b.topFork.y1), iX(b.topFork.x2), iY(b.topFork.y2));
		g.drawLine(iX(b.rearFork.x2), iY(b.rearFork.y2), iX(b.topFork.x2), iY(b.topFork.y2));
		g.drawLine(iX(b.topFork.x2), iY(b.topFork.y2), iX(b.handleBar.x1), iY(b.handleBar.y1));
		g.drawLine(iX(b.handleBar.x2), iY(b.handleBar.y2), iX(b.handleBar.x1), iY(b.handleBar.y1));
	}
	
	public void paint(Graphics gg){
		Graphics2D g = (Graphics2D)gg;
		
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
	    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	    g.drawLine(xLimit, 100, xLimit, 286);
	    g.drawLine(100, 286, xLimit, 286);
	    
		drawBike(g, bike1);
	}

	public void animate(){
		int deltaX = 5;
		int deltaY = 0;
		int edge = 7;          // every 7th rotation steep angle hits the ground 
		double angle = Math.PI / (edge * 2);
		int i = edge - 1;
		
		while (true){
			if (bike1.handleBar.x2 >= xLimit) {
				deltaX = 0;
			}
			// slowing down at the end
			else if (bike1.handleBar.x2 >= xLimit - 10 && deltaX != 1){
				deltaX--;
			}
			
			if (i % edge == 0 ) {
				deltaY = 2;
				i = 0;
			}
			else if ((i + 1) % edge == 0) {
				deltaY = -2;
				}
			else {
				deltaY = 0;
				}
			i++;
			
			bike1.moveSmoothly(deltaX, deltaY, angle);
			repaint();
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException ex){}
		}
	}	
}

/*  ___________
 *  |      ___|
 *  |  ____|  |
 *  | /    \  |
 *  |[]    [] |
 */
class Bike {
	Line2D.Float rearFork, topFork;
	Line2D.Float handleBar;
	FullRectangle[] wheels = new FullRectangle[2];	
	
	private int wheelLength = 30;
	private int lengthBetweenWheels = 50;
	private int heightWithoutWheels = 50;
	private int handleBarLength = 35;
	
	/*
	 * x, y denote top left corner of bike rectangle
	 */
	Bike(float x, float y){
		float temp, temp1;
		
		wheels[0] =  new FullRectangle(new Point2D.Float(x, y + wheelLength + heightWithoutWheels),
				new Point2D.Float(x, y + heightWithoutWheels),
				new Point2D.Float(x + wheelLength, y + heightWithoutWheels),
				new Point2D.Float(x + wheelLength, y + heightWithoutWheels + wheelLength));
		
		temp = x + 2 * wheelLength + lengthBetweenWheels;
		temp1 = y + heightWithoutWheels;
		
		wheels[1] =  new FullRectangle(new Point2D.Float(x + wheelLength + lengthBetweenWheels,
					temp1 + wheelLength),
				new Point2D.Float(x + wheelLength + lengthBetweenWheels, temp1),
				new Point2D.Float(temp, temp1),
				new Point2D.Float(temp, temp1 + wheelLength));
		
		temp = wheelLength / 2;
		temp1 = heightWithoutWheels / 2;
		
		rearFork = new Line2D.Float(x + temp, y + heightWithoutWheels + temp, x + wheelLength + 10, y + temp1);
		topFork = new Line2D.Float(wheels[1].points[1].x + temp, y + heightWithoutWheels + temp,
				 wheels[1].points[1].x + temp - 20, y + temp1);
		handleBar = new Line2D.Float(topFork.x2 + 10, topFork.y2 - temp1, topFork.x2 + 10 + handleBarLength,
				topFork.y2 - temp1);
	}
	
	public Bike moveSmoothly(float deltaX, float deltaY, double angle){
		Point2D.Float center;
		
		for (FullRectangle wheel : wheels){
			center = wheel.getCenterPoint();
			wheel.move(-center.x, -center.y).rotate(angle).move(center.x + deltaX, center.y + deltaY);
		}
		
		rearFork.setLine(rearFork.x1 + deltaX, rearFork.y1 + deltaY, rearFork.x2 + deltaX, rearFork.y2 + deltaY);
		topFork.setLine(topFork.x1 + deltaX, topFork.y1 + deltaY, topFork.x2 + deltaX, topFork.y2 + deltaY);
		handleBar.setLine(handleBar.x1 + deltaX, handleBar.y1 + deltaY, handleBar.x2 + deltaX, handleBar.y2 + deltaY);
		
		return this;
	}
}

class FullRectangle {	
	public Point2D.Float[] points = new Point2D.Float[4];

	public FullRectangle(Point2D.Float p1, Point2D.Float p2, Point2D.Float p3, Point2D.Float p4) {
		points[0] = p1;
		points[1] = p2;
		points[2] = p3;
		points[3] = p4;
	}

	public FullRectangle rotate(double angle){
		float xNew, yNew;

		for (Point2D.Float p : points){
			xNew = (float) (p.x * Math.cos(angle) - Math.sin(angle) * p.y);
			yNew = (float) (p.x * Math.sin(angle) + p.y * Math.cos(angle));
		
			p.x = xNew;
			p.y = yNew;
		}
		
		return this;
	}
	
	public FullRectangle move(float x, float y){
		for (Point2D.Float point : points){
			point.setLocation(point.x + x, point.y + y);
		}
		return this;
	}
	
	public Point2D.Float getCenterPoint(){
		return new Point2D.Float((points[0].x + points[2].x) / 2, (points[0].y + points[2].y) / 2);
	}
}