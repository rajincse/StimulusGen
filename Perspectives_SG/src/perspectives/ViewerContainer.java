package perspectives;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SwingWorker;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

/**
 * This is a window class for Viewers. It will be able to accommodate any of the three main types of Viewers: 2D, 3D, GUI. It will call their rendering, simulation, and interactivity functions (if appropriate), it will implement double buffering for them, and it can be added to the viewer area
 * of the Environment. ViewerContainers automatically implementing panning and zooming for 2D viewers via 2dtransforms applied to the Graphics2D objects passed onto the Viewer2D's render function.
 * @author rdjianu
 *
 */
public class ViewerContainer implements Runnable{
	
	//used to implement double buffering for 2d and 3d viewers
	BufferedImage finalImage = null;
	private BufferedImage tempImage = null;
	 
	//used to do rendering and simulation in the background and display results only when done so that the window remains responsive in between
	MySwingWorker worker;
	boolean working = false;//indicates whether the thread is still working in the background
	 
	Viewer viewer;
	private Viewer2D v2d;
	
	//private Viewer3D v3d;
	
	//apply to 2d viewers
	public double zoom = 1;
	public double translatex = 0;
	public double translatey = 0;
	
	//keeping track of dragging; 
	public boolean rightButtonDown = false;
	public int dragPrevX = 0;
	public int dragPrevY = 0;
	
	public int zoomOriginX = 0;
	public int zoomOriginY = 0;
	
	public AffineTransform transform = AffineTransform.getTranslateInstance(0, 0);
	
	private long lastMouseMove = new Date().getTime();
	
	//a pointer to the parent Environment class (needed for instance to delete the viewer from the Environment if the user activates the 'X')
	final Environment env;
	
	int width, height;
	
	
	

	//swingworker class doing simulation and rendering in the background for some classes of viewers
	class MySwingWorker extends SwingWorker<BufferedImage, Void>
	{
		public BufferedImage image; //we will draw the current state into image; in the done() function, which gets called at the end, we implement double buffering: the old image is saved, the current image is set to image.
		
		public ViewerContainer thisContainer;
		
		public MySwingWorker(BufferedImage bi, ViewerContainer vc)
		{			
			image = bi;			
			working = true;
			thisContainer = vc;
		}		
		 
 	    public BufferedImage doInBackground() {
 	    	
 	    	
 	    	//call the 2D viewer's set simulation function before rendering;
 	    	synchronized(v2d)
 	    	{
 	    	v2d.simulate();
 	    	
 	    	//developers can use this functuon of 2d viwers to skip long renderings that are not actually updating the image
 	    	if (v2d.skipRendering())
 	    		return finalImage;
 	    	
 	    	//this if is for the first time the two buffers are initialized and for when the user changes the size of the container; in this case a new image of the good size needs to be created.
 	    	if (image == null || image.getWidth() != thisContainer.getWidth() || image.getHeight() != thisContainer.getHeight())
 	    		
 	    		if (thisContainer.getWidth() < 0 || thisContainer.getHeight() < 0)
 	    			return finalImage;
 	    		
 	    		//image = thisPanel.getGraphicsConfiguration().createCompatibleImage(thisPanel.getWidth(),thisPanel.getHeight());
 	    		image = new BufferedImage(thisContainer.getWidth(), thisContainer.getHeight(),BufferedImage.TYPE_INT_ARGB);
 	    		
 	    	 	  	    	    	
 	    		Graphics2D gc = image.createGraphics();
 	    		
 	    		gc.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
 	    		
	            gc.setColor(v2d.backgroundColor());
	            gc.fillRect(0, 0, thisContainer.getWidth(), thisContainer.getHeight()); // fill in background
	            
	            if (!v2d.antialiasingSet)
	            {
	            	if (v2d.antialiasing)
	            	{	            		
	            	    gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	            	                         RenderingHints.VALUE_ANTIALIAS_ON);

	            	    gc.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
	            	                         RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	            	}
	            	else
	            	{
	            	    gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	            	                         RenderingHints.VALUE_ANTIALIAS_OFF);

	            	    gc.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
	            	                         RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
	            	}
	            }
	            
	            zoomOriginX = this.thisContainer.getWidth()/2;
	            zoomOriginY = this.thisContainer.getHeight()/2;
	            
	            //sets the proper transformation for the Graphics context that will passed into the rendering function	            
	     
	            
	            gc.setTransform(AffineTransform.getTranslateInstance(zoomOriginX, zoomOriginY));	          
	            gc.transform(AffineTransform.getScaleInstance(zoom,zoom));		           
	            gc.transform(AffineTransform.getTranslateInstance(translatex-zoomOriginX, translatey-zoomOriginY));
	            
	            
	            transform = gc.getTransform();
 	    		    		
 	    			
	            v2d.render(gc);
	            gc.setColor(Color.black);
	            
	           
	          
	            
	            gc.setTransform(AffineTransform.getTranslateInstance(0, 0));
	        	
	            if ((new Date().getTime()- lastMouseMove) > v2d.getTooltipDelay())
	            	if (v2d.getToolTipEnabled() && v2d.getToolTipText().length() > 0)
	            		v2d.renderTooltip(gc);
 	    	}
	            
 	        return image;
 	    }

 	    @Override
 	    public void done() {
 	    	try {
 	    		//when done "switch" the buffers
 	    		tempImage = finalImage;
 	    		
				finalImage = get();
				
				working = false;
				
				} catch (Exception e) {
					e.printStackTrace();
				}
 	    	
 	    }
		
	}
	
	Thread thread = null;
	public ViewerContainer(Viewer v, Environment env, int width, int height)
	{		
		this.env = env;		
		final Environment envtmp = env;
		
		this.width = width;
		this.height = height;
		
		viewer = v;
		
		if (v.getViewerType() == "Viewer2D")
		{				
			//in case of 2d viewers we want MySwingWorkers created regularly to update the rendering and the simulation; this is why we create and start a thread
			v2d = (Viewer2D)v;
			v2d.setContainer(this);			
			zoom = v2d.getDefaultZoom();
			Thread t = new Thread(this);
			this.thread = t;
			t.start();		
	
		}
		else if (v.getViewerType() == "Viewer3D")
		{
			
		}
	}
	
	//this esentially calls the rendering/simulation functions of the viewers by creating background SwingWorker threads; these in turn will do the work;
	//we make sure we don't create a thread if one is already working and we don't try to do anything more often than 10ms.
	public void run() {
		
		long lastTime = new Date().getTime();
		
		while (true)
		{
			long ct = new Date().getTime();
			if (ct-lastTime < 50)
				try {
					//thread.slee(10-ct+lastTime);
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			lastTime = new Date().getTime();
			
			//System.out.println(lastTime);
			
			if (working /*|| drawArea == null || drawArea.getGraphicsConfiguration() == null*/)
				continue;
			
			worker = new MySwingWorker(tempImage,this);
		   	worker.execute();
			
		}
		
		
	}
	
	public void setWidth(int w)
	{
		width = w;
	}
	public void setHeight(int h)
	{
		height = h;
	}
	public int getWidth()
	{
		return width;
	}
	public int getHeight()
	{
		return height;
	}

		

	//for 2D viewers we add automatic zooming and panning; this can happen using the arrow keys and +,- keys; the key strokes are also sent to the viewer as interaction events
	public void keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() == e.VK_UP)
			translatey++;
		else if (e.getKeyCode() == e.VK_DOWN)
			translatey--;
		else if (e.getKeyCode() == e.VK_LEFT)
			translatex--;
		else if (e.getKeyCode() == e.VK_RIGHT)
			translatex++;
		else if (e.getKeyCode() == e.VK_PLUS || e.getKeyCode() == 61)
			zoom = zoom + 0.1;
		else if (e.getKeyCode() == e.VK_MINUS)
		{
			zoom = zoom - 0.1;
			if (zoom < 0.1)
				zoom = 0.1;
		}
		
		v2d.zoom = zoom;
		
		v2d.keyPressed(e.getKeyCode());
	}

	public void keyReleased(KeyEvent e) {
		v2d.keyReleased(e.getKeyCode());				
	}
	public void keyTyped(KeyEvent arg0) {
	}	
	
	

	//we add listeners both to send those events to the 2D viewer but also to implement zooming an panning; there's a 25pixel offset on the y-axes because of the title bar of ViewerContainers; for zooming and panning we use
	//the raw coordinates; when passing the coordinates to the viewer they need to be transformed so they are in the Viewer's space, hence the operations based on translate and zoom.
	
	public void mouseClicked(int ex, int ey, int button) {
	}
	public void mouseEntered(int ex, int ey) {
	}
	public void mouseExited(int ex, int ey) {
	}
	public void mousePressed(int ex, int ey, int button) {
		
		try{
			Point2D.Double tp = new Point2D.Double();
			transform.inverseTransform(new Point2D.Double(ex,ey), tp);
			int x = (int)tp.x;
			int y = (int)tp.y;
			
			v2d.mousepressed(x,y, button);
			
			dragPrevX = ex;
			dragPrevY = ey;
			
			if (button == MouseEvent.BUTTON3)
			{
				rightButtonDown = true;
				zoomOriginX = dragPrevX;
				zoomOriginY = dragPrevY;
			}
		}
		catch(Exception ee)
		{
			
		}
	}
	public void mouseReleased(int ex, int ey, int button)
	{
		try{
			Point2D.Double tp = new Point2D.Double();
			transform.inverseTransform(new Point2D.Double(ex, ey), tp);
			int x = (int)tp.x;
			int y = (int)tp.y;
		
		
			v2d.mousereleased(x,y, button);
		
			if (button == MouseEvent.BUTTON3)
				rightButtonDown = false;

		}
		catch(Exception ee)
		{
			
		}
	}	
	
	
	
	
	public void mouseDragged(int ex, int ey) {
		
		v2d.setToolTipCoordinates(ex,ey);
		
		try{
			Point2D.Double tp = new Point2D.Double();
			transform.inverseTransform(new Point2D.Double(ex, ey), tp);
			int x = (int)tp.x;
			int y = (int)tp.y;
			
			transform.inverseTransform(new Point2D.Double(dragPrevX, dragPrevY), tp);
			int px = (int)tp.x;
			int py = (int)tp.y;
		
		//if (!v2d.mousedragged(x,y, (int)((dragPrevX-translatex)/zoom), (int)((dragPrevY-translatey)/zoom)))
		if (!v2d.mousedragged(x,y, px, py))
		{
			if (rightButtonDown) //zoom
			{
				if (Math.abs(ex-dragPrevX) > Math.abs(ey-0 - dragPrevY))
					zoom *= (1+(ex-dragPrevX)/100.);
				else
					zoom *= (1+(ey-0-dragPrevY)/100.);
				if (zoom <0.01)
					zoom = 0.01;
				v2d.zoom = zoom;
			}
			else
			{
				translatex += (ex-dragPrevX)/zoom;
				translatey += (ey-dragPrevY)/zoom;
			}
		}
		dragPrevX = ex;
		dragPrevY = ey;	
		}
		catch(Exception ee)
		{
			
			
		}
	}
	public void mouseMoved(int ex, int ey)
	{
		v2d.setToolTipCoordinates(ex,ey);
		
		try{
			Point2D.Double tp = new Point2D.Double();
			transform.inverseTransform(new Point2D.Double(ex,ey), tp);
			int x = (int)tp.x;
			int y = (int)tp.y;
		lastMouseMove = new Date().getTime();
		//int x = (int)((e.getX()-translatex)/zoom);
		//int y = (int)((e.getY()-0-translatey)/zoom);
		v2d.mousemoved(x,y);
		}
		catch(Exception ee)
		{
			
		}
	}	
	
	public BufferedImage getImage()
	{
		return finalImage;
	}


}
