import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.AbstractAction;


public class Match{
	
	ArrayList<Movement> movements = new ArrayList<Movement>();
	ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
	int count = Integer.MAX_VALUE;
	final int MAX_COUNT = 5;
	private final int FIELD_WIDTH;
	private final int FIELD_HEIGHT;
	private final int GOAL_LENGTH = 120;
	
	private int fieldOffset = 60; //both X and Y
	private int minKickDistance = 7;
	
	long startTime;
	
	boolean kick = false;
	
	Footballer player = null;
	Ball ball = null;
	
	boolean goal = false;
	
	int goals = 0;
	int goalsAgainst = 0;
	int dribbles = 0; //number of times contacted ball
	int kicks = 0;
	int obstaclesHit = 0;
	double distanceTraveled = 0;
	double totalDistanceToBall = 0;
	int timesMeasured = 0;
	int ballBounces = 0;
	
	double[] inputs = new double[364];
	int radius;
	
	Match(int width, int height, Footballer f){
		this.FIELD_WIDTH = width;
		this.FIELD_HEIGHT = height;
		startTime = System.currentTimeMillis();
		
		player = f;
		ball = new Ball(new Pair((double)width * 1/2 + fieldOffset, (double)height * 1/2 + fieldOffset));
		
		radius = 100 + player.getRadius();
		
		initObstacles();
		
		for(int i = 0; i < 100; i++){
			//obstacles.add(new Obstacle(new Pair(Math.random() * FIELD_WIDTH + fieldOffset, Math.random() * FIELD_HEIGHT + fieldOffset), new Color(204, 204, 204), 3)); 
		}
	}
	
	public void initObstacles(){
		
		//Goalies
		obstacles.add(new Obstacle(new Pair(fieldOffset, FIELD_HEIGHT/2 + fieldOffset), Color.RED, 20, true)); 
		obstacles.get(obstacles.size() - 1).setVelocity(new Pair(0, 3));
		obstacles.add(new Obstacle(new Pair(FIELD_WIDTH + fieldOffset, FIELD_HEIGHT/2 + fieldOffset), Color.BLUE, 20, true)); 
		obstacles.get(obstacles.size() - 1).setVelocity(new Pair(0, 3));
		
		//Goal Posts
		obstacles.add(new Obstacle(new Pair(fieldOffset, FIELD_HEIGHT/2 - GOAL_LENGTH/2 + fieldOffset), new Color(204, 204, 204), 8, false)); 
		obstacles.add(new Obstacle(new Pair(fieldOffset, FIELD_HEIGHT/2 + GOAL_LENGTH/2 + fieldOffset), new Color(204, 204, 204), 8, false)); 
		obstacles.add(new Obstacle(new Pair(FIELD_WIDTH + fieldOffset, FIELD_HEIGHT/2 - GOAL_LENGTH/2 + fieldOffset), new Color(204, 204, 204), 8, false)); 
		obstacles.add(new Obstacle(new Pair(FIELD_WIDTH + fieldOffset, FIELD_HEIGHT/2 + GOAL_LENGTH/2 + fieldOffset), new Color(204, 204, 204), 8, false));
		
		//Real Obstacles
		obstacles.add(new Obstacle(new Pair(7 * FIELD_WIDTH/10 + fieldOffset, FIELD_HEIGHT/2 + fieldOffset + 1), new Color(82, 41, 124), 10, false)); 
		obstacles.add(new Obstacle(new Pair(8 * FIELD_WIDTH/10 + fieldOffset, FIELD_HEIGHT/2 + fieldOffset), new Color(82, 41, 124), 10, false)); 
		obstacles.add(new Obstacle(new Pair(9 * FIELD_WIDTH/10 + fieldOffset, FIELD_HEIGHT/2 + fieldOffset), new Color(82, 41, 124), 10, false)); 
		obstacles.add(new Obstacle(new Pair(8 * FIELD_WIDTH/10 + fieldOffset, 3 * FIELD_HEIGHT/4 + fieldOffset), new Color(82, 41, 124), 10, false)); 
		obstacles.add(new Obstacle(new Pair(8 * FIELD_WIDTH/10 + fieldOffset, 1 * FIELD_HEIGHT/4 + fieldOffset), new Color(82, 41, 124), 10, false)); 
		obstacles.add(new Obstacle(new Pair(6 * FIELD_WIDTH/10 + fieldOffset, 1.5 * FIELD_HEIGHT/4.5 + fieldOffset), new Color(82, 41, 124), 10, false)); 
		obstacles.add(new Obstacle(new Pair(6 * FIELD_WIDTH/10 + fieldOffset, 3 * FIELD_HEIGHT/4.5 + fieldOffset), new Color(82, 41, 124), 10, false)); 
		
		//Goal Outlines
		for(double i = 10; i < fieldOffset; i += 25){
			obstacles.add(new Obstacle(new Pair(i, FIELD_HEIGHT/2 - GOAL_LENGTH/2 + fieldOffset), Color.BLACK, 9, false)); 
			obstacles.add(new Obstacle(new Pair(FIELD_WIDTH + 2 * fieldOffset - i, FIELD_HEIGHT/2 - GOAL_LENGTH/2 + fieldOffset), Color.BLACK, 9, false)); 
		}
		
		for(double i = 10; i < fieldOffset; i += 25){
			obstacles.add(new Obstacle(new Pair(i, FIELD_HEIGHT/2 + GOAL_LENGTH/2 + fieldOffset), Color.BLACK, 9, false)); 
			obstacles.add(new Obstacle(new Pair(FIELD_WIDTH + 2 * fieldOffset - i, FIELD_HEIGHT/2 + GOAL_LENGTH/2 + fieldOffset), Color.BLACK, 9, false)); 
		}
		
		for(double i = FIELD_HEIGHT/2 - GOAL_LENGTH/2 + fieldOffset + 20; i < FIELD_HEIGHT/2 + GOAL_LENGTH/2 + fieldOffset; i += 20){
			obstacles.add(new Obstacle(new Pair(10, i ), Color.BLACK, 9, false)); 
			obstacles.add(new Obstacle(new Pair(FIELD_WIDTH + 2 * fieldOffset - 10, i), Color.BLACK, 9, false)); 
		}
	}
	
	/*
	public void setupKeyBindings(JPanel jp){
		
		InputMap inMap = jp.getInputMap(JComponent.WHEN_FOCUSED);
		jp.requestFocus();
		
		KeyStroke upKey = KeyStroke.getKeyStroke("UP");
		KeyStroke downKey = KeyStroke.getKeyStroke("DOWN");
		KeyStroke leftKey = KeyStroke.getKeyStroke("LEFT");
		KeyStroke rightKey = KeyStroke.getKeyStroke("RIGHT");
		KeyStroke spaceKey = KeyStroke.getKeyStroke("SPACE");
			
		KeyStroke upReleasedKey = KeyStroke.getKeyStroke("released UP");
		KeyStroke downReleasedKey = KeyStroke.getKeyStroke("released DOWN");
		KeyStroke leftReleasedKey = KeyStroke.getKeyStroke("released LEFT");
		KeyStroke rightReleasedKey = KeyStroke.getKeyStroke("released RIGHT");
		KeyStroke spaceReleasedKey = KeyStroke.getKeyStroke("released SPACE");

		inMap.put(upKey, "u");
		inMap.put(downKey, "d");
		inMap.put(leftKey, "l");
		inMap.put(rightKey, "r");
		inMap.put(spaceKey, "space");
		
		inMap.put(upReleasedKey, "uR");
		inMap.put(downReleasedKey, "dR");
		inMap.put(leftReleasedKey, "lR");
		inMap.put(rightReleasedKey, "rR");
		inMap.put(spaceReleasedKey, "spaceR");
		
		ActionMap actMap = jp.getActionMap();
		
		actMap.put("u", new MoveAddAction(player, 0, -2));
		actMap.put("d", new MoveAddAction(player, 0, 2));
		actMap.put("l", new MoveAddAction(player, -2, 0));
		actMap.put("r", new MoveAddAction(player, 2, 0));
		
		actMap.put("uR", new MoveRemoveAction(player, 0, -2));
		actMap.put("dR", new MoveRemoveAction(player, 0, 2));
		actMap.put("lR", new MoveRemoveAction(player, -2, 0));
		actMap.put("rR", new MoveRemoveAction(player, 2, 0));
		
		actMap.put("space", new AbstractAction(){
			
			@Override
			public void actionPerformed(ActionEvent e) {
				kick = true;
			}
			
		});
		
		actMap.put("spaceR", new AbstractAction(){
			
			@Override
			public void actionPerformed(ActionEvent e) {
				kick = false;
			}
			
		});
	}
	*/
	
	public void kickBall(GameObject o){
		
		Pair distanceBetweenObjects = ball.getPos().sub(o.getPos());
		
		double x = o.getVelocity().getX() * distanceBetweenObjects.getX();
		double y = o.getVelocity().getY() * distanceBetweenObjects.getY();
		double kickStrength = (x + y)/distanceBetweenObjects.getLength();
	
		ball.setVelocity(ball.getPos().sub(o.getPos()).normalize().multiply(Math.pow(2.5, kickStrength/3 + 1)));
		
		kicks++;
	}
	
	public void generateNewBall(){
		ball = new Ball(new Pair(FIELD_WIDTH/2 + fieldOffset, FIELD_HEIGHT/2 + fieldOffset));
	}
	
	public void resetPositions(){
		player.setPos(new Pair(FIELD_WIDTH/4 + fieldOffset, FIELD_HEIGHT/2 + fieldOffset));
		player.setVelocity(new Pair(0, 0));
	}
	
	public boolean handleCollisions(GameObject item1, GameObject item2, float amp1, float amp2){
		double xDist, yDist;

        xDist = item1.getPos().getX() - item2.getPos().getX();
        yDist = item1.getPos().getY() - item2.getPos().getY();
        
        double distSquared = xDist*xDist + yDist*yDist;
        if(distSquared <= (item1.getRadius() + item2.getRadius())*(item1.getRadius() + item2.getRadius())){
	        double xVelocity = item2.getVelocity().getX() - item1.getVelocity().getX();
	        double yVelocity = item2.getVelocity().getY() - item1.getVelocity().getY();
	        double dotProduct = xDist*xVelocity + yDist*yVelocity;
	        //dot product to check if the dice are moving toward each other - problems occurred before where the dice would stick
	        if(dotProduct > 0){
	        	//System.out.println("col " +  item1 + " with " + item2);
	            double collisionScale = dotProduct / distSquared;
	            double xCollision = xDist * collisionScale;
	            double yCollision = yDist * collisionScale;
	            
	            //The Collision vector is the speed difference projected on the Dist vector,
	            //thus it is the component of the speed difference needed for the collision.
	            double combinedMass = item1.getMass() + item2.getMass();
	            double collisionWeightA = 2 * item2.getRadius() / combinedMass;
	            double collisionWeightB = 2 * item1.getRadius() / combinedMass;
	           
	            item1.setVelocity(item1.getVelocity().add(new Pair((float)(collisionWeightA * xCollision), (float)(collisionWeightA * yCollision))).multiply(amp1));
	            item2.setVelocity(item2.getVelocity().sub(new Pair((float)(collisionWeightB * xCollision), (float)(collisionWeightB * yCollision))).multiply(amp2));
	            return true;
	        }
        }
        
        return false;
	}
	
	public void handleBoundaries(GameObject item){
		double x = item.getPos().getX();
		double y = item.getPos().getY();
		
		Pair vel = item.getVelocity();
		
		if(x < item.radius){
			item.getVelocity().setX(-vel.getX());
			item.getPos().setX(item.getRadius());
		}else if(x > FIELD_WIDTH + 2 * fieldOffset - item.getRadius()){
			item.getVelocity().setX(-vel.getX());
			item.getPos().setX(FIELD_WIDTH + 2 * fieldOffset - item.getRadius());
		}
		
		if(y < item.getRadius()){
			item.getVelocity().setY(-vel.getY());
			item.getPos().setY(item.getRadius());
		}else if(y > FIELD_HEIGHT + 2 * fieldOffset - item.getRadius() + 22){
			item.getVelocity().setY(-vel.getY());
			item.getPos().setY(FIELD_HEIGHT + 2 * fieldOffset - item.getRadius() + 22);
		}
	}
	
	public boolean handleFieldBoundaries(GameObject item){
		double x = item.getPos().getX();
		double y = item.getPos().getY();
		
		Pair vel = item.getVelocity();
		
		boolean ret = false;
		//System.out.println(Math.abs(x - FIELD_WIDTH/2 - fieldOffset) + " " + FIELD_WIDTH/2);
		
		if(!(Math.abs(y - FIELD_HEIGHT/2 - fieldOffset + item.getRadius()) < GOAL_LENGTH/2)){//inefficient I know
			if(x < item.getRadius() + fieldOffset){
				item.getVelocity().setX(-vel.getX());
				item.getPos().setX(item.getRadius() + fieldOffset);
				ret = true;
			}else if(x > FIELD_WIDTH + fieldOffset - item.getRadius()){
				item.getVelocity().setX(-vel.getX());
				item.getPos().setX(FIELD_WIDTH + fieldOffset - item.getRadius());
				ret = true;
			}
		}
		
		if(y < item.getRadius() + fieldOffset){
			item.getVelocity().setY(-vel.getY());
			item.getPos().setY(item.getRadius() + fieldOffset);
			ret = true;
		}else if(y > FIELD_HEIGHT + fieldOffset - item.getRadius()){
			item.getVelocity().setY(-vel.getY());
			item.getPos().setY(FIELD_HEIGHT + fieldOffset - item.getRadius());
			ret = true;
		}
		
		return ret;
	}
	
	
	public double[] getInput(){
		inputs = new double[Network.numInputs];

		ArrayList<Obstacle> collisionCandidates = new ArrayList<Obstacle>();
		for(Obstacle o: obstacles){
			if(o.getPos().sub(player.getPos()).getLength() < o.getRadius() + radius){
				collisionCandidates.add(o);
			}
		}
			
		
		//check top boundaries
		for(int i = 0; i < 360; i++){
			if(player.getPos().getY() - fieldOffset < radius * Math.sin(convertToRadians(i)) && i < 180){
				inputs[i] = radius - ((player.getPos().getY() - fieldOffset)/Math.sin(convertToRadians(i)));
			}
			
			if(FIELD_HEIGHT + fieldOffset - player.getPos().getY() < -radius * Math.sin(convertToRadians(i)) && i > 180){
				double distance = ((FIELD_HEIGHT + fieldOffset - player.getPos().getY())/-Math.sin(convertToRadians(i)));
				if(inputs[i] == 0 || distance < inputs[i])
					inputs[i] = radius - distance;
			}
			
			if(player.getPos().getX() - fieldOffset < -radius * Math.cos(convertToRadians(i)) && (i > 90 && i < 270)){
				double distance = ((player.getPos().getX() - fieldOffset)/-Math.cos(convertToRadians(i)));
				if(inputs[i] == 0 || distance < inputs[i])
					inputs[i] = radius - distance;
			}
			
			if(FIELD_WIDTH + fieldOffset - player.getPos().getX() < radius * Math.cos(convertToRadians(i)) && (i < 90 || i > 270)){
				double distance = ((FIELD_WIDTH + fieldOffset - player.getPos().getX())/Math.cos(convertToRadians(i)));
				if(inputs[i] == 0 || distance < inputs[i])
					inputs[i] = radius - distance;
				
			}
			
			for(Obstacle o: collisionCandidates){
				
				Pair lineEnd = player.getPos().add(new Pair(radius * Math.cos(convertToRadians(i)), -radius * Math.sin(convertToRadians(i))));
				
			if(Pair.distanceFromPointToLine(player.getPos(), lineEnd, o.getPos()) < o.getRadius()){
					Pair difference = o.getPos().sub(player.getPos());
					double theta = Math.atan2(-(difference.getY()), difference.getX());
				
					double max = theta + Math.PI/2;
					double min = theta - Math.PI/2;
					double theta2 = convertToRadians(i);
					
					if(min < 0){
						max += -min;
						theta2 += -min;
						min = 0;
					}
				
					if(simplifyRadian(theta2) > simplifyRadian(min) && simplifyRadian(theta2) < max){
						double distance = difference.getLength();
						if(inputs[i] == 0 || distance < inputs[i])
							inputs[i] = radius - distance;
					}
					
				}
			}
		}
		
		inputs[360] = ball.getPos().sub(player.getPos()).getLength();
		double theta = Math.atan2(ball.getPos().getY() - player.getPos().getY(), ball.getPos().getX() - player.getPos().getX());
		if(theta < 0)
			theta += 2 * Math.PI;
		inputs[361] = theta;
		
		Pair middleOfGoal = new Pair(FIELD_WIDTH + 2 * fieldOffset - 10, FIELD_HEIGHT/2 + fieldOffset);
		inputs[362] = middleOfGoal.sub(player.getPos()).getLength();
		theta = Math.atan2(middleOfGoal.getY() - player.getPos().getY(), middleOfGoal.getX() - player.getPos().getX());
		if(theta < 0)
			theta += 2 * Math.PI;
		inputs[363] = theta;
		inputs[364] = 1;//bias
		
		return inputs;
	}
	
	public double simplifyRadian(double radian){
		
		double ret = radian;
		
		if(ret >= 0){
			while(ret > 2 * Math.PI){
				ret -= 2 * Math.PI;
			}
		}else{
			while(ret < 0){
				ret += 2 * Math.PI;
			}
		}
		
		return ret;
	}
	
	public double convertToRadians(double angle){
		return angle / 180 * Math.PI;
	}
	

	public void runIteration(){
		
		double[] outputs = player.n.evalNetwork(getInput());
		
		/*
		for(int i = movements.size() - 1; i >= 0; i--){
			Movement m = movements.get(i);
			m.getGameObject().addAcceleration(new Pair(m.getMove().getX(), m.getMove().getY()));
		}
		*/

		if(outputs[0] > 0.5) //UP
			player.addAcceleration(new Pair(0, -2));
		if(outputs[1] > 0.5)//DOWN
			player.addAcceleration(new Pair(0, 2));
		if(outputs[2] > 0.5)//LEFT
			player.addAcceleration(new Pair(-2, 0));
		if(outputs[3] > 0.5)//RIGHT
			player.addAcceleration(new Pair(2, 0));
		if(outputs[4] > 0.5)//KICK
			kick = true;
		else
			kick = false;
		
		player.calculateMove();
		
		boolean col;
		
		if(handleFieldBoundaries(ball))
			col = handleCollisions(player, ball, 2f, 1f);
		else
			col = handleCollisions(player, ball, 1f, 1f);
		
		
		boolean[] colsPlayer = new boolean[obstacles.size()];
		boolean[] colsBall = new boolean[obstacles.size()];
		
		for(int i = 0; i < obstacles.size(); i++){
			Obstacle ob = obstacles.get(i);
			ob.calculateMove();
			ob.move(false);
			colsBall[i] = handleCollisions(ball, ob, 1f, 1f);
			colsPlayer[i] = handleCollisions(player, ob, 1f, 1f);
		}
		
		handleBoundaries(player);
		
		if(handleFieldBoundaries(ball))
			ballBounces++;
		
		if(kick && player.getPos().sub(ball.getPos()).getLength() < player.radius + ball.radius + minKickDistance){
			kickBall(player);
			//kick = false;
		}
		
		for(int i = 0; i < colsPlayer.length; i++){
			if(colsPlayer[i]){
				Obstacle ob = obstacles.get(i);
				
				Pair distance = player.getPos().sub(ob.getPos());
				double x = player.getVelocity().getX() * distance.getX();
				double y = player.getVelocity().getY() * distance.getY();
				double strength = (x + y)/distance.getLength();
			
				player.setVelocity(player.getVelocity().sub(distance.normalize().multiply(strength * 2)));  
				
				player.setPos(ob.getPos().add(player.getPos().sub(ob.getPos()).normalize().multiply(player.getRadius() + ob.getRadius())));  
				obstaclesHit++;
			}
			
			if(colsBall[i]){
				Obstacle ob = obstacles.get(i);
				
				Pair distance = ball.getPos().sub(ob.getPos());
				double x = ball.getVelocity().getX() * distance.getX();
				double y = ball.getVelocity().getY() * distance.getY();
				double strength = (x + y)/distance.getLength();
			
			    ball.setVelocity(ball.getVelocity().sub(distance.normalize().multiply(strength * 2)));  
				ball.setPos(ob.getPos().add(ball.getPos().sub(ob.getPos()).normalize().multiply(ball.getRadius() + ob.getRadius())));  
			
				ballBounces++;
			}
		}
		
		Pair oldPlayerPos = player.getPos();
		player.move(true);
		distanceTraveled += player.getPos().sub(oldPlayerPos).getLength();
		
		ball.move(true);
		
		if(col){
			 ball.setPos(player.getPos().add(ball.getPos().sub(player.getPos()).normalize().multiply(player.getRadius() + ball.getRadius())));
	         player.setPos(ball.getPos().add(player.getPos().sub(ball.getPos()).normalize().multiply(player.getRadius() + ball.getRadius())));  
	         dribbles++;
		}
		
		if(!goal && Math.abs(ball.getPos().getY() - FIELD_HEIGHT/2 - fieldOffset + ball.getRadius()) < GOAL_LENGTH/2 && Math.abs(ball.getPos().getX() - FIELD_WIDTH/2 - fieldOffset) - ball.getRadius() > FIELD_WIDTH/2){
			
			if(ball.getPos().getX() < fieldOffset){
				goalsAgainst++;
			}else{
				goals++;
				goal = true;
			}
			
			/*
			goal = true;
			
			new Thread(new Runnable(){

				@Override
				public void run() {
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					generateNewBall();
					goal = false;
				}
				
			}).start();
			*/
			
			generateNewBall();
			resetPositions();
			
		}
		
		timesMeasured++;
		totalDistanceToBall += ball.getPos().sub(player.getPos()).getLength() - ball.getRadius() - player.getRadius();
	}

	public void updatePlayerStats(int time){
		
		double distanceFromBallToGoal = new Pair(FIELD_WIDTH + fieldOffset, FIELD_HEIGHT/2 + fieldOffset).sub(ball.getPos()).getLength();
		
		if(goals > 0)
			distanceFromBallToGoal = 0;
			
		
		player.calcFitness(goals, goalsAgainst, dribbles, kicks, obstaclesHit, distanceFromBallToGoal, distanceTraveled, totalDistanceToBall/timesMeasured, time, ballBounces);
	}
	
	public void draw(Graphics g){
		
		Graphics2D g2 = (Graphics2D) g; 
		
		runIteration();
		
		drawField(g2);
		
		drawInputs(g2);
		
		for(Obstacle ob: obstacles){
			ob.drawObstacle(g2);
		}

		player.drawFootballer(g2);
		ball.drawBall(g2);
		drawMinKickDistance(g2);
	}
	
	public void drawInputs(Graphics2D g){
		for(int i = 0; i < 360; i++){
			
			double x = player.getPos().getX();
			double y = player.getPos().getY();
			
			g.setStroke(new BasicStroke(1));
			
			if(inputs[i] != 0){
				
				double realDistance = -(inputs[i] - radius);
				
				g.setColor(Color.GREEN);
				g.drawLine((int) x, (int) y, (int)(x + realDistance * Math.cos(convertToRadians(i))), (int)(y - realDistance * Math.sin(convertToRadians(i))));
				g.setColor(Color.BLACK);
				g.drawLine((int)(x + realDistance * Math.cos(convertToRadians(i))), (int)(y - realDistance * Math.sin(convertToRadians(i))), (int)(x + radius * Math.cos(convertToRadians(i))), (int)(y - radius * Math.sin(convertToRadians(i))));
			}else{
				g.setColor(Color.BLACK);
				g.drawLine((int) x, (int) y, (int)(x + radius * Math.cos(convertToRadians(i))), (int)(y - radius * Math.sin(convertToRadians(i))));
			}
		}
		
		//distance/angle to goal center
		AffineTransform original = g.getTransform();
		AffineTransform arrowRotation = new AffineTransform();
		g.setStroke(new BasicStroke(3));
		double theta = inputs[363];
		double rotX = Math.cos(theta) * player.getRadius();
		double rotY = Math.sin(theta) * player.getRadius();
		arrowRotation.rotate(theta, player.getPos().getX() + rotX, player.getPos().getY() + rotY);
		g.setTransform(arrowRotation);
		g.setColor(Color.CYAN);
		g.drawLine((int)(player.getPos().getX() + rotX), (int)(player.getPos().getY() + rotY), (int)(player.getPos().getX() + inputs[362]), (int)(player.getPos().getY() + rotY));
		g.drawLine((int)(player.getPos().getX() + inputs[362]), (int)(player.getPos().getY() + rotY), (int)(player.getPos().getX() + inputs[362] - 10), (int)(player.getPos().getY() - 10 + rotY));
		g.drawLine((int)(player.getPos().getX() + inputs[362]), (int)(player.getPos().getY() + rotY), (int)(player.getPos().getX() + inputs[362] - 10), (int)(player.getPos().getY() + 10 + rotY));
		
		//distance/angle to ball
		
		arrowRotation = new AffineTransform();
		theta = inputs[361];
		rotX = Math.cos(theta) * player.getRadius();
		rotY = Math.sin(theta) * player.getRadius();
		arrowRotation.rotate(theta, player.getPos().getX() + rotX, player.getPos().getY() + rotY);
		g.setTransform(arrowRotation);
		g.setColor(Color.BLUE);
		g.drawLine((int)(player.getPos().getX() + rotX), (int)(player.getPos().getY() + rotY), (int)(player.getPos().getX() + inputs[360]), (int)(player.getPos().getY() + rotY));
		g.drawLine((int)(player.getPos().getX() + inputs[360]), (int)(player.getPos().getY() + rotY), (int)(player.getPos().getX() + inputs[360] - 10), (int)(player.getPos().getY() - 10 + rotY));
		g.drawLine((int)(player.getPos().getX() + inputs[360]), (int)(player.getPos().getY() + rotY), (int)(player.getPos().getX() + inputs[360] - 10), (int)(player.getPos().getY() + 10 + rotY));
		
		g.setTransform(original);
		
	}
	
	private void drawMinKickDistance(Graphics2D g){
		if(kick)
			g.setColor(Color.WHITE);
		else
			g.setColor(Color.GRAY);
		g.setStroke(new BasicStroke(2));
		g.drawOval((int)player.getPos().getX() - player.getRadius() - minKickDistance, (int)player.getPos().getY() - player.getRadius() - minKickDistance, (int)2 * player.getRadius() + 2 * minKickDistance, (int)2 * player.getRadius() + 2 * minKickDistance);
	}
	
	private void drawField(Graphics2D g){
		
		g.setColor(new Color(0, 127, 42));
		g.fillRect(0, 0, 1000, 1000);
		
		//field outline
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g.drawRect(fieldOffset, 2 + fieldOffset, FIELD_WIDTH, FIELD_HEIGHT);
		g.drawLine(FIELD_WIDTH/2 + fieldOffset, 2 + fieldOffset, FIELD_WIDTH/2 + fieldOffset, FIELD_HEIGHT + fieldOffset);
		
		//center circle
		int radius = 70;
		g.drawOval(FIELD_WIDTH/2 - radius + fieldOffset, FIELD_HEIGHT/2 - radius + fieldOffset, 2 * radius, 2 * radius);
	
		//goal boxes
		g.drawRect(fieldOffset, FIELD_HEIGHT/2 - radius + fieldOffset, radius, 2 * radius);
		g.drawRect(FIELD_WIDTH - radius + fieldOffset, FIELD_HEIGHT/2 - radius + fieldOffset, radius, 2 * radius);
		
		//goals
		g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g.drawRect(10, FIELD_HEIGHT/2 - GOAL_LENGTH/2 + fieldOffset, fieldOffset - 10, GOAL_LENGTH);
		g.drawRect(FIELD_WIDTH + fieldOffset, FIELD_HEIGHT/2 - GOAL_LENGTH/2 + fieldOffset, fieldOffset - 10, GOAL_LENGTH);
	}

	
	class MoveAddAction extends AbstractAction{
		
		GameObject o;
		double dx, dy;
		
		MoveAddAction(GameObject o, double dx, double dy){
			this.o = o;
			this.dx = dx;
			this.dy = dy;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			Movement m = new Movement(o, new Pair(dx, dy));
			if(!movements.contains(m))
				movements.add(m);
			//System.out.println("HI");
		}
		
	}
	
	class MoveRemoveAction extends AbstractAction{
		
		GameObject o;
		double dx, dy;
		
		MoveRemoveAction(GameObject o, double dx, double dy){
			this.o = o;
			this.dx = dx;
			this.dy = dy;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			movements.remove(new Movement(o, new Pair(dx, dy)));
		}
	}
}

class Movement{
	
	Pair mov;
	GameObject o;
	
	Movement(GameObject o, Pair mov){
		this.mov = mov;
		this.o = o;
	}
	
	public GameObject getGameObject() {
		return o;
	}
	
	public Pair getMove() {
		return mov;
	}
	
	@Override
	public boolean equals(Object o){
		
		Movement m = (Movement) o;
		
		if(m.getMove().getX() == mov.getX() && m.getMove().getY() == mov.getY() && m.getGameObject().equals(this.o)){
			return true;
		}
					
		return false;
	}
}
