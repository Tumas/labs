package labs.gis;

import org.geotools.graph.path.Path;
import org.geotools.graph.structure.Graph;
import org.geotools.graph.structure.basic.BasicNode;

/*
 *  Custom stocashtic pathfinder. 
 *  Does not guarantee any results, however its possible to use it with trip planning, where
 *  	optimization isn't important.
 */

public class StochasticPathFinder {
	Graph g; 
	
	public StochasticPathFinder(Graph g){
		this.g = g;
	}
	
	public Path path(BasicNode source, BasicNode dest){
		return null;
	}
}