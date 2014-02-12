package eyetrack.stimulusgen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import perspectives.DefaultProperties.DoublePropertyType;
import perspectives.Property;
import perspectives.Viewer2D;
import perspectives.DefaultProperties.ColorPropertyType;
import perspectives.DefaultProperties.IntegerPropertyType;

public class SineWaveStimulusViewer extends Viewer2D{
	public static final String PROPERTY_NAME_MIN_DISTANCE = "Distance.Minimum Distance";
	public static final String PROPERTY_NAME_MAX_DISTANCE = "Distance.Maximum Distance";	
	public static final String PROPERTY_NAME_MIN_AMPLITUDE = "Amplitude.Minimum";
	public static final String PROPERTY_NAME_MAX_AMPLITUDE = "Amplitude.Maximum";	
	public static final String PROPERTY_NAME_MIN_FREQUENCY = "Frquency.Minimum";
	public static final String PROPERTY_NAME_MAX_FREQUENCY = "Frquency.Maximum";
	public static final String PROPERTY_NAME_MIN_ROTATION = "Rotation.Minimum";
	public static final String PROPERTY_NAME_MAX_ROTATION = "Rotation.Maximum";
	
	
	public static final String PROPERTY_NAME_FORECOLOR = "Forecolor";
	public static final String PROPERTY_NAME_COLOR_DIFFERENCE = "ColorDifference";	
	public static final String PROPERTY_NAME_OBJECT_COUNT = "Object Count";
	
	private Stimulus stimuls;
	private LabColorGenerator[] labs ;

	public SineWaveStimulusViewer(String name) {
		super(name);
		this.stimuls =null;
		this.labs = LabColorGenerator.generate(20);
		this.loadProperties();
		this.positionLayout();
	}

	private void loadProperties()
	{
		try {
			Property<IntegerPropertyType> minDistance = new Property<IntegerPropertyType>(PROPERTY_NAME_MIN_DISTANCE);
			minDistance.setValue(new IntegerPropertyType(500));
			this.addProperty(minDistance);
			
			Property<IntegerPropertyType> maxDistance = new Property<IntegerPropertyType>(PROPERTY_NAME_MAX_DISTANCE);
			maxDistance.setValue(new IntegerPropertyType(700));
			this.addProperty(maxDistance);
			
			Property<DoublePropertyType> minAmplitude = new Property<DoublePropertyType>(PROPERTY_NAME_MIN_AMPLITUDE);
			minAmplitude.setValue(new DoublePropertyType(0.1));
			this.addProperty(minAmplitude);
			
			Property<DoublePropertyType> maxAmplitude = new Property<DoublePropertyType>(PROPERTY_NAME_MAX_AMPLITUDE);
			maxAmplitude.setValue(new DoublePropertyType(0.5));
			this.addProperty(maxAmplitude);			
			
			Property<DoublePropertyType> minPeriod = new Property<DoublePropertyType>(PROPERTY_NAME_MIN_FREQUENCY);
			minPeriod.setValue(new DoublePropertyType(0.2));
			this.addProperty(minPeriod);
			
			Property<DoublePropertyType> maxPeriod = new Property<DoublePropertyType>(PROPERTY_NAME_MAX_FREQUENCY);
			maxPeriod.setValue(new DoublePropertyType(0.4));
			this.addProperty(maxPeriod);
			
			Property<DoublePropertyType> minRotation = new Property<DoublePropertyType>(PROPERTY_NAME_MIN_ROTATION);
			minRotation.setValue(new DoublePropertyType(0.2));
			this.addProperty(minRotation);
			
			Property<DoublePropertyType> maxRotation = new Property<DoublePropertyType>(PROPERTY_NAME_MAX_ROTATION);
			maxRotation.setValue(new DoublePropertyType(0.5));
			this.addProperty(maxRotation);	
			
			
			Property<ColorPropertyType> forecolor = new Property<ColorPropertyType>(PROPERTY_NAME_FORECOLOR);
			forecolor.setValue(new ColorPropertyType(this.labs[0].getColor()));
			this.addProperty(forecolor);
			
			
			
			Property<IntegerPropertyType> colorDifference = new Property<IntegerPropertyType>(PROPERTY_NAME_COLOR_DIFFERENCE);
			colorDifference.setValue(new IntegerPropertyType(14));
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
	
	private double getPropertyDoubleValue(String name)
	{
		Property<DoublePropertyType> prop = this.getProperty(name);
		return prop.getValue().doubleValue();
	}
	private void positionLayout()
	{
		int minDist = this.getPropertyIntValue(PROPERTY_NAME_MIN_DISTANCE);
		int maxDist = this.getPropertyIntValue(PROPERTY_NAME_MAX_DISTANCE);
		Color color  = this.getPropertyColorValue(PROPERTY_NAME_FORECOLOR);
		int objectCount = this.getPropertyIntValue(PROPERTY_NAME_OBJECT_COUNT);
		
		double minAmplitude = this.getPropertyDoubleValue(PROPERTY_NAME_MIN_AMPLITUDE);
		double maxAmplitude = this.getPropertyDoubleValue(PROPERTY_NAME_MAX_AMPLITUDE);
		
		double minFrequency = this.getPropertyDoubleValue(PROPERTY_NAME_MIN_FREQUENCY);
		double maxFrequency = this.getPropertyDoubleValue(PROPERTY_NAME_MAX_FREQUENCY);
		
		double minRotation = this.getPropertyDoubleValue(PROPERTY_NAME_MIN_ROTATION);
		double maxRotation = this.getPropertyDoubleValue(PROPERTY_NAME_MAX_ROTATION);
		
		StimulusGenPlotter plotter = this.getPlotter(objectCount, minDist, maxDist);
		this.stimuls = SineWaveStimulus.createStimulus(new Point(150,100),plotter,  color,
				new Period.Double(maxAmplitude, minAmplitude),
				new Period.Double(maxFrequency, minFrequency),
				new Period.Double(maxRotation, minRotation));
		
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
