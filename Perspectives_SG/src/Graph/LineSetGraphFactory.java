package Graph;

import perspectives.Viewer;

public class LineSetGraphFactory extends ClusterGraphViewerFactory{

			public Viewer create(String name) {
			if (this.isAllDataPresent())
				return new LineSetGraph(name, (GraphData)this.getData().get(0));
			return null;
		}

}
