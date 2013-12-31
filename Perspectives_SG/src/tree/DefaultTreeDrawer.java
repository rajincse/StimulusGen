
package tree;

import java.util.ArrayList;

import util.Vector2D;

public class DefaultTreeDrawer extends TreeDrawer 
{
	boolean done;

	public DefaultTreeDrawer(Tree t) {
		super(t);
		
		done = false;
	}

	@Override
	public boolean iteration() {
		
		if (done)
			return true;	
		
		drawSubTree(tree.root());
		
		setX(tree.root().id(), 0);
		setY(tree.root().id(), 0);
		
		drawSubTree2(tree.root());
		
		
		
		ArrayList<String> nodes = tree.nodes();
		for (int i=0; i<nodes.size(); i++)
			{
				setX(nodes.get(i), getX(nodes.get(i))*10);
				
				int y = getY(nodes.get(i));
				setY(nodes.get(i), y*10);
			}
		
		done = true;
		
		return true;
	}
	
	private Vector2D drawSubTree(TreeNode tn)
	{	
		if (tn.isLeaf())
			return new Vector2D(1,1);
		
		TreeNode[] children = tn.children();
		
		int width = 0;
		int height = 0;
		
		Vector2D[] bounds = new Vector2D[children.length];
		
		for (int i=0; i<children.length; i++)
		{
			Vector2D size = drawSubTree(children[i]);
			
			width = width + (int)size.x;
			if (i != children.length-1) width++;
			
			height = Math.max(height, (int)size.y);
			
			bounds[i] = size;
		}
		height++;
		
		int cx = -width/2;
		for (int i=0; i<children.length; i++)
		{
			cx = cx + (int)Math.floor(bounds[i].x/2.);
			
			setX(children[i].id(), cx);
			
			setY(children[i].id(), (int)bounds[i].y - height);
			
			if (i != children.length)
				cx = cx + (int)Math.ceil(bounds[i].x/2.) + 1;
		}
		
		return new Vector2D(width, height);		
	}
	
	private void drawSubTree2(TreeNode tn)
	{
		if (tn.isLeaf()) return;
		
		TreeNode[] children = tn.children();
		for (int i=0; i<children.length; i++)
		{
			setX(children[i].id(), getX(tn.id()) + getX(children[i].id()));
			setY(children[i].id(), getY(tn.id()) - getY(children[i].id()));
			drawSubTree2(children[i]);
		}
	}
	
}
