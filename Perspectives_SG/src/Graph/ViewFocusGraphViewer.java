package Graph;

import java.awt.BasicStroke;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import perspectives.Property;
import perspectives.PropertyType;
import perspectives.DefaultProperties.BooleanPropertyType;
import perspectives.DefaultProperties.OpenFilePropertyType;
import userstudy.UserStudyUtility;
import util.Points2DViewer.PointAspectType;



class ClientInfo
{

    public int userID=-1;
    public Socket mSocket = null;
}
class GazeServer implements Runnable
{
	public static final int PORT = 9876;
	ViewFocusGraphViewer fgv;
	
	Socket socket;
	private ServerSocket serverSocket;
	public GazeServer(ViewFocusGraphViewer fgv)
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
	//	System.out.println("here");
        
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
                    fgv.processGazeFromServer(decoded);
                } catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                }
                catch(Exception ex)
                {
                	ex.printStackTrace();
                }
            } 

            catch (IOException e) {
                break;
            }

        }

      
    }	
	
	
}

public class ViewFocusGraphViewer extends GraphViewer{
	

	double[] nodeScores;
	double[] nodeScores2;

	//each edge is divided into a number of equally lengthed edge segments	
	double[][] edgeSegmentsX;
	double[][] edgeSegmentsY;
	
	//edge selection
	double[][] edgeSegmentScores;
	double[] edgeScores;
	double[] edgeScores2;
	
	//edge filtering
	double[] edgeFilterScores; //values between [0..1] for each edge
		
	double[][] density;
	
	
	Boolean rendering = false;
	
	int IDEALSEGMENTLENGTH = 20;
	int EDGETHRESHOLD = 50;	//viewing distance within which an edge is considered for selection
	
	
	int FOCUSRAD1 = 150;
	int FOCUSRAD2 = 250;
	
	
	///////////// various parameters for simulating eye gaze and such ///////////////
	
	private int gazePositionX = 0;
	private int gazePositionY = 0;
	private int mouseX = 0;
	private int mouseY = 0;
	
	int filterAgX = 0;
	int filterAgY = 0;
	int filterAgCt = 0;	
	
	int focusX=0;
	int focusY = 0;
	
	Object animatingLock = new Object();
	
	int animatingCounter = 1;
	
	private boolean debugging = false;	
	
	boolean fromServer = true;
	
	GazeCorrector gc;
	int correctedGazeX, correctedGazeY;
	
	private String receiverHost;
	private int receiverPort;
	
	
	//////////////////////////        user study stuff      ///////////////////////////////////////
	
	int mistakes = 0;
	int currentuse = 0;
	ArrayList<Integer> use1;
	ArrayList<Integer> use2;
	ArrayList<Boolean> useanswers;
	ArrayList<ArrayList<Integer>> useAnswersPath;
	int showedNode1, showedNode2;
	long ustime = 0;
	boolean withEyeTracking = true;
	boolean withGazeCorrection = true;
	long exptime = 0;
	
	
	long tdecay;
	long tadd;
	long tcompbar;
	
	
	int lag;
	
	private UserStudyUtility userStudy;
	
	
	
	
	/////////////////////////////                 Constructor             //////////////////////////////
	
	public ViewFocusGraphViewer(String name, GraphData g) {
		
		super(name, g);
		
		this.setDirected(false);
		
		this.setColor(Color.lightGray);

		gc = new GazeCorrector(this);
		
		this.userStudy = new UserStudyUtility();

		try {
			//various viewing graphics will be superimposed if debugging is selected		
			Property<BooleanPropertyType> p = new Property<BooleanPropertyType>("debug");
			p.setValue(new BooleanPropertyType(true));
			this.addProperty(p);	
			
			Property<BooleanPropertyType> p2 = new Property<BooleanPropertyType>("eyetracking");
			p2.setValue(new BooleanPropertyType(true));
			this.addProperty(p2);
			
			//loads and starts and experiment
			OpenFilePropertyType ff = new OpenFilePropertyType();
			Property<OpenFilePropertyType> p33 = new Property<OpenFilePropertyType>("Load Experiment");
			p33.setValue(ff);
			this.addProperty(p33);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		if (!fromServer) 	//gaze simulation using mouse position
		{
			// a task that will simulates a gaze (see createGazePosition) and calls the gaze processing functions		
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				  @Override
				  public void run() {
					  createGazePosition(mouseX, mouseY);
					 // processEdgeFocus2(gazePositionX, gazePositionY,  (new Date()).getTime());
					  
					  // the edge filtering is only called every fourth gaze with the avearge of the last four gazes
					  filterAgX += gazePositionX;
					  filterAgY += gazePositionY;
					  filterAgCt++;
					  
					  if (filterAgCt == 4)
					  {
						  processEdgeFilter(filterAgX/4, filterAgY/4);
						  filterAgCt = 0;
						  filterAgX = 0;
						  filterAgY = 0;
					  }
				  }
				}, 0, 15);	
		}
		else
		{
			GazeServer s = new GazeServer(this);
			Thread t = new Thread(s);
			t.start();
		}
		
	}
	private Point processRawGaze(int x , int y) 
	{
		Point gazePoint = new Point();
		int scx = (int)this.getLocationOnScreen().getX();
		int scy = (int)this.getLocationOnScreen().getY();
		Point processingPoint = new Point(x-scx,y-scy);
		try {
			AffineTransform transform = this.getTransform().createInverse();
			transform.transform(processingPoint, gazePoint);
			
			/////////   gaze correction disabled for now  ////////////////////////
			Point cg = gc.correctGaze(x-scx ,y-scy);
		//	cg.x = cg.x - scx; cg.y = cg.y- scy;
			Point correctedGaze = new Point();
			transform.transform(cg,  correctedGaze);
			if(withGazeCorrection)
			{
				this.correctedGazeX = correctedGaze.x;
				this.correctedGazeY = correctedGaze.y;
			}
			else
			{
				this.gazePositionX = gazePoint.x;
				this.gazePositionY = gazePoint.y;	
				this.correctedGazeX = gazePositionX;
				this.correctedGazeY = gazePositionY;
			}
						
			
	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("Unable to invert transform");
			e.printStackTrace();
		}
		return gazePoint;
	}
	public void processGazeFromServer(String gaze)
	{	
		String[] split = gaze.split(",");
		
		if (split.length > 4)//fixation
		{
			if (lag > 0)
			{
				lag--;
				return;
			}
			int x = Integer.parseInt(split[0]);
			int y = Integer.parseInt(split[1]);
	
			this.mouseX = x;
			this.mouseY = y;
			
			long t = new Date().getTime();
			
			this.processRawGaze(x,y);		
			
			processNodeFocus(correctedGazeX, correctedGazeY);
			long t1 = new Date().getTime();
			processEdgeFocus(correctedGazeX, correctedGazeY);
			long t2 = new Date().getTime();
				
			  // the edge filtering is only called every fourth gaze with the avearge of the last four gazes
			  filterAgX += correctedGazeX;
			  filterAgY += correctedGazeY;
			  filterAgCt++;
			  
			  if (filterAgCt == 4)
			  {
				  processEdgeFilter(filterAgX/4, filterAgY/4);
				  filterAgCt = 0;
				  filterAgX = 0;
				  filterAgY = 0;
			  }
			  
			  long t3 = new Date().getTime();
			  if (t3-t >= 10)
			  {
			//  System.out.println("all process: " + (t1-t) + " " + (t2-t1) +"-"+tdecay+","+tadd+","+tcompbar + " " + (t3-t2));
			  	lag = (int)(t3-t)/10;
			  }
			  
			  
		}
		else
		{
			//System.out.println(gaze);	
			
			int x = Integer.parseInt(split[1]);
			int y = Integer.parseInt(split[2]);
			long time = Long.parseLong(split[3]);
			
			if (time < 250000)
				return;
			
			int scx = (int)this.getLocationOnScreen().getX();
			int scy = (int)this.getLocationOnScreen().getY();
			Point processingPoint = new Point(x-scx,y-scy);
			try {
				AffineTransform transform = this.getTransform().createInverse();

				
				gc.transf = transform;
				gc.fixationDetected(processingPoint);
			}
		  catch (Exception e) {
			// TODO Auto-generated catch block
			System.err.println("Unable to invert transform");
			e.printStackTrace();
			
		  }
		}
		
		
	}	
	
	////////////////////////////////////////////////////////////////////////////////////////////////
	//next three functions: simulate gaze position based on mouse position (if fromServer == false)
	////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean mousemoved(int x, int y) {
		boolean sr = super.mousemoved(x, y);
		
		mouseX = x;
		mouseY = y;
		return sr;
	}
	@Override
	protected void setToolTip(int pickedNode) {
		//super.setToolTip(pickedNode);
		this.setToolTipText(""+pickedNode);
	};
	@Override
	public boolean mousepressed(int x, int y, int b) {
		boolean sr = super.mousepressed(x, y, b);		
		mouseX = x;
		mouseY = y;	
		
		return sr;
	}
	
	public void createGazePosition(int mousex, int mousey)
	{
		double jumpSize = 50;
		
		if (Math.random() < 0.1) jumpSize = 150;
		
		this.gazePositionX = (int) (mousex + (1-Math.random()*2) * jumpSize);
		this.gazePositionY = (int) (mousey + (1-Math.random()*2) * jumpSize);
	}
	
	
	/////////////////  property updates //////////////////////////////////////////	
	
	public <T extends PropertyType> void propertyUpdated(Property p, T newvalue)
	{
		super.propertyUpdated(p, newvalue);
		
		if (p.getName() == "Load Experiment")	//loads AND starts an experiment
		{
			loadExperiment(((OpenFilePropertyType)newvalue).path);
			currentuse = 0;
			mistakes = 0;
			if(userStudy.taskType == UserStudyUtility.TASK_TYPE_GAZE_CORRECTION)
			{
				withEyeTracking = true;	
				withGazeCorrection = userStudy.startWithGazeCorrection;
			}
			else
			{
				withEyeTracking =  userStudy.startWithEyeTrack;	
				withGazeCorrection = true;
			}
			
			userStudy.pass=0;
			showUserStudy(use1.get(0), use2.get(0));
			ustime = new Date().getTime();
			userStudy.lastTime= ustime;
			
		}		
		else if (p.getName() == "Load Positions")	//positions were already loaded by the super; here we split edges into segments and create all data structures
		{
			
			/*ArrayList<String> nodes = graph.getNodes();
			for (int i=0; i<nodes.size(); i++)
			{
				this.drawer.setX(i, (int)(this.drawer.getX(i) * 1.2));
				this.drawer.setY(i, (int)(this.drawer.getY(i) * 1.2));
			}*/
				
				prepareStructures();
				vx = vy = null;
						
		}
		//else if (p.getName() == "eyetracking")
		//	this.withEyeTracking = ((BooleanPropertyType)newvalue).boolValue();
	}
	
	//loads an experiment and randomizes the order of the tasks/questions, then starts the experiment
	public void loadExperiment(String path)
	{
		try{
			 FileInputStream fstream = new FileInputStream(path);
			 DataInputStream in = new DataInputStream(fstream);
			 BufferedReader br = new BufferedReader(new InputStreamReader(in));
			 String s;
			 
			 use1 = new ArrayList<Integer>();
			 use2 = new ArrayList<Integer>();
			 useanswers = new ArrayList<Boolean>();
			 this.useAnswersPath = new ArrayList<ArrayList<Integer>>();
			 userStudy.filePath = path;
			 userStudy.selectedNodeIndices.clear();
			 
			 ArrayList<String> nodes = graph.getNodes();
			 
			 while ((s = br.readLine()) != null)
			 {
				s = s.trim();
				if(s.startsWith(UserStudyUtility.HEADER_PREFIX))
				{
					userStudy.parseHeaderLine(s);
					continue;
				}
				String[] split = s.split("\t");
				
				if (split.length < 2) continue;
				
				int e1 = Integer.parseInt(split[0].trim());
				int e2 = Integer.parseInt(split[1].trim());
				use1.add(e1);
				use2.add(e2);
				if(	userStudy.taskType == UserStudyUtility.TASK_TYPE_NEIGHBOR)
				{
					useanswers.add(graph.isEdge(nodes.get(e1), nodes.get(e2)));
				}
				else if(userStudy.taskType == UserStudyUtility.TASK_TYPE_PATH)
				{
					Dijkstra dijkstra = new Dijkstra(this.graph);
					ArrayList<Integer> shortestPath = dijkstra.executeDijkstra(e1, e2);
					this.useAnswersPath.add(shortestPath);
				}					
			 }
			 if(userStudy.taskType == UserStudyUtility.TASK_TYPE_GAZE_CORRECTION)
			 {
			 
			 	this.restartGazeCorrectionTask();
				
			 }
			 
			 //randomize
			 for (int i=0; i<use1.size(); i++)
			 {
				 int newindex = (int)(Math.random() * (use1.size()-2));
				 
				 int e1 = use1.get(i);  int e2 = use2.get(i); boolean answer = useanswers.get(i);
				 
				 use1.remove(i); use2.remove(i); useanswers.remove(i);
				 
				 use1.add(newindex,e1);	use2.add(newindex,e2);	useanswers.add(newindex,answer);					 
			 }				 
			 in.close();				
			}
			catch(Exception e)
			{	}	
	}
	
	public void prepareStructures()
	{
		ArrayList<String> nodes = graph.getNodes();
		nodeScores = new double[nodes.size()];
		nodeScores2 = new double[nodes.size()];
						
		ArrayList<Integer> e1 = new ArrayList<Integer>();
		ArrayList<Integer> e2 = new ArrayList<Integer>();
		graph.getEdgesAsIndeces(e1, e2);				
		
		edgeSegmentsX = new double[e1.size()][];
		edgeSegmentsY = new double[e1.size()][];		
		edgeScores = new double[e1.size()];	
		edgeScores2 = new double[e1.size()];
		edgeSegmentScores = new double[e1.size()][];
		
		edgeFilterScores = new double[e1.size()];
		
		density = new double[e1.size()][];
		
		for (int i=0; i<e1.size(); i++)
		{
			int x1 = this.getNodeX(e1.get(i));
			int y1 = this.getNodeY(e1.get(i));
			
			int x2 = this.getNodeX(e2.get(i));
			int y2 = this.getNodeY(e2.get(i));
			
			//divide the edge into six or more edge segments
			double edgeLength = Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
			int segmentCount = (int)Math.ceil(edgeLength / IDEALSEGMENTLENGTH);
			if (segmentCount < 10) segmentCount = 10;
			double segmentLength = edgeLength / segmentCount; //the real segments length
			
			edgeSegmentsX[i] = new double[segmentCount];
			edgeSegmentsY[i] = new double[segmentCount];
			edgeSegmentScores[i] = new double[segmentCount];
			density[i] = new double[segmentCount];
			
			double dirX = (x2-x1)/edgeLength;
			double dirY = (y2-y1)/edgeLength;
			
			for (int j=0; j<segmentCount; j++)
			{
				double sx = (int) (x1 + j*segmentLength*dirX);
				double sy = (int) (y1 + j*segmentLength*dirY);
				
				if (j == segmentCount-1) //make sure the last segment ends at the edge's endpoint
				{
					sx = x2;
					sy = y2;
				}
				edgeSegmentsX[i][j] = sx;
				edgeSegmentsY[i][j] = sy;
				edgeSegmentScores[i][j] = 0;
			}
			edgeFilterScores[i] = 1;
		}
		computeDensity(edgeSegmentsX, edgeSegmentsY, density);	//this takes some time; if one control point has many other control points around it
		//then its density is high... check method	
	}

	public void computeDensity(double[][] segX, double[][] segY, double[][] density)
	{
		for (int i=0; i<segX.length; i++)
		{
			for (int j=0; j<segX[i].length; j++)
			{
				double dens = 0;
				for (int k=0; k<segX.length; k++)
				{
					if (k == i) continue;
					
					for (int l=0; l<segX[k].length; l++)
					{
						double d = (segX[i][j]-segX[k][l])*(segX[i][j]-segX[k][l]) + (segY[i][j]-segY[k][l])*(segY[i][j]-segY[k][l]);
						if (d > 900) continue;
						
						dens += (1-Math.sqrt(d)/30);						
					}
				}
				dens = dens/10;
				if (dens > 1) dens = 1;
				
				density[i][j] = dens;
			}
		}
	}
	
	
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//											RENDER
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	double[] vx,vy;

	
	public void render(Graphics2D g) {
		
		
		if (debugging && gc !=null)
		{
			AffineTransform a = g.getTransform();			
			g.setTransform(AffineTransform.getTranslateInstance(0,0));
			g.drawImage(gc.bim,0,0,null);
			g.setTransform(a);
		}
		
		synchronized(rendering)
		{
			rendering = true;
		}
		long t = (new Date()).getTime();
		
		
		double respMag = 1; //visual response magnitude
		
		//draw nodes
		ArrayList<String> nodes = this.graph.getNodes();			
		for (int i=0; i<nodes.size(); i++)
		{
			double s1 = 0.; 	//short term selection score
			double s2 = 0.;  //long term selection score			
			if (nodeScores != null && withEyeTracking)
			{
				s1 = respMag*Math.min(1,nodeScores[i]);
				s2 = respMag*Math.min(1,nodeScores2[i]);
			}
				
			
			Color c = new Color(100 + (int)(s1*30) + (int)(s2*90),50,50, 100 + (int)(s1*30)+(int)(s2*120));			
			
			int x = this.getNodeX(i);
			int y = this.getNodeY(i);
			
			int a1 = (int)(Math.min(1, s1*2) * c.getAlpha() *0.1);		

			//this is for the user study
			if (i == this.showedNode1 || i == showedNode2)
				c = Color.red;			
			if(userStudy.taskType== UserStudyUtility.TASK_TYPE_PATH
					&& userStudy.selectedNodeIndices.contains(new Integer(i)))
			{
				c = Color.blue;
			}else if(userStudy.taskType== UserStudyUtility.TASK_TYPE_GAZE_CORRECTION
					&& userStudy.selectedNodeIndices.contains(new Integer(i)))
			{
				c = Color.red;
			}
			if(userStudy.taskType== UserStudyUtility.TASK_TYPE_GAZE_CORRECTION
					&& nodeScores != null
					&& nodeScores[i] >0.5)
			{
				this.pointSelected(i);
			}
			
			if (a1 > 0)
			{
				g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), a1));
				g.fillOval(x-9, y-9, 18, 18);
			}
			if (c.getAlpha() - a1 > 0)
			{
				g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.max(0,c.getAlpha() - a1)));		
				g.fillOval(x-7, y-7, 14,14);
			}
			
			if (this.debugging && gc!= null && i== gc.suggestedLastNode)
				g.drawRect(x-15, y-15, 30,30);
		}		
		
		//draw edges
		ArrayList<Integer> e1 = new ArrayList<Integer>();
		ArrayList<Integer> e2 = new ArrayList<Integer>();
		graph.getEdgesAsIndeces(e1, e2);
		
		
		boolean initvx = false;
		if (vx == null || vy == null)
		{
			vx = new double[e1.size()];
			vy = new double[e2.size()];
			initvx = true;
		}
		
		for (int i=0; i<e1.size(); i++)
		{
			int x1 = this.getNodeX(e1.get(i)); 
			int y1 = this.getNodeY(e1.get(i));

			int x2 = this.getNodeX(e2.get(i));
			int y2 = this.getNodeY(e2.get(i));
			
			if (initvx)
			{
				double l = Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
				vx[i] = (x2-x1)/l;
				vy[i] = (y2-y1)/l;
			}
			x1 = (int)(x1 + vx[i]*7);
			y1 = (int)(y1 + vy[i]*7);
			x2 = (int)(x2 - vx[i]*7);
			y2 = (int)(y2 - vy[i]*7);	//the edge doesn't go all the way to the center of the node, only to it's boundary	
			
			
			double s1 = 0; 	//short term selection score
			double s2 = 0;  //long term selection score
			double s3 = 1.;	//filtering score (1- visible; 0-invisible)
			double s4 = 0.;
			if (edgeScores != null && withEyeTracking)
			{
				s1 = respMag*Math.min(1,1.5 * edgeScores[i]);
				s2 = respMag*edgeScores2[i];
				s3 = respMag*edgeFilterScores[i];
				s4 = respMag*(nodeScores2[e1.get(i)] + nodeScores2[e2.get(i)]) / 2.;
			}
			
			//combining filtering and selection
			double alpha1 = s3*(0.5 + 0.4*s1)+(1-s3)*s1*s1*0.8;
			double alpha2 = s3*(s2*0.2 + s4*0.4)+ (1-s3)*(s2*0.2+ s4*0.4)*0.8;
			double red = 0.4 + 0.1*s1 + 0.1*s2 + 0.2*s4;			

			if (!withEyeTracking)	//no eye-tracking; just do some default stuff
			{
				alpha1 = 0.5;
				alpha2 = 0;
				s1 = 0;
			}
			
			//draw a halo around edges
			//if (s1>0)
			//{
				Color haloColor = new Color(250,250,250, (int)(150*alpha1));
				g.setStroke(new BasicStroke(5));
				g.setColor(haloColor);			
				g.drawLine((int)(x1 + vx[i]*9),(int)(y1 + vy[i]*9),(int)(x2 - vx[i]*9),(int)(y2 - vy[i]*9));
			//}
			
			//edge based on short term score
			Color c = new Color((int)(red*255),90,90, (int)(255*alpha1));
			g.setStroke(new BasicStroke(1));
			g.setColor(c);			
			g.drawLine(x1,y1,x2,y2);
			
			//edge based on long term score
				g.setStroke(new BasicStroke(3));
				Color c2 = new Color((int)(red*255),90,90,(int)(255*alpha2));
				g.setColor(c2);
				g.drawLine(x1,y1,x2,y2);
			
		}
		
		//Little bit fading effect on the time of finding path
		if ((userStudy.taskType == UserStudyUtility.TASK_TYPE_PATH
				||userStudy.taskType == UserStudyUtility.TASK_TYPE_GAZE_CORRECTION)
				&& userStudy.taskDuration > userStudy.alertDuration	&& exptime > 0 && t > exptime-userStudy.alertDuration)
		{
			//System.out.println("Aler time");
			double alpha = (t-exptime+userStudy.alertDuration)/1000.;
			if (alpha > 1) alpha = 1;
			g.setColor(new Color(255,200,200,(int)(alpha*120)));
			g.fillRect(-10000, -10000, 20000, 20000);
		}	
		//user study stuff: hide everything if time to answer question expires
		if (exptime > 0 && t > exptime)
		{
			double alpha = (t-exptime)/1000.;
			if (alpha > 1) alpha = 1;
			g.setColor(new Color(200,200,200,(int)(alpha*255)));
			g.fillRect(-10000, -10000, 20000, 20000);
			return;
		}		
		
		
		debugging = ((BooleanPropertyType)this.getProperty("debug").getValue()).boolValue();
		if (debugging) //draw debugging info
		{
			double zoom = this.getZoom();
			
			
			int et = (int)(EDGETHRESHOLD / zoom);
			
			//int r1  = (int)(this.FOCUSRAD1 / zoom);
			//int r2 = (int)(this.FOCUSRAD2 / zoom);
			//g.drawOval(focusX-r1,focusY-r1, 2*r1, 2*r1);
			//g.drawOval(focusX-r2,focusY-r2, 2*r2, 2*r2);
			
			g.setColor(new Color(0,0,255,120));
			g.fillOval(this.correctedGazeX-5, this.correctedGazeY-5, 10, 10);
			g.drawOval(this.correctedGazeX-et, this.correctedGazeY-et, et*2, et*2);
			
			g.setColor(new Color(255,0,0,120));
			g.fillOval(this.gazePositionX-5, gazePositionY-5, 10, 10);
			g.drawOval(this.gazePositionX-et, gazePositionY-et, et*2, et*2);
			
			/*g.setColor(Color.red);			
			for (int i=0; i<gc.fixationsV.size(); i++)
				g.fillOval(gc.fixationsV.get(i).x-10, gc.fixationsV.get(i).y-10, 20,20);*/
			
			
			
			
			//g.setColor(Color.green);
			//for (int i=0; i<gazeHistory.size(); i++)
				//g.fillOval(gazeHistory.get(i).x-10, gazeHistory.get(i).y-10, 20,20);
			
		
			//the edge segment scores
		/*	g.setColor(new Color(200,200,50,100));
			for (int i=0; edgeSegmentsX != null &&  i<edgeSegmentsX.length; i++)
			{
				for (int j=0; j<edgeSegmentsX[i].length; j++)
				{
					int r = (int)(30*Math.sqrt(edgeSegmentScores[i][j]));
					
					if (this.edgeSegmentScores[i][j] > 0)
					{
					//	System.out.print(" " + edgeSegmentScores[i][j]);
						g.fillOval((int)edgeSegmentsX[i][j]-r, (int)edgeSegmentsY[i][j]-r, 2*r, 2*r);
						
					}
				}	
			}*/
			
		}
		
		//System.out.println("render time: " + (new Date().getTime()-t));
		
		synchronized(rendering)
		{
			rendering = false;
		}
	}
	

	@Override
	public Color backgroundColor() {
		// TODO Auto-generated method stub
		return new Color(250,250,250);
	}	
	
	
	
	
	
	
	
	
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	///////////////////////////////////////////////////   GAZE COMPUTATIONS //////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	//////////////////////   GAZING NODES ///////////////////////////////////////
	public void processNodeFocus(int x, int y)
	{
		synchronized(rendering)
		{
			if (rendering) return;
		}
		
			if (nodeScores == null)
				return;
			
			int et = (int)(EDGETHRESHOLD / this.getZoom());
			
			for (int i=0; i<nodeScores.length; i++)	//for all nodes
			{
				int nx = this.getNodeX(i);
				int ny = this.getNodeY(i);
				
				nodeScores[i] = Math.max(0, nodeScores[i]-0.02);	//node scores decay over time
				if (nodeScores[i] < 0) nodeScores[i] = 0;
					
				double d = Math.sqrt((x-nx) * (x-nx) + (y-ny)*(y-ny));
													
				if (d <= et)
				{				
					//the score is inversely proportional to the distance (small distance -> high score)
					double dbl = (et-d)/et;
					
					nodeScores[i] += dbl/10;
				}	
				if (nodeScores[i] > nodeScores2[i]) nodeScores2[i] += 0.0005;
				else nodeScores2[i] -= 0.0001;
				if (nodeScores2[i] > 1) nodeScores2[i] = 1;
				else if (nodeScores2[i] <0) nodeScores2[i] = 0;
			}
		
		
		//diffuse nodeScores2
		ArrayList<Integer> e1 = new ArrayList<Integer>();
		ArrayList<Integer> e2 = new ArrayList<Integer>();
		graph.getEdgesAsIndeces(e1, e2);
		for (int i=0; i<e1.size(); i++)
		{
			double difscore = Math.max(nodeScores2[e1.get(i)]/5., nodeScores2[e2.get(i)]/5.);
			nodeScores2[e1.get(i)] = Math.max(nodeScores2[e1.get(i)], difscore);
			nodeScores2[e2.get(i)] = Math.max(nodeScores2[e2.get(i)], difscore);
		}
	}
	
	
	///////////////////////////////   FILTERING EDGES    //////////////////////
	//hides edges that pass through the gaze focus but have endpoints far away from the gaze focus. 
	//the hiding is done by computing an edgeFocusScore [0..1] that is aggregated into the edge alpha at rendering
	public void processEdgeFilter(int x, int y)
	{
		synchronized(rendering)
		{
			if (rendering) return;
		}
		

		
		if (edgeSegmentsX == null)
				return;
			
			focusX = x;
			focusY = y;
			
			double zoom = this.getZoom();
			int r1  = (int)(this.FOCUSRAD1 / zoom);
			int r2 = (int)(this.FOCUSRAD2 / zoom);
			
			//compute distances from x,y to all edges
			for (int i=0; i<edgeFilterScores.length; i++)
			{				
				//compute distance from gaze to edge SEGMENT
				Line2D.Double l = new Line2D.Double(edgeSegmentsX[i][0], edgeSegmentsY[i][0], edgeSegmentsX[i][edgeSegmentsX[i].length-1], edgeSegmentsY[i][edgeSegmentsY[i].length-1]);
				double d = l.ptSegDist(x, y);
				
				//compute distances from gaze to edge endpoints (1 and 2) and use the minimum
				double pd1 = Math.sqrt((x-edgeSegmentsX[i][0])*(x-edgeSegmentsX[i][0]) + (y-edgeSegmentsY[i][0])*(y-edgeSegmentsY[i][0]));
				double pd2 = Math.sqrt((x-edgeSegmentsX[i][edgeSegmentsX[i].length-1])*(x-edgeSegmentsX[i][edgeSegmentsX[i].length-1]) + (y-edgeSegmentsY[i][edgeSegmentsX[i].length-1])*(y-edgeSegmentsY[i][edgeSegmentsX[i].length-1]));
				double pd = Math.min(pd1, pd2);
				
				//f = if the closest endpoint is within rad1 f is 1; if its outside rad2 f is 0; between rad1 and rad2 it decays linearly from 1 to 0.
				double f = 1 - (pd-r1)/(r2-r1);
				if (f > 1) f = 1; if (f < 0) f = 0;
				
				//edge scoring function: within rad 1 it only depends on the endpoint (pd); outside rad2 it only depends on d; between rad1 and rad2 it is a combination of the two
				double score = f + (1-f)*Math.min(1.,d/r2);
				
				//combine the current score with the previous scores to create smooth transitions
				if (score < edgeFilterScores[i])
					edgeFilterScores[i] -= 0.005;
				else
					edgeFilterScores[i] += 0.01;
				
				if (edgeFilterScores[i] < this.edgeScores[i])
					edgeFilterScores[i] += 0.05;
				
				if (edgeFilterScores[i]<0) edgeFilterScores[i] = 0;
				if (edgeFilterScores[i]>1) edgeFilterScores[i] = 1;					
			}
				
	}
	
	
	///////////////////////////////         GAZING EDGES  ////////////////////////////////
	public void processEdgeFocus(int x, int y)
	{
		synchronized(rendering)
		{
			if (rendering) return;
		}
		
		if (edgeSegmentsX == null)	// no coordinates have yet been loaded for the graph
			return;
		
		tdecay = 0;
		tcompbar = 0;
		tadd = 0;
		
		double zoom = this.getZoom();
		
		int et = (int)(EDGETHRESHOLD / zoom);
		
		double[] s = new double[edgeSegmentsX.length];
		for (int i=0; i<edgeSegmentsX.length; i++)
		{
			decayEdgeGazes(edgeSegmentScores[i], density[i], 0.04);
			addGazeToEdge(x,y, edgeSegmentsX[i], edgeSegmentsY[i], edgeSegmentScores[i], density[i], et);
			double d = this.computeBars(i,edgeSegmentsX[i], edgeSegmentsY[i], edgeSegmentScores[i], density[i]);
			
			if (d == 0) continue;	//no reason to continue;		
			
			double x1 = edgeSegmentsX[i][0];
			double y1 = edgeSegmentsY[i][0];
			double x2 = edgeSegmentsX[i][edgeSegmentsX[i].length-1];
			double y2 = edgeSegmentsY[i][edgeSegmentsY[i].length-1];
			double l = Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
			
			if (l == 0) continue;
			
			double d1 = d/l; if (d1 > 1) d1 = 1;
			
			double d2 = d/(500/zoom);
			if (d2 > 1) d2 =1;	
			
			s[i] = (3*d2+d1)/4;		
		}
		
		for (int i=0; i<edgeSegmentsX.length; i++)
		{
			if (s[i] > edgeScores[i])
				edgeScores[i] = Math.min(1,edgeScores[i]+s[i]/10);
			else 
				edgeScores[i] = Math.max(0, edgeScores[i]-0.004) ;
			
			
			//edgeScores2 is an edge selection that works for longer times (if the edge has been accessed many times
			//in the last few seconds edge scores 2 will tend to get higher
			if (edgeScores[i] > edgeScores2[i])
				edgeScores2[i] += edgeScores[i]/400;
			else
				edgeScores2[i] -= 0.002;
			
			if (edgeScores2[i] > 1) edgeScores2[i] = 1;
			if (edgeScores2[i] < 0) edgeScores2[i] = 0;
		}	
	}
	
	public void decayEdgeGazes(double[] segV, double[] density, double howMuch)
	{	
		long t = new Date().getTime();
		for (int i=0; i<segV.length; i++)
		{			
			double howMuch2 = howMuch / (1+density[i]);
			segV[i] -= (2*howMuch2 / Math.sqrt(segV.length));
			if (segV[i] < 0) segV[i] = 0;
		}
		tdecay+= (new Date().getTime()-t);
	}
	
	public void addGazeToEdge(int x, int y, double[] segX, double[] segY, double[] segV, double[] density, int threshold)
	{
		long t = new Date().getTime();
		
		double bestDist = Double.MAX_VALUE;
		int bestIndex = -1;
		
		for (int i=0; i<segX.length; i++)
		{
			double d = (x-segX[i])*(x-segX[i]) + (y-segY[i])*(y-segY[i]);
			if (d < bestDist)
			{
				bestDist = d;
				bestIndex = i;
			}
		}
		if (bestIndex >= 0 && bestDist < threshold*threshold)
		{
			bestDist = Math.sqrt(bestDist);
			double v = 1-bestDist/threshold;
			
			int index = bestIndex;
			double max = 0;
			for (int i=bestIndex-2; i<=bestIndex+2; i++)
			{
				if (i < 0 || i>=segX.length)
					continue;
				
				if (segV[i] > max)
				{
					index = i;
					max = segV[i];
				}
			}
			
			segV[index] = segV[index] + v/(2*(1+density[index]));
			if (segV[index] > 1) segV[index] = 1;
		}
		
		tadd+= (new Date().getTime()-t);
	}
	
	
	public double computeBars(int e, double[] segX, double[] segY, double[] segV, double[] density)
	{
		if (segX.length <= 1) return 0;
		
		long t = new Date().getTime();
		
		double[] barsR = new double[segV.length];
		double[] barsL = new double[segV.length];		
		
		double x1 = segX[0];
		double y1 = segY[0];
		double x2 = segX[segX.length-1];
		double y2 = segY[segY.length-1];		
		double len = Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
		double segL = len/(segX.length-1);
		
		
			
		for (int i=0; i<segX.length; i++)
		{
			double val = segV[i];
			if (val == 0) continue;

			val = val* (2+1*(1-density[i]));
			barsL[i] = val; 
			barsR[i] = val;
			
			if (val > i)
				barsR[i] += (val-i);
			if (val+i > segX.length-1)
				barsL[i] += (val+i - segX.length+1);
			
			if (barsL[i] > i)
				barsL[i] = i;
			if (barsR[i] + i > segX.length-1)
				barsR[i] = segX.length-1 - i;
			
		}
		
		double min = -1;
		double max = -1;
		double d = 0;
		for (int i=0; i<segX.length; i++)
		{
			if (segV[i] <= 0) continue;
			
			double l = i*segL - barsL[i]*segL;
			double r = i*segL + barsR[i]*segL;			
			
			if (min < 0)
			{
				min = l; 
				max = r; 				
			}
			else
			{
				if (l > max)
				{					
					d = d + (max-min);
					min = l;
					max = r;					
				}
				else
				{				
					if (l < min) min = l;
					if (r > max) max = r;
				}
			}	
		}
		if (max > 0 && min>=0)
			d = d + (max-min);
		
		tcompbar+= (new Date().getTime()-t);
		return d;
	}
	
	
	
	
	
	
	
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////  USER STUDY STUFF //////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void keyPressed(int keycode) {
		// TODO Auto-generated method stub
		//super.keyPressed(keycode);
		
		
		if (keycode == KeyEvent.VK_Y)
			advanceStudy(true);
		else
			advanceStudy(false);
	}

	
	public void showUserStudy(int index1, int index2)
	{
		//System.out.println(index1 + " " + index2);
		int x1 = getNodeX(index1);
		int y1 = getNodeY(index2);
		
		int x2 = getNodeX(index2);
		int y2 = getNodeY(index1);
		
		showedNode1 = index1;
		showedNode2 = index2;
		
		translate(-(x1+x2)/2 + 600, -(y1+y2)/2 + 400);
		
		exptime = new Date().getTime() + userStudy.taskDuration;
	}
	private void restartGazeCorrectionTask()
	{
		use1.clear();
		use2.clear();
		userStudy.totalGazeSelection=0;
		userStudy.selectedNodeIndices.clear();
		
		int totalSelection=(int)Math.floor( this.graph.nodes.size() * userStudy.selectionRatio)-2;
		userStudy.initiallyUnselected = this.graph.nodes.size() -totalSelection-2;
		use1.add(0);
		use2.add(this.graph.nodes.size()-1);
		Random rand = new Random();
		for(int i=0;i<totalSelection;i++)
		{
			Integer index =new Integer(Math.abs(rand.nextInt() % this.graph.nodes.size()));
			while(userStudy.selectedNodeIndices.contains(index) || index == 0 || index == this.graph.nodes.size()-1)
			{
				index =new Integer(Math.abs(rand.nextInt() % this.graph.nodes.size()));							
			}
			userStudy.selectedNodeIndices.add(index);
		}
	}
	private void analyzeAnswer(boolean answer)
	{
		long currentTime = new Date().getTime();
		long responseTime = currentTime - userStudy.lastTime;
		userStudy.lastTime = currentTime;
		if(userStudy.taskType == UserStudyUtility.TASK_TYPE_NEIGHBOR)
		{
			boolean isCorrectAnswer = false;
			if(answer == useanswers.get(currentuse))
			{
				isCorrectAnswer = true;
			}
			int node1 = use1.get(currentuse);
			int node2 = use2.get(currentuse);
			boolean hasEdge = useanswers.get(currentuse);
			
			userStudy.addResultLineForTaskNeighbor(withEyeTracking, node1, node2, hasEdge, isCorrectAnswer,responseTime);
			
		}
		else if(userStudy.taskType == UserStudyUtility.TASK_TYPE_PATH)
		{
			int node1 = use1.get(currentuse);
			int node2 = use2.get(currentuse);
			ArrayList<Integer> path = this.useAnswersPath.get(currentuse);
			int shortestPathSize = path.size();
			String shortestPath = "";
			for(int i=0;i<shortestPathSize;i++)
			{
				shortestPath +=path.get(i)+",";
			}
			
			
			
			boolean isUserPathValid = false;
			String userPath =node1+",";
			if(this.isUserPathValid(node1, node2, userStudy.selectedNodeIndices))
			{
				for(int i=0;i<userStudy.selectedNodeIndices.size();i++)
				{
					userPath+= userStudy.selectedNodeIndices.get(i)+",";
				}
				userPath+= node2;
				isUserPathValid = true;
			}
			else if(this.isUserPathValid(node2, node1, userStudy.selectedNodeIndices))
			{
				for(int i=userStudy.selectedNodeIndices.size()-1;i>=0;i--)
				{
					userPath+= userStudy.selectedNodeIndices.get(i)+",";
				}
				userPath+= node2;
				isUserPathValid = true;
			}
			else
			{
				for(int i=0;i<userStudy.selectedNodeIndices.size();i++)
				{
					userPath+= userStudy.selectedNodeIndices.get(i)+",";
				}
				userPath+= node2;
				isUserPathValid = false;
			}
			userStudy.selectedNodeIndices.clear();
			userStudy.addResultLineForTaskpath(withEyeTracking, node1, node2,shortestPathSize, shortestPath, userPath, isUserPathValid, responseTime);
		}
		else if(userStudy.taskType == UserStudyUtility.TASK_TYPE_GAZE_CORRECTION)
		{
			boolean isGazeCorrection = this.withGazeCorrection;
			int totalNodes = this.graph.nodes.size();
			int userSelected = userStudy.totalGazeSelection;
			int initiallyUnselected = userStudy.initiallyUnselected;
			double percentile = (100.0* userSelected)/initiallyUnselected;
			userStudy.addResultLineForTaskGazeCorrecton(withGazeCorrection, totalNodes, initiallyUnselected, userSelected, percentile, responseTime);
		}
		
	}
	private boolean isUserPathValid(int source, int target, ArrayList<Integer> intermediateNodes)
	{
		boolean result = true;
		int currentVertex = source;
		
		for(int i=0;i< intermediateNodes.size();i++)
		{
			int node = intermediateNodes.get(i);
			result = result && this.graph.isEdge(this.graph.nodes.get(currentVertex), this.graph.nodes.get(node));
			currentVertex = node;
		}
		result = result && this.graph.isEdge(this.graph.nodes.get(currentVertex), this.graph.nodes.get(target));
		return result;
	}
	private void endOfStudy()
	{
		System.out.println("End of Study");
		JOptionPane.showMessageDialog(null, "End of Study! Result written to "+userStudy.filePath+".result");
		userStudy.OutputResultToFile();
	}
	@Override
	protected void pointSelected(int p) {
		// TODO Auto-generated method stub
		if(userStudy.taskType == UserStudyUtility.TASK_TYPE_PATH)
		{
			userStudy.selectedNodeIndices.add(new Integer(p));
		}
		else if(userStudy.taskType == UserStudyUtility.TASK_TYPE_GAZE_CORRECTION)
		{
			if(!userStudy.selectedNodeIndices.contains(new Integer(p))
					&& p!= 0 && p!=this.graph.nodes.size()-1)
			{
				userStudy.selectedNodeIndices.add(new Integer(p));
				userStudy.totalGazeSelection++;
			}
			
		}
		
	}

	public void advanceStudy(boolean answer)
	{
		analyzeAnswer(answer);
		if (userStudy.taskType == UserStudyUtility.TASK_TYPE_NEIGHBOR
				&& answer != useanswers.get(currentuse))
		{
			mistakes++;
		}
		
		System.out.println(withEyeTracking + " " + currentuse + "" + mistakes);
		
		currentuse++;
		if (currentuse >= use1.size())
		{
			if(userStudy.pass ==1 )
			{
				userStudy.pass =0;
				endOfStudy();
				return;
			}
			else if (userStudy.pass ==0)
			{
				if(userStudy.taskType == UserStudyUtility.TASK_TYPE_GAZE_CORRECTION)
				{
					withEyeTracking = true;
					withGazeCorrection = !withGazeCorrection;
					this.restartGazeCorrectionTask();
				}
				else
				{
					withGazeCorrection = true;
					withEyeTracking = !withEyeTracking;	
				}
							
				currentuse = 0;						
				mistakes = 0;
				ustime = new Date().getTime();
				userStudy.pass++;
			}
			else
			{
				return;
			}
			System.out.println("Result " + withEyeTracking  + ": " + mistakes + " , " + (new Date().getTime() - ustime));
			
//			if (!withEyeTracking)
//			{
//				withEyeTracking = true;				
//				currentuse = 0;						
//				mistakes = 0;
//				ustime = new Date().getTime();
//				userStudy.pass++;
//			}
//			else
//				return;
		}
		showUserStudy(use1.get(currentuse), use2.get(currentuse));	
	}
	
	

}//end class
