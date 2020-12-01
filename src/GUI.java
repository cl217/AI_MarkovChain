import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

import javax.imageio.ImageIO;
import javax.swing.*;
/**
 * 
 * @author Cindy Lin
 *
 */
public class GUI extends javax.swing.JFrame  {

	JFrame jframe;
	JPanel topPanel;
	JPanel bottomPanel;
    JScrollPane scrollPane;
    JLabel label;
    
    int[] move1;
    int[] aiMove;
    
    Color AI = new Color(153,0,0);
    Color human = new Color(0,0,153);
    Color AImove = new Color(255, 153, 0);
    Color humanMove = new Color(51, 204, 255);
    
    HashMap<String, ImageIcon> iconMap;
    String[] iconName = new String[]{ "B", "BF", "BM", "BMF", "BS", "BSF", "BSM", "BSMF", "Empty", "F", "M", "MF", "S", "SF", "SM", "SMF" };
    
    boolean fog = true;
    
    
    ArrayList<JButton> buttonList;
	public GUI(){
		
		move1 = new int[] {-1, -1, -1, -1};
		buttonList = new ArrayList<JButton>();
		
		iconMap = new HashMap<String, ImageIcon>();
		for(String icon : iconName) {
			try {
				iconMap.put(icon, new ImageIcon(ImageIO.read(getClass().getResource("icons/"+icon+".bmp"))));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		
    	jframe = this;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	JSplitPane splitPane = new JSplitPane();
        topPanel = new JPanel();
		bottomPanel = new JPanel(new GridBagLayout());
		newGrid(Main.d);
		topPanel();
		bottomPanel();
        setPreferredSize(new Dimension(1200, 1000)); 
        getContentPane().setLayout(new GridLayout());  
        getContentPane().add(splitPane);             
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT); 
        splitPane.setDividerLocation(800);                   
        splitPane.setTopComponent(scrollPane);                  
        splitPane.setBottomComponent(bottomPanel);           
        pack();  
	}
	
	
	
	private String getImageKey(Piece p) {
		if(p ==  null) {
			return "Empty";
		}
		Pr pr = Main.grid.getPr(p);
		if( !pr.breeze && !pr.stench && !pr.noise && !pr.heat) {
			return "Empty";
		}
		String name = "";
		if(pr.breeze) {
			name += "B";
		}
		if(pr.stench) {
			name += "S";
		}
		if(pr.noise) {
			name += "M";
		}
		if(pr.heat) {
			name += "F";
		}
		return name;
	}
	
	private void updateButtonDisplay() {
		
		
		for(int y = 0; y < Main.d; y++) {
			for(int x = 0; x < Main.d; x++) {
				
				JButton button = buttonList.get(y*Main.d+x);
				Piece p = Main.grid.getCell(x, y);
				//update icon
				if(button.getIcon() == null || !button.getIcon().equals(iconMap.get(getImageKey(p)))) {
					if(p == null ) {
						button.setIcon(iconMap.get("Empty"));
					}else if(p.name != 'P'){
						if(fog && p.side == 1) {
							button.setIcon(iconMap.get("Empty"));
						}else {
							button.setIcon(iconMap.get(getImageKey(p)));
						}
					}
				}		
				
				//update text
				
				String txt = "";
				if( Main.grid.getCell(x, y)!=null) {
					if( !fog || (fog && Main.grid.getCell(x, y).side == 0) ) {
						txt = Main.grid.getCell(x, y).getDisplayText();
					}
				}
				txt += "\n" + Main.grid.prGrid[y][x].getDisplayText();
				
				button.setText("<html><u>" + txt.replaceAll("\\n", "</u><br>") + "</html>");
				
				if(p == null || p.name == 'P') {
					button.setForeground(Color.black);
				}else if(p.side == 1) {
					button.setForeground(AI);
				}else if(p.side == 0) {
					button.setForeground(human);
				}
				
			}
		}
		
		
		
	}
	
	
	
	private void newGrid(int d) {
		int bSize = 120;
    	topPanel.setLayout(new GridLayout(Main.d, Main.d));
    	for(int y = 0; y < Main.d; y++) {
    		for(int x = 0; x < Main.d; x++) {
    			JButton button = new JButton();
    			button.setVerticalTextPosition(SwingConstants.BOTTOM);
    			button.setHorizontalTextPosition(SwingConstants.CENTER);
    			String text = "";
    			if( Main.grid.getCell(x, y) != null) {
					//text = Main.grid.getCell(x, y).getDisplayText();
    				//text = display(x,y);
					if( Main.grid.getCell(x, y).name == 'P') {
						button.setBackground(new Color(153, 102, 0));
					}else {
						button.setBackground(Color.LIGHT_GRAY);
						if(Main.grid.getCell(x, y).side == 0) {
							button.setForeground(human);
						}else {
							button.setForeground(AI);
						}
					}
    			}else{
    				button.setBackground(Color.LIGHT_GRAY);
    				//text = display(x,y);
    			}
    			
	            //System.out.println(text);
				//button.setText("<html><u>" + text.replaceAll("\\n", "</u><br>") + "</html>");
	            button.setFont(new Font("Arial", Font.BOLD, 15));
	            button.setMargin(new Insets(0, 0, 0, 0));
	            
	            
	            /** Start button listener **/
	            button.addActionListener(new ActionListener() {
	            	@Override
	                public void actionPerformed(ActionEvent e) {	         
	            		
	            		int index = buttonList.indexOf(button);
	            		int y = index/d;
	            		int x = index%d;
	            		
	            		/*
	            		for( char c : Main.grid.side0.keySet() ) {
	            			for(Piece p : Main.grid.side0.get(c).keySet()) {
	            				System.out.print(p.name);
	            			}
	            		}
	            		System.out.println("\n==================");
	            		*/
	            		
	            		Piece pTest = Main.grid.getCell(x, y);
	            		Pr prTest = Main.grid.prGrid[y][x];
	            		//System.out.println(pTest.name + " (" + pTest.x+","+pTest.y+")");
	            		
	            		/*
	            		if(Main.grid.getCell(x, y) == null) {
	            			System.out.println("null");
	            		}else {
	            			System.out.print(Main.grid.getCell(x, y).name+": "+ Main.grid.getX(pTest)+","+Main.grid.getY(pTest));
	            			if(Main.grid.getCell(x, y).isEmpty()) {
	            				System.out.println(" (empty)");
	            			}else {
	            				System.out.println();
	            			}
	            		}
	            		*/
	            		
	            		/*
	            		System.out.print("pW(" +  Math.floor(prTest.pW * 100) / 100 );
	            		System.out.print("), pH(" + Math.floor(prTest.pH * 100) / 100 );
	            		System.out.print("), pM("+ Math.floor(prTest.pM * 100) / 100 );
	            		System.out.print("), pP(" + Math.floor(prTest.pP * 100) / 100);
	            		System.out.println(")");
	            		*/
	            		
	            		
	            		
	            		if( move1[0] == x && move1[1] == y) { //reset past pick
	            			buttonList.get(y*Main.d+x).setBackground(Color.LIGHT_GRAY);
	            			move1[0] = -1;
	            			move1[1] = -1;
	            			return;
	            		}
	            		
	            		
	            		if(move1[0] == -1) { //initial pick piece
		            		//check if human piece
		            		if(Main.grid.getCell(x, y)==null|| Main.grid.getCell(x, y).side != 0) {
	            				if(Main.grid.getNumPieces(0) == 0 && Main.grid.getNumPieces(1) == 0) {
	            					label.setText("Tied.");
	            				}else if(Main.grid.getNumPieces(0) == 0) {
	            					label.setText("You lost. AI won.");
	            				}else if(Main.grid.getNumPieces(1)==0){
	            					label.setText("You won. AI lost.");
	            				}else {
	            					label.setText("Invalid piece to move.");
	            				}
	            				//return;
		            		}
		            		label.setText("");
	            			move1[0] = x;
	            			move1[1] = y;
	            			buttonList.get(move1[1]*Main.d + move1[0]).setBackground(Color.YELLOW);
	            		}else {
	            			//attempting move
	            			boolean success = Main.grid.move(move1[0], move1[1], x, y);
	            			if(!success) {
	            				label.setText("Invalid move.");
	            			}else { //Human moved
		            			move1[2] = x;
		            			move1[3] = y;
	             				if(aiMove != null) { //Reset previous AI Move coloring
		            				buttonList.get(aiMove[1]*Main.d + aiMove[0]).setBackground(Color.LIGHT_GRAY);
		            				buttonList.get(aiMove[3]*Main.d + aiMove[2]).setBackground(Color.LIGHT_GRAY);
	            				}
	            				
	            				
	            				label.setText("");
	            				JButton bHuman1 = buttonList.get(move1[1]*Main.d + move1[0]);
	            				JButton bHuman2 = buttonList.get(move1[3]*Main.d + move1[2]);
	            				Piece p1 = Main.grid.getCell(move1[0], move1[1]);
	            				Piece p2 = Main.grid.getCell(move1[2], move1[3]);
	            				
	            				
	            				//SET Display of bHuman1
	            				//bHuman1.setText("");
	            				//bHuman1.setText("<html><u>" + display(move1[0], move1[1]).replaceAll("\\n", "</u><br>") + "</html>");
	            				//bHuman1.setForeground(Color.BLACK);
	            				bHuman1.setBackground(Color.LIGHT_GRAY);
								
								//Update bHuman2 display
	            				if( p2 != null) { 
	            					if(p2.side == 0) {
	                					//bHuman2.setText("<html><u>" + display(move1[2], move1[3]).replaceAll("\\n", "</u><br>") + "</html>");
	            						//bHuman2.setForeground(human);
	            					}else if(p2.side==1){
	            						if(fog) {
	            							bHuman2.setText("");
	            						}else {
		            						//bHuman2.setForeground(AI);
	                    					//bHuman2.setText(p2.getDisplayText());
	            						}
	            					}
	            				}else { 
	            					bHuman2.setText("");
	            				}
	            				updateButtonDisplay();
	            				
	            				move1[0] = -1;
	            				
	            				if(Main.grid.side0.size() > 0 && Main.grid.side1.size() > 0 ) {
		            				//Make AI move here
	            					/*
	            					System.out.println("AI is making move..");
		            				MiniMax m = new MiniMax();
		            				aiMove = m.getNextMove();
		            				if(aiMove != null) {
			            				Main.grid.move(aiMove[0], aiMove[1], aiMove[2], aiMove[3]); //ai moved
			            				
			            				JButton bA1 = buttonList.get(aiMove[1]*Main.d + aiMove[0]);
			            				JButton bA2 = buttonList.get(aiMove[3]*Main.d + aiMove[2]);
			            				Piece pA1 = Main.grid.getCell(aiMove[0], aiMove[1]);
			            				Piece pA2 = Main.grid.getCell(aiMove[2], aiMove[3]);
			            				
				            			//Update bA1 display
			            				if(!fog) {
			            					bA1.setText("");
			            					bA1.setBackground(AImove);
					            			bA2.setBackground(AImove);
			            				}
				            				
				            			//Update bA2 display
				            			if( pA2 != null) {
					            			if(pA2.side == 0) {
					            				bA2.setText(pA2.getDisplayText());
					            				bA2.setForeground(human);
					            			}else if(pA2.side==1){
			            						if(fog) {
			            							bA2.setText("");
			            						}else {
				            						bA2.setForeground(AI);
			                    					bA2.setText(pA2.getDisplayText());
			            						}
					            			}
				            			}else {
				            				bA2.setText("");
				            			}
				            			updateSensesDisplay();
				            				
			            				System.out.println("AI has moved.");

		            				}else {
		            					System.out.println("Ai could not make a move.");
		            				}
		            				*/
	            				}
	            			}
            				if(Main.grid.getNumPieces(0) == 0 && Main.grid.getNumPieces(1) == 0) {
            					label.setText("Tied.");
            				}else if(Main.grid.getNumPieces(0) == 0) {
            					label.setText("You lost. AI won.");
            				}else if(Main.grid.getNumPieces(1)==0){
            					label.setText("You won. AI lost.");
            				}
	            		}
	            		
	            	}
	            });
	            /** End button listener **/

	            buttonList.add(button);
        		button.setPreferredSize(new Dimension(bSize,bSize));
                topPanel.add(button);
    		} //end x loop
    	} //end y loop
		updateButtonDisplay();
	}
	
    private void topPanel() {
        JPanel container = new JPanel(new FlowLayout(FlowLayout.CENTER, 0,0));
        container.add(topPanel);
        scrollPane = new JScrollPane(container);
    }
    
    
    private void bottomPanel() {
		//Component horizontalStrut = Box.createHorizontalStrut(100);
		//bottomPanel.add(horizontalStrut);
		
		Box verticalBox = Box.createVerticalBox();
		bottomPanel.add(verticalBox);
    	
    	
		JCheckBox fogCheckBox = new JCheckBox("Fog of War");
		verticalBox.add(fogCheckBox);
		fogCheckBox.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent event){
				if(fogCheckBox.isSelected()) {
					fog = true;
			    	for(int y = 0; y < Main.d; y++) {
			    		for(int x = 0; x < Main.d; x++) {
			    			JButton button = buttonList.get(y*Main.d + x);
			    			//String text = display(x, y);
			    			if( Main.grid.getCell(x, y) != null ) {
								if(Main.grid.getCell(x, y).side == 0) {
									//button.setForeground(human);
								}else {
									if(Main.grid.getCell(x, y).name != 'P') {
										button.setIcon(iconMap.get("Empty"));
									}
									button.setBackground(Color.LIGHT_GRAY);
								}
			    			}else{
			    				button.setBackground(Color.LIGHT_GRAY);
			    			}
			    			//button.setText("<html><u>" + text.replaceAll("\\n", "</u><br>") + "</html>");
			    		}
			    	}
					
					
					
				}else {
					fog = false;
			    	for(int y = 0; y < Main.d; y++) {
			    		for(int x = 0; x < Main.d; x++) {
			    			JButton button = buttonList.get(y*Main.d + x);
			    			String text = "";
			    			if(Main.grid.getCell(x, y)!= null) {
								//text = display(x,y);
								if( Main.grid.getCell(x, y).name == 'P') {
									
									button.setBackground(new Color(153, 102, 0));
								}else {
									button.setBackground(Color.LIGHT_GRAY);
									if(Main.grid.getCell(x, y).side == 0) {
										button.setForeground(human);
									}else {
										button.setForeground(AI);
									}
								}
			    			}else{
			    				button.setBackground(Color.LIGHT_GRAY);
			    				//text = display(x,y);
			    			}
			    			button.setText("<html><u>" + text.replaceAll("\\n", "<br>") + "</u></html>");
			    		}
			    	}
			    	updateButtonDisplay();
		        }
			}
		});		
		fogCheckBox.setSelected(true);

		
		//bottomPanel.add(fogCheckBox);
    	
    	
		label = new JLabel("");
		label.setFont(new Font("Tahoma", Font.PLAIN, 30));
		verticalBox.add(label);
		//bottomPanel.add(label);
		
		
	
		
		//Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		//bottomPanel.add(horizontalStrut_1);
    }
}
