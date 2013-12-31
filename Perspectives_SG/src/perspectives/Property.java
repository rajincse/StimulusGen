package perspectives;

import java.util.ArrayList;


	public class Property<T extends PropertyType> {
		
		private String name;
		private T value;
		private boolean visible;		
		private boolean readOnly;
		private boolean publ;
		
		
		private PropertyManager pm;
		
		ArrayList<String> aliases;
		
		
		public Property(String n) {
			this.name = n;
			
			visible = true;
			readOnly = false;
			publ = false;	
			
			pm = null;
			
			aliases = new ArrayList<String>();
		}
		
		public void setPropertyManager(PropertyManager pm)
		{
			this.pm = pm;
		}
		
		public String getName()
		{
			return name;
		}
		
		public String getDisplayName()
		{
			String[] s = name.split("\\.");
			String labelText =  s[s.length-1];
			String ext = "";
			if (labelText.length() > 13)
				ext = "..";
			labelText = labelText.substring(0, Math.min(labelText.length(), 13)) + ext;		
			return labelText;
		}
		
		public void setValue(T newValue)
		{
			value = newValue;
			if (pm != null)
				pm.setPropertyValueCallback(this, newValue);	
		}
		
		
		public T getValue()
		{
			return value;
		}
		
		public void setVisible(boolean v)
		{
			visible = v;
			if (pm != null)
				pm.setPropertyVisibleCallback(this,v);
		}
		
		public boolean getVisible()
		{
			return visible;
		}
		
		public void setReadOnly(boolean r)
		{
			readOnly = r;
			if (pm != null)
				pm.setPropertyReadOnlyCallback(this, r);
		}
		
		public boolean getReadOnly()
		{
			return readOnly;
		}
				
		public void setPublic(boolean s)
		{
			publ = s;
		}
		
		public boolean getPublic()
		{
			return publ;
		}
		
		public void addAlias(String alias)
		{
			aliases.add(alias);
		}
		
		public void removeAlias(String alias)
		{
			int index = aliases.indexOf(alias);
			if (index >= 0 )
				aliases.remove(index);
		}
		
		public boolean hasAlias(String alias)
		{
			int index = aliases.indexOf(alias);
			if (index >= 0 )
				return true;
			return false;
		}
	}