package tree;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import perspectives.DefaultProperties.*;
import perspectives.PropertyManager;

import data.DistancedPoints;
import util.*;
import data.*;

public class HierarchicalClusteringViewer extends TreeViewer
{
	Tree tree;
	
	private boolean verticalLeafs = true;
	
	private boolean insideSelection = false;

	public HierarchicalClusteringViewer(String name, TreeData t) {
		super(name, t);		
	}
	

	public HierarchicalClusteringViewer(String name, TableData d) {
		
		super(name);
		
		tree = HierarchicalClustering.compute((DistancedPoints)d.getTable());
		
		this.setTree(tree);
		
	
		ArrayList<String> nodes = tree.nodes();
		ArrayList<String> leafs = new ArrayList<String>();
		for (int i=0; i<nodes.size(); i++)
		{
			if (tree.treeNode(nodes.get(i)).isLeaf())
			{
				setRotation(i,Math.PI/2);
				setHorizontalAlignment(i,Points2DViewer.HorizontalAlignment.LEFT);
				setAspect(i,Points2DViewer.PointAspectType.RECT_LABEL_FIT);
				leafs.add(nodes.get(i));
			}
			else
			{
				setAspect(i,Points2DViewer.PointAspectType.CIRCLE_NO_LABEL);
			}
		}
		
		
		ListPropertyType list = new ListPropertyType();
		list.items = leafs.toArray(new String[leafs.size()]);
		list.selectedIndeces = new int[0];		
		getProperty("Items").setValue(list);
		
		//this.sortItemList();
	}


	@Override
	protected void nodeSelected(int p) {
				
		ArrayList<String> nodes = tree.nodes();
		TreeNode pt = tree.treeNode(nodes.get(p));
		ArrayList<String> sel = tree.nodesInSubtree(pt);
		
		for (int i=0; i<sel.size(); i++)
			selectNode(nodes.indexOf(sel.get(i)));
		
		TreeNode parent = pt.parent;
		if (parent != null)
			this.deselectEdge(nodes.indexOf(parent.id()), p);
		
		//super.nodeSelected(p);
	}
	
	
	

}
