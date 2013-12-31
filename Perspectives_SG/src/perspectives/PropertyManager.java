package perspectives;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;


public class PropertyManager{
	
	
	ArrayList<Property> props;
	
	protected String name;
		
	ArrayList<PropertyChangeListener> listeners;
	
	PropertyManagerGroup propertyManagerGroup;
	
	
	 private static ArrayList<PropertyType> acceptedTypes = new ArrayList<PropertyType>();
	 
	 public static void registerType(PropertyType c)
	 {
		 int found = -1;
		 for (int i=0; i<acceptedTypes.size(); i++)
			 if (acceptedTypes.get(i).typeName().equals(c.typeName()))
			 {
				 found = i;
				 break;
			 }
		 
		 if (found < 0)
				acceptedTypes.add(c);
	 }
	 
	 public static PropertyType deserialize(String type, String value)
	 {
		 int found = -1;
		 for (int i=0; i<acceptedTypes.size(); i++)
			 if (acceptedTypes.get(i).typeName().equals(type))
			 {
				 found = i;
				 break;
			 }
		 if (found < 0) return null;
		 
		 return acceptedTypes.get(found).deserialize(value);
	 }
	
	
	
	TaskObserverDialog taskObserverDialog;
	
	public PropertyManager(String name) {		
		
		this.name = name;			
		props = new ArrayList<Property>();	
		listeners = new ArrayList<PropertyChangeListener>();
	}
	
	public String getName()
	{
		return name;
	}	

	protected void addProperty(Property p) throws Exception {
		
		int index = -1;
		for (int i=0; i<acceptedTypes.size(); i++)
			if (acceptedTypes.get(i).typeName().equals(p.getValue().typeName()))
			{
				index = i;
				break;
			}
		if (index < 0)
			throw new Exception();
		
		props.add(p);
		p.setPropertyManager(this);
		
		for (int i=0; i<listeners.size(); i++)
			listeners.get(i).propertyAdded(this, p);
		

	}
	
	protected void removeProperty(String name)
	{		
		for (int i=0; i<props.size(); i++)
			if (props.get(i).getName().equals(name))
			{
				for (int j=0; j<listeners.size(); j++)
					listeners.get(j).propertyRemoved(this, props.get(i));
				
				props.remove(i);
				break;
			}
	}

	
	public Property getProperty(String n) {
		for (int i=0; i<props.size(); i++)
			if (props.get(i).getName().equals(n))
				return props.get(i);
		return null;
	}
	
	public <T extends PropertyType> void propertyUpdated(Property p, T newvalue)
	{
		
	}
	
	public <T extends PropertyType> boolean propertyBroadcast(Property p, T newvalue, PropertyManager origin)
	{
		return false;
	}
	
	<T extends PropertyType> void receivePropertyBroadcast(PropertyManager origin, String propName, T newvalue)
	{
		Property p = null;
		for (int i=0; i<this.props.size(); i++)
			if (props.get(i).getName().equals(propName) || props.get(i).hasAlias(propName))
			{
				p = props.get(i);
				break;
			}
		if (p == null || !p.getPublic()) return;
		
		T nvc = newvalue.copy();
		boolean ret = propertyBroadcast(p, nvc, origin);
		
		if (ret == true) //accepted and processed
		{
			for (int i=0; i<listeners.size(); i++)
				listeners.get(i).propertyValueChanged(this, p, newvalue);
			
			if (propertyManagerGroup != null && p.getPublic())
			{
				p.setPublic(false);
				propertyManagerGroup.broadcast(this, p, newvalue);
				p.setPublic(true);
			}
		}
	}
	
	public void addPropertyChangeListener(PropertyChangeListener pcl)
	{
		listeners.add(pcl);
	}
	
	public void setAcceptedTypes(ArrayList<PropertyType> t)
	{
		acceptedTypes = t;
	}
	
	
	public void setPropertyManagerGroup(PropertyManagerGroup g)
	{
		propertyManagerGroup = g;		
	}
	
	public PropertyManagerGroup getPropertyManagerGroup()
	{
		return propertyManagerGroup;
	}
	
	public TaskObserverDialog getTaskObserverDialog()
	{
		return taskObserverDialog;
	}
	
	public <T extends PropertyType> void setPropertyValueCallback(Property p, T newvalue)
	{
		for (int i=0; i<listeners.size(); i++)
			listeners.get(i).propertyValueChanged(this, p, newvalue);
		
		this.propertyUpdated(p, newvalue);
		
		if (propertyManagerGroup != null && p.getPublic())
		{
			p.setPublic(false);
			propertyManagerGroup.broadcast(this, p, newvalue);
			p.setPublic(true);
		}
	}
	public void setPropertyVisibleCallback(Property p, boolean v)
	{
		for (int i=0; i<listeners.size(); i++)
			listeners.get(i).propertyVisibleChanged(this, p, v);
	}
	
	public void setPropertyReadOnlyCallback(Property p, boolean v)
	{
		for (int i=0; i<listeners.size(); i++)
			listeners.get(i).propertyReadonlyChanged(this, p, v);
	}
	
	public Property[] getProperties()
	{
		Property[] p = new Property[props.size()];
		for (int i=0; i<p.length; i++)
			p[i] = props.get(i);
		return p;
	}
	

	
	
}
