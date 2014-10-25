package applets;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;

public class ScanImage extends PApplet {
	PShape path;
	PImage image;
	float x,y = 0;
	int f = 2;
	Facade p;
	
	public void setup() {
		p = new Facade(this);
		image = loadImage("../data/glacier.jpg");
		size (image.width,image.height);
//		size(50,18);
//		path = generatePath();
	}

	public void draw() {
		image(image,0,0);
		noFill();
		stroke(0);
		rect(x,y,50,18);
		
		PImage pim = image.get((int)x,(int)y,50,18);
		
		x = (x + 0.05f)%image.width;
		y = (y + 0.01f)%image.width;
		
		p.toFacade(pim);
	}
	
	PShape generatePath() {
		
		
		return null;
	}

}
