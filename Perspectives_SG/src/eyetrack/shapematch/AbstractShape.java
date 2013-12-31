package eyetrack.shapematch;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.LinkedList;

import eyetrack.shapematch.rule.ILocalRule;

public abstract class AbstractShape {
	protected Color color;
	protected Color invertedColor ;
	
	protected double score;
	protected LinkedList<ILocalRule> localRules;
	protected boolean isSelected;
	
	protected boolean isDraggable;
	public  AbstractShape(Color color) {
		// TODO Auto-generated constructor stub
		this.color =color;
		
		this.invertedColor = new Color(255-this.color.getRed(), 255-this.color.getGreen(), 255- this.color.getBlue());
		this.score =0;
		this.isSelected = false;
		this.localRules = new LinkedList<ILocalRule>();
		this.isDraggable = true;
	}
	abstract public void render(Graphics2D g);
	
	abstract public double getDistance(Point p);
	
	public void addLocalRule(ILocalRule localRule)
	{
		this.localRules.add(localRule);
	}
	
	
	public void applyLocalrules(LinkedList<Point> gazeList)
	{
		for(ILocalRule localRule:this.localRules)
		{
			localRule.applyLocalRule(this, gazeList);
		}
	}
	
	public void setScore(double score)
	{
		this.score = score;
	}
	public void setDraggable(boolean isDraggable)
	{
		this.isDraggable = isDraggable;
	}
	abstract public boolean isInShape(int x, int y);

	abstract public boolean mousedragged(int currentx, int currenty, int oldx, int oldy);
	abstract public boolean mousepressed(int x, int y, int button);
}
