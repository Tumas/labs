package labs.gis;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
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
import org.geotools.graph.path.DijkstraShortestPathFinder;
import org.geotools.graph.path.Path;
import org.geotools.graph.structure.Edge;
import org.geotools.graph.structure.basic.BasicNode;
import org.geotools.graph.traverse.standard.DijkstraIterator;
import org.geotools.graph.traverse.standard.DijkstraIterator.EdgeWeighter;
import org.geotools.map.MapContext;
import org.geotools.map.MapLayer;
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
	
	@SuppressWarnings("serial")
	public TripPlannerFrame(final AppG parent) throws CQLException, IOException{
		this.parent = parent;
		this.mapContext = parent.getFrame().getMapContext();
		
		getContentPane().setLayout(new GridLayout(10, 2, 0, 5));
		
		sourceObject.setModel(new DefaultComboBoxModel(getSelectModelForObjects(GeomType.POINT, "gyvenvie", "GYVVARDAS")));
		destObject.setModel(new DefaultComboBoxModel(getSelectModelForObjects(GeomType.POINT, "gyvenvie", "GYVVARDAS")));

		tripLength.setModel(new DefaultComboBoxModel(getTripLengths(5, 1000, 20)));
		minDayTrip.setModel(new DefaultComboBoxModel(getTripLengths(5, 100, 10)));
		maxDayTrip.setModel(new DefaultComboBoxModel(getTripLengths(5, 100, 10)));
		
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
	
		getContentPane().add(new JLabel("Take same route twice? "));
		getContentPane().add(sameRoute);
		
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
				
				// step 1 : create graph
				tp.createGraph(parent.getSelectedObjectsByGeometry(GeomType.LINE, "keliai"));
				
				// step 2: find nearest nodes
				Feature a = parent.getSelectedFeatureByType(GeomType.POINT, "gyvenvie", "GYVVARDAS", sourceObject.getSelectedItem().toString());
				Feature b = parent.getSelectedFeatureByType(GeomType.POINT, "gyvenvie", "GYVVARDAS", destObject.getSelectedItem().toString());
				
				System.out.println(a);
				System.out.println(b);
				
				Coordinate ac = ((Point) a.getDefaultGeometryProperty().getValue()).getCoordinate();
				Coordinate bc = ((Point) b.getDefaultGeometryProperty().getValue()).getCoordinate();
				
				BasicNode na = tp.nearestNode(ac);
				BasicNode nb = tp.nearestNode(bc);
				
				// step 3:  Find all paths within given graph, from A to B
				EdgeWeighter weighter = new DijkstraIterator.EdgeWeighter() {
					@Override
					public double getWeight(Edge e) {
						return ((LineString) e.getObject()).getLength();
					}
				};
				
				//create the path finder
				DijkstraShortestPathFinder pf = new DijkstraShortestPathFinder( tp.gg.getGraph(), na, weighter);
				pf.calculate();
				
				// TODO: find all paths
				
				Path path = pf.getPath(nb);
				System.out.println(path);
				
				FeatureCollection fc = parent.getSelectedObjectsByGeometry(GeomType.LINE, "keliai");
				FeatureIterator fi = fc.features();

				Set<FeatureId> pathFeatures = new HashSet<FeatureId>();
				ArrayList<Edge> list = (ArrayList<Edge>) path.getEdges();
				
				// collect ids of features in a path
				while(fi.hasNext()){
					Feature f = fi.next();
					for (Edge e : list){
						LineString ls = (LineString) e.getObject();

						MultiLineString mls = (MultiLineString) f.getDefaultGeometryProperty().getValue();
						
						if (mls.equals(ls.getGeometryN(0))){
							pathFeatures.add(f.getIdentifier());
							break;
						}
					}
				}

				System.out.println(pathFeatures);
				
				fi.close();
				MapLayer roadLayer = parent.getLayerByName("keliai");
			    parent.displayFeatures(roadLayer, 
			    		parent.createCustomStyle(roadLayer, parent.ff.id(pathFeatures), Color.BLUE, Color.BLUE));
				
				// step 4: Examine each path if it suites your search parameters
			}
        }));
		
		pack();
	}
	

	private void validateOptions() throws IOException{
		String test = null;
		String test2 = null;
				
		test = (String) sourceObject.getSelectedItem();
		test2 = (String) destObject.getSelectedItem();

		//if (test.equals(test2))
		//	throw new IOException("Need to ");
		
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

//	public Vector<String> layersByType(GeomType gType) throws CQLException, IOException{
//		Vector<String> vs = new Vector<String>();
//
//		for (MapLayer m : parent.activeLayers(gType))
//			vs.add(m.getFeatureSource().getName().getLocalPart());
//		return vs;
//	}
}