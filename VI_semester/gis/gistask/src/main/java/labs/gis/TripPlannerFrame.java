package labs.gis;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

import labs.gis.AppG.GeomType;

import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.graph.path.Path;
import org.geotools.graph.structure.Edge;
import org.geotools.graph.structure.basic.BasicNode;
import org.geotools.map.MapContext;
import org.geotools.swing.action.SafeAction;
import org.opengis.feature.Feature;
import org.opengis.filter.identity.FeatureId;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;

public class TripPlannerFrame extends JFrame {
	private AppG parent;
	private MapContext mapContext;
	private TripPlannerFrame me;

	// A and B points 
	private JComboBox sourceObject = new JComboBox();
	private JComboBox destObject = new JComboBox();
	
	// length parameters
	private JComboBox tripLength = new JComboBox();
	private JComboBox minDayTrip = new JComboBox();
	private JComboBox maxDayTrip = new JComboBox();

	// yes-no 
	private JComboBox sameRoute = new JComboBox();
	private JComboBox includePeaks = new JComboBox();
	private JComboBox includeRivers = new JComboBox();
	private JComboBox includeLakes = new JComboBox();
	
	//deltas
	private int tripLengthDelta = 20;
	private int tripBetweenDelta = 10;
	
	private String noConstraints = "All";
	
	@SuppressWarnings("serial")
	public TripPlannerFrame(final AppG parent) throws CQLException, IOException{
		this.parent = parent;
		this.mapContext = parent.getFrame().getMapContext();
		this.me = this;
		
		getContentPane().setLayout(new GridLayout(10, 2, 0, 5));
		
		sourceObject.setModel(new DefaultComboBoxModel(getSelectModelForObjects(GeomType.POINT, "gyvenvie", "GYVVARDAS")));
		destObject.setModel(new DefaultComboBoxModel(getSelectModelForObjects(GeomType.POINT, "gyvenvie", "GYVVARDAS")));
	
		Vector<String> vs = getTripLengths(5, 1000, tripLengthDelta);
		vs.add(0,  noConstraints);
		
		tripLength.setModel(new DefaultComboBoxModel(vs));
		minDayTrip.setModel(new DefaultComboBoxModel(getTripLengths(5, 100, tripBetweenDelta)));
		maxDayTrip.setModel(new DefaultComboBoxModel(getTripLengths(5, 100, tripBetweenDelta)));
		
		sameRoute.setModel(new DefaultComboBoxModel(new Object[]{"Yes", "No"}));
		includePeaks.setModel(new DefaultComboBoxModel(new Object[]{"Yes", "No"}));
		includeRivers.setModel(new DefaultComboBoxModel(new Object[]{"Yes", "No"}));
		includeLakes.setModel(new DefaultComboBoxModel(new Object[]{"Yes", "No"}));
		
		getContentPane().add(new JLabel("Source: "));
		getContentPane().add(sourceObject);
		
		getContentPane().add(new JLabel("Destination: "));
		getContentPane().add(destObject);
		
		getContentPane().add(new JLabel("Approximate trip length: "));
		getContentPane().add(tripLength);
		
		getContentPane().add(new JLabel("Minimal trip length between stops: "));
		getContentPane().add(minDayTrip);
		
		getContentPane().add(new JLabel("Maximum trip length between stops: "));
		getContentPane().add(maxDayTrip);
	
		/*
		getContentPane().add(new JLabel("Take the same route twice? "));
		getContentPane().add(sameRoute);
		*/
		
		getContentPane().add(new JLabel("Include peaks in a trip?"));
		getContentPane().add(includePeaks);

		getContentPane().add(new JLabel("Include rivers in a trip?"));
		getContentPane().add(includeRivers);

		getContentPane().add(new JLabel("Include lakes in a trip?"));
		getContentPane().add(includeLakes);
		
		getContentPane().add(new JButton(new SafeAction("Find Trips") {
			@Override
			public void action(ActionEvent arg0) throws Throwable {
				validateOptions();
				TripPlanner tp = new TripPlanner();
				
				// TODO:
				// Would be NICE to have: 
				//   2. stochastic search implementation
				//	 3. proper styling
				//	 4. caching

				// CRITICAL:
				//	 * Include Peaks, Rivers, Lakes in your journey
				//	 * Stops info should include both A and B points
				
				// NEEDED:
				//	 8. Customizable Deltas for searchable objects
				// 	 9. More accurrate FROM and TO points binding
				
				// step 1 : create graph
				tp.createGraph(parent.getSelectedObjectsByGeometry(GeomType.LINE, "keliai"));
				
				// step 2: find nearest nodes
				Feature a = parent.getSelectedFeatureByType(GeomType.POINT, "gyvenvie", "GYVVARDAS", sourceObject.getSelectedItem().toString());
				Feature b = parent.getSelectedFeatureByType(GeomType.POINT, "gyvenvie", "GYVVARDAS", destObject.getSelectedItem().toString());
				
				Coordinate ac = ((Point) a.getDefaultGeometryProperty().getValue()).getCoordinate();
				Coordinate bc = ((Point) b.getDefaultGeometryProperty().getValue()).getCoordinate();

				BasicNode na = tp.nearestNode(ac);
				BasicNode nb = tp.nearestNode(bc);

				// step 3:  Find "all" paths within given graph, from A to B
				ArrayList<PathInfo> pathsWithInfo = null;
				
				// Shortest-path strategy
				PathInfo shortest = new DijkstraPathFinder(tp.gg.getGraph()).path(na, nb);
				
				String val = (String) tripLength.getSelectedItem();
				int requestedTotalLength = val.equals(noConstraints) ? -1 : Integer.parseInt(val);
				Path pathToInspect = shortest.getPath();
				
				if (requestedTotalLength != -1){
					// IF shortest path does not exist or shortest path is too long 
					// do not attempt any other strategies
					if (pathToInspect == null){
						System.out.println("Path between nodes does not exists. Try selecting wider area.");
						throw new Exception();
					}

					if ((shortest.getLength() / 1000) - tripLengthDelta > requestedTotalLength){
						System.out.println("Shortest path is already to long for your journey:\n\t Shortest path: " +
								shortest.getLength() + "\n\t You requested: " + requestedTotalLength);
						throw new Exception();
					} 
				}

				// Exhaustive strategy
				int scale = 100;
				if (shortest != null) scale = (int) shortest.getLength();
				pathsWithInfo = new ExhaustivePathFinder().paths(na, nb, scale);
				pathsWithInfo.add(shortest);
				
				// Stochastic strategy 
				// TODO
				
				// step 4: Examine each path if it suites your search parameters
				ArrayList<PathInfo> selectedPaths = new ArrayList<PathInfo>();
				FeatureCollection fc = parent.getSelectedObjectsByGeometry(GeomType.POINT, "gyvenvie");
				
				for (PathInfo p : pathsWithInfo){
						
					// Total length filter
					double actualTripLength = p.getLengthKM();
					System.out.println(requestedTotalLength);
					
					if ((requestedTotalLength != -1) && ((actualTripLength < requestedTotalLength - tripLengthDelta) 
							|| (actualTripLength > requestedTotalLength + tripLengthDelta))){
						System.out.println("path : " + p.getTitle() + " REJECTED : trip length " + p.getLength());
						continue;
					}
	
					// Filter between stops 
					int requestedInnerMin = Integer.parseInt((String) minDayTrip.getSelectedItem());
					int requestedInnerMax = Integer.parseInt((String) maxDayTrip.getSelectedItem());
					
					p.updateStopsInfo(fc, 1000);
					if (requestedTotalLength == -1) {
						requestedInnerMax = Integer.MAX_VALUE;
						requestedInnerMin = 0;
					}
					
					if (!tp.validByInnerTrips(p, requestedInnerMin, requestedInnerMax)) {
						System.out.println("path : " + p.getTitle() + " REJECTED : because of inner trip length ");
						continue;
					}
						
					selectedPaths.add(p);
				}
				
				// step 5: Result display:
				if (selectedPaths == null || selectedPaths.isEmpty()){
					System.out.println("Could not find any path with given parameters. Try adjusting parameter values " +
							"or selecting greater region.");
					throw new IOException();
				}
				else {
					new PathBrowserFrame(me, parent, selectedPaths, a, b).setVisible(true);
				}
			}
        }));
		
		pack();
	}

	public Vector<String> getObjectsList(){
		Vector<String> v = new Vector<String>();
		v.add("keliai");
		v.add("gyvenvie");
		
		if (includePeaks.getSelectedItem().equals("Yes")) v.add("virsukalnes");
		if (includeRivers.getSelectedItem().equals("Yes")) v.add("upes");
		if (includeLakes.getSelectedItem().equals("Yes")) v.add("ezerai");
			
		return v;
	}
	
	/*
	 * O(N*N) 
	 */
	@SuppressWarnings("unchecked")
	public static ArrayList<Feature> listFeaturesInPath(AppG app, PathInfo path, GeomType gType, String name){
		FeatureCollection fc = app.getSelectedObjectsByGeometry(gType, name);
		FeatureIterator fi = fc.features();

		ArrayList<Feature> features = new ArrayList<Feature>(); 

		// TODO: refactor me -> branching and hard-coded layer names >_<
		
		if (name.equals("keliai")){
			// find all roads that make up the path
			ArrayList<Edge> list = (ArrayList<Edge>) path.getPath().getEdges();
		
			while(fi.hasNext()){
				Feature f = fi.next();
				for (Edge e : list){
					LineString ls = (LineString) e.getObject();
					MultiLineString mls = (MultiLineString) f.getDefaultGeometryProperty().getValue();
					if (mls.equals(ls.getGeometryN(0))){
						features.add(f);
						break;
					}
				}
			}
		} else if (name.equals("gyvenvie")) {
			Collection c = path.getStopsInfo().values();
			for (Object item : c) {
				features.add((Feature) item);
			}
		}
		
		fi.close();
		return features;
	}
	
	/* 
	 * remove duplicates from feature list
	 */
	public static Set<FeatureId> featuresInPath(ArrayList<Feature> features){
		Set<FeatureId> featIDs = new HashSet<FeatureId>();
		for (Feature f : features) featIDs.add(f.getIdentifier());
		return featIDs;
	}

	private void validateOptions() throws IOException{
		String test = null;
		String test2 = null;
				
		test = (String) sourceObject.getSelectedItem();
		test2 = (String) destObject.getSelectedItem();

		if (test.equals(test2))
			throw new IOException("Not yet supported");
		
		if (test.isEmpty() || test2.isEmpty())
			throw new IOException("Empty start and/or finish points");

		test = (String) minDayTrip.getSelectedItem();
		test2 = (String) maxDayTrip.getSelectedItem();
		
		if (Integer.parseInt(test) > Integer.parseInt(test2))
			throw new IOException("Minimal day trip length > maximum day trip length");
	}
	
	private Vector<String> getTripLengths(int min, int max, int step) {
		Vector<String> v = new Vector<String>();
		for (Integer i = min; i < max; i += step){
			v.add(i.toString());
		}
		return v;
	}

	@SuppressWarnings("unchecked")
	private Vector<String> getSelectModelForObjects(GeomType type, String layerName, String id) throws IOException {
		Vector<String> v = new Vector<String>();
		FeatureCollection ft = parent.getSelectedObjectsByGeometry(type, layerName);
		FeatureIterator iter = ft.features();
		
		if (ft.getSchema().getDescriptor(id) == null){
			throw new IOException("Bad descriptor : " + id);
		}
		
		try {
			while (iter.hasNext()) {
				v.add(iter.next().getProperty(id).getValue().toString());
			}
		} finally {
			iter.close();
		}
		
		return v;
	}
}