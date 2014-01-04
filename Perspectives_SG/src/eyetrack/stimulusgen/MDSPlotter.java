package eyetrack.stimulusgen;

import java.util.Random;

import multidimensional.Embedder2D;
import multidimensional.MDSEmbedder;
import multidimensional.SpringEmbedder;

import data.DistanceMatrix;

public class MDSPlotter {
	private int objectCount;
	private int maxDistance;
	private int minDistance;
	
	private DistanceMatrix distanceMatrix;
	private MDSEmbedder embedder;
	public MDSPlotter( int objectCount, int minDistance  ,int maxDistance)
	{
		this.objectCount = objectCount;
		this.maxDistance = maxDistance;
		this.minDistance = minDistance;
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
	private void simulate()
	{
		int kMax =1000;
		for(int i=0;i<kMax;i++)
		{
			this.embedder.iteration();
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
	
}
