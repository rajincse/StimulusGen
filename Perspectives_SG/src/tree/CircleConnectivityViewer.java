package tree;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle.Control;

import javax.swing.JOptionPane;

import multidimensional.SpringEmbedder;

import perspectives.DefaultProperties.*;
import perspectives.Property;
import perspectives.*;

import util.SplineFactory;

import Graph.GraphData;
import data.DistanceMatrix;
import data.Table;
import data.TableData;
import data.Table.TableElementType;
import data.TableDistances;

public class CircleConnectivityViewer extends HierarchicalClusteringViewer {
	
	private float maxX = 0;
	private float minX = 0;
	private float maxY = 0;
	private float minY = 0;
	
	private double[] circleCoordsX;
	private double[] circleCoordsY;
	
	private float circleRadius = 2000;
	
	private ArrayList<String> connectionSources = new ArrayList<String>();
	private ArrayList<String> connectionTargets = new ArrayList<String>();
	private boolean[] connectionSelected = null;
	
	double centerout = 0.8;
	double controlout = 250;
	double squish = 1.25;
	
	private Color[] connectionColors = null;
	
	
	private int connectionThickness = 1;
	
	private double scaledNodeSize = -1;
	
	private TableDistances table = null;
	
	
	int[][] splinesX = null;
	int[][] splinesY = null;
	Object splinesLock = new Object();
	
	private HashMap<String,Color> similarityColoring = null;
	
	private String[] validControl = {"-80","-58","-33","-56","-55","-31","-28","-77","-73","-70","-79","-49","-38","-74","-43","-18","-45","-67","-54","-48","-53","-51","-24","-42","-40","-66","-78","-59","-63","-72","-81"};
		
	public CircleConnectivityViewer(String name, TreeData d, GraphData g) {
		super(name, d);
		
		g.graph.getEdges(connectionSources, connectionTargets);
		connectionSelected = new boolean[connectionSources.size()];

		
		createProps();
	}


	public CircleConnectivityViewer(String name, TableData d, GraphData g) {
		
		super(name, d);	
		
		table = d.getTable();
		
		g.graph.getEdges(connectionSources, connectionTargets);
		connectionSelected = new boolean[connectionSources.size()];
		
		createProps();

	}
	
	public CircleConnectivityViewer(String name, TreeData d) {
		super(name, d);		
		
		createProps();
		
		try
		{
			Property<OpenFilePropertyType> p = new Property<OpenFilePropertyType>("Connections");
			OpenFilePropertyType f = new OpenFilePropertyType();
            f.dialogTitle = "Load Connections";
            this.addProperty(p);
		}
		catch(Exception e){	}			
	}
	
	public CircleConnectivityViewer(String name, TableData d) {		
		super(name, d);	
		
		table = d.getTable();
		
		createProps();
		
		try
		{
			Property<OpenFilePropertyType> p = new Property<OpenFilePropertyType>("Connections");
			OpenFilePropertyType f = new OpenFilePropertyType();
            f.dialogTitle = "Load Connections";
            f.extensions = new String[1];
            f.extensions[0] = "*";
            p.setValue(f);
            this.addProperty(p);
		}
		catch(Exception e){	}			
	}
	
	protected void createProps()
	{
		try
		{
			Property<PercentPropertyType> p1 = new Property<PercentPropertyType>("Appearance.Straightness");
			p1.setValue(new PercentPropertyType(0.2));
			this.addProperty(p1);
			
			Property<IntegerPropertyType> p2 = new Property<IntegerPropertyType>("Appearance.CircleSize");
			p2.setValue(new IntegerPropertyType(2000));
			this.addProperty(p2);
			
			Property<IntegerPropertyType> p = new Property<IntegerPropertyType>("Appearance.Conn_Thick");
			p.setValue(new IntegerPropertyType(1));
			this.addProperty(p);
			
			Property<DoublePropertyType> p3 = new Property<DoublePropertyType>("Appearance.SkipControl");
			p3.setValue(new DoublePropertyType(250));
			this.addProperty(p3);
			
			Property<DoublePropertyType> p4 = new Property<DoublePropertyType>("Appearance.CenterOut");
			p4.setValue(new DoublePropertyType(0.8));
			this.addProperty(p4);
			
			Property<DoublePropertyType> p5 = new Property<DoublePropertyType>("Appearance.Squish");
			p5.setValue(new DoublePropertyType(1.25));
			this.addProperty(p5);
			
			this.getProperty("Appearance.Node Size").setValue(new IntegerPropertyType(100));
		}
		catch(Exception e)
		{
			
		}	
	}
	
	public void setConnections(ArrayList<String> sources, ArrayList<String> targets)
	{
		connectionSources = sources;
		connectionTargets = targets;
		connectionSelected = new boolean[connectionSources.size()];
		
		this.connectionColors = null;
	}
	
	@Override
	public <T extends PropertyType> void propertyUpdated(Property p, T newvalue) {
		// TODO Auto-generated method stub
		if (p.getName() == "Appearance.Straightness")
		{
			double b = ((PercentPropertyType)newvalue).getRatio();
			this.recomputeSplines(b);
		}
		else if (p.getName() == "Appearance.CircleSize")
		{			
			setCircleRadius(((IntegerPropertyType)newvalue).intValue());
			double b = ((PercentPropertyType)(getProperty("Appearance.Straightness")).getValue()).getRatio();
			this.recomputeSplines(b);
		}
		else if (p.getName() == "Appearance.SkipControl")
		{			
			double b = ((PercentPropertyType)(getProperty("Appearance.Straightness")).getValue()).getRatio();
			controlout = ((DoublePropertyType)newvalue).doubleValue();
			this.recomputeSplines(b);
		}		
		else if (p.getName() == "Appearance.CenterOut")
		{			
			double b = ((PercentPropertyType)(getProperty("Appearance.Straightness")).getValue()).getRatio();
			centerout = ((DoublePropertyType)newvalue).doubleValue();
			this.recomputeSplines(b);
		}
		else if (p.getName() == "Appearance.Squish")
		{			
			double b = ((PercentPropertyType)(getProperty("Appearance.Straightness")).getValue()).getRatio();
			squish = ((DoublePropertyType)newvalue).doubleValue();
			this.recomputeSplines(b);
		}
		else if (p.getName() == "Connections")
		{
			loadConnections(((OpenFilePropertyType)newvalue).path);
		}
		else if (p.getName() == "Appearance.Conn_Thick")
		{
			connectionThickness = ((IntegerPropertyType)newvalue).intValue();
		}
		super.propertyUpdated(p, newvalue);
	}
	
	public void loadConnections(String path)
	{
	     try {
	            BufferedReader in = new BufferedReader(new FileReader(path));

	            String str;
	            ArrayList<String> lines = new ArrayList<String>();
	            while ((str = in.readLine()) != null) {
	                lines.add(str);
	            }
	            
	            if (lines.size() == 0)
	            {
	            	JOptionPane.showMessageDialog(null, "Connections file seems to be empty.");
	            	return;
	            }
	            
	            connectionSources.clear();
	            connectionTargets.clear();
	            connectionSelected = new boolean[connectionSources.size()];;
	            
	            for (int i=0; i<lines.size(); i++)
	            {
	            	String[] split = lines.get(i).split("\t");
	            	if (split.length != 2)
	            	{
		            	JOptionPane.showMessageDialog(null, "Connections file format is wrong. Loading unsucessful.");
		            	return;
	            	}
	            	
	            	connectionSources.add(split[0]);
	            	connectionTargets.add(split[1]);
	            }
	            connectionSelected = new boolean[connectionSources.size()];
	            in.close();   
	           
	        } catch (Exception e) {
	            e.printStackTrace();
	        }     
	}
	
	private float computeMaxX()
	{
		float mm = -999999999;
		for (int i=0; i<this.getNumberOfNodes(); i++)
		{
			float x = super.getNodeX(i);
			if (x > mm)
				mm = x;
		}
		return mm;
	}
	
	private float computeMinX()
	{
		float mm = 999999999;
		for (int i=0; i<this.getNumberOfNodes(); i++)
		{
			float x = super.getNodeX(i);
			if (x < mm)
				mm = x;
		}
		return mm;
	}
	
	private float computeMaxY()
	{
		float mm = -999999999;
		for (int i=0; i<this.getNumberOfNodes(); i++)
		{
			float x = super.getNodeY(i);
			if (x > mm)
				mm = x;
		}
		return mm;
	}
	
	private float computeMinY()
	{
		float mm = 999999999;
		for (int i=0; i<this.getNumberOfNodes(); i++)
		{
			float x = super.getNodeY(i);
			if (x < mm)
				mm = x;
		}
		return mm;
	}
	
	protected void cachePositions()
	{		
		maxX = computeMaxX();
		minX = computeMinX();
		maxY = computeMaxY();
		minY = computeMinY();
		
		ArrayList<String> nodes = tree.nodes();
		
		circleCoordsX = new double[nodes.size()];
		circleCoordsY = new double[nodes.size()];
		
		for (int p=0; p<nodes.size(); p++)
		{	
			double x = super.getNodeX(p);
			double y = super.getNodeY(p);
			x = (x-minX) / (maxX-minX);
			y = (y-minY) / (maxY-minY);
			
			double angle = 1.7 * Math.PI * x;
		
			double cx = ((1-y)*1*circleRadius + circleRadius) * Math.cos(angle);
			double cy = -((1-y)*1*circleRadius + circleRadius) * Math.sin(angle);
			
			circleCoordsX[p] = (int)cx;
			circleCoordsY[p] = (int)cy;
			
			if (tree.treeNode(nodes.get(p)).isLeaf())
				this.setRotation(p, Math.PI - angle);
			else
				this.setRotation(p, Math.PI/2 - angle);
		}		
	}
	
	@Override
	protected int getNodeX(int p)
	{
		
		if (circleCoordsX == null)
			return 0;	
		
		return (int)circleCoordsX[p];
	}

	@Override
	protected int getNodeY(int p) {
		
		if (circleCoordsX == null)
			return 0;
		
		return (int)circleCoordsY[p];
	}
	
	protected void setNodeX(int p, int x)
	{
		circleCoordsX[p] = x;
	}
	protected void setNodeY(int p, int y)
	{
		circleCoordsY[p] = y;
	}
	
	
	@Override
	public void renderEdge(int p1, int p2, int edgeIndex, boolean selected,
			Graphics2D g) {
		
		Tree t= this.tree;
		ArrayList<String> nodes = tree.nodes();
		int n2 =getEdgeTargets()[edgeIndex];
		int n1 = getEdgeSources()[edgeIndex];
		
		
		boolean left = false;
		if (super.getNodeX(n2) < super.getNodeX(n1))
			left = true;
			
		
		
		// TODO Auto-generated method stub
		int x1 = getNodeX(p1);
		int y1 = getNodeY(p1);
		int x2 = getNodeX(p2);
		int y2 = getNodeY(p2);
		
		if (selected)
		{
			g.setColor(this.getSelectedEdgeColor());
			g.setStroke(new BasicStroke(2));
		}
		else{
			g.setColor(this.getEdgeColor());
			g.setStroke(new BasicStroke(1));
		}
		
		//if (x1 == 0 || x2 == 0)
		//	return;
		
		double radius1 = Math.sqrt(x1*x1 + y1*y1);
		double radius2 = Math.sqrt(x2*x2 + y2*y2);
		
		Arc2D.Double a = new Arc2D.Double(-(int)radius1, -(int)radius1, 2*(int)radius1, 2*(int)radius1, 90, 135,Arc2D.OPEN);
		if (left)
		{
			a.setAngles(x2, y2, x1, y1);
			a.setAngleStart(new Point2D.Double(x2,y2));
		}
		else
		{
			a.setAngles(x1, y1, x2, y2);
			a.setAngleStart(new Point2D.Double(x1,y1));
		}
		
		
		//g.draw(a);
		
		ArrayList<Point2D.Double> pts = new ArrayList<Point2D.Double>();
		double[] coords = new double[6];
		FlatteningPathIterator pi = new FlatteningPathIterator(a.getPathIterator(null),1,5);		
		for (; !pi.isDone(); pi.next()) {
			pi.currentSegment(coords);
			pts.add(new Point2D.Double(coords[0], coords[1]));
		}
		
		int[] polyx = new int[pts.size()+1];
		int[] polyy = new int[pts.size()+1];

		if (!left)
			for (int i=0; i<pts.size(); i++)
			{
				polyx[i] = (int)pts.get(i).getX();
				polyy[i] = (int)pts.get(i).getY();
			}
		else
			for (int i=pts.size()-1, j=0; i>=0; i--,j++)
			{
				polyx[j] = (int)pts.get(i).getX();
				polyy[j] = (int)pts.get(i).getY();
			}
			
		polyx[polyx.length-1] = x2;
		polyy[polyx.length-1] = y2;
		
		
		g.drawPolyline(polyx, polyy, polyx.length);
	}
	
	
	
	public void recomputeSplines(double b)
	{
		synchronized(splinesLock)
		{
			splinesX = new int[connectionSources.size()][];
			splinesY = new int[connectionSources.size()][];
		
			ArrayList<String> nodes = tree.nodes();
			HashMap<String,Integer> map = new HashMap<String,Integer>();
			for (int i=0; i<nodes.size(); i++)
				map.put(nodes.get(i), new Integer(i));
			
	
		    //compute the splines
		    for (int i=0; i<connectionSources.size(); i++)
		    {
		    	ArrayList<Point2D.Double> control = new ArrayList<Point2D.Double>();	
		    	
		    	
		        ArrayList<String> parents1 = new ArrayList<String>();
		        ArrayList<String> parents2 = new ArrayList<String>();
		       
		        //find the path/control points
		        TreeNode t1 = tree.treeNode(connectionSources.get(i));
		        TreeNode t2 = tree.treeNode(connectionTargets.get(i));
		        
		        if (t1 == null || t2 == null)
		        {
		        	splinesX[i] = new int[0];
		        	splinesY[i] = new int[0];
		        	continue;
		        }
		        
		        while (true)
		        {
		            parents1.add(t1.id());
		            t1 = t1.parent;
		            if (t1 == tree.root())
		            {
		                parents1.add(t1.id());
		                break;
		            }
		        }
		        
		        while (true)
		        {
		            int index = parents1.indexOf(t2.id());
		            if (index >= 0)
		            {
		                if (index != parents1.size()-1)
		                	for (int k=index+1; k<parents1.size(); k++)
		                	{
		                		parents1.remove(k);
		                		k--;
		                	}
		                break;
		            }
		            parents2.add(t2.id());
		            t2 = t2.parent;
		        }
	
		        ArrayList<String> validControlList = new ArrayList<String>();
		        for (int j=0; j<validControl.length; j++) validControlList.add(validControl[j]);
		        
		        for (int j=0; j<parents1.size(); j++)
		        {
		        	
		        	if (j!=0 && validControlList.indexOf(parents1.get(j)) < 0)
		        		continue;
		        	
		        	int pindex = map.get(parents1.get(j)).intValue();
		            double px = getNodeX(pindex);
		            double py = getNodeY(pindex);		            
		            double pl = Math.sqrt(px*px + py*py);
		            
		           
		            
		            double h = (pl-circleRadius)/circleRadius;
		            if (h > centerout)
		                continue;
		            
		            h= Math.pow(h, this.squish);
		            if (Double.isNaN(h)) h = 0;
		            
		            Point2D.Double p = new Point2D.Double(circleRadius * px/pl * (1-h), circleRadius * py/pl * (1-h));	           
		            control.add(p);
		        }
		        
		        for (int j=parents2.size()-1; j>=0; j--)
		        {
		        	if (j!=0 && validControlList.indexOf(parents2.get(j)) < 0)
		        		continue;
		        	
		        	int pindex = map.get(parents2.get(j)).intValue();
		            double px = getNodeX(pindex);
		            double py = getNodeY(pindex);		           
		            double pl = Math.sqrt(px*px + py*py);
		            
		            double h = (pl-circleRadius)/circleRadius;
		            if (h > centerout)
		                continue;
		            
		            h= Math.pow(h, this.squish);
		            if (Double.isNaN(h)) h = 0;
		            
		            Point2D.Double p = new Point2D.Double(circleRadius * px/pl * (1-h), circleRadius * py/pl * (1-h));	           
		            control.add(p);
		        }
	
		        for (int j=1; j<control.size()-1; j++)
		            {	        	
		                if (control.get(j).distance(control.get(j-1)) < controlout)
		                {
		                    control.remove(j);	               
		                    j--;
		                }
		            }
	
		        //straighten
		        Point2D.Double p1 = control.get(0);
		        Point2D.Double pn = control.get(control.size()-1);	        
		      
		       double[] c = new double[control.size()*3];
		        for (int j=0; j<control.size(); j++)
		        {
		        	Point2D.Double cc = control.get(j);
		        	c[3*j] = cc.getX();
		        	c[3*j+1] = cc.getY();
		        	c[3*j+2] = 0;
		        	
		        	//straighten
		        	if (j != 0 && j != control.size()-1)
		        	{
			        	double perc = (double)j/(control.size()-1);
			        	double psx = p1.getX()+(pn.getX()-p1.getX())*perc;
			        	double psy = p1.getY()+(pn.getY()-p1.getY())*perc;
			        	
			        	c[3*j] = c[3*j] + (psx - c[3*j])*b;
			        	c[3*j+1] = c[3*j+1] + (psy - c[3*j+1])*b;
		        	}
		        	
		        }
		        
		      double[] spline1 = SplineFactory.createCubic(c, 10);
		      
		      splinesX[i] = new int[spline1.length/3];
		      splinesY[i] = new int[spline1.length/3];		        
		  
		       for (int j=0; j<splinesX[i].length;  j++)
		       {
		    	   splinesX[i][j] = (int)Math.round(spline1[j*3]);
		    	   splinesY[i][j] = (int)Math.round(spline1[j*3+1]);	    	    
		       }   
		    }
		}
	}
	
	public void setConnectionColor(int i, Color c)
	{
		if (connectionColors == null || connectionColors.length < connectionSources.size())
			connectionColors = new Color[connectionSources.size()];
		
		if (i<0 || i>=connectionColors.length)
			return;
		
		connectionColors[i] = c;
	}
	
	public void setConnectionColor(Color c)
	{
		if (connectionColors == null || connectionColors.length < connectionSources.size())
			connectionColors = new Color[connectionSources.size()];
		
		for (int i=0; i< connectionColors.length; i++)
				connectionColors[i] = c;		
	}
	
	public Color getConnectionColor(int i)
	{
		if (connectionColors != null && i >= 0 && i < connectionColors.length)
			return connectionColors[i];
		else return Color.black;
	}


	@Override
	public void render(Graphics2D g) {		
		
		super.render(g);
		
		synchronized(splinesLock)
		{
			if (splinesX == null)
				return;
			
			g.setStroke(new BasicStroke(connectionThickness));
			for (int i=0; i<splinesX.length; i++)
			{
				if (splinesX[i] == null || splinesY[i] == null)
					continue;
				
				if (connectionSelected[i])
					g.setColor(Color.red);
				else 
					g.setColor(getConnectionColor(i));	
				g.drawPolyline(splinesX[i],splinesY[i], splinesX[i].length);
					
			}
		}
	}
	
	protected void doneComputingPositions()
	{
		this.cachePositions();
	}
	
	public void setCircleRadius(float cr)
	{
		float scale = cr/circleRadius;		
		
		ArrayList<String> nodes = tree.nodes();		
		
		for (int i=0; i<nodes.size(); i++)
		{
			circleCoordsX[i] *= scale;
			circleCoordsY[i] *= scale;
		}
		
		circleRadius = cr;
		
		int s = ((IntegerPropertyType)this.getProperty("Appearance.Node Size").getValue()).intValue();
		if (scaledNodeSize < 0) scaledNodeSize = s;
		scaledNodeSize *= scale;
		if (s != (int)scaledNodeSize)
			this.getProperty("Appearance.Node Size").setValue(new IntegerPropertyType((int)scaledNodeSize));
		
	}
	
	public void selectNode(int p)
	{
		super.selectNode(p);
		
		ArrayList<String> nodes = tree.nodes();
		
		for (int i=0; i<connectionSources.size(); i++)
		{
			System.out.println(nodes.get(p) + " " + connectionSources.get(i) + " " +connectionTargets.get(i));
			if (connectionSources.get(i).equals(nodes.get(p)) || connectionTargets.get(i).equals(nodes.get(p)))
				connectionSelected[i] = true;
		}
	}
	
	public void nodeDeselected(int p)
	{
		super.nodeDeselected(p);
		
		ArrayList<String> nodes = tree.nodes();
		
		for (int i=0; i<connectionSources.size(); i++)
		{
			if (connectionSources.get(i).equals(nodes.get(p)) || connectionTargets.get(i).equals(nodes.get(p)))
				connectionSelected[i] = false;
		}		
	}
	
	public void computeSimilarityColoring()
	{		
		DistanceMatrix d = null;
		if (table != null)
		{	
			int n = table.getRowCount();
			d = new DistanceMatrix(n*(n-1)/2);
			
			int index1 = -1;
			for (int i=0; i<n-1; i++)
				for (int j=i+1; j<n; j++)
				{
					index1++;
					
					d.setPointId(index1, table.getRowName(i) + table.getRowName(j));
					
					int index2 = -1;
					for (int k=0; k<n-1; k++)
						for (int l=k+1; l<n; l++)
						{
							index2++;							
							if (index2 <= index1) continue;							
							
							float d1; if (i == k) d1 = 0; else d1 = table.getDistance(i, k);
							float d2; if (i == l) d2 = 0; else d2 = table.getDistance(i, l);
							
							if (d1 < d2)
							{
								if (j==l) d2 = 0; else d2 = table.getDistance(j, l);
							}
							else
							{
								if (j==k) d1 = 0; else d1 = table.getDistance(j, k);
							}
							
							d.setDistance(index1, index2, (d1+d2)/2);
						}
				}
			SpringEmbedder se = new SpringEmbedder(d);
			for (int i=0; i<n*(n-1)/3; i++)
			{
				se.iteration();
				System.out.println(i);
			}
			similarityColoring = new HashMap<String,Color>();
			for (int i=0; i<d.getCount(); i++)
				similarityColoring.put(d.getPointId(i), se.getColor(i));
		}
		else
		{	
		}
	}
	
	public void applySimilarityColoring()
	{
		if (similarityColoring == null)
			computeSimilarityColoring();
		
			
		for (int i=0; i<connectionSources.size(); i++)
		{
			String s = connectionSources.get(i);
			String t = connectionTargets.get(i);
			
			Color c = similarityColoring.get(s+t);
			if (c == null)
				c = similarityColoring.get(t+s);
			
			if (c!=null)
			{
				Color cc = this.getConnectionColor(i);
				
				Color ca = new Color(c.getRed(),c.getGreen(), c.getBlue(), cc.getAlpha());
				this.setConnectionColor(i, ca);
			}
		}
	}
	
	


}

