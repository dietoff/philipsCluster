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
	  size(w*10, h*10);
	  background(0);
	  noSmooth();
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
	 
	 PImage img = get(0,0,50,18);
	 image(img,w,0,w*9,h*9);
	 stroke(255);
	 rect(w*1.0f,0.0f,w*4.5f,h*9f);
	 rect(w*5.5f,0.0f,w*9f,h*9f);
	 p.toFacade(img);
	}

static public void main(String[] args) {
	  PApplet.main(RandomWalk.class.getName());
	}
}
