package Graph;

import perspectives.Viewer;
import perspectives.ViewerFactory;

public class ViewFocusGraphViewerFactory extends ViewerFactory {
	
	

		public RequiredData requiredData() {
			
			RequiredData rd = new RequiredData("GraphData","1");
			return rd;
		}

		@Override
		public String creatorType() {
			// TODO Auto-generated method stub
			return "focus graphviewer";
		}

		@Override
		public Viewer create(String name) {
			if (this.isAllDataPresent())
				return new ViewFocusGraphViewer(name, (GraphData)this.getData().get(0));
			return null;
		}
}
