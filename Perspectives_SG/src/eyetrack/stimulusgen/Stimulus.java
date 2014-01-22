package eyetrack.stimulusgen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import eyetrack.shapematch.AbstractBoundedShape;
import eyetrack.shapematch.OvalShape;

public class Stimulus {
	
	private Point origin;
	private double angle;
	private ArrayList<AbstractBoundedShape> shapeList;

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
	public static Stimulus createStimulus(StimulusGenPlotter plotter, int objectSize, Color objectColor)
	{
		Stimulus stimulus = new Stimulus();
		stimulus.getShapeList().clear();
		int objectCount = plotter.getObjectCount();
		for(int i=0;i<objectCount;i++)
		{
			Point p = stimulus.getTransformedPoint(plotter.getPosition(i));
			OvalShape circle = new OvalShape(p.x, p.y, objectSize, objectSize, objectColor, true);			
			stimulus.getShapeList().add(circle);
		}
		
		return stimulus;
	}
	public static Stimulus createStimulus(Point origin,double angle, StimulusGenPlotter plotter, int objectSize, Color objectColor)
	{
		Stimulus stimulus = new Stimulus(origin, angle);
		stimulus.getShapeList().clear();
		int objectCount = plotter.getObjectCount();
		for(int i=0;i<objectCount;i++)
		{
			Point p = stimulus.getTransformedPoint(plotter.getPosition(i));
			OvalShape circle = new OvalShape(p.x, p.y, objectSize, objectSize, objectColor, true);			
			stimulus.getShapeList().add(circle);
		}
		
		return stimulus;
	}
	
	public void render(Graphics2D g)
	{
		for(AbstractBoundedShape shape:this.shapeList)
		{
			shape.render(g);
		}
	}
}
