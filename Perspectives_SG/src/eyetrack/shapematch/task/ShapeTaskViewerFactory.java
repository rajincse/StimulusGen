package eyetrack.shapematch.task;

import perspectives.Viewer;
import perspectives.ViewerFactory;
import eyetrack.shapematch.ShapeViewer;

public class ShapeTaskViewerFactory extends ViewerFactory{

	@Override
	public RequiredData requiredData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String creatorType() {
		// TODO Auto-generated method stub
		return "ShapeTaskViewer";
	}

	@Override
	public Viewer create(String name) {
		// TODO Auto-generated method stub
		if(this.isAllDataPresent())
		{
			return new ShapeTaskViewer(name);
		}
		else
		{
			return null;
		}
			
	}

}