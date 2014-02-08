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
	
	public SineWaveStimulus(Point origin,double angle)
	{
		super(origin, angle);
	}
	public Point getTransformedPoint(Point p)
	{
		int x =origin.x+p.x;
		int y =origin.y+p.y;
		
		return new Point(x,y);
	}

	public static Stimulus createStimulus(Point origin,double angle, StimulusGenPlotter plotter, int objectSize, Color objectColor)
	{
		Stimulus stimulus = new SineWaveStimulus(origin, angle);
		stimulus.getShapeList().clear();
		int objectCount = plotter.getObjectCount();
		Random rand = new Random();
		for(int i=0;i<objectCount;i++)
		{
			Point p = stimulus.getTransformedPoint(plotter.getPosition(i));
			int rotation =Math.abs( rand.nextInt() %360);
			SineWaveShape sine = new SineWaveShape(p.x, p.y, objectSize, objectColor, 100, 0.1, rotation);	
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
