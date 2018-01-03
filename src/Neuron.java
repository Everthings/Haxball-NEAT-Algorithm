import java.util.ArrayList;


public class Neuron {
	private double value = 0;
	private ArrayList<Gene> incoming = new ArrayList<Gene>();
	
	public ArrayList<Gene> getIncoming(){
		return incoming;
	}
	
	public void add(Gene g){
		incoming.add(g);
	}
	
	public double getValue(){
		return value;
	}
	
	public void setValue(double d){
		this.value = d;
	}
}
