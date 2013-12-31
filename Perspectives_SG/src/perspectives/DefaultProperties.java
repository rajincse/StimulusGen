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


public class DefaultProperties {
	
	public static class StringPropertyType extends PropertyType
	{
		private String value;
		@Override
		public PropertyType copy() {
			// TODO Auto-generated method stub
			return new StringPropertyType(new String(value));
		}
		
		public StringPropertyType(String value)
		{
			this.value = value;
		}
		
		public String stringValue()
		{
			return value;
		}

		@Override
		public String typeName() {
			// TODO Auto-generated method stub
			return "StringPropertyType";
		}


		@Override
		public String serialize() {
			// TODO Auto-generated method stub
			return value;
		}

		@Override
		public StringPropertyType deserialize(String s) {
			return new StringPropertyType(s);
			
		}
		
	}
	
	public static class StringPropertyWidget extends PropertyWidget {
		
		JTextField control = null;
		JLabel readOnlyControl = null;
		
		public void widgetLayout()
		{			
			setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			this.add(new JLabel(this.p.getDisplayName()));

			final PropertyWidget th = this;
			
			control = new JTextField();
			control.setText(((StringPropertyType)th.p.getValue()).stringValue());
			control.setMaximumSize(new Dimension(70,20));
			control.setPreferredSize(new Dimension(70,20));
			
			readOnlyControl = new JLabel();
			readOnlyControl.setText(((StringPropertyType)th.p.getValue()).stringValue());
			//readOnlyControl.setMaximumSize(new Dimension(200,20));
			
			control.addActionListener(new java.awt.event.ActionListener() {
			    public void actionPerformed(java.awt.event.ActionEvent e) {

			        th.propertyUpdated(new StringPropertyType(control.getText()));      
			     }
			});
			
			this.add(Box.createRigidArea(new Dimension(5,1)));
			this.add(control);	
			this.add(Box.createHorizontalGlue());
		
			
			setPropertyReadOnly(p.getReadOnly());	
		}		
		
		public <T extends PropertyType> void setPropertyValue(T newvalue)
		{
			control.setText(((StringPropertyType)newvalue).stringValue());
			
		}


		@Override
		protected void setPropertyReadOnly(boolean r) {
			if (control != null)
			{
				if (r)
				{
					this.remove(control);					
					this.add(readOnlyControl,2);
				}
				else
				{
					this.remove(readOnlyControl);					
					this.add(control,2);
				}
			}
		}
	}

	public static class DoublePropertyType extends PropertyType
	{
		private double value;
		@Override
		public PropertyType copy() {
			// TODO Auto-generated method stub
			return new DoublePropertyType(value);
		}
		
		public DoublePropertyType(double value)
		{
			this.value = value;
		}
		
		public double doubleValue()
		{
			return value;
		}
		
		@Override
		public String typeName() {
			// TODO Auto-generated method stub
			return "DoublePropertyType";
		}


		@Override
		public String serialize() {
			// TODO Auto-generated method stub
			return ""+value;
		}

		@Override
		public DoublePropertyType deserialize(String s) {
			return new DoublePropertyType(Double.parseDouble(s));
			
		}
		
	}

	public static class DoublePropertyWidget extends PropertyWidget {
		
		JTextField control = null;
		JLabel readOnlyControl = null;
		
		public void widgetLayout()
		{
			setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			this.add(new JLabel(this.p.getDisplayName()));

			final PropertyWidget th = this;
			
			control = new JTextField();
			String startText = "" + ((DoublePropertyType)th.p.getValue()).doubleValue();
			control.setText(startText);
			control.setMaximumSize(new Dimension(70,20));
			control.setPreferredSize(new Dimension(70,20));
			
			readOnlyControl = new JLabel();
			
			readOnlyControl.setText(startText);
			//readOnlyControl.setMaximumSize(new Dimension(200,20));
			
			control.addActionListener(new java.awt.event.ActionListener() {
			    public void actionPerformed(java.awt.event.ActionEvent e) {
			        th.propertyUpdated(new DoublePropertyType(Double.parseDouble(control.getText())));      
			     }
			});
			
			//this.add(Box.createRigidArea(new Dimension(5,1)));
			this.add(Box.createHorizontalGlue());
			this.add(control);	
			
			
			
			setPropertyReadOnly(p.getReadOnly());
		}		
		
		public <T extends PropertyType> void setPropertyValue(T newvalue)
		{
			control.setText(""+((DoublePropertyType)newvalue).doubleValue());
		}



		@Override
		protected void setPropertyReadOnly(boolean r) {
			if (control != null)
			{
				if (r)
				{
					this.remove(control);					
					this.add(readOnlyControl,2);
				}
				else
				{
					this.remove(readOnlyControl);					
					this.add(control,2);
				}
			}
			
		}
	}

	public static class IntegerPropertyType extends PropertyType
	{
		private int value;
		@Override
		public PropertyType copy() {
			// TODO Auto-generated method stub
			return new IntegerPropertyType(value);
		}
		
		public IntegerPropertyType(int value)
		{
			this.value = value;
		}
		
		public int intValue()
		{
			return value;
		}
		
		public String typeName() {
			// TODO Auto-generated method stub
			return "IntegerPropertyType";
		}

		@Override
		public String serialize() {
			// TODO Auto-generated method stub
			return ""+value;
		}

		@Override
		public IntegerPropertyType deserialize(String s) {
			return new IntegerPropertyType(Integer.parseInt(s));
			
		}
		
	}
	
	public static class IntegerPropertyWidget extends PropertyWidget {
		
		JSpinner control = null;
		JLabel readOnlyControl = null;
		public void widgetLayout()
		{			
			setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			
			//this.add(Box.createHorizontalGlue());
			this.add(new JLabel(this.p.getDisplayName()));
			
			final PropertyWidget th = this;
			
			control = new JSpinner();
			control.setValue(((IntegerPropertyType)th.p.getValue()).intValue());
			
			control.setPreferredSize(new Dimension(100,20));
			control.setMaximumSize(new Dimension(100,20));
			
			readOnlyControl = new JLabel();
			readOnlyControl.setText(((IntegerPropertyType)th.p.getValue()).intValue()+"");
			
			
			ChangeListener listener = new ChangeListener() {
			      public void stateChanged(ChangeEvent e) {
			    	 th.propertyUpdated(new IntegerPropertyType(new Integer(((JSpinner)e.getSource()).getValue().toString())));			        
			      }
			    };
			control.addChangeListener(listener);

			//this.add(Box.createRigidArea(new Dimension(5,1)));
			this.add(Box.createHorizontalGlue());
			this.add(control);	
			
			
			
			setPropertyReadOnly(p.getReadOnly());
		}
		
		public <T extends PropertyType> void setPropertyValue(T newvalue)
		{
			control.setValue(((IntegerPropertyType)newvalue).intValue());
		}



		@Override
		protected void setPropertyReadOnly(boolean r) {
			if (control != null)
			{
				if (r)
				{
					this.remove(control);					
					this.add(readOnlyControl,2);
				}
				else
				{
					this.remove(readOnlyControl);					
					this.add(control,2);
				}
			}			
		}
	}

	static public class OptionsPropertyType extends PropertyType
	{
		public int selectedIndex;
		public String[] options;
		
		@Override
		public OptionsPropertyType copy() {
			OptionsPropertyType opt = new OptionsPropertyType();
			opt.selectedIndex = selectedIndex;
			opt.options = options.clone();
			return opt;
		}
		
		public String typeName() {
			// TODO Auto-generated method stub
			return "OptionsPropertyType";
		}


		@Override
		public String serialize() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public OptionsPropertyType deserialize(String s) {
			return null;
			
		}
	}

	public static class OptionsPropertyWidget extends PropertyWidget {
		
		JComboBox control = null;
		
		public void widgetLayout()
		{
			setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			this.add(new JLabel(this.p.getDisplayName()));
			
			final PropertyWidget th = this;
			
			control = new JComboBox(((OptionsPropertyType)th.p.getValue()).options);
			
			control.setPreferredSize(new Dimension(100,20));
			control.setMaximumSize(new Dimension(100,20));
			
			ActionListener listener = new ActionListener() {
			      public void actionPerformed(ActionEvent e) {
			    	  OptionsPropertyType o =  ((OptionsPropertyType)th.p.getValue()).copy();	
			    	  o.selectedIndex = control.getSelectedIndex();
			    	 th.propertyUpdated(o);			        
			      }
			    };			    
		
			control.addActionListener(listener);
			
			//this.add(Box.createRigidArea(new Dimension(5,1)));
			this.add(Box.createHorizontalGlue());
			this.add(control);
			
			p.setReadOnly(p.getReadOnly());
			p.setVisible(p.getVisible());
		}
		
		public <T extends PropertyType> void setPropertyValue(T newvalue)
		{
			control.setSelectedIndex(((OptionsPropertyType)newvalue).selectedIndex);
		}

		@Override
		protected void setPropertyReadOnly(boolean r) {
			if (control != null)
				control.setEnabled(!r);
			
			
		}
	}	


	static public class ListPropertyType extends PropertyType
	{
		public int[] selectedIndeces;
		public String[] items;
		
		public boolean prefixSearchable = true;

		@Override
		public ListPropertyType copy() {
			// TODO Auto-generated method stub
			ListPropertyType l = new ListPropertyType();
			l.selectedIndeces = selectedIndeces.clone();
			l.items = new String[items.length];
			for (int i=0; i<items.length; i++)
				l.items[i] = new String(items[i]);
			return null;
		}
		
		public String typeName() {
			// TODO Auto-generated method stub
			return "ListPropertyType";
		}


		@Override
		public String serialize() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ListPropertyType deserialize(String s) {
			// TODO Auto-generated method stub
			return null;
			
		}
		
		
	}

	public static class ListPropertyWidget extends PropertyWidget {
		
		JList<String> control = null;
		
		public void widgetLayout()
		{
			setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			
			JPanel subpanel = new JPanel();
			subpanel.setBorder(null);
			//subpanel.setLayout(new BoxLayout(subpanel, BoxLayout.Y_AXIS));
			subpanel.setLayout(new BorderLayout());
			
			this.add(subpanel);
			
			subpanel.add(new JLabel(this.p.getDisplayName()),BorderLayout.NORTH);
			
			subpanel.setOpaque(false);
			
			final PropertyWidget th = this;
			
						
			control = new JList<String>(((ListPropertyType)th.p.getValue()).items);
			
			JScrollPane listScroller = new JScrollPane(control);
			listScroller.setPreferredSize(new Dimension(180, 80));			
			//listScroller.setMaximumSize(new Dimension(180,500));
			
			ListSelectionListener listener = new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					if (e.getValueIsAdjusting() == false)
					{
						ListPropertyType l = ((ListPropertyType)th.p.getValue()).copy();
						l.selectedIndeces = control.getSelectedIndices();
				    	th.propertyUpdated(l);		   
					}					
				}
			    };
			    
			   KeyListener keyListener = new KeyListener()
			    {
				   String full = "";
				   long lastType = -1;

					@Override
					public void keyPressed(KeyEvent arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void keyReleased(KeyEvent arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void keyTyped(KeyEvent e) {
						Date d = new Date();
							if (d.getTime() - lastType > 500)
								full = "";	
							
							full = full + e.getKeyChar();
							
							for (int i=0; i < control.getModel().getSize(); i++) {
							    String str = ((String)control.getModel().getElementAt(i)).toLowerCase();
							    if (str.startsWith(full)) {
							        control.setSelectedIndex(i); 
							        control.ensureIndexIsVisible(i); 
							        break;
							    }
							}
							
							lastType = d.getTime();
							
					}
			    	
			    };
			   
					    
			 
			control.addListSelectionListener(listener);
			control.addKeyListener(keyListener);
		
			subpanel.add(listScroller,BorderLayout.CENTER);
			subpanel.invalidate();
			
			this.add(Box.createHorizontalGlue());

			p.setReadOnly(p.getReadOnly());
			p.setVisible(p.getVisible());
		}
		
		public <T extends PropertyType> void setPropertyValue(T newvalue)
		{
			control.setListData(((ListPropertyType)newvalue).items);
			control.setSelectedIndices(((ListPropertyType)newvalue).selectedIndeces);			
		}

		@Override
		protected void setPropertyReadOnly(boolean r) {
			if (control != null)
				control.setEnabled(!r);	
		}
	}	


	static public class BooleanPropertyType extends PropertyType
	{
		private boolean value;
		
		public boolean prefixSearchable = true;

		@Override
		public BooleanPropertyType copy() {
			// TODO Auto-generated method stub
			return new BooleanPropertyType(value);
		}
		
		public BooleanPropertyType(boolean b)
		{
			value = b;
		}
		
		public boolean boolValue()
		{
			return value;
		}
		
		public String typeName() {
			// TODO Auto-generated method stub
			return "BooleanPropertyType";
		}

		@Override
		public String serialize() {
			// TODO Auto-generated method stub
			if (value)
				return "1";
			else
				return "0";
		}

		@Override
		public BooleanPropertyType deserialize(String s) {
			// TODO Auto-generated method stub
			if (s.equals("1"))
				return new BooleanPropertyType(true);
			else return new BooleanPropertyType(false);
		}
	}

	public static class BooleanPropertyWidget extends PropertyWidget {
		
		JCheckBox control = null;
		
		public void widgetLayout()
		{
			setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			this.add(new JLabel(this.p.getDisplayName()));
			
			final PropertyWidget th = this;
			
			control = new JCheckBox();
			control.setSelected(((BooleanPropertyType)th.p.getValue()).boolValue());
			ChangeListener listener = new ChangeListener() {
			      public void stateChanged(ChangeEvent e) {
			    	  boolean b = control.isSelected();
			    	  System.out.println(b);
			    	  th.propertyUpdated(new BooleanPropertyType(b));			        
			      }
			    };
			control.addChangeListener(listener);
			
			control.setBackground(new Color(0,0,0,0));

			//this.add(Box.createRigidArea(new Dimension(5,1)));
			this.add(Box.createHorizontalGlue());
			this.add(control);		
			//this.add(Box.createHorizontalGlue());
			
			
			setPropertyReadOnly(p.getReadOnly());
		}
		
		public <T extends PropertyType> void setPropertyValue(T newvalue)
		{
			control.setSelected(((BooleanPropertyType)newvalue).boolValue());
		}


		@Override
		protected void setPropertyReadOnly(boolean r) {
			if (control != null)
				control.setEnabled(!r);
		}
	}	

	static public class OpenFilePropertyType extends PropertyType
	{
		public String path = "";
		
		public String[] extensions = new String[0];	
		public int currentExtension = -1;
		
		public boolean onlyFiles = true;
		public boolean onlyDirectories = false;
		public boolean filesAndDirectories = false;
		
		public String dialogTitle = "";

		@Override
		public OpenFilePropertyType copy() {
			// TODO Auto-generated method stub
			OpenFilePropertyType of = new OpenFilePropertyType();			
			of.path = new String(path);
			of.currentExtension = currentExtension;
			of.onlyFiles = onlyFiles;
			of.onlyDirectories = onlyDirectories;
			of.filesAndDirectories = this.filesAndDirectories;
			of.dialogTitle = new String(this.dialogTitle);
			of.extensions = new String[extensions.length];
			for (int i=0; i<extensions.length; i++)
				of.extensions[i] = new String(extensions[i]);
			return of;
		}
		public String typeName() {
			// TODO Auto-generated method stub
			return "OpenFilePropertyType";
		}

		@Override
		public String serialize() {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public OpenFilePropertyType deserialize(String s) {
			return null;
			
		}
	}

	public static class OpenFilePropertyWidget extends PropertyWidget {
		
		FileFilter[] fileFilters;
		JButton control = null;
		
		public void widgetLayout()
		{
			
			setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			final PropertyWidget th = this;
			
			control = new JButton(this.p.getDisplayName(), new ImageIcon(Toolkit.getDefaultToolkit().getImage("Open16.gif")));
			//control.setMaximumSize(new Dimension(2000,20));
			control.setPreferredSize(new Dimension(130,20));
			ActionListener listener = new ActionListener() {
			      public void actionPerformed(ActionEvent e) {
			    	  
			    	  JFileChooser fc = new JFileChooser();
			    	  
			    	  final OpenFilePropertyType prop = (OpenFilePropertyType)th.p.getValue();
			    	  
			    	  if (prop.dialogTitle.length() > 0) fc.setDialogTitle(prop.dialogTitle);
			    	  
			    	  if (prop.extensions.length > 0)
			    	  {
			    		  fc.setAcceptAllFileFilterUsed(false);
			    		  
			    		  fileFilters = new FileFilter[prop.extensions.length];
			    		  
			    		  for (int i=0; i<prop.extensions.length; i++)
			    		  {
			    			  final int ii = i;
			    			  FileFilter ff = new FileFilter(){			    				  
			    				  @Override
			    				  public boolean accept(File f) {
			    					  if (f.isDirectory() || prop.extensions[ii].equals("*"))
			    						  return true;
			    					 

			    					String extension = getExtension(f);
								    if (extension != null && extension.equals(prop.extensions[ii]))
								    	return true;
								    else
								    	return false;
			    				  }
			    			  			

			    				  @Override
			    				  public String getDescription() {
			    					  if (prop.extensions[ii].equals("*"))
			    						  return "All Files";
			    					  return prop.extensions[ii]; 
									}			    			  
			    			  };
			    			  
			    			  fc.addChoosableFileFilter(ff);
			    			  
			    			  fileFilters[i] = ff;
			    		  }
			    		  
			    		  if (prop.currentExtension >=0 && prop.currentExtension<fileFilters.length)
			    			  fc.setFileFilter(fileFilters[prop.currentExtension]);
			    		
			    	  }
			    	  
			    	  if (prop.onlyDirectories)
			    		  fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			    	  else if (prop.filesAndDirectories)
			    		  fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			    	  			    	  
			    	  int returnVal = fc.showOpenDialog(th);
			    	  if (returnVal == JFileChooser.APPROVE_OPTION) {
			    		  OpenFilePropertyType v = prop.copy();
			    		  v.path = fc.getSelectedFile().getAbsolutePath();		              
			              th.propertyUpdated(v);	
			    	  }
			      }
			    };
			control.addActionListener(listener);

			this.add(Box.createHorizontalGlue());
			this.add(control);
			this.add(Box.createHorizontalGlue());
			
			p.setReadOnly(p.getReadOnly());
			p.setVisible(p.getVisible());
		}
		
		public <T extends PropertyType> void setPropertyValue(T newvalue)
		{
			this.propertyUpdated(newvalue);
		}
		
		 private String getExtension(File f) {
		        String ext = null;
		        String s = f.getName();
		        int i = s.lastIndexOf('.');
		 
		        if (i > 0 &&  i < s.length() - 1) {
		            ext = s.substring(i+1).toLowerCase();
		        }
		        return ext;
		    }

		@Override
		protected void setPropertyReadOnly(boolean r) {
			if (control != null)
				control.setEnabled(!r);
			
		}
	}	


	
	static public class SaveFilePropertyType extends PropertyType
	{
		public String path = "";
		
		public String[] extensions = new String[0];	
		public int currentExtension = -1;
		
		public boolean onlyFiles = true;
		public boolean onlyDirectories = false;
		public boolean filesAndDirectories = false;
		
		public String dialogTitle = "";

		@Override
		public SaveFilePropertyType copy() {
			// TODO Auto-generated method stub
			SaveFilePropertyType of = new SaveFilePropertyType();
			of.path = new String(path);
			of.currentExtension = currentExtension;
			of.onlyFiles = onlyFiles;
			of.onlyDirectories = onlyDirectories;
			of.filesAndDirectories = this.filesAndDirectories;
			of.dialogTitle = new String(this.dialogTitle);
			of.extensions = new String[extensions.length];
			for (int i=0; i<extensions.length; i++)
				of.extensions[i] = new String(extensions[i]);
			return of;
		}
		
		public String typeName() {
			// TODO Auto-generated method stub
			return "SaveFilePropertyType";
		}


		@Override
		public String serialize() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public SaveFilePropertyType deserialize(String s) {
			return null;
			
		}
	}

	public static class SaveFilePropertyWidget extends PropertyWidget {
		
		JButton control = null;
		FileFilter[] fileFilters;
		
		public void widgetLayout()
		{
			setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			final PropertyWidget th = this;
			
			control = new JButton(this.p.getDisplayName(), new ImageIcon(Toolkit.getDefaultToolkit().getImage("Save16.gif")));			
			control.setPreferredSize(new Dimension(130,20));
			ActionListener listener = new ActionListener() {
			      public void actionPerformed(ActionEvent e) {
			    	  
			    	  JFileChooser fc = new JFileChooser();
			    	  
			    	  final SaveFilePropertyType prop = (SaveFilePropertyType)th.p.getValue();
			    	  
			    	  if (prop.dialogTitle.length() > 0) fc.setDialogTitle(prop.dialogTitle);
			    	  
			    	  if (prop.extensions.length > 0)
			    	  {
			    		  fc.setAcceptAllFileFilterUsed(false);
			    		  
			    		  fileFilters = new FileFilter[prop.extensions.length];
			    		  
			    		  for (int i=0; i<prop.extensions.length; i++)
			    		  {
			    			  final int ii = i;
			    			  FileFilter ff = new FileFilter(){			    				  
			    				  @Override
			    				  public boolean accept(File f) {
			    					  if (f.isDirectory() || prop.extensions[ii].equals("*"))
			    						  return true;
			    					 

			    					String extension = getExtension(f);
								    if (extension != null && extension.equals(prop.extensions[ii]))
								    	return true;
								    else
								    	return false;
			    				  }
			    			  			

			    				  @Override
			    				  public String getDescription() {
			    					  if (prop.extensions[ii].equals("*"))
			    						  return "All Files";
			    					  return prop.extensions[ii]; 
									}			    			  
			    			  };
			    			  
			    			  fc.addChoosableFileFilter(ff);
			    			  
			    			  fileFilters[i] = ff;
			    		  }
			    		  
			    		  if (prop.currentExtension >=0 && prop.currentExtension<fileFilters.length)
			    			  fc.setFileFilter(fileFilters[prop.currentExtension]);
			    		
			    	  }
			    	  
			    	  if (prop.onlyDirectories)
			    		  fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			    	  else if (prop.filesAndDirectories)
			    		  fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
			    	  			    	  
			    	  int returnVal = fc.showOpenDialog(th);
			    	  if (returnVal == JFileChooser.APPROVE_OPTION) {
			    		  SaveFilePropertyType v = prop.copy();
			    		  v.path = fc.getSelectedFile().getAbsolutePath();			        
			              th.propertyUpdated(v);	
			    	  }
			      }
			    };
			control.addActionListener(listener);

			this.add(Box.createHorizontalGlue());
			this.add(control);
			this.add(Box.createHorizontalGlue());
			
			p.setReadOnly(p.getReadOnly());
			p.setVisible(p.getVisible());
		}
		
		 private String getExtension(File f) {
		        String ext = null;
		        String s = f.getName();
		        int i = s.lastIndexOf('.');
		 
		        if (i > 0 &&  i < s.length() - 1) {
		            ext = s.substring(i+1).toLowerCase();
		        }
		        return ext;
		    }
		
		public <T extends PropertyType> void setPropertyValue(T newvalue)
		{
			this.propertyUpdated(newvalue);
		}

		@Override
		protected void setPropertyReadOnly(boolean r) {
			if (control != null)
				control.setEnabled(!r);
			
		}
	}	


	static public class PercentPropertyType extends PropertyType
	{
		private double val = 0.5;
		
		public PercentPropertyType(double v)
		{
			setRatio(v);
		}
		public PercentPropertyType(int p)
		{
			setPercent(p);
		}
		public void setPercent(int vv)
		{
			double v = vv/100.;
			setRatio(v);
		}
		public void setRatio(double v)
		{
			if (v < 0) val = 0;
			else if (v > 1) val = 1;
			else val = v;		
		}		
		public int getPercent()
		{
			return (int)(val*100);
		}
		public double getRatio()
		{
			return val;
		}
		@Override
		public PercentPropertyType copy() {
			// TODO Auto-generated method stub
			return new PercentPropertyType(val);
		}
		public String typeName() {
			// TODO Auto-generated method stub
			return "PercentPropertyType";
		}

		@Override
		public String serialize() {
			// TODO Auto-generated method stub
			return ""+val;
		}
		@Override
		public PercentPropertyType deserialize(String s) {
			return new PercentPropertyType(Double.parseDouble(s));
			
		}
	}

	public static class PercentPropertyWidget extends PropertyWidget {
		
		JSlider control = null;
		JLabel readOnlyControl = null;
		public void widgetLayout()
		{			
			setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			this.add(new JLabel(this.p.getDisplayName()));
			
			final PropertyWidget th = this;
			
			control = new JSlider(0,100);
			control.setValue(((PercentPropertyType)th.p.getValue()).getPercent());
			control.setPreferredSize(new Dimension(70,20));
			control.setBackground(new Color(0,0,0,0));
			
			readOnlyControl = new JLabel();
			readOnlyControl.setText(((PercentPropertyType)th.p.getValue()).getPercent()+"%");
			
			
			ChangeListener listener = new ChangeListener() {
			      public void stateChanged(ChangeEvent e) {
			    	 th.propertyUpdated(new PercentPropertyType(((JSlider)e.getSource()).getValue()));			        
			      }
			    };
			control.addChangeListener(listener);

			//this.add(Box.createRigidArea(new Dimension(5,1)));
			this.add(Box.createHorizontalGlue());
			this.add(control);			
		
			
			setPropertyReadOnly(p.getReadOnly());
		}
		
		public <T extends PropertyType> void setPropertyValue(T newvalue)
		{
			control.setValue(((PercentPropertyType)newvalue).getPercent());
		}



		@Override
		protected void setPropertyReadOnly(boolean r) {
			if (control != null)
			{
				if (r)
				{
					this.remove(control);					
					this.add(readOnlyControl,2);
				}
				else
				{
					this.remove(readOnlyControl);					
					this.add(control,2);
				}
			}			
		}
	}

	static public class ColorPropertyType extends PropertyType
	{
		private Color value;
		
		public boolean prefixSearchable = true;

		@Override
		public ColorPropertyType copy() {
			// TODO Auto-generated method stub
			return new ColorPropertyType(new Color(value.getRed(), value.getGreen(), value.getBlue(), value.getAlpha()));
		}
		
		public ColorPropertyType(Color c)
		{
			value = c;
		}
		
		public Color colorValue()
		{
			return value;
		}
		
		public String typeName() {
			// TODO Auto-generated method stub
			return "ColorPropertyType";
		}


		@Override
		public String serialize() {
			// TODO Auto-generated method stub
			return ""+value.getRed() + "," + value.getGreen() + "," + value.getBlue() + "," + value.getAlpha();
		}

		@Override
		public ColorPropertyType deserialize(String s) {
			String[] split = s.split(",");
			if (split.length <= 3)
				return new ColorPropertyType(new Color(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])));
			if (split.length > 3)
				return new ColorPropertyType(new Color(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3])));
			return null;
			
		}
	}

	public static class ColorPropertyWidget extends PropertyWidget {
		
		JButton control = null;
				
		public void widgetLayout()
		{
			setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			final PropertyWidget th = this;
			
			control = new JButton(this.p.getDisplayName(), new ImageIcon(Toolkit.getDefaultToolkit().getImage("color_picker.GIF")));
			//control = new JButton("coo");
			control.setMaximumSize(new Dimension(2000,20));
			control.setPreferredSize(new Dimension(130,20));
			ActionListener listener = new ActionListener() {
			      public void actionPerformed(ActionEvent e) {  
			    	 
			    	  Color newColor = JColorChooser.showDialog(th,"Choose Color",((ColorPropertyType)th.p.getValue()).colorValue());
			    	  th.propertyUpdated(new ColorPropertyType(newColor));
			    	  
			      }
			    };
			control.addActionListener(listener);

			this.add(Box.createHorizontalGlue());
			this.add(control);
			this.add(Box.createHorizontalGlue());
			
			p.setReadOnly(p.getReadOnly());
			p.setVisible(p.getVisible());
		}		
		
		public <T extends PropertyType> void setPropertyValue(T newvalue)
		{
			this.propertyUpdated(newvalue);
		}

		@Override
		protected void setPropertyReadOnly(boolean r) {
			if (control != null)
				control.setEnabled(!r);	
		}
	}
}
