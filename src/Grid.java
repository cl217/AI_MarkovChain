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
	
	List<Pr> possHuman;
	
	int nPossW;
	int nPossH;
	int nPossM;
	
	
	
	public Grid() {
		
		possPit = new HashMap<Integer, List<Pr>>();
		possHuman = new ArrayList<Pr>();
		
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

		nPossW = Main.d/3;
		nPossH = Main.d/3;
		nPossM = Main.d/3;
		
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
			possHuman.add(prHuman);
			
			
			
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
		
		//this gets called after human and ai move
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
			pr.resetPr();
		}
		

		
		
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
		
		//update AI discover pr
		if(p.side == 1) {
			
			Pr parent = getPr(p);
			double dist_pW = parent.pW;
			double dist_pH = parent.pH;
			double dist_pM = parent.pM;
			if(parent.pW != 0) {
				parent.pW = 0;
				nPossW--;
			}
			if(parent.pH != 0) {
				parent.pH = 0;
				nPossH--;
			}
			
			if(parent.pM != 0) {
				parent.pM = 0;
				nPossM--;
			}
			
			
			possHuman.remove(getPr(p));
			
			for(Pr adj : succ) {
				//narrows pr of Pit
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
				
				
				if(!pr.stench && adj.pW != 0) {
					dist_pW += adj.pW;
					adj.pW = 0.0;
					nPossW--;
				}
				if(!pr.noise && adj.pH != 0) {
					dist_pH += adj.pH;
					adj.pH = 0.0;
					nPossH--;
				}
				if(!pr.heat && adj.pM != 0) {
					dist_pM += adj.pM;
					adj.pM = 0.0;
					nPossM--;
				}
				
				if(adj.pW == 0 && adj.pH == 0 && adj.pM == 0) {
					//System.out.println("Removing (" + adj.x + "," + adj.y + ")");
					possHuman.remove(adj);
				}
				
			}
			
			//distribute, foreach pW!=0, pW += dist/sizeof(pW!=0)
			List<Pr> ones = distribute(dist_pW, dist_pH, dist_pM);
			
			
			while(!ones.isEmpty()) {
				System.out.println("ones not empty: " + ones.size());
				dist_pW = 0;
				dist_pH = 0;
				dist_pM = 0;
				for(Pr one : ones) {
					
					if(one.pW == 1 && (one.pH != 0 || one.pM != 0)) {
						dist_pH += one.pH;
						dist_pM += one.pM;
						one.pH = 0;
						one.pM = 0;
					}else if(one.pH == 1 && (one.pW != 1 || one.pM != 0)) {
						dist_pW += one.pW;
						dist_pM += one.pM;
						one.pW = 0;
						one.pM = 0;
					}else if(one.pM == 1 && (one.pH != 0 || one.pW != 0)) {
						dist_pW += one.pW;
						dist_pH += one.pH;
						one.pW = 0;
						one.pH = 0;
					}
					
				}
				ones = distribute(dist_pW, dist_pH, dist_pM); 
			}
			
			
			
		}		
	}
	
	
	
	public List<Pr> distribute(double dist_pW, double dist_pH, double dist_pM){
		List<Pr> ones = new ArrayList<Pr>();
		for(Pr prH : possHuman) {
			if(prH.pW != 0 && dist_pW != 0) {
				prH.pW += dist_pW/nPossW;
			}
			if(prH.pH != 0  && dist_pH != 0) {
				prH.pH += dist_pH/nPossH;
			}
			if(prH.pM != 0 && dist_pM != 0) {
				prH.pM += dist_pM/nPossM;
			}
			
			if(prH.pW == 1 && (prH.pH != 0 || prH.pM != 0)) {
				ones.add(prH);
			}else if(prH.pH == 1 && (prH.pW != 0 || prH.pM != 0)) {
				ones.add(prH);
			}else if(prH.pM == 1 && (prH.pH != 0 || prH.pW != 0)) {
				ones.add(prH);
			}
		}
		return ones;
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
	    
	    /*
        //checks if same side	
        if(grid[y1][x1] != null && grid[y2][x2] != null && grid[y1][x1].side == grid[y2][x2].side) {
    		return false;
        }
        */

		return true;
	        
	}

	
	public boolean move(int x1, int y1, int x2, int y2) {
        if (!isValidMove(x1, y1, x2, y2)){
            return false;
        }
        if( getCell(x1,y1) != null && getCell(x2, y2) != null && getCell(x1,y1).side == getCell(x2, y2).side) {
        	return false;
        }
        
        int side = grid[y1][x1].side;
        Pr humanDie = null;
        char humanDiePname = 'a';
        //checks current grid piece
        if(grid[y2][x2] != null) {
            //battle
            Piece p1 = grid[y1][x1];
            Piece p2 = grid[y2][x2];
            
            if(p1.name == p2.name) {
            	//both die
            	if(p1.side==0) {
            		humanDie = getPr(p1);
            		humanDiePname = p1.name;
            		side0.get(p1.name).remove(p1);
            		side1.get(p2.name).remove(p2);
            	}else {
            		side0.get(p2.name).remove(p2);
            		side1.get(p1.name).remove(p1);
            	}
				grid[y1][x1] = null; 
				grid[y2][x2] = null; 

            } else if( ( p1.name=='W' && p2.name=='M' ) || ( p1.name=='H' && p2.name=='W' ) || ( p1.name=='M' && p2.name=='H' ) ) {
                //p2 dies
            	if(p2.side==0) {
            		humanDie = getPr(p2);
            		humanDiePname = p2.name;
            		side0.get(p2.name).remove(p2);
            		side1.get(p1.name).put(p1, xyStr(x2, y2));
            	}else {
            		side1.get(p2.name).remove(p2);
            		side0.get(p1.name).put(p1, xyStr(x2, y2));
            	}
            	grid[y2][x2] = grid[y1][x1];
            	grid[y1][x1] = null;
            }else { //P1 dies
            	if(p1.side==0) {
               		humanDie = getPr(p1);
               		humanDiePname = p1.name;
            		side0.get(p1.name).remove(p1);
            	}else {
            		side1.get(p1.name).remove(p1);
            	}
            	grid[y1][x1] = null;
            }
        }else {
            //(x2, y2) empty
    		grid[y2][x2] = grid[y1][x1];
    		grid[y1][x1] = null;
    		if(grid[y2][x2].side == 0) {
    			side0.get(grid[y2][x2].name).put(grid[y2][x2], xyStr(x2, y2));
    		}else {
    			side1.get(grid[y2][x2].name).put(grid[y2][x2], xyStr(x2, y2));
    		}
        }
        
        if(humanDie != null) {
        	System.out.println("Human die");
        	//cut
        	//clear dead piece's percentages
        	deadHumanP(humanDiePname);
        }
       
		if(side == 0) {
			updateHumanP();
		}
		setSenses();
		
		
		return true;
	}
	
	
	public void deadHumanP(char name) {
		double numPLeft = (double) side0.get(name).keySet().size();
		double multiplyBy = (numPLeft == 0)? 0: numPLeft/(numPLeft+1);
		
		for(Pr pr : possHuman) {
			switch(name) {
				case 'W': 
					pr.pW *= multiplyBy;
					if(pr.pW == 0) {
						nPossW--;
					}
					break;
				case 'H': 
					System.out.println(pr.pH + " * " + multiplyBy );
					pr.pH *= multiplyBy;
					if(pr.pH == 0) {
						nPossH--;
						System.out.println("nPossH: " + nPossH);
					}
					break;
				case 'M': 

					pr.pM *= multiplyBy;
					if(pr.pM == 0) {
						nPossM--;
					}
					break;
			}
			if(pr.pW == 0 && pr.pM == 0 && pr.pH == 0) {
				possHuman.remove(pr);
			}
		}
	}
	
	
	public void updateHumanP() {
		/*
		System.out.println("========updateHumanp==============");
		
		int count = 0;
		for(Pr pr : possHuman) {
			if(pr.pW != 0) {
				count++;
			}
			if(pr.x == 1 && pr.y == 1) {
				//System.out.println("(1,1): " + pr.pW);
			}
		}
		System.out.println("   beforeExpansionPossWumpus=" + count);
		*/
		
		//HashMap<Pr, Integer> map = new HashMap<Pr, Integer>();
		ArrayList<Pr> newPossHuman = new ArrayList<Pr>();
		
		
		HashMap<Pr, double[]> originalPr = new HashMap<Pr, double[]>(); //0-pW, 1-pH, 2-pM
		for(Pr pr : possHuman) {
			double[] prArr = new double[] {pr.pW, pr.pH, pr.pM}; 
			originalPr.put(pr, prArr);
			

			pr.pW = 0;
			pr.pH = 0;
			pr.pM = 0;
			
		}
		
		
		for(Pr prH : possHuman) {
			List<Pr> succ =  succ(prH, originalPr);
			
			double[] prArr = originalPr.get(prH);
			
			/*
			if(prArr[0] != 0) {
				System.out.println("Pr(" +prH.x + "," +prH.y + "), succSize=" + succ.size() + ", Pw=" +prArr[0]);
			}
			*/
			
			//initial values
			int divBy = succ.size();
			//map.put(prH, divBy);

			for(Pr s : succ) {
				
				/*
				if(s.x == 0 && s.y == 4 ) {
					System.out.println("\n*** debug (0,4) ***");
					System.out.println("prArr[0] = " + prArr[0] + ", divBy=" + divBy);
					System.out.println("before) s.pW = " + s.pW);
				}
				*/
				
				s.pW += (double) prArr[0]/divBy;
				s.pH += (double) prArr[1]/divBy;
				s.pM += (double) prArr[2]/divBy;
				
				/*
				if(s.x == 0 && s.y == 4 ) {
					System.out.println("after) s.pW = " + s.pW);
					System.out.println("*** end debug (0,4) ***\n");
				}
				*/
				
				if(!possHuman.contains(s) && !newPossHuman.contains(s)) {
					newPossHuman.add(s);
				}
			}
		}
		
	
		
		possHuman.addAll(newPossHuman);
		nPossW = 0;
		nPossH = 0;
		nPossM = 0;
		for(Pr pr : possHuman) {
			if(pr.pW != 0) {
				nPossW++;
			}
			if(pr.pH != 0) {
				nPossH++;
			}
			if(pr.pM != 0) {
				nPossM++;
			}
		}
		
		/*
		count = 0;
		for(Pr pr : possHuman) {
			if(pr.pW != 0) {
				count++;
			}
		}
		System.out.println("   afterExpansionPossWumpus=" + count);
		for(Pr pr : possHuman) {
			if(pr.pW != 0) {
				System.out.println("           (" + pr.x + ","+pr.y + ") | pW=" +pr.pW );
			}
		}
		*/
		
	}
	
	public List<Pr> succ( Pr pr , HashMap<Pr, double[]> origPr){
		List<Pr> succ = new ArrayList<Pr>();
		for(int y = pr.y-1; y <= pr.y+1; y++) {
			for(int x = pr.x-1; x <= pr.x+1; x++) {
				if(isValidMove(pr.x, pr.y, x, y)) {
					//System.out.println("validMove: " + x + "," + y );
					double[] arr = origPr.get(prGrid[y][x]);
					if(arr == null || (arr[0] != 1 && arr[1] != 1 && arr[2] != 1 && prGrid[y][x].pP != 1)) {
						if(grid[y][x] != null && grid[y][x].side == 1) {
							continue;
						}
						succ.add(prGrid[y][x]);
					}
				}
			}
		}
		succ.add(pr);
		//System.out.println("succSize: " + succ.size());
		return succ;
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
