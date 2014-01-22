package eyetrack.stimulusgen;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import perspectives.Property;
import perspectives.Viewer2D;
import perspectives.DefaultProperties.ColorPropertyType;
import perspectives.DefaultProperties.IntegerPropertyType;

public class StimulusGenViewer extends Viewer2D{
	public static final String PROPERTY_NAME_MIN_DISTANCE = "Distance.Minimum Distance";
	public static final String PROPERTY_NAME_MAX_DISTANCE = "Distance.Maximum Distance";
	public static final String PROPERTY_NAME_SIZE = "Size";
	public static final String PROPERTY_NAME_FORECOLOR = "Forecolor";
	public static final String PROPERTY_NAME_BACKGROUND_COLOR = "BackgroundColor";
	public static final String PROPERTY_NAME_OBJECT_COUNT = "Object Count";
	//noisy background stuffs
	public static final String PROPERTY_NAME_BACKGROUND_COPIES_COUNT = "Background.Copies";
	public static final String PROPERTY_NAME_BACKGROUND_MIN_DISTANCE = "Background.Minimum Distance";
	public static final String PROPERTY_NAME_BACKGROUND_MAX_DISTANCE = "Background.Maximum Distance";
	public static final String PROPERTY_NAME_BACKGROUND_MIN_ROTATION = "Background.Minimum Rotation";
	public static final String PROPERTY_NAME_BACKGROUND_MAX_ROTATION = "Background.Maximum Rotation";
	public static final String PROPERTY_NAME_BACKGROUND_BLURRING = "Background.Blurring";
	
	
	
	private Stimulus stimuls;
	private NoisyBackground noisyBackground;
	
	public StimulusGenViewer(String name) {
		super(name);
		// TODO Auto-generated constructor stub
		this.loadProperties();
		
		this.stimuls =null;
		this.noisyBackground = null;
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
			size.setValue(new IntegerPropertyType(20));
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
			
			Property<IntegerPropertyType> backgroundCopies = new Property<IntegerPropertyType>(PROPERTY_NAME_BACKGROUND_COPIES_COUNT);
			backgroundCopies.setValue(new IntegerPropertyType(4));
			this.addProperty(backgroundCopies);
			
			Property<IntegerPropertyType> backgroundMinDistance = new Property<IntegerPropertyType>(PROPERTY_NAME_BACKGROUND_MIN_DISTANCE);
			backgroundMinDistance.setValue(new IntegerPropertyType(200));
			this.addProperty(backgroundMinDistance);
			
			Property<IntegerPropertyType> backgroundMaxDistance = new Property<IntegerPropertyType>(PROPERTY_NAME_BACKGROUND_MAX_DISTANCE);
			backgroundMaxDistance.setValue(new IntegerPropertyType(500));
			this.addProperty(backgroundMaxDistance);
			
			Property<IntegerPropertyType> backgroundMinRotation = new Property<IntegerPropertyType>(PROPERTY_NAME_BACKGROUND_MIN_ROTATION);
			backgroundMinRotation.setValue(new IntegerPropertyType(10));
			this.addProperty(backgroundMinRotation);
			
			Property<IntegerPropertyType> backgroundMaxRotation = new Property<IntegerPropertyType>(PROPERTY_NAME_BACKGROUND_MAX_ROTATION);
			backgroundMaxRotation.setValue(new IntegerPropertyType(90));
			this.addProperty(backgroundMaxRotation);
			
			Property<IntegerPropertyType> backgroundBlurring = new Property<IntegerPropertyType>(PROPERTY_NAME_BACKGROUND_BLURRING);
			backgroundBlurring.setValue(new IntegerPropertyType(3));
			this.addProperty(backgroundBlurring);
			
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

		this.stimuls = Stimulus.createStimulus(new Point(150,100),30,plotter, size, color);
		
		int copies =  this.getPropertyIntValue(PROPERTY_NAME_BACKGROUND_COPIES_COUNT);
		int backgroundMinDist = this.getPropertyIntValue(PROPERTY_NAME_BACKGROUND_MIN_DISTANCE);
		int backgroundMaxDist = this.getPropertyIntValue(PROPERTY_NAME_BACKGROUND_MAX_DISTANCE);
		int minRotation = this.getPropertyIntValue(PROPERTY_NAME_BACKGROUND_MIN_ROTATION);
		int maxRotation = this.getPropertyIntValue(PROPERTY_NAME_BACKGROUND_MAX_ROTATION);
		int blurringAmount = this.getPropertyIntValue(PROPERTY_NAME_BACKGROUND_BLURRING);
		
		
		this.noisyBackground = new 
				NoisyBackground(
						plotter, 
						copies, 
						backgroundMinDist, backgroundMaxDist, 
						minRotation, maxRotation, 
						size,
						color,
						blurringAmount,
						3000,3000);
		
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
		if(this.noisyBackground != null )
		{
			this.noisyBackground.render(g);
		}
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
