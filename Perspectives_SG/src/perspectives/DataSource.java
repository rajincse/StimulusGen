package perspectives;

import java.awt.Container;
import java.util.Date;

import javax.swing.JInternalFrame;

/**
 * 
 * @author rdjianu
 * 
 * Datasource is a the base class that any type of data source (e.g., graph, matrix, mesh) should implement. Each datasource should have a name that will be displayed in the GUI. For each new datasource you implement, you also need to create a DataSourceFactory
 * 
 * @see DataSourceFactory
 *
 */
public abstract class DataSource extends PropertyManager
{
	public DataSource(String name)
	{
		super(name);
		loaded = false;
		
		lastUpdate = new Date().getTime();
	}
	
	
	protected boolean loaded;
	protected long lastUpdate;
	
	public boolean isLoaded()
	{
		return loaded;
	}
	
	public void setLoaded(boolean l)
	{
		loaded = l;
	}
	
	public void wasUpdated()
	{
		lastUpdate = new Date().getTime();
	}
	
	public long lastUpdate()
	{
		return lastUpdate;
	}
		
	

}
