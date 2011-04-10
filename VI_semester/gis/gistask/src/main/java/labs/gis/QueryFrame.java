package labs.gis;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.geotools.data.DataStore;
import org.geotools.data.FeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.filter.text.cql2.CQL;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.map.MapContext;
import org.geotools.map.MapLayer;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.action.SafeAction;
import org.geotools.swing.table.FeatureCollectionTableModel;
import org.opengis.feature.Feature;
import org.opengis.filter.Filter;
import org.opengis.filter.identity.FeatureId;

public class QueryFrame extends JFrame {
	private JComboBox featureTypeCBox;
	private JTable table;
	private JTextField text;
	private MapContext mapContext;
	private AppG parentApplication;
	private FeatureCollection featuresOnDisplay;
	
	@SuppressWarnings("serial")
	public QueryFrame(AppG parentApp) {
		this.parentApplication = parentApp;
		this.mapContext = parentApplication.getFrame().getMapContext();
		
		getContentPane().setLayout(new BorderLayout());

		text = new JTextField(80);
		text.setText("include"); 
		getContentPane().add(text, BorderLayout.NORTH);

		table = new JTable();
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setModel(new DefaultTableModel(5, 5));
		table.setPreferredScrollableViewportSize(new Dimension(500, 200));

		JScrollPane scrollPane = new JScrollPane(table);
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		JMenuBar menubar = new JMenuBar();
		setJMenuBar(menubar);

		featureTypeCBox = new JComboBox();
		menubar.add(featureTypeCBox);
	
		JMenu dataMenu = new JMenu("Data");
        dataMenu.add(new SafeAction("Select") {
        	@Override
            public void action(ActionEvent e) throws Throwable {
                filterFeatures();
            }
        });

        dataMenu.add(new SafeAction("Show selected") {
        	@Override
            public void action(ActionEvent e) throws Throwable {
        		if (table.getRowCount() > 0){
        			Set<FeatureId> IDs = new HashSet<FeatureId>();

        			try {
        				FeatureIterator iter = featuresOnDisplay.features();

        				try {
        					while (iter.hasNext()) {
        						Feature feature = iter.next();
        						IDs.add(feature.getIdentifier());
        					}
        				} finally {
        					iter.close();
        				}
        			} catch (Exception ex) {}
        			parentApplication.displayFeatures(IDs);
        		}
        	}
        });

        menubar.add(dataMenu);
        pack();
	}
	
	public void updateLayers() throws CQLException, IOException{
		Vector<String> vs = new Vector<String>();
		for (MapLayer m : mapContext.getLayers())
			vs.add(m.getFeatureSource().getName().getLocalPart());
		
		featureTypeCBox.setModel(new DefaultComboBoxModel(vs));
	}
	
	public void filterFeatures() throws IOException{
		String name = (String) featureTypeCBox.getSelectedItem();
		FeatureSource source = getSelectedFeatureSource(name);
	
		try {
			Filter filter = CQL.toFilter(text.getText());
			featuresOnDisplay = source.getFeatures(filter);
	        FeatureCollectionTableModel model = new FeatureCollectionTableModel(featuresOnDisplay);
	        table.setModel(model);
		} catch (NullPointerException e) {
			if (mapContext.getLayerCount() == 0)
				throw new IOException("Don't forget to load some layers first");
			else
				throw new IOException(e.getMessage());
		} catch (CQLException e) {
			throw new IOException("Invalid query");
		}
	}
	
	/*
	private void queryFeatures() throws Exception {
		String selectedName = (String) featureTypeCBox.getSelectedItem();
		FeatureSource source = getSelectedFeatureSource(selectedName);
		FeatureType schema = source.getSchema();
		String name = schema.getGeometryDescriptor().getLocalName();

		Filter filter = CQL.toFilter(text.getText());
		Query query = new Query(selectedName, filter, new String[] { name });
		
		FeatureCollection features = source.getFeatures(query);
		FeatureCollectionTableModel model = new FeatureCollectionTableModel(
				features);
		table.setModel(model);
	}
	*/

	private FeatureSource getSelectedFeatureSource(String name) {
		FeatureSource source = null;
		for (MapLayer ml : mapContext.getLayers()) {
			if (ml.getFeatureSource().getName().getLocalPart() == name) {
				source = ml.getFeatureSource();
				break;
			}
		}
		return source;
	}
}
