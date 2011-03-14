package labs.psi;

public class Main {
	public static void main(String[] args) throws CloneNotSupportedException{
		//Population population = new GA8QueensPopulation(1000, 500, true, false);
		Population population = new EP8QueensPopulation(1000, 700, true, false);
		population.Evolve(1000, 100);
	}
}
