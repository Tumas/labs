package labs.psi.strategy;

import labs.psi.EightQueensSolution;
import labs.psi.Solution;

public class EightQueensPopulation extends Population {
	public EightQueensPopulation(EvolutionaryAlgorithm evo, int size, int offspringCount,
			boolean isMutated, boolean isRecombined) {
		super(evo, size, offspringCount, isMutated, isRecombined);
	}
	
	@Override
	protected void init(){
		for (int i = 0; i < getSize(); i++)
			population[i] = EightQueensSolution.randomSolution();
	}
	
	@Override
	protected int fitness(Solution s){
		int conflictingPositions = 0;
		int[] test = {0, 0, 0, 0, 0, 0, 0, 0};
		int[] g = s.getGenome();
		
		for (int i : g) test[i] += 1;		
		for (int i : test)  if (i > 1) conflictingPositions += 1;
		
		int index, offset;
		for (int i = 0; i < s.size(); i++){
			// checking left
			for (index = i-1, offset = 1; index > 0; index--, offset++)
				if (g[i] == g[index] + offset || g[i] == g[index] - offset) conflictingPositions++;
			
			// checking right
			for (index = i+1, offset = 1; index < s.size(); index++, offset++)
				if (g[i] == g[index] + offset || g[i] == g[index] - offset) conflictingPositions++;
		}
		
		return conflictingPositions;
	}
	
	@Override
	protected void mutate(Solution s){	
		int[] genome = s.getGenome();
		int pos = generator.nextInt(s.size());
		
		int delta = generator.nextInt(2) + 1;
		if (getMinFitness() == 1 && !recentlyFound) delta = 1;
		
		genome[pos] += (generator.nextBoolean() ? +delta : -delta);
		genome[pos] %= s.size();
		genome[pos] = Math.abs(genome[pos]);
	}
}