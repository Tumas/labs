package labs.psi;

public class EP8QueensPopulation extends EightQueensPopulation {
	// temporary place to store indexes
	private int[] indexes = new int[getOffspringCount()];
	
	public EP8QueensPopulation(int size, int offspringCount, boolean isMutated,
			boolean isBred) {
		super(size, offspringCount, isMutated, isBred);
	}

	@Override
	protected Solution[] select() {
		// randomly select m parents
		Solution[] selected = new Solution[getOffspringCount()];
		int index;
		
		for (int i = 0; i < getOffspringCount(); i++) {
			index = generator.nextInt(getSize());
			selected[i] = getPopulation()[index];
			indexes[i] = index;
		}
		return selected;
	}

	@Override
	protected Solution[] createOffsprings(Solution[] parents) throws CloneNotSupportedException {
		Solution[] offsprings = new EightQueensSolution[parents.length];

		for (int i = 0; i < parents.length; i++){
			offsprings[i] = parents[i].replicate();
			mutate(offsprings[i]);
		}
		return offsprings;
	}

	@Override
	protected void survive(Solution[] children, Solution[] parents) {
		for (int i = 0; i < children.length; i++){
			population[indexes[i]] = children[i].getFitness() < parents[i].getFitness() ?
					children[i] : parents[i];
		}
	}
}
