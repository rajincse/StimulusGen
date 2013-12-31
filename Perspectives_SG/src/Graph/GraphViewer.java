package Graph;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;

import perspectives.DefaultProperties.*;
import perspectives.Property;
import perspectives.PropertyManager;
import perspectives.PropertyType;
import perspectives.Viewer2D;
import util.BubbleSets;
import util.ImageTiler;
import util.NodeLinkViewer;
import util.Util;


public class GraphViewer extends NodeLinkViewer {

	Graph graph;
	
	GraphDrawer drawer;
	
	int[] edgeSources;
	int [] edgeTargets;
	
	BufferedImage bgim = null;
	int bgimx=0;
	int bgimy=0;

	public GraphViewer(String name, GraphData g) {
		super(name);
		
		try {
		    bgim = ImageIO.read(new File("c:\\bgim.png"));			       
		} catch (IOException e) {
		}
		
		graph = g.graph;
		
		
		
		setDirected(graph.getDirected());
		
		ArrayList<Integer> e1 = new ArrayList<Integer>();
		ArrayList<Integer> e2 = new ArrayList<Integer>();
		graph.getEdgesAsIndeces(e1, e2);
		
		edgeSources = new int[e1.size()];
		edgeTargets = new int[e2.size()];
		for (int i=0; i<e1.size(); i++)
		{
			edgeSources[i] = e1.get(i);
			edgeTargets[i] = e2.get(i);
		}
		
		drawer = new ForceGraphDrawer(graph);
		
		System.out.println("done graph drawer");
				
		try {
			
			
			Property<IntegerPropertyType> bgimx = new Property<IntegerPropertyType>("bgimx");
			bgimx.setValue(new IntegerPropertyType(0));
			this.addProperty(bgimx);
			Property<IntegerPropertyType> bgimy = new Property<IntegerPropertyType>("bgimy");
			bgimy.setValue(new IntegerPropertyType(0));
			this.addProperty(bgimy);
			
			
			OpenFilePropertyType ff = new OpenFilePropertyType();
			Property<OpenFilePropertyType> p33 = new Property<OpenFilePropertyType>("Load Positions");
			p33.setValue(ff);
			this.addProperty(p33);
			
			
			Property<DoublePropertyType> p = new Property<DoublePropertyType>("Simulation.K_REP");
			p.setValue(new DoublePropertyType(5000000.));
			((ForceGraphDrawer)drawer).k_rep = 5000000;
			this.addProperty(p);
			
			p = new Property<DoublePropertyType>("Simulation.K_ATT");
			p.setValue(new DoublePropertyType(100.));
			this.addProperty(p);
			
			p = new Property<DoublePropertyType>("Simulation.SPRING_LENGTH");
			p.setValue(new DoublePropertyType(30.));
			this.addProperty(p);			
			
			p = new Property<DoublePropertyType>("Simulation.MAX_STEP");
			p.setValue(new DoublePropertyType(100.));
			((ForceGraphDrawer)drawer).max_step = 100.;
			this.addProperty(p);
			
			Property<BooleanPropertyType> p2= new Property<BooleanPropertyType>("Simulation.Simulate");
			p2.setValue(new BooleanPropertyType(false));
			this.addProperty(p2);			

			SaveFilePropertyType f = new SaveFilePropertyType();
			Property<SaveFilePropertyType> p3 = new Property<SaveFilePropertyType>("Save");
			p3.setValue(f);
			this.addProperty(p3);		
			
			Property<IntegerPropertyType> p7 = new Property<IntegerPropertyType>("Tiles");
			p7.setValue(new IntegerPropertyType(0));
			this.addProperty(p7);	
			
			Property<IntegerPropertyType> p77 = new Property<IntegerPropertyType>("ToImage");
			p77.setValue(new IntegerPropertyType(0));
			this.addProperty(p77);
			
			Property<StringPropertyType> p99 = new Property<StringPropertyType>("SelectedNodes");
			p99.setValue(new StringPropertyType(""));
			this.addProperty(p99);
			p99.setPublic(true);
		} 
		catch (Exception e) {		
			e.printStackTrace();
		}
	}

	public void simulate() {
		
		boolean b = ((BooleanPropertyType)this.getProperty("Simulation.Simulate").getValue()).boolValue();
		if (b)
		{
			long t = new Date().getTime();
			drawer.iteration();
		}
	}
	
	public <T extends PropertyType> boolean propertyBroadcast(Property p, T newvalue, PropertyManager origin)
	{
		if (p.getName() == "SelectedNodes")
		{
			this.clearNodeSelection();
			String[] split = ((StringPropertyType)newvalue).stringValue().split("\t");
			ArrayList<String> nodes = graph.getNodes();
			for (int i=0; i<split.length; i++)
			{
				int index = nodes.indexOf(split[i]);
				if (index >= 0 && index<nodes.size())
				this.selectNode(index);
			}
		}
		return true;
	}
	
	public <T extends PropertyType> void propertyUpdated(Property p, T newvalue)
	{
		if (p.getName() == "Simulation.K_REP")
			((ForceGraphDrawer)drawer).k_rep = ((DoublePropertyType)newvalue).doubleValue();
		else if (p.getName() == "Simulation.K_ATT")
			((ForceGraphDrawer)drawer).k_att = ((DoublePropertyType)newvalue).doubleValue();
		else if (p.getName() == "Simulation.SPRING_LENGTH")
			((ForceGraphDrawer)drawer).spring_length = ((DoublePropertyType)newvalue).doubleValue();
		else if (p.getName() == "Simulation.MAX_STEP")
			((ForceGraphDrawer)drawer).max_step = ((DoublePropertyType)newvalue).doubleValue();
		else if (p.getName() == "Save")
			this.save(new File(((SaveFilePropertyType)newvalue).path));

		else if (p.getName() == "Load Positions")
		{
			ArrayList<String> nodes = graph.getNodes();
			
			try{
			 FileInputStream fstream = new FileInputStream(((OpenFilePropertyType)newvalue).path);
			 DataInputStream in = new DataInputStream(fstream);
			 BufferedReader br = new BufferedReader(new InputStreamReader(in));
			 String s;
			 while ((s = br.readLine()) != null)
			 {
				s = s.trim();
				
				String[] split = s.split("\t");
				
				if (split.length < 2) continue;
				
				int index = nodes.indexOf(split[0].trim());
				if (index < 0)
				{
					String s2 = split[0].trim().toLowerCase();
					for (int i=0; i<nodes.size(); i++)
					{
						String s1 = nodes.get(i).toLowerCase();
						if (s1.equals(s2))
						{
							index = i;
							break;
						}
					}
					if (index < 0)
					continue;
				}
				
				String[] poss = split[1].split(",");
				int x = (int)Double.parseDouble(poss[0]);
				int y = -(int)Double.parseDouble(poss[1]);
				
				if (drawer != null)
				{
					drawer.setX(index, (int)(x * 1.335));
					drawer.setY(index, (int)(y * 1.335));
					
					this.setAspect(index, PointAspectType.RECT_LABEL_FIT);
				}
			 }
			 
			 in.close();
			}
			catch(Exception e)
			{
				
			}
		}		
		else if (p.getName() == "Tiles")
		{
			ImageTiler it = new ImageTiler(this, this.getTaskObserverDialog());
			ArrayList<String> nodes = graph.getNodes();
			int maxX = 0;
			int maxY = 0;
			int minX = 999999;
			int minY = 999999;
			for (int i=0; i<nodes.size(); i++)
			{
				int x = drawer.getX(i);
				int y = drawer.getY(i);
				if (x >  maxX) maxX = x;
				if (y>maxY) maxY = y;
				if (x< minX) minX = x;
				if (y<minY) minY = y;
			}
			it.createTilePyramid(256, (int)(minX-0.05*(maxX-minX)), (int)(minY-0.05*(maxY-minY)), (int)(1.1*(maxX-minX)), (int)(1.1*(maxY-minY)), 10, new File("c:/Work/"));
		}
		else if (p.getName() == "ToImage")
		{
			
			
			ArrayList<String> nodes = graph.getNodes();
			int maxX = 0;
			int maxY = 0;
			int minX = 999999;
			int minY = 999999;
			for (int i=0; i<nodes.size(); i++)
			{
				int x = drawer.getX(i);
				int y = drawer.getY(i);
				if (x >  maxX) maxX = x;
				if (y>maxY) maxY = y;
				if (x< minX) minX = x;
				if (y<minY) minY = y;
			}
			
			
			BufferedImage img2 = new BufferedImage((int)(1.1*(maxX-minX)), (int)(1.1*(maxY-minY)),BufferedImage.TYPE_INT_RGB);						
			Graphics2D g = img2.createGraphics();
			g.setColor(this.backgroundColor());
			g.fillRect(0, 0, (int)(1.1*(maxX-minX)), (int)(1.1*(maxY-minY)));
			
        	
        	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        	                         RenderingHints.VALUE_ANTIALIAS_ON);

        	g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        	                         RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        	
        	g.translate(-(minX-200), -(minY-100));
        	
        	this.render(g);
        	
        	try {
				ImageIO.write(img2,"PNG",new File("C:/Work/graphviewer.PNG"));
			} catch (IOException e) {
				e.printStackTrace();
			}
        	           	    
        	    
			
		}
		else
			super.propertyUpdated(p, newvalue);
	}
	
	
	
	public void save(File f)
	{
		ArrayList<String> nodes = graph.getNodes();
		for (int i=0; i<nodes.size(); i++)
		{
			int x = drawer.getX(i);
			int y = drawer.getY(i);
			
			
			Property<IntegerPropertyType> px = graph.nodeProperty(nodes.get(i), "x");
			if (px == null)
			{
				px = new Property<IntegerPropertyType>("x");
				graph.addNodeProperty(nodes.get(i), px);
			}
			px.setValue(new IntegerPropertyType(x));
			
			Property<IntegerPropertyType> py = graph.nodeProperty(nodes.get(i), "y");
			if (py == null)
			{
				py = new Property<IntegerPropertyType>("y");
				graph.addNodeProperty(nodes.get(i), py);
			}
			py.setValue(new IntegerPropertyType(y));
						
		}		
		graph.toGraphML(f);		
	}
	
	public void load(File f)
	{
		graph.fromGraphML(f);
		
		ArrayList<String> nodes = graph.getNodes();
		for (int i=0; i<nodes.size(); i++)
		{
			Property<IntegerPropertyType> p = graph.nodeProperty(nodes.get(i), "x");
			if (p != null)
				drawer.setX(i,p.getValue().intValue());
			else
				drawer.setX(i,0);
			
			p = graph.nodeProperty(nodes.get(i), "y");
			if (p != null)
				drawer.setY(i,p.getValue().intValue());
			else
				drawer.setY(i,0);
					}
	}

	@Override
	protected String getNodeLabel(int p) {
		return graph.getNodes().get(p);
	}

	@Override
	protected int getNumberOfNodes() {
		return graph.numberOfNodes();
	}

	@Override
	protected int getNodeX(int p) {
		return drawer.getX(p);
	}

	@Override
	protected int getNodeY(int p) {
		return drawer.getY(p);
	}

	@Override
	protected void setNodeX(int p, int x) {
		drawer.setX(p, x);
	}

	@Override
	protected void setNodeY(int p, int y) {
		drawer.setY(p, y);
	}

	@Override
	protected int[] getEdgeSources() {
		return edgeSources;
	}

	@Override
	protected int[] getEdgeTargets() {
		return edgeTargets;
	}




	@Override
	public Color backgroundColor() {
		return new Color(230,230,230);		
	}
	
	
	public void render(Graphics2D g)
	{
		bgimx = ((IntegerPropertyType)this.getProperty("bgimx").getValue()).intValue();
		bgimy = ((IntegerPropertyType)this.getProperty("bgimy").getValue()).intValue();
		g.drawImage(bgim,bgimx,bgimy,null);
		super.render(g);
	}
	
	public void renderEdge(int p1, int p2, int edgeIndex, boolean selected, Graphics2D g)
	{		
		if (selected)
		{
			g.setColor(this.getSelectedEdgeColor());
			g.setStroke(new BasicStroke(2));
		}
		else
		{
			g.setColor(this.getEdgeColor());
			g.setStroke(new BasicStroke(1));
		}
		
		int x1 = getPointX(p1);
		int y1 = getPointY(p1);
		int x2 = getPointX(p2);
		int y2 = getPointY(p2);	
		
		Rectangle2D bounds1 = getPointBounds(p1);	
		Rectangle2D bounds2 = getPointBounds(p2);	
		
		Line2D.Double l = new Line2D.Double(x1,y1,x2,y2);		
		
    	Rectangle2D b1 = new Rectangle2D.Double(x1-bounds1.getWidth()/2, y1-bounds1.getHeight()/2, bounds1.getWidth(), bounds1.getHeight());
    	Rectangle2D b2 = new Rectangle2D.Double(x2-bounds2.getWidth()/2, y2-bounds2.getHeight()/2, bounds2.getWidth(), bounds2.getHeight());
    	    	
    	//check for intersection with the rectangle bounds
    	Point2D pp1 = null;
    	Point2D pp2 = null;
    	if (Util.linesIntersect(x1, y1, x2, y2, b1.getX(), b1.getY(), b1.getX()+b1.getWidth(), b1.getY()))
         		pp1 = Util.getLineLineIntersection(x1, y1, x2, y2, b1.getX(), b1.getY(), b1.getX()+b1.getWidth(), b1.getY());
    	
    	else if (Util.linesIntersect(x1, y1, x2, y2, b1.getX()+b1.getWidth(), b1.getY(), b1.getX()+b1.getWidth(), b1.getY()+b1.getHeight()))
    		pp1 = Util.getLineLineIntersection(x1, y1, x2, y2, b1.getX()+b1.getWidth(), b1.getY(), b1.getX()+b1.getWidth(), b1.getY()+b1.getHeight());
    		    	        	
    	else if (Util.linesIntersect(x1, y1, x2, y2, b1.getX()+b1.getWidth(), b1.getY()+b1.getHeight(), b1.getX(), b1.getY()+b1.getHeight()))	    	        	
    		pp1 = Util.getLineLineIntersection(x1, y1, x2, y2, b1.getX()+b1.getWidth(), b1.getY()+b1.getHeight(), b1.getX(), b1.getY()+b1.getHeight());
    	    	        	
    	else if (Util.linesIntersect(x1, y1, x2, y2, b1.getX(), b1.getY()+b1.getHeight(), b1.getX(), b1.getY()))
    		pp1 = Util.getLineLineIntersection(x1, y1, x2, y2, b1.getX(), b1.getY()+b1.getHeight(), b1.getX(), b1.getY());
    
      	if (Util.linesIntersect(x1, y1, x2, y2, b2.getX(), b2.getY(), b2.getX()+b2.getWidth(), b2.getY()))
    		pp2 = Util.getLineLineIntersection(x1, y1, x2, y2, b2.getX(), b2.getY(), b2.getX()+b2.getWidth(), b2.getY());
   
    	if (Util.linesIntersect(x1, y1, x2, y2, b2.getX()+b2.getWidth(), b2.getY(), b2.getX()+b2.getWidth(), b2.getY()+b2.getHeight()))
    		pp2 = Util.getLineLineIntersection(x1, y1, x2, y2, b2.getX()+b2.getWidth(), b2.getY(), b2.getX()+b2.getWidth(), b2.getY()+b2.getHeight());
    	
    	else if (Util.linesIntersect(x1, y1, x2, y2, b2.getX()+b2.getWidth(), b2.getY()+b2.getHeight(), b2.getX(), b2.getY()+b2.getHeight()))
    		pp2 = Util.getLineLineIntersection(x1, y1, x2, y2, b2.getX()+b2.getWidth(), b2.getY()+b2.getHeight(), b2.getX(), b2.getY()+b2.getHeight());
    	
    	if (Util.linesIntersect(x1, y1, x2, y2, b2.getX(), b2.getY()+b2.getHeight(), b2.getX(), b2.getY()))
    		pp2 = Util.getLineLineIntersection(x1, y1, x2, y2, b2.getX(), b2.getY()+b2.getHeight(), b2.getX(), b2.getY());    
			
		if (pp1 != null && pp2 != null)
			g.drawLine((int)pp1.getX(), (int)pp1.getY(), (int)pp2.getX(), (int)pp2.getY());
		else if (pp1 == null && pp2 == null)
			g.drawLine(x1,y1,x2,y2);
		else if (pp1 == null && pp2 != null)
			g.drawLine(x1,y1, (int)pp2.getX(), (int)pp2.getY());
		else if (pp1 != null && pp2 == null)
			g.drawLine((int)pp1.getX(), (int)pp1.getY(),x2,y2);		
	}
	
	
	protected void pointSelectionChanged()
	{	
		super.pointSelectionChanged();
		
		int[] sel = getSelectedPoints();
		if (sel.length == 0) return;
		String s = "";
		for (int i=0; i<sel.length; i++)
		{
			if (i != 0) s = s + "\t";
			s = s + this.getNodeLabel(sel[i]);
		}
		
		this.getProperty("SelectedNodes").setValue(new StringPropertyType(s));
	}
	
}
