package perspectives;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Each Viewer implementation should be accompanied by a ViewerFactory implementation. Generally a viewer should be constructed for one or more data sources. Thus, a Viewer constructor may require a certain number of DataSources of specific types. This can be specified
 * by implementing the function requiredData() which returns a list of RequiredData options. These required data sources are displayed to users as they try to create a new Viewer. The user will need to select data sources that will meet the requirements. Otherwise they
 * won't be able to create a Viewer. 
 * <br>
 * @author rdjianu
 *
 */
public abstract class ViewerFactory {
	
	Vector<DataSource> data = new Vector<DataSource>();
	
	void addDataSource(DataSource d)
	{
		data.add(d);
	}
	
	public abstract RequiredData requiredData(); 
	
	protected Vector<DataSource> getData()
	{
		return data;
	}
	
	public void clearData()
	{
		data.clear();
	}
	
	/**
	 *  checks whether the data sources that the user selected satisfy the source requirements for this Viewer
	 * @return
	 */
	public boolean isAllDataPresent()
	{
		RequiredData rd = requiredData();
		
		if (rd == null)
			return true;
		
		Vector<String> ds = new Vector<String>();
		for (int j=0; j<data.size(); j++)
		{
			String s1 = data.get(j).getClass().getName();
			String[] split = s1.split("\\.");
			s1 = split[split.length-1];
			ds.add(s1);
		}
		
		boolean ret = rd.matches(ds);
		
		return ret;
	}
	
	/**
	 * Interprets the data source requirements of this Viewer and produces a user friendly message
	 * @return
	 */
	public String requiredDataString()
	{
			RequiredData rd = requiredData();	
			
			if (rd == null)
				return "No data required for this viewer.";
		
			
			String s = rd.requiredDataString();
			s = s.substring(1);
			s = s.substring(0, s.length()-1);
			
			return "Viewers of this type require: " + s;	
	}
	
	/**
	 * This will be displayed to the user as the type of Viewer that they can create
	 * @return
	 */
	public abstract String creatorType();
	
	/**
	 * Developers should implement this to create a Viewer of the desired type. Developers can retrieve the data sources that users selected using getData() which is ensured to return exactly the data sources that this type of Viewer requires
	 * @param name
	 * @return
	 */
	public abstract Viewer create(String name);
	
	public static class RequiredData
	{
		public RequiredData(String className, String amount)
		{
			this.className = className;
			this.amount = amount;
			this.operator = 0;
		}
		
		private RequiredData()
		{
			this.operator = 0;
			this.className = null;
			this.amount = null;
		}
		
		public static RequiredData And(RequiredData[] operands)
		{
			RequiredData rd = new RequiredData();
			rd.operands = operands;
			rd.operator = 1;
			return rd;
		}
		
		public static RequiredData Or(RequiredData[] operands)
		{
			RequiredData rd = new RequiredData();
			rd.operands = operands;
			rd.operator = 2;
			return rd;
		}		
		
		public static RequiredData Not(RequiredData operand)
		{
			RequiredData rd = new RequiredData();
			rd.operands = new RequiredData[1];
			rd.operands[0] = operand;
			rd.operator = 3;
			return rd;
		}		
		
		private String className; //DataSource classes
		private String amount; //amount of each: number, '+', '*'
		private int operator;
		
		private RequiredData operands[] = null;
		
		public String requiredDataString()
		{
			if (operator == 0) //leaf
			{
				if (amount == "+")
					return " one or more " + className;
				else if (amount == "*")
					return "any number of " + className;
				else
					return amount + " " + className;
			}
			else if (operator == 1)
			{
				String s = "(";
				for (int i=0; i<operands.length; i++)
				{
					s = s + operands[i].requiredDataString();
					if (i != operands.length-1) s = s + " AND ";
				}
				s = s + ")";
				return s;
			}
			else if (operator == 2)
			{
				String s = "(";
				for (int i=0; i<operands.length; i++)
				{
					s = s + operands[i].requiredDataString();
					if (i != operands.length-1) s = s + " OR ";
				}
				s = s + ")";
				return s;
			}
			else
				return " NOT " + operands[0].requiredDataString();
		}
		
		public boolean matches(Vector<String> data)
		{
			if (operator == 0)
			{
				if (amount == "*")
					return true;
				else if (amount == "+")
				{
					if (data.size() == 0) return false;
					for (int i=0; i<data.size(); i++)
						if (data.get(i).equals(className))
						{
							data.remove(i);
							return true;
						}
					return false;
				}
				else
				{
					int c = 0;
					for (int i=0; i<data.size(); i++)
						if (data.get(i).equals(className)) c++;	
					if (c < Integer.parseInt(amount))
						return false;
					for (int i=0; i<data.size() && c > 0; i++)
						if (data.get(i).equals(className))
						{
							c--;
							data.remove(i);
							i--;
						}
					return true;
				}					
			}
			else if (operator == 1)
			{
				boolean b = true;
				for (int i=0; i<operands.length; i++)
				{
					b = operands[i].matches(data);
					if (!b) return false;
				}
				return true;
			}
			else if (operator == 2)
			{
				boolean b = true;
				Vector savedData = new Vector(); savedData.addAll(data);
				for (int i=0; i<operands.length; i++)
				{
					b = operands[i].matches(data);
					if (b) return true;
					
					data.clear(); data.addAll(savedData);
				}
				return false;
			}
			else
			{
				Vector savedData = new Vector(); savedData.addAll(data);
				boolean b1 = operands[0].matches(data);
				data.clear(); data.addAll(savedData);
				return !b1;				
			}
		}
		
	}
	

}