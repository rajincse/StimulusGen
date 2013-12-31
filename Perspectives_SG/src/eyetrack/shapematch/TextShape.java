package eyetrack.shapematch;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;

public class TextShape extends AbstractShape{
	private int x;
	private int y;
	
	private Font font;
	private String text;
	
	public TextShape(Color color, int x, int y, Font font, String text) {
		super(color);
		this.x = x;
		this.y = y;
		this.font = font;
		this.text = text;
	}

	@Override
	public void render(Graphics2D g) {
		// TODO Auto-generated method stub
		g.setColor(this.color);
		Font previousFont = g.getFont();
		g.setFont(this.font);
		g.drawString(this.text, x, y);
	}

	@Override
	public double getDistance(Point p) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean mousedragged(int currentx, int currenty, int oldx, int oldy) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInShape(int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mousepressed(int x, int y, int button) {
		// TODO Auto-generated method stub
		return false;
	}

}
