package labs.gis;

import org.geotools.graph.path.DijkstraShortestPathFinder;
import org.geotools.graph.structure.Edge;
import org.geotools.graph.structure.Graph;
import org.geotools.graph.structure.basic.BasicNode;
import org.geotools.graph.traverse.standard.DijkstraIterator;
import org.geotools.graph.traverse.standard.DijkstraIterator.EdgeWeighter;

import com.vividsolutions.jts.geom.LineString;

public class DijkstraPathFinder {
	Graph g;
	EdgeWeighter weighter = new DijkstraIterator.EdgeWeighter() {
		@Override
		public double getWeight(Edge e) {
			return ((LineString) e.getObject()).getLength();
		}
	};
	
	public DijkstraPathFinder(Graph g){
		this.g = g;
	}
	
	public PathInfo path(BasicNode source, BasicNode dest){
		DijkstraShortestPathFinder pf = new DijkstraShortestPathFinder(this.g, source, this.weighter);
		pf.calculate();
		return new PathInfo(pf.getPath(dest), pf.getCost(dest), "Shortest path");
	}
}
