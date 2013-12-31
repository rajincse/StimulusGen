package eyetrack.shapematch.task;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import perspectives.DefaultProperties.IntegerPropertyType;
import perspectives.DefaultProperties.OpenFilePropertyType;
import perspectives.Property;
import perspectives.PropertyType;
import perspectives.Viewer2D;
import eyetrack.shapematch.AbstractShape;
import eyetrack.shapematch.LineShape;
import eyetrack.shapematch.OvalShape;
import eyetrack.shapematch.PolylineShape;
import eyetrack.shapematch.RectangleShape;
import eyetrack.shapematch.TextShape;

public class ShapeTaskViewer extends Viewer2D {
	public static final int GAZE_LIST_THRESHOLD =10;
	public static final boolean DRAG_ENABLED = true;
	
	private ArrayList<ShapeTask> taskList ;
	private ShapeTask currentTask;
	
	public ShapeTaskViewer(String name) {
		super(name);
		this.taskList = new ArrayList<ShapeTask>();
		this.currentTask = null;
		this.fiilupProperties();
		
		// TODO Auto-generated constructor stub
	}
	
	
	protected void fiilupProperties()
	{
		try
		{
			OpenFilePropertyType ff = new OpenFilePropertyType();
			Property<OpenFilePropertyType> p33 = new Property<OpenFilePropertyType>("Load Tasks");
			p33.setValue(ff);
			this.addProperty(p33);
			
			Property<IntegerPropertyType> p34 = new Property<IntegerPropertyType>("Task");
			
			p34.setValue(new IntegerPropertyType(0));
			this.addProperty(p34);
			
		}
		catch (Exception e) {		
			e.printStackTrace();
		}
	}
	private void loadTasks(File f)
			throws SAXException, IOException, ParserConfigurationException
	{
		this.taskList.clear();
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(f);
		doc.getDocumentElement().normalize();
		NodeList tasks = doc.getElementsByTagName("Task");
		for(int index=0;index<tasks.getLength();index++)
		{
			Node taskNode = tasks.item(index);
			if(taskNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element taskElement = (Element) taskNode;
				ShapeTask task = new ShapeTask(taskElement);
				this.taskList.add(task);
			}
		}
		if(!this.taskList.isEmpty())
		{
			this.currentTask = this.taskList.get(0);
		}
		
		
	}
	
	public  <T extends PropertyType> void propertyUpdated(Property p, T newvalue)
	{
		System.out.println("Property updated "+p.getName());
		if (p.getName() == "Load Tasks")
		{
						
			try{
			  File f =new File( ((OpenFilePropertyType)newvalue).path);
			  this.loadTasks(f);
			  
			  
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else if
		(p.getName() == "Task")
		{
						
			IntegerPropertyType currentTaskIndex = (IntegerPropertyType) newvalue;
			if(currentTaskIndex.intValue() <  this.taskList.size())
			{
				this.currentTask = this.taskList.get(currentTaskIndex.intValue());
			}
			else
			{
				p.setValue(new IntegerPropertyType(0));
			}
			
		}
	}
	@Override
	public void render(Graphics2D g) {
		// TODO Auto-generated method stub
		if(currentTask != null)
		{
			currentTask.render(g);
		}
//		System.out.println("Start");
//		int x =100;
//		int y =100;
//		int w = 750;
//		int h =500;;
//		Random rand = new Random();
//		for(int i=0;i<10;i++)
//		{
//			int randomX =(int) Math.abs( rand.nextInt() % w);
//			int randomY =(int) Math.abs( rand.nextInt() % h);
//			
//			System.out.println("<Oval x=\""+(x+randomX)+"\" y=\""+(y+randomY)+"\" width=\"50\" height=\"50\" color=\"CC3B5B\" fill=\"Y\"/>");
//		}
//		System.out.println("End");
		
//		g.setColor(Color.blue);
//		double[] control = {
//				150,500,0,
//				250,600,0,
//				300,700,0,
//				450,600,0,
//				600,600,0
//				};
//
//		double[] spline = SplineFactory.createCatmullRom(control, 10);
//		int[] sx = new int[spline.length/3];
//		int[] sy = new int[spline.length/3];
//		
//		System.out.println("Start");
//		for (int j=0; j<sx.length; j++)
//		{
//			sx[j] = (int)spline[3*j];
//			sy[j] = (int)spline[3*j+1];
//			System.out.println("<Point x=\""+sx[j]+"\" y=\""+sy[j]+"\" />");
//		}
//		System.out.println("End");
//		g.drawPolyline(sx, sy, sx.length);
	}

	@Override
	public void simulate() {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public boolean mousepressed(int x, int y, int button) {
		// TODO Auto-generated method stub
		System.out.println("At: ("+x+","+y+")");
		if(DRAG_ENABLED)
		{
			return this.currentTask.mousepressed(x, y, button);
		}	
		else
		{
			return super.mousepressed(x, y, button);
		}
		
	}
	@Override
	public boolean mousedragged(int currentx, int currenty, int oldx, int oldy) {
		// TODO Auto-generated method stub
		
		
		if(DRAG_ENABLED)
		{
			return this.currentTask.mousedragged(currentx, currenty, oldx, oldy);
		}	
		else
		{
			return super.mousedragged(currentx, currenty, oldx, oldy);
		}
		
	}
	@Override
	public boolean mousemoved(int x, int y) {
		// TODO Auto-generated method stub
		
		return super.mousemoved(x, y);
	}
}