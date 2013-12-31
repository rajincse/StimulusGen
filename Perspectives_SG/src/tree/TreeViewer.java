package tree;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.util.ArrayList;

import perspectives.DefaultProperties;
import perspectives.DefaultProperties.BooleanPropertyType;
import perspectives.DefaultProperties.ListPropertyType;
import perspectives.DefaultProperties.OpenFilePropertyType;
import perspectives.Property;
import perspectives.PropertyManager;
import perspectives.PropertyType;
import perspectives.Viewer2D;
import util.NodeLinkViewer;
import Graph.ForceGraphDrawer;
import Graph.Graph;
import Graph.GraphData;
import Graph.GraphDrawer;

public class TreeViewer extends NodeLinkViewer {

	Tree tree;
	
	TreeDrawer drawer;
	
	double nodeSize;
	
	private String[] treeNodes;
	private int[] edgeTargets;
	private int[] edgeSources;
	
	boolean prevdone = false;
	
	protected boolean drawOrthogonal = false;
	
	protected TreeViewer(String name)
	{
		super(name);
		createProperties();
	}
	
	public TreeViewer(String name, TreeData t) {
		super(name);
		
		tree = t.tree;
		
		setTree(tree);
		createProperties();
	}
	
	public void setTree(Tree t)
	{
		tree = t;
		
		ArrayList<String> tn = tree.nodes();
		treeNodes = new String[tn.size()];
		for (int i=0; i<tn.size(); i++)
			treeNodes[i] = tn.get(i);
		
		
		ArrayList<String> e1 = new ArrayList<String>();
		ArrayList<String> e2 = new ArrayList<String>();
		computeTreeEdges(tree.root(), e1, e2);
		
		edgeTargets = new int[e1.size()];
		edgeSources = new int[e1.size()];
		ArrayList<String> nodes = tree.nodes();
		for (int i=0; i<e1.size(); i++)
		{
			edgeSources[i] = nodes.indexOf(e1.get(i));
			edgeTargets[i] = nodes.indexOf(e2.get(i));
		}
		
		drawer = new DefaultTreeDrawer(tree);
		
		ListPropertyType list = new ListPropertyType();
		list.items = new String[this.getNumberOfPoints()];
		for (int i=0; i<list.items.length; i++)
			list.items[i] = this.getPointLabel(i);
		list.selectedIndeces = new int[0];
		
		getProperty("Items").setValue(list);
		//this.sortItemList();
	}
	
	protected void createProperties()
	{
		try
		{
			Property<BooleanPropertyType> p4 = new Property<BooleanPropertyType>("Appearance.Orthogonal");
			p4.setValue(new BooleanPropertyType(true));
			this.addProperty(p4);
		}
		catch (Exception e) {		
			e.printStackTrace();
		}
	}
	
	private void computeTreeEdges(TreeNode n, ArrayList<String> e1, ArrayList<String> e2)
	{
		if (n == null)
			return;
		
		TreeNode[] c = n.children();
		for (int i=0; i<c.length; i++)
		{
			e1.add(n.id());
			e2.add(c[i].id());
			computeTreeEdges(c[i],e1,e2);	
		}
	}


	public void simulate() {
		boolean done = drawer.iteration();
		if (prevdone == false && done)
		{
			
			
			//set heights;
			/*
			ArrayList<String> nodes = tree.nodes();
			for (int i=0; i<nodes.size(); i++)
			{
				Property<Double> p = tree.treeNode(nodes.get(i)).property("Height");
				if (p!=null)
					drawer.setY(nodes.get(i), -20*(int)p.getValue().doubleValue());
			}*/
			
			doneComputingPositions();
		}
		prevdone = done;
	}
	

	
	protected void doneComputingPositions()
	{
		
	}
	
	public <T extends PropertyType> void propertyUpdated(Property p, T newvalue)
	{
		if (p.getName() == "treesave")
			tree.toFile(new File(((OpenFilePropertyType)newvalue).path), "GraphML");
		else if (p.getName() == "Appearance.Orthogonal")
			drawOrthogonal = ((BooleanPropertyType)newvalue).boolValue();
		else
			super.propertyUpdated(p, newvalue);
	}

	@Override
	public void renderEdge(int p1, int p2, int edgeIndex, boolean selected,
			Graphics2D g) {
		// TODO Auto-generated method stub
		if (!drawOrthogonal)
			super.renderEdge(p1, p2, edgeIndex, selected, g);
		else{
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
			
			
			g.drawLine(x1, y1, x2, y1);
			g.drawLine(x2, y1, x2, y2);
		}
	}

	@Override
	protected String getNodeLabel(int p) {
		return treeNodes[p];
	}

	@Override
	protected int getNumberOfNodes() {
		if (tree == null)
			return 0;
		return treeNodes.length;
	}

	@Override
	protected int getNodeX(int p) {
		return drawer.getX(treeNodes[p]);
	}

	@Override
	protected int getNodeY(int p) {
		return drawer.getY(treeNodes[p]);
	}

	@Override
	protected void setNodeX(int p, int x) {
		drawer.setX(treeNodes[p], x);
		
	}

	@Override
	protected void setNodeY(int p, int y) {
		drawer.setY(treeNodes[p], y);
	}

	@Override
	protected int[] getEdgeSources() {
		return edgeSources;
	}

	@Override
	protected int[] getEdgeTargets() {
		// TODO Auto-generated method stub
		return edgeTargets;
	}


}
