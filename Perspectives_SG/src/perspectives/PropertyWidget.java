package perspectives;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.Date;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;


abstract class PropertyWidget extends JPanel{
	
	private PropertyManager pm;
	//private PropertyManagerViewer pmviewer;
	public Property p;
	
	private boolean changeFromOutside = false;
	
	public PropertyWidget()
	{
		
	}
	
	public void setReferences(Property p, PropertyManager pm)
	{
		this.pm = pm;
		this.p = p;			
	}
	
	public abstract void widgetLayout();
	
	public <T extends PropertyType> void propertyUpdated(T newvalue)
	{
		pm.propertyUpdated(p, newvalue);
		p.setPropertyManager(null);
		p.setValue(newvalue);
		p.setPropertyManager(pm);		
	}
	
	/**
	 * sets the actual widgets to reflect the value change
	 * @param newvalue
	 */
	protected abstract <T extends PropertyType> void setPropertyValue(T newvalue);
	
	public <T extends PropertyType> void setProperty(T newvalue)
	{
		changeFromOutside = true;
		setPropertyValue(newvalue);
		changeFromOutside = false;
	}
	
	protected abstract void setPropertyReadOnly(boolean v);
					
}

abstract class PropertyWidgetFactory {
	
	abstract PropertyWidget createWidget();
	
}

