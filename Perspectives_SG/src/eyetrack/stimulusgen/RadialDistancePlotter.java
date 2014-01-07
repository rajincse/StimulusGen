package eyetrack.stimulusgen;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

public class RadialDistancePlotter extends StimulusGenPlotter{
	protected static final int ANGLE_DIVISION =30;
	protected static final int INFINITY =1000;

	protected ArrayList<Point> positions;
	protected ArrayList<Point> shadowPositions;
	
	public RadialDistancePlotter(int objectCount, int minDistance,
			int maxDistance) {
		super(objectCount, minDistance, maxDistance);

		this.positions = new ArrayList<Point>();
		this.shadowPositions = new ArrayList<Point>();

		this.init();
		this.simulate();
	}
	protected void init()
	{
		for(int i=0;i<this.objectCount;i++)			
		{
			this.positions.add(new Point(0, 0));
			this.shadowPositions.add(new Point(0, 0));
		}
	}
	@Override
	protected void simulate() 
	{
		Random random = new Random();
		int d=0;
		double minConfigScore = Double.MAX_VALUE;
		double unitAngle = Math.PI *2 / ANGLE_DIVISION;
		
		for(int i=0;i<this.objectCount;i++)
		{
			d =Math.abs( random.nextInt()% (maxDistance-minDistance))+minDistance;
			minConfigScore = Double.MAX_VALUE;
			for(int j=0;j<i;j++)
			{
				for(int k=0;k<ANGLE_DIVISION;k++)
				{
					Point position = this.getOnCirclePoint(this.positions.get(j), d, unitAngle*(k+1));
					this.shadowPositions.set(i, position);
					double score = this.getConfigurationScore(i);
					if(score < minConfigScore)
					{
						minConfigScore = score;
						this.positions.set(i, position);
					}
					else
					{
						this.shadowPositions.set(i, this.positions.get(i));
					}
				}
			}
		}
	}
	private Point getOnCirclePoint(Point center, int radius, double angle)
	{
		double delX = Math.cos(angle) * radius;
		double delY = Math.sin(angle) * radius;
		
		Point position = new Point(center.x+(int)delX, center.y+(int)delY);
		
		return position;
	}
	private double getConfigurationScore(int index)
	{
		double aspectRatioScore = this.getAspectRatioScore();
		int distanceScore = this.getDistanceScore(index);
				
		return aspectRatioScore * distanceScore;
	}
	private int getDistanceScore(int index)
	{
		int score =0;
		Point comparingPoint = this.shadowPositions.get(index);
		for(int i=0;i<this.shadowPositions.size();i++)
		{
			if(i!=index)
			{
				Point p = this.shadowPositions.get(i);
				double distance = comparingPoint.distance(p);
				if(distance > maxDistance)
				{
					score++;
				}
				else if(distance < minDistance)
				{
					score = INFINITY;
				}
						
			}
		}
		return score;
	}
	private double getAspectRatioScore()
	{
		int minX = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxY = Integer.MIN_VALUE;
		
		for(Point p: this.shadowPositions)
		{
			if(p.x < minX)
			{
				minX = p.x;
			}
			if(p.x > maxX)
			{
				maxX = p.x;
			}
			if(p.y < minY)
			{
				minY = p.y;
			}
			if(p.y > maxY)
			{
				maxY = p.y;
			}
		}
		
		int width = maxX -minX;
		int height = maxY - minY;
		double score =Double.MAX_VALUE;
		if(width != 0 && height !=0)
		{
			double min=Math.min(width, height);
			double max =  Math.max(width, height);
			score = min/max;
		}
		
		return score;
	}
	public void printPosition()
	{
		for(int i=0;i<this.positions.size();i++)
		{
			Point p = this.positions.get(i);
			System.out.println(""+i+"=>("+p.x+", "+p.y+")");
		}
	}
	@Override
	public Point getPosition(int index) {
		return this.positions.get(index);
	}

}
