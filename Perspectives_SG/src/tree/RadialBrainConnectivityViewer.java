package tree;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import data.TableData;
import data.Table;
import perspectives.DefaultProperties;
import perspectives.DefaultProperties.IntegerPropertyType;
import perspectives.DefaultProperties.OptionsPropertyType;
import perspectives.Property;
import perspectives.PropertyType;
import util.Points2DViewer;

public class RadialBrainConnectivityViewer extends CircleConnectivityViewer{

	Table allConnections = null;
	
	private boolean inRender = false;
	private boolean inmm = false;
	private boolean inmp = false;
	
	private boolean[] isLeaf = null;
	private double[] scale =  null;
	
	
	public RadialBrainConnectivityViewer(String name, TreeData d) {
		super(name, d);		
	}
	
	public RadialBrainConnectivityViewer(String name, TableData d) {
		super(name, d);	
		
		ArrayList<String> nodes = tree.nodes();
		for (int i=0; i<nodes.size(); i++)
			if (tree.treeNode(nodes.get(i)).isLeaf())
				setHorizontalAlignment(i,Points2DViewer.HorizontalAlignment.RIGHT);
		
		try
		{			
		}
		catch(Exception e)
		{
			
		}
	}
	
	public void loadConnections(String path)
	{
	     try {
	    	 allConnections = new Table();	    	 
	    	 allConnections.fromFile(path, "\t", true, false);
	    	 
	    	 Property<OptionsPropertyType> p = this.getProperty("Connectivity");
	    	 if (p == null)
	    		 p = new Property<OptionsPropertyType>("Connectivity");
	    	 OptionsPropertyType o = new OptionsPropertyType();
	    	 o.options = new String[allConnections.getColumnCount()-2];
	    	 for (int i=2; i<allConnections.getColumnCount(); i++)
	    		 o.options[i-2] = allConnections.getColumnName(i);
	    	 o.selectedIndex = 0;
	    	 p.setValue(o);
	    	 addProperty(p);
	    	 
	    	 setConnectivity(0);
	    	 applySimilarityColoring();
	    	  
	        } catch (Exception e) {
	            e.printStackTrace();
	        }     
	}
	
	public Color backgroundColor()
	{
		return new 	Color(0,0,0);
	}
	
	public double getDefaultZoom()
	{
		return 0.2;
	}
	
	@Override
	public <T extends PropertyType> void propertyUpdated(Property p, T newvalue) {
		// TODO Auto-generated method stub
		if (p.getName() == "Connectivity")
		{
			OptionsPropertyType o = (OptionsPropertyType)newvalue;
			setConnectivity(o.selectedIndex);	
			applySimilarityColoring();

		}
		else if (p.getName() == "Appearance.Node Size")
		{		
			super.propertyUpdated(p, newvalue);
			this.computeScale(((IntegerPropertyType)newvalue).intValue());
			
		}
		else
			super.propertyUpdated(p, newvalue);
	}

	
	public void setConnectivity(int which)
	{
		ArrayList<String> sources = new ArrayList<String>();
		ArrayList<String> targets = new ArrayList<String>();
		
		for (int i=0; i<allConnections.getRowCount(); i++)
		{
			double v = (Double)allConnections.getValueAt(i, which+2);
			if (v < 0.3)
				continue;
			sources.add((String)allConnections.getValueAt(i, 0));
			targets.add((String)allConnections.getValueAt(i, 1));			
		}
		
		this.setConnections(sources, targets);
		
		int index = 0;
		for (int i=0; i<allConnections.getRowCount(); i++)
		{
			double v = (Double)allConnections.getValueAt(i, which+2);
			if (v < 0.3)
				continue;
			
			v = (v-0.3)/0.7;
			
			System.out.println(v);
			
			int alpha = (int)(v*600);
			if (alpha > 255) alpha = 255;
			Color c = new Color(0,0,0, alpha);				
			this.setConnectionColor(index, c);
			index++;
		}			
		this.recomputeSplines(0.2);				
	}
	
	public void render(Graphics2D g){

			inRender = true;
			super.render(g);
			inRender = false;

	}
	
	@Override
	public boolean mousereleased(int x, int y, int button) {

			inmp = true;
			boolean ret = super.mousereleased(x, y, button);
			inmp = false;
			return ret;

	}
	
	@Override
	public boolean mousepressed(int x, int y, int button) {

			inmp = true;
			boolean ret = super.mousepressed(x, y, button);
			inmp = false;
			return ret;

	}

	@Override
	public boolean mousemoved(int x, int y) {

			inmm = true;
			boolean ret = super.mousemoved(x, y);
			inmm = false;
			return ret;

	}
	
	@Override
	protected int getNodeX(int p)
	{
		if ( (inRender || inmm || inmp) && !isLeaf[p])
			return (int)(super.getNodeX(p) * scale[p]);
		else return super.getNodeX(p);
	}
	@Override
	protected int getNodeY(int p)
	{
		if ( (inRender || inmm || inmp) && !isLeaf[p])
			return (int)(super.getNodeY(p) * scale[p]);
		else return super.getNodeY(p);
	}
	
	protected void doneComputingPositions()
	{
		super.doneComputingPositions();
		
		ArrayList<String> nodes = tree.nodes();
		isLeaf = new boolean[nodes.size()];
		int mx = 0;
		for (int i=0; i<nodes.size(); i++)
		{
			if (nodes.get(i).length() > mx)
				mx = nodes.get(i).length();
			
			if (tree.treeNode(nodes.get(i)).isLeaf())
				isLeaf[i] = true;
			else isLeaf[i] = false;
		}
		computeScale( ((IntegerPropertyType)getProperty("Appearance.Node Size").getValue()).intValue());
		
	
	}
	
	private void computeScale(int size)
	{
		ArrayList<String> nodes = tree.nodes();
		
		scale = new double[nodes.size()];		
		BufferedImage bi = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);	
		int fontSize = (int)(0.8*size);
		FontMetrics fm = bi.getGraphics().getFontMetrics(new Font("Sans-Serif",Font.PLAIN,fontSize));					
				
		int mx = 0;
		for (int i=0; i<nodes.size(); i++)
		{
			if (!tree.treeNode(nodes.get(i)).isLeaf())
				continue;
			
			java.awt.geom.Rectangle2D rect = fm.getStringBounds(nodes.get(i), bi.getGraphics());
			if (rect.getWidth() > mx)
				mx = (int)rect.getWidth();					
		}
		
		for (int i=0; i<nodes.size(); i++)
		{
			if (!tree.treeNode(nodes.get(i)).isLeaf())
			{
				int x = getNodeX(i);
				int y = getNodeY(i);
				
				double r = Math.sqrt(x*x + y*y);
				scale[i] = (r +  mx)/r;
			}
			else scale[i] = 1;
		}
	}

}
