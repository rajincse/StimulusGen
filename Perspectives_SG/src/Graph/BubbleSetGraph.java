package Graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import perspectives.DefaultProperties.DoublePropertyType;
import perspectives.DefaultProperties.IntegerPropertyType;
import perspectives.DefaultProperties.StringPropertyType;
import perspectives.Property;
import perspectives.PropertyType;
import util.BubbleSets;
import util.Util;

public class BubbleSetGraph extends ClusterGraphViewer {
	
	Object bigGridCompute = new Object();
	BubbleSets bubbleSets = null;	
	BufferedImage bigGrid;
	int bigGridSX, bigGridSY;

	public BubbleSetGraph(String name, GraphData g) {
		super(name, g);
		try {			
			
			Property<IntegerPropertyType> p44 = new Property<IntegerPropertyType>("Cluster Bubbles");
			p44.setValue(new IntegerPropertyType(0));
			this.addProperty(p44);	
			
			Property<IntegerPropertyType> p4 = new Property<IntegerPropertyType>("Bubble");
			p4.setValue(new IntegerPropertyType(0));
			this.addProperty(p4);	
			
			Property<IntegerPropertyType> p5 = new Property<IntegerPropertyType>("BubbleCellSize");
			p5.setValue(new IntegerPropertyType(10));
			this.addProperty(p5);	
			
			Property<IntegerPropertyType> p6 = new Property<IntegerPropertyType>("BubbleR1");
			p6.setValue(new IntegerPropertyType(100));
			this.addProperty(p6);	
			
			Property<DoublePropertyType> p8 = new Property<DoublePropertyType>("BubbleThresh");
			p8.setValue(new DoublePropertyType(0.2));
			this.addProperty(p8);
			
			Property<StringPropertyType> p9 = new Property<StringPropertyType>("Smaller");
			p9.setValue(new StringPropertyType(""));
			this.addProperty(p9);	
			}
			catch (Exception e) {		
				e.printStackTrace();
			}
	}
	
	public <T extends PropertyType> void propertyUpdated(Property p, T newvalue)
	{			
		if (p.getName() == "Cluster Bubbles")
			this.createBubbles();
		else if (p.getName() == "Bubble")
		{
			Rectangle2D.Double[] points = new Rectangle2D.Double[this.getNumberOfNodes()];
			for (int i=0; i<this.getNumberOfNodes(); i++)
				points[i] = new Rectangle2D.Double(this.getNodeX(i) - this.getNodeSize(i)/2, this.getNodeY(i) - this.getNodeSize(i)/2, this.getNodeSize(i), this.getNodeSize(i));
			
			//this.setAspect(PointAspectType.RECT_NO_LABEL);
			
			int[] s1 = this.getSelectedNodes();
			int[] s2 = new int[this.getNumberOfNodes()-s1.length];
			for (int i=0, c=0; i<this.getNumberOfNodes(); i++)
			{
				boolean found = false;
				for (int j=0; j<s1.length; j++)
					if (s1[j] == i){ found = true; break;};
				if (!found)
				{
					s2[c] = i;
					c++;
				}
			}
			int[][] sets = new int[2][];
			sets[0] = s1;
			sets[1] = s2;
				
			
			this.bubbleSets = new BubbleSets(points,sets);
			bubbleSets.computeContour(10, 100, this.getNodeSize(0)/2, 0);
		}
		else if (p.getName() == "BubbleCellSize")
		{
			if (bubbleSets != null)
				bubbleSets.computeContour(((IntegerPropertyType)newvalue).intValue(), ((IntegerPropertyType)this.getProperty("BubbleR1").getValue()).intValue(),this.getNodeSize(0)/2, 0);
		}
		else if (p.getName() == "BubbleR1")
		{
			if (bubbleSets != null)
				bubbleSets.computeContour(((IntegerPropertyType)this.getProperty("BubbleCellSize").getValue()).intValue(),((IntegerPropertyType)newvalue).intValue(), this.getNodeSize(0)/2,0);
		}
		else 
			super.propertyUpdated(p,newvalue);
	}
	
	
	
	@Override
	public void render(Graphics2D g) {
		// TODO Auto-generated method stub
		
		
		if (((IntegerPropertyType)this.getProperty("Bubble").getValue()).intValue() > 0)
		{
			Rectangle2D.Double[] points = new Rectangle2D.Double[this.getNumberOfNodes()];
			for (int i=0; i<this.getNumberOfNodes(); i++)
			{
				Rectangle2D r = this.getPointBounds(i);
				
				//points[i] = new Rectangle2D.Double(this.getNodeX(i) - this.getNodeSize(i)/2, this.getNodeY(i) - this.getNodeSize(i)/2, this.getNodeSize(i), this.getNodeSize(i));
				points[i] = new Rectangle2D.Double(this.getNodeX(i) - r.getWidth()/2, this.getNodeY(i) - r.getHeight()/2, r.getWidth(), r.getHeight());
				
			}
			
		//	this.setAspect(PointAspectType.RECT_NO_LABEL);
			
			int[] s1 = this.getSelectedNodes();
			int[] s2 = new int[this.getNumberOfNodes()-s1.length];
			for (int i=0, c=0; i<this.getNumberOfNodes(); i++)
			{
				boolean found = false;
				for (int j=0; j<s1.length; j++)
					if (s1[j] == i){ found = true; break;};
				if (!found)
				{
					s2[c] = i;
					c++;
				}
			}
			int[][] sets = new int[2][];
			sets[0] = s1;
			sets[1] = s2;
				
			
			this.bubbleSets = new BubbleSets(points,sets);
			bubbleSets.computeContour(10, 100,this.getNodeSize(0)/2,0);
		}
		
		if (bigGrid != null)
		{
			g.drawImage(bigGrid, bigGridSX, bigGridSY,null);
		}
		
		
		
		if (bubbleSets != null)
		{
			bubbleSets.computeContour(((IntegerPropertyType)this.getProperty("BubbleCellSize").getValue()).intValue(),((IntegerPropertyType)this.getProperty("BubbleR1").getValue()).intValue(), this.getNodeSize(0)/5, 0);
			
			
		
		/*	for (int i=0;  bubbleSets.paths != null && i<bubbleSets.paths.size(); i++)
			{
				for (int j=1; j<bubbleSets.paths.get(i).size(); j++)
				{
					Point2D.Double p1 = bubbleSets.paths.get(i).get(j-1);
					Point2D.Double p2 = bubbleSets.paths.get(i).get(j);
					g.drawLine((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
				}				
			}*/
			
			// the grid
			
			double contThresh = ((DoublePropertyType)this.getProperty("BubbleThresh").getValue()).doubleValue();
			
			/*for (int i=0; bubbleSets.grid != null && i<bubbleSets.grid.length; i++)
			{
				for (int j=0; j<bubbleSets.grid[i].length; j++)
				{
					int cx = (int)bubbleSets.centroid.x;
					int cy = (int)bubbleSets.centroid.y;		

					
					double degd = bubbleSets.grid[i][j];					
					
				
					if (degd < 0) degd = 0; if (degd > 1) degd = 1;
					if (degd > contThresh) degd = 0.5;
					int deg = (int)(255* degd);
					if (deg < 0) deg = 0;
					
					if (deg == 0)
						continue;
					
					
					g.setColor(new Color(255-deg, 255, 255-deg, 100));
					
					
									
					g.fillRect((int)(bubbleSets.minX + i*bubbleSets.cellSize),(int)( bubbleSets.minY + j*bubbleSets.cellSize),(int)bubbleSets.cellSize, (int)bubbleSets.cellSize);				
				}
			}*/
			
				
			Point2D.Double[][] lin = Util.marchingSquares(bubbleSets.grid, bubbleSets.cellSize, new Point2D.Double(bubbleSets.minX, bubbleSets.minY), contThresh);
			
			
		    g.setColor(Color.green);
		    g.setStroke(new BasicStroke(2));
		    
		   Path2D.Double path = new Path2D.Double();
		   for(int i=0;i<lin.length;i++)
		   {
		
			   if (lin[i].length > 0) path.moveTo((int)lin[i][0].x,(int)lin[i][0].y);
			   
			   for (int j=1; j<lin[i].length; j++)		    		   
		           path.lineTo((int)lin[i][j].x,(int)lin[i][j].y);
			   
			   path.closePath();
		    }	
		 //  path.closePath();
		   g.fill(path);
		   if (lin.length > 1)
			   g.setColor(Color.red);
		   else
			   g.setColor(Color.black);
		   
		   Color[] cs = {Color.red, Color.blue, Color.orange, Color.black, Color.cyan, Color.MAGENTA, Color.pink};
		   for(int i=0;i<lin.length;i++)
		   {		
			   g.setColor(cs[i]);
			   for (int j=1; j<lin[i].length; j++)		    		   
		           g.drawLine((int)lin[i][j-1].x,(int)lin[i][j-1].y,(int)lin[i][j].x,(int)lin[i][j].y);
			    
		    }	
		   
		   //g.draw(path);
	
		}
		 super.render(g);
	}
	
	
	void createBubbles()
	{
		if (clusters == null)
			return;
		
		
		
		synchronized(this.bigGridCompute)
		{
			String sm = ((StringPropertyType)(this.getProperty("Smaller").getValue())).stringValue();
			String[] smaller = sm.split(",");
			
			ArrayList<String> nodes = graph.getNodes();			
			
			int minx = 999999999;
			int miny = 999999999;
			int maxx = -999999999;
			int maxy = -999999999;
			for (int i=0; i<nodes.size(); i++)
			{
				int x = drawer.getX(i);
				int y = drawer.getY(i);
				
				if (x < minx) minx = x;
				if (x > maxx) maxx = x;
				if (y < miny) miny = y;
				if (y > maxy) maxy = y;
			}
			minx = minx - 500;
			maxx = maxx + 500;
			miny = miny -100;
			maxy = maxy + 100;
			
			bigGrid = new BufferedImage(maxx-minx, maxy-miny,BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = bigGrid.createGraphics();
			
			bigGridSX = minx;
			bigGridSY = miny;
		
	
		
				
		for (int j=0; j<clusterTypes.size(); j++)
		{
			int ct = 0;
			for (int i=0; i<nodes.size(); i++)
				if (clusterTypes.get(j).equals(clusters[i]))
					ct++;
				
			Rectangle2D.Double[] points = new Rectangle2D.Double[nodes.size()];
			int[] s1 = new int[ct];
			int[] s2 = new int[nodes.size()-ct];
			
			ct = 0;
			int ct2 = 0;
			for (int i=0; i<nodes.size(); i++)
			{
				Rectangle2D r = this.getPointBounds(i);					
				points[i] = new Rectangle2D.Double(this.getNodeX(i) - r.getWidth()/2, this.getNodeY(i) - r.getHeight()/2, r.getWidth(), r.getHeight());
				
				if (!clusterTypes.get(j).equals(clusters[i]))
				{
					s2[ct2] = i;
					ct2++;
				}
				else
				{
					s1[ct] = i;
					ct++;
				}
			}		
			
			int[][] sets = new int[2][];
			sets[0] = s1;
			sets[1] = s2;
				
			
			BubbleSets bs = new BubbleSets(points,sets);
			int r = ((IntegerPropertyType)this.getProperty("BubbleR1").getValue()).intValue();
			boolean b = false;
			for (int k=0; k<smaller.length; k++) if (smaller[k].equals(clusterTypes.get(j))) b = true;
			if (b)	r = r/2;
			bs.computeContour(((IntegerPropertyType)this.getProperty("BubbleCellSize").getValue()).intValue(),r, this.getNodeSize(0)/5, 0);
			double contThresh = ((DoublePropertyType)this.getProperty("BubbleThresh").getValue()).doubleValue();
			//Point2D.Double[][] lin = Util.marchingSquares(bs.grid, bs.cellSize, new Point2D.Double(bs.minX, bs.minY), contThresh);
			
						
			
			Color c = this.getColor(s1[0]);				
			
			double re1 = (c.getRed()+20- 105./255.*230.)/(150./255.);
			double gr1 = (c.getGreen()+20- 105./255.*230.)/(150./255.);
			double bl1 = (c.getBlue()+20- 105./255.*230.)/(150./255.);
			
			if (re1 < 0) re1 = 0; if (re1 > 255) re1 = 255;
			if (gr1 < 0) gr1 = 0; if (gr1 > 255) gr1 = 255;
			if (bl1 < 0) bl1 = 0; if (bl1 > 255) bl1 = 255;
			
			c = new Color((int)re1, (int)gr1, (int)bl1, 150);
			Color c2 = new Color((int)re1, (int)gr1, (int)bl1, 140);
			Color c3 = new Color((int)re1, (int)gr1, (int)bl1, 130);
			
			//Color c3 = new Color((int)(0.2*c.getRed()), (int)(0.2*c.getGreen()),(int)(0.2*c.getBlue()), 50);
			g.setColor(c);
			
			for (int i=0; bs.grid != null && i<bs.grid.length; i++)
			{
				for (int k=0; k<bs.grid[i].length; k++)
				{
					double degd = bs.grid[i][k];					
					
				
					if (degd < 0) degd = 0; if (degd > 1) degd = 1;
					if (degd > contThresh)
					{
						if (degd > contThresh * 1.5)
							g.setColor(c);
						else if (degd > contThresh * 1.25)
							g.setColor(c2);
						else g.setColor(c3);
						
						g.fillRect( (int)(bs.minX + i*bs.cellSize - bigGridSX),								
								    (int)(bs.minY + k*bs.cellSize - bigGridSY),
								    (int)bs.cellSize, (int)bs.cellSize);
					}
				}
			}
			
			
			Point2D.Double[][] lin = Util.marchingSquares(bs.grid, bs.cellSize, new Point2D.Double(bs.minX, bs.minY), contThresh);
			
			
			int lum = (c.getRed() + c.getGreen() + c.getBlue())/3;
			lum = (int)(0.3*lum);
			
			int minc = Math.min(c.getRed(), Math.min(c.getGreen(), c.getBlue()));
			int maxc = Math.max(c.getRed(), Math.max(c.getGreen(), c.getBlue()));
			
			double re = lum + (200-lum)*(c.getRed()-minc)/((double)maxc-minc);
			double gr = lum + (200-lum)*(c.getGreen()-minc)/((double)maxc-minc);
			double bl = lum + (200-lum)*(c.getBlue()-minc)/((double)maxc-minc);
			
			Color c4 = new Color((int)re, (int)gr, (int)bl, 75);
		    g.setColor(c4);
		    g.setStroke(new BasicStroke(4));
		    
		    Path2D.Double path = new Path2D.Double();
		   for(int i=0;i<lin.length;i++)
		   {	
			   if (lin[i].length > 0) path.moveTo((int)lin[i][0].x  - bigGridSX,(int)lin[i][0].y - bigGridSY); 
			   for (int k=1; k<lin[i].length; k++)	
			   {
				   Point2D.Double p1 = lin[i][k-1];
				   Point2D.Double p2 = lin[i][k];
		        //  g.drawLine((int)lin[i][k-1].x  - bigGridSX, (int)lin[i][k-1].y - bigGridSY, (int)lin[i][k].x  - bigGridSX, (int)lin[i][k].y- bigGridSY);
				   path.lineTo((int)lin[i][k].x  - bigGridSX,(int)lin[i][k].y - bigGridSY);
			   }
			   path.closePath();
		    }
		   g.draw(path);			 
		}
		
		
	}
	}

}
