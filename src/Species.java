import java.util.ArrayList;


public class Species{
	private double topFitness = 0;
	private int staleness = 0;  // a measure of how long a species has gone without improving
	private ArrayList<Footballer> footballers = new ArrayList<Footballer>();
	private double averageFitness = -1;
	
	public ArrayList<Footballer> getMembers(){
		return footballers;
	}
	
	public Footballer getMember(int i){
		return footballers.get(i);
	}
	
	public void addMember(Footballer f){
		footballers.add(f);
	}
	
	public double getTopFitness(){
		return topFitness;
	}

	public void setTopFitness(double topFitness) {
		this.topFitness = topFitness;
	}

	public int getStaleness() {
		return staleness;
	}

	public void setStaleness(int staleness) {
		this.staleness = staleness;
	}

	public double getAverageFitness() {
		return averageFitness;
	}

	public void setAverageFitness(double averageFitness) {
		this.averageFitness = averageFitness;
	}

}
