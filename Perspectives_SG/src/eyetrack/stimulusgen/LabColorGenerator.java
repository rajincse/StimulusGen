package eyetrack.stimulusgen;


import java.awt.Color;

import util.Util;

public class LabColorGenerator {
	public double l;
	public double a;
	public double b;
	public LabColorGenerator(double l, double a, double b)
	{
		this.l = l;
		this.a = a;
		this.b = b;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		Color c = Util.labToRgb(l, a, b);
		String hex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
		return l+","+a+", "+b+"= "+hex;
	}
	public String getDivHtml()
	{
		Color c = Util.labToRgb(l, a, b);
		String hex = String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
		return "<div style=\"background-color:"+hex+"\"></div>";
	}
	public Color getColor()
	{
		return Util.labToRgb(l, a, b);
	}
	public static LabColorGenerator [] generate(int size)
	{
		LabColorGenerator []labs = new LabColorGenerator[size];
		
		/*
		double l1 = 14.5;
		double a1 = 127;
		double b1 = -128;
		*/
		labs[0] = new LabColorGenerator( 14.5,127,-128);
		/*
		double l2 = 90;
		double a2 = -126;
		double b2 = 125;
		*/
		labs[labs.length-1] = new LabColorGenerator(90,-126,125);
		
//		System.out.println(labs[0]);
//		System.out.println(labs[9]);
		
		for(int i=1;i< labs.length-1;i++)
		{
			double m1 = i;
			double m2 = labs.length -m1;
			
			double l = (m2* labs[0].l +m1 * labs[labs.length-1].l)/(m1+m2);
			double a = (m2* labs[0].a +m1 * labs[labs.length-1].a)/(m1+m2);
			double b = (m2* labs[0].b +m1 * labs[labs.length-1].b)/(m1+m2);
			
			labs[i] = new LabColorGenerator(l,a,b);
		}
		return labs; 
	}
}
