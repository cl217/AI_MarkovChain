import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
/**
 * 
 * @author Cindy Lin
 *
 */
public class Grid {

	Piece[][] grid;
	HashMap<Character, HashMap<Piece, String>> side0;
	HashMap<Character, HashMap<Piece, String>> side1;
	
	public Grid() {
		grid = new Piece[Main.d][Main.d];
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
		
		for( char c : side1.keySet() ) {
			for(Piece p : side1.get(c).keySet()) {
				setSenses(p);
			}
		}
		
		for( char c : side0.keySet() ) {
			for(Piece p : side0.get(c).keySet()) {
				setSenses(p);
			}
		}
	}
	
	
	
	public void setSenses(Piece p) {
		for(int y = getY(p)-1; y <= getY(p)+1; y++) {
			for(int x = getX(p)-1; x <= getX(p)+1; x++) {
				if(isValidMove(getX(p), getY(p), x, y)) {
					Piece adjP = Main.grid.getCell(x, y);
					if(adjP == null || adjP.side == p.side ) {
						continue;
					}
					switch(adjP.name) {
						case 'P': p.breeze = true; break;
						case 'W': p.stench = true; break;
						case 'H': p.noise = true; break;
						case 'M': p.heat = true; break;
					}
				}
			}
		}
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
            	return true;
            }else { //P1 dies
            	if(p1.side==0) {
            		side0.get(p1.name).remove(p1);
            	}else {
            		side1.get(p1.name).remove(p1);
            	}
            	grid[y1][x1] = null;
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
	
	/*
	public int[] getCoord(Piece p) {
		int[] coords = new int[2];
		String[] split;
		if(p.side == 0) {
			split = side0.get(p.name).get(p).split(",");
		}else {
			split = side1.get(p.name).get(p).split(",");
		}
		coords[0] = Integer.parseInt(split[0]);
		coords[1] = Integer.parseInt(split[1]);
		return coords;
	}
	*/
	
	public int getX(Piece p) {
		String coords;
		if(p.side == 0) {
			coords = side0.get(p.name).get(p);
		}else {
			coords = side1.get(p.name).get(p);
		}
		
		return Integer.parseInt(coords.substring(0, coords.indexOf(',')));
	}
	
	public int getY(Piece p) {
		String coords;
		if(p.side == 0) {
			coords = side0.get(p.name).get(p);
		}else {
			coords = side1.get(p.name).get(p);
		}
		
		return Integer.parseInt(coords.substring(coords.indexOf(',')+1, coords.length()));
	}
	
	
}
