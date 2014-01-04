package multidimensional;

import util.Vector2D;
import data.DistancedPoints;

public class MDSEmbedder extends Embedder2D{

	double maxStep = 0.01;
	
	double k = 0.001;
	
	DistancedPoints points;
	
	private double maxRealDist;
	
	public MDSEmbedder(DistancedPoints t) {
		super(t);
		points = t;
		
		maxRealDist = 1;
	}
	
	public void iteration()
	{
		// Calculate max Real dist
		for(int i=0;i<points.getCount()-1;i++)
		{
			for(int j=i+1;j<points.getCount();j++)
			{
				if(maxRealDist > Math.abs(points.getDistance(i, j)))
				{
					maxRealDist =  Math.abs(points.getDistance(i, j));
				}
			}
			System.out.println();
		}
		//init force-vectors to (0,0) for all nodes
		Vector2D[] force = new Vector2D[points.getCount()];
         
         for(int i=0;i<points.getCount();i++)
         {	
        	 force[i] = new Vector2D(0, 0);
         }
		//compute spring forces and add them to the force vectors
         
         
        for(int i=0;i<points.getCount()-1;i++)
 		{
        	double x1 = getX(i);
        	double y1 = getY(i);
 			for(int j=i+1;j<points.getCount();j++)
 			{				
 				double x2 = getX(j);
 				double y2 = getY(j);
 				
 				Vector2D deltaVect = new Vector2D( x2-x1, y2-y1);
 				double delta = Math.sqrt(deltaVect.x*deltaVect.x + deltaVect.y*deltaVect.y);
 				while (delta < 1)
 				{
 					x2 = x2 + Math.random();
 					y2 = y2 + Math.random();
 					deltaVect = new Vector2D( x2-x1, y2-y1);
 					delta = Math.sqrt(deltaVect.x*deltaVect.x + deltaVect.y*deltaVect.y);
 				}
 				
 				
 				double attraction = delta / getSpringK(i, j);
 				
 				
 				double repulsion = - getSpringK(i, j) / ( delta * delta); 		
 				deltaVect.multiply((attraction + repulsion) / delta);
 				
 				force[i].add(deltaVect);
 				force[j].substract(deltaVect);			
 			}
 		}
        double maxDisp = Double.MIN_VALUE;
		for(int i=0;i<points.getCount();i++)
		{
			if( Math.abs(force[i].x) > maxDisp)
			{
				maxDisp = Math.abs( force[i].x);
			}
			if( Math.abs(force[i].y )> maxDisp)
			{
				maxDisp = Math.abs(force[i].y);
			}
		}
		//add forces to current positions of the nodes (getX, getY will give you the current positions).
        for (int i=0; i<points.getCount(); i++)
		{
			double x = getX(i);
			double y = getY(i);
			
			double normalizationFactor = Math.sqrt(force[i].x*force[i].x + force[i].y*force[i].y);
			if (normalizationFactor > maxStep)
			{
				force[i].multiply(maxStep /(normalizationFactor) );
				
			}
			setX(i,x + force[i].x/maxDisp);
			setY(i,y + force[i].y/maxDisp);
			
		}
		//cool down system (max_step)
        maxStep = Math.max(2,maxStep * 0.99);
		
		
	}
	
	public double getSpringK(int i,int j)
	{
		if(i<j)
		{
			return this.points.getDistance(i, j) / maxRealDist;
		}
		else if(j<i)
		{
			return this.points.getDistance(j, i)/maxRealDist;
		}
		else
		{
			return 0;
		}
			
	}
}
