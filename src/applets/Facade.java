package applets;

import java.awt.Graphics;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import eDMX.*;

class Facade {
	int numChainsTotal = 18;
	int chainLength = 50;
	PApplet p;
	sACNSource source;
	sACNUniverse[] universeArray = new sACNUniverse[numChainsTotal];
	byte[][] chainDataArray = new byte[numChainsTotal][chainLength * 3];


	Facade(PApplet p_) {
		this.p = p_;
		source = new sACNSource(p, "NEU Source");
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

	void toFacade(PImage chainPhoto) {
		for (int i = 0; i < numChainsTotal; i++) {
			for (int j = 0; j < chainLength; j++) { 
				int j2 = chainPhoto.pixels[i*chainLength + j];
				chainDataArray[i][3*j + 0] = (byte)(p.red(j2));
				chainDataArray[i][3*j + 1] = (byte)(p.green(j2));
				chainDataArray[i][3*j + 2] = (byte)(p.blue(j2));
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
			p.exit();
		}
	}



	void toFacade(PGraphics p) {
		for (int i = 0; i < numChainsTotal; i++) {
			for (int j = 0; j < chainLength; j++) {
				int j2 = p.pixels[i*chainLength + j];
				chainDataArray[i][3*j + 0] = (byte) p.red(j2);
				chainDataArray[i][3*j + 1] = (byte) p.green(j2);
				chainDataArray[i][3*j + 2] = (byte) p.blue(j2);
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
		}
	}
}

