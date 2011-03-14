package labs.psi;

public interface Solution {
	public int[] getGenome();
	public void  setGenome(int[] g);
	
	public int   getFitness();
	public void  setFitness(int f);
	
	public boolean valid();
	public int size();
	public Solution replicate() throws CloneNotSupportedException;
}
