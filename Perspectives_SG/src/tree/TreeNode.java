package tree;

import java.util.ArrayList;

import perspectives.Property;

public class TreeNode
{
	ArrayList<TreeNode> childs; //package protected
	
	private String id;
	
	TreeNode parent;	//package protected
	
	ArrayList<Property> properties;
	
	public TreeNode(String id)
	{
		childs = new ArrayList<TreeNode>();
		this.id = id;
		parent = null;
		properties = new ArrayList<Property>();
	}
	
	public String id()
	{
		return id;
	}
	
	public boolean isLeaf()
	{
		if (childs.size() == 0) return true;
		return false;
	}
	
	public boolean isRoot()
	{
		if (parent == null)
			return true;
		return false;
	}
	
	public TreeNode[] children()
	{
		TreeNode[] r = new TreeNode[childs.size()];
		childs.toArray(r);
		return r;
	}
	
	public Property property(String name)
	{
		for (int i=0; i<properties.size(); i++)
			if (properties.get(i).getName() == name)
				return properties.get(i);
		return null;
	}
	
	public ArrayList<Property> allProperties()
	{
		return properties;
	}
	
	public void addProperty(Property p)
	{
		properties.add(p);
	}
	
	public void removeProperty(Property p)
	{
		int index = properties.indexOf(p);
		if (index < 0)
			return;
		
		properties.remove(index);
	}
	
	public void removeProperty(String name)
	{
		removeProperty(property(name));
	}
	
}
