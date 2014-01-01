import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import multidimensional.PlanarProjectionViewerFactory;

import Graph.BubbleSetGraphFactory;
import Graph.GraphData;
import Graph.GraphDataFactory;
import Graph.GraphViewer;
import Graph.GraphViewerFactory;
import Graph.LineSetGraphFactory;
import Graph.ViewFocusGraphViewer;
import Graph.ViewFocusGraphViewerFactory;
import ParallelCoord.ParallelCoordinateViewerFactory;

import perspectives.*;
import perspectives.DefaultProperties.*;

import tree.*;
import util.EyeCatcherFactory;
import util.EyeExperimentFactory;
import util.Hotspots;
import util.HotspotsFactory;
import util.ImageTiler;
import util.ImageTilerFactory;

import data.*;
import eyetrack.shapematch.ShapeViewerFactory;
import eyetrack.shapematch.task.ShapeTaskViewerFactory;
import eyetrack.stimulusgen.StimulusGenViewerFactory;


public class main {

	  public static void main(String[] args) {  
		  
		  Environment e = new Environment(false);
	    
	       
//	      e.registerDataSourceFactory(new GraphDataFactory());
//	     e.registerDataSourceFactory(new TableDataFactory());
//	       
//	      e.registerViewerFactory(new RadialBrainConnectivityViewerFactory());
//	      
//	     e.registerViewerFactory(new PlanarProjectionViewerFactory());
//	      
//	      e.registerViewerFactory(new HierarchicalClusteringViewerFactory());
//	      
	      e.registerViewerFactory(new GraphViewerFactory());
	      
	      e.registerViewerFactory(new ViewFocusGraphViewerFactory());
	      
//	      e.registerViewerFactory(new ImageTilerFactory());
//	      
//	      e.registerViewerFactory(new HotspotsFactory());
//	      
//	      e.registerViewerFactory(new ParallelCoordinateViewerFactory());
//	      
//	      e.registerViewerFactory(new LineSetGraphFactory());
//	      
//	      e.registerViewerFactory(new BubbleSetGraphFactory());
//	      
//	      e.registerViewerFactory(new EyeCatcherFactory());
//	      
//	      e.registerViewerFactory(new EyeExperimentFactory());
	      
	      e.registerViewerFactory(new ShapeViewerFactory());
	      
	      e.registerViewerFactory(new ShapeTaskViewerFactory());
	      
	      e.registerViewerFactory(new StimulusGenViewerFactory());
	      
	      if(args.length == 6 && args[0].equalsIgnoreCase("-view") && args[2].equalsIgnoreCase("-edgefile") && args[4].equalsIgnoreCase("-positionfile"))
	      {
	    	  GraphData data = new GraphData("FocusViewGraph");
	    	  OptionsPropertyType formatOption = (OptionsPropertyType) data.getProperty("Format").getValue();
	    	  formatOption.selectedIndex =1;
	    	  data.getProperty("Format").setValue(formatOption);
	    	  
	    	  OpenFilePropertyType f = (OpenFilePropertyType) data.getProperty("Graph File").getValue();
	    	  f.path = args[3];
	    	  data.getProperty("Graph File").setValue(f);
	    	  e.addDataSource(data);
	    	  
	    	 // data.graph.fromEdgeList(new File(args[3]))
	    	  
	    	  while (!data.isLoaded());
	    	  
	    	  System.out.println("Hello ");
	    	  ViewFocusGraphViewer focusView = new ViewFocusGraphViewer("focusView", data);
	    	  
	    	  OpenFilePropertyType positionFile = (OpenFilePropertyType) focusView.getProperty("Load Positions").getValue();
	    	  positionFile.path = args[5];
	    	  
	    	  focusView.getProperty("Load Positions").setValue(positionFile);
	    	  
	    	  e.addViewer(focusView);
	      }
	      
	   /*   for (int i=0; i<20; i++)
	      {
	    	  String s = "";
	    	  if (i / 5 == 0) s = "bar";
	    	  else if (i /5 == 1) s= "scatter";
	    	  else if (i /5 == 2) s = "force";
	    	  else if (i/5 == 3) s = "photo";
	    	  
	    	  s = s + ((i%5)+1);
	    	  
		      OpenFilePropertyType stimulus = new OpenFilePropertyType();
		      stimulus.path = args[0] + s + ".png";
		      OpenFilePropertyType dataet = new OpenFilePropertyType();
		      dataet.path = args[1] + s + ".txt";
		      OpenFilePropertyType datamt = new OpenFilePropertyType();
		      datamt.path = args[2] + s + ".txt";
		      SaveFilePropertyType save1 = new SaveFilePropertyType();
		      save1.path = args[3] + s + "_et_1.png" ;
		      SaveFilePropertyType save2 = new SaveFilePropertyType();
		      save2.path = args[3] + s + "_et_2.png" ;
		      SaveFilePropertyType save3 = new SaveFilePropertyType();
		      save3.path = args[3] + s + "_mt_1.png" ;
		      SaveFilePropertyType save4 = new SaveFilePropertyType();
		      save4.path = args[3] + s + "_mt_2.png" ;		      
	
		      Hotspots v1 = new Hotspots("hotspots"+i+"1");
		      Hotspots v2 = new Hotspots("hotspots"+i+"2");
		      
		      v1.getProperty("Load Stimulus").setValue(stimulus);
		      v1.getProperty("Load Data").setValue(dataet);
		      v1.getProperty("Time").setValue(new IntegerPropertyType(100));
		      v1.getProperty("Save").setValue(save1);
		      OptionsPropertyType o = v1.getProperty("Mode").getValue().copy();
		      o.selectedIndex = 1;
		      v1.getProperty("Mode").setValue(o);
		      v1.getProperty("Save").setValue(save2);
		      
		      v2.getProperty("Load Stimulus").setValue(stimulus);
		      v2.getProperty("Load Data").setValue(datamt);
		      v2.getProperty("Time").setValue(new IntegerPropertyType(100));
		      v2.getProperty("Save").setValue(save3);
		      o.selectedIndex = 1;
		      v2.getProperty("Mode").setValue(o);
		      v2.getProperty("Save").setValue(save4);
	      }*/
	      
	      
	    
	        
	        
	     /* e.getViewerContainers().get(0).mousePressed(50,50,MouseEvent.BUTTON1);
	      for (int k=0; k<50; k++)
	      {
	    	  e.getViewerContainers().get(0).mouseDragged(50+k*10, 50+k*10);
	      
	    	  BufferedImage image = e.getViewerContainers().get(0).getImage();
	    	  
	    	//  System.out.println(image);
	    	  
	    	  if (image == null)
	    	  {
	    		  k--;
	    		  continue;
	    	  }
	    	  String name = e.getViewers().get(0).getName();
	    	  try {
				ImageIO.write(image, "PNG", new File("c:\\viewer_" + name + k + ".png"));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	     
	      }
	      e.getViewerContainers().get(0).mouseReleased(150,150,MouseEvent.BUTTON1);*/
	      
	      
	  }
	  
	  /*public static void main(String[] args) { 
		  
		  if (args.length != 4)
		  {
			  System.out.println("Insufficient arguments. Four arguments (filename, width, and height, output image file) are required.");
			  System.exit(0);
		  }
		  
		  Hotspots h = new Hotspots("bla");
	
		  
		  Property<OpenFile> p = new Property<OpenFile>("Load");
		  
		  OpenFile ff = h.new OpenFile();
		  ff.path = args[0];
		  
		  h.propertyUpdated(p, ff);
		  
		  int width = Integer.parseInt(args[1]);
		  int height = Integer.parseInt(args[2]);
		  
		  
		  BufferedImage bf =  new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		  
		  h.render(bf.createGraphics(), width, height);
		  
		  try {
			ImageIO.write(bf, "PNG", new File(args[3]));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
	  }*/
	
}
