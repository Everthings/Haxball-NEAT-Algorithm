
public class Pair {
	private double x;
	private double y;
	
	public Pair(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public static double distanceFromPointToLine(Pair p1, Pair p2, Pair point){
		double x0 = point.getX();
		double y0 = point.getY();
		
		double x1 = p1.getX();
		double y1 = p1.getY();
		
		double x2 = p2.getX();
		double y2 = p2.getY();
		
		return Math.abs((y2 - y1) * x0 - (x2 - x1) * y0 + x2 * y1 - y2 * x1) / Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2));
	}
	
	public void setX(double x){
		this.x = x;
	}
	
	public void setY(double y){
		this.y = y;
	}
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
	
	public double getLength(){
		return Math.sqrt(x * x + y * y);
	}
	
	public Pair normalize(){
		
		double length = getLength();
		
		return new Pair(x/length, y/length);
	}
	
	public Pair multiply(double a){
		return new Pair(a * x, a * y);
	}
	
	public Pair add(Pair p){
		return new Pair(x + p.getX(), y + p.getY());
	}
	
	public Pair sub(Pair p){
		return new Pair(x - p.getX(), y - p.getY());
	}
}
