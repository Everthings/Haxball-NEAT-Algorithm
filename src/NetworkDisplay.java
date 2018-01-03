import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;


public class NetworkDisplay extends JPanel{
	
	Footballer f;
	
	int WIDTH = 600;
	int HEIGHT = 450;
	
	NetworkDisplay(){		
		setSize(WIDTH, HEIGHT);
		setVisible(true);
		setBackground(Color.BLACK);
	}
	
	public void setFootballer(Footballer f){
		this.f = f;
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		
		int radius = 3;
		int left = 10;
		int right = WIDTH - 60;
		
		int inputMargin = 30;
		double inputGap = (double)(HEIGHT - 2 * inputMargin)/(Network.numInputs);
		
		int outputMargin = 50;
		double outputGap = (double)(HEIGHT - 2 * outputMargin)/(Network.numOutputs - 1);
		
		g.setColor(Color.WHITE);
		String[] labels = {"UP", "DOWN", "LEFT", "RIGHT", "KICK"};
		for(int i = 0; i < Network.numOutputs; i++){
			g.drawString(labels[i], right + 10, (int)(outputGap * i + outputMargin) - 17);
		}
		
		Cell[] cells = new Cell[Network.numInputs + Network.maxHidden + Network.numOutputs];
		for(int i = 0; i < Network.numInputs + Network.maxHidden + Network.numOutputs; i++){
			Neuron n = f.n.getNeurons()[i];
			
			if(n != null){
				if(i >= Network.numInputs && i < Network.numInputs + Network.maxHidden)
					cells[i] = new Cell((left + right)/2, HEIGHT/2 - 22, n.getValue());
				else if(i < Network.numInputs)
					cells[i] = new Cell(left, (int)(inputGap * i + inputMargin) - 22, n.getValue());
				else
					cells[i] = new Cell(right, (int)(outputGap * (i - Network.numInputs - Network.maxHidden) + outputMargin) - 22, n.getValue());
			}
			
			cells[360] = new Cell(left, HEIGHT - 25 - 22, f.n.getNeurons()[360].getValue());
			cells[361] = new Cell(left, HEIGHT - 20 - 22, f.n.getNeurons()[361].getValue());
			cells[362] = new Cell(left, HEIGHT - 15 - 22, f.n.getNeurons()[362].getValue());
			cells[363] = new Cell(left, HEIGHT - 10 - 22, f.n.getNeurons()[363].getValue());
			cells[364] = new Cell(left + 50, HEIGHT - 10 - 22, f.n.getNeurons()[364].getValue());//bias
		}
		
		for(int i = 0; i < 4; i++){
			for(Gene gene: f.genes.getGenome()){
				if(gene.isEnabled()){
					Cell c1 = cells[gene.getFrom()];
					Cell c2 = cells[gene.getInto()];
					
					if(gene.getFrom() >= Network.numInputs && gene.getFrom() < Network.numInputs + Network.maxHidden){
						c1.x = (int)(0.75 * c1.x + 0.25 * c2.x);		
						c1.y = (int)(0.75 * c1.y + 0.25 * c2.y);
					}
					
					if(gene.getInto() >= Network.numInputs && gene.getInto() < Network.numInputs + Network.maxHidden){
						c2.x = (int)(0.75 * c2.x + 0.25 * c1.x);
						c2.y = (int)(0.75 * c2.y + 0.25 * c1.y);
					}
								
				}
			}
		}
		
		for(Gene gene: f.genes.getGenome()){
			if(gene.isEnabled()){
				Cell c1 = cells[gene.getFrom()];
				Cell c2 = cells[gene.getInto()];
				
				int opacity = 255;
				if(c1.value <= 0.5){
					opacity = 100;
				}
				
				Color c = new Color(0, 255, 0, opacity);
				if(gene.getWeight() < 0){
					c = new Color(255, 0, 0, opacity);
				}
				
				g.setColor(c);
				g.drawLine(c1.x + radius, c1.y, c2.x - radius, c2.y);
			}
		}
		
		for(Cell c: cells){
			if(c != null){
				if(c.value <= 0.5)
					g2.setColor(new Color(255, 255, 255, 100));
				else
					g2.setColor(new Color(255, 255, 255, 255));
				
				g2.fillOval(c.x - radius, c.y - radius, 2 * radius, 2 * radius);
			}
		}
	}
	
	public double sigmoid(double input){
		double ret = (double)1/(double)(1 + Math.pow(Math.E, -input));
		return ret;
	}
}

class Cell{
	int x;
	int y; 
	double value;
	
	Cell(int x, int y, double value){
		this.x = x;
		this.y = y;
		this.value = value;
	}
	
}

