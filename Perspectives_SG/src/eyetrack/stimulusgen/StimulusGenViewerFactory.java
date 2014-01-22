package eyetrack.stimulusgen;

import perspectives.Viewer;
import perspectives.ViewerFactory;

public class StimulusGenViewerFactory extends ViewerFactory {

	@Override
	public RequiredData requiredData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String creatorType() {
		// TODO Auto-generated method stub
		return "Stimulus Generator";
	}

	@Override
	public Viewer create(String name) {
		// TODO Auto-generated method stub
		if(this.isAllDataPresent())
		{
			return new StimulusGenViewer(name);
		}
		else
		{
			return null;
		}
			
	}

}
