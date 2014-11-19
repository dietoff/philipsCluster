package applets;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;


public class Card extends PApplet {
	int w = 50;
	int h = 18;
	Facade p;

	public void setup() {
		size(w, h);
	}

	public void draw() {
		noStroke();
		fill(0,0,0);
		rect(0,0,w,h);
		noFill();
		stroke(255);
		strokeWeight(1.5f);
		pushMatrix();
		translate(w*0.5f, h*0.5f);
		rotate(frameCount / -100.0f);
		star(0, 0, 3f, 7f, 5); 
		popMatrix();
		pushMatrix();
		translate(w*0.18f, h*0.25f);
		rotate(frameCount / -100.0f);
		star(0, 0, 1f, 4f, 5); 
		popMatrix();
		pushMatrix();
		strokeWeight(1f);
		translate(w*0.83f, h*0.75f);
		rotate(frameCount / -100.0f);
		star(0, 0, 1f, 4f, 5); 
		popMatrix();
		pushMatrix();
		strokeWeight(0.5f);
		translate(w*0.35f, h*0.75f);
		rotate(frameCount / -100.0f);
		star(0, 0, 1.5f, 4f, 5); 
		popMatrix();
		pushMatrix();
		translate(w*0.69f, h*0.15f);
		rotate(frameCount / -100.0f);
		star(0, 0, 0.5f, 3.5f, 5); 
		popMatrix();
		
		
		PImage pim = get();
		p.toFacade(pim);
	}

	void star(float x, float y, float radius1, float radius2, int npoints) {
		float angle = TWO_PI / npoints;
		float halfAngle = angle/2.0f;
		beginShape();
		for (float a = 0; a < TWO_PI; a += angle) {
			float sx = x + cos(a) * radius2;
			float sy = y + sin(a) * radius2;
			vertex(sx, sy);
			sx = x + cos(a+halfAngle) * radius1;
			sy = y + sin(a+halfAngle) * radius1;
			vertex(sx, sy);
		}
		endShape(CLOSE);
	}
}
