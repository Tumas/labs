package labs.gis;

import java.util.ArrayList;

import org.geotools.graph.path.Path;
import org.geotools.graph.structure.Edge;
import org.geotools.graph.structure.basic.BasicNode;

import com.vividsolutions.jts.geom.LineString;

public class ExhaustivePathFinder {
	public ArrayList<PathInfo> paths(BasicNode source, BasicNode dest, int scale){
		org.geotools.graph.path.ExhaustivePathFinder ep = new org.geotools.graph.path.ExhaustivePathFinder(1000, 100);

		ArrayList<PathInfo> pathsWithInfo = new ArrayList<PathInfo>();
		int index = 0;
		
		for (Object p : ep.getPaths(source, dest)){
			Path pp = (Path) p;
			pathsWithInfo.add(new PathInfo(pp, sumPath(pp), "Exhaustive " + index++));
		}
		
		return pathsWithInfo;
	}
	
	private double sumPath(Path p){
		double sum = 0;

		for (Object e : p.getEdges())
			sum += ((LineString) ((Edge) e).getObject()).getLength();

		return sum;
	}
}