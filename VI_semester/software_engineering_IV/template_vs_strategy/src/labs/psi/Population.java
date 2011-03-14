package labs.psi;

import java.util.ArrayList;

public abstract class Population {
	private int size;
	private int offspringCount;
	protected int solutionsToFind = 1;
	private boolean isMutated; 
	private boolean isBred;
	protected boolean recentlyFound = false;
	
	protected Solution[] population;
	protected ArrayList<Solution> solutions = new ArrayList<Solution>();
	
	private double averageFitness = 0.0;
	private long 	 minFitness = Long.MAX_VALUE;
	private long 	 maxFitness = Long.MIN_VALUE;
	private long   cost 		= 0;
	
	public Population(int size, int offspringCount, boolean isMutated, boolean isBred){
		setBred(isBred);
		setMutated(isMutated);
		setSize(size);
		setOffspringCount(offspringCount);
		setPopulation(new Solution[size]);
	}
	
	protected abstract void		survive(Solution[] s1, Solution[] s2);
	protected abstract Solution[] select();
	protected abstract int 		fitness(Solution s);
	protected abstract void 		init();
	protected abstract Solution[] createOffsprings(Solution[] s) throws CloneNotSupportedException;
		
	public void setBred(boolean isBred) {
		this.isBred = isBred;
	}
	
	public void setMutated(boolean isMutated) {
		this.isMutated = isMutated;
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
	
	public long getMaxFitness() {
		return maxFitness;
	}
	
	public long getMinFitness() {
		return minFitness;
	}
	
	public boolean isBred() {
		return isBred;
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
	
	public void Evolve(int maxIterations, int solutionsToFind) throws CloneNotSupportedException{
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
}