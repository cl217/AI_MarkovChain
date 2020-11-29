import java.util.Collections;
import java.util.HashMap;
import java.util.PriorityQueue;
/**
 * 
 * @author Cindy Lin
 *
 */
public class MiniMax {

	Grid grid;
	Node root;
	Node worstState;
	Node bestState;
	int expand = 1;
	
	public void printH(Node n) {
		System.out.println("H: " + n.hEndState +", " + n.hNumPieces+", " + n.hAdjacent + ", " + n.hDistance);
	}
	
	public MiniMax(){
		
		grid = Main.grid;
		root = new Node(grid, null);
		
		bestState = new Node(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
		bestState.hEndState = Integer.MAX_VALUE;
		worstState = new Node(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
		worstState.hEndState = Integer.MIN_VALUE;
		
	}
	
	public int[] getNextMove() {
		
		//System.out.println("~~~~~~~~~~New minimax~~~~~~~~~~~");
		
		/*
		getChildren(1, root, 1);
		PriorityQueue<Node> nextMoveQ = new PriorityQueue<Node>(Collections.reverseOrder());
		for(Node c : root.children) {
			nextMoveQ.add(c);
		}
		*/
		
		/*
		System.out.println("~~~~~AI Next Possible Moves(" + nextMoveQ.size() +")~~~~~~~~~~~" );
		while(nextMoveQ.size() > 0) {
			Node c = nextMoveQ.poll();
			printH(c);
			Main.printGrid(c.grid);
		}
		*/
		
		/*
		Node bestMove = null;
		Node bestAlphaBeta = worstState;
		while(nextMoveQ.size() > 0) {
			Node move = nextMoveQ.poll();
			Node ab = alphabeta(move, Main.depth, worstState, bestState, true);
			if(ab.compareTo(bestAlphaBeta) > 0) {
				bestAlphaBeta = ab;
				bestMove = move;
			}
		}
		*/
		Node bestMove = alphabeta(root, Main.depth, worstState, bestState, true);
		
		
		
		if(bestMove == null) {
			//System.out.println("Null node returned: No best state found");
			return null;
		}
		
		if(bestMove.equals(worstState) || bestMove.equals(bestState)) {
			//System.out.println("Best/Worst state returned");
			return null;
		}
		
		//System.out.println("Returning best move end state: ");
		//Main.printGrid(bestMove.grid);
		
		
		//System.out.println("~~~~~~~~~~~~~~~~~~~~~~");
		

		//System.out.println("Best move found");
		//System.out.println(Arrays.toString(bestMove.move));
		//Main.printGrid(bestMove.grid);
		return bestMove.nextMove;
	}
	
	public Node alphabeta(Node node, int depth, Node alpha, Node beta, boolean maximizingPlayer) {

		if(depth==0 || node.hEndState != 0 ) {	
			return node;
		}
		
		if(maximizingPlayer) {
			Node value = worstState;
			PriorityQueue<Node> q = new PriorityQueue<Node>(Collections.reverseOrder());
			getChildren(1, node, depth);
			for(Node c : node.children) {
				q.add(c);
			}
			
			//System.out.println("AI Possible Moves: " + q.size());
			while(q.size() > 0) {
				Node child = q.poll();
				Node alphabeta = alphabeta(child, depth-1, alpha, beta, false);
				
				if( value.compareTo(alphabeta) < 0) { //if value is less alpha
					value = alphabeta;
				}
				if( alpha.compareTo(value) < 0) {
					alpha = value;
				}
				if( alpha.compareTo(beta) >= 0 ) {
					break;
				}
			}
			return value;
		}else {
			Node value = bestState;
			PriorityQueue<Node> q = new PriorityQueue<Node>();
			getChildren(0, node, depth);
			for(Node c : node.children) {
				q.add(c);
			}
			//System.out.println("Human Possible Moves: " + q.size());
			while(q.size() > 0) {
				Node child = q.poll();
				Node alphabeta = alphabeta(child, depth-1, alpha, beta, true);
				
				if(value.compareTo(alphabeta) > 0) { //if value is greater than ab
					value = alphabeta; //set value to ab
				}
				if( beta.compareTo(value) > 0 ) {
					beta = value;
				}
				if( beta.compareTo(alpha) <= 0 ) { //beta <= alpha
					break;
				}
			}
			return value;
		}
	}
	
	/*
	public void constructTree(Node node, int depth) {
		//side = 1 if depth is odd, side = 0 if depth is even
		System.out.println("depth: " + depth);
		getChildren( depth%2, node );
		if(depth == Main.d) {
			return;
		}
		for(Node child : node.children) {
			constructTree(child, depth+1);
		}
		
	}
	*/
	
	
	//add children to parent node
	public void getChildren(int side, Node parent, int depth) {
		//System.out.println(expand+", parent depth = " + parent.depth);
		//expand++;
		
		
		HashMap<Character, HashMap<Piece, String>> pieces = (side == 0)? parent.grid.side0 : parent.grid.side1;

		for(Character c : pieces.keySet()) {
			for(Piece p : pieces.get(c).keySet()) {
				
				int x1 = parent.grid.getX(p);
				int y1 = parent.grid.getY(p);
				for( int y2 = y1-1; y2 <= y1+1; y2++ ) {
					for( int x2 = x1-1; x2 <= x1+1; x2++ ) {
						if(parent.grid.isValidMove(x1, y1, x2, y2)) {
							//System.out.println("("+x1+", "+y1+") to " + "("+x2+", "+y2+")");
							Grid grid = new Grid(parent.grid);
							grid.move(x1, y1, x2, y2);
							//Main.printGrid(grid);
							Node node = new Node(grid, new int[] {x1, y1, x2, y2});
							//node.side = side;
							if(node.grid.getNumPieces(0) == 0 && node.grid.getNumPieces(1) == 0) {
								//node.hValue = Integer.MIN_VALUE+1;
								node.hEndState = -1;
							}else if(node.grid.getNumPieces(0) == 0) {
								//node.hValue = Integer.MAX_VALUE-depth;
								node.hEndState = 1;
							}else if(node.grid.getNumPieces(1) == 0) {
								//node.hValue = Integer.MIN_VALUE+depth;
								node.hEndState = -2;
							}else {
								
								node.hNumPieces = node.grid.getNumPieces(1) - node.grid.getNumPieces(0);
								node.hAdjacent = getAdjHeuristic(node.grid);
								node.hDistance = getDistanceHeuristic(node.grid);
								
							}
							node.depth = depth;
							if(parent != root) {
								node.nextMove = parent.nextMove;
							}else {
								node.nextMove = node.move;
							}
							parent.children.add(node);
						}
					}
				}
			}
		}

	}
	
	
	private int getAdjHeuristic(Grid grid) {
		int h = 0;
		for(Character c : grid.side1.keySet()) {
			for( Piece p : grid.side1.get(c).keySet()) {
				//int[] pC = grid.getCoord(p);
				
				int pX = grid.getX(p);
				int pY = grid.getY(p);
				
				
				for(int x = pX-1; x <= pX+1; x++) {
					for(int y = pY-1; y<=pY+1; y++) {
						if( x < 0 || x >= Main.d || y < 0 || y >= Main.d || grid.grid[y][x] == null || grid.grid[y][x].side == 1) {
							continue;
						}else {
							char p2c = grid.grid[y][x].name;
							if( p2c == 'P' ) {
								h += -25;
							}else if( p2c == 'W'){
								if( c == 'W' ) h += -50;
								if( c == 'H' ) h += 100;
								if( c == 'M' ) h += -100;
							}else if( p2c == 'H' ) {
								if( c == 'W' ) h += -100;
								if( c == 'H' ) h += -50;
								if( c == 'M' ) h += 100;
							}else if( p2c == 'M') {
								if( c == 'W' ) h += 100;
								if( c == 'H' ) h += -100;
								if( c == 'M' ) h += -50;
							}
						}
					}
				}
			}
		}
		return h;
	}
	
	private double getDistanceHeuristic(Grid grid) {
		double tDistance = 0;
		for(Character c : grid.side1.keySet()) {
			for( Piece p : grid.side1.get(c).keySet()) {
				//int[] pC = grid.getCoord(p);
				
				int pX = grid.getX(p);
				int pY = grid.getY(p);
				
				char loseC = 'a';
				char winC = 'a';
				switch(c) {
					case 'W': loseC = 'H'; winC = 'M'; break;
					case 'H': loseC = 'M'; winC = 'W'; break;
					case 'M': loseC = 'W'; winC = 'H'; break;
				}
				/*
				for(Piece lose : grid.side0.get(loseC).keySet()) {
					tDistance += getDiagonalDistance(pC, grid.getCoord(lose), print);
				}
				*/
				for(Piece win : grid.side0.get(winC).keySet()) {
					tDistance -= getDiagonalDistance(pX, pY, grid.getX(win), grid.getY(win));
				}
			}
		}
		return tDistance;
	}
	
	private double getDiagonalDistance(int x1, int y1, int x2, int y2) {
		
		double distance = Math.sqrt(Math.pow((x2-x1), 2) + Math.pow(y2-y1, 2));
		return distance;
	}
	
}
