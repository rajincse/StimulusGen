package tree;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import perspectives.DefaultProperties.BooleanPropertyType;
import perspectives.DefaultProperties.DoublePropertyType;
import perspectives.Property;

import Graph.Graph;

public class Tree
{
	private TreeNode root;
	
	private HashMap<String, TreeNode> idToNode;
	
	private HashMap<String,Float> heights;
	
	public Tree(Graph g)
	{				
		idToNode = new HashMap<String, TreeNode>();
		heights = new HashMap<String, Float>();
					
		this.fromGraph(g);
	}

	
	public Tree(TreeNode root)
	{
		this.root = root;		
		
		idToNode = new HashMap<String, TreeNode>();
		idToNode.put(root.id(), root);
		
		heights = new HashMap<String, Float>();
		heights.put(root.id(), new Float(0));
	}
	
	public Tree(File f, String format)
	{
		this.root = null;
		idToNode = new HashMap<String,TreeNode>();
		
		heights = new HashMap<String, Float>();
		
		fromFile(f, format);
	}
	
	public void setRoot(TreeNode newNode)
	{
		newNode.parent = null;
		idToNode.put(newNode.id(), newNode);		
		setHeight(newNode.id(), 0);	
		
		root = newNode;
	}
	
	public void addNode(TreeNode parent, TreeNode newNode)
	{
		if (parent == null) return;
		
		parent.childs.add(newNode);
		newNode.parent = parent;
		idToNode.put(newNode.id(), newNode);
		
		setHeight(newNode.id(), getHeight(parent.id()) + 1);
	}
	
	public void addNode(String parent, TreeNode newNode)
	{
		TreeNode pn = idToNode.get(parent);
		if (pn == null) return;
		
		pn.childs.add(newNode);
		newNode.parent = pn;
		idToNode.put(newNode.id(), newNode);
		
		setHeight(newNode.id(), getHeight(parent) + 1);
	}
	
	public TreeNode treeNode(String e)
	{
		TreeNode tn = idToNode.get(e);
		return tn;
	}
	
	public void removeNode(TreeNode n)
	{
		if (n.parent != null)
		{
			int index = n.parent.childs.indexOf(n);
			n.parent.childs.remove(n);
			
			ArrayList<String> ids = nodesInSubtree(n);
			for (int i=0; i<ids.size(); i++)
				idToNode.remove(ids.get(i));			
		}
	}
	
	public TreeNode root()
	{
		return root;
	}
	
	public void setHeight(String id, float height)
	{
		if (heights.containsKey(id))
			heights.remove(id);
		
		heights.put(id, new Float(height));
	}
	
	public float getHeight(String id)
	{
		Float f = heights.get(id);
		if (f != null)
			return f.floatValue();
		else return Float.POSITIVE_INFINITY;
	}
	
	public ArrayList<String> nodesInSubtree(TreeNode n)
	{
		ArrayList<String> r = new ArrayList<String>();
		r.add(n.id());
		
		for (int i=0; i<n.childs.size(); i++)
			r.addAll(nodesInSubtree(n.childs.get(i)));
		
		return r;	
	}
	
	public int nodeCount()
	{
		return idToNode.size();
	}
	
	public ArrayList<String> nodes()
	{
		return nodesInSubtree(root);
	}
	
	public boolean isLeaf(String n)
	{
		TreeNode nn = treeNode(n);
		return nn.isLeaf();
	}
	
	public void fromFile(File f, String format)
	{
		if (format == "Newick")
		{
			try{
			 FileInputStream fstream = new FileInputStream(f);
			 DataInputStream in = new DataInputStream(fstream);
			 BufferedReader br = new BufferedReader(new InputStreamReader(in));
			 String s;
			 if ((s = br.readLine()) != null)
			  parseNewickTree(s);			 
			 in.close();
			}
			catch(Exception e)
			{
				
			}
		}
		else if (format == "GraphML")
		{
			Graph g = new Graph(false);
			g.fromGraphML(f);
			this.fromGraph(g);
		}
	}
	
	public void toFile(File f, String format)
	{
		if (format == "Newick")
		{
			try{
				 FileOutputStream fstream = new FileOutputStream(f);
				 DataOutputStream out = new DataOutputStream(fstream);
				 String s = newickTree(root) + ";";
				 out.writeChars(s);				 
				 out.close();
				}
				catch(Exception e)
				{
					
				};
		}
		else if (format == "GraphML")
		{
			Graph g = this.asGraph();
			g.toGraphML(f);
		}		
	}
	
	public Graph asGraph() 
	{
		Graph g = new Graph(false);
		asGraph(g, root);
		return g;
	}
	
	private void asGraph(Graph g, TreeNode n)
	{
		g.addNode(n.id());
		ArrayList<Property> props = n.allProperties();
		for (int i=0; i<props.size(); i++)
		{
			g.addNodeProperty(n.id(), props.get(i));
			
			Float f = heights.get(n.id());
			if (f != null)
			{
				Property p = new Property<DoublePropertyType>("height");
				p.setValue(new DoublePropertyType(heights.get(n.id())));
				g.addNodeProperty(n.id(), p);
			}
		}
		
		for (int i=0; i<n.childs.size(); i++)
		{
			String c = n.childs.get(i).id();
			g.addEdge(n.id(), c);
			asGraph(g,n.childs.get(i));
		}
		
		Property<BooleanPropertyType> p = new Property<BooleanPropertyType>("root");
		p.setValue(new BooleanPropertyType(true));
		g.addNodeProperty(root().id(), p);
		
	}
	
	private void fromGraph(Graph g)
	{
		ArrayList<String> nodes = g.getNodes();
		for (int i=0; i<nodes.size(); i++)
		{
			Property p = g.nodeProperty(nodes.get(i), "root");
			if (p != null && ((BooleanPropertyType)p.getValue()).boolValue())
				setRoot(new TreeNode(nodes.get(i)));
		}
		
		ArrayList<String> stack = new ArrayList<String>();
		stack.add(root.id());
		while (stack.size() > 0)
		{
			String e = stack.get(0);
			stack.remove(0);
			
			ArrayList<String> c = g.neighbors(e);
			for (int i=0; i<c.size(); i++)
			{
				if (treeNode(c.get(i)) != null)
					continue;
				
				stack.add(c.get(i));
				
				TreeNode tn = new TreeNode(c.get(i));
				this.addNode(e, tn);				
			}
		}
	}

	private void parseNewickTree(String s)
	{
		String[] split = s.split(";");
		for (int i=0; i<split.length; i++)
			parseNewickSubtree(root,split[i]);
	}
	
	private void parseNewickSubtree(TreeNode parent, String s)
	{
		int index = s.lastIndexOf(":");
		String s1;
		if (index >= 0)
			s1 = s.substring(index);
		else
			s1 = "NaN" + "";
		
		float f = Float.parseFloat(s1);
		boolean h = false;
		if (!Float.isInfinite(f) && !Float.isNaN(f))
		{
			h = true;
			s = s.substring(0, s.lastIndexOf(":")-1);
		}
		
		index = s.lastIndexOf(')');
		
		if (index < 0) //leaf
		{
			TreeNode n = new TreeNode(s);
			this.addNode(parent, n);
			if (h) setHeight(s,f);
			return;
		}
		
		//internal
		String branchSet = s.substring(1,index);
		String branchName = s.substring(index+1);
		
		TreeNode n = new TreeNode(branchName);
		
			
		if (parent == null) setRoot(n);
		else this.addNode(parent, n);
		
		parseNewickBranchset(n,branchSet);	
		
		if (h) setHeight(branchName,f);
	}
	
	private void parseNewickBranchset(TreeNode parent, String s)
	{
		String st = "";
		int pc = 0;
		for (int i=0; i<s.length(); i++)
		{
			if (s.charAt(i) == ',')
			{
				if (pc == 0)
				{
					parseNewickSubtree(parent,st);
					st = "";
				}
				else st = st + ',';
			}
			else if (s.charAt(i) == '(')
			{
				pc++;
				st = st + '(';
			}
			else if (s.charAt(i) == ')')
			{
				pc--;
				st = st + ')';
			}
			else
				st = st + s.charAt(i);
		}
		
		if (pc == 0)
			parseNewickSubtree(parent,st);
	}
	
	private String newickTree(TreeNode n)
	{
		if (n.childs.size() == 0)
			return n.id() + ":" + heights.get(n.id());
		
		String r = "(";
		for (int i=0; i<n.childs.size(); i++)
		{
			r += newickTree(n.childs.get(i));
			if (i != n.childs.size()-1)
				r = r + ",";
		}
		r = r + ")";
		r = r + n.id() + ":" + heights.get(n.id());
		
		return r;		
	}
		
}
