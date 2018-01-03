
public class Gene implements Comparable{
	
	int into;
	int from;
	double weight;
	boolean enabled;
	int innovation;
	
	Gene(int from, int into, double weight, boolean enabled, int innovation){
		this.into = into;
		this.from = from;
		this.weight = weight;
		this.enabled = enabled;
		this.innovation = innovation;
	}
	
	public int getInto() {
		return into;
	}

	public int getFrom() {
		return from;
	}

	public double getWeight() {
		return weight;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public void setWeight(double w) {
		this.weight = w;
	}
	
	public Gene clone(){
		return new Gene(from, into, weight, enabled, innovation);
	}

	@Override
	public int compareTo(Object o) {
		Gene g = (Gene) o;
		return into - g.getInto();
	}
	
	@Override
	public boolean equals(Object o) {
		Gene g = (Gene) o;
		return into == g.getInto() && from == g.getFrom();
	}
}
