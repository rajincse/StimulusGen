package tree;

import java.util.Vector;

import perspectives.Viewer;
import perspectives.ViewerFactory;
import perspectives.ViewerFactory.RequiredData;
import data.*;

public class HierarchicalClusteringViewerFactory extends ViewerFactory {
	
	@Override
	public RequiredData requiredData() {
		RequiredData rd1 = new RequiredData("TreeData","1");
		RequiredData rd2 = new RequiredData("TableData","1");
		
		RequiredData[] op = {rd1,rd2};
		RequiredData rd3 = RequiredData.Or(op);


		return rd3;
	}

	@Override
	public String creatorType() {
		return "Hierarchical Clustering";
	}

	@Override
	public Viewer create(String name) {
		if (this.isAllDataPresent())
		{
			if (this.getData().get(0).getClass() == new TreeData("dummy").getClass())
				return new HierarchicalClusteringViewer(name, (TreeData)this.getData().get(0));
			else
				return new HierarchicalClusteringViewer(name, (TableData)this.getData().get(0));
		}
		return null;
	}

}
