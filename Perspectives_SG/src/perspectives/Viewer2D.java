package perspectives;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

/**
 * Base class for Viewers doing 2D graphics. Developers should overload render for the actual graphics. They should overload simulate for animation or computational changes. Developers should not try to overload paint, repaint etc. The viewer rendering is refreshed automatically
 * with double buffering. Developers can block automatic rendering by reimplementing the method skipRendering. As long as skipRendering returns true the paint method will not be called. <br>
 * 
 * Viewer2D is generally contained in a ViewerContainer which provides automatic panning (left mouse button down drag, or arrows), and zooming (right mouse button down, or + - keys). If a Viewer2D implementation needs to use mouse press and dragging for something else (e.g.,
 * dragging nodes in a graph visualization) then those functions should return true indicating that the functionality has already been processed. Consult some of the examples.
 * <br>
 * @author rdjianu
 *
 */
public abstract class Viewer2D extends Viewer {
	
	protected boolean antialiasing = true;
	public boolean antialiasingSet = false;	
	
	
	
	private boolean tooltip;
	private String tooltipText;
	
	int mouseX;
	int mouseY;
	double zoom;
	
	private int tooltipX = 0;
	private int tooltipY = 0;
	private long tooltipDelay = 1000;
	
	ViewerContainer container;
	

	public Viewer2D(String name)
	{
		super(name);
		
		tooltip = true;
		tooltipText = "";
		mouseX = 0;
		mouseY = 0;
		zoom = 1;
	}
	public abstract void render(Graphics2D g);
	public abstract void simulate();
	
	public boolean mousepressed(int x, int y, int button) {return false;};
	public boolean mousereleased(int x, int y, int button) {return false;};
	public boolean mousemoved(int x, int y)
	{ 
		return false;
	};
	public boolean mousedragged(int currentx, int currenty, int oldx, int oldy){return false;};
	public void zoomed(double zoom){};
	public void keyPressed(int keycode) {};
	public void keyReleased(int keycode) {};
	
	public double getZoom()
	{
		return zoom;
	}
	
	public AffineTransform getTransform()
	{
		return container.transform;
	}
	
	public void translate(int dx, int dy)
	{
		container.translatex = dx;
		container.translatey = dy;
	}
	
	public void renderTooltip(Graphics2D g)
	{
		g.setFont(new Font("Sans-serif",Font.PLAIN, 11));
			int sizeX = g.getFontMetrics().stringWidth(tooltipText)+6;
			int sizeY = g.getFontMetrics().getHeight()+1;
			g.setColor(new Color(235,235,100));
			g.fillRect(tooltipX, tooltipY-sizeY, sizeX, sizeY);
			g.setColor(Color.DARK_GRAY);
			g.setStroke(new BasicStroke(1));
			g.drawRect(tooltipX, tooltipY-sizeY, sizeX, sizeY);
			g.drawString(tooltipText, tooltipX+4, tooltipY-3);	
	}
	
	public Color backgroundColor() { return Color.WHITE;}
	
	public String getViewerType()
	{
		return "Viewer2D";
	}	
	
	public void enableAntialising(boolean yes)
	{
		antialiasing = yes;
		antialiasingSet = false;
	}
	
	public boolean skipRendering() { return false;}
	
	public void enableToolTip(boolean t)
	{
		tooltip = t;
	}
	
	public boolean getToolTipEnabled()
	{
		return tooltip;
	}
	
	public void setToolTipText(String t)
	{
		tooltipText = t;
	}
	
	public String getToolTipText()
	{
		return tooltipText;
	}
	
	public void setToolTipCoordinates(int x, int y)
	{
		tooltipX = x;
		tooltipY = y;
	}
	
	public long getTooltipDelay()
	{
		return tooltipDelay;
	}
	
	public void setTooltipDelay(long ms)
	{
		tooltipDelay = ms;
	}
	
	public double getDefaultZoom()
	{
		return 1;
	}
	
	public void setZoom(double z)
	{
		if (this.container != null)
			this.container.zoom = z;
	}
	
	public void setTranslation(int tx, int ty)
	{
		if (this.container != null)
		{
			this.container.translatex = tx;
			this.container.translatey = ty;
		}
	}
	
	public void setContainer(ViewerContainer c)
	{
		container = c;
		
	}
	


}
