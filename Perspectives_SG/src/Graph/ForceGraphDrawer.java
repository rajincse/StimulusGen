package Graph;
import java.util.ArrayList;
import java.util.HashSet;

import util.Util;


public class ForceGraphDrawer extends GraphDrawer {

	double k_rep, k_att, spring_length, max_step;
	
	int[] nodeDegrees;
	
	public ForceGraphDrawer(Graph g) {
		super(g);
		
		k_rep = 100;
		k_att = 100.0;		
		max_step = 100;		
		spring_length = 30;	
		
		nodeDegrees = null;
		
		cacheNodeDegrees();
	}

	public void iteration() {
		
		//init forces
		double[] fx = new double[graph.numberOfNodes()];
		double[] fy = new double[graph.numberOfNodes()];
		
		for (int i=0; i<fx.length; i++)
		{
			fx[i] = 0; fy[i] = 0;
		}
		
		ArrayList<String> nodes = graph.getNodes();
		
		//compute repulsive forces
		for (int i=0; i<nodes.size()-1; i++)
		{			
			double x1 = getX(i);
			double y1 = getY(i);
			
			int deg1 = nodeDegrees[i];
						
			for (int j=i+1; j<nodes.size(); j++)
			{				
				double x2 = getX(j);
				double y2 = getY(j);
				
				double d = Util.distanceBetweenPoints(x1, y1, x2, y2);
				while (d < 1)
				{
					x2 = x2 + Math.random();
					y2 = y2 + Math.random();
					d = Util.distanceBetweenPoints(x1, y1, x2, y2);
				}
				
				double vx = x2-x1;
				double vy = y2-y1;
				double vl = Math.sqrt(vx*vx + vy*vy);
				vx = vx/vl;
				vy = vy/vl;
				
				double mag = -k_rep/(d*d);
				
				double deg2 = nodeDegrees[j];
				
				mag = mag * Math.sqrt((Math.min(deg1, deg2)));
				
				if (deg1 == 1 || deg2 == 1)
					mag = mag/10;
				
				fx[i] = fx[i] + vx *mag;
				fy[i] = fy[i] + vy *mag;
				fx[j] = fx[j] - vx *mag;
				fy[j] = fy[j] - vy *mag;			
			}
		}
		
		//compute spring forces
		ArrayList<Integer> e1 = new ArrayList<Integer>();
		ArrayList<Integer> e2 = new ArrayList<Integer>();
		graph.getEdgesAsIndeces(e1, e2);
		
		for (int i=0; i<e1.size(); i++)
		{
			int id1 = e1.get(i);
			double x1 = getX(id1);
			double y1 = getY(id1);	
			int id2 = e2.get(i);
			double x2 = getX(id2);
			double y2 = getY(id2);
			
			double d = Util.distanceBetweenPoints(x1, y1, x2, y2);
			
			if (d == 0) continue;
			
			double vx = x2-x1;
			double vy = y2-y1;
			double vl = Math.sqrt(vx*vx + vy*vy);
			vx = vx/vl;
			vy = vy/vl;
			
			double d1 = nodeDegrees[e1.get(i)];
			double d2 = nodeDegrees[e2.get(i)];		
			double degreeFactor = Math.sqrt((Math.min(d1,d2)));
			double adjusted_spring_length = spring_length * degreeFactor;
			
			if (d < adjusted_spring_length)
				continue;
			
			double mag = Math.signum(d-spring_length) * k_att*Math.pow((d-spring_length),1);
			
			mag /= (degreeFactor*degreeFactor * degreeFactor);
			
			fx[id1] = fx[id1] + vx *mag;
			fy[id1] = fy[id1] + vy *mag;
			fx[id2] = fx[id2] - vx *mag;
			fy[id2] = fy[id2] - vy *mag;		
		}
			
		
		//add forces to positions
		for (int i=0; i<nodes.size(); i++)
		{
			int x = getX(i);
			int y = getY(i);
			
			double fl = Math.sqrt(fx[i]*fx[i] + fy[i]*fy[i]);
			if (fl > max_step)
			{
				fx[i] = max_step * (fx[i]/fl);
				fy[i] = max_step * (fy[i]/fl);
			}
			setX(i,x + (int)fx[i]);
			setY(i,y + (int)fy[i]);
			
		}
		
		max_step = Math.max(2,max_step * 0.99);
		
	}
	
	private void cacheNodeDegrees()
	{
		ArrayList<String> nodes = graph.getNodes();
		nodeDegrees = new int[nodes.size()];
		
	
		for (int i=0; i<nodes.size(); i++)
		{
			ArrayList<String> n = graph.neighbors(nodes.get(i));
			nodeDegrees[i] = n.size();
			if (n.indexOf(nodes.get(i)) >= 0)
				nodeDegrees[i]--;
		}
		
		HashSet<String> deleted = new HashSet<String>();
		for (int k=0; k<10; k++)
		{
			System.out.println("deleted: " + deleted.size());
			//sw = false;
			for (int i=0; i<nodes.size(); i++)
				if (nodeDegrees[i] == 1)
					deleted.add(nodes.get(i));
			
			for (int i=0; i<nodes.size(); i++)
			{
				ArrayList<String> n = graph.neighbors(nodes.get(i));
				nodeDegrees[i] = n.size();
				for (int j=0; j<n.size(); j++)
					if (deleted.contains(n.get(j)))
					{
						nodeDegrees[i]--;
					}
			}
			
			
				
		}
	}
	

}
