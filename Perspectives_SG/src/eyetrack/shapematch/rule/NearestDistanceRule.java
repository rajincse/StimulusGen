package eyetrack.shapematch.rule;

import java.awt.Point;
import java.util.LinkedList;

import eyetrack.shapematch.AbstractShape;

/**
 * 
 * @author rajin
 * Rule where nearest distance with gaze have good score
 */
public class NearestDistanceRule implements ILocalRule{
	private static final int THRESHOLD =300;

	@Override
	public void applyLocalRule(AbstractShape shape, LinkedList<Point> gazeList) {
		// TODO Auto-generated method stub
		double score=0;
		double weight =1;
		for(Point p :gazeList)
		{
			double distance = shape.getDistance(p);
			if(distance ==0)
			{
				distance = 0.01;
			}
			if(distance<= THRESHOLD)
			{
				score += (weight / distance);
				weight++;
			}
			else
			{
				score -= (weight / distance);
				weight++;
			}
		}
		shape.setScore(score);
	}

}
