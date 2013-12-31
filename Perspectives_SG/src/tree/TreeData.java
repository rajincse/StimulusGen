package tree;

import java.io.File;

import perspectives.DataSource;
import perspectives.DefaultProperties;
import perspectives.DefaultProperties.OpenFilePropertyType;
import perspectives.DefaultProperties.OptionsPropertyType;
import perspectives.Property;
import perspectives.PropertyType;

public class TreeData extends DataSource{
	
	Tree tree;
	
	boolean valid;

	public TreeData(String name) {
		super(name);
		
		tree = null;
		valid = false;
		
		try {
			OpenFilePropertyType f = new OpenFilePropertyType();
			f.dialogTitle = "Open Graph File";
			f.extensions = new String[3];
			f.extensions[0] = "txt";
			f.extensions[1] = "xml";
			f.extensions[2] = "*";
			
			Property<OpenFilePropertyType> p1 = new Property<OpenFilePropertyType>("Tree File");
			p1.setValue(f);
			this.addProperty(p1);
			
			OptionsPropertyType o = new OptionsPropertyType();
			o.options = new String[2];
			o.options[0] = "Newick"; o.options[1] = "GraphML";
			o.selectedIndex = 0;
			
			Property<OptionsPropertyType> p2 = new Property<OptionsPropertyType>("Format");
			p2.setValue(o);
			this.addProperty(p2);
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}

	@Override
	public <T extends PropertyType> void propertyUpdated(Property p, T newvalue) {
		if (p.getName() == "Tree File")
		{
			Property format = this.getProperty("Format");
			
			tree = new Tree(new File(((OpenFilePropertyType)newvalue).path), ((OptionsPropertyType)format.getValue()).options[((OptionsPropertyType)format.getValue()).selectedIndex]);
			
			this.setLoaded(true);
		}
		if (p.getName() == "Format")
		{
			String fs = ((OptionsPropertyType)newvalue).options[((OptionsPropertyType)newvalue).selectedIndex];
			
			if (fs.equals("GraphML"))
				((OpenFilePropertyType)this.getProperty("Graph File").getValue()).currentExtension = 1;
			else if (fs.equals("Newick"))
				((OpenFilePropertyType)this.getProperty("Graph File").getValue()).currentExtension = 0;	
		}
	}

	

	
	

}
