package eyetrack.shapematch.rule;

import java.awt.Point;
import java.util.LinkedList;

import eyetrack.shapematch.AbstractShape;

public interface ILocalRule extends IRule{
	void applyLocalRule(AbstractShape shape, LinkedList<Point> gazeList);
}
