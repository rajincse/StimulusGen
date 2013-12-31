package util;

import perspectives.Viewer;
import perspectives.Viewer2D;
import perspectives.ViewerFactory;
import perspectives.ViewerFactory.RequiredData;

public class EyeExperimentFactory extends ViewerFactory {

	@Override
	public RequiredData requiredData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String creatorType() {
		return "EyeExperiment";
	}

	@Override
	public Viewer create(String name) {
		// TODO Auto-generated method stub
		return new EyeExperiment(name);
	}
}
