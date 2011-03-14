package labs.psi;

import java.util.Arrays;
import java.util.Random;

public class EightQueensSolution implements Solution, Comparable<EightQueensSolution>, Cloneable {
	private int[] genotype;
	private int   henotype;
	private static int size = 8;
	
	public EightQueensSolution(){
		this(new int[size]);
	}
	
	public EightQueensSolution(int[] genotype){
		setGenome(genotype);
	}
	
	public int size(){
		return size;
	}
	
	@Override
	public void setGenome(int[] g) {
		this.genotype = g;
	}
	
	@Override
	public int[] getGenome() {
		return genotype;
	}
	
	@Override
	public void setFitness(int fitness) {
		this.henotype = fitness;
	}
	
	@Override
	public int getFitness() {
		return henotype;
	}

	public static Solution randomSolution() {
		Random generator = new Random();
		EightQueensSolution s = new EightQueensSolution();
		
		for (int i = 0; i < size; i++){
			s.genotype[i] = generator.nextInt(size);
		}
		return s;
	}
	
	public boolean valid(){
		int[] test = {0, 0, 0, 0, 0, 0, 0, 0};
		int[] g = getGenome();
		
		// horizontal test
		for (int i : g){
			test[i] += 1;
			if (test[i] > 1) return false;
		}
		
		// diagonal test
		int index, offset;
		for (int i = 0; i < size; i++){
			// checking left
			for (index = i-1, offset = 1; index > 0; index--, offset++)
				if (g[i] == g[index] + offset || g[i] == g[index] - offset) return false;
			
			// checking right
			for (index = i+1, offset = 1; index < size; index++, offset++)
				if (g[i] == g[index] + offset || g[i] == g[index] - offset) return false;
		}
		return true;
	}
	
	public int compareTo(EightQueensSolution other){
		return getFitness() - other.getFitness();
	}
	
	@Override
	public boolean equals(Object other){
		int costDifference = getFitness() - ((EightQueensSolution) other).getFitness();
		if (costDifference != 0) return false;
		return Arrays.equals(getGenome(), ((EightQueensSolution) other).getGenome());
	}
	
	@Override
	// Deep copy 
	public Object clone() throws CloneNotSupportedException {
		EightQueensSolution copy = (EightQueensSolution) super.clone();
		copy.setGenome(getGenome().clone());
		return copy;
	}

	@Override
	public Solution replicate() throws CloneNotSupportedException {
		return (Solution) clone();
	}
	
	public String toString(){
		String repr = "";
		for (int i = 0; i < size; i++) repr += getGenome()[i] + ",";
		return repr.substring(0, repr.length() - 1) + " cost: " + getFitness();
	}
}