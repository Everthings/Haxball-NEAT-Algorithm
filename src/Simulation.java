import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class Simulation extends JPanel{
	
	//private ArrayList<Footballer> population = new ArrayList<Footballer>();
	private ArrayList<Species> speciesInPool = new ArrayList<Species>();
	
	private final int FIELD_WIDTH;
	private final int FIELD_HEIGHT;
	
	private final int POPULATION_SIZE;
	
	private int generation = 1;
	private int timeToPlay = 10;//seconds
	private int refreshTime = 30;//miliseconds
	private int timeElapsed = 0;

	private double crossoverChance = 0.75;
	
	private float mutationRate = 0.05f;
	
	private long seed;
	
	private Match currentMatch;
	
	private final int PARALLEL = 0;
	private final int ITERATIVE = 1;
	private int Mode = PARALLEL;
	
	private int fIndex = 0;
	
	private int fieldOffset = 60;
	
	private double disjointWeight = 50;
	private double weightWeight = 0.4;
	private double speciesThreshold = 1;
	
	private int maxStaleness = 10;
	private double bestFitness;
	
	private JFrame display = new JFrame();
	private NetworkDisplay netDisplay = new NetworkDisplay();
	
	private Random random;
	
	Simulation(int width, int height, int populationSize, float mutationRate, long seed){
		this.FIELD_WIDTH = width;
		this.FIELD_HEIGHT = height;
		this.mutationRate = mutationRate;
		this.seed = seed;
		
		this.POPULATION_SIZE = populationSize;//must be factor of 2
		
		random = new Random(seed);
		
		setSize(width, height);
		setVisible(true);
		setBackground(new Color(0, 127, 42));
		
		initWindow();
	}
	
	public void initWindow(){
		display = new JFrame();
		display.setVisible(true);
		display.setTitle("Network");
		display.setSize(600, 450);
		display.setLocationRelativeTo(null); 
		display.setBackground(Color.WHITE); 
		display.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		display.setResizable(false);
	}

	public void newSimulation(){
		
		ArrayList<Footballer> children = new ArrayList<Footballer>();
		
		for(int i = 0; i < POPULATION_SIZE; i++){
			children.add(new Footballer(new Pair(FIELD_WIDTH/4 + fieldOffset, FIELD_HEIGHT/2 + fieldOffset), random));
			children.get(i).initGenome();
			children.get(i).n.initNetwork(children.get(i).genes);
			children.get(i).mutate(mutationRate, 2);
			children.get(i).n.initNetwork(children.get(i).genes);
		}
		
		for(Footballer c: children)
			addToSpecies(c);
		
		setupKeyBindings();
		
		netDisplay.setFootballer(children.get(0));
		display.add(netDisplay);
		
		new Thread(new Runnable(){

			@Override
			public void run() {
				runParallelMatches();
			}
			
		}).start();
	}
	
	public void runParallelMatches(){
	
		ArrayList<Footballer> population = new ArrayList<Footballer>();//inefficient
		for(Species s: speciesInPool)
			population.addAll(s.getMembers());
		
		for(int a = fIndex/20; a < POPULATION_SIZE/20; a++){
			
			Thread[] threads = new Thread[20];
			
			Match m = null;
			
			int index = 0;
			if(a == fIndex/20)
				index = fIndex;
			
			for(int i = index; i < 20; i++){
				
				if(a * 20 + i < POPULATION_SIZE){
					
					m = new Match(FIELD_WIDTH, FIELD_HEIGHT, population.get(a * 20 + i));

					Match match = m;
					Footballer f = population.get(a * 20 + i);
					
					threads[i] = new Thread(new Runnable(){
						
						int time = 0;
		
						@Override
						public void run() {
							while(time <= timeToPlay * 1000){
								match.runIteration();
								time += 35;
								
								if(match.goal)
									break;
							}
							
							match.updatePlayerStats(time);
							f.setPos(new Pair(FIELD_WIDTH/4 + fieldOffset, FIELD_HEIGHT/2 + fieldOffset));
							f.setVelocity(new Pair(0, 0));
						}
						
					});
					
					threads[i].start();
				}
				
			}

			for(Thread t: threads){
				if(t != null){
					try {
						t.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			if(m != null){
				currentMatch = m;
				netDisplay.setFootballer(currentMatch.player);
				display.repaint();
			}
			repaint();
		}

		currentMatch = null;
		newGeneration();

		if(Mode == PARALLEL)
			runParallelMatches();
		else
			runIterativeMatch();
	}
	
	public void runIterativeMatch(){

		JPanel p = this;
		
		new Thread(new Runnable(){

			@Override
			public void run() {
				
				ArrayList<Footballer> population = new ArrayList<Footballer>();//inefficient
				for(Species s: speciesInPool)
					population.addAll(s.getMembers());
				
				timeElapsed = 0;
				long time = System.currentTimeMillis();
				
				currentMatch = new Match(FIELD_WIDTH, FIELD_HEIGHT, population.get(fIndex));
				netDisplay.setFootballer(population.get(fIndex));
				//currentMatch.setupKeyBindings(p);
				while(timeElapsed < timeToPlay * 1000){
					if(System.currentTimeMillis() - time > 35){
						currentMatch.runIteration();
						time = System.currentTimeMillis();
						timeElapsed += 35;
						repaint();
						display.repaint();
						
						if(currentMatch.goal)
							break;
					}
				}
				
				currentMatch.updatePlayerStats(timeElapsed);
				fIndex++;
				
				if(fIndex > POPULATION_SIZE){
					currentMatch = null;
					newGeneration();
				}
				
				if(Mode == PARALLEL){
					runParallelMatches();
				}else{
					runIterativeMatch();
				}
			}
			
		}).start();
	}

	public void setupKeyBindings(){
		
		InputMap inMap = this.getInputMap(JComponent.WHEN_FOCUSED);
		this.requestFocus();
		
		KeyStroke sKey = KeyStroke.getKeyStroke('s');
			
		inMap.put(sKey, "switch");
		
		ActionMap actMap = this.getActionMap();
		
		actMap.put("switch", new AbstractAction(){

			@Override
			public void actionPerformed(ActionEvent e) {
				Mode = (Mode + 1) % 2;
			}

		});
	}
	
	public void updateGenerationStats(){
		//pre: receives a array of new Scores
		//post: adds the new scores to the scores array(running list of all scores) and returns the new player positions in the list(eg. return 1 for best score)
		
		BufferedWriter writer;
		
		try{
			if(!new File("Generation_Stats-" + seed + ".txt").exists()) { 
				writer = new BufferedWriter(new PrintWriter("Generation_Stats-" + seed + ".txt", "UTF-8"));
			}
			
			ArrayList<Footballer> footballers = rankFootballers();
			Species topOrganismOrigin = null;
			for(Species s: speciesInPool){
				if(s.getMembers().contains(footballers.get(footballers.size() - 1))){
					topOrganismOrigin = s;
					break;
				}
			}
			
			for(Species s: speciesInPool)
				calculateAverageFitness(s);

			writer = new BufferedWriter(new PrintWriter(new FileOutputStream(new File("Generation_Stats-" + seed + ".txt"), true)));
			writer.write("Generation: " + generation);
			writer.newLine();
			writer.write("Top Organism: " + footballers.get(footballers.size() - 1) + " of species " + topOrganismOrigin + " with fitness " + footballers.get(footballers.size() - 1).getFitness());
			writer.newLine();
			writer.write("Species: ");
			writer.newLine();
			for(int i = 0; i < Math.min(speciesInPool.size(), 100); i++){
				Species s = speciesInPool.get(i);
				writer.write((i + 1) + ": " + s + " - " + s.getMembers().size() + " members with averageFitness " + s.getAverageFitness());
				writer.newLine();
			}
			writer.newLine();
			writer.newLine();
			writer.newLine();
			
			writer.close();
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void sortSpecies(){
		speciesInPool.sort(new Comparator<Species>(){

			@Override
			public int compare(Species s1, Species s2) {
				
				double fit1 = s1.getAverageFitness();
				double fit2 = s2.getAverageFitness();
				
				if(fit1 > fit2)
					return -1;
				else if(fit1 < fit2)
					return 1;
				
				return 0;
			}
			
		});
	}
	
	public void newGeneration(){
		
		for(Species s: speciesInPool)
			calculateAverageFitness(s);
		
		sortSpecies();
		updateGenerationStats();
		removeStaleSpecies();
		cullSpecies(false);
		removeStaleSpecies();
		rankFootballers();
		
		removeWeakSpecies();
		double sum = totalAverageFitness();
		ArrayList<Footballer> children = new ArrayList<Footballer>();
		for(Species s: speciesInPool){
			int breed = (int) Math.floor(s.getAverageFitness() / sum * POPULATION_SIZE) - 1;
			for(int i = 0; i < breed; i++)
				children.add(breedChild(s));
		}
		
		cullSpecies(true);
		
		while(children.size() + speciesInPool.size() < POPULATION_SIZE){
			Species rand = speciesInPool.get((int)(random.nextDouble() * speciesInPool.size()));
			children.add(breedChild(rand));
		}
		
		for(Footballer c: children)
			addToSpecies(c);
		
		generation++;
		fIndex = 0;
		
	}
	
	public void addToSpecies(Footballer child){
		for(Species s: speciesInPool){
			if(isSameSpecies(child, s.getMember(0))){
				s.addMember(child);
				return;
			}
		}
		
		//if not found
		Species newSpecies = new Species();
		newSpecies.addMember(child);
		speciesInPool.add(newSpecies);		
	}
	
	public double disjoints(Footballer f1, Footballer f2){
		
		boolean[] g1 = new boolean[Footballer.innovationNumber + 1];
		for(int i = 0; i < f1.genes.getGenome().size(); i++){
			Gene g = f1.genes.getGenome().get(i);
			g1[g.innovation] = true;
		}
		
		boolean[] g2 = new boolean[Footballer.innovationNumber + 1];
		for(int i = 0; i < f2.genes.getGenome().size(); i++){
			Gene g = f2.genes.getGenome().get(i);
			g2[g.innovation] = true;
		}
		
		int disjointGenes = 0;
		for(int i = 0; i < f1.genes.getGenome().size(); i++){
			Gene g = f1.genes.getGenome().get(i);
			if(!g2[g.innovation])
				disjointGenes++;
		}

		for(int i = 0; i < f2.genes.getGenome().size(); i++){
			Gene g = f2.genes.getGenome().get(i);
			if(!g1[g.innovation])
				disjointGenes++;
		}
		
		int totalGenes = Math.max(f1.genes.getGenome().size(), f2.genes.getGenome().size());

		return (double)disjointGenes/totalGenes;
	}
	
	public double averageWeightDifference(Footballer f1, Footballer f2){
		Gene[] g1 = new Gene[Footballer.innovationNumber + 1];
		for(int i = 0; i < f1.genes.getGenome().size(); i++){
			Gene g = f1.genes.getGenome().get(i);
			g1[g.innovation] = g;
		}
		
		double sum = 0;
		int total = 0;
		
		for(int i = 0; i < f2.genes.getGenome().size(); i++){
			Gene g2 = f2.genes.genome.get(i);
			
			if(g1[g2.innovation] != null){
				sum += Math.abs(g2.weight - g1[g2.innovation].weight);
				total++;
			}
		}
		
		return sum/total;
	}
	
	public boolean isSameSpecies(Footballer f1, Footballer f2){
		double disjoint = disjointWeight * disjoints(f1, f2);
		double weights = weightWeight * averageWeightDifference(f1, f2);
		
		return disjoint + weights < speciesThreshold;
	}
	
	public Footballer crossOver(Footballer f1, Footballer f2){
		
		Footballer better;
		Footballer worser;
		
		if(f1.getFitness() < f2.getFitness()){
			better = f2;
			worser = f1;
		}else{
			better = f1;
			worser = f2;
		}
		
		Footballer child = new Footballer(new Pair(FIELD_WIDTH/4 + fieldOffset, FIELD_HEIGHT/2 + fieldOffset), random);
		ArrayList<Gene> genome = child.genes.getGenome();
		Gene[] innovations = new Gene[Math.max(better.innovationNumber + 1, worser.innovationNumber + 1)];
		
		for(int i = 0; i < worser.genes.getGenome().size(); i++){
			Gene g = worser.genes.getGenome().get(i);
			innovations[g.innovation] = g;
		}
		
		for(int i = 0; i < better.genes.getGenome().size(); i++){
			Gene g = better.genes.getGenome().get(i);
			Gene g2 = innovations[g.innovation];
			
			if(g2 != null && g2.isEnabled() && random.nextDouble() > 0.5)
				genome.add(g2.clone());
			else
				genome.add(g.clone());
		}
		
		child.maxNeuron = Math.max(better.maxNeuron, worser.maxNeuron);
		
		return child;
	}
	
	public Footballer breedChild(Species s){
		Footballer child;
		
		if(random.nextDouble() < crossoverChance){
			int rand1 = (int)(random.nextDouble() * s.getMembers().size());
			int rand2 = (int)(random.nextDouble() * s.getMembers().size());
			
			child = crossOver(s.getMember(rand1), s.getMember(rand2));
		}else{
			child = new Footballer(new Pair(FIELD_WIDTH/4 + fieldOffset, FIELD_HEIGHT/2 + fieldOffset), random);
			int rand = (int)(random.nextDouble() * s.getMembers().size());
			ArrayList<Gene> genomeCopy = (ArrayList<Gene>) s.getMember(rand).genes.genome;
			for(Gene g: genomeCopy)
				child.genes.genome.add(g.clone());
			child.maxNeuron = s.getMember(rand).maxNeuron;
		}
		
		child.n.initNetwork(child.genes);
		child.mutate(mutationRate, 2);
		child.n.initNetwork(child.genes);
		
		return child;
	}
	
	public void cullSpecies(boolean one){
		
		for(Species s: speciesInPool){
			s.getMembers().sort(new Comparator<Footballer>(){
				@Override
				public int compare(Footballer f1, Footballer f2) {
					if(f1.fitness > f2.fitness)
						return 1;
					else if(f1.fitness < f2.fitness)
						return -1;
					
					return 0;
				}
			});
			
			int remaining = (int) Math.ceil((double)s.getMembers().size()/2);
			
			if(one)
				remaining = 1;
			
			while(s.getMembers().size() > remaining)
				s.getMembers().remove(0);
		}
		
	}
	
	public ArrayList<Footballer> rankFootballers(){
		
		ArrayList<Footballer> population = new ArrayList<Footballer>();
		
		for(int i = 0; i < speciesInPool.size(); i++){
			population.addAll(speciesInPool.get(i).getMembers());
		}
		
		population.sort(new Comparator<Footballer>(){
			@Override
			public int compare(Footballer f1, Footballer f2) {
				if(f1.fitness > f2.fitness)
					return 1;
				else if(f1.fitness < f2.fitness)
					return -1;
				
				return 0;
			}
			
		});
		
		for(int i = 0; i < population.size(); i++){
			population.get(i).setRank(i + 1);
		}
		
		return population;
	}
	
	public void removeStaleSpecies(){
		ArrayList<Species> survived = new ArrayList<Species>();
		
		for(Species s: speciesInPool){
			
			s.getMembers().sort(new Comparator<Footballer>(){
				@Override
				public int compare(Footballer f1, Footballer f2) {
					if(f1.fitness > f2.fitness)
						return -1;
					else if(f1.fitness < f2.fitness)
						return 1;
					
					return 0;
				}
			});
			
			if(s.getMember(0).getFitness() > s.getTopFitness()){
				s.setTopFitness(s.getMember(0).getFitness());
				s.setStaleness(0);
			}else{
				s.setStaleness(s.getStaleness() + 1);
			}
			
			if(s.getStaleness() < maxStaleness || s.getTopFitness() >= bestFitness)
				survived.add(s);
		}
		
		speciesInPool = survived;
	}
	
	public void removeWeakSpecies(){
		ArrayList<Species> survived = new ArrayList<Species>();
		
		double sum = totalAverageFitness();
		for(Species s: speciesInPool){
			int breed = (int) Math.floor(s.getAverageFitness() / sum * POPULATION_SIZE);
			
			if(breed >= 1)
				survived.add(s);
		}
		
		speciesInPool = survived;
	}
	
	public void calculateAverageFitness(Species s){
		double total = 0;
		
		for(int i = 0; i < s.getMembers().size(); i++){
			total += s.getMember(i).rank;
		}
		
		s.setAverageFitness(total / s.getMembers().size());
	}
	
	public double totalAverageFitness(){
		double total = 0;
		
		for(Species s: speciesInPool){
			total += s.getAverageFitness();
		}
		
		return total;
	}
	

	public int getNumDigits(float value){//works accurately for only value > 1
		int num = 1;
		
		while(value >= Math.pow(10, num)){
			num++;
		}
			
		return num;
	}
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		try{
			g.setColor(Color.BLACK);
	
			if(currentMatch != null)
				currentMatch.draw(g);
			
			//generation number
			g.setColor(Color.BLACK);
			g.setFont(new Font("SomeFont", Font.PLAIN, 30));
			g.drawString("Generation: " + generation, 40, 32);
			g.setFont(new Font("SomeFont", Font.PLAIN, 20));
			g.drawString("Time Left: " + (timeToPlay - timeElapsed/1000), 40, 52);
			g.setFont(new Font("SomeFont", Font.PLAIN, 25));
			g.drawString("Number of Species: " + speciesInPool.size(), FIELD_WIDTH - 300 - getNumDigits(speciesInPool.size()) * 8, 40);

		}catch(Exception e){
			
		}
	}
}
