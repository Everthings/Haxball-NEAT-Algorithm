import java.awt.Color;
import java.awt.Graphics2D;


public class Obstacle extends GameObject{
	
	private Pair originalPos;
	private boolean moveable;

	public Obstacle(Pair startingPos, Color c, int radius, boolean moveable) {
		super(startingPos, Integer.MAX_VALUE, c, radius);
		
		this.moveable = moveable;
		this.originalPos = startingPos;
	}
	
	public boolean isMovable(){
		return moveable;
	}
	
	public void drawObstacle(Graphics2D g){
		g.setColor(c);
		g.fillOval((int)pos.getX() - radius, (int)pos.getY() - radius, (int)2 * radius, (int)2 * radius);
	}
	
	@Override
	public void calculateMove(){
		if(originalPos.sub(pos).getLength() > 30){
			velocity.setY(-velocity.getY());
		}
	}
}
