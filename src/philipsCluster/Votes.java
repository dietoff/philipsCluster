package philipsCluster;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import eDMX.*;

public class Votes extends PApplet {
	int h = 18;
	int w = 50;
	int sc = 10;

	// state variables
	int frame = 0; //current frame number
	int selectedCluster = -1; // Cluster that is highlighted, -1 if none
	boolean initMessage = false; // set true to initiate scrolling message
	int startframe = 0; // startframe of the scrolling message, for syncing
	boolean isScrolling= false; // as long as the text is scrolling, this is true
	int skipfactor = 0;

	//physics properties
	double repelFactor = 0.01;  // repel strength between nodes
	double attractFactor = 0.05; // attract strength between nodes
	double maxDistance = h/3; //don't repel beyond this distance

	// elements
	int nClusters = 5; // number of clusters
	Cluster[] clusters = new Cluster[nClusters];
	String[] clusterNames = {"relieved", "anxious", "exhausted", "relaxed", "fatigue"};
	float[] factors = {20,15,40,30,50}; // pulsing frequency
	Integer[] clusterColors = {this.color(252, 80, 123), this.color(252, 101, 61), this.color(98, 181, 112), this.color(255, 200, 62), this.color(0, 163, 195)};
	float diameter = 1f; // pixel size

	PGraphics p; // off screen buffer, for simulation purposes
	private PFont f;
	private boolean isBarMode; //  this is true if nodes are arranged in bar charts
	private int columnwidth = 6;
	private int total; // total number of nodes, only for calculating percentages
	private boolean skip; // if one cluster gets larger than 144, eliminate half of the nodes in each cluster

	int numChainsTotal = 18;
	int chainLength = 50;

	// philips hardware
	sACNSource source;
	sACNUniverse[] universeArray = new sACNUniverse[numChainsTotal];
	// Byte array length = (number of LEDs in chain) * 3
	byte[][] chainDataArray = new byte[numChainsTotal][chainLength * 3];
	private LoadVotes loadVotes;
	private boolean runNext = false;


	public void setup() {
		size(w*sc,h*sc);

		//create clusters
		for(int i=0; i<nClusters;i++) {
			clusters[i] =new Cluster(clusterNames[i],clusterColors[i],factors[i]);
		}
		// lets add some random nr of nodes
		for(Cluster c:clusters) {
			c.initializeVotes((int) random(1,144), (double)random(0,w), (double)random(0,h));
		}
		// create buffer and font
		p = createGraphics(w,h);
		f = createFont("Wendy", 10);
		p.textFont(f);
		noSmooth();
		p.noSmooth();
		initFacade();
		loadVotes = new LoadVotes(this,"loader");
	}

	public void draw() {
		frame++;
		if (frame==Integer.MAX_VALUE)  frame = 0; // prevent overflow exception, just to be safe
		
		if (runNext) loadVotes = new LoadVotes(this,"loader");
		
		//automatic();

		// first create list of all nodes
		ArrayList<Node> nodes = new ArrayList<Node>();
		for (int i=0;i<clusters.length;i++) {
			if (clusters[i].nodes.size()>144) skip=true; // as soon as one cluster is longer than 144, lets start skipping nodes to keep up with rendering
			nodes.addAll(clusters[i].nodes);
			total = nodes.size();
		}
		//eliminate skipped nodes
		if(skip) {
			for (int i=0;i<clusters.length;i++) {
				int size = clusters[i].nodes.size();
				for (int j=0;j<size/2;j++) {
					clusters[i].nodes.remove(0); //remove first element, until we reach half cluster size
				}
				total = nodes.size(); // adjust totals for percentages
			} 
			skip=false; //reset skip flag until next time
		}


		if (isBarMode) {
			bars(nodes); // display the bar chart
		}
		else {
			freeform(nodes); // free forces
		}

		if (initMessage) { // initiate text scrolling
			initMessage = false;
			startframe = frame;
			isScrolling = true;
		}

		if (isScrolling) textScroll("How do you feel?", this.color(255), startframe); // scroll question


		sendToFacade();

		scaleImage(p); // scale up rendered image for our simulator
	}

	private void automatic() {
		int n = (int) random(0,500);
		if (n<10) {
			int r = (int)random(0,5);
			Cluster c1 = clusters[r];
			c1.addVote(w,h);
		}

		if (n==1) {
			ArrayList<Node> nodes = new ArrayList<Node>();
			for (Cluster c:clusters) {
				nodes.addAll(c.nodes);
			}
			for (Node r:nodes) {
				r.pos.x = (double)random(0,w);
				r.pos.y = (double)random(0,h);
			}

		}
	}

	/**
	 * set up node placements in the bar-chart
	 * @param nodes
	 */
	private void bars(ArrayList<Node> nodes) {
		{
			//set targets for movement, sort nodes into 2x3 table
			int counter = 0;
			for (int i=0;i<3;i++) {
				for (int j=0; j<2; j++) {
					Cluster c = clusters[counter];
					Iterator<Node> n = c.nodes.iterator();
					int x = 0;
					int y = 0;
					while (n.hasNext()) {
						Node node = n.next();
						node.goal = new Vector(x+j*24,y+i*6);
						y++;
						if (y>=6) {
							y=0;
							x++;
						}
					}
					counter++;
					if (counter>4) break; // prevent out of bounds error, since we only have 5 clusters
				}
			}

			// make nodes move slowly towards their goals
			for (Node n:nodes) {
				Vector d = Vector.subtract(n.goal, n.pos);
				d.mult(0.1f);
				n.pos.add(d);
			}

			// render nodes and text
			renderNodes(nodes);
			barsText(nodes);
		}
	}

	/**
	 * this calculates the position of the nodes in the force model
	 * @param nodes
	 */
	private void freeform(ArrayList<Node> nodes) {
		{
			// attract
			for (int i=0;i<clusters.length;i++) {
				Cluster c = clusters[i];
				c.updateCenter(); // calculate new cluster center
				c.attractNodes(attractFactor+0.02*sin((float) (frame/c.factor))); // make nodes move a bit to the cluster center
			} 

			// if we have highlighted a cluster, lets show only this one - create a new nodes arraylist without all the others and center cluster.
			if (selectedCluster>-1) {
				Cluster c = clusters[selectedCluster];
				c.setCenter(w/2, h/3);
				c.attractNodes(attractFactor+0.02*sin((float) (frame/c.factor)));
				nodes.clear();
				nodes.addAll(c.nodes);
			}

			// repel nodes
			repel(nodes);
			boundaryConditions(nodes); // make borders of the screen repelling
		}
		// again render our nodes and text
		renderNodes(nodes);
		freeText();
	}

	/**
	 * method for calculating repelling forces between nodes
	 * @param nodes
	 */
	private void repel(ArrayList<Node> nodes) {
		for (Node n1:nodes){
			for (Node n2:nodes){
				if (n1 != n2) {
					double distance = Vector.distance(n1.pos, n2.pos)+0.00000000001; // add to avoid division by zero
					if (distance < maxDistance) {
						Vector d = Vector.subtract(n2.pos, n1.pos);
						d.mult(repelFactor/(distance*distance)); 
						n2.pos.add(d);  
						n1.pos.subtract(d); 
					}
				}
			}
		}
	}

	/**
	 * make edges of the screen repel nodes (for force-based mode)
	 * @param nodes
	 */
	private void boundaryConditions(ArrayList<Node> nodes) {
		float r = h/2;
		float rep = 0.007f;
		for(Node n:nodes) {
			if (n.pos.x > w-r) 	n.pos.x -= rep*(n.pos.x-(w-r));
			if (n.pos.x < r) 	n.pos.x += rep*(r-n.pos.x);
			if (n.pos.y > h-r) 	n.pos.y -= rep*(n.pos.y-(h-r));
			if (n.pos.y < r) 	n.pos.y += rep*(r-n.pos.y);
		}
	}

	/**
	 * render our nodes to the buffer
	 * @param nodes
	 */
	private void renderNodes(ArrayList<Node> nodes) {
		p.beginDraw();
		p.background(0);
		p.smooth();
		p.noStroke();
		for(Node n:nodes) {
			int col = n.col;
			p.fill(col); 
			p.rect((float)n.pos.x,(float)n.pos.y,(float)diameter,(float)diameter);
		}
		p.endDraw();
	}

	/**
	 * this lets a text scroll across the screen with a color and a startframe. 
	 * to initiate, set initMessage = true
	 * @param s
	 * @param c
	 * @param st
	 */
	private void textScroll(String s, int c, int st) {
		p.beginDraw();
		int w = (int) p.textWidth(s);
		int cur = (-frame+st+w*3)/3;
		if (cur > w) isScrolling = false;
		p.fill(c);
		p.noSmooth();
		p.text(s,cur,12);
		p.endDraw();
	}


	/**
	 * render the labels in the force based mode
	 */
	private void freeText() {
		if (selectedCluster>-1) {
			p.beginDraw();
			p.fill(clusters[selectedCluster].color);
			p.noSmooth();
			p.text(clusters[selectedCluster].name + " " +round(clusters[selectedCluster].nodes.size()*100/total)+"%", 1, h);
			p.endDraw();
		}
	}

	/**
	 * this renders the text in the bar chart mode
	 * @param nodes
	 */
	private void barsText(ArrayList<Node> nodes) {
		p.beginDraw();
		p.noSmooth();
		int counter = 0;
		for (int i=0;i<3;i++) {
			for (int j=0; j<2; j++) {
				Cluster c = clusters[counter];
				int x = 0;
				int y = 0;
				p.fill(this.color(255));
				p.text(round(c.nodes.size()*100/total)+"%",12+x+j*24,6+y+i*6);
				counter++;
				if (counter>4) break;
			}
		}
		p.endDraw();
	}

	/**
	 * scale our rendered image for the simulator 
	 * @param p2
	 */
	private void scaleImage(PGraphics p2) {
		noSmooth();
		stroke(255);
		//image(p2,0,0,w,h);
		image(p2,0,0,w*sc,h*sc);
		for (int i=0;i<4; i++) {
			line(0,(i*h/3)*sc,w*sc,(i*h/3)*sc);
		}
		for (int j=0;j<8;j++){
			line((j*w/8)*sc,0,(j*w/8)*sc,h*sc);
		}
	}

	private void initFacade() {
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

	private void sendToFacade() {
		for (int i = 0; i < numChainsTotal; i++) {
			for (int j = 0; j < chainLength; j++) {
				int j2 = p.pixels[i*chainLength + j];
				chainDataArray[i][3*j + 0] = (byte) red(j2);
				chainDataArray[i][3*j + 1] = (byte) green(j2);
				chainDataArray[i][3*j + 2] = (byte) blue(j2);
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

	/* convenience functions for controlling the simulation
	 * @see processing.core.PApplet#keyReleased()
	 */
	public void keyReleased() {
		switch (key) {
		case 49:
			int r = (int)random(0,5);
			Cluster c1 = clusters[r];
			c1.addVote(w,h);
			break;
		case 50:
			selectedCluster = (int)random(0,5);
			break;
		case 51:
			selectedCluster = -1;
			break;
		case 52:
			isBarMode = !isBarMode;
			break;
		case 53:
			initMessage = true;
			break;
		default:
			
			ArrayList<Node> nodes = new ArrayList<Node>();
			for (Cluster c:clusters) {
				nodes.addAll(c.nodes);
			}
			for (Node n:nodes) {
				n.pos.x = (double)random(0,w);
				n.pos.y = (double)random(0,h);
			}
			break;
		}



	}

	public void doneLoading(String s) {
		if (s!=null){
		String separators = ";:";
		String[] words = splitTokens(trim(s), separators);
		printArray(words);
		Integer integer = Integer.getInteger(words[5]);
		Integer integer2 = Integer.getInteger(words[9]);
		Integer integer3 = Integer.getInteger(words[13]);
		Integer integer4 = Integer.getInteger(words[17]);
		Integer integer5 = Integer.getInteger(words[21]);
		int vote[] = {integer,integer2,integer3,integer4,integer5};
		}
	}

	class LoadVotes implements Runnable {
		Votes votes;
		Thread runner;
		public LoadVotes() {
		}
		public LoadVotes(Votes p, String threadName) {
			runner = new Thread(this, threadName); 
			runner.start();
			votes=p;
		}
		public void run() {
			URL url;
			try {
				url = new URL("http://nuweb9.neu.edu/visualization/dotvote/tally.txt");
				InputStream i = url.openStream();
				Scanner scan = new Scanner(i);
				final String totalText = scan.nextLine();
				votes.doneLoading(totalText);
				votes.runNext  = true;
			} catch (MalformedURLException e) {
				votes.runNext  = true;
				e.printStackTrace();
			} catch (IOException e) {
				votes.runNext  = true;
				e.printStackTrace();
			}
		}
	}
}
