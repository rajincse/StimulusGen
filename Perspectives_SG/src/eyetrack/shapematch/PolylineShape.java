package eyetrack.shapematch;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

public class PolylineShape extends AbstractShape {
	private int xPoints[];
	private int yPoints[];

	
	public PolylineShape(int xPoints[],int yPoints[],Color color )
	{
		super(color);
		this.xPoints= xPoints;
		this.yPoints = yPoints;
		
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
		
		g.drawPolyline(xPoints, yPoints, xPoints.length);
//		if(score >0)
//		{
//			g.setColor(Color.black);
//			int midIndex = xPoints.length/2-1;
//			g.drawString(String.format("%.2f", this.score), xPoints[midIndex] -20, yPoints[midIndex]);
//			
//		}
	}
	

	@Override
	public String toString()
	{
		return "Polyline("+this.xPoints[0]+","+this.yPoints[0]+","+this.xPoints[this.xPoints.length-1]+","+this.yPoints[this.yPoints.length-1]+")";
	}

	@Override
	public double getDistance(Point p) {
		// TODO Auto-generated method stub
		double minDistance = Integer.MAX_VALUE;
		double tempDistance =0;
		for(int i=0;i<this.xPoints.length;i++)
		{
			tempDistance = Point.distance(p.x, p.y, this.xPoints[i], this.yPoints[i]);
			if(tempDistance < minDistance)
			{
				minDistance = tempDistance;
			}
		}
		return minDistance;
	}
	@Override
	public boolean mousedragged(int currentx, int currenty, int oldx, int oldy) {
		if(isInShape(oldx, oldy) & this.isSelected)
		{
			
			int dx = currentx-oldx;
			int dy = currenty-oldy;
			for(int i=0;i<this.xPoints.length;i++)
			{
				this.xPoints[i]+= dx;
				this.yPoints[i]+= dy;
			}
			
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
