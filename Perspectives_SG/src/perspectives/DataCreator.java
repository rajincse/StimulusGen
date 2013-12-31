package perspectives;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/** 
 * A small dialog that helps users create DataSources. It contains a list of possible types of Datasources, a text field describing those datasources, and a name edit box. <br>
 * <br>
 * @author rdjianu
 *
 */
public class DataCreator extends JDialog
{
	//pointer to the parent Environment
	private Environment env = null;
	
	//this is what we'll return as the result
	DataSource createdDatasource = null;
	
	public String createdDatasourceName = "";
	
	private JTextField datasourceName;
	
	private int defaultNameCounter = 1;
	
	private DataSourceFactory currentDataSourceFactory = null;
	
	public DataCreator(Environment env, String defaultName)
	{
		final DataCreator thisDialog = this;
		
		//this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		this.requestFocus();
		this.setAlwaysOnTop(true);
		this.setModal(true);
		
		this.setIconImage(Toolkit.getDefaultToolkit().getImage("new_data.png"));
		this.setTitle("Create New Data");
		
		this.setBounds(200, 200, 300, 300);
		 
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
				
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
		datasourceName = new JTextField(defaultName);
		datasourceName.setMaximumSize(new Dimension(2000,20));
		datasourceName.setMinimumSize(new Dimension(100,20));
		datasourceName.setPreferredSize(new Dimension(100,20));		
		button.add(datasourceName);
		button.add(Box.createHorizontalStrut(20));
		button.add(create);
		button.add(Box.createHorizontalGlue());
		
		//create the list of available datasource types
	    DefaultListModel<String> lm2 = new DefaultListModel<String>();
	    final JList dataList2  = new JList<String>(lm2);	     	       
	    dataList2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	   
	    JScrollPane listScrollPane2 = new JScrollPane(dataList2);
	    listScrollPane2.setPreferredSize(new Dimension(150,150));
	    listScrollPane2.setMaximumSize(new Dimension(2000,150));
	    
	    //populate the available viewers list
	    Vector<DataSourceFactory> dsf2 = env.getDataFactories();
	    for (int i=0; i<dsf2.size(); i++)
	    	lm2.add(lm2.size(),dsf2.get(i).creatorType());
		
		this.getContentPane().add(listScrollPane2);
		this.getContentPane().add(Box.createVerticalStrut(10));
		this.getContentPane().add(info);
		this.getContentPane().add(Box.createVerticalStrut(10));
		this.getContentPane().add(button);
		this.getContentPane().add(Box.createVerticalGlue());	     
	      
	    
	    
	    //done with the layout
	    
	    final Environment finalenv = env;
	    
	    //if the viewer list selection changes, we need to update the current viewer factory, we need to clear any user selections in the data source list since they are trying to create a new type of viewer, 
	    //display their required data sources in the designted text field, and, if the datasource requirements are met (probably no datasource), then we an activate the create button
	    dataList2.addListSelectionListener(new ListSelectionListener()
	       {
			public void valueChanged(ListSelectionEvent e) {	
				int index = dataList2.getSelectedIndex();
								
				if (index < 0)
				{
					create.setEnabled(false);
					currentDataSourceFactory = null;
					return;
				}

				DataSourceFactory df = finalenv.getDataFactories().get(index);
				String inf = df.description();
				info.setText(inf);				
								
				currentDataSourceFactory = df;
				
				create.setEnabled(true);
								
			}	    	   
	       });			
          
	    
   
	    create.addActionListener(new ActionListener()
	    {
			public void actionPerformed(ActionEvent arg0) {				
				
				if (datasourceName.getText().isEmpty())
				{
					JOptionPane.showMessageDialog(thisDialog, "Please enter a viewer name first.");
					return;
				}
				createdDatasource = currentDataSourceFactory.create(datasourceName.getText());
				thisDialog.setVisible(false);				
			}	    	
	    });     
		
	}

}

