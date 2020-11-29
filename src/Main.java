import java.util.Scanner;
/**
 * 
 * @author Cindy Lin
 *
 */
public class Main {
	
	public static Grid grid;
	public static int d;
	public static int depth;
	
	public static void main (String[]args) {
		d = 6;
		depth = 3;
		grid = new Grid();
		GUI gui = new GUI();
		gui.setVisible(true);

		

		
		/*
		Scanner scanner = new Scanner(System.in);
		
		System.out.println("Choose dxd grid size. d must be multiple of 3:");
		d = isValidInput(scanner.next());
		while( d == -1 || d % 3 != 0 ) {
			System.out.println("Invalid d. Try again:");
			d = isValidInput(scanner.next());
		}
		
		System.out.println("Choose search depth: depth=");
		depth = isValidInput(scanner.next());
		while( depth <= 0 ) {
			System.out.println("Invalid depth. Try again:");
			depth = isValidInput(scanner.next());
		}
		
		scanner.close();
		
		grid = new Grid();
		GUI gui = new GUI();
		gui.setVisible(true);
		*/
		
	}
	
	
	public static int isValidInput(String str) {
		try {
			int i = Integer.parseInt(str);
			return i;
		}catch(NumberFormatException e) {
			return -1;
		}
	}
	
	
	public static void printGrid(Grid g) {
		for(int y = 0; y < d; y++) {
			for(int x = 0; x < d; x++) {
				if(g.getCell(x, y)==null) {
					System.out.print("O");
				}else{
					System.out.print(g.getCell(x, y).name);
				}
			}
			System.out.println();
		}
		System.out.println();
	}
}
