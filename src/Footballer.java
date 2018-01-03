import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;


public class Footballer extends GameObject{
	
	Genome genes;
	Network n;
	double fitness;
	int rank;
	int maxNeuron = Network.numInputs;
	
	static int innovationNumber = Network.numInputs;
	
	Random random;
	
	public Footballer(Pair startingPos, Random rand) {
		super(startingPos, 10, Color.RED, 20);
		
		genes = new Genome();
		n = new Network();
		this.random = rand;
	}
	
	public void calcFitness(int goals, int goalsAgainst, int dribbles, int kicks, int obstaclesHit, double distanceFromBallToGoal, double distanceTraveled, double averageDistanceToGoal, int millis, int ballBounces){
		fitness = Math.max(Math.pow(goals - goalsAgainst + 4, 5) - 1024 + distanceTraveled * 0.01 + dribbles * 0.5 + kicks * 0 - ballBounces * 2 - millis * 0.005 - distanceFromBallToGoal * 0.05 - averageDistanceToGoal * 0.05 - obstaclesHit * 1 - (maxNeuron - Network.numInputs) * 0.5 + 200, 1);// min fitness of 1
		//fitness = Math.max(Math.pow(goals - goalsAgainst + 2, 5) - 32 + distanceTraveled * 0.01 + dribbles * 0.15 + kicks * 0.1 - distanceFromBallToGoal * 0.01 - averageDistanceToGoal * 0.05 - obstaclesHit * 1 - (maxNeuron - Network.numInputs) * 0.5 + 50, 1);// min fitness of 1
	}
	
	public static int getNextInnovation(){
		return innovationNumber++;
	}
	
	public static void setInnovation(int i){
		innovationNumber = i;
	}
	
	public void setRank(int r){
		this.rank = r;
	}
	
	public void initGenome(){
		for(int i = 0; i < Network.numInputs - 1; i++){
			genes.getGenome().add(new Gene(i, Network.numInputs + Network.maxHidden + (i % Network.numOutputs), random.nextDouble() * 4 - 2, true, i));
		}
		
		genes.getGenome().add(new Gene(Network.numInputs - 1, Network.numInputs + Network.maxHidden + 3, random.nextDouble() * 4 - 2, true, Network.numInputs - 1));
	}
	
	public void mutate(double chance, double step){

		double connectionMutationChance = 0.1;
		double nodeMutationChance = 0.1;
		//BiasMutationChance = 0.40
		double stepSize = 0.1;
		double switchMutationChance = 0.1;
		
		while(connectionMutationChance > 0){
			if(random.nextDouble() <= connectionMutationChance)
				addConnectionMutation(n);
			else
				connectionMutationChance--;
		}
		
		while(nodeMutationChance > 0){
			if(random.nextDouble() <= nodeMutationChance)
				addNodeMutation();
			else
				nodeMutationChance--;
		}
		
		weightsMutation(chance, stepSize);
		
		while(switchMutationChance > 0){
			if(random.nextDouble() <= switchMutationChance)
				switchMutation();
			else
				switchMutationChance--;
		}
	}
	
	private void addConnectionMutation(Network n){
		
		int index1 = randomNeuron(n, -1);
		int index2 = randomNeuron(n, Network.numInputs);
		
		if(index1 < Network.numInputs && index2 < Network.numInputs)
			return;
		
		Gene newConnection;
		
		if(index2 < Network.numInputs){
			int temp = index1;
			index1 = index2;
			index2 = temp;
		}
		
		newConnection = new Gene(index1, index2, random.nextDouble() * 4 - 2, true, 0);
		
		if(!genes.getGenome().contains(newConnection)){
			newConnection.innovation = getNextInnovation();
			genes.genome.add(newConnection);
		}
	}
	
	public void addNodeMutation(){
		
		Gene g = genes.getGenome().get((int)(random.nextDouble() * genes.getGenome().size()));
	
		if(g.isEnabled()){
			
			maxNeuron++;
			
			g.setEnabled(false);
			
			Gene gene1 = new Gene(g.getFrom(), maxNeuron, 1, true, getNextInnovation());
			Gene gene2 = new Gene(maxNeuron, g.getInto(), 1, true, getNextInnovation());
			
			genes.getGenome().add(gene1);
			genes.getGenome().add(gene2);
		}
	}
	
	public void weightsMutation(double chance, double step){
		for(Gene g: genes.getGenome()){
			if(random.nextDouble() <= chance){
				g.setWeight(g.getWeight() + (random.nextDouble() * 2 * step - step));
			}
		}
	}
	
	private void switchMutation(){
		int rand = (int)(random.nextDouble() * genes.getGenome().size());
		genes.getGenome().get(rand).setEnabled(!genes.getGenome().get(rand).isEnabled());
	}
	
	private int randomNeuron(Network n, int numInputs){
		//numInputs > 0 if inputs are not to be included
		
		ArrayList<Integer> neuronIndex = new ArrayList<Integer>();
		
		Neuron[] nArray = n.getNeurons();
		
		for(int i = 0; i < nArray.length; i++){
			if(nArray[i] != null){
				neuronIndex.add(i);
			}
		}
		
		int rand;
		if(numInputs < 0){
			rand = (int)(random.nextDouble() * neuronIndex.size());
		}else{
			rand = (int)(random.nextDouble() * (neuronIndex.size() - Network.numInputs)) + Network.numInputs;
		}
		
		return neuronIndex.get(rand);
		
	}
	
	public double getFitness(){
		return fitness;
	}
	
	public void setFitness(double fit){
		this.fitness = fit;
	}
	
	public void drawFootballer(Graphics2D g){
		g.setColor(c);
		g.fillOval((int)pos.getX() - radius, (int)pos.getY() - radius, (int)2 * radius, (int)2 * radius);
		g.setStroke(new BasicStroke(2));
		g.setColor(Color.BLACK);
		g.drawOval((int)pos.getX() - radius, (int)pos.getY() - radius, (int)2 * radius, (int)2 * radius);
	}
}
