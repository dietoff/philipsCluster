package applets;

import processing.core.PApplet;
import processing.core.PImage;
import eDMX.*;

public class Images extends PApplet {


	int numChainsTotal = 18;
	int chainLength = 50;
	int nr = 0;
	int frame = 0;
	String pics[];
	sACNSource source;
	sACNUniverse[] universeArray = new sACNUniverse[numChainsTotal];

	PImage chainPhoto;
	// Byte array length = (number of LEDs in chain) * 3
	byte[][] chainDataArray = new byte[numChainsTotal][chainLength * 3];

	public void setup() {
		int sketchScaleFactor = 5;
		size(chainLength*sketchScaleFactor, numChainsTotal*sketchScaleFactor);
		frameRate(30);
		pics = loadStrings("../data/dir.txt");

		chainPhoto = loadImage("../data/black.png");
		chainPhoto.loadPixels();

		source = new sACNSource(this, "NEU Source");
		// "Unit 1"
		// NEU sPDS480-1
		// IP Address: 10.1.3.230
		int minUniverseUnit1 = 0;
		int numChainsUnit1 = 8;
		for (int i = minUniverseUnit1; i < minUniverseUnit1+numChainsUnit1; i++){
			universeArray[i] = new sACNUniverse(source, (short)(i+1));
		}
		// "Unit 2"
		// NEU sPDS480-2
		// IP Address: 10.1.3.231
		int minUniverseUnit2 = 8;
		int numChainsUnit2 = 8;
		for (int i = minUniverseUnit2; i < minUniverseUnit2+numChainsUnit2; i++){
			universeArray[i] = new sACNUniverse(source, (short)(i+1));
		}
		// "Unit 3"
		// NEU sPDS480-3
		// IP Address: 10.1.3.232
		int minUniverseUnit3 = 16;
		int numChainsUnit3 = 2;
		for (int i = minUniverseUnit3; i < minUniverseUnit3+numChainsUnit3; i++){
			universeArray[i] = new sACNUniverse(source, (short)(i+1));
		}
	}

	public void draw() {
		frame++;
		if (frame%300==0) { 
			nr++;
			if (nr==pics.length) nr=0;
			chainPhoto = loadImage(pics[nr]);
			chainPhoto.loadPixels();
		}

		background(0);
		image(chainPhoto, 0, 0, width, height);
		for (int i = 0; i < numChainsTotal; i++) {
			for (int j = 0; j < chainLength; j++) { 
				chainDataArray[i][3*j + 0] = (byte) this.red(chainPhoto.pixels[i*chainLength + j]);
				chainDataArray[i][3*j + 1] = (byte) this.green(chainPhoto.pixels[i*chainLength + j]);
				chainDataArray[i][3*j + 2] = (byte) this.blue(chainPhoto.pixels[i*chainLength + j]);
			}
		}
		for (int i = 0; i < numChainsTotal; i++) {
			universeArray[i].setSlots(0, chainDataArray[i]);
		}
		try {
			for (int i = 0; i < numChainsTotal; i++) {
				universeArray[i].sendData();
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
			exit();
		}
	}

	public void keyReleased() {
		if (keyCode == UP) {
			nr++;
			if (nr==pics.length) nr=0;

		} 
		if(keyCode == DOWN) {
			nr--;
			if (nr<0) nr=pics.length-1;
		}

		chainPhoto = loadImage(pics[nr]);
		chainPhoto.loadPixels();
	}
	static public void main(String[] args) {
		PApplet.main(Images.class.getName());
	}
}
