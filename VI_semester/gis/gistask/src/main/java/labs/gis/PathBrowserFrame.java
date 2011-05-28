package labs.gis;

import java.awt.BorderLayout;
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
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.geotools.feature.FeatureCollection;
import org.geotools.graph.path.Path;
import org.geotools.map.MapLayer;
import org.geotools.swing.table.FeatureCollectionTableModel;
import org.opengis.feature.Feature;
import org.opengis.filter.identity.FeatureId;

@SuppressWarnings("serial")
public class PathBrowserFrame extends JFrame {
	ArrayList<Path> paths;
	JComboBox pathChooser;
	JComboBox objectsChooser;
	JTable roadsTable; 
	
	AppG app;
	TripPlannerFrame parent;
	
	public PathBrowserFrame(TripPlannerFrame parent1, AppG parent, final ArrayList<Path> paths){
		this.paths = paths;
		this.app = parent;
		this.parent = parent1;
		
		// TODO: proper path naming
		Vector<String> vs = new Vector<String>();
		for (Integer i = 0; i < paths.size(); i++) vs.add(i.toString());
		
		// Create custom class path : for path information
		//this.pathChooser = new JComboBox(paths.toArray());
	
		this.pathChooser = new JComboBox(vs);
		this.objectsChooser = new JComboBox(parent1.getObjectsList());
		
		this.roadsTable = new JTable(); 
		if (vs.size() != 0) selectedPath(pathChooser.getSelectedIndex());

		JScrollPane scrollPane = new JScrollPane(roadsTable);

		getContentPane().setLayout(new GridLayout(2, 1));
		pathChooser.setPreferredSize(new Dimension(100, 50));
		getContentPane().add(pathChooser);
		getContentPane().add(objectsChooser);
		
		//getContentPane().add(scrollPane);

		pathChooser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectedPath(pathChooser.getSelectedIndex());
			}
		});
		
		pack();
	}

	public void selectedPath(int index){
		Path path = paths.get(index);
		ArrayList<Feature> feats = TripPlannerFrame.listFeaturesInPath(app, path);
		Set<FeatureId> pathFeatures = TripPlannerFrame.featuresInPath(feats);
		
		MapLayer roadLayer = app.getLayerByName("keliai");
		setFeatureCollectionTableModel(roadsTable, TripPlannerFrame.subsetFeature(feats, app));

		app.displayFeatures(roadLayer, 
	    		app.createCustomStyle(roadLayer, app.ff.id(pathFeatures), Color.BLUE, Color.BLUE));
	}
	
	public void setFeatureCollectionTableModel(JTable table, FeatureCollection fc){
		table.setModel(new FeatureCollectionTableModel(fc));
	}
}