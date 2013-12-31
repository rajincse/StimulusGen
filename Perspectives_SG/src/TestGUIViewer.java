import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import perspectives.Property;
import perspectives.ViewerGUI;


public class TestGUIViewer extends ViewerGUI {
	
	JButton b;
	
	public TestGUIViewer(String name)
	{
		super(name);
	}

	@Override
	public void init() {
		
		
		
		try {
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JPanel p = getPanel();
		p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
		
		b = new JButton("ggg");
		p.add(b);
		p.add(new JButton("dfd"));
	}
	
	public <T> void propertyUpdated(Property p, T newvalue)
	{
		b.setText((String)newvalue);
	}

}
