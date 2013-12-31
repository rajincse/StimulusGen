package util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import Graph.ForceGraphDrawer;

import perspectives.DefaultProperties.*;
import perspectives.Property;
import perspectives.PropertyType;

public abstract class NodeLinkViewer extends Points2DViewer {

	private boolean[] edgesSelected;
	
	private Color edgeColor;
	private Color selectedEdgeColor;
	
	private int edgeThickness;
	
	protected abstract String getNodeLabel(int p);
	protected abstract int getNumberOfNodes();
	protected abstract int getNodeX(int p);
	protected abstract int getNodeY(int p);
	protected abstract void setNodeX(int p, int x);
	protected abstract void setNodeY(int p, int y);
	
	protected abstract int[] getEdgeSources();
	protected abstract int[] getEdgeTargets();
	
	private boolean[] edgeBidir;
	private boolean[] edgeBidirSkip;
	
	private boolean directed;
	
	private boolean drawSelfEdges = false;
	
	public NodeLinkViewer(String name) {
		super(name);
		
		edgesSelected = null;
		
		directed = false;
		
		edgeColor = Color.black;
		selectedEdgeColor = Color.red;
		edgeThickness = 1;
		
		edgeBidir = null;
		
		try {
			Property<IntegerPropertyType> p4 = new Property<IntegerPropertyType>("Appearance.Node Size");
			p4.setValue(new IntegerPropertyType(10));
			this.addProperty(p4);
			
			Property<ColorPropertyType> p7 = new Property<ColorPropertyType>("Appearance.Node Color");
			p7.setValue(new ColorPropertyType(new Color(200,150,150)));
			this.addProperty(p7);
			
			Property<PercentPropertyType> p8 = new Property<PercentPropertyType>("Appearance.Node Alpha");
			p8.setValue(new PercentPropertyType(0.5));
			this.addProperty(p8);
			
			Property<ColorPropertyType> p6 = new Property<ColorPropertyType>("Appearance.Edge Color");
			p6.setValue(new ColorPropertyType(new Color(200,150,150)));
			this.addProperty(p6);
			
			Property<ColorPropertyType> p9 = new Property<ColorPropertyType>("Appearance.Sel Edge Color");
			p9.setValue(new ColorPropertyType(new Color(100,50,50)));
			this.addProperty(p9);
			
			Property<PercentPropertyType> p5 = new Property<PercentPropertyType>("Appearance.Edge Alpha");
			p5.setValue(new PercentPropertyType(0.2));
			this.addProperty(p5);
		}
		catch (Exception e) {		
			e.printStackTrace();
		}
	}
	
	public void setDrawSelfEdges(boolean v)
	{
		drawSelfEdges = v;
	}
	
	public boolean getDrawSelfEdges()
	{
		return drawSelfEdges;
	}
	
	public <T extends PropertyType> void propertyUpdated(Property p, T newvalue)
	{		
		if (p.getName() == "Appearance.Node Size")
			this.setNodeSize(((IntegerPropertyType)newvalue).intValue());
		if (p.getName() == "Appearance.Node Color")
			this.setColor(((ColorPropertyType)newvalue).colorValue());
		if (p.getName() == "Appearance.Edge Color")
			this.setEdgeColor(((ColorPropertyType)newvalue).colorValue());
		if (p.getName() == "Appearance.Node Alpha")
		{
			int alpha = (int)(255.*((PercentPropertyType)newvalue).getRatio());
			for (int i=0; i<getNumberOfNodes(); i++)
			{
				Color c = this.getColor(i);
				this.setColor(i,new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
			}
		}
		if (p.getName() == "Appearance.Sel Edge Color")
		{
			int alpha = this.getEdgeColor().getAlpha();
			Color c = this.getSelectedEdgeColor();
			this.setSelectedEdgeColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
		}
		if (p.getName() == "Appearance.Edge Alpha")
		{
			int alpha = (int)(255.*((PercentPropertyType)newvalue).getRatio());
			Color c = this.getEdgeColor();
			Color sc = this.getSelectedEdgeColor();
			this.setEdgeColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
			this.setSelectedEdgeColor(new Color(sc.getRed(), sc.getGreen(), sc.getBlue(), alpha));
		}	
		else super.propertyUpdated(p, newvalue);

	}

	@Override
	public void render(Graphics2D g) {
		
		if (directed && edgeBidir == null)
			this.computeEdgeBidir();
		
		
		super.render(g);
		renderEdges(g);
		
		
		
		

	}



	public void renderEdges(Graphics2D g)
	{									
		int[] e1 = getEdgeSources();
		int[] e2 = getEdgeTargets();
		
		if (edgesSelected == null)
		{
			edgesSelected = new boolean[e1.length];
			for (int i=0; i<edgesSelected.length; i++)
				edgesSelected[i] = false;
		}

		for (int i=0; i<e1.length; i++)
			renderEdge(e1[i], e2[i], i, edgesSelected[i], g);
	}
	
	public void renderEdge(int p1, int p2, int edgeIndex, boolean selected, Graphics2D g)
	{
		if (directed && edgeBidirSkip[edgeIndex])
			return;
		
		int x1 = getPointX(p1);
		int y1 = getPointY(p1);
		int x2 = getPointX(p2);
		int y2 = getPointY(p2);	
		
		int ns1 = getNodeSize(p1);
		int ns2 = getNodeSize(p2);
		
		int ss = ns1; //self node arc
		
		
		if (selected)
		{
			g.setColor(selectedEdgeColor);
			g.setStroke(new BasicStroke(2));
			
			if (p1 != p2)
			{
				if (!directed)
					g.drawLine(x1, y1, x2, y2);
				else
					Util.drawArrow(g, x1, y1, x2, y2, edgeBidir[edgeIndex], (int)(0.3*ns2), ns2/2);
			}
			else if (this.drawSelfEdges)
				g.drawArc(x1-ss/2,y1-(int)(1.2*ss),ss, ss, -60, 300);
		}
		else
		{
			g.setColor(edgeColor);
			g.setStroke(new BasicStroke(1));
			if (p1 != p2)
			{
				if (!directed)
					g.drawLine(x1, y1, x2, y2);
				else
					Util.drawArrow(g, x1, y1, x2, y2, edgeBidir[edgeIndex], (int)(0.3*ns2), ns2/2);
			}
				
			else if (this.drawSelfEdges)
				g.drawArc(x1-ss/2,y1-(int)(1.2*ss),ss, ss, -60, 300);
		}
	}

	protected void pointSelected(int p) {		
		nodeSelected(p);
	}
	
	protected void pointDeselected(int p)
	{
		nodeDeselected(p);
	}

	protected void nodeDeselected(int p) {
		
		int[] e1 = getEdgeSources();
		int[] e2 = getEdgeTargets();
		
		if (edgesSelected == null || edgesSelected.length != e1.length)
		{
			edgesSelected = new boolean[e1.length];
			for (int i=0; i<edgesSelected.length; i++)
				edgesSelected[i] = false;
		}
		for (int i=0; i<e1.length; i++)
		{
			if (e1[i] == p || e2[i] == p)
				edgesSelected[i] = false;
		}			
	}
	
	public int[] getSelectedEdges()
	{
		ArrayList<Integer> indeces = new ArrayList<Integer>();
		if (edgesSelected.length == 0)
			return new int[0];
		
		for (int i=0; i<edgesSelected.length; i++)
			if (edgesSelected[i])
				indeces.add(i);
		
		int[] r = new int[indeces.size()];
		for (int i=0; i<r.length; i++)
			r[i] = indeces.get(i);
		
		return r;
	}
	
	protected void pointSelectionChanged()
	{	
		if (getSelectedPoints().length == 0)
		{
			if (edgesSelected == null)
			{
				int[] e1 = getEdgeSources();
				int[] e2 = getEdgeTargets();
				edgesSelected = new boolean[e1.length];
			}				
			for (int i=0; i<edgesSelected.length; i++)
				edgesSelected[i] = false;		
		}
	}
	
	public void clearEdgeSelection()
	{
		if (edgesSelected != null)
			for (int i=0; i<edgesSelected.length; i++)
				edgesSelected[i] = false;
		edgeSelectionChanged();
	}
	
	public void selectEdge(int p1, int p2)
	{
		int[] e1,e2;
		e1 = getEdgeSources();
		e2 = getEdgeTargets();
		
		if (e1 == null || e2 == null)
			return;
		
		for (int i=0; i<e1.length; i++)
		{
			if ((e1[i] == p1 && e2[i] == p2) || (e1[i] == p2 && e2[i] == p1))
			{
				edgesSelected[i] = true;
				edgeSelected(p1,p2);
				edgeSelectionChanged();
				break;
			}
		}
	}
	
	public void deselectEdge(int p1, int p2)
	{
		int[] e1,e2;
		e1 = getEdgeSources();
		e2 = getEdgeTargets();
		
		if (e1 == null || e2 == null)
			return;
		
		for (int i=0; i<e1.length; i++)
		{
			if ((e1[i] == p1 && e2[i] == p2) || (e1[i] == p2 && e2[i] == p1))
			{
				edgesSelected[i] = false;
				edgeDeselected(p1,p2);
				edgeSelectionChanged();
				break;
			}
		}		
	}
	
	protected void edgeSelectionChanged()
	{
		
	}
	
	protected void edgeSelected(int p1, int p2)
	{
	}
	
	protected void edgeDeselected(int p1, int p2)
	{
	}

	@Override
	protected String getPointLabel(int p) {
		return getNodeLabel(p);
	}

	@Override
	protected int getNumberOfPoints() {
		return getNumberOfNodes();
	}

	@Override
	protected int getPointX(int p) {
		return getNodeX(p);
	}

	protected int getPointY(int p) {
		return getNodeY(p);
	}

	protected void setPointX(int p, int x) {
		setNodeX(p, x);
	}

	@Override
	protected void setPointY(int p, int y) {
		setNodeY(p, y);
		
	}
	
	public void renderPoints(Graphics2D g)
	{
		this.renderNodes(g);
	}
	
	public void renderNodes(Graphics2D g)
	{
		super.renderPoints(g);
	}

	protected void renderPoint(int p, boolean selected, Graphics2D g)
	{
		this.renderNode(p, selected, g);
	}
	protected void renderNode(int p, boolean selected, Graphics2D g) {
		super.renderPoint(p, selected, g);
	}

	public void clearNodeSelection() {
		super.clearPointSelection();
	}


	public void deselectNode(int p) {
		super.deselectPoint(p);
	}
	public void deselectPoint(int p)
	{
		deselectNode(p);
	}
	

	public int[] getSelectedNodes() {
		return super.getSelectedPoints();
	}

	protected void nodeSelected(int p) {
		super.pointSelected(p);
		
		int[] e1 = getEdgeSources();
		int[] e2 = getEdgeTargets();
		
		if (edgesSelected == null || edgesSelected.length != e1.length)
		{
			edgesSelected = new boolean[e1.length];
			for (int i=0; i<edgesSelected.length; i++)
				edgesSelected[i] = false;
		}
		
		for (int i=0; i<e1.length; i++)
		{
			if (e1[i] == p || e2[i] == p)
				edgesSelected[i] = true;
		}
	}


	protected void nodetMoved(int point, int deltaX, int deltaY) {
		super.pointMoved(point, deltaX, deltaY);
	}
	
	public void setNodeSize(int s)
	{
		super.setPointSize(s);
	}
	
	public int getNodeSize(int p)
	{
		return super.getPointSize(p);
	}	
	
	public void setEdgeColor(Color c)
	{
		edgeColor = c;
	}
	
	public Color getEdgeColor()
	{
		return edgeColor;
	}
	
	public void setSelectedEdgeColor(Color c)
	{
		selectedEdgeColor = c;
	}
	
	public Color getSelectedEdgeColor()
	{
		return selectedEdgeColor;
	}	
	
	public void setEdgeThickness(int t)
	{
		edgeThickness = t;
	}
	
	public int getEdgeThickness()
	{
		return edgeThickness;
	}
	///////// reimplement the points methods for naming purpuses: points->nodes ///
	
	private void computeEdgeBidir()
	{
		int[] e1 = getEdgeSources();
		int[] e2 = getEdgeTargets();
		
		edgeBidir = new boolean[e1.length];
		edgeBidirSkip = new boolean[e1.length];
		
		for (int i=0; i<e1.length; i++)
		{
			edgeBidir[i] = false;
			edgeBidirSkip[i] = false;		
		}
		for (int i=0; i<e1.length; i++)
		{
			for (int j=i+1; j<e1.length; j++)
				if (e1[i] == e2[j] && e2[i] == e1[j])
				{
					edgeBidir[i] = true;
					edgeBidir[j] = true;
					edgeBidirSkip[j] = true;
				}
		}
	}
	
	public void setDirected(boolean d)
	{
		directed = d;
	}
	
	public boolean getDirected()
	{
		return directed;
	}
	
	public void selectPoint(int p)
	{
		selectNode(p);
	}
	public void selectNode(int p)
	{
		super.selectPoint(p);
		
		int[] e1 = getEdgeSources();
		int[] e2 = getEdgeTargets();
		
		if (edgesSelected == null || edgesSelected.length != e1.length)
		{
			edgesSelected = new boolean[e1.length];
			for (int i=0; i<edgesSelected.length; i++)
				edgesSelected[i] = false;
		}
		
		for (int i=0; i<e1.length; i++)
		{
			if (e1[i] == p || e2[i] == p)
				edgesSelected[i] = true;
		}
		
		
	}
	

}
