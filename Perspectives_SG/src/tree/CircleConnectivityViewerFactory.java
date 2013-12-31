package tree;

import Graph.GraphData;
import perspectives.Viewer;
import perspectives.ViewerFactory;
import perspectives.ViewerFactory.RequiredData;
import data.TableData;

public class CircleConnectivityViewerFactory extends ViewerFactory {
	
	@Override
	public RequiredData requiredData() {
		RequiredData fromTree1 = new RequiredData("TreeData","1");
		RequiredData fromTree2 = new RequiredData("GraphData","1");
		RequiredData[] op1 = {fromTree1, fromTree2};
		RequiredData fromTree3 = RequiredData.And(op1);
	
		
		RequiredData fromTable1 = new RequiredData("TableData","1");
		RequiredData fromTable2 = new RequiredData("GraphData","1");
		RequiredData[] op2 = {fromTable1, fromTable2};
		RequiredData fromTable3 = RequiredData.And(op2);
		
		RequiredData[] op = {fromTree1, fromTable1, fromTree3, fromTable3};
				
		RequiredData rd = RequiredData.Or(op);

		return rd;
	}

	@Override
	public String creatorType() {
		return "Circle Connectivity";
	}

	@Override
	public Viewer create(String name) {
		if (this.isAllDataPresent())
		{
			if (this.getData().size() ==2)
			{
				if (this.getData().get(0).getClass() == new TreeData("dummy").getClass() && 
						this.getData().get(1).getClass() == new GraphData("dummy").getClass())
					return new CircleConnectivityViewer(name, (TreeData)this.getData().get(0), (GraphData)this.getData().get(1));
				else if (this.getData().get(1).getClass() == new TreeData("dummy").getClass() && 
						this.getData().get(0).getClass() == new GraphData("dummy").getClass())
					return new CircleConnectivityViewer(name, (TreeData)this.getData().get(1), (GraphData)this.getData().get(0));
				
				if (this.getData().get(0).getClass() == new TableData("dummy").getClass() && 
						this.getData().get(1).getClass() == new GraphData("dummy").getClass())
					return new CircleConnectivityViewer(name, (TableData)this.getData().get(0), (GraphData)this.getData().get(1));
				else if (this.getData().get(1).getClass() == new TableData("dummy").getClass() && 
						this.getData().get(0).getClass() == new GraphData("dummy").getClass())
					return new CircleConnectivityViewer(name, (TableData)this.getData().get(1), (GraphData)this.getData().get(0));
			}
			else if (this.getData().size() == 1)
			{
				if (this.getData().get(0).getClass() == new TreeData("dummy").getClass())
					return new CircleConnectivityViewer(name, (TreeData)this.getData().get(0));
				else if (this.getData().get(0).getClass() == new TableData("dummy").getClass())
					return new CircleConnectivityViewer(name, (TableData)this.getData().get(0));		
			}
		}
		return null;
	}

}
