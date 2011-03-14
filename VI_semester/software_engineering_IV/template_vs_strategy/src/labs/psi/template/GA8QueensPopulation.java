package labs.psi.template;

import java.util.Arrays;

import labs.psi.EightQueensSolution;
import labs.psi.Solution;

public class GA8QueensPopulation extends EightQueensPopulation {
	public GA8QueensPopulation(int size, int offspringCount, boolean isMutated,
			boolean isBred) {
		super(size, offspringCount, isMutated, isBred);
	}

	@Override
	protected Solution[] select() {
		return getPopulation();
	}

	@Override
	protected Solution[] createOffsprings(Solution[] parents) {
		Solution[] offsprings = new EightQueensSolution[getOffspringCount()];
		
		// elitism : only best individuals can breed
		Arrays.sort(parents);
		
		for (int i = 0; i < getOffspringCount(); i++){
			offsprings[i] = crossOver(parents[i], parents[(i+1) % getOffspringCount()]);
			if (mutationOccurs()) mutate(offsprings[i]);
		}
		return offsprings;
	}

	@Override
	protected void survive(Solution[] s1, Solution[] s2) {
		Solution[] survivors = new EightQueensSolution[getSize()];
		
		int i = 0;
		for (; i < getSize() && i < s1.length; i++)  survivors[i] = s1[i];
		for (int j = 0; i < getSize() && j < s2.length; i++, j++) survivors[i] = s2[j];
		setPopulation(survivors);
	}
	
	protected Solution crossOver(Solution s1, Solution s2) {
		int pos = generator.nextInt(s1.size());
		int[] newGenome = new int[s1.size()];
		
		for (int i = 0; i < s1.size(); i++)
			newGenome[i] = i < pos ? s1.getGenome()[i] : s2.getGenome()[i];
		return new EightQueensSolution(newGenome);
	}
}
