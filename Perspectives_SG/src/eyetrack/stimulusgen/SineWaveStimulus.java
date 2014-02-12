package eyetrack.stimulusgen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Random;

import eyetrack.shapematch.AbstractBoundedShape;
import eyetrack.shapematch.OvalShape;
import eyetrack.shapematch.SineWaveShape;

public class SineWaveStimulus extends Stimulus{

	public SineWaveStimulus()
	{
		super();
	}
	
	public SineWaveStimulus(Point origin)
	{
		super(origin, 0.0);
	}
	public Point getTransformedPoint(Point p)
	{
		int x =origin.x+p.x;
		int y =origin.y+p.y;
		
		return new Point(x,y);
	}

	public static Stimulus createStimulus(
			Point origin,
			StimulusGenPlotter plotter, 
			Color objectColor, 
			Period.Double amplitude,
			Period.Double frequency,
			Period.Double rotation)
	{
		Stimulus stimulus = new SineWaveStimulus(origin);
		stimulus.getShapeList().clear();
		int objectCount = plotter.getObjectCount();
		Random rand = new Random();
		for(int i=0;i<objectCount;i++)
		{
			Point p = stimulus.getTransformedPoint(plotter.getPosition(i));
			SineWaveShape sine = new SineWaveShape(p.x, p.y,  objectColor, amplitude.getRandom(), frequency.getRandom(), rotation.getRandom());	
			stimulus.getShapeList().add(sine);
		}
		
		return stimulus;
	}
	
	public void render(Graphics2D g)
	{
		for(AbstractBoundedShape shape:this.shapeList)
		{
			shape.render(g);
		}
	}

}
