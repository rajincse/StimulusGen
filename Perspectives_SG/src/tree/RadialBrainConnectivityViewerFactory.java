package tree;

import perspectives.Viewer;
import perspectives.ViewerFactory.RequiredData;
import perspectives.ViewerFactory;
import Graph.GraphData;
import data.TableData;

public class RadialBrainConnectivityViewerFactory extends ViewerFactory {
	@Override
	public RequiredData requiredData() {
		RequiredData fromTree1 = new RequiredData("TreeData","1");		
		RequiredData fromTable1 = new RequiredData("TableData","1");	
		
		RequiredData[] op = {fromTree1, fromTable1};
				
		RequiredData rd = RequiredData.Or(op);

		return rd;
	}

	@Override
	public String creatorType() {
		return "Radian Brain Connectivity";
	}

	@Override
	public Viewer create(String name) {
		if (this.isAllDataPresent())
		{
			if (this.getData().size() == 1)
			{
				if (this.getData().get(0).getClass() == new TreeData("dummy").getClass())
					return new RadialBrainConnectivityViewer(name, (TreeData)this.getData().get(0));
				else if (this.getData().get(0).getClass() == new TableData("dummy").getClass())
					return new RadialBrainConnectivityViewer(name, (TableData)this.getData().get(0));		
			}
		}
		return null;
	}
}
