package applets;

import processing.core.PApplet;
import processing.core.PImage;

public class RandomWalk extends PApplet {
	
	Facade p = new Facade(this);
	int w = 50;
	int h = 18;
	float x = h/2;
	float y = w/2;
	float range = 4;

	public void setup() 
	{
	  size(w, h);
	  background(0);
	}

	public void draw() {
	 fill(0,0,0,5); // fill screen with a transparent rectangle to dim previous strokes
	 noStroke();
	 rect(0,0,w,h);
	 float newx = x+random(-range,range); // generate random destination
	 float newy = y+random(-range,range);
	 newx = constrain(newx,0,w); // make sure line stays on screen
	 newy = constrain(newy,0,h);
	 
	 stroke(255);
	 line(x,y,newx,newy); // draw new line segment
	 
	 x= newx; // update starting point
	 y= newy;
	 
	 PImage img = get();
	 
	 p.toFacade(img);
	}
}


