package perspectives;

import java.awt.geom.Point2D;

import javax.swing.JPanel;

/**
 * 
 * Base class for viewers. Possible Viewer implementations are Viewer2D, Viewer3D and ViewerGUI. Developers should overload one of these three.
 * 
 * @author rdjianu
 *
 */
public abstract class Viewer extends PropertyManager
{
	JPanel drawArea;
	
	public Viewer(String name)
	{
		super(name);
	}
	
	public abstract String getViewerType();
	
	public Point2D getLocationOnScreen()
	{
		if (drawArea == null)
			return null;
		else
			return drawArea.getLocationOnScreen();
	}
}


