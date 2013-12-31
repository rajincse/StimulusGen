package util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import perspectives.DefaultProperties.*;
import perspectives.Property;
import perspectives.PropertyManager;
import perspectives.PropertyType;
import perspectives.Viewer2D;

public abstract class Points2DViewer extends Viewer2D {
		
	private ArrayList<Integer> selectedPoints;		
	
	private boolean controlDown;
	
	
	protected abstract String getPointLabel(int p);
	protected abstract int getNumberOfPoints();
	protected abstract int getPointX(int p);
	protected abstract int getPointY(int p);
	protected abstract void setPointX(int p, int x);
	protected abstract void setPointY(int p, int y);
	
	public enum VerticalAlignment {TOP, MIDDLE, BOTTOM};	
	public enum HorizontalAlignment {LEFT, CENTER, RIGHT};
	public enum PointAspectType {CIRCLE_NO_LABEL, RECT_NO_LABEL, RECT_LABEL_FIT};
	
	private int[] pointSize = null;	
	private Color[] pointColor = null;
	private VerticalAlignment[] vAlign = null;
	private HorizontalAlignment[] hAlign = null;
	private double[] pointRotation = null;
	private PointAspectType[] pointAspect = null; 
	
	private Rectangle2D[] pointBounds = null;
	private Object pointBoundsLock = new Object();
	
	private Graphics2D graphics = null;
	
	private Font font;		
	
	private int pickedNode = -1;
	private boolean dragged = false;
	
	
	public Points2DViewer(String name) {
		super(name);		
		
		font = new Font("Sans-Serif", Font.PLAIN, 8);		
		
		selectedPoints = new ArrayList<Integer>();	
		
		controlDown = false;
			
		this.setTooltipDelay(500);	
		
	
	
	}
	
	public <T extends PropertyType> void propertyUpdated(Property p, T newvalue)
	{
	}
	
		
	public void setAspect(PointAspectType a)
	{
		if (pointAspect == null) pointAspect = new PointAspectType[getNumberOfPoints()];
		for (int i=0; i<pointAspect.length; i++)
			pointAspect[i] = a;
		updateBounds();
	}
	
	public void setAspect(int p, PointAspectType a)
	{
		if (pointAspect == null || p >= pointAspect.length) pointAspect = new PointAspectType[getNumberOfPoints()];
		pointAspect[p] = a;
		updateBounds();
	}
	
	public PointAspectType getAspect(int p)
	{
		if (pointAspect == null || p < 0 || p>= pointAspect.length)
			return PointAspectType.CIRCLE_NO_LABEL;
		else return pointAspect[p];
	}
	
	public void setPointSize(int s)
	{
		if (pointSize == null) pointSize = new int[this.getNumberOfPoints()];
		
		if (s%2 != 0) s=s+1;
		
		for (int i=0; i<pointSize.length; i++)
			pointSize[i] = s;
		
		updateBounds();
	}
	
	public void setPointSize(int p, int s)
	{
		if (pointSize == null || p >= pointSize.length) pointSize = new int[getNumberOfPoints()];
		if (s%2 != 0) s=s+1;
		pointSize[p] = s;
				
		updateBounds();
	}
	
	public int getPointSize(int p)
	{
		if (pointSize == null || p<0 || p>= pointSize.length)
			return 10;
		
		else return pointSize[p];
	}
	
	public void setColor(Color c)
	{
		if (pointColor == null) pointColor = new Color[getNumberOfPoints()];
		for (int i=0; i<pointColor.length; i++)
			pointColor[i] = c;		
		updateBounds();
	}
	
	public void setColor(int p, Color c)
	{
		if (pointColor == null || p >= pointColor.length)
		{
			pointColor = new Color[getNumberOfPoints()];
			for (int i=0; i<pointColor.length; i++)
				pointColor[i] = c;		
		}
		pointColor[p] = c;
		updateBounds();
	}
	
	public Color getColor(int p)
	{
		if (pointColor == null || p<0 || p>= pointColor.length)
			return Color.LIGHT_GRAY;
		
		else return pointColor[p];
	}
	
	public void setRotation(double r)
	{
		if (pointRotation == null) pointRotation = new double[this.getNumberOfPoints()];
		for (int i=0; i<pointRotation.length; i++)
			pointRotation[i] = r;
		updateBounds();
	}
	
	public void setRotation(int p, double r)
	{
		if (pointRotation == null || p >= pointRotation.length) pointRotation = new double[this.getNumberOfPoints()];
		pointRotation[p] = r;
		updateBounds();
	}
	
	public double getRotation(int p)
	{
		if (pointRotation == null || p<0 || p>= pointRotation.length)
			return 0;
		else return pointRotation[p];
	}
	
	public void setHorizontalAlignment(HorizontalAlignment h)
	{
		if (hAlign == null) hAlign = new HorizontalAlignment[getNumberOfPoints()];
		for (int i=0; i<hAlign.length; i++)
			hAlign[i] = h;
		updateBounds();
	}
	
	public void setHorizontalAlignment(int p, HorizontalAlignment h)
	{
		if (hAlign == null)
		{
			hAlign = new HorizontalAlignment[getNumberOfPoints()];
			for (int i=0; i<hAlign.length; i++)
				hAlign[i] = HorizontalAlignment.CENTER;
		}
		hAlign[p] = h;
		updateBounds();
	}
	
	public HorizontalAlignment getHorizontalAlignment(int p)
	{
		if (hAlign == null || p<0 || p>=hAlign.length)
			return HorizontalAlignment.CENTER;
		else return hAlign[p];
	}
	
	public void setVerticalAlignment(VerticalAlignment v)
	{
		if (vAlign == null) vAlign = new VerticalAlignment[getNumberOfPoints()];
		for (int i=0; i<vAlign.length; i++)
			vAlign[i] = v;
		updateBounds();
	}
	
	public void setVerticalAlignment(int p, VerticalAlignment v)
	{
		if (vAlign == null)
		{
			vAlign = new VerticalAlignment[getNumberOfPoints()];
			for (int i=0; i<vAlign.length; i++)
				vAlign[i] = VerticalAlignment.MIDDLE;			
		}
		vAlign[p] = v;		
		updateBounds();		
	}
	
	public VerticalAlignment getVerticalAlignment(int p)
	{
		if (vAlign == null || p<0 || p>=vAlign.length) return VerticalAlignment.MIDDLE;
		else return vAlign[p];
	}
	
	public void updateBounds()
	{
		synchronized(pointBoundsLock){
			pointBounds = null;}
	}
		

	public void render(Graphics2D g) {	
		graphics = g;
		renderPoints(g);		
	}	
	
	public void renderPoints(Graphics2D g)
	{		
		int n = getNumberOfPoints();
		
		boolean[] pickedPoints = new boolean[n];
		
		for (int i=0; i<pickedPoints.length; i++)
			pickedPoints[i] = false;		
		
		for (int i=0; i<selectedPoints.size(); i++)
			pickedPoints[selectedPoints.get(i)] = true;		
		
		synchronized(pointBoundsLock)
		{		
			if (pointBounds == null)
				cachePointBounds();	
			
				
			for (int i=0; i<n; i++)
			{	
				int x = getPointX(i);
				int y = getPointY(i);		
				
				g.translate(x, y);	
				g.rotate(this.getRotation(i));			
				g.translate(pointBounds[i].getX(), pointBounds[i].getY());				
				renderPoint(i, pickedPoints[i], g);				
				g.translate(-pointBounds[i].getX(), -pointBounds[i].getY());
				g.rotate(-this.getRotation(i));			
				g.translate(-x, -y);
			}			
		}
	}
	
	private void cachePointBounds()
	{
		int n = getNumberOfPoints();
		
		pointBounds = new Rectangle2D[n];			
	
		for (int i=0; i<n; i++)
		{	
			Rectangle2D bounds = getPointBounds(i);
			int xOffset = -(int)bounds.getWidth()/2;
			int yOffset = -(int)bounds.getHeight()/2;		
				
			if (getVerticalAlignment(i) == VerticalAlignment.TOP)
				yOffset = 0;
			else if (getVerticalAlignment(i) == VerticalAlignment.BOTTOM)
				yOffset = getPointSize(i)/2;
			if (getHorizontalAlignment(i) == HorizontalAlignment.LEFT)
				xOffset = 0;
			else if (getHorizontalAlignment(i) == HorizontalAlignment.RIGHT)
				xOffset = -(int)bounds.getWidth();
				
			pointBounds[i] = new Rectangle2D.Double(xOffset, yOffset, bounds.getWidth(), bounds.getHeight());
		}		
	}
	
	protected Rectangle2D getPointBounds(int p)
	{
		if (getAspect(p) == PointAspectType.RECT_LABEL_FIT)
		{	
			int fontSize = (int)(getPointSize(p)*0.8);	
			
			BufferedImage bi = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);			
			FontMetrics fm = bi.getGraphics().getFontMetrics(new Font("Sans-Serif",Font.PLAIN,fontSize));		
						
			String label = this.getPointLabel(p);
			String[] split = label.split("\\(-\\)");
			Rectangle2D rect = null;
			for (int i=0; i<split.length; i++)
			{
				java.awt.geom.Rectangle2D recttmp = fm.getStringBounds(split[i], bi.getGraphics());
				if (rect == null || recttmp.getWidth() > rect.getWidth())
					rect = recttmp;
			}
							
			int w = (int)(rect.getWidth());			
			int wrend = w + (int)Math.max(4,w*0.15);
			
			return new Rectangle2D.Double(0,0,wrend+6,getPointSize(p)*split.length);
		}
		else
			return new Rectangle2D.Double(0,0,getPointSize(p),getPointSize(p));
	}
	
	protected void renderPoint(int p, boolean selected, Graphics2D g)
	{	
		Color c = getColor(p);
		int s = getPointSize(p);
		
		Color selectedNodeColor = new Color(c.getRed(), Math.max(0,c.getGreen()-40), Math.max(0,c.getBlue()-40), Math.min(255,c.getAlpha() + 100));
		
		if (selected)
			g.setColor(selectedNodeColor);
		else
			g.setColor(c);		
			
		if (getAspect(p) == PointAspectType.CIRCLE_NO_LABEL)
		{
			g.fillOval(0, 0, s, s);
			if (selected)
			{
				g.setColor(Color.BLACK);
				g.drawOval(0,0, s, s);	
			}
		}
		else if (getAspect(p) == PointAspectType.RECT_LABEL_FIT || getAspect(p) == PointAspectType.RECT_NO_LABEL)
		{
		
			Rectangle2D bounds = getPointBounds(p);			
			
			g.fillRect(0, 0, (int)bounds.getWidth(), (int)bounds.getHeight());			
	
			if (selected)
			{				
				g.setColor(Color.BLACK);
				g.drawRect(0, 0, (int)bounds.getWidth(), (int)bounds.getHeight());
			}
			
			g.setColor(new Color(0,0,0,c.getAlpha()));	
			
			if (getAspect(p) == PointAspectType.RECT_LABEL_FIT)
			{
				int fontSize = (int)(s*0.8);
				
				if (g.getFont().getSize() != fontSize)
					g.setFont(new Font("Sans-Serif",Font.PLAIN,fontSize));
				
				String[] split = getPointLabel(p).split("\\(-\\)");
				g.setColor(Color.BLACK);
				for (int i=0; i<split.length; i++)
					//g.drawString(split[i], 3+(int)Math.ceil(Math.max(2,0.05*bounds.getWidth())),(int)((bounds.getHeight()-10)*0.8/split.length + fontSize*i+5));
					g.drawString(split[i], (int)Math.ceil(Math.max(2,0.05*bounds.getWidth())),(int)(bounds.getHeight()*0.8/split.length + fontSize*i));
			}
		}				
	}	
	
	
	@Override
	public boolean mousepressed(int x, int y, int button) {
				
		int n = getNumberOfPoints();
		
		double minDist = Double.MAX_VALUE;
						
		pickedNode = -1;
		for (int i=0; i<n; i++)
		{
			double nx = getPointX(i);
			double ny = getPointY(i);			
			//containment
			double x1 = x - nx;
			double y1 = y - ny;
			Point2D.Double dest = new Point2D.Double();
			AffineTransform.getRotateInstance(-this.getRotation(i)).transform(new Point2D.Double(x1,y1), dest);			
			double d = Math.sqrt(x1*x1+y1*y1);
			
			synchronized(pointBoundsLock)
			{
				if (pointBounds == null) cachePointBounds();
				
				if (pointBounds[i].contains(dest) && d<minDist)
				{
					pickedNode = i;					
					minDist = d;	
				}
			}			
		}
		
		if (pickedNode >= 0)
		{	
			if (!controlDown)
				this.clearPointSelection();
			
			
			selectPointInside(pickedNode);			
			return true;
		}
		else return false;
	}

	@Override
	public boolean mousereleased(int x, int y, int button) {
				
		int n = getNumberOfPoints();
		
		double minDist = Double.MAX_VALUE;
		
		int pickedNode = -1;
		for (int i=0; i<n; i++)
		{
			double nx = getPointX(i);
			double ny = getPointY(i);			
			//containment
			double x1 = x - nx;
			double y1 = y - ny;
			Point2D.Double dest = new Point2D.Double();
			AffineTransform.getRotateInstance(-this.getRotation(i)).transform(new Point2D.Double(x1,y1), dest);			
			double d = Math.sqrt(x1*x1+y1*y1);
			
			synchronized(pointBoundsLock)
			{
				if (pointBounds == null) cachePointBounds();
				
				if (pointBounds[i].contains(dest) && d<minDist)
				{
					pickedNode = i;					
					minDist = d;	
				}
			}			
		}
		
		if (!dragged && pickedNode < 0 && !controlDown)
		{	
			this.clearPointSelection();	
			dragged = false;
			return true;
		}		
		else
		{
			dragged = true;
			return false;
		}
	}

	@Override
	public boolean mousedragged(int currentX, int currentY, int oldX, int oldY) {
		
		dragged = true;
		
		if (pickedNode >= 0 && selectedPoints.size() != 0)
		{
			int deltaX = currentX - oldX;
			int deltaY = currentY - oldY;
			for (int i=0; i<selectedPoints.size(); i++)
			{
				int index = selectedPoints.get(i);
				setPointX(index,getPointX(index) + deltaX);
				setPointY(index,getPointY(index) + deltaY);
				
				pointMoved(selectedPoints.get(i), deltaX, deltaY);
			}
			return true;
		}
		else
			return false;
	}

	@Override
	public boolean mousemoved(int x, int y) {
		
		synchronized(pointBoundsLock)
		{
			if (pointBounds == null)
				return false;
				
			int n = getNumberOfPoints();
			
			this.setToolTipText("");
			
			int pickedNode = -1;
			double minDist = Double.MAX_VALUE;
			for (int i=0; i<n; i++)
			{
				double nx = getPointX(i);
				double ny = getPointY(i);
			
				//containment
				double x1 = x - nx;
				double y1 = y - ny;
				Point2D.Double dest = new Point2D.Double();
				AffineTransform.getRotateInstance(-this.getRotation(i)).transform(new Point2D.Double(x1,y1), dest);			
				double d = Math.sqrt(x1*x1+y1*y1);
				
				if (pointBounds[i].contains(dest) && d<minDist)
				{
					pickedNode = i;					
					minDist = d;	
				}			
			}
			
			if (pickedNode >= 0)
			{	
				//this.setToolTipText(getPointLabel(pickedNode));
				this.setToolTip(pickedNode);
				return true;
			}
		}
		return false;		
	}
	
	protected void setToolTip(int pickedNode)
	{
		this.setToolTipText(getPointLabel(pickedNode));
	}
	
	@Override
	public void keyPressed(int keycode) {				
		if (keycode == KeyEvent.VK_CONTROL)
			controlDown = true;
	}

	@Override
	public void keyReleased(int keycode) {
		controlDown = false;
	}

	public void clearPointSelection()
	{
		for (int i=0; i<selectedPoints.size(); i++)
			pointDeselected(selectedPoints.get(i));
		selectedPoints.clear();
		pointSelectionChanged();
	}
	
	private void selectPointInside(int p)
	{
		if (selectedPoints.indexOf(p) < 0)
		{
			selectedPoints.add(p);
			pointSelected(p);
			pointSelectionChanged();			
		}
	}
	
	public void selectPoint(int p)
	{
		if (selectedPoints.indexOf(p) < 0)
			selectedPoints.add(p);
	}
	
	public void deselectPoint(int p)
	{
		int index = selectedPoints.indexOf(p);
		if (index >= 0)
		{
			selectedPoints.remove(index);
			pointDeselected(p);
			pointSelectionChanged();
		}
	}
	
	public int[] getSelectedPoints()
	{
		int[] result = new int[selectedPoints.size()];
		for (int i=0; i<result.length; i++)
			result[i] = selectedPoints.get(i);
		return result;
	}
		
	protected void pointSelected(int p)
	{
	}
	
	protected void pointDeselected(int p)
	{	
	}
	
	protected void pointSelectionChanged()
	{		
	}
	
	protected void pointMoved(int point, int deltaX, int deltaY)
	{		
	}
}
