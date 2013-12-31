package servlets;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.*;
import java.util.Date;
import java.util.Hashtable;

import com.keypoint.*;

import javax.imageio.ImageIO;
import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import multidimensional.PlanarProjectionViewerFactory;
import Graph.BubbleSetGraphFactory;
import Graph.Graph;
import Graph.GraphData;
import Graph.GraphDataFactory;
import Graph.GraphViewer;
import Graph.GraphViewerFactory;
import Graph.LineSetGraphFactory;
import Graph.ViewFocusGraphViewerFactory;
import ParallelCoord.ParallelCoordViewer;
import ParallelCoord.ParallelCoordinateViewerFactory;
import data.TableData;
import data.TableDataFactory;
import data.TableDistances;

import perspectives.Environment;
import perspectives.Property;
import perspectives.PropertyChangeListener;
import perspectives.PropertyManager;
import perspectives.PropertyType;
import perspectives.Viewer;
import perspectives.DefaultProperties.*;

import tree.HierarchicalClusteringViewerFactory;
import tree.RadialBrainConnectivityViewerFactory;
import util.EyeCatcherFactory;
import util.Hotspots;
import util.HotspotsFactory;
import util.ImageTilerFactory;

public class VizOnlineServlet extends HttpServlet{

	BufferedImage[] screen = null;
	BufferedImage[] splitImage = null;
	boolean[] compressed;
	private Object[] syncobj;
	int[] cnt;
	int[] step;
	
	
	int encoding = 2;
	
	PropertyChangeListener listener;
	
	String propertyCommands = "";
	Object pcsync = new Object();
	
	
	
    public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {
        Hashtable<VizOnlineServlet, Environment> envs = (Hashtable<VizOnlineServlet, Environment>)this.getServletContext().getAttribute("envs");
		Environment e = envs.get(this);   
        
        String cencoding = request.getParameter("changeEncoding");
        if (cencoding != null)
        {
        	encoding = Integer.parseInt(cencoding);
        	return;
        }
      
        String mType = request.getParameter("mousetype");
        String changeProp = request.getParameter("changeProp");
        String pollprops = request.getParameter("pollprops");

        if (pollprops != null)
        {
        	
             synchronized(pcsync)
             {
            	 response.setContentType("text/html");
            	 response.getWriter().write(propertyCommands);
             	System.out.println("command sent: " + propertyCommands);
             	
             	response.flushBuffer();
             	propertyCommands = "";
             	
             }
        }
        else if (changeProp != null)
        {
        	System.out.println("changerop");
        	 String name = request.getParameter("name");
        	 String value = request.getParameter("value");
 
        	 String type = e.getViewers().get(0).getProperty(name).getValue().typeName();
        	 e.getViewers().get(0).getProperty(name).setValue(e.getViewers().get(0).deserialize(type, value));   
        	 
        }
        else if (mType != null)
        {
        	 response.setContentType("text/html");
             response.setHeader("Cache-control", "no-cache, no-store");
             response.setHeader("Pragma", "no-cache");
             response.setHeader("Expires", "-1");
             
        	String cmd = request.getParameter("cmd");
        	String xs = request.getParameter("mx");
        	String ys = request.getParameter("my");

        	
        	int x = Integer.parseInt(xs);
        	int y = Integer.parseInt(ys);


            boolean leftMouse = false;
            if (mType.trim().equalsIgnoreCase("left"))         
                leftMouse = true;
                    
            
            
            if (cmd.equalsIgnoreCase("mouseUp")) {

                if (leftMouse) {
                	e.getViewerContainers().get(0).mouseReleased(x, y, MouseEvent.BUTTON1);
                } else {
                	e.getViewerContainers().get(0).mouseReleased(x, y, MouseEvent.BUTTON3);
                }
            } else if (cmd.equalsIgnoreCase("mouseDown")) {

                //System.out.println("MOUSE-DOWN");
                if (leftMouse) {
                	e.getViewerContainers().get(0).mousePressed(x, y, MouseEvent.BUTTON1);
                } else {
                	e.getViewerContainers().get(0).mousePressed(x, y, MouseEvent.BUTTON3);
                }

            } else if (cmd.equalsIgnoreCase("mouseMoved")) {
            
                e.getViewerContainers().get(0).mouseMoved(x, y);
               
                
            } else if (cmd.equalsIgnoreCase("mouseDragged")) {
               // System.out.println("MOUSE-DRAGGED");
                e.getViewerContainers().get(0).mouseDragged(x, y);
            }	
        }
        else{
        	 response.setContentType("image/png");
             response.setHeader("Cache-control", "no-cache, no-store");
             response.setHeader("Pragma", "no-cache");
             response.setHeader("Expires", "-1");
             
             
        	String tiles = request.getParameter("tile");
        	int t = Integer.parseInt(tiles);
        	//synchronized(syncobj[t])
        	//{
        		//System.out.println(t + "enter");
        	long t1 = (new Date()).getTime();
        	
        	        	
        	BufferedImage im;
        	
        	synchronized(splitImage)
        	{
        		
        		im = e.getViewerContainers().get(0).getImage();         		
        	}
        	
        	if (im == null)
        	{
        		im = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
        		ImageIO.write(im, "PNG", response.getOutputStream());
        		return;
        	}
        	
        	Graphics2D g = im.createGraphics();
        	g.setColor(Color.black);
        	g.drawRect(1, 1, im.getWidth()-2, im.getHeight()-2);
        	
	        int x1= im.getWidth()/2; int x2 = im.getWidth();
	        int y1 = im.getHeight()/2; int y2 = im.getHeight();
	        
	       
	        	        
	        	        if (t == 0)
	        	        {
	        	        	
		        	        splitImage[0] = new BufferedImage(im.getWidth()/2, im.getHeight()/2, BufferedImage.TYPE_INT_ARGB);
		        	        splitImage[0].createGraphics().drawImage(im, 0,0, x1, y1, 0,0,x1,y1, null);
	        	        }
	        	        
	        	        
	        	        if (t == 1)
	        	        {
	        	        	
		        	        splitImage[1] = new BufferedImage(im.getWidth()/2, im.getHeight()/2, BufferedImage.TYPE_INT_ARGB);
		        	        splitImage[1].createGraphics().drawImage(im, 0,0, x1, y1, x1,0,x2,y1, null);
	        	        } 
	        	        
	        	        if (t == 2)
	        	        {
	        	        	
		        	        splitImage[2] = new BufferedImage(im.getWidth()/2, im.getHeight()/2, BufferedImage.TYPE_INT_ARGB);
		        	        splitImage[2].createGraphics().drawImage(im, 0,0, x1, y1, 0,y1,x1,y2, null);
	        	        }
	        	        
	        	        if (t == 3)
	        	        {
	        	        	
		        	        splitImage[3] = new BufferedImage(im.getWidth()/2, im.getHeight()/2, BufferedImage.TYPE_INT_ARGB);
		        	        splitImage[3].createGraphics().drawImage(im, 0,0, x1, y1, x1,y1,x2,y2, null);
	        	        }
	        	        
	        	      
       	     /*   for (int i=0; i<999999; i++)
       	         for (int j=0; j<99; j++)
       	        	 
       	        		 {
       	        			 double v = Math.exp(i);
       	        			 v = Math.pow(v, 3.41);
       	        		 }
	        	*/
	        	    
	        
	        	this.sendImage(splitImage[t], response,t);
	        	
	        	long t2 = (new Date()).getTime();
	        	System.out.println(t + ":   time " + (t2-t1) + "; " + splitImage[t].getWidth() + " " +  splitImage[t].getHeight());
	        	
	          	
        }
       // }
    }

	@Override
	public void destroy() {
		System.out.println("servlet destroyed");
		super.destroy();
	}

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		System.out.println("servlet init");
		Hashtable<VizOnlineServlet, Environment> envs = (Hashtable<VizOnlineServlet, Environment>)this.getServletContext().getAttribute("envs");
		
		screen = new BufferedImage[6];
		compressed = new boolean[6];
		syncobj = new Object[6];
		cnt = new int[6];
		step = new int[6];
		
		splitImage = new BufferedImage[6];
		for (int i=0; i<6; i++)
		{
			splitImage[i] = null;
			screen[i] = null;
			compressed[i] = false;
			syncobj[i] = new Object();
			cnt[i] =0;
			step[i] = 0;
		}
		
		
		if (envs.get(this) == null)
		{
			listener = new PropertyChangeListener()
			{

				@Override
				public void propertyAdded(PropertyManager pm, Property p) {
	                synchronized(pcsync)
	                {
					if (propertyCommands.length() != 0) propertyCommands += ";";
					propertyCommands = propertyCommands + "addProperty,"+pm.getName()+","+p.getName()+","+ p.getValue().typeName() + "," + p.getValue();
	                }
					System.out.println(propertyCommands);
					
				}

				@Override
				public void propertyRemoved(PropertyManager pm, Property p) {
	                synchronized(pcsync)
	                {
					if (propertyCommands.length() != 0) propertyCommands += ";";
					propertyCommands = propertyCommands + "removeProperty,"+pm.getName()+","+p.getName()+","+p.getValue();
	                }
					
				}

				@Override
				public void propertyValueChanged(PropertyManager pm,
						Property p, PropertyType newValue) {
	                synchronized(pcsync)
	                {
					if (propertyCommands.length() != 0) propertyCommands += ";";
					propertyCommands = propertyCommands + "changeProperty,"+pm.getName()+","+p.getName()+","+p.getValue();
	                }
					
				}

				@Override
				public void propertyReadonlyChanged(PropertyManager pm,
						Property p, boolean newReadOnly) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void propertyVisibleChanged(PropertyManager pm,
						Property p, boolean newVisible) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void propertyPublicChanged(PropertyManager pm,
						Property p, boolean newPublic) {
					// TODO Auto-generated method stub
					
				}
				
			};
			
			Environment e = new Environment(true);			
			
			
			String path = (getServletContext().getRealPath("/WEB-INF/table_data_sample.txt"));
		    TableData tb = new TableData(path);
		    TableDistances table = new TableDistances();

		       
		        table.fromFile(path, "\t", true, true);  //Function to Get the Data from File

		        tb.setTable(table);

		       
		       // Viewer v = new ParallelCoordViewer("PC", tb);
		       // e.addViewer(v);
		        
		        GraphData gd  = new GraphData("d1");		       
		        
		        Graph g = new Graph(false);
		        g.fromEdgeList(new File((getServletContext().getRealPath("/WEB-INF/edge_list.txt"))));
		        gd.setGraph(g);
		        
		        Viewer v = new GraphViewer("graphvi", gd);
		        System.out.println(g.numberOfNodes());
		        OpenFilePropertyType opt = new OpenFilePropertyType();
		        opt.path = getServletContext().getRealPath("/WEB-INF/pos5.txt");
		        v.getProperty("Load Positions").setValue(opt);		        
		        e.addViewer(v);
		        
		        Property[] ps = v.getProperties();		       
		       
                synchronized(pcsync)
                {
			        for (int i=0; i<ps.length; i++)
			        {		        	
			        	if (i!=0) propertyCommands += ";";
			        	propertyCommands += "addProperty,"+v.getName()+","+ps[i].getName()+","+ps[i].getValue().typeName() + ",10";
			        }
                }
		        v.addPropertyChangeListener(listener);
		        
			envs.put(this, e);
		}
		Environment e = envs.get(this);
		super.init();
	}
	
	
	
	public void sendImage(BufferedImage capture, HttpServletResponse response, int t)
	{
		int frameCount = 50;
		
			
			
			/*try {				
				ImageIO.write(capture, "png", response.getOutputStream());	// convert to byte output stream
				return;

			} catch (IOException e) {		
				e.printStackTrace();
			}*/
			
			
			
		cnt[t]++;
		
		long t1 = new Date().getTime();
		
			
		
		BufferedImage capture2 = new BufferedImage(capture.getWidth(), capture.getHeight(), BufferedImage.TYPE_INT_ARGB);
		capture2.createGraphics().drawImage(capture,0,0,null);
			

		int changedPixels = 0;

		if (screen[t] != null)
		{			
			int[] cpixels = ((DataBufferInt) capture.getRaster().getDataBuffer()).getData();
			int[] spixels = ((DataBufferInt) screen[t].getRaster().getDataBuffer()).getData();
			
			int[] calpha = ((DataBufferInt) capture.getAlphaRaster().getDataBuffer()).getData();
			
			changedPixels = 0;			
			
			for (int i=0; i<cpixels.length; i+= 1)
			{
				int r1 = (cpixels[i])&0xFF;
				int g1 = (cpixels[i]>>8)&0xFF;
				int b1 = (cpixels[i]>>16)&0xFF;
				int r2 = (spixels[i])&0xFF;
				int g2 = (spixels[i]>>8)&0xFF;
				int b2 = (spixels[i]>>16)&0xFF;	
				
				if (!((Math.abs(r1-r2) < 10 && Math.abs(g1-g2) < 10 && Math.abs(b1-b2) < 10)))
					changedPixels++;
			}
			
						
				int fact = changedPixels/20000 + 1; // 1-5
				if (fact > 5) fact = 5;
				
				int coldif = 10;

				fact = 11 - fact; //1-5	
				
				//if (fact == 5) changedPixels=0;
				
				int badindex = -1;
				for (int i=0; i<cpixels.length; i+= 1)
				{	
					int r1 = (cpixels[i])&0xFF;
					int g1 = (cpixels[i]>>8)&0xFF;
					int b1 = (cpixels[i]>>16)&0xFF;
					int r2 = (spixels[i])&0xFF;
					int g2 = (spixels[i]>>8)&0xFF;
					int b2 = (spixels[i]>>16)&0xFF;	
					
					if (((Math.abs(r1-r2) < coldif && Math.abs(g1-g2) < coldif && Math.abs(b1-b2) < coldif)))
					{
						cpixels[i] = 0;
						calpha[i] = 0;
					}
					else
					{
						if ((i/7-step[t]) % 5 >= fact)
						{
							cpixels[i] = 0;
							calpha[i] = 0;
							badindex = i;
						}
						else
						{
							spixels[i] = cpixels[i];
						}
					}
				}
				
			//	System.out.println("step: " + step + "; fact:" + fact + "; badindex" + badindex );
				
				step[t] = (step[t] + fact)%5;				
			
				
			
			
		}
		else
		{
			screen[t] = new BufferedImage(capture.getWidth(), capture.getHeight(), BufferedImage.TYPE_INT_ARGB);
			screen[t].createGraphics().drawImage(capture, 0, 0, null);
		}

		if ((compressed[t] && changedPixels == 0) || (cnt[t] >= frameCount))
		{			
			capture = capture2;
			screen[t].createGraphics().drawImage(capture,0,0,null);
			compressed[t]  = false;
			cnt[t]=0;
		}
		else if (changedPixels == 0 && cnt[t] < frameCount)
			capture = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
		else if (changedPixels > 100000 || compressed[t])
		{
			System.out.println("compressing");
			BufferedImage capturecopy = new BufferedImage(capture.getWidth()/2, capture.getHeight()/2, BufferedImage.TYPE_INT_ARGB);
			capturecopy.createGraphics().drawImage(capture2, 0,0,capturecopy.getWidth(), capturecopy.getHeight(), 0,0,capture2.getWidth(), capture2.getHeight(),null);
					
			cnt[t] = 0;									
			capture = capturecopy;
			compressed[t] = true;
		}
		
		
		long t2 = new Date().getTime();		
		try {
			
			//ImageIO.write(capture, "png", new File((getServletContext().getRealPath("/WEB-INF/fim"+t2+"capture.png"))));
			
			PngEncoder p = new PngEncoder(capture, true);
			
			System.out.println(capture.getWidth() + " " + capture.getHeight());
			p.setFilter(PngEncoder.FILTER_NONE);
			
			p.setCompressionLevel(encoding);
			//System.out.println("-1-");
			byte[] bs = p.pngEncode(true);
			//System.out.println("-2-");
			response.getOutputStream().write(bs);
			//System.out.println("-3-");
			response.flushBuffer();
			//System.out.println("-4-");
			
			long t3 = new Date().getTime();
			
			//System.out.println("T1: " +  (t2-t1) + ";       T2: " + (t3-t2) + "      Econding:" + encoding);

		} catch (Exception e) {	
			System.out.println("-e-");
			e.printStackTrace();
			
		}
		
		if (cnt[t] >= frameCount) cnt[t] = 0;
		
	}
}