package stress;

import eyetrack.stimulusgen.StimulusGenViewer;
import perspectives.Viewer;
import perspectives.ViewerFactory;

public class StressViewerFactory extends ViewerFactory{

	@Override
	public RequiredData requiredData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String creatorType() {
		// TODO Auto-generated method stub
		return "Stress Viewer";
	}

	@Override
	public Viewer create(String name) {
		// TODO Auto-generated method stub
		if(this.isAllDataPresent())
		{
			return new StressViewer(name);
		}
		else
		{
			return null;
		}
	}

}
