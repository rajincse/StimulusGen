package perspectives;
import javax.swing.JPanel;

/**
 * Base class for Viewers that are mainly GUI implementations (Collections of Lists, Options, Buttons and so on). Developers can get access to the Viewer's main panel and then add components etc. They can also set an existing panel as the main content panel.
 * Developers should implement init() to load the components into getPanel().
 * <br>
 * @author rdjianu
 *
 */
public abstract class ViewerGUI extends Viewer{
	
	JPanel container;
	
	public ViewerGUI(String name)
	{
		super(name);
		container = null;
	}
	
	public JPanel getPanel()
	{
		return container;
	}
	
	public void setContentPanel(JPanel c)
	{
		container = c;
	}
	
	public String getViewerType()
	{
		return "ViewerGUI";
	}
	
	public abstract void init();

}
