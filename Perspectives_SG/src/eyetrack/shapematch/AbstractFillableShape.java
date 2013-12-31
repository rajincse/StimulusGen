package eyetrack.shapematch;

import java.awt.Color;
import java.awt.Graphics2D;

public abstract class AbstractFillableShape extends AbstractShape{
	protected boolean fill;
	
	public AbstractFillableShape(Color color, boolean fill)
	{
		super(color);

		this.fill = fill;
	}
	
	abstract void drawFilled(Graphics2D g);
	abstract void drawUnFilled(Graphics2D g);
	abstract void additionalRender(Graphics2D g);
	abstract void selectionRender(Graphics2D g);

	@Override
	public void render(Graphics2D g) {
		// TODO Auto-generated method stub
		if(this.isSelected)
		{
			this.selectionRender(g);
		}
	
		g.setColor(color);
		
		
		if(fill)
		{
			this.drawFilled(g);
		}
		else
		{
			this.drawUnFilled(g);
		}
		this.additionalRender(g);
	}
	
}
