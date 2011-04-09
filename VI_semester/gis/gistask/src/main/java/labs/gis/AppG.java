package labs.gis;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.geotools.data.CachingFeatureSource;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureCollections;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.map.MapLayer;
import org.geotools.styling.StyleFactory;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.CursorTool;
import org.opengis.feature.Feature;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;

/**
 * 1. Informacijos sluoksnio (vektorinio, rastrinio) idejimas/pasalinimas vaizde,
 * 		galimybe valdyti ju parodyma arba paslepima. (add/remove layer, show/hidden layer)
 * 2. Informacijos sluoksnio artinimas/tolinimas objektu perziurai, stambesniu/smulkesniu planu. (zoom in/out) 
 * 3. Informacijos sluoksnio vaizdo postumis
 * 4. Grazinimas i pradini vaizda/pilnos duomenu apimties. (full extent)
 * 
 * 5. Objektu pasirinkimo galimybe individualiai (ji pazymint) arba teriterijoje (pasirinktame staciakampyje)
 * 
 * 6. Parinktu (pazymetu) objektu parodymas stambiu planu (maksimaliai isdidinus pasirinktame vaizde) (zoom to extent)
 * 7. Atitinkamu atributiniu duomenu parodymas pasirinkus (pasizymejus) grafinius arba grafiniu objektu parodymas 
 * 		pasirinkus atributinius (sarase).
 * 8. Informacijos sluoksnio objektu atributiniu duomenu perziurejimas dalimi arba pilnu sarasu
 * 9. Objektu paieskos ir isrinkimo pagal atributinius duomenis funkcija.
 */

@SuppressWarnings("serial")
public class AppG extends JFrame
{
	private MapContext map = new DefaultMapContext();
	private JMapFrame frame;
	 
	/*
     * Factories that we will use to create style and filter objects
     */
    private StyleFactory sf = CommonFactoryFinder.getStyleFactory(null);
    private FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
	
    private FeatureCollection selectedFeatures = FeatureCollections.newCollection();

    public void start() throws Exception{
	    map.setTitle("GIS task");

		JMenuBar jmenu = new JMenuBar();
		JMenu menu = new JMenu("Layers");
		
		// Menu
		JMenuItem addLayerMenuItem = new JMenuItem("Add");
		addLayerMenuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {
					loadLayer();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		menu.add(addLayerMenuItem);
		jmenu.add(menu);

		frame = new JMapFrame(map);
		frame.enableLayerTable(true);
        frame.enableStatusBar(true);
        frame.enableToolBar(true);
        frame.enableInputMethods(true);
        frame.setJMenuBar(jmenu);
		
        // enable selection
        frame.getMapPane().setCursorTool(new CursorTool() {
        	@Override
        	public void onMouseClicked(MapMouseEvent event){
        		selectFeatures(event);
        	}
        });
        
        frame.setSize(800, 600);
        frame.setVisible(true);
	}
	
    public static void main( String[] args ) throws Exception
    {
    	new AppG().start();
    }
    
    public void loadLayer() throws Exception {
    	File file = JFileDataStoreChooser.showOpenFile("shp", new File("resources/shp"), null);
    	if (file == null) {
            return;
        }
    	displayShapeFile(file);
    }
    
    public void selectFeatures(MapMouseEvent event){
        /* Construct a 5x5 pixel rectangle centered on the mouse click position */
        Point screenPos = event.getPoint();
        Rectangle screenRect = new Rectangle(screenPos.x-2, screenPos.y-2, 5, 5);
        
        /*
         * Transform the screen rectangle into bounding box in the coordinate
         * reference system of our map context. Note: we are using a naive method
         * here but GeoTools also offers other, more accurate methods. TODO!
         */
        AffineTransform screenToWorld = frame.getMapPane().getScreenToWorldTransform();
        Rectangle2D worldRect = screenToWorld.createTransformedShape(screenRect).getBounds2D();
        ReferencedEnvelope bbox = new ReferencedEnvelope(
                worldRect,
                frame.getMapContext().getCoordinateReferenceSystem());

        /*
         * Create a Filter to select features that intersect with
         * the bounding box
         */
        String geometryAttributeName = null; // test
        Filter filter = ff.intersects(ff.property(geometryAttributeName), ff.literal(bbox));

        /*
         * Use the filter to identify the selected features
         */
		Set<FeatureId> IDs = new HashSet<FeatureId>();
        for (MapLayer ml : map.getLayers()){
        	try {
        		FeatureCollection selectedFeatures = (FeatureCollection) ml.getFeatureSource().getFeatures(filter);
        		FeatureIterator iter = selectedFeatures.features();

        		try {
        			while (iter.hasNext()) {
        				Feature feature = iter.next();
        				IDs.add(feature.getIdentifier());
        			}
        		} finally {
        			iter.close();
        		}
            
        		System.out.println(IDs);
        	} catch (Exception ex) {}
        }
    }
    
	public void displayShapeFile(File file) throws Exception {
    	FileDataStore store = FileDataStoreFinder.getDataStore(file);
    	FeatureSource featureSource = store.getFeatureSource();
    	
    	// TODO: why do we need to set geometries for each source?
    	// FeatureSource featureSource = store.getFeatureSource();
    	// setGeometry(featureSource);
    	
    	CachingFeatureSource cache = new CachingFeatureSource(featureSource);
    	map.addLayer(featureSource, null);
    }
}