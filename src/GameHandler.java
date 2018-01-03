import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameHandler extends JPanel{
	
	private JFrame window = new JFrame();
	private final int FRAME_WIDTH = 920;
	private final int FRAME_HEIGHT = 665;
	Match m;
	public static Random rng;
	
	public static void main(String args[]){
		
		new GameHandler();
		
		/*
		GameHandler gh = new GameHandler();
		gh.initWindow();
		gh.initRepaintThread();
		*/
	}
	
	GameHandler(){
		Simulation s = new Simulation(800, 500, 3000, 0.05f, 1);
		initWindow();
		window.add(s);
		s.newSimulation();
	}
	
	
	public void initWindow(){
		window = new JFrame();
		window.setVisible(true);
		window.setTitle("Simulation");
		window.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		window.setLocationRelativeTo(null); 
		window.setBackground(Color.WHITE); 
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		window.setResizable(false);
		
		//setSize(FRAME_WIDTH, FRAME_HEIGHT);
		//setVisible(true);
		
	
		
		//m = new Match(800, 500, new Footballer(new Pair(100, 200)));
		//m.setupKeyBindings(this);
	}
	
	
	/*
	public void initRepaintThread(){
		new Thread(new Runnable(){

			long lastUpdate = System.currentTimeMillis();
			
			@Override
			public void run() {
				while(true){
					if(System.currentTimeMillis() > lastUpdate + 10){
						repaint();
						lastUpdate = System.currentTimeMillis();
					}
				}
			}
			
		}).start();
	}
	
	@Override
	public void paintComponent(Graphics g){
		m.draw(g);
	}
	
	*/
}
