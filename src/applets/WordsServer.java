package applets;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;

public class WordsServer extends PApplet {

	PFont f;
	PGraphics p;
	int w= 48;
	int h= 18;
	String votes[];
	int colors[] = {color(255),color(252,80,123),color(252,101,61),color(98,181,112),color(255,200,62),color(0,163,195)};

	public void setup() {
	  size(w*10, h*10);
	  f = createFont("Wendy", 10);
	  // create a graphics buffer (to scale up)
	  p = createGraphics(w, h);
	  noSmooth();
	  
	  // load from server
	  String lines[] = loadStrings("http://azmar.org/files/tally.txt");
	  // split up text-response from server
	  String separators = ";:";
	  String[] words = splitTokens(trim(lines[0]), separators);
	  // pick out what we want and put it into new string array
	  String vote[] = {".vote",words[5],words[9],words[13],words[17],words[21]};
	  // assign it to our global variable (to be able to access it later)
	  votes = vote;
	}

	public void draw() {
	  p.beginDraw(); // start drawing to the buffer
	  p.textFont(f);
	  p.background(50);
	  p.noSmooth();
	  // draw the strings from votes as a grid
	  int counter = 0;
	  for (int i = 0; i<2;i++) {
	    for (int j = 1; j<4;j++){
	      p.fill(colors[counter]);
	      p.text(votes[counter],i*24+1, j*6-1);
	      counter++;
	    }
	  }
	  p.endDraw(); // finish drawing to the buffer
	  noSmooth();
	  // now take the buffer image from p and put it on the screen with scale =10
	  image(p,0,0,w*10,h*10);
	}


	
}
