package Graph;

import perspectives.Viewer;

public class ClusterGraphViewerFactory extends GraphViewerFactory {
	
	public Viewer create(String name) {
		if (this.isAllDataPresent())
			return new ClusterGraphViewer(name, (GraphData)this.getData().get(0));
		return null;
	}
}
