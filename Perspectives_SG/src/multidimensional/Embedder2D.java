package multidimensional;

import java.awt.Color;
import java.util.ArrayList;

import data.DistancedPoints;
import data.TableDistances;

import perspectives.Property;
import util.Util;
import util.Vector2D;

public abstract class Embedder2D {
	
	protected DistancedPoints points;
	
	protected Vector2D[] xy;
	ArrayList<String> savedPoints = null; //saved nodes in case the graph is modified
	
	long tableLastUpdated;
	
	double maxX, maxY, minX, minY;
	
	
	public Embedder2D(DistancedPoints p)
	{
		this.points = p;
		tableLastUpdated = points.getLastUpdateTime();
		
		savedPoints = new ArrayList<String>();
		for (int i=0; i<points.getCount(); i++)
			savedPoints.add(points.getPointId(i));
		
		xy = new Vector2D[points.getCount()];	
		
		for (int i=0; i<points.getCount(); i++)
			xy[i] = new Vector2D(0,0);
		
		maxX = 0;
		maxY = 0;
		minX = 0; 
		minY = 0;
	}
	
	public abstract void iteration();	
	
	public double getX(int index)
	{
		if (index < 0 || index >= xy.length)
		{
			System.out.println("error in Embedder2D, getY; no such id in graph");
			System.exit(0);
		}
		
		long u = points.getLastUpdateTime();
		if (tableLastUpdated != u )
			updateStructure(u);
		
		return xy[index].x;
	}
	
	public double getY(int index)
	{
		if (index < 0 || index >= xy.length)
		{
			System.out.println("error in Embedder2D, getY; no such id in graph");
			System.exit(0);
		}		
		
		long u = points.getLastUpdateTime();
		if (tableLastUpdated != u )
			updateStructure(u);
		
		return xy[index].y;
	}
	
	
	public void setX(int index, double x)
	{
		if (index < 0 || index >= xy.length)
		{
			System.out.println("error in Embedder2D, setX; no such id in graph");
			System.exit(0);
		}
		
		long u = points.getLastUpdateTime();
		if (tableLastUpdated != u )
			updateStructure(u);
		
		xy[index].x = x;
		
		if (x> maxX) maxX = x;
		if (x< minX) minX = x;
		
	}
	
	public void setY(int index, double y)
	{
		if (index < 0 || index >= xy.length)
		{
			System.out.println("error in Embedder2D, setY; no such id in graph");
			System.exit(0);
		}
		
		long u = points.getLastUpdateTime();
		if (tableLastUpdated != u )
			updateStructure(u);
		
		xy[index].y = y;
		
		if (y> maxY) maxY = y;
		if (y< minY) minY = y;
	}
	
	private void updateStructure(long u)
	{
		tableLastUpdated = u;
		
		ArrayList<String> newPoints = new ArrayList<String>();
		for (int i=0; i<points.getCount(); i++)
			newPoints.add(points.getPointId(i));
		
		Vector2D[] newxy = new Vector2D[newPoints.size()];		
		
		for (int i=0; i<newxy.length; i++)
		{
			int index = savedPoints.indexOf(newPoints.get(i));
			if (index >=0 && index < savedPoints.size())
				newxy[i] = xy[index];
			else
				newxy[i] = new Vector2D();
		}
		
		xy = newxy;
		savedPoints = newPoints;
	}
	
	public Color getColor(int index)
	{
        double nx = (getX(index)-minX)/(maxX-minX);
        double ny = (getY(index)-minY)/(maxY-minY);
        
        double m = Math.max(maxY-minY,maxX-minX);
        
        //in this case we map the first coordinate to b
        if (m == maxX-minX)
        {
            double b = -38 + nx*(75+38);
            double a = -49 + ny*(41+49);
            return Util.labToRgb(75,a,b);
           
        }
        else
        //we map the first coordinate to a
        {
            double b = -38 + ny*(75+38);
            double a = -49 + nx*(41+49);
            return Util.labToRgb(75,a,b);
        }
	}
	
}
