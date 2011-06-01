package labs.gis;

import java.util.ArrayList;

import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.graph.build.feature.FeatureGraphGenerator;
import org.geotools.graph.build.line.LineStringGraphGenerator;
import org.geotools.graph.structure.Edge;
import org.geotools.graph.structure.Node;
import org.geotools.graph.structure.basic.BasicEdge;
import org.geotools.graph.structure.basic.BasicNode;
import org.opengis.feature.Feature;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateFilter;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;

/*
	Strategy 1:
		1. Create graph from roads with nodes as intersects 
		2. Find nodes in a graph nearest to points A, B
 		3. Find all paths within given graph, from A to B
 		4. Examine each path if it suites your search parameters
 		
 */

public class TripPlanner {
	LineStringGraphGenerator gg = new LineStringGraphGenerator();
	
	@SuppressWarnings("unchecked")
	public void createGraph(FeatureCollection fc){
		FeatureIterator featureIterator = fc.features();
		
	    while( featureIterator.hasNext() ) {
	        Feature feature = (Feature) featureIterator.next();
	        MultiLineString multiLineString = (MultiLineString) feature.getDefaultGeometryProperty().getValue();

	        for ( int i = 0; i < multiLineString.getNumGeometries(); i++ ) {
	          LineString lineString = (LineString) multiLineString.getGeometryN(i);

	          gg.add( lineString );
	        }
	    }
	}

	@SuppressWarnings("unchecked")
	public boolean validByInnerTrips(PathInfo p, int min, int max){
		double costSoFar = 0;
		
		for (Object e : p.getPath().getEdges()){
			Edge ee = (Edge) e;
			
			Feature ft = (Feature) p.getStopsInfo().get(ee.getNodeB());
			costSoFar += ((LineString) ee.getObject()).getLength();
			
			if (ft != null){
				int costSoFarKM = (int) (costSoFar / 1000);
				
				if (costSoFarKM > max){
					System.out.println(p.getTitle() + " : " + costSoFar);
					return false;
				}
				
				// Remove node if its too near
				if (costSoFarKM < min){
					p.getStopsInfo().remove(ee.getNodeB());
					continue;
				}

				p.getStopsLengths().put(ft, costSoFar);
				costSoFar = 0;
			}
		}
		
		return true;
	}
	
	/*
	 *  Find node nearest to the given coordinate.
	 *  
	 *  Find nearest edge and add its node or something.
	 */
	public BasicNode nearestNode(Coordinate c){
		double min = Double.MAX_VALUE;
		double dist = 0;
		BasicNode minNode = null;
		
		/*
		for (Object n: gg.getGraph().getEdges()){
			LineString ln = (LineString) ((BasicEdge) n).getObject();
			ln.apply(new CoordinateFilter() {
				
				@Override
				public void filter(Coordinate coord) {
					
				}
			});
		}
		*/
		
		for (Object n : gg.getGraph().getNodes()){
			Point pt = (Point) ((BasicNode) n).getObject();
			dist = c.distance(pt.getCoordinate());
			
			if (dist < min){
				min = dist;
				minNode = (BasicNode) n;
			}
		}
		
		return minNode;
	}
}