package labs.psi.strategy;

import java.util.Arrays;

import labs.psi.EightQueensSolution;
import labs.psi.Solution;

public class GA implements EvolutionaryAlgorithm {

	@Override
	public Solution[] createOffsprings(Population pop, Solution[] parents) {
		int offspringsCount = pop.getOffspringCount();
		Solution[] offsprings = new EightQueensSolution[offspringsCount];
		
		// elitism : only best individuals can breed
		Arrays.sort(parents);
		
		for (int i = 0; i < offspringsCount; i++){
			offsprings[i] = crossOver(pop, parents[i], parents[(i+1) % offspringsCount]);
			if (pop.mutationOccurs()) pop.mutate(offsprings[i]);
		}
		return offsprings;
	}

	@Override
	public Solution[] select(Population pop) {
		return pop.getPopulation();
	}

	@Override
	public void survive(Population pop, Solution[] children, Solution[] parents) {
		Solution[] survivors = new EightQueensSolution[pop.getSize()];
		
		int i = 0;
		for (; i < pop.getSize() && i < children.length; i++)  survivors[i] = children[i];
		for (int j = 0; i < pop.getSize() && j < parents.length; i++, j++) survivors[i] = parents[j];
		pop.setPopulation(survivors);
	}
	
	protected Solution crossOver(Population pop, Solution s1, Solution s2) {
		int pos = pop.getGenerator().nextInt(s1.size());
		int[] newGenome = new int[s1.size()];
		
		for (int i = 0; i < s1.size(); i++)
			newGenome[i] = i < pos ? s1.getGenome()[i] : s2.getGenome()[i];
		return new EightQueensSolution(newGenome);
	}
}
