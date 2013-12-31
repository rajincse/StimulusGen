package multidimensional;

import java.awt.Color;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import perspectives.DefaultProperties;
import perspectives.DefaultProperties.SaveFilePropertyType;
import perspectives.DefaultProperties.StringPropertyType;
import perspectives.Property;
import perspectives.PropertyType;
import data.Table;
import data.TableData;
import util.Points2DViewer;

public class PlanarProjectionViewer extends Points2DViewer {
	
	Embedder2D embedder = null;
	
	private Table table = null;
	
	public PlanarProjectionViewer(String name, TableData t)
	{
		super(name);
		
		table = t.getTable();
		embedder = new SpringEmbedder(t.getTable());
		
		this.setPointSize(25);
		
		try
		{
			Property<StringPropertyType> p = new Property<StringPropertyType>("Color");
			p.setValue(new StringPropertyType(""));
			
			addProperty(p);
			
			SaveFilePropertyType f = new SaveFilePropertyType();
			Property<SaveFilePropertyType> p3 = new Property<SaveFilePropertyType>("Save Colors");
			p3.setValue(f);
			this.addProperty(p3);
			
		}
		catch(Exception e)
		{
			
		}
		
	}

	@Override
	public <T extends PropertyType> void propertyUpdated(Property p, T newvalue) {
		if (p.getName() == "Color")
		{
			for (int i=0; i<this.getNumberOfPoints(); i++)
			{
				Color c = this.embedder.getColor(i);
				System.out.println(i + " -> " + c.getRed() + " " + c.getGreen() + " " + c.getBlue());
				this.setColor(i,c);
			}
		}
		else if (p.getName() == "Save Colors")
		{
			try{
				 
				 FileOutputStream fstream = new FileOutputStream(new File(((SaveFilePropertyType)newvalue).path));
				 DataOutputStream out = new DataOutputStream(fstream);
				 
				for (int i=0; i<this.getNumberOfPoints(); i++)
				{
						Color c = this.embedder.getColor(i);						
						String s = this.getPointLabel(i) + "\t" + c.getRed() + "\t" + c.getGreen() + " \t" + c.getBlue();
						out.writeChars(s);
						
				}		 	 
				 out.close();
				}
				catch(Exception e)
				{
					
				};
		}
		else
			super.propertyUpdated(p, newvalue);
	}

	@Override
	protected String getPointLabel(int p) {
		return table.getRowName(p);
	}

	@Override
	protected int getNumberOfPoints() {
		return table.getRowCount();
	}

	@Override
	protected int getPointX(int p) {
		return (int)(embedder.getX(p)*400);
	}

	@Override
	protected int getPointY(int p) {
		return (int)(embedder.getY(p)*400);
	}

	@Override
	protected void setPointX(int p, int x) {
		// TODO Auto-generated method stub
		embedder.setX(p, x/400.);
		
	}

	@Override
	protected void setPointY(int p, int y) {
		embedder.setY(p, y/400.);
		
	}

	@Override
	public void simulate() {
		if (embedder != null)
			embedder.iteration();
		
	}
}
