package labs.psi.strategy;

import labs.psi.Solution;

public interface EvolutionaryAlgorithm {
	public Solution[] select(Population pop);
	public Solution[] createOffsprings(Population pop, Solution[] parents) throws CloneNotSupportedException;
	public void	   survive(Population pop, Solution[] children, Solution[] parents);
}
