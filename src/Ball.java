import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;



public class Ball extends GameObject{

	public Ball(Pair startingPos) {
		super(startingPos, 25, Color.WHITE, 7);
		
		setLessenAmount(0.015);
		setMaxVel(7);
	}
	
	public void drawBall(Graphics2D g){
		
		g.setColor(c);
		g.fillOval((int)pos.getX() - radius, (int)pos.getY() - radius, (int)2 * radius, (int)2 * radius);
		g.setStroke(new BasicStroke(2));
		g.setColor(Color.BLACK);
		g.drawOval((int)pos.getX() - radius, (int)pos.getY() - radius, (int)2 * radius, (int)2 * radius);
	}

}
