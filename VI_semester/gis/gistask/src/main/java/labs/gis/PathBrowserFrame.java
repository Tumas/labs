package labs.gis;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Set;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import labs.gis.AppG.GeomType;

import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.FeatureCollection;
import org.geotools.map.MapLayer;
import org.geotools.swing.table.FeatureCollectionTableModel;
import org.opengis.feature.Feature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.identity.FeatureId;

@SuppressWarnings("serial")
public class PathBrowserFrame extends JFrame {
	ArrayList<PathInfo> paths;
	JComboBox pathChooser;
	JComboBox objectsChooser;
	JTable objectsTable; 
	JTable stopsTable;
	JLabel length;
	
	AppG app;
	TripPlannerFrame parent;
	
	PathInfo selectedPath;

	Feature from;
	Feature to;
	
	public PathBrowserFrame(TripPlannerFrame parent1, AppG parent, final ArrayList<PathInfo> pathsWithInfo, final Feature from, final Feature to){
		this.paths = pathsWithInfo;
		this.selectedPath = paths.get(0);
		
		this.app = parent;
		this.parent = parent1;
		
		this.from = from;
		this.to = to;
		
		// Choose paths by their names
		Vector<String> vs = new Vector<String>();
		for (PathInfo p : pathsWithInfo)
			vs.add(p.getTitle());
		
		this.pathChooser = new JComboBox(vs);
		this.objectsChooser = new JComboBox(parent1.getObjectsList());
		this.objectsTable = new JTable(); 
		this.stopsTable = new JTable();
		this.length = new JLabel();
		
		if (vs.size() != 0) selectedPath(pathChooser.getSelectedIndex());

		JScrollPane scrollPane = new JScrollPane(objectsTable);
		JScrollPane scrollPane1 = new JScrollPane(stopsTable);

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		//getContentPane().setPreferredSize(new Dimension(200, 100));
		
		getContentPane().add(pathChooser);
		getContentPane().add(length);
		getContentPane().add(objectsChooser);
		
		getContentPane().add(scrollPane);
		getContentPane().add(scrollPane1);

		pathChooser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedPath = selectedPath(pathChooser.getSelectedIndex());
				updateTableWithRoads();
				updateTableWithStopsInfo();
			}
		});
		
		objectsChooser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String selectedType = objectsChooser.getSelectedItem().toString();
				MapLayer objectsLayer = app.getLayerByName(selectedType);

				if (selectedPath != null){
					if (selectedType.equals("gyvenvie")){
						updateTableWithStops(objectsLayer);
					} else if (selectedType.equals("keliai")) {
						updateTableWithRoads();
					}
				}
			}
		});
		
		updateTableWithRoads();
		updateTableWithStopsInfo();
		pack();
	}
	
	private void updateTableWithStopsInfo() {
		System.out.println(selectedPath.getStops().size());
		System.out.println(selectedPath.getStopsInfo().size());
		
		String[][] data = new String[selectedPath.getStops().size()][3];
		
		int index = 0;
		for (StopInformation st : selectedPath.getStops()){
			data[index][0] = ((Feature) st.getFrom()).getProperty("GYVVARDAS").getValue().toString();
			data[index][1] = ((Feature) st.getDestination()).getProperty("GYVVARDAS").getValue().toString();
			data[index++][2] = st.getDistance().toString();
		}
		
		stopsTable.setModel(new DefaultTableModel(data, new String[]{"GYVVARDAS", "GYVVARDAS", "Atstumas"}));
		this.length.setText("Trip length: " + selectedPath.getLength() + " ( " + selectedPath.getLengthKM() + " Km)");
	}

	/* Update table with specifc layer information */
	private void updateTableWithStops(MapLayer objectsLayer){
		ArrayList<Feature> feats = TripPlannerFrame.listFeaturesInPath(app, selectedPath, GeomType.POINT, "gyvenvie");
		Set<FeatureId> pathFeatures = TripPlannerFrame.featuresInPath(feats);

		feats.add(0, from);
		feats.add(to);

		pathFeatures.add(from.getIdentifier());
		pathFeatures.add(to.getIdentifier());
		
		app.displayFeatures(objectsLayer, 
	    		app.createCustomStyle(objectsLayer, app.ff.id(pathFeatures), Color.RED, Color.RED, false));

		updateFeaturesTable("stoteles", feats);
	}
	
	private void updateTableWithRoads(){
		ArrayList<Feature> feats = TripPlannerFrame.listFeaturesInPath(app, selectedPath, GeomType.LINE, "keliai");
		updateFeaturesTable("keliai", feats);
	}
	
	/*
	 * Update table with feature information from features collection
	 */
	@SuppressWarnings("unchecked")
	private void updateFeaturesTable(String title, ArrayList<Feature> feats){
		if (feats.isEmpty()) return; 
		
		FeatureCollection fc = new DefaultFeatureCollection("keliai", (SimpleFeatureType) feats.get(0).getType());
		fc.addAll(feats);

		setFeatureCollectionTableModel(objectsTable, fc);
	}

	/*
	 * Action to perform on selecting pathInfo object. 
	 */
	public PathInfo selectedPath(int index){
		PathInfo path = paths.get(index);
		ArrayList<Feature> feats = TripPlannerFrame.listFeaturesInPath(app, path, GeomType.LINE, "keliai");
		Set<FeatureId> pathFeatures = TripPlannerFrame.featuresInPath(feats);
		
		MapLayer roadLayer = app.getLayerByName("keliai");
		
		app.displayFeatures(roadLayer, 
	    		app.createCustomStyle(roadLayer, app.ff.id(pathFeatures), Color.BLUE, Color.BLUE, false));
		
		return path;
	}
	
	public void setFeatureCollectionTableModel(JTable table, FeatureCollection fc){
		table.setModel(new FeatureCollectionTableModel(fc));
	}
}