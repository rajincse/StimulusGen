package Graph;
import java.io.File;

import javax.swing.JButton;

import perspectives.DataSource;
import perspectives.DefaultProperties.*;
import perspectives.Property;
import perspectives.PropertyManager;
import perspectives.PropertyType;
import perspectives.Task;


public class GraphData extends DataSource {
	
	public Graph graph;
	
	boolean valid;
		

	public GraphData(String name) {
		super(name);
		
		valid = false;
		
		graph= new Graph(false);
		
		try {
			
			OpenFilePropertyType f = new OpenFilePropertyType();
			f.dialogTitle = "Open Graph File";
			f.extensions = new String[3];
			f.extensions[0] = "xml";
			f.extensions[1] = "txt";
			f.extensions[2] = "*";
			
			Property<OpenFilePropertyType> p1 = new Property<OpenFilePropertyType>("Graph File");
			p1.setValue(f);			
			addProperty(p1);
			
			OptionsPropertyType o = new OptionsPropertyType();
			o.options = new String[2];
			o.options[0] = "GraphML"; o.options[1] = "EdgeList";
			o.selectedIndex = 0;
			
			Property<OptionsPropertyType> p2 = new Property<OptionsPropertyType>("Format");
			p2.setValue(o);
			this.addProperty(p2);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}	
	
	public <T extends PropertyType> boolean propertyBroadcast(Property p, T newvalue, PropertyManager origin)
	{
		return false;
	}

	
	public <T extends PropertyType> void propertyUpdated(Property p, T newvalue)
	{
		if (p.getName() == "Graph File")
		{
			final GraphData th = this;
			final T newvalue_ = newvalue;
			Task t = new Task("Loading network")
			{
				public void task()
				{
					Property format = th.getProperty("Format");
					
					format.getValue();
					
					String fs = ((OptionsPropertyType)format.getValue()).options[((OptionsPropertyType)format.getValue()).selectedIndex];
								
					if (fs.equals("GraphML"))
						graph.fromGraphML(new File(((OpenFilePropertyType) newvalue_).path));
					else if (fs.equals("EdgeList"))
						graph.fromEdgeList(new File(((OpenFilePropertyType)newvalue_).path));
					
					
					
					if (graph.numberOfNodes() != 0)
					{
						th.setLoaded(true);
		
						th.removeProperty("Graph File");
						th.removeProperty("Format");
						
						try {
							Property<IntegerPropertyType> p1 = new Property<IntegerPropertyType>("Info.# nodes");
							p1.setValue(new IntegerPropertyType(graph.numberOfNodes()));
							p1.setReadOnly(true);
							th.addProperty(p1);
							
							Property<IntegerPropertyType> p2 = new Property<IntegerPropertyType>("Info.# edges");
							p2.setValue(new IntegerPropertyType(graph.numberOfEdges()));
							p2.setReadOnly(true);
							th.addProperty(p2);					
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}		
		
					}
				}
			};
			t.indeterminate = true;
			t.startTask(this.getTaskObserverDialog());
		}
		else if (p.getName() == "Format")
		{
			String fs = ((OptionsPropertyType)newvalue).options[((OptionsPropertyType)newvalue).selectedIndex];
			
			if (fs.equals("GraphML"))
				((OpenFilePropertyType)this.getProperty("Graph File").getValue()).currentExtension = 0;
			else if (fs.equals("EdgeList"))
				((OpenFilePropertyType)this.getProperty("Graph File").getValue()).currentExtension = 1;
		}
	}
	
	public boolean isValid()
	{
		return valid;
	}

	public void setGraph(Graph g) {
		this.graph = g;
		valid = true;
		
	}

}
