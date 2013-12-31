
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import perspectives.DataSource;

import perspectives.DefaultProperties.*;
import perspectives.Property;


public class FloatMatrix extends DataSource {

	float [][] data;
	
	String[] columnHeaders, rowHeaders;
	
	public FloatMatrix(String name) {
		super(name);
		
		try {
			Property<OpenFilePropertyType> p1 = new Property<OpenFilePropertyType>("Source File");
			this.addProperty(p1);
			
			Property<BooleanPropertyType> p2 = new Property<BooleanPropertyType>("Col Headers?");
			p2.setValue(new BooleanPropertyType(true));
			this.addProperty(p2);
			
			p2 = new Property<BooleanPropertyType>("Row Headers?");
			p2.setValue(new BooleanPropertyType(true));
			this.addProperty(p2);
			
			Property<OptionsPropertyType> p3 = new Property<OptionsPropertyType>("Delimiter");
			OptionsPropertyType o = new OptionsPropertyType();
			o.options = new String[3];
			o.options[0] = "TAB"; o.options[1] = "SPACE"; o.options[2] = "COMMA";
			o.selectedIndex = 0;
			p3.setValue(o);
			this.addProperty(p3);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int width()
	{
		return data[0].length;
	}
	
	public int height()
	{
		return data.length;
	}
	
	public float get(int col, int row)
	{
		return data[row][col];
	}
	
	public void create(int width, int height)
	{
		data = new float[height][width];
	}
	
	public <T> void propertyUpdated(Property p, T newvalue)
	{
		if (p.getName() == "Source File")
		{
			boolean ch = ((BooleanPropertyType)getProperty("Col Headers?").getValue()).boolValue();
			boolean rh = ((BooleanPropertyType)getProperty("Row Headers?").getValue()).boolValue();
			int delim = ((OptionsPropertyType)(getProperty("Delimiter").getValue())).selectedIndex;
			OpenFilePropertyType f = ((OpenFilePropertyType)newvalue);
			
			String d = "\t";
			if (delim == 1) d = " ";
			else if (delim == 2) d = ",";
			
			fromFile(f.path, d, ch, rh);
		}
	}
	
	public void fromFile(String filename, String delim, boolean colH, boolean rowH)
	{
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(filename));
		   
			String str;
			Vector<String> lines = new Vector<String>();
			while ((str = in.readLine()) != null)
				lines.add(str);
			
			if (colH && lines.size() != 0) 
			{
				String[] split = lines.get(0).split(delim);
				
				
				if (rowH)
				{
					columnHeaders = new String[split.length-1];
					create(split.length-1, lines.size()-1);
					
					for (int i=1; i<split.length; i++)
						columnHeaders[i-1] = split[i];
				}
				else
				{
					columnHeaders = new String[split.length];
					create(split.length,lines.size()-1);
					for (int i=0; i<split.length; i++)
						columnHeaders[i] = split[i];
				}
				
				lines.remove(0);
			}
			else if (lines.size() != 0)
			{
				if (rowH)
					create(lines.get(0).split(delim).length -1, lines.size());
				else
					create(lines.get(0).split(delim).length, lines.size());
					
			}
			
			if (rowH)
				rowHeaders = new String[lines.size()];
			
			for (int i=0; i<lines.size(); i++)
			{
				String[] split = lines.get(i).split(delim);
				
				int si = 0;
				
				if (rowH)
				{
					rowHeaders[i] = split[0];
					si = 1;
				}
				
				for (int j=si; j<split.length; j++)
				{
					float f = Float.parseFloat(split[j]);
					data[i][j-si] = f;
				}
			}
			
			 in.close();
		}
		catch (Exception e) {
			
		}
	}
	

}
