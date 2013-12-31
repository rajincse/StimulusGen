package perspectives;
import java.awt.BasicStroke;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyVetoException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import util.Util;


/**
 * 
 * @author rdjianu <br>
 * 
 * Main class that regulates the global activity of Perspectives. It sets up the GUI, keeps track of Viewers, Datasources, links between them, propagates events etc. You should create one of these classes in your main function.
 * <br>
 * 
 * The Environment consists of a viewer area that behaves like a desktop. ViewerContainers can live within this space wrapping Viewer objects. ViewerContainers are windows that can be moved, minimized, maximized, and closed.
 * On the left hand side the Environment has a few controls: a menu panel in which users can manage datasources and viewers and a dedicated slot below in which properties of selected items (DataSources and Viewers) are displayed.
 * Users may draw links between Viewers displayed in the viewer area. To do this, they need to activate the Links toggle button and drag little icons that are placed under each ViewerContainer. If they release the arrow inside
 * a target ViewerContainer those two viewwers will be linked, meaning a property change in one viewer will be transmitted to the other. 
 *
 */
public class Environment extends PropertyManagerGroup{
	
	private boolean offline = false;
	
	private JPanel allPanel; //main gui
	private JDesktopPane viewerArea; //has a this
	private JPanel controlArea; //and this
	
	private JPanel propertyPanel; //contained in control area
	private JPanel menuPanel; //contained in control area
	
	//these give the types of datasources and viewers that can be created by users; developers can add such factories to the Environment
	private Vector<ViewerFactory> viewerFactories = new Vector<ViewerFactory>();
	private Vector<DataSourceFactory> dataFactories = new Vector<DataSourceFactory>();
	
	//this is used for links, because then links are drawn the viewer windows are minimize. ultimately they need to be restored, which is what this is for
	private Vector<Rectangle> viewerBounds = new Vector();	
	
	//viewers are contained in viewerContainers so we need to have a list of both;
	private Vector<Viewer> viewers = new Vector<Viewer>();
	private Vector<ViewerContainer> viewerContainers = new Vector<ViewerContainer>();
	private Vector<ViewerWindow> viewerWindows = new Vector<ViewerWindow>();
	
	//contains all created data sources
	private JList<String> dataList;
	
	private Vector<DataSource> dataSources = new Vector<DataSource>();
	
	//used to repain the drwaing area. we need to repaint the entire drawing area because of the links we are dragging around;
	private Timer timer;
	
	private Font font = new Font("Courier", Font.PLAIN, 11);
	
	//the icon displayed for Perspectives main window
	private Icon frameicon;
	
	private Icon viewerIcon, newViewerIcon, dataIcon, newDataIcon, linksIcon, helpIcon;
	
	//the main frame
	private JFrame frame;
	
	//auto-naming integers for generating automatic names for data sources and viewers
	private int autoDataName = 1;
	
	//auto-naming integers for generating automatic names for data sources and viewers
	private int autoViewerName = 1;	
	
	private int newDataY = 50;
    private int newDataX = 20;
    
    private int defaultDataNameCounter = 1;
    

	
	
	// -----------------------------    Managing and drawing links -----------------------------
	
	//this is how we store connections between viewers: pairs of viewers and then a boolean indicating whether the connection is bidirectional or not.
	private Vector<Integer> links1 = new Vector<Integer>();
	private Vector<Integer> links2 = new Vector<Integer>();
	private Vector<Boolean> linksDouble = new Vector<Boolean>();
	
	private boolean showingLinks = false; //indicating whether we are in link dragging mode
	
	private Line2D currentArrow; //for drawing draggable arrows; actual arrow, type of arrow, and indeces in the viewer lists for the endpoints
	private boolean currentArrowDouble = false;
	private int currentArrowSource = -1;
	private int currentArrowDest = -1;
	
	//little icons below viewers and on links that users can use to set up or delete a new link
	private Image icon_doublearrow, icon_singlearrow, icon_cross;
	
	

	

	
	public Environment(boolean offline)
	{
		this.offline = offline;
		
	     ///set default properties
	     
				this.registerNewType(new DefaultProperties.StringPropertyType(""), new PropertyWidgetFactory() {
					PropertyWidget createWidget() {
						return new DefaultProperties.StringPropertyWidget();
				}});		
				
				this.registerNewType(new DefaultProperties.DoublePropertyType(0), new PropertyWidgetFactory() {
					PropertyWidget createWidget() {
						return new DefaultProperties.DoublePropertyWidget();
				}});	
				
				this.registerNewType(new DefaultProperties.IntegerPropertyType(0), new PropertyWidgetFactory() {
					PropertyWidget createWidget() {
						return new DefaultProperties.IntegerPropertyWidget();
				}});	
				
				this.registerNewType(new DefaultProperties.BooleanPropertyType(true), new PropertyWidgetFactory() {
					PropertyWidget createWidget() {
						return new DefaultProperties.BooleanPropertyWidget();
				}});		
								
				this.registerNewType(new DefaultProperties.OptionsPropertyType(), new PropertyWidgetFactory() {
					PropertyWidget createWidget() {
						return new DefaultProperties.OptionsPropertyWidget();
				}});	
									
				this.registerNewType(new DefaultProperties.OpenFilePropertyType(), new PropertyWidgetFactory() {
					PropertyWidget createWidget() {
						return new DefaultProperties.OpenFilePropertyWidget();
				}});			
								
				this.registerNewType(new DefaultProperties.SaveFilePropertyType(), new PropertyWidgetFactory() {
					PropertyWidget createWidget() {
						return new DefaultProperties.SaveFilePropertyWidget();
				}});			
								
				this.registerNewType(new DefaultProperties.PercentPropertyType(0), new PropertyWidgetFactory() {
					PropertyWidget createWidget() {
						return new DefaultProperties.PercentPropertyWidget();
				}});		
									
				this.registerNewType(new DefaultProperties.ColorPropertyType(Color.black), new PropertyWidgetFactory() {
					PropertyWidget createWidget() {
						return new DefaultProperties.ColorPropertyWidget();
				}});		
							
				this.registerNewType(new DefaultProperties.ListPropertyType(), new PropertyWidgetFactory() {
					PropertyWidget createWidget() {
						return new DefaultProperties.ListPropertyWidget();
				}});		
		
		if (offline)
			return;
		
		 frame = new JFrame("Perspectives");
		 
		 //maximize the window
	     GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
	     frame.setMaximizedBounds(e.getMaximumWindowBounds());
	     frame.setExtendedState(frame.getExtendedState()|JFrame.MAXIMIZED_BOTH );
	     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	     frame.setVisible(true);	    
	        
		 
		 Toolkit tool = Toolkit.getDefaultToolkit();		 
		 Image icon = tool.getImage("frame_icon.png");
		 frame.setIconImage(icon);
		 
		 frameicon = new ImageIcon(icon);
		 
		 viewerIcon = new ImageIcon(tool.getImage("viewer.png"));
		 newViewerIcon = new ImageIcon(tool.getImage("new_viewer.png"));
		 dataIcon = new ImageIcon(tool.getImage("data.png"));
		 newDataIcon = new ImageIcon(tool.getImage("new_data.png"));
		 linksIcon = new ImageIcon(tool.getImage("links.png"));
		 helpIcon = new ImageIcon(tool.getImage("help.png"));
	     
	     allPanel = new JPanel(new BorderLayout());
	     frame.getContentPane().add(allPanel);  
	     
	     //sets the look and feel of th esystem (e.g. Windows)
	     try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e1) {
				e1.printStackTrace();
		}
	     
	     
	
	     
	     //see variable declaration
		 icon_doublearrow = tool.getImage("two-arrow.png");
		 icon_singlearrow = tool.getImage("one-arrow.png");
		 icon_cross = tool.getImage("cross.png");
		 
		 
	     //the viewer area instantiation : it implements paint so that arrows can be dragged and drawn; it also adds mouse listeners for the same reasons;
		 
	     viewerArea = new JDesktopPane(){
	    	 
	    	 protected void paintComponent(Graphics g)
	    	    {
 	        		int w = 175;
 	        		int h = 45;
 	        		int mw = 15;
 	        		int mh = 15;
 	        	
	    	        if (!showingLinks)
	    	        	return;
	    	        
            	    ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                         RenderingHints.VALUE_ANTIALIAS_ON);
            	    
            	    ((Graphics2D)g).setStroke(new BasicStroke(2));	    	        
    	        
            	    //first draw existing links; we don't draw links between the centers of the viewers; instead we draw the arrows so that they don't overlap the bounds of the viewers; we use intersection points with the bounds for that;
	    	        for (int i=0; i<links1.size(); i++)
	    	        {
	    	        	int i1 = links1.get(i).intValue();
	    	        	int i2 = links2.get(i).intValue();
	    	        	
	    	        	int x1 = viewerWindows.get(i1).getBounds().x + 80;
	    	        	int y1 = viewerWindows.get(i1).getBounds().y + 15;
	    	        	int x2 = viewerWindows.get(i2).getBounds().x + 80;
	    	        	int y2 = viewerWindows.get(i2).getBounds().y + 15;
	    	        	
	    	        	
	    	        	Rectangle2D b1 = viewerWindows.get(i1).getBounds();
	    	        	Rectangle2D b2 = viewerWindows.get(i2).getBounds();
	    	        	
	    	        	//check for intersection with the rectangle bounds
	    	        	Point2D p1 = null;
	    	        	Point2D p2 = null;
	    	        	if (Util.linesIntersect(x1, y1, x2, y2, b1.getX()-mw, b1.getY()-mh, b1.getX()+w, b1.getY()-mh))
	    	             		p1 = Util.getLineLineIntersection(x1, y1, x2, y2, b1.getX()-mw, b1.getY()-mh, b1.getX()+w, b1.getY()-mh);
	    	        	
	    	        	else if (Util.linesIntersect(x1, y1, x2, y2, b1.getX()+w, b1.getY()-mh, b1.getX()+w, b1.getY()+h))
	    	        		p1 = Util.getLineLineIntersection(x1, y1, x2, y2, b1.getX()+w, b1.getY()-mh, b1.getX()+w, b1.getY()+h);
	    	        		    	        	
	    	        	else if (Util.linesIntersect(x1, y1, x2, y2, b1.getX()+w, b1.getY()+h, b1.getX()-mw, b1.getY()+h))	    	        	
	    	        		p1 = Util.getLineLineIntersection(x1, y1, x2, y2, b1.getX()+w, b1.getY()+h, b1.getX()-mw, b1.getY()+h);
	    	        	    	        	
	    	        	else if (Util.linesIntersect(x1, y1, x2, y2, b1.getX()-mw, b1.getY()+h, b1.getX()-mw, b1.getY()-mh))
	    	        		p1 = Util.getLineLineIntersection(x1, y1, x2, y2, b1.getX()-mw, b1.getY()+h, b1.getX()-mw, b1.getY()-mh);
	    	        
	    	          	if (Util.linesIntersect(x1, y1, x2, y2, b2.getX()-mw, b2.getY()-mh, b2.getX()+w, b2.getY()-mh))
	    	        		p2 = Util.getLineLineIntersection(x1, y1, x2, y2, b2.getX()-mw, b2.getY()-mh, b2.getX()+w, b2.getY()-mh);
	    	       
	    	        	if (Util.linesIntersect(x1, y1, x2, y2, b2.getX()+w, b2.getY()-mh, b2.getX()+w, b2.getY()+h))
	    	        		p2 = Util.getLineLineIntersection(x1, y1, x2, y2, b2.getX()+w, b2.getY()-mh, b2.getX()+w+10, b2.getY()+h);
	    	        	
	    	        	else if (Util.linesIntersect(x1, y1, x2, y2, b2.getX()+w, b2.getY()+h, b2.getX()-mw, b2.getY()+h))
	    	        		p2 = Util.getLineLineIntersection(x1, y1, x2, y2, b2.getX()+w, b2.getY()+h, b2.getX()-mw, b2.getY()+h);
	    	        	
	    	        	if (Util.linesIntersect(x1, y1, x2, y2, b2.getX()-mw, b2.getY()+h, b2.getX()-mw, b2.getY()-mh))
	    	        		p2 = Util.getLineLineIntersection(x1, y1, x2, y2, b2.getX()-mw, b2.getY()+h, b2.getX()-mw, b2.getY()-mh);


	    	          	if (p1 == null || p2 ==null)
	    	          		return;
	    	          	
	    	          	Util.drawArrow(g,(int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY(), linksDouble.get(i).booleanValue(), 10); 
	    	        
	    	          	g.drawImage(icon_cross,x1+(x2-x1)/2-5, y1 + (y2-y1)/2-5, 10, 10,null);
	    	        	
	    	        }
	    	        
	    	        //draw little arrow icons that user can drag below the viewer containers
	    	        for (int i=0; i<viewerWindows.size(); i++)
	    	        {
	    	        	int x = viewerWindows.get(i).getX()+60;
	    	        	int y = viewerWindows.get(i).getY()+30;
	    	        	
	    	        	g.drawImage(icon_singlearrow,x, y, 10, 15,null);
	    	        	g.drawImage(icon_doublearrow,x+20, y, 10, 15,null);
	    	        }
	    	        
	    	        //an arrow is currently being drawn; we draw it; we again have to find intersections; we also check if the endpoint of the arrow intersects a viewer and then mark that viewer container with a rectangle over it so that the user knows that viwer
	    	        // is a valid endpoint for their arrow
	    	        if (currentArrow != null)	
	    	        {
	    	        	
	    	        	int x1 = (int)currentArrow.getP1().getX();
	    	        	int y1 = (int)currentArrow.getP1().getY();
	    	        	int x2 = (int)currentArrow.getP2().getX();
	    	        	int y2 = (int)currentArrow.getP2().getY();
	    	        	
	    	        	Rectangle2D b1 = viewerWindows.get(currentArrowSource).getBounds();
	    	        	
	    	        	//check for intersection with the rectangle bounds
	    	        	Point2D p1 = null;
	    	        	
	    	        	if (Util.linesIntersect(x1, y1, x2, y2, b1.getX()-mw, b1.getY()-mh, b1.getX()+w, b1.getY()-mh))
	    	             		p1 = Util.getLineLineIntersection(x1, y1, x2, y2, b1.getX()-mw, b1.getY()-mh, b1.getX()+w, b1.getY()-mh);
	    	        	
	    	        	else if (Util.linesIntersect(x1, y1, x2, y2, b1.getX()+w, b1.getY()-mh, b1.getX()+w, b1.getY()+h))
	    	        		p1 = Util.getLineLineIntersection(x1, y1, x2, y2, b1.getX()+w, b1.getY()-mh, b1.getX()+w, b1.getY()+h);
	    	        		    	        	
	    	        	else if (Util.linesIntersect(x1, y1, x2, y2, b1.getX()+w, b1.getY()+h, b1.getX()-mw, b1.getY()+h))	    	        	
	    	        		p1 = Util.getLineLineIntersection(x1, y1, x2, y2, b1.getX()+w, b1.getY()+h, b1.getX()-mw, b1.getY()+h);
	    	        	    	        	
	    	        	else if (Util.linesIntersect(x1, y1, x2, y2, b1.getX()-mw, b1.getY()+h, b1.getX()-mw, b1.getY()-mh))
	    	        		p1 = Util.getLineLineIntersection(x1, y1, x2, y2, b1.getX()-mw, b1.getY()+h, b1.getX()-mw, b1.getY()-mh);
	    	        	
	    	        	Point2D p2 = null;	    	        	
	    	        	if (currentArrowDest != -1)
	    	        	{
		    	        	Rectangle2D b2 = viewerWindows.get(currentArrowDest).getBounds();
		    	        	
		 	        		w = 165;
		 	        		h = 35;
		 	        		mw = 5;
		 	        		mh = 5;
		    	        			    	        	
		    	          	if (Util.linesIntersect(x1, y1, x2, y2, b2.getX()-mw, b2.getY()-mh, b2.getX()+w, b2.getY()-mh))
		    	        		p2 = Util.getLineLineIntersection(x1, y1, x2, y2, b2.getX()-mw, b2.getY()-mh, b2.getX()+w, b2.getY()-mh);
		    	       
		    	        	if (Util.linesIntersect(x1, y1, x2, y2, b2.getX()+w, b2.getY()-mh, b2.getX()+w, b2.getY()+h))
		    	        		p2 = Util.getLineLineIntersection(x1, y1, x2, y2, b2.getX()+w, b2.getY()-mh, b2.getX()+w+10, b2.getY()+h);
		    	        	
		    	        	else if (Util.linesIntersect(x1, y1, x2, y2, b2.getX()+w, b2.getY()+h, b2.getX()-mw, b2.getY()+h))
		    	        		p2 = Util.getLineLineIntersection(x1, y1, x2, y2, b2.getX()+w, b2.getY()+h, b2.getX()-mw, b2.getY()+h);
		    	        	
		    	        	if (Util.linesIntersect(x1, y1, x2, y2, b2.getX()-mw, b2.getY()+h, b2.getX()-mw, b2.getY()-mh))
		    	        		p2 = Util.getLineLineIntersection(x1, y1, x2, y2, b2.getX()-mw, b2.getY()+h, b2.getX()-mw, b2.getY()-mh);
		    	        	
		    	        	g.drawRect((int)b2.getX()-3, (int)b2.getY()-3, (int)b2.getWidth()+6, (int)b2.getHeight()+6);
		    	        	//g.drawImage(icon_cross,(int)b2.getX()-3, (int)b2.getY()-3, (int)b2.getWidth()+6, (int)b2.getHeight()+6, null);
	    	        	}
		    	        
	    	        	if (p1 != null && p2 != null)
	    	        		Util.drawArrow(g, (int)p1.getX(),(int)p1.getY(), (int)p2.getX(),(int)p2.getY(), currentArrowDouble, 10);
	    	        	else if (p1 != null)
	    	          		Util.drawArrow(g, (int)p1.getX(),(int)p1.getY(), x2, y2, currentArrowDouble, 10);
	    	        	else
	    	        	  Util.drawArrow(g, x1,y1,x2,y2, currentArrowDouble, 10);
	    	        }
	    	        
	    	    }
 
	     };
	     
	     viewerArea.setBackground(Color.WHITE);
	     
	     viewerArea.addMouseListener(new MouseListener()
	     {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				 for (int i=0; i<links1.size(); i++)
				 {
	    	        	int i1 = links1.get(i).intValue();
	    	        	int i2 = links2.get(i).intValue();
	    	        	
	    	        	int x1 = viewerWindows.get(i1).getBounds().x + 80;
	    	        	int y1 = viewerWindows.get(i1).getBounds().y + 15;
	    	        	int x2 = viewerWindows.get(i2).getBounds().x + 80;
	    	        	int y2 = viewerWindows.get(i2).getBounds().y + 15;
	    	        	
	    	        	Rectangle r = new Rectangle(x1+(x2-x1)/2-5, y1 + (y2-y1)/2-5, 10, 10);
	    	        	
	    	        	if (r.contains(new Point(e.getX(),e.getY())))
	    	        	{
	    	        		links1.remove(i);
	    	        		links2.remove(i);
	    	        		linksDouble.remove(i);
	    	        		break;
	    	        	}
				 }				
				
			}
			
			
			@Override
			public void mouseEntered(MouseEvent arg0) {	}
			
			@Override
			public void mouseExited(MouseEvent arg0) { }
			
			@Override
			//if a mouse press happens on of the little link-drawing icons then link drawing is initiated with that specific viewer as source
			public void mousePressed(MouseEvent e) {
				//check for press on an arrow
				 for (int i=0; i<viewerWindows.size(); i++)
	    	        {
	    	        	int x = viewerWindows.get(i).getX()+60;
	    	        	int y = viewerWindows.get(i).getY()+30;
	    	        	
	    	        	Rectangle r1 = new Rectangle(x,y,10,15);
	    	        	Rectangle r2 = new Rectangle(x+20,y,10,15);
	    	        	
	    	        	
	    	        	if (r1.contains(new Point(e.getX(),e.getY())))
	    	        	{
	    	              		currentArrow = new Line2D.Double((double)viewerWindows.get(i).getX()+80,(double)viewerWindows.get(i).getY()+15,(double)viewerWindows.get(i).getX()+80,(double)viewerWindows.get(i).getY()+15);
	    	              		currentArrowSource = i;
	    	        	}
	    	        	else if (r2.contains(new Point(e.getX(),e.getY())))
	    	        	{
	    	        		currentArrow = new Line2D.Double((double)viewerWindows.get(i).getX()+80,(double)viewerWindows.get(i).getY()+15,(double)viewerWindows.get(i).getX()+80,(double)viewerWindows.get(i).getY()+15);
	    	        		currentArrowDouble = true;
	    	        		currentArrowSource = i;
	    	        	}	    	        	
	    	        }

			}

			//if we are in link drawing mode and a mouse released happens over a certain viewer then if that links doesn't already exist it will be added to lists of links.
			//in any case, link drawing is disabled
			public void mouseReleased(MouseEvent e) {
				
				if (currentArrowDest !=  -1 && currentArrowSource != -1)
				{
					//make sure there isn't a link there yet
					boolean alreadyIn = false;
					for (int i=0; i<links1.size(); i++)
					{
						if ((links1.get(i).intValue() == currentArrowDest && links2.get(i).intValue() == currentArrowSource) ||
								(links2.get(i).intValue() == currentArrowDest && links1.get(i).intValue() == currentArrowSource))
						{
							alreadyIn = true;
							break;
						}
					}
					if (!alreadyIn)
					{
						links1.add(new Integer(currentArrowSource));
						links2.add(new Integer(currentArrowDest));
						linksDouble.add(new Boolean(currentArrowDouble));
					}
				}
				
				currentArrow = null;
				currentArrowDouble = false;
				currentArrowSource = -1;
				currentArrowDest = -1;
			}
	    	 
	     });
	     
	     viewerArea.addMouseMotionListener(new MouseMotionListener(){

			@Override
			//we might be drawing an arrow; if we are, we update the endpoints of the arrow; also, if the endpoint is over a viewer then we mark it in currentArrowDest so that we render a rectangle of it to mark it as a potential destination;
			public void mouseDragged(MouseEvent e) {
				if (currentArrow == null)
					return;
				
				currentArrow = new Line2D.Double(currentArrow.getX1(),currentArrow.getY1(), (double)e.getX(), (double)e.getY());
				
				//find a potential destiation (the end of the arrow is in one of the viewers)
				currentArrowDest = -1;
				 for (int i=0; i<viewerWindows.size(); i++)
	    	     {
	    	        	Rectangle r1 = new Rectangle(viewerWindows.get(i).getBounds());	    	        		    	        	
	    	        	
	    	        	if (r1.contains(new Point(e.getX(),e.getY())))
	    	        	{
	    	              		currentArrowDest = i;
	    	              		break;
	    	        	}	    	        	
	    	        }				
			}

			@Override
			public void mouseMoved(MouseEvent arg0) {}
	    	 
	     });	     
	     //all done with the viewing area	

	    
	     allPanel.add(viewerArea,BorderLayout.CENTER);	   
	       
	      JButton newViewer = new JButton();	    
	      newViewer.setIcon(newViewerIcon);
	      newViewer.setBounds(50,10,25,25);	 
	      newViewer.setToolTipText("Create a new data viewer");
	      viewerArea.add(newViewer);
	      newViewer.setVisible(true);
	    
		  newViewer.addActionListener(new ActionListener() {	    	   
		    	   public void actionPerformed(ActionEvent e) {
		    		   createNewViewer();			    		        
				      }			      
		       });    
	    
	    
	       JToggleButton linkButton = new JToggleButton();	      
	       linkButton.setIcon(linksIcon);
	       linkButton.setBounds(80,10,50,25);	     
		   viewerArea.add(linkButton);
		   linkButton.setVisible(true);
		   linkButton.setToolTipText("Link your viewers to each other");
		   
		   linkButton.addActionListener(new ActionListener() {	
			   
			   //if the links button is on, the viewer containers show up as small icons; once the button is off they are restored to their original size; sizes thus need to be stored and restored
		    	   public void actionPerformed(ActionEvent e) {
		    		   
		    		   if (!showingLinks)
		    		   {		    		   
			    		   viewerBounds.clear();			    	       	    	        
			    		   for (int i=0; i<viewers.size(); i++)
			    		   {
			    			   Rectangle b = viewerWindows.get(i).getBounds();
			    			   viewerBounds.add(b);
			    			   viewerWindows.get(i).setBounds(b.x,b.y,160,30);		    			   
			    		   }		    		   
			    		   showingLinks = true;
			    		   viewerArea.repaint();
		    		   }
		    		   else
		    		   {
			    		   for (int i=0; i<viewers.size(); i++)
			    			   viewerWindows.get(i).setBounds(viewerBounds.get(i));
			    
			    		   showingLinks = false;
			    		   viewerArea.repaint();
		    		   }
				      }			      
		       });   
	       
		  
	       
	       JButton newData = new JButton();		       
	       newData.setIcon(newDataIcon);
	       newData.setBounds(20,10,25,25);	     
		   viewerArea.add(newData);
		   newData.setVisible(true);  
		   newData.setToolTipText("Create a new data source");
	     
	       newData.addActionListener(new ActionListener() {	    	   
	    	   public void actionPerformed(ActionEvent e) {
	    		   createNewData();			    		        
			      }			      
	       });	
	       
	       
	       JButton helpButton = new JButton();		       
	       helpButton.setIcon(helpIcon);
	       helpButton.setBounds(135,10,25,25);	     
		   viewerArea.add(helpButton);
		   helpButton.setVisible(true);   
		   helpButton.setToolTipText("Help. Not yet implemented.");
	     
		   helpButton.addActionListener(new ActionListener() {	    	   
	    	   public void actionPerformed(ActionEvent e) {
	    		   createNewData();			    		        
			      }			      
	       });	
	       
	       allPanel.revalidate();       

	       
	       //finally a timer is creteated to redraw the entire viewer area; this is done for the links dragging basically
	       timer = new Timer(50, new ActionListener()
	       {
	    	   public void actionPerformed(ActionEvent arg0) {
	    		   viewerArea.repaint();				
	    		   }	    	   
	       });
	       timer.start();	                 
	}
	
	
	
	/**
	 * Opens a dialog box where the user can create a new viewer: specify the type; select its datasources, name it, add it to Environment
	 */
	public void createNewViewer()
	{
		//open a dialog box; create a viewer
		ViewerCreator vc = new ViewerCreator(this, "viewer" + (autoViewerName++));
		vc.setVisible(true);
		if (vc.createdViewer != null)
		{
			Viewer v = vc.createdViewer;
			v.setPropertyManagerGroup(this);
			addViewer(v);			
		}
	}
	
	/**
	 * Opens a dialog box where the user can create a new datasource: specify the type, name it, add it to Environment
	 */
	public void createNewData()
	{
		DataCreator dc = new DataCreator(this, "Data"+(defaultDataNameCounter++));
		dc.setVisible(true);
		
		if (dc.createdDatasource != null)
			addDataSource(dc.createdDatasource);			
	}
	
	public JPanel getMenuPanel()
	{
		return menuPanel;
	}
	
	/**
	 * @return All DataSource items available in the Environment
	 */
	public Vector<DataSource> getDataSources()
	{
		return dataSources;
	}
	
	/**
	 * @return All Viewer items available in the Environment
	 */
	public Vector<Viewer> getViewers()
	{
		return viewers;
	}	

	/**
	 * Register a new Viewer factory with the system; users will then be able to create this type of Viewers in the system
	 * @param vf
	 */
	public void registerViewerFactory(ViewerFactory vf)
	{
		viewerFactories.add(vf);
	}
	
	/**
	 * 
	 * @return All ViewerFactory items available in the Environment
	 */
	public Vector<ViewerFactory> getViewerFactories()
	{
		return viewerFactories;
	}
	
	/**
	 * Register a new DataSourceFactory with the system; users will then be able to create this type of DataSources in the system
	 * @param vf
	 */	
	public void registerDataSourceFactory(DataSourceFactory df)
	{
		dataFactories.add(df);
	}	
	
	/**
	 * 
	 * @return All DataSourceFactory items available in the Environment
	 */
	public Vector<DataSourceFactory> getDataFactories()
	{
		return dataFactories;
	}
	
	/**
	 * Adds a viewer to the environment; this is generally handeld internally by Environment as a result of the createViewer function;
	 * @param v
	 */
	public void addViewer(Viewer v)
	{
		final ViewerContainer vc = new ViewerContainer(v,this,1000,700);
		
		if (!offline)
		{
			ViewerWindow vw = new ViewerWindow(vc);
			vw.setFrameIcon(viewerIcon);
			viewerArea.add(vw);
			int offsetx = 250+viewers.size()*10;
			int offsety = 100+viewers.size()*10;
			
			vw.setBounds(offsetx,offsety,700,500);	

			viewerWindows.add(vw);
			setFocusedViewer(viewers.size()-1);	
		}
		viewers.add(v);
		viewerContainers.add(vc);	
			
	}
	
	/**
	 * A user selected a different viewer to be in the foreground. Its properties are displayed in the property slot.
	 * @param v
	 */
	public void setFocusedViewer(Viewer v)
	{
		int index = viewers.indexOf(v);
		if (index >= 0)
			setFocusedViewer(index);
			
	}
	
	public void setFocusedViewer(int which)
	{		
		if (which < 0 || which >= viewers.size())
		{
					try {
						if (viewerArea.getSelectedFrame() != null)
							viewerArea.getSelectedFrame().setSelected(false);
					} catch (PropertyVetoException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
			return;
		}		
		viewerWindows.get(which).setBorder(BorderFactory.createLineBorder(new Color(150,150,200),2));
		viewerArea.setComponentZOrder(viewerWindows.get(which), 0);
		
		try {
			viewerWindows.get(which).setSelected(true);
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		viewerArea.repaint();
		
	}
	
	/**
	 * User deleted a viewer
	 * @param v
	 */
	public void deleteViewer(Viewer v)
	{
		int index = viewers.indexOf(v);
		
		if (index < 0) return;
		
		viewers.remove(index);
		
		if (!offline)
			viewerWindows.get(index).dispose();
	}
	
	public void addDataSource(DataSource d)
	{
		dataSources.add(d);
		
		final JInternalFrame dataFrame = new JInternalFrame(d.getName());
		dataFrame.setFrameIcon(dataIcon);
		dataFrame.setBorder(BorderFactory.createLineBorder(new Color(180,180,250)));
		dataFrame.setBounds(newDataX,newDataY,200,400);
		dataFrame.setVisible(true);
		dataFrame.setClosable(true);
		dataFrame.setIconifiable(true);
		viewerArea.add(dataFrame);	
		
		d.taskObserverDialog = new TaskObserverDialog(dataFrame, true);
		
		
		newDataY += 30;
		newDataX += 10;
		
		viewerArea.setComponentZOrder(dataFrame, 0);
		
		PropertyManagerViewer pmv = new PropertyManagerViewer(d)
		{
			@Override
			public void propertyAdded(PropertyManager pm, Property p){
				super.propertyAdded(pm, p);			
				dataFrame.pack();	

			}

			@Override
			public void propertyRemoved(PropertyManager pm, Property p) {
				super.propertyRemoved(pm, p);			
				dataFrame.pack();	
			}
		};
		
		dataFrame.add(pmv);		
		dataFrame.pack();
		
		final DataSource fd = d;		
		
		
		
		dataFrame.addInternalFrameListener(new InternalFrameListener()
		{
			public void internalFrameActivated(InternalFrameEvent arg0) {
			}
			public void internalFrameClosed(InternalFrameEvent arg0) {
				deleteDataSource(fd);
			}
			public void internalFrameClosing(InternalFrameEvent arg0) {
			}
			public void internalFrameDeactivated(InternalFrameEvent arg0) {
			}
			public void internalFrameDeiconified(InternalFrameEvent arg0) {
			}
			public void internalFrameIconified(InternalFrameEvent arg0) {
			}
			public void internalFrameOpened(InternalFrameEvent arg0) {
			}
              
        } ); 
	
		
	}
	
	public void deleteDataSource(DataSource d)
	{
		int index = dataSources.indexOf(d);
		
		if (index < 0 || index >= dataSources.size())
			return;
		
		dataSources.remove(index);
	}


	@Override
	/**
	 * Overide from PropertyManagerGroup: based on the links network we propagate the the property change from the originating viewer
	 */
	public <T extends PropertyType> void broadcast(PropertyManager p, Property prop, T newvalue) {
		
		Vector<PropertyManager> receivers = new Vector<PropertyManager>();

		for (int j=0; j<links1.size(); j++)
		{				
			int i1 = links1.get(j).intValue();
			int i2 = links2.get(j).intValue();
	        	
			if (viewers.get(i1) == p)
			{
				if (receivers.indexOf(viewers.get(i2)) < 0)
					receivers.add(viewers.get(i2));
			}
			else if (viewers.get(i2) == p && linksDouble.get(j).booleanValue())
			{
					if (receivers.indexOf(viewers.get(i1)) < 0)
						receivers.add(viewers.get(i1));
			}
		}
		
		for (int i=0; i<receivers.size(); i++)
			receivers.get(i).receivePropertyBroadcast(p, prop.getName(), newvalue);
	}
	
	public Vector<ViewerContainer> getViewerContainers()
	{
		return viewerContainers;
	}
	
	public void registerNewType(PropertyType c, PropertyWidgetFactory pwf) {	
		PropertyManager.registerType(c);
		PropertyManagerViewer.registerNewType(c, pwf);	
	}	
	
	

}
