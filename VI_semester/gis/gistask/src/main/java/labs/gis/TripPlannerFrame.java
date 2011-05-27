package labs.gis;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;

import labs.gis.AppG.GeomType;

import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.map.MapContext;
import org.geotools.map.MapLayer;
import org.geotools.swing.action.SafeAction;
import org.opengis.feature.Feature;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.filter.identity.FeatureId;

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
	public TripPlannerFrame(AppG parent) throws CQLException, IOException{
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

		getContentPane().add(new JLabel("Include rivesr in a trip?"));
		getContentPane().add(includeRivers);

		getContentPane().add(new JLabel("Include lakes in a trip?"));
		getContentPane().add(includeLakes);
		
		getContentPane().add(new JButton(new SafeAction("Find Trips") {
			@Override
			public void action(ActionEvent arg0) throws Throwable {
				validateOptions();
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
		
		while (iter.hasNext()) {
			Feature feature = iter.next();
			v.add(feature.getProperty(id).getValue().toString());
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