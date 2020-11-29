import java.util.ArrayList;
/**
 * 
 * @author Cindy Lin
 *
 */
public class Node implements Comparable<Node> {
	
	int hEndState = 0;
	int hNumPieces;
	int hAdjacent;
	double hDistance;
	
	int depth =0;
	boolean visited = false;
	
	//int side;
	int[] move;
	int[] nextMove;
	Grid grid;
	
	
	ArrayList<Node> children;
	
	public Node(Grid grid, int[] move){
		this.grid = grid;
		//children = new PriorityQueue<Node>(5, Comparator.comparing(Node::hValue));	//lowest first
		children = new ArrayList<Node>();
		this.move = move;
	}
	
	
	public Node( int hNumPieces, int hAjacent, int hDistance ) {
		this.hNumPieces = hNumPieces;
		this.hAdjacent = hAjacent;
		this.hDistance = hDistance;
	}
	
	/*
	public double hValue() {
		return hValue;
	}
	*/

	@Override
	public int compareTo(Node o) {
		
		if(this.hEndState > o.hEndState) {
			return 1;
		}else if( this.hEndState < o.hEndState ) {
			return -1;
		}
		
		if(this.hNumPieces > o.hNumPieces) {
			return 1;
		}else if(this.hNumPieces < o.hNumPieces) {
			return -1;
		}
		
		if(this.hAdjacent > o.hAdjacent) {
			return 1;
		}else if(this.hAdjacent < o.hAdjacent) {
			return -1;
		}
		
		if(this.hDistance > o.hNumPieces) {
			return 1;
		}else if(this.hDistance < o.hNumPieces) {
			return -1;
		}
		
		return 0;
	}
	
	
}
