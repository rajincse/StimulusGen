package eyetrack.stimulusgen;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Random;

import perspectives.DefaultProperties.ColorPropertyType;
import perspectives.DefaultProperties.DoublePropertyType;
import perspectives.Property;
import perspectives.Viewer2D;
import perspectives.DefaultProperties.IntegerPropertyType;
import util.SplineFactory;
import util.Util;

public class CurveViewer extends Viewer2D{
	public static final String PROPERTY_NAME_MIN_DISTANCE = "Distance.Minimum Distance";
	public static final String PROPERTY_NAME_MAX_DISTANCE = "Distance.Maximum Distance";
	public static final String PROPERTY_NAME_CONTROL_POINT_COUNT = "Control Point Count";
	public static final String PROPERTY_NAME_CURVE_COUNT = "Curve Count";
	
	public static final String PROPERTY_NAME_COLOR1 = "Color1";
	public static final String PROPERTY_NAME_COLOR2 = "Color2";
	public static final String PROPERTY_NAME_SEGMENT1 = "Segment1";
	public static final String PROPERTY_NAME_SEGMENT2 = "Segment2";
	
	
	private CurveShape[] curves;
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
			
			Property<IntegerPropertyType> controlPointCount = new Property<IntegerPropertyType>(PROPERTY_NAME_CONTROL_POINT_COUNT);
			controlPointCount.setValue(new IntegerPropertyType(5));
			this.addProperty(controlPointCount);
			
			Property<IntegerPropertyType> curveCount = new Property<IntegerPropertyType>(PROPERTY_NAME_CURVE_COUNT);
			curveCount.setValue(new IntegerPropertyType(2));
			this.addProperty(curveCount);
			
			Property<ColorPropertyType> color1 = new Property<ColorPropertyType>(PROPERTY_NAME_COLOR1);
			color1.setValue(new ColorPropertyType(Color.blue));
			this.addProperty(color1);
			
			Property<ColorPropertyType> color2 = new Property<ColorPropertyType>(PROPERTY_NAME_COLOR2);
			color2.setValue(new ColorPropertyType(Color.gray));
			this.addProperty(color2);
			
			Property<DoublePropertyType> segment1 = new Property<DoublePropertyType>(PROPERTY_NAME_SEGMENT1);
			segment1.setValue(new DoublePropertyType(0.5));
			this.addProperty(segment1);
			
			Property<DoublePropertyType> segment2 = new Property<DoublePropertyType>(PROPERTY_NAME_SEGMENT2);
			segment2.setValue(new DoublePropertyType(0.8));
			this.addProperty(segment2);
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
	
	private double getPropertyDoubleValue(String name)
	{
		Property<DoublePropertyType> prop = this.getProperty(name);
		return prop.getValue().doubleValue();
	}
	
	private Color getPropertyColorValue(String name)
	{
		Property<ColorPropertyType> prop = this.getProperty(name);
		return prop.getValue().colorValue();
	}
	
	private void positionLayout()
	{
		
		int minDist = this.getPropertyIntValue(PROPERTY_NAME_MIN_DISTANCE);
		int maxDist = this.getPropertyIntValue(PROPERTY_NAME_MAX_DISTANCE);
		int controlPointCount = this.getPropertyIntValue(PROPERTY_NAME_CONTROL_POINT_COUNT);
		int curveCount = this.getPropertyIntValue(PROPERTY_NAME_CURVE_COUNT);
		Color color1=this.getPropertyColorValue(PROPERTY_NAME_COLOR1);
		Color color2=this.getPropertyColorValue(PROPERTY_NAME_COLOR2);
		double segment1=this.getPropertyDoubleValue(PROPERTY_NAME_SEGMENT1);
		double segment2=this.getPropertyDoubleValue(PROPERTY_NAME_SEGMENT2);
		
		this.curves = new CurveShape[curveCount];
		Random random = new Random();
		int selectionIndex = (int) Math.abs(random.nextInt()) % curveCount;
		for(int i=0;i<this.curves.length;i++)
		{
			StimulusGenPlotter plotter = this.getPlotter(controlPointCount, minDist, maxDist);
			Point[] curvePoints = this.createCurveXY(plotter);
			boolean selection = false;
			if(i == selectionIndex)
			{
				selection = true;
			}
			CurveShape curve = new CurveShape(curvePoints, color1, color2, segment1, segment2, selection);
			this.curves[i] = curve;
		}
		
	}
	
	private Point[] createCurveXY(StimulusGenPlotter plotter)
	{
		
		int controlLength=(plotter.getObjectCount())*3;
		double[] control=new double[controlLength];
		int minX =0;
		int maxX = 1200;
		int x=minX;
		int step =( maxX - minX )/ plotter.getObjectCount();
		int j=1;
		Point p=null;
		//Randomize the first point
		Random random= new Random();
		x+= step;
		control[0]=x;
		control[1]=random.nextInt()%300;
		control[2]=0;
		for(int i=3;i<controlLength;i=i+3){
			
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
		Point[] curvePoints = new Point[spline.length/3];
		
		for (int k=0; k<curvePoints.length; k++)
		{
			curvePoints[k] = new Point((int)spline[3*k], (int)spline[3*k+1]);
//			System.out.println("<Point x=\""+curveX[k]+"\" y=\""+curveY[k]+"\" />");
		}
		
		return curvePoints;
	}
			
	private StimulusGenPlotter getPlotter(int objectCount, int minDistance, int maxDistance)
	{
		StimulusGenPlotter plotter = new RadialDistancePlotter(objectCount, minDistance, maxDistance);
		
		return plotter;
	}		
	public <T extends perspectives.PropertyType> void propertyUpdated(Property p, T newvalue)
	{
		positionLayout();
		if(p.getName()== PROPERTY_NAME_SEGMENT1)
		{
			
			double newValue = ((DoublePropertyType) newvalue).doubleValue();
			double segment2Val = this.getPropertyDoubleValue(PROPERTY_NAME_SEGMENT2);
			if(newValue < 0)
			{
				DoublePropertyType val = new DoublePropertyType(0.0);
				((Property<DoublePropertyType>) p).setValue(val);
			}
			else if(newValue > 1)
			{
				DoublePropertyType val = new DoublePropertyType(1.0);
				((Property<DoublePropertyType>) p).setValue(val);
			}
			else if(newValue >segment2Val)
			{
				DoublePropertyType val = new DoublePropertyType(0.0);
				((Property<DoublePropertyType>) p).setValue(val);
			}
			
			
		}
		else if(p.getName()== PROPERTY_NAME_SEGMENT2)
		{
			double newValue = ((DoublePropertyType) newvalue).doubleValue();
			double segment1Val = this.getPropertyDoubleValue(PROPERTY_NAME_SEGMENT1);
			if(newValue < 0)
			{
				DoublePropertyType val = new DoublePropertyType(0.0);
				((Property<DoublePropertyType>) p).setValue(val);
			}
			else if(newValue > 1)
			{
				DoublePropertyType val = new DoublePropertyType(1.0);
				((Property<DoublePropertyType>) p).setValue(val);
			}
			else if(newValue < segment1Val)
			{
				DoublePropertyType val = new DoublePropertyType(1.0);
				((Property<DoublePropertyType>) p).setValue(val);
			}
		}
	}
	
	
	@Override
	public void render(Graphics2D g) {
		// TODO Auto-generated method stub
		if(this.curves != null)
		{
			for(int i=0;i< this.curves.length;i++)
			{	
				if(this.curves[i] != null)
				{
					this.curves[i].render(g);
				}
				
			}
		}
		
		
		
	}

	@Override
	public void simulate() {
		// TODO Auto-generated method stub
		
	}

}
