import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JPanel;


public class Network{
	
	static int numInputs = 365;
	static int numOutputs = 5;
	static int maxHidden = 300;
	
	Neuron[] neurons;
	
	public Neuron[] getNeurons(){
		return neurons;
	}
	
	public void initNetwork(Genome genes){
		
		neurons = new Neuron[numInputs + maxHidden + numOutputs];
		
		for(int i = 0; i < numInputs; i++){
			neurons[i] = new Neuron();
		}
		
		for(int i = 0; i < numOutputs; i++){
			neurons[numInputs + maxHidden + i] = new Neuron();
		}
		
		genes.getGenome().sort(new Comparator<Gene>(){

			@Override
			public int compare(Gene g1, Gene g2) {
				return g1.getInto() - g2.getInto();
			}
			
		});
		
		for(Gene g: genes.getGenome()){
			if(g.isEnabled()){
				
				if(neurons[g.getInto()] == null){
					neurons[g.getInto()] = new Neuron();
				}
				
				neurons[g.getInto()].add(g);
				
				if(neurons[g.getFrom()] == null){
					neurons[g.getFrom()] = new Neuron();
				}
				
			}
		}
	}	
	
	public double[] evalNetwork(double[] inputs){
		
		for(int i = 0; i < numInputs; i++){
			neurons[i].setValue(inputs[i]);
		}
		
		for(int i = inputs.length; i < neurons.length; i++){
			Neuron n = neurons[i];
			
			if(n != null){
				long sum = 0;
				
				for(Gene g: n.getIncoming()){
					sum += neurons[g.from].getValue() * g.getWeight();
				}
				
				n.setValue(sigmoid(sum));
			}
		}
		
		double[] outputs = new double[numOutputs];
		
		for(int i = 0; i < outputs.length; i++)
			outputs[i] = neurons[i + numInputs + maxHidden].getValue();
		
		return outputs;
	}
	
	public double sigmoid(double input){
		
		double ret = (double)1/(double)(1 + Math.pow(Math.E, -input));
		
		return ret;
	}
	
}