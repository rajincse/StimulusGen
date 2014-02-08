package eyetrack.stimulusgen;

import perspectives.Viewer;
import perspectives.ViewerFactory;

public class SineWaveStimulusViewerFactory extends ViewerFactory{

	@Override
	public RequiredData requiredData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String creatorType() {
		// TODO Auto-generated method stub
		return "Sinewave Generator";
	}

	@Override
	public Viewer create(String name) {
		// TODO Auto-generated method stub
		if(this.isAllDataPresent())
		{
			return new SineWaveStimulusViewer(name);
		}
		else
		{
			return null;
		}
	}

}
