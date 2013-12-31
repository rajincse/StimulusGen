package tree;

import java.util.ArrayList;
import java.util.HashMap;

import perspectives.DefaultProperties.IntegerPropertyType;
import perspectives.Property;
import util.Vector2D;


public abstract class TreeDrawer
{
	protected HashMap<String, Vector2D> xy;
	
	protected Tree tree;	
	
	public TreeDrawer(Tree t)
	{
		xy = new HashMap<String, Vector2D>();
		this.tree = t;
		
		ArrayList<String> nodes = t.nodes();
		for (int i=0; i<nodes.size(); i++)
		{
			TreeNode n = t.treeNode(nodes.get(i));
			
			if (n == null)
				continue;
			
			Property<IntegerPropertyType> p = n.property("x");
			if (p != null)
				setX(nodes.get(i),p.getValue().intValue());
			else
				setX(nodes.get(i),0);
			
			p = n.property("y");
			if (p != null)
				setY(nodes.get(i),p.getValue().intValue());
			else
				setY(nodes.get(i),0);
		}
	}
	
	public abstract boolean iteration();	
	
	public int getX(String id)
	{
		if (tree.treeNode(id) == null)
		{
			System.out.println("error in TreeDrawer, getX(" + id + "); no such id in graph");
			System.exit(0);
		}
		
		if (xy.containsKey(id))
			return (int) xy.get(id).x;
		
		xy.put(id, new Vector2D());
		return 0;
	}
	
	public int getY(String id)
	{
		if (tree.treeNode(id) == null)
		{
			System.out.println("error in TreeDrawer, getY(" + id + "); no such id in graph");
			System.exit(0);
		}
		
		if (xy.containsKey(id))
			return (int) xy.get(id).y;
		
		xy.put(id, new Vector2D());
		return 0;
	}
	
	public void setX(String id, int x)
	{
		if (tree.treeNode(id) == null)
		{
			System.out.println("error in TreeDrawer, setX(" + id + "); no such id in graph");
			System.exit(0);
		}
		
		if (xy.containsKey(id))
			xy.get(id).x = x;
		else
			xy.put(id, new Vector2D(x,0));
	}
	
	public void setY(String id, int y)
	{
		if (tree.treeNode(id) == null)
		{
			System.out.println("error in TreeDrawer, setY(" + id + "); no such id in graph");
			System.exit(0);
		}
		
		if (xy.containsKey(id))
			xy.get(id).y = y;
		else
			xy.put(id, new Vector2D(0,y));
	}

}
	

