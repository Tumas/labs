package com.github.tumas.chaoslabs;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.Shape;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.HashMap;

import com.github.tumas.chaoslabs.helper.ExtendedCanvas;

@SuppressWarnings("serial")
public class SierpinskyTask extends Frame {
	public SierpinskyTask() {
		super("Sierpinsky Triangle");
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){System.exit(0);};
		});
		
		setSize(700, 500);
		setLayout(new BorderLayout());

		final TextField numberText = new TextField();
		final SierpinskyCanvas sp = new SierpinskyCanvas(5);
		
		Button findPointButton = new Button("Highligh Point");
		findPointButton.addActionListener(new ActionListener (){

			public void actionPerformed(ActionEvent ae){
				Point2D.Float pnt = sp.pointsMap.get(numberText.getText());

				if (pnt == null){
					sp.showErrorMessage("There is no such point");
					return ;
				}

				sp.update(sp.getGraphics());
				sp.highlightPoint(pnt, Color.yellow);
			}
		});
		
		Panel bottomPanel = new Panel(new FlowLayout());
		Panel dataPanel = new Panel(new GridLayout(3, 0, 5, 5));
		
		dataPanel.add(numberText);
		dataPanel.add(findPointButton);
		bottomPanel.add(dataPanel);
		
		add(sp, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
		
		setVisible(true);
	}
	
	public static void main(String[] args){
		new SierpinskyTask();
	}
}

@SuppressWarnings("serial")
class SierpinskyCanvas extends ExtendedCanvas {
	private int[] iterationPoints;
	private int iterations;
	private int capacity;
	
	HashMap<String, Point2D.Float> pointsMap; 
	
	@Override
	public int iY(float y){return maxY - Math.round(y);}
	
	public SierpinskyCanvas() {
		this(5);
	}
	
	public SierpinskyCanvas(int iterations){
		capacity = (int) ((1 - Math.pow(3, iterations + 1)) / -2);
		pointsMap = new HashMap<String, Point2D.Float>(capacity);

		this.iterations = iterations;
		iterationPoints = new int[iterations];
		for (int i = 0; i < iterations; i++) iterationPoints[i] = 0;
	}
	
	public void paint(Graphics g){
		initCanvasInfo();
		resetStatistics();
		
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
	
		drawSierpinsky(g, iterations, xA, yA, xB, yB, xC, yC);
	}

	public void drawSierpinsky(Graphics g, int iteration, float xA, float yA, float xB, float yB, float xC, float yC){
		if (iteration > 0){
			float xA1, xB1, xC1;
			float yA1, yB1, yC1;

			xA1 = (xA + xB) / 2;
			yA1 = (yA + yB) / 2;
			xB1 = (xB + xC) / 2;
			yB1 = (yB + yC) / 2;
			xC1 = (xC + xA) / 2;
			yC1 = (yC + yA) / 2;
			
			pointsMap.put(generatePointID(iteration), new Point2D.Float(xA1, yA1));
			pointsMap.put(generatePointID(iteration), new Point2D.Float(xB1, yB1));
			pointsMap.put(generatePointID(iteration), new Point2D.Float(xC1, yC1));
			
			g.drawLine(iX(xA1), iY(yA1), iX(xB1), iY(yB1));
			g.drawLine(iX(xB1), iY(yB1), iX(xC1), iY(yC1));
			g.drawLine(iX(xC1), iY(yC1), iX(xA1), iY(yA1));
		
			drawSierpinsky(g, iteration - 1, xC1, yC1, xB1, yB1, xC, yC);
			drawSierpinsky(g, iteration - 1, xA, yA, xA1, yA1, xC1, yC1);
			drawSierpinsky(g, iteration - 1, xA1, yA1, xB, yB, xB1, yB1);
		}
	}

	public void highlightPoint(Point2D.Float pnt, Color color){
		Graphics2D ga = (Graphics2D) getGraphics();
		Color tempColor = ga.getColor();
		Shape circle = new Ellipse2D.Float((float)pnt.getX() - 2, (float)iY((float)pnt.getY()), 5f, 5f);
		
		ga.setColor(color);
		ga.draw(circle);
		ga.fill(circle);
		ga.setColor(tempColor);
	}
	
	public void resetStatistics(){
		for (int i = 0; i < iterations; i++) iterationPoints[i] = 0;
	}

	public void showErrorMessage(String msg){
		Graphics g = getGraphics();
		Color color = g.getColor();
		
		g.setColor(Color.RED);
		g.drawString(msg, 50, 30);
		g.setColor(color);
	}
	
	private String generatePointID(int iter)
	{
		String id = "";
		int number = iterationPoints[iterations - iter]++; 
		
		while (number >= 3){
			id = number % 3 + id;
			number /= 3;
		}
		id = number + id;

		for (int i = iterations - iter; i >= id.length();) { id = "0" + id; } 
		return id;
	}
}