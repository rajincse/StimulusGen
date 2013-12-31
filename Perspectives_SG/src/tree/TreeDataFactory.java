package tree;

import perspectives.DataSource;
import perspectives.DataSourceFactory;

public class TreeDataFactory extends DataSourceFactory
{

	@Override
	public DataSource create(String name) {
		
		return new TreeData(name);
	}

	@Override
	public String creatorType() {
		// TODO Auto-generated method stub
		return "TreeData";
	}

}
