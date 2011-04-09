package labs.gis;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;

import org.geotools.data.CachingFeatureSource;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.map.MapLayer;
import org.geotools.styling.FeatureTypeStyle;
import org.geotools.styling.Fill;
import org.geotools.styling.Graphic;
import org.geotools.styling.Mark;
import org.geotools.styling.Rule;
import org.geotools.styling.Style;
import org.geotools.styling.Symbolizer;
import org.geotools.swing.JMapFrame;
import org.geotools.swing.action.SafeAction;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.CursorTool;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;
import org.opengis.style.StyleFactory;

/**
 * [IMPLEMENTED] 
 * 	1. Informacijos sluoksnio (vektorinio, rastrinio) idejimas/pasalinimas vaizde,
 * 		galimybe valdyti ju parodyma arba paslepima. (add/remove layer, show/hidden layer)
 * 
 * [OUT-OF-THE-BOX] 
 * 2. Informacijos sluoksnio artinimas/tolinimas objektu perziurai, stambesniu/smulkesniu planu. (zoom in/out) 
 * 
 * [OUT-OF-THE-BOX]
 * 3. Informacijos sluoksnio vaizdo postumis
 * 
 * [OUT-OF-THE-BOX]
 * 4. Grazinimas i pradini vaizda/pilnos duomenu apimties. (full extent)
 * 
 * 5. Objektu pasirinkimo galimybe individualiai (ji pazymint) arba teriterijoje (pasirinktame staciakampyje)
 * 
 * 6. Parinktu (pazymetu) objektu parodymas stambiu planu (maksimaliai isdidinus pasirinktame vaizde) (zoom to extent)
 * 
 * [OUT-OF-THE-BOX]
 * 7. Atitinkamu atributiniu duomenu parodymas pasirinkus (pasizymejus) grafinius
 * 
 * 7a grafiniu objektu parodymas pasirinkus atributinius (sarase).
 * 
 * 8. Informacijos sluoksnio objektu atributiniu duomenu perziurejimas dalimi arba pilnu sarasu
 * 
 * 9. Objektu paieskos ir isrinkimo pagal atributinius duomenis funkcija.
 */

@SuppressWarnings("serial")
public class AppG extends JFrame
{
	private MapContext map = new DefaultMapContext();
	private JMapFrame frame;
	private Set<FeatureId> selectedFeatures;
	
    private FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
    private StyleFactory sf = CommonFactoryFinder.getStyleFactory(null);
    
	private static final Color SELECTED_COLOUR = Color.YELLOW;
	
	// Enable selection
	public class ClickCursorTool extends CursorTool {
		public void onMouseClicked(MapMouseEvent event){
			selectedFeatures = selectFeatures(event);
	        System.out.println(selectedFeatures);
	        //displayFeatures(selectedFeatures);
		}
	}

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

        JToolBar toolbar = frame.getToolBar();
        toolbar.addSeparator();
        toolbar.add(new JButton(new SafeAction("Reset cursor") {
			@Override
			public void action(ActionEvent arg0) throws Throwable {
				frame.getMapPane().setCursorTool(new ClickCursorTool());
			}
        }));
        
        frame.getMapPane().setCursorTool(new ClickCursorTool());
        frame.setSize(800, 600);
        frame.setVisible(true);
	}
	
    public static void main( String[] args ) throws Exception
    {
    	new AppG().start();
    }

    public void displaySelectedFeatures(Set<FeatureId> IDs) {
    	// Create style for each selected feature
    	
    	Style style = createSelectedStyle(IDs);
        frame.getMapContext().getLayer(0).setStyle(style);
        frame.getMapPane().repaint();
    }
    
    private Style createSelectedStyle(Set<FeatureId> IDs) {
        Rule selectedRule = createRule(SELECTED_COLOUR, SELECTED_COLOUR);
        selectedRule.setFilter(ff.id(IDs));

        Rule otherRule = createRule(Color.black, SELECTED_COLOUR);
        otherRule.setElseFilter(true);

        FeatureTypeStyle fts = ((org.geotools.styling.StyleFactory) sf).createFeatureTypeStyle();
        fts.rules().add(selectedRule);
        fts.rules().add(otherRule);

        Style style = ((org.geotools.styling.StyleFactory) sf).createStyle();
        style.featureTypeStyles().add(fts);
        return style;
    }
    
    private static final float OPACITY = 1.0f;
    private static final float LINE_WIDTH = 1.0f;
    private static final float POINT_SIZE = 10.0f;
    private enum GeomType { POINT, LINE, POLYGON };
    
    private Rule createRule(Color outlineColor, Color fillColor) {
        Symbolizer symbolizer = null;
        Fill fill = null;
        Stroke stroke = (java.awt.Stroke) ((org.geotools.styling.StyleFactory) sf).createStroke(ff.literal(outlineColor), ff.literal(LINE_WIDTH));

        fill = ((org.geotools.styling.StyleFactory) sf).createFill(ff.literal(fillColor), ff.literal(OPACITY));
        symbolizer = ((org.geotools.styling.StyleFactory) sf).createPolygonSymbolizer();
       
        /*
        switch (GEO) {
            case POLYGON:
                fill = ((org.geotools.styling.StyleFactory) sf).createFill(ff.literal(fillColor), ff.literal(OPACITY));
                symbolizer = sf.createPolygonSymbolizer(stroke, fill, geometryAttributeName);
                break;
            case LINE:
                symbolizer = sf.createLineSymbolizer(stroke, geometryAttributeName);
                break;
            case POINT:
                fill = ((org.geotools.styling.StyleFactory) sf).createFill(ff.literal(fillColor), ff.literal(OPACITY));

                Mark mark = ((org.geotools.styling.StyleFactory) sf).getCircleMark();
                mark.setFill(fill);
                mark.setStroke((org.opengis.style.Stroke) stroke);

                Graphic graphic = ((org.geotools.styling.StyleFactory) sf).createDefaultGraphic();
                graphic.graphicalSymbols().clear();
                graphic.graphicalSymbols().add(mark);
                graphic.setSize(ff.literal(POINT_SIZE));

                symbolizer = sf.createPointSymbolizer(graphic, geometryAttributeName);
        }
        */

        Rule rule = ((org.geotools.styling.StyleFactory) sf).createRule();
        rule.symbolizers().add(symbolizer);
        return rule;
    }
    
    public void loadLayer() throws Exception {
    	File file = JFileDataStoreChooser.showOpenFile("shp", new File("resources/shp"), null);
    	if (file == null) {
            return;
        }
    	displayShapeFile(file);
    }
    
    public Set<FeatureId> selectFeatures(MapMouseEvent event){
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

        Set<FeatureId> IDs = new HashSet<FeatureId>();

        for (MapLayer ml : map.getLayers()){
        	FeatureType schema = ml.getFeatureSource().getSchema();
        	String geometryAttributeName = schema.getGeometryDescriptor().getLocalName();
            Filter filter = ff.intersects(ff.property(geometryAttributeName), ff.literal(bbox));
        	
        	try {
        		FeatureCollection selectedFeatures = ml.getFeatureSource().getFeatures(filter);
        		FeatureIterator iter = selectedFeatures.features();

        		try {
        			while (iter.hasNext()) {
        				Feature feature = iter.next();
        				IDs.add(feature.getIdentifier());
        			}
        		} finally {
        			iter.close();
        		}
            
        	} catch (Exception ex) {}
        }
        
        return IDs;
    }
    
	public void displayShapeFile(File file) throws Exception {
    	FileDataStore store = FileDataStoreFinder.getDataStore(file);
    	FeatureSource featureSource = store.getFeatureSource();
    	
    	// FeatureSource featureSource = store.getFeatureSource();
    	// setGeometry(featureSource);
    	
    	CachingFeatureSource cache = new CachingFeatureSource(featureSource);
    	map.addLayer(featureSource, null);
    }
}