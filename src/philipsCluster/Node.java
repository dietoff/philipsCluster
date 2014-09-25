package philipsCluster;

public class Node {
	Vector pos;
	int col;
	Vector goal;
	boolean target = false;
	
	public Node() {
		col = 0xffffffff;
		pos = new Vector(0,0);
		goal = new Vector(0,0);
	}
	
	public Node(int color, int x, int y) {
		col = color;
		pos = new Vector(x,y);
		goal = new Vector(0,0);
	}
}
