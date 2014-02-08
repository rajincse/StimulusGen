package eyetrack.shapematch;

import java.awt.Color;
import java.awt.Graphics2D;

public class SineWaveShape extends AbstractBoundedShape{
	private static final int WIDTH =400;
	private static final int HEIGHT =50;
	private double amplitude;
	private double frequency;
	private double rotation;

	private int sizeFactor;
	public SineWaveShape(int x, int y,int sizeFactor, Color color,  double amplitude, double frequency, double rotation) {
		super(x, y, WIDTH, HEIGHT, color, false);
		System.out.println("(x,y) => ("+x+", "+y+")");
		this.sizeFactor = sizeFactor;
		this.amplitude = amplitude;
		this.frequency = frequency;
		this.rotation = rotation;
	}

	@Override
	void drawFilled(Graphics2D g) {
		// TODO Auto-generated method stub
		this.draw(g);
	}

	@Override
	void drawUnFilled(Graphics2D g) {
		// TODO Auto-generated method stub
		this.draw(g);
	}
	
	void draw(Graphics2D g)
	{
		double stepX =  Math.PI / 180;
		int lastX = this.x- this.width/2;
		int lastY = this.y- this.width/2;
		double x=0;
		double y =0;
		int totalDegree =(int)( this.width * sizeFactor / stepX*2);
		for(int i= 1;i<= totalDegree ;i++)
		{
			x = i* stepX;
			y = this.amplitude*sizeFactor* Math.sin(this.frequency* x);
			double radianAngle = this.rotation * Math.PI / 180;
			int transformedX =(int)( (double)this.x+ x *Math.cos(radianAngle)-  y * Math.sin(radianAngle) ) - this.width/2;
			int transformedY =(int)( (double)this.y+ x *Math.sin(radianAngle)+  y * Math.cos(radianAngle) )- this.width/2;
			g.drawLine(lastX, lastY, transformedX, transformedY);
			lastX = transformedX;
			lastY = transformedY;
		}
		
	}

	@Override
	void additionalRender(Graphics2D g) {
		// TODO Auto-generated method stub
		
	}

	@Override
	void selectionRender(Graphics2D g) {
		// TODO Auto-generated method stub
		
	}
	
}
