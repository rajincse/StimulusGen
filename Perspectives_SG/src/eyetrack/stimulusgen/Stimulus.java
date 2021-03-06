package eyetrack.stimulusgen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import eyetrack.shapematch.AbstractBoundedShape;
import eyetrack.shapematch.OvalShape;

public abstract class Stimulus {
	
	protected Point origin;
	protected double angle;
	protected ArrayList<AbstractBoundedShape> shapeList;

	public Stimulus()
	{
		this.origin = new Point(0,0);
		this.angle =0.0;
		this.shapeList = new ArrayList<AbstractBoundedShape>();
	}
	
	public Stimulus(Point origin,double angle)
	{
		this.origin =origin;
		this.angle =angle;
		this.shapeList = new ArrayList<AbstractBoundedShape>();
	}

	public Point getOrigin() {
		return origin;
	}

	public void setOrigin(Point origin) {
		this.origin = origin;
		this.updatePositions();
	}

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
		this.updatePositions();
	}

	public ArrayList<AbstractBoundedShape> getShapeList() {
		return shapeList;
	}

	public void setShapeList(ArrayList<AbstractBoundedShape> shapeList) {
		this.shapeList = shapeList;
	}
	public Point getTransformedPoint(Point p)
	{
		double radianAngle = this.angle * Math.PI / 180;
		int x =(int)( (double)this.origin.x+ (double)p.x *Math.cos(radianAngle)- (double) p.y * Math.sin(radianAngle) );
		int y =(int)( (double)this.origin.y+ (double)p.x *Math.sin(radianAngle)+ (double) p.y * Math.cos(radianAngle) );
		
		return new Point(x,y);
	}
	private void updatePositions()
	{
		for(AbstractBoundedShape shape:this.shapeList)
		{
			Point p = new Point (shape.getX(), shape.getY());
			p = this.getTransformedPoint(p);
			shape.setX(p.x);
			shape.setY(p.y);
		}
	}
	public abstract void render(Graphics2D g);
}
