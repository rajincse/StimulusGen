package Graph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Date;
import java.util.Queue;






public class GazeCorrector{
	
	GraphViewer gv;
	
	double[][] dx;
	double[][] dy;
	

	public boolean noGridUpdate = false;
	
	ArrayList<Point> fixations;
	ArrayList<Point> fixationsV;
	
	int[][] closestVisible;
	
		
	int scx;
	int scy;
	AffineTransform transf = null;
	
	int suggestedLastNode = -1;
	
	BufferedImage bim;
	Graphics2D g;
	
	public GazeCorrector(GraphViewer gv)
	{
		this.gv = gv;
		
		int screenWidth = 1900;
		int screenHeight = 1200;
		
		bim = new BufferedImage(screenWidth, screenHeight, BufferedImage.TYPE_INT_ARGB);
		g = bim.createGraphics();
		//g.setColor(new Color(250,250,200));
		//g.fillRect(0, 0, screenWidth, screenHeight);
		
		dx = new double[screenWidth/10+1][];
		dy = new double[screenWidth/10+1][];
		for (int i=0; i<dx.length; i++)
		{
			dx[i] = new double[screenHeight/10 +1];
			dy[i] = new double[screenHeight/10 +1];
		}	
		
		fixations = new ArrayList<Point>();
		fixationsV = new ArrayList<Point>();
	}
	
	public Point correctGaze(int x, int y)
	{
		
		
		if (x >= 0 && y>=0 && x/10<dx.length && y/10<dx[x/10].length)
		{
		x = (int)(x + dx[x/10][y/10]);
		y = (int)(y + dy[x/10][y/10]);
		
	//	System.out.println("correcting" + dx[x/10][y/10] + ","+dy[x/10][y/10]);
		}

		
		return new Point(x,y);
	}
	
	private void processFixations()
	{
		if (fixations.size() < 3)
			return;
		
		this.integrateFixation2();
	}
	

	
	
	
	public void integrateFixation2()
	{
		if (fixations.size() < 3)
			return;
		
	
		
		/////
		//for each fixations find the three closest graph nodes
		int[][] closest = new int[fixations.size()][];
		ArrayList<String> nodes = gv.graph.getNodes();
		
		for (int i=0; i<fixations.size(); i++)
		{
			closest[i] = new int[3];	
			
			double[] tmpd = new double[nodes.size()];		
			
			for (int j=0; j<nodes.size(); j++)
			{
				double d = Math.sqrt((fixationsV.get(i).x-gv.getNodeX(j))*(fixationsV.get(i).x-gv.getNodeX(j)) + (fixationsV.get(i).y-gv.getNodeY(j))*(fixationsV.get(i).y-gv.getNodeY(j)));
				tmpd[j] = d;
			}
			
			for (int j=0; j<closest[i].length; j++)
			{
				double mind = 99999999;
				int mini = -1;
				
				for (int k=0; k<tmpd.length; k++)
					if (tmpd[k] < mind)
					{
						mind = tmpd[k];
						mini = k;
					}
				
				closest[i][j] = mini;
				tmpd[mini] = 99999999;
			}
		}
		
		closestVisible = closest;
		//3 for loops, each for one of the three fixations
		double minscore = 999999999;
		Point[] bestns=null, bestgs=null, bestgsv=null;
		int[] bestnss = null;
		for (int i=-1; i<closest[0].length; i++)
			for (int j=-1; j<closest[1].length; j++)
				for (int k=-1; k<closest[2].length; k++)
				{
					int ctminus = 0;
					if (i == -1) ctminus++; if (j==-1) ctminus++; if (k==-1) ctminus++;
					
					if (ctminus > 0) continue;
					
					Point[] ns = new Point[3-ctminus];
					Point[] gs = new Point[3-ctminus];
					Point[] gsv = new Point[3-ctminus];
					int[] nss = new int[3-ctminus];
					int l = 0;
					
					if (i != -1)
					{
						ns[l] = new Point(gv.getNodeX(closest[0][i]), gv.getNodeY(closest[0][i]));
						gs[l] = fixations.get(0);
						gsv[l] = fixationsV.get(0);
						nss[l] = closest[0][i];
						l++;
					}
					if ( j != -1)
					{
						ns[l] = new Point(gv.getNodeX(closest[1][j]), gv.getNodeY(closest[1][j]));
						gs[l] = fixations.get(1);
						gsv[l] = fixationsV.get(1);
						nss[l] = closest[1][j];
						l++;
					}
					if (k != -1)
					{
						ns[l] = new Point(gv.getNodeX(closest[2][k]), gv.getNodeY(closest[2][k]));
						gs[l] = fixations.get(2);
						gsv[l] = fixationsV.get(2);
						nss[l] = closest[2][k];
					}					
					
					double score = scoreForFixationConfiguration(gsv, ns);
					
					
					
					if (score < minscore)
					{
						bestns = ns;
						bestgs = gs;
						bestgsv = gsv;
						bestnss = nss;
						minscore = score;
					}
				}
		
	//	System.out.println("score= " + minscore);
		
		if (minscore < 750)
		{
			//System.out.println("   correcting");
			this.suggestedLastNode = bestnss[bestnss.length-1];
			
			for (int i=0; !noGridUpdate && bestgs!= null &&  i<bestgs.length; i++)
			{
				double dispx = (bestns[i].x - bestgsv[i].x);
				double dispy= (bestns[i].y - bestgsv[i].y);
				
				int wx = bestgs[i].x/10;
				int wy = bestgs[i].y/10;
				
				for (int k=-5; k<=5; k++)
					for (int l=-5; l<=5; l++)
					{
						if (wx+k < 0 || wx+k >= dx.length || wy+l < 0 || wy+l >= dx[wx+k].length)
							continue;

						
						double f = (1-(k*k + l*l)/50.);
					
						double comb = f*(i+1.)/3.;
						dx[wx+k][wy+l] = dx[wx+k][wy+l]*(0.95-f/10) + (0.05+f/10)*dispx;
						dy[wx+k][wy+l] = dy[wx+k][wy+l]*(0.95-f/10) + (0.05+f/10)*dispy;
						
						double len = Math.sqrt(dx[wx+k][wy+l]*dx[wx+k][wy+l] + dy[wx+k][wy+l]*dy[wx+k][wy+l]);
						
						len /= 100;
						if (len > 1) len = 1;
						
						g.setColor(new Color(255, 255- (int)(100*len),  255 - (int)(100*len)));
						//System.out.println("here " + k + " " + l);
						//g.setColor(Color.red);
						g.fillRect((wx+k)*10, (wy+l)*10, 10,10);
						//g.setColor(new Color(150,100,100));
						//g.drawLine((wx+k)*10+3, (wy+l)*10+3, (wx+k)*10+3 + (int)(dx[wx+k][wy+l]/len), (wy+l)*10+3 + (int)(dy[wx+k][wy+l]/len));
					}

			}
		}	
			
	}
	
	private double scoreForFixationConfiguration(Point[] gs, Point[] ns)
	{
		//first, how close are the gazes to their corresponding nodes
		double avgd = 0;
		
		for (int i=0; i<gs.length; i++)
		{
			double d = Math.sqrt((gs[i].x-ns[i].x)*(gs[i].x-ns[i].x) + (gs[i].y-ns[i].y)*(gs[i].y - ns[i].y));
			avgd += d;
		}
		
		avgd = avgd/gs.length;
		
		//second, how similar the configuration is
		double avgv = 0;
		int ct = 0;
		
		for (int i=0; i<gs.length-1; i++)
		{
			for (int j=i+1; j<gs.length; j++)
			{
				ct++;
				int v1x = gs[i].x - gs[j].x;
				int v1y = gs[i].y - gs[j].y;
				
				if (v1x*v1x + v1y*v1y > 300*300)
					return 999999999;
				
				int v2x = ns[i].x - ns[j].x;
				int v2y = ns[i].y - ns[j].y;
				
				
				double d = (v1x-v2x)*(v1x-v2x)+ (v1y-v2y)*(v1y-v2y);
				avgv += d;
			}
		}

			avgv /= ct;
		
		return avgd*avgv / (gs.length*gs.length*gs.length*gs.length);
		

	}


	public void fixationDetected(Point fixation) {
		
		long t = new Date().getTime();
		int x = fixation.x;
		int y = fixation.y;
		fixations.add(new Point(x,y));
		Point pt = new Point();
		transf.transform(new Point(x+scx, y+scy),  pt);
		fixationsV.add(pt);
		
		for (int i=0; i<fixations.size()-1; i++)
		{
			double d = Math.sqrt((fixationsV.get(i).x - fixationsV.get(fixationsV.size()-1).x)*(fixationsV.get(i).x - fixationsV.get(fixationsV.size()-1).x) +
					(fixationsV.get(i).y - fixationsV.get(fixationsV.size()-1).y)*(fixationsV.get(i).y - fixationsV.get(fixationsV.size()-1).y));
			
			if (d < 50)
			{
				fixations.remove(i);
				fixationsV.remove(i);
				i--;
			}
		}
		
		if (fixations.size() > 3)
		{
			fixations.remove(0);
			fixationsV.remove(0);
			
		}
		processFixations();
		
		//System.out.println("proc fixations time: " + (new Date().getTime() - t));
	}


}
