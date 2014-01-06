package eyetrack.stimulusgen;

import java.awt.Point;
import java.util.Random;

import multidimensional.Embedder2D;
import multidimensional.MDSEmbedder;
import data.DistanceMatrix;

public class MDSPlotter extends StimulusGenPlotter {
	private int objectCount;
	private int maxDistance;
	private int minDistance;
	
	private DistanceMatrix distanceMatrix;
	private MDSEmbedder embedder;
	public MDSPlotter( int objectCount, int minDistance  ,int maxDistance)
	{
		super(objectCount, minDistance, maxDistance);
		this.distanceMatrix = new DistanceMatrix(objectCount);
		
		this.init();
		
		this.embedder = new MDSEmbedder(this.distanceMatrix);
		this.simulate();
	}
	
	private void init()
	{
		Random rand = new Random();
		
		for(int i=0;i<objectCount;i++)
		{
			for(int j=i;j<objectCount;j++)
			{				
				if(i==j)
				{
					//this.distanceMatrix.setDistance(i, j, 0);
				}
				else
				{			
					int range = maxDistance -minDistance;
					float distance = Math.abs(rand.nextInt()% range)+minDistance;
					this.distanceMatrix.setDistance(i, j, distance);
				}
			}
		}
	}
	
	public Embedder2D getEmbedder()
	{
		return this.embedder;
	}
	public void printMatrix()
	{
		for(int i=0;i<objectCount;i++)
		{
			for(int j=0;j<objectCount;j++)
			{				
				System.out.print(this.distanceMatrix.getDistance(i, j)+", ");
			}
			System.out.println();
		}
	}
	
	public static void main(String[] args)
	{
		MDSPlotter mds = new MDSPlotter(10, 10, 20);
		mds.printMatrix();
	}

	@Override
	protected void simulate() {
		// TODO Auto-generated method stub
		int kMax =1000;
		for(int i=0;i<kMax;i++)
		{
			this.embedder.iteration();
		}
	}

	@Override
	public Point getPosition(int index) {
		// TODO Auto-generated method stub
		Point p = new Point((int)this.embedder.getX(index),(int)this.embedder.getY(index));
		return p;
	}
	
}
