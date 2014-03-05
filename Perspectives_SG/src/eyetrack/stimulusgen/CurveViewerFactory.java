package eyetrack.stimulusgen;

import perspectives.Viewer;
import perspectives.ViewerFactory;
import stress.StressViewer;

public class CurveViewerFactory extends ViewerFactory {

	@Override
	public RequiredData requiredData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String creatorType() {
		// TODO Auto-generated method stub
		return "Curve Viewer";
	}

	@Override
	public Viewer create(String name) {
		// TODO Auto-generated method stub
		if(this.isAllDataPresent())
		{
			return new CurveViewer(name);
		}
		else
		{
			return null;
		}
	}

}
