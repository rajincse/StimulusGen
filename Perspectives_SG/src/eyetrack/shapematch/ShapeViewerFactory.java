package eyetrack.shapematch;

import perspectives.Viewer;
import perspectives.ViewerFactory;

public class ShapeViewerFactory extends ViewerFactory{

	@Override
	public RequiredData requiredData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String creatorType() {
		// TODO Auto-generated method stub
		return "ShapeViewer";
	}

	@Override
	public Viewer create(String name) {
		// TODO Auto-generated method stub
		if(this.isAllDataPresent())
		{
			return new ShapeViewer(name);
		}
		else
		{
			return null;
		}
			
	}

}
