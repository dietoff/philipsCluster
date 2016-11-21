
package applets;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;

public class SimpleImage extends PApplet {
	PShape path;
	PImage image;
	Facade p;

	public void setup() {
		p = new Facade(this);
		image = loadImage("../data/rgb.png");
				size(50,18);
	}

	public void draw() {
		image(image,0,0); // draw on screen, just for monitoring purposes
		p.toFacade(image); // send to facade
	}

	static public void main(String[] args) {
		PApplet.main(SimpleImage.class.getName());
	}

}
