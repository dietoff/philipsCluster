package applets;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

public class RandomWalk2 extends PApplet {
	
	Facade p = new Facade(this);
	int w = 50;
	int h = 18;
	float x = h/2;
	float y = w/2;
	float range = 4;
	PGraphics pim;

	public void setup() 
	{
	  size(w, h);
	  background(0);
	  pim = createGraphics(w,h);
	}

	public void draw() {
	 pim.fill(0,0,0,5); // fill screen with a transparent rectangle to dim previous strokes
	 pim.noStroke();
	 pim.rect(0,0,w,h);
	 float newx = x+random(-range,range); // generate random destination
	 float newy = y+random(-range,range);
	 newx = constrain(newx,0,w); // make sure line stays on screen
	 newy = constrain(newy,0,h);
	 
	 pim.stroke(255);
	 pim.line(x,y,newx,newy); // draw new line segment
	 
	 x= newx; // update starting point
	 y= newy;
	 
	 
	 p.toFacade(pim);
	}
}


