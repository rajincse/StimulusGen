package eyetrack.stimulusgen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

public class NoisyBackground {
	private ArrayList<Stimulus> stimulusList;
	private StimulusGenPlotter shapePlotter;
	
	public NoisyBackground(StimulusGenPlotter shapePlotter, 
							int stimuluscount,
							int minDistance,
							int maxDistance,
							int minRotation,
							int maxRotation,
							int objectSize, 
							Color objectColor)
	{
		this.shapePlotter = shapePlotter;
		this.stimulusList = new ArrayList<Stimulus>();
		
		this.configure(stimuluscount, minDistance, maxDistance, minRotation, maxRotation,objectSize,objectColor);
	}
	
	public void configure(int stimuluscount,
			int minDistance,
			int maxDistance,
			int minRotation,
			int maxRotation,
			int objectSize, 
			Color objectColor)
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
		
	}
	private StimulusGenPlotter getPlotter(int objectCount, int minDistance, int maxDistance)
	{
		StimulusGenPlotter plotter = new RadialDistancePlotter(objectCount, minDistance, maxDistance);
		
		return plotter;
	}
	
	public void render(Graphics2D g)
	{
		for(Stimulus s: this.stimulusList)
		{
			s.render(g);
		}
	}
	
}
