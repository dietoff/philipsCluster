package applets;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;

public class Eyes extends PApplet {

	PFont f;
	PGraphics p;
	Facade fac = new Facade(this);
	int w= 50;
	int h= 18;
	float start = 0;
	float end = PI/2;
	int rads = 5;
	int rade = 5;
	int duration = 10;
	int frame = 0;

	public void setup() {
		size(w*10, h*10);
		p = createGraphics(w, h);
		frameRate(30);
		noSmooth();
		p.noSmooth();
		duration = (int) random(8,60);
	}

	public void draw() {
		frame++;
		if (frame==duration) {
			frame =0;
			start = end;
			end = random(-PI/2,PI/2);
			duration = (int) random(8,60);
			rads = rade;
			rade = (int) random(3,5);
		}
		float angle = lerp(start,end,(float)frame/(float)duration);
		float radius = lerp(rads,rade,(float)frame/(float)duration);
		p.beginDraw();
		p.background(0);
		p.stroke(255);
		p.noFill();
		p.ellipse(12,8,23,17);
		p.ellipse(36,8,23,17);
		p.fill(255);
		p.translate(12, 8);
		p.pushMatrix();
		p.rotate(angle);
		p.translate(0, radius);
		p.ellipse(0, 0, 6, 6);
		p.popMatrix();
		p.translate(24, 0);
		p.rotate(angle);
		p.translate(0, radius);
		p.ellipse(0, 0, 6, 6);
		p.endDraw();
		noSmooth();
		fac.toFacade(p);
		image(p,0,0,w*10,h*10);
	}



	static public void main(String[] args) {
		PApplet.main(Eyes.class.getName());
	}

}
