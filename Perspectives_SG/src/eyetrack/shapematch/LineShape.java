package eyetrack.shapematch;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.LinkedList;

public class LineShape extends AbstractShape {

	private int x1;
	private int y1;
	
	private int x2;
	private int y2;
	
	public LineShape(int x1, int y1, int x2, int y2, Color color)
	{
		super(color);
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		
		
	}
	@Override
	public void render(Graphics2D g) {
		// TODO Auto-generated method stub
	
		if(this.isSelected)
		{
			g.setColor(this.invertedColor);
		}
		else
		{
			g.setColor(color);
		}
		
	
		
		g.drawLine(x1, y1, x2, y2);
//		if(score >0)
//		{
//			g.setColor(Color.black);
//			g.drawString(String.format("%.2f", this.score), (3*x1+x2)/4 -20, (3*y1+y2)/4);
//			
//		}
	}
	@Override
	public double getDistance(Point p) {
		// TODO Auto-generated method stub
		double distance =Math.abs( (y1-y2)*p.x+(x2-x1)*p.y+x1*y2-x2*y1 )/ Point.distance(x1, y1, x2, y2);
		return distance;
	}
	
	@Override
	public boolean mousedragged(int currentx, int currenty, int oldx, int oldy) {
		// TODO Auto-generated method stub
		if(isInShape(oldx, oldy) & this.isSelected)
		{
			
			int dx = currentx-oldx;
			int dy = currenty-oldy;
			this.x1+= dx;
			this.y1+=dy;
			this.x2+= dx;
			this.y2+=dy;
			
			
			return true;
		}
		else 
		{
			return false;
		}
	}
	@Override
	public boolean mousepressed(int x, int y, int button) {
		if(isInShape(x, y)& this.isDraggable)
		{
			this.isSelected = true;
			return true;
		}
		else
		{
			this.isSelected = false;
			return false;
		}
	}
	@Override
	public boolean isInShape(int x, int y) {
		Point p = new Point(x,y);
		if(this.getDistance(p) < 5)
		{	
			return true;
		}
		else
		{
			return false;
		}
	}
	
}
