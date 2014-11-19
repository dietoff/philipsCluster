package applets;

import processing.core.PApplet;

public class Card extends PApplet {

	public void setup() {
		size(50, 18);
	}

	public void draw() {

		background(0);
		noFill();
		stroke(255);
		strokeWeight(1.5f);
		pushMatrix();
		translate(width*0.5f, height*0.5f);
		rotate(frameCount / -100.0f);
		star(0, 0, 3f, 7f, 5); 
		popMatrix();
		pushMatrix();
		translate(width*0.25f, height*0.25f);
		rotate(frameCount / -100.0f);
		star(0, 0, 1f, 4f, 5); 
		popMatrix();
		pushMatrix();
		strokeWeight(1f);
		translate(width*0.75f, height*0.75f);
		rotate(frameCount / -100.0f);
		star(0, 0, 1f, 4f, 5); 
		popMatrix();
		pushMatrix();
		strokeWeight(0.5f);
		translate(width*0.35f, height*0.75f);
		rotate(frameCount / -100.0f);
		star(0, 0, 1.5f, 4f, 5); 
		popMatrix();
		pushMatrix();
		translate(width*0.65f, height*0.15f);
		rotate(frameCount / -100.0f);
		star(0, 0, 0.5f, 3.5f, 5); 
		popMatrix();
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
