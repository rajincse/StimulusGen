package eyetrack.shapematch;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import eyetrack.shapematch.rule.NearestDistanceRule;

import perspectives.DefaultProperties.OpenFilePropertyType;
import perspectives.Property;
import perspectives.PropertyType;
import perspectives.Viewer2D;
import util.SplineFactory;
import util.Points2DViewer.PointAspectType;

public class ShapeViewer extends Viewer2D {
	public static final int GAZE_LIST_THRESHOLD =10;
	public static final boolean DRAG_ENABLED = true;
	
	private ArrayList<AbstractShape> shapeList ;
	private LinkedList<Point> gazeList;
	
	public ShapeViewer(String name) {
		super(name);
		this.shapeList = new ArrayList<AbstractShape>();
		this.gazeList = new LinkedList<Point>();
		this.fiilupProperties();
		
		// TODO Auto-generated constructor stub
	}
	
	private void addLocalRules()
	{
		NearestDistanceRule nearestDistanceRule = new NearestDistanceRule();
		for(AbstractShape shape: shapeList)
		{
			shape.addLocalRule(nearestDistanceRule);
		}
	}
	
	private void addGlobalRules()
	{
		
	}
	protected void fiilupProperties()
	{
		try
		{
			OpenFilePropertyType ff = new OpenFilePropertyType();
			Property<OpenFilePropertyType> p33 = new Property<OpenFilePropertyType>("Load Shape");
			p33.setValue(ff);
			this.addProperty(p33);
		}
		catch (Exception e) {		
			e.printStackTrace();
		}
	}
	private void loadShapes(File f)
			throws SAXException, IOException, ParserConfigurationException
	{
		this.shapeList.clear();
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(f);
		doc.getDocumentElement().normalize();
		
		NodeList ovalShapes = doc.getElementsByTagName("Oval");
		for(int i=0;i<ovalShapes.getLength();i++)
		{
			Node cNode = ovalShapes.item(i);
			if(cNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element element = (Element) cNode;
				int x = Integer.parseInt( element.getAttribute("x"));
				int y = Integer.parseInt( element.getAttribute("y"));
				int width = Integer.parseInt( element.getAttribute("width"));
				int height = Integer.parseInt( element.getAttribute("height"));
				Color color = new Color((int)Long.parseLong(element.getAttribute("color"),16));
				boolean fill = element.getAttribute("fill").toUpperCase().contains("Y");
				
				OvalShape oval = new OvalShape(x, y, width, height, color, fill);
				if(element.hasAttribute("draggable"))
				{
					boolean isDraggable = element.getAttribute("draggable").toUpperCase().contains("Y");
					oval.setDraggable(isDraggable);
				}
				this.shapeList.add(oval);
			}
		}
		
		NodeList rectShapes = doc.getElementsByTagName("Rectangle");
		
		for(int i=0;i<rectShapes.getLength();i++)
		{
			Node rNode = rectShapes.item(i);
			if(rNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element element = (Element) rNode;
				int x = Integer.parseInt( element.getAttribute("x"));
				int y = Integer.parseInt( element.getAttribute("y"));
				int width = Integer.parseInt( element.getAttribute("width"));
				int height = Integer.parseInt( element.getAttribute("height"));
				Color color = new Color((int)Long.parseLong(element.getAttribute("color"),16));
				boolean fill = element.getAttribute("fill").toUpperCase().contains("Y");
				RectangleShape rect = new RectangleShape(x, y, width, height, color, fill);
				if(element.hasAttribute("draggable"))
				{
					boolean isDraggable = element.getAttribute("draggable").toUpperCase().contains("Y");
					rect.setDraggable(isDraggable);
				}
				this.shapeList.add(rect);
			}
		}	
		
		NodeList lineShapes = doc.getElementsByTagName("Line");
		
		for(int i=0;i<lineShapes.getLength();i++)
		{
			Node lNode = lineShapes.item(i);
			if(lNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element element = (Element) lNode;
				int x1 = Integer.parseInt( element.getAttribute("x1"));
				int y1 = Integer.parseInt( element.getAttribute("y1"));
				int x2 = Integer.parseInt( element.getAttribute("x2"));
				int y2 = Integer.parseInt( element.getAttribute("y2"));
				Color color = new Color((int)Long.parseLong(element.getAttribute("color"),16));
				LineShape line = new LineShape(x1, y1, x2, y2, color);
				if(element.hasAttribute("draggable"))
				{
					boolean isDraggable = element.getAttribute("draggable").toUpperCase().contains("Y");
					line.setDraggable(isDraggable);
				}
				this.shapeList.add(line);
			}
		}
		
		NodeList polylineShapes = doc.getElementsByTagName("Polyline");
		for(int i=0;i<polylineShapes.getLength();i++)
		{
			Node pNode = polylineShapes.item(i);
			if(pNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element pElement = (Element) pNode;
				Color color = new Color((int)Long.parseLong(pElement.getAttribute("color"),16));
				
				NodeList points = pElement.getElementsByTagName("Point");
				
				int []xPoints = new int[points.getLength()];
				int []yPoints = new int[points.getLength()];
				
				for(int j=0;j<points.getLength();j++)
				{
					Element pointElement  = (Element) points.item(j);
					xPoints[j] =  Integer.parseInt( pointElement.getAttribute("x"));
					yPoints[j] =  Integer.parseInt( pointElement.getAttribute("y"));
				}
				PolylineShape polyline = new PolylineShape(xPoints, yPoints, color);
				if(pElement.hasAttribute("draggable"))
				{
					boolean isDraggable = pElement.getAttribute("draggable").toUpperCase().contains("Y");
					polyline.setDraggable(isDraggable);
				}
				this.shapeList.add(polyline);
			}
		}
		
		NodeList textShapes = doc.getElementsByTagName("Text");
		for(int i=0;i<textShapes.getLength();i++)
		{
			Node tNode = textShapes.item(i);
			if(tNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element tElement = (Element) tNode;
				Color color = new Color((int)Long.parseLong(tElement.getAttribute("color"),16));
				int x = Integer.parseInt( tElement.getAttribute("x"));
				int y = Integer.parseInt( tElement.getAttribute("y"));
				String fontName = tElement.getAttribute("fontName");
				int size = Integer.parseInt( tElement.getAttribute("fontSize"));
				String text = tElement.getAttribute("text");
				
				Font font = new Font(fontName, Font.PLAIN, size);
				TextShape textShape = new TextShape(color, x, y, font, text);
				if(tElement.hasAttribute("draggable"))
				{
					boolean isDraggable = tElement.getAttribute("draggable").toUpperCase().contains("Y");
					textShape.setDraggable(isDraggable);
				}
				this.shapeList.add(textShape);
			}
		}
		this.addLocalRules();
	}
	
	public <T extends PropertyType> void propertyUpdated(Property p, T newvalue)
	{
		if (p.getName() == "Load Shape")
		{
						
			try{
			  File f =new File( ((OpenFilePropertyType)newvalue).path);
			  this.loadShapes(f);
			  
			  
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	@Override
	public void render(Graphics2D g) {
		// TODO Auto-generated method stub
		for(AbstractShape shape: this.shapeList)
		{
			shape.render(g);
		}
		
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
	private void processGaze(int x, int y)
	{
		if(this.gazeList.size() == GAZE_LIST_THRESHOLD)
		{
			this.gazeList.remove();
		}
		this.gazeList.add(new Point(x,y));
		
		for(AbstractShape shape:this.shapeList)
		{
			shape.applyLocalrules(this.gazeList);
		}
	}

	@Override
	public boolean mousepressed(int x, int y, int button) {
		// TODO Auto-generated method stub
		System.out.println("At: ("+x+","+y+")");
		if(DRAG_ENABLED)
		{
			boolean handled=false;
			for(AbstractShape shape:this.shapeList)
			{
			
				handled = handled || shape.mousepressed(x, y, button);
			
			}
			if(handled)
			{
				this.gazeList.clear();
				for(int i=0;i<15;i++)
				{
					this.processGaze(x, y);
				}
			}
			return handled;
		}	
		else
		{
			this.processGaze(x, y);
			
			return super.mousepressed(x, y, button);
		}
		
	}
	@Override
	public boolean mousedragged(int currentx, int currenty, int oldx, int oldy) {
		// TODO Auto-generated method stub
		
		
		if(DRAG_ENABLED)
		{
			boolean handled=false;
			for(AbstractShape shape:this.shapeList)
			{
			
				handled = handled || shape.mousedragged(currentx, currenty, oldx, oldy);
			
			}
			if(handled)
			{
				this.gazeList.clear();
				for(int i=0;i<15;i++)
				{
					this.processGaze(currentx, currenty);
				}
			}
			return handled;
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
