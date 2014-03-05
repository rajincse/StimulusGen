package eyetrack.stimulusgen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Random;

import perspectives.Property;
import perspectives.Viewer2D;
import perspectives.DefaultProperties.IntegerPropertyType;
import util.SplineFactory;

public class CurveViewer extends Viewer2D{
	public static final String PROPERTY_NAME_MIN_DISTANCE = "Distance.Minimum Distance";
	public static final String PROPERTY_NAME_MAX_DISTANCE = "Distance.Maximum Distance";
	public static final String PROPERTY_NAME_OBJECT_COUNT = "Object Count";

	private int []curveX;
	private int []curveY;
	public CurveViewer(String name) {
		super(name);
		this.loadProperties();
		positionLayout();
		// TODO Auto-generated constructor stub
	}
	
	private void loadProperties()
	{
		try {
			Property<IntegerPropertyType> minDistance = new Property<IntegerPropertyType>(PROPERTY_NAME_MIN_DISTANCE);
			minDistance.setValue(new IntegerPropertyType(50));
			this.addProperty(minDistance);
			
			Property<IntegerPropertyType> maxDistance = new Property<IntegerPropertyType>(PROPERTY_NAME_MAX_DISTANCE);
			maxDistance.setValue(new IntegerPropertyType(100));
			this.addProperty(maxDistance);
			
			Property<IntegerPropertyType> objectCount = new Property<IntegerPropertyType>(PROPERTY_NAME_OBJECT_COUNT);
			objectCount.setValue(new IntegerPropertyType(15));
			this.addProperty(objectCount);
		}
		catch (Exception e) {		
			e.printStackTrace();
		}
	}
	private int getPropertyIntValue(String name)
	{
		Property<IntegerPropertyType> prop = this.getProperty(name);
		return prop.getValue().intValue();
	}
	
	private void positionLayout()
	{
		
		int minDist = this.getPropertyIntValue(PROPERTY_NAME_MIN_DISTANCE);
		int maxDist = this.getPropertyIntValue(PROPERTY_NAME_MAX_DISTANCE);
		int objectCount = this.getPropertyIntValue(PROPERTY_NAME_OBJECT_COUNT);
		
		StimulusGenPlotter plotter = this.getPlotter(objectCount, minDist, maxDist);
		this.createCurveXY(plotter);
	}
	
	private void createCurveXY(StimulusGenPlotter plotter)
	{
	
		int controlLength=(plotter.getObjectCount())*3;
		double[] control=new double[controlLength];
		int minX =0;
		int maxX = 1200;
		int x=minX;
		int step =( maxX - minX )/ plotter.getObjectCount();
		int j=0;
		Point p=null;
		for(int i=0;i<controlLength;i=i+3){
			
			if(j<plotter.getObjectCount()){
				p =plotter.getPosition(j);
			}
			x+= step;
			control[i]=x;
			control[i+1]=p.y;
			control[i+2]=0;	
			j++;
		}
		double[] spline = SplineFactory.createCatmullRom(control, 10);
		curveX = new int[spline.length/3];
		curveY = new int[spline.length/3];
		
		System.out.println("Start");
		for (int k=0; k<curveX.length; k++)
		{
			curveX[k] = (int)spline[3*k];
			curveY[k] = (int)spline[3*k+1];
			System.out.println("<Point x=\""+curveX[k]+"\" y=\""+curveY[k]+"\" />");
		}
	}
			
	private StimulusGenPlotter getPlotter(int objectCount, int minDistance, int maxDistance)
	{
		StimulusGenPlotter plotter = new RadialDistancePlotter(objectCount, minDistance, maxDistance);
		
		return plotter;
	}		
	public <T extends perspectives.PropertyType> void propertyUpdated(Property p, T newvalue)
	{
		positionLayout();
	}
	@Override
	public void render(Graphics2D g) {
		// TODO Auto-generated method stub
		
		g.setColor(Color.blue);
		if(this.curveX != null && this.curveY != null)
		{
			Point p1 = new Point(this.curveX[0], this.curveY[0]);
			Point p2 = null;
			for(int i=1;i<this.curveX.length;i++)
			{
				p2 = new Point(this.curveX[i],this.curveY[i]);
				g.drawLine(p1.x, p1.y, p2.x, p2.y);
				p1 = p2;
			}
		}
		
	}

	@Override
	public void simulate() {
		// TODO Auto-generated method stub
		
	}

}
