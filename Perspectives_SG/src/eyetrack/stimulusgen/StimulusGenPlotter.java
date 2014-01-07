package eyetrack.stimulusgen;

import java.awt.Point;

public abstract class StimulusGenPlotter {
	
	
	protected int objectCount;
	protected int maxDistance;
	protected int minDistance;
	
	
	
	public StimulusGenPlotter( int objectCount, int minDistance  ,int maxDistance)
	{
		this.objectCount = objectCount;
		this.maxDistance = maxDistance;
		this.minDistance = minDistance;
	}
	
	
	protected abstract void simulate();
	

	public abstract Point getPosition(int index);
	
	
	
	
}
