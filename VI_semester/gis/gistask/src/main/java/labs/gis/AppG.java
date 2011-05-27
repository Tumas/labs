package labs.gis;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTable;
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
import org.geotools.styling.Stroke;
import org.geotools.styling.Style;
import org.geotools.styling.StyleFactory;
import org.geotools.styling.Symbolizer;

import org.geotools.swing.JMapFrame;
import org.geotools.swing.action.SafeAction;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.table.FeatureCollectionTableModel;
import org.geotools.swing.tool.CursorTool;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.identity.FeatureId;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

@SuppressWarnings("serial")
public class AppG extends JFrame
{
	private MapContext map = new DefaultMapContext();
	private JMapFrame frame;
	private Set<FeatureId> selectedFeatures = new HashSet<FeatureId>();
	
    private FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2(null);
    private StyleFactory sf = CommonFactoryFinder.getStyleFactory(null);
    private AppG appG = this;
    
    public enum GeomType { POINT, LINE, POLYGON };
    
	private static final Color SELECTED_COLOUR = Color.YELLOW;
	private static final Color LINE_COLOUR = Color.BLACK;
    private static final Color FILL_COLOUR = Color.WHITE;
    private static final Color SELECTED_LINE_COLOUR = Color.ORANGE;
    
    private static final float OPACITY = 1.0f;
    private static final float LINE_WIDTH = 1.0f;
    private static final float POINT_SIZE = 3.0f;
	
	// Enable selection
	public class BoxSelectCursorTool extends CursorTool {
		private Point p1;
		
		@Override
		public boolean drawDragBox(){
			return true;
		}
		
		@Override
		public void onMouseClicked(MapMouseEvent event){
			if ((event.getModifiers() & ActionEvent.CTRL_MASK) > 0) 
				for (FeatureId fid : selectFeatures(event.getPoint(), null))
					selectedFeatures.add(fid);
			else 
				selectedFeatures = selectFeatures(event.getPoint(), null);
	        
			System.out.println(selectedFeatures);
	        displayFeatures(selectedFeatures);
		}
		
		@Override
		public void onMousePressed(MapMouseEvent event){
			p1 = event.getPoint();
		}
		
		@Override
		public void onMouseReleased(MapMouseEvent event){
			Point p2 = event.getPoint();
			
			if (!(p1.x == p2.x && p1.y == p2.y)){
				selectedFeatures = selectFeatures(p1, p2);
				System.out.println(selectedFeatures);
				displayFeatures(selectedFeatures);
			}
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

		JMenuItem addQueryMenuItem = new JMenuItem("Query");
		addQueryMenuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				try {
					QueryFrame qf = new QueryFrame(appG);
					qf.updateLayers();
					qf.setVisible(true);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		
		menu.add(addLayerMenuItem);
		menu.add(addQueryMenuItem);
		jmenu.add(menu);

		frame = new JMapFrame(map);
		frame.enableLayerTable(true);
        frame.enableStatusBar(true);
        frame.enableToolBar(true);
        frame.enableInputMethods(true);
        frame.setJMenuBar(jmenu);

        JToolBar toolbar = frame.getToolBar();
        toolbar.addSeparator();
        toolbar.add(new JButton(new SafeAction("Select") {
			@Override
			public void action(ActionEvent arg0) throws Throwable {
				frame.getMapPane().setCursorTool(new BoxSelectCursorTool());
			}
        }));
        
        toolbar.addSeparator();
        toolbar.add(new JButton(new SafeAction("Zoom To Select") {
			@Override
			public void action(ActionEvent arg0) throws Throwable {
				zoomToExtent();
			}
        }));
        
        toolbar.addSeparator();
        toolbar.add(new JButton(new SafeAction("Info about selected") {
			@Override
			public void action(ActionEvent arg0) throws Throwable {
				selectedInfo();
			}
        }));
       
        toolbar.addSeparator();
        toolbar.add(new JButton(new SafeAction("Plan a trip") {
			@Override
			public void action(ActionEvent arg0) throws Throwable {
				new TripPlannerFrame(appG).setVisible(true);
			}
        }));
        
        
        frame.getMapPane().setCursorTool(new BoxSelectCursorTool());
        frame.setSize(800, 600);
        frame.setVisible(true);
        
        preloadShapeFiles(new File[]{ 
        							  //new File("resources/shp/apskrity.shp"),
        							  new File("resources/shp/gyvenvie.shp"),
        							  new File("resources/shp/virsukal.shp") });
	}
    /*
     * Get active layers by geometry type
     */
    public ArrayList<MapLayer> activeLayers(GeomType gType){
    	ArrayList<MapLayer> mls = new ArrayList<MapLayer>();
    	
    	for (MapLayer m : frame.getMapContext().getLayers()){
    		if (m.isSelected() && m.isVisible() && getGeomType(m) == gType)
    			mls.add(m);
    	}
    	return mls;
    }

    @SuppressWarnings("unchecked")
	public FeatureCollection getSelectedObjectsByGeometry(GeomType gType, String name) {
    	FeatureCollection ft = null; 
    	
    	for (MapLayer m : activeLayers(gType)){
    		FeatureSource fs = m.getFeatureSource();
    		if (fs.getName().getLocalPart().equals(name)){
    			try {
					ft = fs.getFeatures(ff.id(selectedFeatures));
					break;
    			} catch (IOException e) {
					e.printStackTrace();
				}
    		}
    	}

    	return ft;
    }
    
    protected void selectedInfo() throws IOException {
    	ArrayList<JTable> tables = new ArrayList<JTable>();
		
    	if (!selectedFeatures.isEmpty()){
    		for (MapLayer m : frame.getMapContext().getLayers()){
    			// create a table for each layer 
    			// showing info about selected feature
    			if (m.isSelected() && m.isVisible()){
					try {
	        			FeatureCollection features = m.getFeatureSource().getFeatures(ff.id(selectedFeatures));
	        	        FeatureCollectionTableModel model = new FeatureCollectionTableModel(features);
	        	        JTable table = new JTable();
	        	        table.setModel(model);
	        	        tables.add(table);
	        		} catch (Exception ex) {}
				}
    		}
    	
    		for (JTable t : tables) new FeaturesFrame(t).setVisible(true);
    	}
    	else {
    		throw new IOException("Nothing selected");
    	}
	}

	public static void main( String[] args ) throws Exception
    {
    	new AppG().start();
    }

    public void displayFeatures(Set<FeatureId> IDs) {
    	Style style;
    	
    	for (MapLayer m : frame.getMapContext().getLayers()){
    		style = createSelectedStyle(m, IDs);
    		m.setStyle(style);
    	}
    	
    	frame.getMapPane().repaint();
    }
    
    private Style createSelectedStyle(MapLayer m, Set<FeatureId> IDs) {
    	Rule selectedRule = createRule(m, SELECTED_LINE_COLOUR, SELECTED_COLOUR);
    	selectedRule.setFilter(ff.id(IDs));
    		
        Rule otherRule = createRule(m, LINE_COLOUR, FILL_COLOUR);
        otherRule.setElseFilter(true);
            
        FeatureTypeStyle fts = sf.createFeatureTypeStyle();
        fts.rules().add(selectedRule);
        fts.rules().add(otherRule);
    	
        Style style = sf.createStyle();
        style.featureTypeStyles().add(fts);
        return style;
    }
    
    private Rule createRule(MapLayer layer, Color outlineColor, Color fillColor) {
        Symbolizer symbolizer = null;
        Fill fill = null;
        Stroke stroke = sf.createStroke(ff.literal(outlineColor), ff.literal(LINE_WIDTH));
        
        FeatureType schema = layer.getFeatureSource().getSchema();
		String geometryAttributeName = schema.getGeometryDescriptor().getLocalName();
        
        switch (getGeomType(layer)) {
            case POLYGON:
                fill = ((org.geotools.styling.StyleFactory) sf).createFill(ff.literal(fillColor), ff.literal(OPACITY));
                symbolizer = sf.createPolygonSymbolizer(stroke, fill, geometryAttributeName);
                break;
            case LINE:
                symbolizer = sf.createLineSymbolizer(stroke, geometryAttributeName);
                break;
            case POINT:
                fill = sf.createFill(ff.literal(fillColor), ff.literal(OPACITY));

                Mark mark = sf.getCircleMark();
                mark.setFill(fill);
                mark.setStroke(stroke);

                Graphic graphic = sf.createDefaultGraphic();
                graphic.graphicalSymbols().clear();
                graphic.graphicalSymbols().add(mark);
                graphic.setSize(ff.literal(POINT_SIZE));
                
                symbolizer = sf.createPointSymbolizer(graphic, geometryAttributeName);
        }

        Rule rule = sf.createRule();
        rule.symbolizers().add(symbolizer);
        return rule;
    }
    
    private GeomType getGeomType(MapLayer layer) {
    	 GeometryDescriptor geomDesc = layer.getFeatureSource().getSchema().getGeometryDescriptor();
         Class<?> clazz = geomDesc.getType().getBinding();

         if (Polygon.class.isAssignableFrom(clazz) ||
                 MultiPolygon.class.isAssignableFrom(clazz)) {
             return GeomType.POLYGON;

         } else if (LineString.class.isAssignableFrom(clazz) ||
                 MultiLineString.class.isAssignableFrom(clazz)) {
             return GeomType.LINE;

         } else {
        	 return GeomType.POINT;
         }
	}

	public void loadLayer() throws Exception {
    	File file = JFileDataStoreChooser.showOpenFile("shp", new File("resources/shp"), null);
    	if (file == null) {
            return;
        }
    	displayShapeFile(file);
    }
    
	
	public void zoomToExtent() throws IOException{
		ReferencedEnvelope box, maxBox = null;
		
		if (!selectedFeatures.isEmpty()){
			for (MapLayer m : frame.getMapContext().getLayers()){
				if (m.isSelected()){
					box = m.getFeatureSource().getFeatures(ff.id(selectedFeatures)).getBounds();
			
					if (maxBox == null){
						maxBox = box;
					} else {
					maxBox.expandToInclude(box);
					}
				}
			}
		}
		
		if (maxBox != null) frame.getMapPane().setDisplayArea(maxBox);
	}
	
    public Set<FeatureId> selectFeatures(Point screenPos1, Point screenPos2){
    	Rectangle screenRect;

    	if (screenPos2 == null)
    		screenRect = new Rectangle(screenPos1.x-2, screenPos1.y-2, 3, 3);
    	else {
    		int lx = Math.min(screenPos1.x, screenPos2.x);
    		int ly = Math.min(screenPos1.y, screenPos2.y);
    		
    		screenRect = new Rectangle(lx, ly, Math.abs(screenPos2.x - screenPos1.x),
    				Math.abs(screenPos2.y - screenPos1.y));
    	}
    	
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
        	if (ml.isSelected()){
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
        }
        return IDs;
    }
    
	public void displayShapeFile(File file) throws Exception {
    	FileDataStore store = FileDataStoreFinder.getDataStore(file);
    	FeatureSource featureSource = store.getFeatureSource();
    	CachingFeatureSource cache = new CachingFeatureSource(featureSource);
    	
    	map.addLayer(featureSource, null);
    }
	
	private void preloadShapeFiles(File[] filenames) throws Exception{
		for (File f : filenames) displayShapeFile(f);
	}

	public void setFrame(JMapFrame frame) {
		this.frame = frame;
	}

	public JMapFrame getFrame() {
		return frame;
	}
	
	public Set<FeatureId> getSelectedFeatures(){
		return selectedFeatures;
	}
}