package util;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;

import javax.imageio.ImageIO;

import perspectives.DefaultProperties;
import perspectives.Property;
import perspectives.PropertyType;
import perspectives.Viewer2D;

import com.objectplanet.image.PngEncoder;

import de.ksquared.system.mouse.GlobalMouseListener;
import de.ksquared.system.mouse.MouseAdapter;
import de.ksquared.system.mouse.MouseEvent;


class GazeServer implements Runnable
{
	public static final int PORT = 9876;
	EyeCatcher fgv;
	
	Socket socket;
	private ServerSocket serverSocket;
	public GazeServer(EyeCatcher fgv)
	{
		this.fgv = fgv;
		
		
		this.serverSocket = null;
		    try {
		       serverSocket = new ServerSocket(GazeServer.PORT);}
		    catch (IOException se) 
		    {
		       System.err.println("Can not start listening on port " + GazeServer.PORT);
		       se.printStackTrace();
		       System.exit(-1);
		    }
		    
		 
	}
	
	  
	    private BufferedReader mIn;
	    private String message;
	    private String decoded = null;


	   

	@Override
	   public void run() {

		if(this.serverSocket != null)
		{
	       try  
	       {
	           socket = serverSocket.accept();
	           mIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	       }
	       catch (IOException ioe) 
	       {
	           ioe.printStackTrace();
	       }
		}
		System.out.println("here");
        
        while (true) {
            try {
                message = mIn.readLine();
               // System.out.println("Got Gaze: "+message);
                if (message == null)
                {
                	continue;
                }
                    
                try {
                    decoded = URLDecoder.decode(message, "UTF-8");
                    fgv.gotFixation(decoded);
                } catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                }
                catch(Exception ex)
                {
                	ex.printStackTrace();
                }
               // System.out.println(decoded);
                
                
	     
	              

            } 

            catch (IOException e) {
                break;
            }

        }

      
    }	
	
	
}

public class EyeCatcher extends Viewer2D {

	Robot robot;
	BufferedImage screen = null;
	int screenIndex = -1;
	
	long lastCapture = -1;
	long startTime = 0;
	
	String folder = null;
	
	ArrayList<String> events = null;
	
	boolean mousedown = false;
	
	int eyeSampleX = 0;
	int eyeSampleY = 0;
	int eyeSampleCt = 0;
	
	int saveEventsCt = 0;
	
	public EyeCatcher(String name) {
		super(name);
		
		try {
			
			DefaultProperties.OpenFilePropertyType f2 = new DefaultProperties.OpenFilePropertyType();
			f2.onlyDirectories = true;
			Property<DefaultProperties.OpenFilePropertyType> p4 = new Property<DefaultProperties.OpenFilePropertyType>("Folder");
			p4.setValue(f2);
			this.addProperty(p4);		
//			DefaultProperties.OpenFile f2 = new DefaultProperties.OpenFile();
//			f2.onlyDirectories = true;
//			Property<DefaultProperties.OpenFile> p4 = new Property<DefaultProperties.OpenFile>("Folder");
//			p4.setValue(f2);
//			this.addProperty(p4);	
			
			GazeServer s = new GazeServer(this);
			Thread t = new Thread(s);
			t.start();	
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void gotFixation(String s) 
	{
		if (folder == null) return;
		
		String[] split = s.split(",");
		
		long t = (new Date()).getTime() - startTime;
		
		if (split.length < 4) //sample
		{
			String et = split[0];
			int x = Integer.parseInt(split[0]);
			int y = Integer.parseInt(split[1]);
			
		 	x = (int)(x*0.75);
	    	y = (int)(y*0.75);
	    	
	    	eyeSampleX += x;
	    	eyeSampleY += y;
	    	eyeSampleCt++;
	    	
	    	if (eyeSampleCt < 3) return;
	    	
	    	eyeSampleCt = 0;
	    	eyeSampleX /= 3;
	    	eyeSampleY /= 3;
			String es = "radu\t"+folder + "\t" + x + "\t" + y + "\t" + t + "\t" + t + "\t" + "1" + "\t" + "S"; 
			events.add(es);
		}
		else
		{
			String et = split[0];
			int x = Integer.parseInt(split[1]);
			int y = Integer.parseInt(split[2]);
			long dur = Long.parseLong(split[3])/1000;
			
		 	x = (int)(x*0.75);
	    	y = (int)(y*0.75);
			String es = "radu\t"+folder + "\t" + x + "\t" + y + "\t" + t + "\t" + (t+dur) + "\t" + "1" + "\t" + et; 
			events.add(es);
		}
		
	}
	
	
	@Override
	public  <T extends PropertyType> void  propertyUpdated(Property p, T newvalue) {
		if (p.getName() == "Folder")
		{
			folder = ((DefaultProperties.OpenFilePropertyType)newvalue).path;
			try {
				robot = new Robot();
			} catch (AWTException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			startTime = (new Date()).getTime();		
			events = new ArrayList<String>();
			
			
			  new GlobalMouseListener().addMouseListener(new MouseAdapter() {
		            @Override 
		            public void mousePressed(MouseEvent e)
		            {
		            	int x = (int)(e.getX()*0.75);
		            	int y = (int)(e.getY()*0.75);
		            	long t = (new Date()).getTime() - startTime;
		            	String es = "radu\t"+folder + "\t" + x + "\t" + y + "\t" + t + "\t" + t + "\t" + "1" + "\t" + "mousedown"; 
		            	events.add(es);	
		            	mousedown = true;
		            }
		            @Override 
		            public void mouseReleased(MouseEvent e)
		            {
		            	int x = (int)(e.getX()*0.75);
		            	int y = (int)(e.getY()*0.75);
		            	long t = (new Date()).getTime() - startTime;
		            	String es = "radu\t"+folder + "\t" + x + "\t" + y + "\t" + t + "\t" + t + "\t" + "1" + "\t" + "mouseup"; 
		            	events.add(es);	
		            	mousedown = false;
		            }
		            @Override 
		            public void mouseMoved(MouseEvent e)
		            {
		            	int x = (int)(e.getX()*0.75);
		            	int y = (int)(e.getY()*0.75);
		            	long t = (new Date()).getTime() - startTime;
		            	String es = "radu\t"+folder + "\t" + x + "\t" + y + "\t" + t + "\t" + t + "\t" + "1" + "\t" + "mousemove"; 
		            	if (mousedown)
		            		es = "radu\t"+folder + "\t" + x + "\t" + y + "\t" + t + "\t" + t + "\t" + "1" + "\t" + "mousedrag"; 
		            	events.add(es);	  
		            }
		        });
		   
		    }
		else
			super.propertyUpdated(p, newvalue);
	}
	

	@Override
	public void render(Graphics2D g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void simulate() {		
		
		long t = (new Date()).getTime();
		if (t - lastCapture < 200)
			return;
		
		capture();
		

		
	}
	
	
	public void capture()
	{
		if (folder == null) return;
		
		long t = (new Date()).getTime();
		
		lastCapture = t;
		
		
		BufferedImage capture0 = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));		
		BufferedImage capture = new BufferedImage((int)(capture0.getWidth()*0.75), (int)(capture0.getHeight()*0.75), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = capture.createGraphics();
		g.drawImage(capture0, 0,0, capture.getWidth(), capture.getHeight(), 0, 0, capture0.getWidth(), capture0.getHeight(),null);
		
		t = (new Date()).getTime();
	
		boolean write = false;
		
		int changedPixels = 0;
		
		if (screen != null)
		{
			int[] cpixels = ((DataBufferInt) capture.getRaster().getDataBuffer()).getData();
			int[] spixels = ((DataBufferInt) screen.getRaster().getDataBuffer()).getData();
			
			int[] calpha = ((DataBufferInt) capture.getAlphaRaster().getDataBuffer()).getData();
			
			
			
			for (int i=0; i<cpixels.length; i++)
			{
				int r1 = (cpixels[i])&0xFF;
				int g1 = (cpixels[i]>>8)&0xFF;
				int b1 = (cpixels[i]>>16)&0xFF;
				int r2 = (spixels[i])&0xFF;
				int g2 = (spixels[i]>>8)&0xFF;
				int b2 = (spixels[i]>>16)&0xFF;					
				if (Math.abs(r1-r2) < 10 && Math.abs(g1-g2) < 10 && Math.abs(b1-b2) < 10)
				{
					cpixels[i] = 0;
					calpha[i] = 0;
				}
				else
				{
					spixels[i] = cpixels[i];					
					write = true;
					changedPixels++;
				}
			}
			System.out.println("---------" + changedPixels);
			
			
			
			
		}
		else
		{
			screen = capture;
			write = true;
		}
		if (!write)
			{
			System.out.println("not write");
			return;
			}

		try {
			screenIndex++;
			// PngEncoder encoder = new PngEncoder();
			// FileOutputStream fout = new FileOutputStream(folder + "\\screen_" + String.format("%06d", screenIndex) + "_" + (t-startTime) + ".png");
	        // encoder.encode(capture, fout);
			
			
			ImageIO.write(capture, "PNG", new File(folder + "\\screen_" + String.format("%06d", screenIndex) + "_" + (t-startTime) + ".png"));
			
			/*byte[] bytes = this.toPNG(capture, true);
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(folder + "\\screen_" + String.format("%06d", screenIndex) + "_" + (t-startTime) + ".png")));
			bos.write(bytes);
			bos.flush();
			bos.close();*/
			if (saveEventsCt > 4 && changedPixels < 1000)
			{
				saveEvents();
				saveEventsCt = 0;
			}
			else
				saveEventsCt++;
		} catch (IOException e) {		
			e.printStackTrace();
		}
		System.out.println((new Date()).getTime() - t);
		
	}
	
	public void saveEvents()
	{
        try {
        BufferedWriter out = new BufferedWriter(new FileWriter(folder + "\\events.txt"));
            for (int i = 0; i < events.size(); i++) {
               out.write(events.get(i));
               out.newLine();
            }
            out.close();
        } catch (IOException e) {}
	}
	
	
	
	

    
}
