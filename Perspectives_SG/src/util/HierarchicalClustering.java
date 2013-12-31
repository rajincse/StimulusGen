package util;

import java.util.HashMap;

import perspectives.DefaultProperties.BooleanPropertyType;
import perspectives.Property;

import tree.Tree;
import tree.TreeNode;
import data.DistancedPoints;
import data.TableDistances;

import perspectives.DefaultProperties.*;

import Graph.*;

class Node{
	int left; 
	int right; 
	float distance;
}
class TwoInts
{
	int int1;
	int int2;
}

public class HierarchicalClustering
{
	
	public static Tree compute(DistancedPoints d)
	{	     
	        Tree tree = null;
	       
	        int idCounter = 0;
	      
			float[][] dm = new float[d.getCount()][];
			
			for (int i=1; i<dm.length; i++)
			{
				dm[i] = new float[i];
				for (int j=0; j<i; j++)
					dm[i][j] = d.getDistance(i,j);
			}					

			Node[] r = palcluster(dm.length,dm);
						
			Graph g = new Graph(false);
			
			for (int i=d.getCount()-2; i>=0; i--)
			{
				int ll = r[i].left;
				int rr = r[i].right;
				float dd = r[i].distance;

				String id = "-" + (i+1);				
				String lls = "";
				if (ll >= 0) lls = d.getPointId(ll);
				else lls = ""+ ll;
				String rrs = "";
				if (rr >= 0) rrs = d.getPointId(rr);
				else rrs = "" + rr;
				
				g.addEdge(id, lls);
				g.addEdge(id, rrs);
			}
			
			Property<BooleanPropertyType> p = new Property<BooleanPropertyType>("root");
			p.setValue(new BooleanPropertyType(true));
			g.addNodeProperty("-" + (d.getCount()-1), p);
			
			tree = new Tree(g);				
			
			for (int i=0; i<d.getCount()-1; i++)
			{
				Property<DoublePropertyType> pr = new Property<DoublePropertyType>("Height");
				pr.setValue(new DoublePropertyType(r[i].distance));
				tree.treeNode("-"+(i+1)).addProperty(pr);
				
			}
			
			for (int i=0; i<d.getCount(); i++)
			{
				Property<DoublePropertyType> pr = new Property<DoublePropertyType>("Height");
				pr.setValue(new DoublePropertyType(0));
				tree.treeNode(d.getPointId(i)).addProperty(pr);
				
			}
			
			return tree;
				    
	}
	
	private static Node[] palcluster (int nelements, float[][] distmatrix)
	{
		int[] clusterid;
		int[] number;
		Node[] result;
		
		clusterid = new int[nelements];
		number = new int[nelements];		
		result = new Node[nelements-1];
		for (int i=0; i<result.length; i++)
			result[i] = new Node();
		
		for (int j = 0; j < nelements; j++)
		{
			number[j] = 1;
			clusterid[j] = j;
		}
		
		for (int n = nelements; n > 1; n--)
		{
			int sum;

			
			TwoInts ints = new TwoInts();
			float d =  find_closest_pair(n,distmatrix, ints);
			int is = ints.int1;
			int js = ints.int2;
			System.out.println(d);
			result[nelements-n].distance = d;

			/* Save result */
			result[nelements-n].left = clusterid[is];
			result[nelements-n].right = clusterid[js];

			/* Fix the distances */
			sum = number[is] + number[js];
			for (int j = 0; j < js; j++)
			{
				distmatrix[js][j] = distmatrix[is][j]*number[is] + distmatrix[js][j]*number[js];
				distmatrix[js][j] /= sum;
			}
			for (int j = js+1; j < is; j++)
			{ 
				distmatrix[j][js] = distmatrix[is][j]*number[is] + distmatrix[j][js]*number[js];
				distmatrix[j][js] /= sum;			
			}
			
			for (int j = is+1; j < n; j++)
			{ 
				System.out.println(distmatrix[j].length);
				distmatrix[j][js] = distmatrix[j][is]*number[is] + distmatrix[j][js]*number[js];
				distmatrix[j][js] /= sum;
			}
			
			for (int j = 0; j < is; j++)
				{
				System.out.println("f: " + is + " " + j + " " + (n-1));
				distmatrix[is][j] = distmatrix[n-1][j];
				}
			for (int j = is+1; j < n-1; j++)
				{
				System.out.println("s: " + j + " " + is + " " + (n-1));
				distmatrix[j][is] = distmatrix[n-1][j];
				}
			


			/* Update number of elements in the clusters */
			number[js] = sum;
			number[is] = number[n-1];

			/* Update clusterids */
			clusterid[js] = n-nelements-1;
			clusterid[is] = clusterid[n-1];
	  }
	  return result;
	}
	
	
	private static float find_closest_pair(int n, float[][] distmatrix, TwoInts ints)
	{
		float distance = Float.MAX_VALUE;
		ints.int1 = 1;
		ints.int2 = 0;
		
		for (int i = 1; i < n; i++)
		{ 
			for (int j = 0; j < i; j++)
			{ 
				float temp = distmatrix[i][j];
				if (temp<distance)
				{
					distance = temp;
					ints.int1 = i;
					ints.int2 = j;
				}
			}
		}
		return distance;
	}

}


