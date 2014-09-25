package philipsCluster;

import java.util.ArrayList;

public class Cluster {
	String name = "name";
	ArrayList<Node> nodes;
	int color;
	Vector center;
	double radius;
	float factor;


	public Cluster() {
		nodes = new ArrayList<Node>();
		name = "cluster";
		color = 0xffffffff;
		center = new Vector(0,0);
		radius = 3; //not used now
		factor = 25;
	}


	public Cluster(String n1, Integer col1, float factors) {
		nodes = new ArrayList<Node>();
		name = n1;
		color = col1;
		center = new Vector(0,0);
		radius = 3; //not used now
		factor = factors;
	}

	public Node addVote() {
		Node n = new Node(color,0,0);
		nodes.add(n);
		return n;
	}
	public Node addVote(int x, int y) {
		Node n = new Node(color,x,y);
		nodes.add(n);
		return n;
	}

	public void initializeVotes(int n, double startx, double starty) {
		for (int i = 0; i< n; i++) {
			Node vote = addVote();
			vote.pos.x = startx + Math.random();
			vote.pos.y = starty + Math.random();
		}
	}

	public void updateCenter() {
		double xavg=0;
		double yavg=0;

		// plain old average
		for (Node n:nodes) {
			xavg +=n.pos.x;
			yavg +=n.pos.y;
		}
		xavg = xavg/nodes.size();
		yavg = yavg/nodes.size();
		center.x = xavg;
		center.y = yavg;
	}

	public void attractNodes(double attractFactor) {
		for (Node n:nodes) {
			Vector d = Vector.subtract(center, n.pos);
			if(d.magnitude()>0.5) {
				d.mult(attractFactor);
				n.pos.add(d);
			}
		}
	}

	public void setCenter(int i, int h) {
		center.x=i;
		center.y=h;
	}
}
