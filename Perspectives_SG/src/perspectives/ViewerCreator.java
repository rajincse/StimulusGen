package perspectives;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/** 
 * A small dialog that helps users create Viewers. It contains a list of possible types of Viewers, a list of available datasources, a text field that will guide users, and a name edit box. <br>
 * 
 * Users need to select a type of Viewer, see in the text field what type of data sources are required as inputs for that Viewer, select such datasources from the available ones, name the viewer and then create it.
 * <br>
 * @author rdjianu
 *
 */
public class ViewerCreator extends JDialog
{
	//pointer to the parent Environment
	private Environment env = null;
	
	//this is what we'll return as the result
	Viewer createdViewer = null;
	
	public String createdViewerName = "";
	
	//as users select different types of viewers from the available ones, this gets updated
	ViewerFactory currentViewerFactory = null;
	
	private JTextField viewerName;
	
	public ViewerCreator(Environment env, String defaultName)
	{
		final ViewerCreator thisDialog = this;
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		this.requestFocus();
		this.setAlwaysOnTop(true);
		this.setModal(true);
		
		this.setIconImage(Toolkit.getDefaultToolkit().getImage("new_viewer.png"));
		this.setTitle("Create New Viewer");
		
		this.setBounds(200, 200, 300, 400);
		 
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		
		JPanel lists = new JPanel();
		lists.setLayout(new BoxLayout(lists, BoxLayout.X_AXIS));
		
		final JTextArea info = new JTextArea();
		info.setFont(new Font("Courier", Font.PLAIN, 11));
		info.setLineWrap(true);
		info.setWrapStyleWord(true);
		
		JPanel button = new JPanel();
		button.setLayout(new BoxLayout(button, BoxLayout.X_AXIS));
		final JButton create = new JButton("Create");
		
		create.setEnabled(false);
		
		button.add(Box.createHorizontalGlue());
		button.add(new JLabel("Name: "));
		viewerName = new JTextField(defaultName);
		viewerName.setMaximumSize(new Dimension(2000,20));
		viewerName.setMinimumSize(new Dimension(100,20));
		viewerName.setPreferredSize(new Dimension(100,20));		
		button.add(viewerName);
		button.add(Box.createHorizontalStrut(20));
		button.add(create);
		button.add(Box.createHorizontalGlue());
		
		this.getContentPane().add(lists);
		this.getContentPane().add(Box.createVerticalStrut(10));
		this.getContentPane().add(info);
		this.getContentPane().add(Box.createVerticalStrut(10));
		this.getContentPane().add(button);
		this.getContentPane().add(Box.createVerticalGlue());	
		
		//create the list of available viewer types
	    DefaultListModel<String> lm2 = new DefaultListModel<String>();
	    final JList dataList2  = new JList<String>(lm2);	     	       
	    dataList2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	   
	    JScrollPane listScrollPane2 = new JScrollPane(dataList2);
	    listScrollPane2.setPreferredSize(new Dimension(150,150));
	    listScrollPane2.setMaximumSize(new Dimension(2000,150));
	    
	    //populate the available viewers list
	    Vector<ViewerFactory> dsf2 = env.getViewerFactories();
	    for (int i=0; i<dsf2.size(); i++)
	    	lm2.add(lm2.size(),dsf2.get(i).creatorType());
	       
	    lists.add(listScrollPane2);
	    
	    //create the list of available datasources
	    DefaultListModel<String> lm1 = new DefaultListModel<String>();
	    final JList dataList1  = new JList<String>(lm1);	     	       
	    dataList1.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	    
	    //populate the available datasources list
	    Vector<DataSource> dsf = env.getDataSources();
	    for (int i=0; i<dsf.size(); i++)
	    	lm1.add(lm1.size(),dsf.get(i).getName());
	   
	    
	    JScrollPane listScrollPane1 = new JScrollPane(dataList1);
	    listScrollPane1.setPreferredSize(new Dimension(150,150));
	    listScrollPane1.setMaximumSize(new Dimension(2000,150));
	       
	    lists.add(listScrollPane1);	   
	    
	    //done with the layout
	    
	    final Environment finalenv = env;
	    
	    //if the viewer list selection changes, we need to update the current viewer factory, we need to clear any user selections in the data source list since they are trying to create a new type of viewer, 
	    //display their required data sources in the designted text field, and, if the datasource requirements are met (probably no datasource), then we an activate the create button
	    dataList2.addListSelectionListener(new ListSelectionListener()
	       {
			public void valueChanged(ListSelectionEvent e) {	
				int index = dataList2.getSelectedIndex();
				
				currentViewerFactory = null;
				dataList1.clearSelection();
				
				if (index < 0)
				{
					currentViewerFactory = null;
					return;

				}
				ViewerFactory vf = finalenv.getViewerFactories().get(index);
				String inf = vf.requiredDataString();
				info.setText(inf);
				currentViewerFactory = vf;
					
				currentViewerFactory.clearData();
				
				if (currentViewerFactory.isAllDataPresent())
					create.setEnabled(true);
				else
					create.setEnabled(false);				
			}	    	   
	       });		
		
       //if the datasource list selection changes we need to check whether all the data source requirements for the currently selected viewer type are met and depending on that validate or invalidate the create button
	    dataList1.addListSelectionListener(new ListSelectionListener()
	       {
			public void valueChanged(ListSelectionEvent e)
			{
				if (currentViewerFactory == null)
					return;		
				
				currentViewerFactory.clearData();
				
				int[] selection = dataList1.getSelectedIndices();
				String invalids = "";
				for (int i=0; i<selection.length; i++)
				{
					DataSource df = finalenv.getDataSources().get(selection[i]);
					
					if (!df.isLoaded())
						invalids += df.getName();
					else
						currentViewerFactory.addDataSource(df);
				}
				if (!invalids.equals(""))
					JOptionPane.showMessageDialog(getContentPane(),"One or more of the selected data sources are invalid (e.g., no actual data loaded): " + invalids); 
				
				if (currentViewerFactory.isAllDataPresent())
					create.setEnabled(true);
				else
					create.setEnabled(false);
			}
	       });
	    
	    lists.add(Box.createHorizontalStrut(10));
	    
	    
   
	    create.addActionListener(new ActionListener()
	    {
			public void actionPerformed(ActionEvent arg0) {				
				
				if (viewerName.getText().isEmpty())
				{
					JOptionPane.showMessageDialog(thisDialog, "Please enter a viewer name first.");
					return;
				}
				createdViewer = currentViewerFactory.create(viewerName.getText());
				thisDialog.setVisible(false);
				
			}	    	
	    });     
		
	}

}
