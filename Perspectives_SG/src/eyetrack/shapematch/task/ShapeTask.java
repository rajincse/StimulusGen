package eyetrack.shapematch.task;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eyetrack.shapematch.AbstractShape;
import eyetrack.shapematch.LineShape;
import eyetrack.shapematch.OvalShape;
import eyetrack.shapematch.PolylineShape;
import eyetrack.shapematch.RectangleShape;
import eyetrack.shapematch.TextShape;

public class ShapeTask {
	private ArrayList<AbstractShape> shapeList;
	

	private boolean isDraggingEnabled;
	
	public ShapeTask(boolean isDraggingEnabled)
	{
		this.isDraggingEnabled = isDraggingEnabled;
	}
	
	public ShapeTask(Element taskElement)
	{
		this.shapeList = new ArrayList<AbstractShape>();
		this.isDraggingEnabled = taskElement.getAttribute("DraggingEnabled").toUpperCase().contains("Y");
		NodeList ovalShapes = taskElement.getElementsByTagName("Oval");
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
				shapeList.add(oval);
			}
		}
		
		NodeList rectShapes = taskElement.getElementsByTagName("Rectangle");
		
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
				shapeList.add(rect);
			}
		}	
		
		NodeList lineShapes = taskElement.getElementsByTagName("Line");
		
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
				shapeList.add(line);
			}
		}
		
		NodeList polylineShapes = taskElement.getElementsByTagName("Polyline");
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
				shapeList.add(polyline);
			}
		}
		
		NodeList textShapes = taskElement.getElementsByTagName("Text");
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
				shapeList.add(textShape);
			}
		}
	}
	public ArrayList<AbstractShape> getShapeList() {
		return shapeList;
	}

	public void setShapeList(ArrayList<AbstractShape> shapeList) {
		this.shapeList = shapeList;
	}
	public void render(Graphics2D g)
	{
		for(AbstractShape shape:this.shapeList)
		{
			shape.render(g);
		}
	}
	public boolean mousepressed(int x, int y, int button) {
		// TODO Auto-generated method stub
		if(this.isDraggingEnabled)
		{
			boolean handled=false;
			for(AbstractShape shape: shapeList)
			{
			
				handled = handled || shape.mousepressed(x, y, button);
			
			}
			
			return handled;
		}	
		else
		{
			return false;
		}
		
	}
	public boolean mousedragged(int currentx, int currenty, int oldx, int oldy) {
		// TODO Auto-generated method stub
		
		
		if(this.isDraggingEnabled)
		{
			boolean handled=false;
			for(AbstractShape shape: shapeList)
			{
			
				handled = handled || shape.mousedragged(currentx, currenty, oldx, oldy);
			
			}
			
			return handled;
		}	
		else
		{
			return  false;
		}
		
	}
}
