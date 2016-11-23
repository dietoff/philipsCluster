package applets;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;

public class Words extends PApplet {

	PFont f;
	PGraphics p;
	Facade fac = new Facade(this);
	String text = "we are back hello hello";
	int frame = -text.length()*4;
	int w= 50;
	int h= 18;

	public void setup() {
	  size(w*10, h*10);
//	  printArray(PFont.list());
	  f = createFont("Wendy", 10);
	  p = createGraphics(w, h);
	  frameRate(3);
	  noSmooth();
	  p.noSmooth();
	  p.textFont(f);
	}

	public void draw() {
	  frame++;
	  if (frame==text.length()*4) {
	    frame = -text.length()*4;
	  }
	  p.beginDraw();
	  p.background(0);
	  p.fill(255);
	  p.text(text, frame, 12);
	  p.endDraw();
	  noSmooth();
	  fac.toFacade(p);
	  image(p,0,0,w*10,h*10);
	}



	static public void main(String[] args) {
		PApplet.main(Words.class.getName());
	}

}
