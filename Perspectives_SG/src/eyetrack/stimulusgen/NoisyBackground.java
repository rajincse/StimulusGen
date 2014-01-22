package eyetrack.stimulusgen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.ArrayList;
import java.util.Random;

public class NoisyBackground {
	private ArrayList<Stimulus> stimulusList;
	private StimulusGenPlotter shapePlotter;
	private BufferedImage image;
	
	public NoisyBackground(StimulusGenPlotter shapePlotter, 
							int stimuluscount,
							int minDistance,
							int maxDistance,
							int minRotation,
							int maxRotation,
							int objectSize, 
							Color objectColor,
							int blurringAmount,
							int width, int height)
	{
		this.shapePlotter = shapePlotter;
		this.stimulusList = new ArrayList<Stimulus>();
		this.image = null;
		
		this.configure(stimuluscount,
				minDistance, maxDistance,
				minRotation, maxRotation,
				objectSize,
				objectColor, 
				blurringAmount, 
				width, height);
	}
	
	public void configure(int stimuluscount,
			int minDistance,
			int maxDistance,
			int minRotation,
			int maxRotation,
			int objectSize, 
			Color objectColor,
			int blurringAmount,
			int width, int height)
	{
		StimulusGenPlotter stimulusPlotter = this.getPlotter(stimuluscount, minDistance, maxDistance);
		Random random = new Random();
		
		this.stimulusList.clear();
		
		for(int i=0;i< stimuluscount;i++)
		{
			Point position = stimulusPlotter.getPosition(i);
			double angle = Math.abs(random.nextInt()%(maxRotation-minRotation))+minRotation;
			Stimulus stimulus = Stimulus.createStimulus(position, angle, this.shapePlotter, objectSize, objectColor);
			this.stimulusList.add(stimulus);
		}
		this.image=new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g =image.createGraphics();
		this.renderToImage(g);
		this.image = getBlurredImage(this.image, blurringAmount);
	}
	public BufferedImage getBlurredImage(BufferedImage sourceImage,int blurringAmount)
	{
		BufferedImage dstImage = null;
		float[] matrix  = this.getBlurringMatrix(blurringAmount);
		Kernel kernel = new Kernel(blurringAmount, blurringAmount, matrix);
		ConvolveOp op = new ConvolveOp(kernel);
		dstImage = op.filter(sourceImage, dstImage);
		return dstImage;
	}
	private float[] getBlurringMatrix(int dimension)
	{
		float[] matrix = new float[dimension * dimension];
		for(int i=0;i<matrix.length;i++)
		{
			matrix[i] =(float) 1.0f/ matrix.length;
		}

		return matrix;
	}
	
	private StimulusGenPlotter getPlotter(int objectCount, int minDistance, int maxDistance)
	{
		StimulusGenPlotter plotter = new RadialDistancePlotter(objectCount, minDistance, maxDistance);
		
		return plotter;
	}
	
	private void renderToImage(Graphics2D g)
	{
		for(Stimulus s: this.stimulusList)
		{
			s.render(g);
		}
	}
	public void render(Graphics2D g)
	{
		
		if(this.image != null)
		{
			g.drawImage(this.image, null, 0, 0);
		}
	}
	
}
