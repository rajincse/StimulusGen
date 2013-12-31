package Graph;
import java.util.Vector;

import perspectives.Viewer;
import perspectives.ViewerFactory;


public class GraphViewerFactory extends ViewerFactory {

	public RequiredData requiredData() {
		
		RequiredData rd = new RequiredData("GraphData","1");
		return rd;
	}

	@Override
	public String creatorType() {
		// TODO Auto-generated method stub
		return "graphviewer";
	}

	@Override
	public Viewer create(String name) {
		if (this.isAllDataPresent())
			return new GraphViewer(name, (GraphData)this.getData().get(0));
		return null;
	}
	
}
