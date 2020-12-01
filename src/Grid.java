import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
/**
 * 
 * @author Cindy Lin
 *
 */
public class Grid {

	Piece[][] grid;
	Pr[][] prGrid;
	HashMap<Character, HashMap<Piece, String>> side0;
	HashMap<Character, HashMap<Piece, String>> side1;
	HashMap<Character, HashMap<Piece, String>> empty;
	
	HashMap<Integer, List<Pr>> possPit;
	
	
	
	
	public Grid() {
		
		possPit = new HashMap<Integer, List<Pr>>();
		
		grid = new Piece[Main.d][Main.d];
		prGrid = new Pr[Main.d][Main.d];
		
		
		side0 = new HashMap<Character, HashMap<Piece, String>>();
		side1 = new HashMap<Character, HashMap<Piece, String>>();
		char[] c = {'W', 'H', 'M'};
		for(int i = 0; i < c.length; i++) {
			side0.put(c[i], new HashMap<Piece, String>());
			side1.put(c[i], new HashMap<Piece, String>());
		}
		
		initializeGrid();
		
		
	}
	
	public Grid(Grid copyThis) { //creates copy of a grid
		side0 = new HashMap<Character, HashMap<Piece, String>>();
		for(char c : copyThis.side0.keySet()) {
			side0.put(c, new HashMap<Piece, String>());
			for(Piece p : copyThis.side0.get(c).keySet()) {
				side0.get(c).put(p, copyThis.side0.get(c).get(p));
			}
		}
		
		side1 = new HashMap<Character, HashMap<Piece, String>>();
		for(char c : copyThis.side1.keySet()) {
			side1.put(c, new HashMap<Piece, String>());
			for(Piece p : copyThis.side1.get(c).keySet()) {
				side1.get(c).put(p, copyThis.side1.get(c).get(p));
			}
		}
		grid = new Piece[Main.d][Main.d];
		grid = Arrays.stream(copyThis.grid).map(Piece[]::clone).toArray(Piece[][]::new);
	}
	

	public String xyStr(int x, int y) {
		return Integer.toString(x) + "," + Integer.toString(y);
	}
	
	private void initializeGrid() {

		
		//put (d/3)-1 pits in random cells in each row
		Random rand = new Random();
		int pitcount = 0;
		for(int y = 1; y < Main.d-1; y++) {
			while (pitcount < ((Main.d/3)-1)) {
				int x = rand.nextInt(Main.d-1); //d columns 
				grid[y][x]= new Piece('P', -1); 
				pitcount++;
			}
			pitcount = 0;
		}
		
		
		//intial possibilites for pits
		for(int y = 1; y < Main.d-1; y++) {
			for(int x = 0; x < Main.d; x++) {
				
				prGrid[y][x] = new Pr(x, y);
				prGrid[y][x].pP = (double) 1/Main.d;

				if(!possPit.containsKey(y)) {
					possPit.put(y, new ArrayList<Pr>());
				}
				possPit.get(y).add(prGrid[y][x]);
				
			}
		}
		
		

		//put alternating wumpus, hero, mage in bottom and top row
		int alternate = 1;
		for(int i = 0; i < Main.d; i++) {
			char c = 'O';
			switch(alternate) {
				case 1: c = 'W'; break;
				case 2: c = 'H'; break;
				case 3: c = 'M'; break;
			}
			
			Piece ai = new Piece(c,1);
			Piece human = new Piece(c, 0);
			
			Pr prAI = new Pr(i, 0);
			Pr prHuman = new Pr(i, Main.d-1);
			
			//set initial probabilites for human piece location
			switch(alternate) {
				case 1: prHuman.pW = 1; break;
				case 2: prHuman.pH = 1; break;
				case 3: prHuman.pM = 1; break;
			}
			prGrid[0][i] = prAI; //AI location initial pr is all 0
			prGrid[Main.d-1][i] = prHuman;
			
			
			grid[0][i] = ai;
			grid[Main.d-1][i] = human;
			
			side1.get(c).put(ai, xyStr(i, 0));
			side0.get(c).put(human, xyStr(i, Main.d-1));
			
			if(alternate == 3) {
				alternate = 1;
			}else {
				alternate++;
			}
		}
		
		Main.grid = this;
		setSenses();
		
	}
	

	
	public void setSenses() {
		for( char c : side1.keySet() ) {
			for(Piece p : side1.get(c).keySet()) {
				setSensesP(p);
			}
		}
		
		
		for( char c : side0.keySet() ) {
			for(Piece p : side0.get(c).keySet()) {
				//System.out.println("setSenses: " + p.name);
				setSensesP(p);
			}
		}
		
		
		
	}
	
	public void setSensesP(Piece p) {
		Pr pr = getPr(p);
		pr.breeze = false;
		pr.stench = false;
		pr.noise = false;
		pr.heat = false;
		
		//distribute pit pr
		if(p.side == 1 && pr.pP != 0) {
			possPit.get(pr.y).remove(pr);
			for(Pr otherPr : possPit.get(pr.y)) {
				otherPr.pP += (double) pr.pP/possPit.get(pr.y).size();
			}
		}
		
		pr.resetPr();
		
		
		List<Pr> succ = succ(p);
		
		for(Pr adj : succ ) {
			Piece adjP = getCell(adj.x, adj.y);
			if(adjP == null || adjP.side == p.side ) {
				continue;
			}
			switch(adjP.name) {
				case 'P': pr.breeze = true; break;
				case 'W': pr.stench = true; break;
				case 'H': pr.noise = true; break;
				case 'M': pr.heat = true; break;
			}
		}
		//update probability
		if(p.side == 1) {
			for(Pr adj : succ) {
				//elimates prob
				if(!pr.breeze) {
					List<Pr> possPitY = possPit.get(adj.y);
					if(possPitY==null) {
						continue;
					}
					possPitY.remove(adj);
					//distribute prob to every other pit in the same row
					for(Pr otherPr : possPitY) {
						otherPr.pP += (double) adj.pP/possPitY.size();
					}
					adj.pP = 0.0;
				}
				if(!pr.stench) {
					adj.pW = 0.0;
				}
				if(!pr.noise) {
					adj.pH = 0.0;
				}
				if(!pr.heat) {
					adj.pM = 0.0;
				}
			}
		}
	}
	
	
	public List<Pr> succ( Piece p ){
		List<Pr> succ = new ArrayList<Pr>();
		for(int y = getY(p)-1; y <= getY(p)+1; y++) {
			for(int x = getX(p)-1; x <= getX(p)+1; x++) {
				if(isValidMove(getX(p), getY(p), x, y)) {
					succ.add(prGrid[y][x]);
				}
			}
		}
		return succ;
	}

	public boolean isValidMove(int x1, int y1, int x2, int y2){
		//checks bounds
		if (x2 < 0) return false;
		if (y2 < 0) return false;
		if (x2 >= Main.d) return false;
		if (y2 >= Main.d) return false;
		
        //checks if same cell
        if(x1 == x2 && y1 == y2) {
        	return false;
        }
	        
        //checks if its an adjacent cell        
	    if (Math.abs(x2-x1) > 1) return false;
	    if (Math.abs(y2-y1) > 1) return false;
	    
        //checks if same side	
        if(grid[y2][x2] != null && grid[y1][x1].side == grid[y2][x2].side) {
    		return false;
        }

		return true;
	        
	}

	public boolean move(int x1, int y1, int x2, int y2) {
        if (!isValidMove(x1, y1, x2, y2)){
            return false;
        }
        
        //checks current grid piece
        if(grid[y2][x2] != null) {
            //battle
            Piece p1 = grid[y1][x1];
            Piece p2 = grid[y2][x2];
            
            if(p1.name == p2.name) {
            	//both die
            	if(p1.side==0) {
            		side0.get(p1.name).remove(p1);
            		side1.get(p2.name).remove(p2);
            	}else {
            		side0.get(p2.name).remove(p2);
            		side1.get(p1.name).remove(p1);
            	}
				grid[y1][x1] = null; 
				grid[y2][x2] = null; 
				setSenses();
				return true;
            }
            
            //p2 dies
            if( ( p1.name=='W' && p2.name=='M' ) || ( p1.name=='H' && p2.name=='W' ) || ( p1.name=='M' && p2.name=='H' ) ) {
            	if(p2.side==0) {
            		side0.get(p2.name).remove(p2);
            		side1.get(p1.name).put(p1, xyStr(x2, y2));
            	}else {
            		side1.get(p2.name).remove(p2);
            		side0.get(p1.name).put(p1, xyStr(x2, y2));
            	}
            	grid[y2][x2] = grid[y1][x1];
            	grid[y1][x1] = null;
            	setSenses();
            	return true;
            }else { //P1 dies
            	if(p1.side==0) {
            		side0.get(p1.name).remove(p1);
            	}else {
            		side1.get(p1.name).remove(p1);
            	}
            	grid[y1][x1] = null;
            	setSenses();
            	return true;
            }
        }
       
        //(x2, y2) empty
		grid[y2][x2] = grid[y1][x1];
		grid[y1][x1] = null;
		if(grid[y2][x2].side == 0) {
			side0.get(grid[y2][x2].name).put(grid[y2][x2], xyStr(x2, y2));
		}else {
			side1.get(grid[y2][x2].name).put(grid[y2][x2], xyStr(x2, y2));
		}
		setSenses();
		return true;
	}
	
	public Piece getCell(int x, int y) {
		return grid[y][x];
	}
	
	public int getNumPieces(int side) {
		HashMap<Character, HashMap<Piece, String>> map = (side == 0)? side0 : side1;
		int size = 0;
		for(Character c : map.keySet()) {
			size += map.get(c).size();
		}
		return size;
	}
	
	public int getX(Piece p) {
		String coords;
		if(p.side == 0) {
			coords = side0.get(p.name).get(p);
		}else if (p.side == 1){
			coords = side1.get(p.name).get(p);
		}else {
			return -1;
		}
		
		return Integer.parseInt(coords.substring(0, coords.indexOf(',')));
	}
	
	public int getY(Piece p) {
		String coords;
		if(p.side == 0) {
			coords = side0.get(p.name).get(p);
		}else if (p.side == 1) {
			coords = side1.get(p.name).get(p);
		}else {
			return -1;
		}
		
		return Integer.parseInt(coords.substring(coords.indexOf(',')+1, coords.length()));
	}
	
	public void swap(Piece p1, Piece p2) {
		
		char tname = p1.name;
		int tside = p1.side;
		
		p1.name = p2.name;
		p1.side = p2.side;
		
		p2.name = tname;
		p2.side = tside;
		
	}
	
	public Pr getPr(Piece p) {
		return prGrid[getY(p)][getX(p)];
	}
	
	public void printSides() {
		for( char c : side0.keySet() ) {
			for(Piece p : side0.get(c).keySet()) {
				System.out.print(p.getDisplayText());
			}
		}
		System.out.println();
		for( char c : side1.keySet() ) {
			for(Piece p : side1.get(c).keySet()) {
				System.out.print(p.getDisplayText());
			}
		}
		System.out.println();
		//System.out.println("\n====");
	}
	
}
