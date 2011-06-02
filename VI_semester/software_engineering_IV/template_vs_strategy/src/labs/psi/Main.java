package labs.psi;

import labs.psi.strategy.EV;
import labs.psi.strategy.GA;
import labs.psi.template.EP8QueensPopulation;
import labs.psi.template.GA8QueensPopulation;
import labs.psi.template.Population;

public class Main {
	public static void main(String[] args) throws CloneNotSupportedException{
		int population_size   = 1000;
		int children          =   500;
		int max_generations   =  1000;
		int solutionsCount    =   100;
		boolean isMutated     = true;
		boolean isRecombined  = true;
		
		
		/* Template variant */

		//labs.psi.template.Population pop;
		//pop = new GA8QueensPopulation(population_size, children, isMutated, isRecombined);
		//pop = new EP8QueensPopulation(population_size, children, isMutated, isRecombined);
		//pop.evolve(max_generations, solutionsCount);

		/* Strategy variant */

		labs.psi.strategy.Population pop2;
		pop2 = new labs.psi.strategy.EightQueensPopulation(new GA(), population_size, children, isMutated, isRecombined);
		//pop2 = new labs.psi.strategy.EightQueensPopulation(new EV(), population_size, children, isMutated, isRecombined);
		
		pop2.evolve(max_generations, solutionsCount);
	}
}
