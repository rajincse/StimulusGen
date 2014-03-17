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
	public static final String PROPERTY_NAME_OBJECT_COUNT = "Object Count";
	
	public static final String PROPERTY_NAME_COLOR1 = "Color1";
	public static final String PROPERTY_NAME_COLOR2 = "Color2";
	public static final String PROPERTY_NAME_SEGMENT1 = "Segment1";
	public static final String PROPERTY_NAME_SEGMENT2 = "Segment2";
	
	

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
		
		
		if(this.curveX != null && this.curveY != null)
		{
			Point p1 = new Point(this.curveX[0], this.curveY[0]);
			Point p2 = null;
			g.setColor(Color.red);
			g.fillOval((p1.x)-5, (p1.y)-5, 10, 10);
			Font font = new Font("Verdana", Font.BOLD, 16);
			g.setFont(font);
			g.drawString("A", (p1.x)-10, (p1.y)-10);
			Color color1 = this.getPropertyColorValue(PROPERTY_NAME_COLOR1);
			Color color2 = this.getPropertyColorValue(PROPERTY_NAME_COLOR2);

			double segment1 = this.getPropertyDoubleValue(PROPERTY_NAME_SEGMENT1);
			double segment2 = this.getPropertyDoubleValue(PROPERTY_NAME_SEGMENT2);
			
			int firstSegment = (int) ( this.curveX.length * segment1);
			int secondSegment = (int) ( this.curveX.length * segment2);
			
			for(int i=1;i<this.curveX.length;i++)
			{
				if(i< firstSegment)
				{
					g.setColor(color1);
				}
				else if(i> secondSegment)
				{
					g.setColor(color2);
				}
				else
				{
					int m = i-firstSegment;
					int n= secondSegment-i;

					int r = (n * color1.getRed()+ m*color2.getRed())/(m+n);
					int gr = (n * color1.getGreen()+ m*color2.getGreen())/(m+n);
					int b = (n * color1.getBlue()+ m*color2.getBlue())/(m+n);
					Color color3= new Color(r,gr,b);
					g.setColor(color3);
					
				}
				p2 = new Point(this.curveX[i],this.curveY[i]);
				g.drawLine(p1.x, p1.y, p2.x, p2.y);
				p1 = p2;
			}
			g.setColor(Color.red);
			g.fillOval((p2.x)-5, (p2.y)-5, 10, 10);
			g.setFont(font);
			g.drawString("B", (p2.x)+10, (p2.y)-10);
		}
		
	}

	@Override
	public void simulate() {
		// TODO Auto-generated method stub
		
	}

}
