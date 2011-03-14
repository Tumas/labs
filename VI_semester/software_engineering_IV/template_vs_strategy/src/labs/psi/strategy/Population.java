package labs.psi.strategy;

import java.util.ArrayList;
import java.util.Random;

import labs.psi.Solution;

public abstract class Population {
	// configuration
	private int size;
	private int offspringCount;
	private boolean isMutated; 
	private boolean isRecombined;

	// statistics
	private double averageFitness = 0.0;
	private long 	 minFitness = Long.MAX_VALUE;
	private long 	 maxFitness = Long.MIN_VALUE;
	private long   cost 		= 0;

	protected int solutionsToFind = 1;
	protected boolean recentlyFound = false;
	protected Solution[] population;
	protected ArrayList<Solution> solutions = new ArrayList<Solution>();
	protected Random generator = new Random();

	// strategy
	protected EvolutionaryAlgorithm evo; 
	
	public Population(EvolutionaryAlgorithm evo,
			int size, int offspringCount, boolean isMutated, boolean isRecombined){
		setEvo(evo);
		setRecombined(isRecombined);
		setMutated(isMutated);
		setSize(size);
		setOffspringCount(offspringCount);
		setPopulation(new Solution[size]);
	}
	
	protected abstract int 		fitness(Solution s);
	protected abstract void 		init();
	protected abstract void      mutate(Solution s);
		
	public void setRecombined(boolean isRecombined) {
		this.isRecombined = isRecombined;
	}
	
	public void setMutated(boolean isMutated) {
		this.isMutated = isMutated;
	}
	
	public void setEvo(EvolutionaryAlgorithm evo) {
		this.evo = evo;
	}
	
	public void setOffspringCount(int offspringCount) {
		this.offspringCount = offspringCount;
	}
	
	public void setSize(int size) {
		this.size = size;
	}
	
	public int getOffspringCount() {
		return offspringCount;
	}
	
	public int getSize() {
		return size;
	}
	
	public double getAverageFitness() {
		return averageFitness;
	}
	
	public long getCost() {
		return cost;
	}
	
	public EvolutionaryAlgorithm getEvo() {
		return evo;
	}
	public long getMaxFitness() {
		return maxFitness;
	}
	
	public long getMinFitness() {
		return minFitness;
	}
	
	public Random getGenerator() {
		return generator;
	}
	
	public boolean isRecombined() {
		return isRecombined;
	}
	
	public boolean mutationOccurs() {
		return isMutated;
	}

	public void setPopulation(Solution[] population) {
		this.population = population;
	}
	
	public Solution[] getPopulation() {
		return population;
	}

	private void calculateStats(){
		cost = 0;
		maxFitness = Long.MIN_VALUE;
		minFitness = Long.MAX_VALUE;
		
		int fit = 0;
		for (Solution s : getPopulation()){
			fit = s.getFitness();
			cost += fit;
			
			if (fit > maxFitness) maxFitness = fit;
			if (fit < minFitness) minFitness = fit;
		}
		
		averageFitness = ((double) cost) / getSize();
	}
	
	public void evolve(int maxIterations, int solutionsToFind) throws CloneNotSupportedException{
		this.solutionsToFind = solutionsToFind;
		int solutionsFound = 0;
		int generation = 0;
		
		init();
		evaluate(getPopulation());
		
		while (solutionsFound != solutionsToFind && generation != maxIterations){
			generation += 1;
			calculateStats();
			
			System.out.println("Gen: " + generation + " Cost: " + getCost() + " Max: " + 
					getMaxFitness() + " Min: " + getMinFitness() + " Avg: " + getAverageFitness());
		
			Solution[] parents = select();         
			Solution[] children = createOffsprings(parents);
			evaluate(children); 
			survive(children, parents);
			
			int currentSolutions = recordSolutions();
			solutionsFound += currentSolutions;
			recentlyFound = currentSolutions != 0;
		}
		
		System.out.println("Solution found: " + solutionsFound);
		for (Solution s : solutions)
			System.out.println("Solution: " + s);
	}
	
	protected void evaluate(Solution[] solutions){
		for (Solution s : solutions) s.setFitness(fitness(s));
	}	
	
	private int recordSolutions() throws CloneNotSupportedException{
		int count = 0;
		for (Solution s : getPopulation()){
			if (s.valid() && !solutions.contains(s)){
				count++;
				solutions.add(s.replicate());
			}
		}
		return count;
	}
	
	// Delegating to strategy
	protected void		survive(Solution[] s1, Solution[] s2){
		getEvo().survive(this, s1, s2);
	}
	
	protected Solution[] select(){
		return getEvo().select(this);
	}
	
	protected Solution[] createOffsprings(Solution[] parents) throws CloneNotSupportedException {
		return getEvo().createOffsprings(this, parents);
	}
}