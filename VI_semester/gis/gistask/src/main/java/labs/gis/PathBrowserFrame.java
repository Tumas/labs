package labs.gis;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Set;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

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
		
		if (vs.size() != 0) selectedPath(pathChooser.getSelectedIndex());

		JScrollPane scrollPane = new JScrollPane(objectsTable);

		getContentPane().setLayout(new GridLayout(3, 1));
		getContentPane().setPreferredSize(new Dimension(100, 50));

		getContentPane().add(pathChooser);
		getContentPane().add(objectsChooser);
		getContentPane().add(scrollPane);

		pathChooser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedPath = selectedPath(pathChooser.getSelectedIndex());
				System.out.println("SELECTED PATH is now " + selectedPath.getTitle());
				// set table to roads
			}
		});
		
		objectsChooser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String selectedType = objectsChooser.getSelectedItem().toString();
				MapLayer objectsLayer = app.getLayerByName(selectedType);

				if (selectedPath != null){

					if (selectedType.equals("gyvenvie")){
						ArrayList<Feature> feats = TripPlannerFrame.listFeaturesInPath(app, selectedPath, GeomType.POINT, "gyvenvie");
						Set<FeatureId> pathFeatures = TripPlannerFrame.featuresInPath(feats);

						feats.add(0, from);
						feats.add(to);

						pathFeatures.add(from.getIdentifier());
						pathFeatures.add(to.getIdentifier());
						
						app.displayFeatures(objectsLayer, 
					    		app.createCustomStyle(objectsLayer, app.ff.id(pathFeatures), Color.RED, Color.RED, false));

						updateFeaturesTable("stoteles", feats);
						
					} else if (selectedType.equals("keliai")) {
						ArrayList<Feature> feats = TripPlannerFrame.listFeaturesInPath(app, selectedPath, GeomType.LINE, "keliai");
						
						updateFeaturesTable("keliai", feats);
					}
				}
			}
		});
		
		pack();
	}
	
	@SuppressWarnings("unchecked")
	private void updateFeaturesTable(String title, ArrayList<Feature> feats){
		if (feats.isEmpty()) return; 
		
		FeatureCollection fc = new DefaultFeatureCollection("keliai", (SimpleFeatureType) feats.get(0).getType());
		fc.addAll(feats);

		setFeatureCollectionTableModel(objectsTable, fc);
	}
	
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