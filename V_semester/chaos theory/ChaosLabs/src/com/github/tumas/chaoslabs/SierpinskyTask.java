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
		final SierpinskyCanvas sp = new SierpinskyCanvas();
		
		Button findPointButton = new Button("Highligh Point");
		findPointButton.addActionListener(new ActionListener (){
			public void actionPerformed(ActionEvent ae){
				int decimalNumber = Integer.parseInt(numberText.getText(), 3);
				
				if (decimalNumber > sp.getArraySize()){
					System.out.println("Selected Point does not exist");
				}
				
				sp.highlightPoint(sp.getGraphics(), decimalNumber, Color.yellow);
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
	private Point2D.Float[] points;
	private int iterations;
	private int counter;
	private int arraySize;
	
	@Override
	public int iY(float y){return maxY - Math.round(y);}
	
	public SierpinskyCanvas() {
		this(5);
	}
	
	public SierpinskyCanvas(int iterations){
		setArraySize((int) (( 1 - Math.pow(3, iterations + 1)) / -2) + 2);
		
		points = new Point2D.Float[getArraySize()];
		this.iterations = iterations;
	}
	
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
	
		registerPoint(xA, yA);
		registerPoint(xB, yB);
		registerPoint(xC, yC);
	
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

			registerPoint(xA1, yA1);
			registerPoint(xB1, yB1);
			registerPoint(xC1, yC1);
			
			g.drawLine(iX(xA1), iY(yA1), iX(xB1), iY(yB1));
			g.drawLine(iX(xB1), iY(yB1), iX(xC1), iY(yC1));
			g.drawLine(iX(xC1), iY(yC1), iX(xA1), iY(yA1));
		
			drawSierpinsky(g, iteration - 1, xC1, yC1, xB1, yB1, xC, yC);
			drawSierpinsky(g, iteration - 1, xA, yA, xA1, yA1, xC1, yC1);
			drawSierpinsky(g, iteration - 1, xA1, yA1, xB, yB, xB1, yB1);
		}
	}

	private void registerPoint(float x, float y){
		points[counter++ % getArraySize()] = new Point2D.Float(x, y);
	}
	
	public void highlightPoint(Graphics g, int number, Color color){
		Point2D.Float pnt = points[number];
		Color tempColor = g.getColor();
		Graphics2D ga = (Graphics2D) g;
		Shape circle = new Ellipse2D.Float((float)pnt.getX() - 2, (float)iY((float)pnt.getY()), 5f, 5f);
		
		g.setColor(color);
		ga.draw(circle);
		ga.fill(circle);
		g.setColor(tempColor);
	}

	public void setArraySize(int arraySize) {
		this.arraySize = arraySize;
	}

	public int getArraySize() {
		return arraySize;
	}
}