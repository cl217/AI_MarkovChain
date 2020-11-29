import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;

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
    
    ArrayList<JButton> buttonList;
	public GUI(){
		
		move1 = new int[] {-1, -1};
		buttonList = new ArrayList<JButton>();
		
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
	
	
	private String getImage(Piece p) {
		if(!p.breeze && !p.stench && !p.noise && !p.heat) {
			return "Empty.bmp";
		}
		String name = "";
		if(p.breeze) {
			name += "B";
		}
		if(p.stench) {
			name += "S";
		}
		if(p.noise) {
			name += "M";
		}
		if(p.heat) {
			name += "F";
		}
		name += ".bmp";
		return name;
	}
	
	
	
	private void newGrid(int d) {
		int bSize = 100;
    	topPanel.setLayout(new GridLayout(Main.d, Main.d));
    	for(int y = 0; y < Main.d; y++) {
    		for(int x = 0; x < Main.d; x++) {
    			JButton button = new JButton();
    			button.setVerticalTextPosition(SwingConstants.BOTTOM);
    			button.setHorizontalTextPosition(SwingConstants.CENTER);
    			String text = "";
    			if(Main.grid.getCell(x, y) != null) {
					text = Main.grid.getCell(x, y).getDisplayText();
					if( Main.grid.getCell(x, y).name == 'P') {
						button.setBackground(new Color(153, 102, 0));
					}else {
						button.setBackground(Color.LIGHT_GRAY);
						try {
							String imgFile = getImage(Main.grid.getCell(x, y));
							Image img = ImageIO.read(getClass().getResource(imgFile));
							button.setIcon(new ImageIcon(img));
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						
						
						if(Main.grid.getCell(x, y).side == 0) {
							button.setForeground(human);
						}else {
							button.setForeground(AI);
						}
					}
    			}else{
    				button.setBackground(Color.LIGHT_GRAY);
    			}
	            
				button.setText(text);
	            button.setFont(new Font("Arial", Font.BOLD, 15));
	            button.setMargin(new Insets(0, 0, 0, 0));
	            button.addActionListener(new ActionListener() {
	            	@Override
	                public void actionPerformed(ActionEvent e) {	         

	            		int index = buttonList.indexOf(button);
	            		int y = index/d;
	            		int x = index%d;
	            		
	            		if( move1[0] == x && move1[1] == y) {
	            			buttonList.get(y*Main.d+x).setBackground(Color.LIGHT_GRAY);
	            			move1[0] = -1;
	            			move1[1] = -1;
	            			return;
	            		}
	            		
	            		
	            		if(move1[0] == -1) {
		            		//check if human piece
		            		if(Main.grid.getCell(x, y) == null || Main.grid.getCell(x, y).side != 0) {
	            				if(Main.grid.getNumPieces(0) == 0 && Main.grid.getNumPieces(1) == 0) {
	            					label.setText("Tied.");
	            				}else if(Main.grid.getNumPieces(0) == 0) {
	            					label.setText("You lost. AI won.");
	            				}else if(Main.grid.getNumPieces(1)==0){
	            					label.setText("You won. AI lost.");
	            				}else {
	            					label.setText("Invalid piece to move.");
	            				}
		            			return;
		            		}
		            		label.setText("");
	            			move1[0] = x;
	            			move1[1] = y;
	            			buttonList.get(move1[1]*Main.d + move1[0]).setBackground(Color.YELLOW);
	            		}else {
	            			boolean success = Main.grid.move(move1[0], move1[1], x, y);
	            			//System.out.println(Main.grid.getCell(move1[0], move1[1]).getDisplayText()+"("+move1[0]+", " + move1[1] +") -> ("+x+", " +y+ ")" );
	            			if(!success) {
	            				//System.out.println("Invalid move");
	 
	            				label.setText("Invalid move.");
	            			}else { //update GUI
	            				label.setText("");
	            				if(aiMove != null) {
		            				buttonList.get(aiMove[1]*Main.d + aiMove[0]).setBackground(Color.LIGHT_GRAY);
		            				buttonList.get(aiMove[3]*Main.d + aiMove[2]).setBackground(Color.LIGHT_GRAY);
	            				}
	            				
	            				buttonList.get(move1[1]*Main.d + move1[0]).setText("");
	            				buttonList.get(move1[1]*Main.d + move1[0]).setBackground(Color.LIGHT_GRAY);
	            				Piece moveP = Main.grid.getCell(x, y);
	            				if(moveP != null) {
	            					JButton moveB = buttonList.get(y*Main.d + x);
	            					moveB.setText(moveP.getDisplayText());
	            					if(moveP.side == 0) {
	            						moveB.setForeground(human);
	            					}else if(moveP.side==1){
	            						moveB.setForeground(AI);
	            					}
	            				}else {
	            					buttonList.get(y*Main.d + x).setText("");
	            				}
	            				//System.out.println("Successful move");
	            				//Main.printGrid(Main.grid);
	            				move1[0] = -1;
	            				
	            				if(Main.grid.side0.size() > 0 && Main.grid.side1.size() > 0 ) {
		            				//Make AI move here
	            					//label.setText("AI is making a move.");
	            					System.out.println("AI is making move..");
		            				MiniMax m = new MiniMax();
		            				aiMove = m.getNextMove();
		            				if(aiMove != null) {
			            				
			            				Main.grid.move(aiMove[0], aiMove[1], aiMove[2], aiMove[3]);
			            				
			            				//Update GUI
			            				buttonList.get(aiMove[1]*Main.d + aiMove[0]).setText("");
			            				buttonList.get(aiMove[1]*Main.d + aiMove[0]).setBackground(AImove);
			            				buttonList.get(aiMove[3]*Main.d + aiMove[2]).setBackground(AImove);
			            				if(Main.grid.getCell(aiMove[2], aiMove[3]) != null) {
			            					JButton moveB =buttonList.get(aiMove[3]*Main.d + aiMove[2]);
			            					moveB.setText(Main.grid.getCell(aiMove[2], aiMove[3]).getDisplayText());
			            					if(Main.grid.getCell(aiMove[2], aiMove[3]).side == 0) {
			            						moveB.setForeground(human);
			            					}else if(Main.grid.getCell(aiMove[2], aiMove[3]).side==1){
			            						moveB.setForeground(AI);
			            					}
			            				}else {
			            					buttonList.get(aiMove[3]*Main.d + aiMove[2]).setText("");
			            				}
			            				System.out.println("AI has moved.");

		            				}else {
		            					System.out.println("Ai could not make a move.");
		            				}
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
	            buttonList.add(button);
        		button.setPreferredSize(new Dimension(bSize,bSize));
                topPanel.add(button);
    		}
    	}
    	
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
					System.out.println("fog of war on");
								
			    	for(int y = 0; y < Main.d; y++) {
			    		for(int x = 0; x < Main.d; x++) {
			    			JButton button = buttonList.get(y*Main.d + x);
			    			String text = "";
			    			if(Main.grid.getCell(x, y) != null) {
								if(Main.grid.getCell(x, y).side == 0) {
									text = Main.grid.getCell(x, y).getDisplayText();
									button.setForeground(human);
								}else {
									button.setBackground(Color.LIGHT_GRAY);
								}
			    			}else{
			    				button.setBackground(Color.LIGHT_GRAY);
			    			}
			    			button.setText(text);
			    		}
			    	}
					
					
					
				}else {
					System.out.println("fog of war off");
					
			    	for(int y = 0; y < Main.d; y++) {
			    		for(int x = 0; x < Main.d; x++) {
			    			JButton button = buttonList.get(y*Main.d + x);
			    			String text = "";
			    			if(Main.grid.getCell(x, y) != null) {
								text = Main.grid.getCell(x, y).getDisplayText();
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
			    			}
			    			button.setText(text);
			    		}
			    	}
		        }
			}
		});
		
		//bottomPanel.add(fogCheckBox);
    	
    	
		label = new JLabel("");
		label.setFont(new Font("Tahoma", Font.PLAIN, 30));
		verticalBox.add(label);
		//bottomPanel.add(label);
		
		
	
		
		//Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		//bottomPanel.add(horizontalStrut_1);
    }
}
