package eyetrack.stimulusgen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import perspectives.Property;
import perspectives.Viewer2D;
import perspectives.DefaultProperties.ColorPropertyType;
import perspectives.DefaultProperties.IntegerPropertyType;

public class SineWaveStimulusViewer extends Viewer2D{
	public static final String PROPERTY_NAME_MIN_DISTANCE = "Distance.Minimum Distance";
	public static final String PROPERTY_NAME_MAX_DISTANCE = "Distance.Maximum Distance";
	
	public static final String PROPERTY_NAME_SIZE = "Size";
	public static final String PROPERTY_NAME_FORECOLOR = "Forecolor";
	public static final String PROPERTY_NAME_COLOR_DIFFERENCE = "ColorDifference";	
	public static final String PROPERTY_NAME_OBJECT_COUNT = "Object Count";
	
	private Stimulus stimuls;
	private LabColorGenerator[] labs ;

	public SineWaveStimulusViewer(String name) {
		super(name);
		this.stimuls =null;
		this.labs = LabColorGenerator.generate(10);
		this.loadProperties();
		this.positionLayout();
	}

	private void loadProperties()
	{
		try {
			Property<IntegerPropertyType> minDistance = new Property<IntegerPropertyType>(PROPERTY_NAME_MIN_DISTANCE);
			minDistance.setValue(new IntegerPropertyType(200));
			this.addProperty(minDistance);
			
			Property<IntegerPropertyType> maxDistance = new Property<IntegerPropertyType>(PROPERTY_NAME_MAX_DISTANCE);
			maxDistance.setValue(new IntegerPropertyType(700));
			this.addProperty(maxDistance);
			
			
			Property<IntegerPropertyType> size = new Property<IntegerPropertyType>(PROPERTY_NAME_SIZE);
			size.setValue(new IntegerPropertyType(1));
			this.addProperty(size);
			
			Property<ColorPropertyType> forecolor = new Property<ColorPropertyType>(PROPERTY_NAME_FORECOLOR);
			forecolor.setValue(new ColorPropertyType(this.labs[0].getColor()));
			this.addProperty(forecolor);
			
			
			
			Property<IntegerPropertyType> colorDifference = new Property<IntegerPropertyType>(PROPERTY_NAME_COLOR_DIFFERENCE);
			colorDifference.setValue(new IntegerPropertyType(7));
			this.addProperty(colorDifference);
			
			
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
		this.stimuls = SineWaveStimulus.createStimulus(new Point(150,100),30,plotter, size, color);
		
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
		Property<IntegerPropertyType> colorDiff = this.getProperty(PROPERTY_NAME_COLOR_DIFFERENCE);
		Color c = labs[colorDiff.getValue().intValue() % labs.length].getColor();
		
		return c;
	}

	@Override
	public void render(Graphics2D g) {
		// TODO Auto-generated method stub
		
		if(stimuls != null)			
		{
			stimuls.render(g);
		}
		
	}

	@Override
	public void simulate() {
		// TODO Auto-generated method stub
		
	}

}
