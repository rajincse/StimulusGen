package Graph;

import java.awt.Point;

public interface GazeAnalyzerListener {
	
	public void fixationDetected(Point fixation, long when);
	public void fixationEnded(Point fixation, long when);
	public void fixationUpdated(Point fixation, long when);
}
