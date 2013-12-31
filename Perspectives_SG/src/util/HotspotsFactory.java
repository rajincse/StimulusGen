package util;

import perspectives.Viewer;
import perspectives.ViewerFactory;

public class HotspotsFactory extends ViewerFactory{

	@Override
	public RequiredData requiredData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String creatorType() {
		return "Hotspots";
	}

	@Override
	public Viewer create(String name) {
		// TODO Auto-generated method stub
		return new Hotspots(name);
	}

}
