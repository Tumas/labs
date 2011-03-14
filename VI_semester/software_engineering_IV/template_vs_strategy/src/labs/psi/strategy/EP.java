package labs.psi.strategy;

import labs.psi.EightQueensSolution;
import labs.psi.Solution;

public class EP implements EvolutionaryAlgorithm {
	private int[] indexes;
	
	@Override
	public Solution[] createOffsprings(Population pop, Solution[] parents) throws CloneNotSupportedException {
		Solution[] offsprings = new EightQueensSolution[parents.length];

		for (int i = 0; i < parents.length; i++){
			offsprings[i] = parents[i].replicate();
			pop.mutate(offsprings[i]);
		}
		return offsprings;
	}

	@Override
	public Solution[] select(Population pop) {
		indexes = new int[pop.getOffspringCount()];
		
		// randomly select m parents
		Solution[] selected = new Solution[pop.getOffspringCount()];
		int index;
		
		for (int i = 0; i < pop.getOffspringCount(); i++) {
			index = pop.getGenerator().nextInt(pop.getSize());
			selected[i] = pop.getPopulation()[index];
			indexes[i] = index;
		}
		return selected;
	}

	@Override
	public void survive(Population pop, Solution[] children, Solution[] parents) {
		for (int i = 0; i < children.length; i++){
			pop.getPopulation()[indexes[i]] = children[i].getFitness() < parents[i].getFitness() ?
					children[i] : parents[i];
		}
	}
}
