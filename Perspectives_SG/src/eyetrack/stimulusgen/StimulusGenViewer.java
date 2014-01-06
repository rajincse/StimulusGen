package eyetrack.stimulusgen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import eyetrack.shapematch.AbstractBoundedShape;
import eyetrack.shapematch.AbstractShape;
import eyetrack.shapematch.OvalShape;

import perspectives.Property;
import perspectives.Viewer2D;
import perspectives.DefaultProperties.ColorPropertyType;
import perspectives.DefaultProperties.IntegerPropertyType;
import perspectives.DefaultProperties.PercentPropertyType;

public class StimulusGenViewer extends Viewer2D{
	public static final String PROPERTY_NAME_MIN_DISTANCE = "Distance.Minimum Distance";
	public static final String PROPERTY_NAME_MAX_DISTANCE = "Distance.Maximum Distance";
	public static final String PROPERTY_NAME_SIZE = "Size";
	public static final String PROPERTY_NAME_FORECOLOR = "Forecolor";
	public static final String PROPERTY_NAME_BACKGROUND_COLOR = "BackgroundColor";
	public static final String PROPERTY_NAME_OBJECT_COUNT = "Object Count";
	
	private ArrayList<AbstractBoundedShape> shapeList;
	
	
	public StimulusGenViewer(String name) {
		super(name);
		// TODO Auto-generated constructor stub
		this.loadProperties();
		
		this.shapeList = new ArrayList<AbstractBoundedShape>();
		positionLayout();
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
			
			Property<IntegerPropertyType> size = new Property<IntegerPropertyType>(PROPERTY_NAME_SIZE);
			size.setValue(new IntegerPropertyType(10));
			this.addProperty(size);
			
			Property<ColorPropertyType> forecolor = new Property<ColorPropertyType>(PROPERTY_NAME_FORECOLOR);
			forecolor.setValue(new ColorPropertyType(new Color(50,50,50)));
			this.addProperty(forecolor);
			
			Property<ColorPropertyType> backgroundColor = new Property<ColorPropertyType>(PROPERTY_NAME_BACKGROUND_COLOR);
			backgroundColor.setValue(new ColorPropertyType(new Color(250,250,250)));
			this.addProperty(backgroundColor);
			
			Property<IntegerPropertyType> objectCount = new Property<IntegerPropertyType>(PROPERTY_NAME_OBJECT_COUNT);
			objectCount.setValue(new IntegerPropertyType(10));
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
	private Color getPropertyColorValue(String name)
	{
		Property<ColorPropertyType> prop = this.getProperty(name);
		return prop.getValue().colorValue();
	}
	private void positionLayout()
	{
		int minDist = this.getPropertyIntValue(PROPERTY_NAME_MIN_DISTANCE);
		int maxDist = this.getPropertyIntValue(PROPERTY_NAME_MAX_DISTANCE);
		int size = this.getPropertyIntValue(PROPERTY_NAME_SIZE);
		Color color  = this.getPropertyColorValue(PROPERTY_NAME_FORECOLOR);
		int objectCount = this.getPropertyIntValue(PROPERTY_NAME_OBJECT_COUNT);
		StimulusGenPlotter plotter = this.getPlotter(objectCount, minDist, maxDist);
		this.shapeList.clear();
		for(int i=0;i<objectCount;i++)
		{
			Point p = plotter.getPosition(i);
			OvalShape circle = new OvalShape(p.x, p.y, size, size, color, true);
			this.shapeList.add(circle);
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
	public Color backgroundColor() {
		// TODO Auto-generated method stub
		Property<ColorPropertyType> backgroundColorProperty = this.getProperty(PROPERTY_NAME_BACKGROUND_COLOR);
		return backgroundColorProperty.getValue().colorValue();
	}

	@Override
	public void render(Graphics2D g) {
		// TODO Auto-generated method stub
		for(AbstractBoundedShape shape:this.shapeList)
		{
			shape.render(g);
		}
	}

	@Override
	public void simulate() {
		// TODO Auto-generated method stub
		
	}

}
