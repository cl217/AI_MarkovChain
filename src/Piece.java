import java.util.HashMap;

/**
 * 
 * @author Cindy Lin
 *
 */
public class Piece {
	
	public char name; //W, H, M, P
	public int side; //0 (Human), 1 (AI), -1 (PIT)
	
	//AI's probability
	/*
	double pW = 0.0;
	double pH = 0.0;
	double pM = 0.0;
	double pP = 0.0;
	
	
	boolean breeze = false;
	boolean stench = false;
	boolean heat = false;
	boolean noise = false;
	*/
	
	
	public Piece(char name, int side) {
		this.name = name;
		this.side = side;
	}
	
	/*
	public void updateCoordinates(int x, int y) {
		this.x = x;
		this.y = y;
	}
	*/
	
	
	
	public String getDisplayText() {
		if(name == 'P') {
			return "P";
		}
		return Character.toString(name) + Integer.toString(side);
	}

	/*
	public boolean isEmpty() {
		if(name == '0') {
			return true;
		}else {
			return false;
		}
	}
	
	public Piece getEmpty() {
		return new Piece('0', -1, 0, 0);
	}
	
	public void setEmpty() {
		if(Main.grid.side0.get(name) != null ) {
			Main.grid.side0.get(name).remove(this);
		}
		if(Main.grid.side1.get(name) != null ) {
			Main.grid.side1.get(name).remove(this);
		}
		name = '0';
		side = -1;
	}
	*/
	/*
	public String getImage() {
		if(!breeze && !stench && !noise && !heat) {
			return "icons/Empty.bmp";
		}
		String name = "icons/";
		if(breeze) {
			name += "B";
		}
		if(stench) {
			name += "S";
		}
		if(noise) {
			name += "M";
		}
		if(heat) {
			name += "F";
		}
		name += ".bmp";
		return name;
	}
	*/
}
