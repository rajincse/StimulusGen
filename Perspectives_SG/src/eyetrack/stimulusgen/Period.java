package eyetrack.stimulusgen;

import java.util.Random;

public class Period {
	public int maximum;
	public int minimum;
	
	public Period(int max, int min)
	{
		this.maximum = max;
		this.minimum = min;
	}
	public int getRandom()
	{	
		int range = this.maximum - this.minimum;
		if(this.maximum <= this.minimum )
		{
			return this.minimum;
		}
		else
		{
			Random rand = new Random();
			int randomValue =(int)Math.abs( rand.nextInt());
			
			return randomValue % range+this.minimum; 
		}
		
	}
	public static class Double  {
		public double maximum;
		public double minimum;
		
		public Double (double max, double min)
		{
			this.maximum = max;
			this.minimum = min;
		}
		
		public double getRandom()
		{
			double random  =Math.random();
			double range = this.maximum - this.minimum;
			return random * range+ this.minimum;
		}
	}
}



