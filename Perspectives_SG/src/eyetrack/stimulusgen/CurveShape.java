package eyetrack.stimulusgen;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;

public class CurveShape {

	private Point[] curvePoints;
	private Color color1;
	private Color color2;
	private double segment1;
	private double segment2;
	private boolean selection;
	
	public CurveShape(Point[] curvePoints,Color color1,Color color2,double segment1,double segment2 , boolean selection) {

		this.curvePoints = curvePoints;
		this.color1=color1;
		this.color2=color2;
		this.segment1=segment1;
		this.segment2=segment2;
		this.selection = selection;
	}

	
	public void render(Graphics2D g) {
		// TODO Auto-generated method stub
		if(this.curvePoints != null)
		{
			Point p1 = this.curvePoints[0];
			Point p2 = null;
			Font font = new Font("Verdana", Font.BOLD, 16);
			if(this.selection)
			{
				g.setColor(Color.red);
				g.fillOval((p1.x)-5, (p1.y)-5, 10, 10);
				
				g.setFont(font);
				g.drawString("A", (p1.x)-10, (p1.y)-10);
			}
			
	
			
			int firstSegment = (int) ( this.curvePoints.length * segment1);
			int secondSegment = (int) ( this.curvePoints.length * segment2);
			
			for(int i=1;i<this.curvePoints.length;i++)
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
				p2 = this.curvePoints[i];
				g.drawLine(p1.x, p1.y, p2.x, p2.y);
				p1 = p2;
			}
			if(this.selection)
			{
				g.setColor(Color.red);
				g.fillOval((p2.x)-5, (p2.y)-5, 10, 10);
				g.setFont(font);
				g.drawString("B", (p2.x)+10, (p2.y)-10);
			}
		}
	}
}
