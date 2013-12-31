package util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import Graph.ForceGraphDrawer;

import perspectives.DefaultProperties.*;
import perspectives.Property;
import perspectives.PropertyType;
import perspectives.Viewer2D;

public class Hotspots extends Viewer2D{

	ArrayList<Point2D> fixations;
	ArrayList<Long> startTimes;
	ArrayList<Long> endTimes;
	ArrayList<Long> duration;
	ArrayList<String> users;
	
	ArrayList<String> allUsers;
	
	ArrayList<String> mouseEvents;
	
	
	BufferedImage stimulusImage = null;
	
	boolean loaded = false;
	
	float[][] heatmap = null;
	float[][] spot = null;
	
	boolean heatmapChanged = false;
	
	ArrayList<float[][]> heatmapSeries = new ArrayList<float[][]>();
	ArrayList<Long> heatmapSeriesTimes  = new ArrayList<Long>();
	
	long maxTime;
	
	boolean normalize = true;
	
	ArrayList<BufferedImage> stimulusSequence = null;
	ArrayList<Long> stimulusSequenceTimes = null;
	
	public float[][] initHeatmap(int width, int height)
	{
		float[][] heatmap = new float[width][];
		for (int i=0; i<width; i++)
		{
			heatmap[i] = new float[height];
			for (int j=0; j<heatmap[i].length; j++)
				heatmap[i][j] = 0;
		}	
		heatmapChanged = true;
		
		return heatmap;
	}
	
	public void initSpot(int r)
	{
		spot = new float[2*r][];
		for (int i=0; i<2*r; i++)
			spot[i] = new float[2*r];
		
		float sigma = r/3;
		
		float oneover2pi = 1/ (sigma * (float)Math.sqrt(2*Math.PI));
		
		
		for (int i=0; i<2*r; i++)
			for (int j=0; j<2*r; j++)
			{
				float distance = (float)((i-r)*(i-r) + (j-r)*(j-r));
				float f = oneover2pi * (float)Math.exp(-0.5 * distance / (sigma*sigma));
				
				//spot[i][j] = 1-(float)Math.min((float)distance/(r*r),1.);	
				spot[i][j] = f;
				
				
			}
	}
	
	public void putSpotOnHeatmap(float[][] heatmap, int x, int y, int r, double f)
	{
		if (spot == null || spot.length != 2*r)
			initSpot(r);
		
		for (int i=-r; i<r; i++)
		{
			int cx = x+i;
			if (cx < 0 || cx >= heatmap.length)
				continue;
			
			for (int j=-r; j<r; j++)
			{				
				int cy = y+j;
				if (cy < 0 || cy >= heatmap[cx].length)
					continue;
				
				heatmap[cx][cy] += f*spot[i+r][j+r];
			}
		}
		heatmapChanged = true;
	}
	
	public float maxValueOnHeatmap(float[][] heatmap)
	{
		float mx = 0;
		for (int i=0; i<heatmap.length; i++)
			for (int j=0; j<heatmap[i].length; j++)
			{
				if (heatmap[i][j] > mx)
					mx = heatmap[i][j];
			}
		return mx;
	}
	
	public void normalizeHeatmap(float[][] heatmap)
	{
		float mx = maxValueOnHeatmap(heatmap);
		if (mx == 0) return;
		for (int i=0; i<heatmap.length; i++)
			for (int j=0; j<heatmap[i].length; j++)
				heatmap[i][j] /= mx;
					
	}
	
	public void normalizeHeatmap(float[][] heatmap, double mx)
	{
		if (mx == 0) return;
		for (int i=0; i<heatmap.length; i++)
			for (int j=0; j<heatmap[i].length; j++)
			{
				heatmap[i][j] /= mx;
				heatmap[i][j] = (float)Math.pow((double)heatmap[i][j], 1.);
			}
					
	}
	
	public void drawHeatmap(float[][] heatmap, Graphics2D g)
	{
		if (heatmap == null) return;
		
		BufferedImage image = new BufferedImage(heatmap.length, heatmap[0].length, BufferedImage.TYPE_INT_ARGB);
		
		int mode = ((OptionsPropertyType)getProperty("Mode").getValue()).selectedIndex;
		double transp = 1-((PercentPropertyType)getProperty("Transparency").getValue()).getRatio();
	
        WritableRaster raster = (WritableRaster) image.getData();
        for (int i=0; i<heatmap.length; i++)
        	for (int j=0; j<heatmap[i].length; j++)
        	{
        		int r = (int)(255*heatmap[i][j]);
        		if (r > 255) r=255;
        		r = (int)(r * transp);
        	//	if (r != 0)
        		//	System.out.println(r);
        		Color c = null;
        		if (mode == 0)
        		{
        			//if (i % 2 == 0)
        				c= new Color(255,0,0,r);
        			//else 
        			//	c= new Color(255,255,0,r);
        		}
        		else if (mode == 1)
        			c= new Color(0,0,0,255-r);
        		
        		image.setRGB(i, j, c.getRGB());
        	}
     
       g.drawImage(image, 0,0, null);
	}
	
	public void computeHeatmap(int r)
	{
		if (stimulusSequence != null)
			heatmap = initHeatmap(stimulusSequence.get(0).getWidth(), stimulusSequence.get(0).getHeight());
		else
			heatmap = initHeatmap(stimulusImage.getWidth(), stimulusImage.getHeight());
		
		long mxtime = 0;
		 for (int i=0; i<fixations.size(); i++)
			{
			 if (mouseEvents.get(i).length() != 0)
				 continue;
			 
				double x = fixations.get(i).getX();
				double y = fixations.get(i).getY();
				
				long time = endTimes.get(i) - startTimes.get(i);	
				if (time < 0) time = 0;	
				
				if (time > mxtime) mxtime = time;
				
			//	time = (long)Math.log(time);
		
				this.putSpotOnHeatmap(this.heatmap, (int)x, (int)y, r, time/1000.);
			}
	//	 normalizeHeatmap(this.heatmap);
	}
	
	public void computeHeatmapSeries(int r)
	{
		heatmapSeries = new ArrayList<float[][]>();
		heatmapSeriesTimes  = new ArrayList<Long>();
		
		int[] m = new int[users.size()];
		for (int i=0; i<m.length; i++)
			m[i] = 0;
		
		//int rr = (Integer)this.getProperty("Radius").getValue();
		
		long prevcmt = -1;
		for (int k=0; k<m.length*2; k++ )
		{
			long cmt = Long.MAX_VALUE;
			int index = 0;
			for (int i=0; i<users.size(); i++)
			{
				if (mouseEvents.get(i).length() != 0)
					continue;
				
				if (m[i] == 0 && startTimes.get(i) < cmt)
				{
					cmt = startTimes.get(i);
					index = i;
				}
				else if (m[i] == 1 && endTimes.get(i) < cmt)
				{
					cmt = endTimes.get(i);
					index = -i;
				}
			}
			
			m[Math.abs(index)]++;
			
			float[][] ph, ch; 
			if (heatmapSeries.size() == 0)
			{
				if (stimulusSequence != null)
					ch = initHeatmap(stimulusSequence.get(0).getWidth(), stimulusSequence.get(0).getHeight());
				else
					ch = initHeatmap(stimulusImage.getWidth(), stimulusImage.getHeight());
				
						
			}
			else
			{
				ph = heatmapSeries.get(heatmapSeries.size()-1);
				if (prevcmt != cmt)
				{
					if (stimulusSequence != null)
						ch = initHeatmap(stimulusSequence.get(0).getWidth(), stimulusSequence.get(0).getHeight());
					else
						ch = initHeatmap(stimulusImage.getWidth(), stimulusImage.getHeight());
				for (int x=0; x<ph.length; x++)
					for (int y=0; y<ph[x].length; y++)
						ch[x][y] = ph[x][y];
				}
				else ch = ph;
			}
			

			
			long dur = duration.get(Math.abs(index));	
			
			if (dur < 0) dur = 0;
			dur = (long)Math.sqrt(dur);
			if (index > 0)
			if (dur != 0)
				this.putSpotOnHeatmap(ch, (int)this.fixations.get(Math.abs(index)).getX(), (int)fixations.get(Math.abs(index)).getY(), r, (float)(dur * Math.signum(index)));
			
			if (prevcmt != cmt)
			{
			heatmapSeries.add(ch);
			heatmapSeriesTimes.add(cmt);
			}
			
			prevcmt = cmt;
			
		}
		//maxTime = (long)Math.log(maxTime);
		double maxf = Double.MIN_NORMAL;
		for (int i=0; i<heatmapSeries.size(); i++)
		{
			double v = this.maxValueOnHeatmap(heatmapSeries.get(i));
			if (v > maxf) maxf = v;
		}
		for (int i=0; i<heatmapSeries.size(); i++)
			this.normalizeHeatmap(heatmapSeries.get(i),maxf);
	}
	
	public Hotspots(String name)
	{
		super(name);
		
		fixations = new ArrayList<Point2D>();
		startTimes = new ArrayList<Long>();
		endTimes = new ArrayList<Long>();
		users = new ArrayList<String>();
		duration = new ArrayList<Long>();
		allUsers = new ArrayList<String>();
		mouseEvents = new ArrayList<String>();
		
		OpenFilePropertyType fff = new OpenFilePropertyType();
		Property<OpenFilePropertyType> p333 = new Property<OpenFilePropertyType>("Load Stimulus");
		p333.setValue(fff);
		
		OpenFilePropertyType ff = new OpenFilePropertyType();
		Property<OpenFilePropertyType> p33 = new Property<OpenFilePropertyType>("Load Data");
		p33.setValue(ff);
		
		
		Property<IntegerPropertyType> rad = new Property<IntegerPropertyType>("Radius");
		rad.setValue(new IntegerPropertyType(50));
		
		OptionsPropertyType mode = new OptionsPropertyType();
		mode.options = new String[2];
		mode.options[0] = "1";
		mode.options[1] = "2";
		Property<OptionsPropertyType> modep = new Property<OptionsPropertyType>("Mode");
		modep.setValue(mode);
		
		Property<PercentPropertyType> transp = new Property<PercentPropertyType>("Transparency");
		transp.setValue(new PercentPropertyType(0.));
		
		Property<IntegerPropertyType> time = new Property<IntegerPropertyType>("Time");
		time.setValue(new IntegerPropertyType(0));
		
		OpenFilePropertyType f2 = new OpenFilePropertyType();
		f2.onlyDirectories = true;
		Property<OpenFilePropertyType> p4 = new Property<OpenFilePropertyType>("FolderStimulus");
		p4.setValue(f2);
		
		SaveFilePropertyType sf = new SaveFilePropertyType();
		Property<SaveFilePropertyType> p3333 = new Property<SaveFilePropertyType>("Save");
		p3333.setValue(sf);
		
		
		
		try {			
			this.addProperty(p333);
			this.addProperty(p4);	
			this.addProperty(p33);
			this.addProperty(modep);
			this.addProperty(transp);
			this.addProperty(rad);
			this.addProperty(time);
			this.addProperty(p3333);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

	public <T extends PropertyType> void propertyUpdated(Property p, T newvalue)
	{
		if (p.getName() == "Save")
		{			
			try {
				BufferedImage image = new BufferedImage(stimulusImage.getWidth(), stimulusImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
				
				Graphics2D gc = image.createGraphics();
    		gc.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
 	    		
	                        
	                     		
	            	    gc.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	            	                         RenderingHints.VALUE_ANTIALIAS_ON);

	            	    gc.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
	            	                         RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	            
	            
				this.render(gc);
			    ImageIO.write(image, "PNG",new File(((SaveFilePropertyType)newvalue).path));			    
			    initHeatmap(stimulusImage.getWidth(), stimulusImage.getHeight());
			    
			} catch (IOException e) {
			}
		}
		else if (p.getName() == "Load Stimulus")
		{			
			try {
			    stimulusImage = ImageIO.read(new File(((OpenFilePropertyType)newvalue).path));			    
			    initHeatmap(stimulusImage.getWidth(), stimulusImage.getHeight());
			    
			} catch (IOException e) {
			}
		}
		if (p.getName() == "FolderStimulus")
		{			
			try {
				File folder = new File(((OpenFilePropertyType)newvalue).path);
				File[] listOfFiles = folder.listFiles();
				
				stimulusSequence = new ArrayList<BufferedImage>();
				stimulusSequenceTimes = new ArrayList<Long>();
				
				BufferedImage allim = null;
				Graphics2D g = null;
				long mt = 0;
				
				for (int i=0; i<listOfFiles.length; i++)
				{
					File f = listOfFiles[i];
				    if (f.isFile()) {
				    	String fn = f.getName();
				    	if (!fn.startsWith("screen"))
				    		continue;
				    	
				    	fn = fn.replace(".", "_");
				    	
				    	String[] split = fn.split("_");
				    	
				    	System.out.println(f);
				    	BufferedImage im = ImageIO.read(f);
				    	if (allim == null)
				    	{
				    		allim = new BufferedImage(im.getWidth(), im.getHeight(), im.getType());
				    		g = allim.createGraphics();
				    	}
				    	
				    	g.drawImage(im, 0,0,null);
				    	
				    	Graphics2D g2 = im.createGraphics();
				    	g2.drawImage(allim, 0, 0 ,null);
				    	
				    	long t = Long.parseLong(split[2]);
				    	stimulusSequence.add(im);
				    	stimulusSequenceTimes.add(t); 
				    	
				    	if (t > mt) mt = t;
				    	
				    	System.out.println(stimulusSequence.size());
  
				    }
				}
				maxTime = mt;
				
				if (normalize)
				{
					ArrayList<Long> norm = new ArrayList<Long>();
					for (int i=0; i<stimulusSequenceTimes.size(); i++)
					{
						norm.add((long)(100*stimulusSequenceTimes.get(i)/(double)maxTime));
					}
					stimulusSequenceTimes = norm;
					maxTime = 100;
				}
			    initHeatmap(stimulusImage.getWidth(), stimulusImage.getHeight());
			    
			} catch (IOException e) {
			}
		}
		if (p.getName() == "Radius")
		{
			computeHeatmap(((IntegerPropertyType)newvalue).intValue());
		}
		if (p.getName() == "Load Data")
		{
			try{
				startTimes.clear();
				endTimes.clear();
				users.clear();
				fixations.clear();
				mouseEvents.clear();
				
				 FileInputStream fstream = new FileInputStream(((OpenFilePropertyType)newvalue).path);
				 DataInputStream in = new DataInputStream(fstream);
				 BufferedReader br = new BufferedReader(new InputStreamReader(in));
				 String s;
				 while ((s = br.readLine()) != null)
				 {
					s = s.trim();
					
					String[] split = s.split("\t");
					
					if (split.length < 7) continue;
									
					
					Point2D.Double p1 = new Point2D.Double(Double.parseDouble(split[2]), Double.parseDouble(split[3]));
					//p1.y = p1.y +185;
					//p1.x = p1.x +400;
					
					long time = Integer.parseInt(split[5]) - Integer.parseInt(split[4]);
					
					if (split[4].length() > 6)
					{
						
					}
					
					long t1 = Long.parseLong(split[4]);
					long t2 = Long.parseLong(split[5]);
					if (t2-t1 < 0 || t2-t1 > 10000 || split[0].equals("A1OJ3UBSRZWVQ9")) 
						continue;
					startTimes.add(t1);					
					endTimes.add(t2);
					duration.add(t2-t1);
					System.out.println(duration.get(duration.size()-1));
					fixations.add(p1);
					users.add(split[0]);
					
					if (split.length > 7)
						mouseEvents.add(split[7]);
					else
						mouseEvents.add("");
					
					if (allUsers.indexOf(split[0]) < 0)
						allUsers.add(split[0]);
				 }	
				
				 
				 for (int i=0; i<allUsers.size(); i++)
				 {
					 long mnt = Long.MAX_VALUE;
					 long mxt = Long.MIN_VALUE;
					 long mxd = Long.MIN_VALUE;
					 for (int j=0; j<users.size(); j++)
					 {
						 if (users.get(j).equals(allUsers.get(i)) && startTimes.get(j) < mnt)
							 mnt = startTimes.get(j);
						 if (users.get(j).equals(allUsers.get(i)) && endTimes.get(j) > mxt)
							 mxt = endTimes.get(j);
						 if (users.get(j).equals(allUsers.get(i)) && duration.get(j) > mxd)
							 mxd = duration.get(j);
					 }
					 maxTime = Math.max(mxt, maxTime);
					 
					 for (int j=0; normalize && j<users.size(); j++)
					 {
						 if (users.get(j).equals(allUsers.get(i)))
						 {
							 long nst = (long)(100*(startTimes.get(j) - mnt)/(double)(mxt-mnt));
							 long net = (long)(100*(endTimes.get(j) - mnt)/(double)(mxt-mnt));
							 
							 
							 if (nst < 0 || net < 0)
							 {
								 long nnn = startTimes.get(j); 
								 long ppp = endTimes.get(j);
								 nnn = nnn;
								 ppp = ppp;
							 }
							 startTimes.remove(j); startTimes.add(j,nst);
							 endTimes.remove(j); endTimes.add(j,net);
							 //double dur = 1000* duration.get(j) / (double)mxd;
							// duration.remove(j); duration.add(j,(long)dur);
							 
							 if (net > maxTime)
								 maxTime = net;
						 }
					 }
				 }
				 
				 if (normalize) maxTime = 100;
				 
				
				 
				 
				 computeHeatmap(((IntegerPropertyType)this.getProperty("Radius").getValue()).intValue());
				 computeHeatmapSeries(((IntegerPropertyType)this.getProperty("Radius").getValue()).intValue());
				 
				 loaded = true;
				
				 

				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			};
			
			
			
/*			try{
				loaded = true;
				 FileInputStream fstream = new FileInputStream(((OpenFile)newvalue).path);
				 DataInputStream in = new DataInputStream(fstream);
				 BufferedReader br = new BufferedReader(new InputStreamReader(in));
				 String s;
				 while ((s = br.readLine()) != null)
				 {
					s = s.trim();
					
					String[] split = s.split("\t");
					
					if (split.length < 7) continue;
					if (!split[2].equals("pan") ) continue;
					

					
					
					
					String coords = split[6];
					
					
					String startLoc = coords.substring(9, coords.indexOf(",end"));
					String endLoc = coords.substring(coords.indexOf("endLoc:")+7, coords.indexOf(",zoom"));
					
					startLoc = startLoc.substring(startLoc.indexOf("(")+1);
					startLoc = startLoc.substring(0, startLoc.indexOf(")"));
					
					endLoc = endLoc.substring(endLoc.indexOf("(")+1);
					endLoc = endLoc.substring(0, endLoc.indexOf(")"));
					
					String x1 = startLoc.split(",")[0];
					String y1 = startLoc.split(",")[1];
					String x2 = endLoc.split(",")[0];
					String y2 = endLoc.split(",")[1];
					
					Point2D.Double p1 = new Point2D.Double(Double.parseDouble(x1), Double.parseDouble(y1));
					Point2D.Double p2 = new Point2D.Double(Double.parseDouble(x2), Double.parseDouble(y2));				
					
					if (fixations.size() == 0)
						fixations.add(p1);
					fixations.add(p2);
					
				 }
				
			}
			catch(Exception e){};
*/			
			
			
		}
	}
	@Override
	public void render(Graphics2D g) {
		
		//g.setColor(Color.red);
		//g.fillOval(0,0,100,100);
		render(g, -1, -1);
	}
	
	int ovx = 0;
	int ovy = 0;
	public void render(Graphics2D g, int width, int height) {
		// TODO Auto-generated method stub
		
		
		//if (stimulusImage != null)
		//	g.drawImage(stimulusImage, 0, 0, null);
		
		
		//if (heatmap != null)
		//	this.drawHeatmap(g);
		
		double time = (((IntegerPropertyType)this.getProperty("Time").getValue()).intValue());
		
		if (!normalize) time *= 30;
		
		time = Math.min(time, maxTime);
		
		if (stimulusSequence != null)
		{
			for (int i=stimulusSequence.size()-1; i>=0; i--)
			{
				if (stimulusSequenceTimes.get(i) < time)
				{
					g.drawImage(stimulusSequence.get(i),0,0,null);
					break;
				}
			}
		}
		
		for (int i=heatmapSeries.size()-1; i>=0; i--)
		{
			if (heatmapSeriesTimes.get(i) < time)
			{
				this.drawHeatmap(heatmapSeries.get(i), g);
				return;
			}
		}
		
		
		for (int i=0; i<fixations.size(); i++)
		{
			double t = startTimes.get(i);
			
			if (mouseEvents.get(i).equals("F"))
			{
				if (t <= time && time-t < 100.)
				{
					g.setColor(Color.blue);
					g.fillOval((int)fixations.get(i).getX()-12, (int)fixations.get(i).getY()-12, 24, 24);
				}
			}
			
			if (mouseEvents.get(i).equals("S"))
			{
				if (t <= time && time-t < 100.)
				{
					g.setColor(Color.cyan);
					g.fillOval((int)fixations.get(i).getX()-8, (int)fixations.get(i).getY()-8, 16, 16);
				}
			}
			
			
			
			if (mouseEvents.get(i).equals("mousedown") && t <= time && time-t < 100.)
			{
				g.setColor(Color.red);
				g.fillOval((int)fixations.get(i).getX()-12, (int)fixations.get(i).getY()-12, 24, 24);
			}
			if (mouseEvents.get(i).equals("mouseup")&& t <= time  && time-t < 100.)
			{
				g.setColor(Color.green);
				g.fillOval((int)fixations.get(i).getX()-8, (int)fixations.get(i).getY()-8, 16, 16);
			}
			if (mouseEvents.get(i).equals("mousemove")&& t <= time  && time-t < 100.)
			{
				g.setColor(Color.green);
				g.fillOval((int)fixations.get(i).getX()-4, (int)fixations.get(i).getY()-4, 8, 8);
			}
			if (mouseEvents.get(i).equals("mousedrag")&& t <= time  && time-t < 100.)
			{
				g.setColor(Color.red);
				g.fillOval((int)fixations.get(i).getX()-4, (int)fixations.get(i).getY()-4, 8, 8);
			}
		}
	}


	@Override
	public void simulate() {
		// TODO Auto-generated method stub
		
	}

}
