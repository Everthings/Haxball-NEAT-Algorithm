import java.awt.Color;


class GameObject{
	
	Pair velocity = new Pair(0, 0);
	Pair acceleration = new Pair(0, 0);
	
	double MAX_VELOCITY = 2;
	double MAX_ACCELERATION = 0.1;
	
	int mass; //used for collisions
	Pair pos;
	Color c;
	int radius;
	double lessenAmount = 0.05;
	
	public GameObject(Pair startingPos, int mass, Color c, int radius){
		this.pos = startingPos;
		this.mass = mass;
		this.c = c;
		this.radius = radius;
	}
	
	public void setMaxVel(double max){
		this.MAX_VELOCITY = max;
	}
	
	public void setMaxAcc(double max){
		this.MAX_ACCELERATION = max;
	}
	
	public void setLessenAmount(double amount){
		this.lessenAmount = amount;
	}
	
	public void calculateMove(){
		double length = acceleration.getLength();
		
		if(length > MAX_ACCELERATION){
			acceleration = acceleration.normalize().multiply(MAX_ACCELERATION);
		}
		//System.out.println(acceleration.getX() + " " + acceleration.getY());
		
		velocity = velocity.add(acceleration);
		
		length = velocity.getLength();
		if(length > MAX_VELOCITY)
			velocity = velocity.normalize().multiply(MAX_VELOCITY);
	}
	
	public void move(boolean friction){
		pos = pos.add(velocity);
		
		if(friction)
			velocity = friction(velocity);
		
		acceleration = new Pair(0, 0);
	}
	
	private Pair friction(Pair p){// slow down movement
		double x = p.getX();
		double y = p.getY();
		
		double lessenX = p.normalize().multiply(lessenAmount).getX();
		double lessenY = p.normalize().multiply(lessenAmount).getY();
		
		if(Math.abs(x) <= lessenX)
			x = 0;
		else if(x != 0)
			x -= lessenX;
		
		
		if(Math.abs(y) <= lessenY)
			y = 0;
		else if(y != 0)
			y -= lessenY;
		
		return new Pair(x, y);
	}
	
	public Pair getVelocity() {
		return velocity;
	}

	public void setVelocity(Pair velocity) {
		this.velocity = velocity;
	}

	public Pair getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(Pair acceleration) {
		this.acceleration = acceleration;
	}

	public void addAcceleration(Pair acc) {
		acceleration.setX(acceleration.getX() + acc.getX());
		acceleration.setY(acceleration.getY() + acc.getY());
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	public int getMass() {
		return mass;
	}

	public void setMass(int mass) {
		this.mass = mass;
	}

	public Pair getPos() {
		return pos;
	}

	public void setPos(Pair pos) {
		this.pos = pos;
	}

	public Color getC() {
		return c;
	}

	public void setC(Color c) {
		this.c = c;
	}
}
