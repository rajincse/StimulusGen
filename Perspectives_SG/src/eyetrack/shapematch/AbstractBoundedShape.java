package eyetrack.shapematch;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public abstract class AbstractBoundedShape extends AbstractFillableShape{
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	
	public AbstractBoundedShape(int x, int y, int width,int height, Color color, boolean fill)
	{
		super(color,fill);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	@Override
	public double getDistance(Point p) {
		// TODO Auto-generated method stub
		double avgRadius = (1.0 * width/2+1.0 * height/2)/2;
		double distance = Point.distance(p.x, p.y, x+width/2, y+height/2);
		if(distance > avgRadius)
		{
			distance = distance -avgRadius;
		}
		else
		{
			distance = avgRadius - distance;
		}
		return distance;
	}
	
	@Override
	public boolean mousedragged(int currentx, int currenty, int oldx, int oldy) {
		if(isInShape(oldx, oldy) & this.isSelected)
		{
			int dx = currentx-oldx;
			int dy = currenty-oldy;
			this.x+= dx;
			this.y+=dy;
			
			
			
			return true;
		}
		else 
		{
			return false;
		}
		
		
	}
	@Override
	public boolean mousepressed(int x, int y, int button) {
		if(isInShape(x, y) & this.isDraggable)
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
		if(x >= this.x && y>=this.y && x<=this.x+this.width && y<=this.y+height)
		{	
			return true;
		}
		else
		{
			return false;
		}
	}
	
}
