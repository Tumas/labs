package com.github.tumas.chaoslabs.helper;

import java.awt.Canvas;
import java.awt.Dimension;

@SuppressWarnings("serial")
public class ExtendedCanvas extends Canvas {
	protected int maxX, maxY, xCenter, yCenter;
	
	public int iX(float x){return Math.round(x);}
	public int iY(float y){return Math.round(y);}

	protected void initCanvasInfo(){
		Dimension d = getSize();
	
		this.maxX = d.width - 1;
		this.maxY = d.height - 1;
		
		this.xCenter = maxX / 2;
		this.yCenter = maxY / 2;
	}
}