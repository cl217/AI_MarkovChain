
public class Pr {
	
	int x;
	int y;
	
	//AI's probability
	double pW = 0.0;
	double pH = 0.0;
	double pM = 0.0;
	double pP = 0.0;
	
	
	boolean breeze = false;
	boolean stench = false;
	boolean heat = false;
	boolean noise = false;
	
	
	
	public Pr(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
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
	
	
	public void resetPr() {
		pW = 0.0;
		pH = 0.0;
		pM = 0.0;
		pP = 0.0;
	}
	
	public String getDisplayText() {
		String txt = "W(" +  Math.floor(pW * 100) / 100;
		txt += "), H(" + Math.floor(pH * 100) / 100;
		txt += "), M("+ Math.floor(pM * 100) / 100;
		txt += "), P(" + Math.floor(pP * 100) / 100 + ")";
		return txt;
	}
	
}
