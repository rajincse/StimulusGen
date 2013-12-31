package data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;


public class Table extends DefaultTableModel
{
	ArrayList<String> rowNames;
	
	public enum TableElementType { Double, String};
	
	private long lastUpdate;
	
	public Table()
	{
		super();
		rowNames = null;
		
		lastUpdate = (new Date()).getTime();
	}
	
	public Table(int columncount, int rowcount)
	{
		super();
		rowNames = new ArrayList<String>();
		for (int i=0; i<rowcount; i++)
			rowNames.add("row_" + i);
		lastUpdate = (new Date()).getTime();
	}
	
	public void addRow(Vector rowData, String name)
	{
		addRow(rowData);
		rowNames.add(name);
		lastUpdate = (new Date()).getTime();
	}
	
	public void addRow(Object[] rowData, String name)
	{
		addRow(rowData);
		rowNames.add(name);
		lastUpdate = (new Date()).getTime();
	}
	
	public void setRowName(int index, String name)
	{
		if (index  >= 0 || index < rowNames.size())
			rowNames.set(index, name);
		lastUpdate = (new Date()).getTime();
	}
	
	public String getRowName(int index)
	{
		if (index < 0 || index >= rowNames.size())
			return "";
		return rowNames.get(index);
	}
	
	public long getLastUpdateTime()
	{
		return lastUpdate;		
	}
	
	
    public void fromFile(String filename, String delim, boolean colH, boolean rowH)
    {
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));

            String str;
            ArrayList<String> lines = new ArrayList<String>();
            while ((str = in.readLine()) != null) {
                lines.add(str);
            }
            
            if (lines.size() == 0)
            	return;
            
        	int is = 0;
        	if (rowH) is = 1;

            String[] columnHeaders = null;
            String[] rowHeaders = null;
            
            if (colH) //if there are column headers...Column headers are needed
            {
                String[] split = lines.get(0).split(delim);

                columnHeaders = new String[split.length-is];
                for (int i = is; i < split.length; i++) {
                    columnHeaders[i-is] = split[i];
                }

                lines.remove(0);   //remove the headers
            }
            
            String[][] data = new String[lines.size()][];
            for (int i=0; i<lines.size(); i++)
            	data[i] = lines.get(i).split(delim);
            
            int maxrowcount = 0;
            for (int i=0; i<data.length; i++)
            	if (data[i].length > maxrowcount)
            		maxrowcount = data[i].length;
            
            if (rowH) maxrowcount--;
            
            if (colH && maxrowcount > columnHeaders.length)
            {
            	String[] ch2 = new String[maxrowcount];
            	for (int i=0; i<maxrowcount; i++)
            		if (i < columnHeaders.length) ch2[i] = columnHeaders[i];
            		else ch2[i] = "column_" + (i+1);
            	columnHeaders = ch2;
            }
        	
        	rowHeaders = new String[data.length];
        	for (int i=0; i<data.length; i++)
        			if (rowH && data[i].length >= 1)
        				rowHeaders[i] = data[i][0];
        			else
        				rowHeaders[i] = "row_" + i;
        	rowNames = new ArrayList<String>();
        	for (int i=0; i<rowHeaders.length; i++) rowNames.add(rowHeaders[i]);
        	
  
        	
        	for (int i=0; i<columnHeaders.length; i++)
        		addColumn(columnHeaders[i]);    
        	
        	this.setRowCount(data.length);           
           
           
            //determine each column's type
            TableElementType[] types = new TableElementType[maxrowcount];            
            for (int i=0; i<maxrowcount; i++)
            {

            	types[i] = TableElementType.Double;
            	for (int j=0; j<data.length; j++)
            	{
            		String v = "";
            		
            		if (data[j].length-is == maxrowcount)
            			v = data[j][i+is];
            		
            		if (!isNumeric(v))
            		{
            			types[i] = TableElementType.String;
            			break;
            		}        			
            	}
            	
            }
            
            //set the data
            for (int i=0; i<maxrowcount; i++)
            {
            	for (int j=0; j<data.length; j++)
            	{
            		String v = "";
            		
            		if (data[j].length-is == maxrowcount)
            			v = data[j][i+is];
            		
            		if (types[i] == TableElementType.String)
            			setValueAt(v, j, i);
            		else if (types[i] == TableElementType.Double)
            			setValueAt(new Double(v), j, i);		
            	}
            }
            
           
            in.close();  
            
            lastUpdate = (new Date()).getTime();

        } catch (Exception e) {
            e.printStackTrace();
        }     

    }
    
    public boolean isNumeric(String str) {

        if(str.matches("\\d+(\\.\\d+)?"))
             return true;
    
        return false;
 
    }

}
