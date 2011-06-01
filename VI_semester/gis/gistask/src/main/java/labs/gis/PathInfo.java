package labs.gis;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.graph.path.Path;
import org.geotools.graph.structure.Edge;
import org.geotools.graph.structure.Node;
import org.opengis.feature.Feature;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

public class PathInfo {
	private Path p;
	private double length;
	private String title = "";
	private Map stopsInfo = new Hashtable<Node, Feature>();
	private Map stopsLengths = new Hashtable<Feature, Double>();
	
	public PathInfo(Path p, double length, String title){
		this(p, length);
		this.setTitle(title);
	}

	public PathInfo(Path p, double length){
		this.setPath(p);
		this.setLength(length);
	}
	
	public void setPath(Path p) {
		this.p = p;
	}

	public Path getPath() {
		return p;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public double getLength() {
		return length;
	}

	public double getLengthKM(){
		return length / 1000;
	}

	public void setTitle(String title) {
		this.title = title;
	}


	public String getTitle() {
		return title;
	}

	public void setStopsInfo(Map stopsInfo) {
		this.stopsInfo = stopsInfo;
	}

	public Map getStopsInfo() {
		return stopsInfo;
	}
	
	/*
	 *  Set stopping information for given path. O(N*N)
	 */
	@SuppressWarnings("unchecked")
	public void updateStopsInfo(FeatureCollection features, int delta){
		FeatureIterator fi = features.features();
		ArrayList<Edge> list = (ArrayList<Edge>) getPath().getEdges();
		
		while(fi.hasNext()){
			Feature f = fi.next();
			Point pt = (Point) f.getDefaultGeometryProperty().getValue();

			for (Edge e : list){
				LineString ls = (LineString) e.getObject();

				if (ls.distance(pt.getEnvelope()) < delta){
					Node a = e.getNodeA();
					Node b = e.getNodeB();

					double dista = pt.getCoordinate().distance( ((Point) a.getObject()).getCoordinate()); 
					double distb = pt.getCoordinate().distance( ((Point) b.getObject()).getCoordinate());
					
					Node shortest = null;
					double dist = 0;

					if (dista < distb) {
						shortest = a;
						dist = dista;
					} else {
						shortest = b;
						dist = distb;
					}

					getStopsInfo().put(shortest, f);
					break;
				}
			}
		}

		fi.close();
	}

	public void setStopsLengths(Map stopsLengths) {
		this.stopsLengths = stopsLengths;
	}

	public Map getStopsLengths() {
		return stopsLengths;
	}
}
