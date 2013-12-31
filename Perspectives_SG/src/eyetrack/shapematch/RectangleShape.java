package eyetrack.shapematch;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.LinkedList;

public class RectangleShape extends AbstractBoundedShape {
	
	
	public RectangleShape(int x, int y, int width,int height, Color color, boolean fill)
	{
		super(x,y,width,height,color,fill);
	}
	
	
	
	@Override
	public String toString()
	{
		return "Rectangle("+this.x+","+this.y+","+this.width+","+this.height+")";
	}
	@Override
	void drawFilled(Graphics2D g) {
		// TODO Auto-generated method stub
		g.fillRect(x, y, width, height);
//		if(score >0)
//		{
//		
//		
//			g.setColor(invertedColor);
//			
//			g.drawString(String.format("%.2f", this.score),( x+width/2)-20, y+height/2);
//			
//		}
	}
	@Override
	void drawUnFilled(Graphics2D g) {
		// TODO Auto-generated method stub
		g.drawRect(x, y, width, height);
//		if(score >0)
//		{
//			g.setColor(Color.black);
//			g.drawString(String.format("%.2f", this.score),( x+width/2)-20, y+height/2);
//			
//		}
	}
	@Override
	void additionalRender(Graphics2D g) {
		// TODO Auto-generated method stub
		
	}



	@Override
	void selectionRender(Graphics2D g) {
		// TODO Auto-generated method stub
		g.setColor(Color.black);
		int selectionMargin = 5;
		g.drawRect(x-selectionMargin, y-selectionMargin, width+2*selectionMargin, height+2*selectionMargin);
		
	}
	
}
