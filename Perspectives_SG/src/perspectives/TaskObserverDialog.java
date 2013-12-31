package perspectives;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class TaskObserverDialog extends JPanel implements TaskObserver, Runnable {

	private ArrayList<Task> taskList;
	private ArrayList<Long> taskStartTime;
	private ArrayList<Double> taskProgress;
	private ArrayList<JProgressBar> progressBars;
	private ArrayList<JPanel> progressPanels;
	
	private JInternalFrame parent;
	private boolean pack;
	
	public TaskObserverDialog(JInternalFrame parent, boolean pack)
	{
		this.setBounds(20, 20, 200, 400);		 
		this.setLayout(new FlowLayout());
		
		this.pack = pack;
		//this.setAlwaysOnTop(true);
		//this.setModal(true);
		
		this.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5,5,5,5),BorderFactory.createLineBorder(Color.black,2)));
		
		this.setBackground(new Color(200,220,250));
		
		
		
		this.parent = parent;
		
		
		taskList = new ArrayList<Task>();
		taskStartTime = new ArrayList<Long>();
		taskProgress = new ArrayList<Double>();
		progressBars = new ArrayList<JProgressBar>();
		progressPanels = new ArrayList<JPanel>();
		
		(new Thread(this)).start();
	}
	
	@Override
	public void addTask(Task t) {
		synchronized(taskProgress)
		{
		taskList.add(t);
		taskStartTime.add((new Date()).getTime());
		taskProgress.add(0.);
		progressBars.add(null);
		progressPanels.add(null);
		}
	}

	@Override
	public void progressChanged(Task t, double d) {
		
		synchronized(taskProgress)
		{
		int index = taskList.indexOf(t);
		if (index < 0 || index >= taskList.size())
			return;
		
		taskProgress.remove(index);
		taskProgress.add(index,d);	
		}
	}

	@Override
	public void run() {
		while (true)
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			synchronized(taskProgress)
			{
			boolean show = false;
			for (int i=0; i<taskList.size(); i++)
			{				
				if (taskList.get(i).done)
				{
					taskList.remove(i);
					taskStartTime.remove(i);
					taskProgress.remove(i);					
					progressBars.remove(i);		
					if (progressPanels.get(i) != null)
						this.remove(progressPanels.get(i));
					i--;
					//this.pack();
					continue;
				}
				
				long time = (new Date()).getTime();
				
				if (time-taskStartTime.get(i) > 500)
				{
					show = true;
					if (progressBars.get(i) == null)
					{
						JProgressBar pb = new JProgressBar(0,100);						
						pb.setIndeterminate(taskList.get(i).indeterminate);
						progressBars.remove(i);
						progressBars.add(i,pb);	
						
						JPanel panel = new JPanel();
						panel.setBackground(this.getBackground());
						progressPanels.remove(i);
						progressPanels.add(i,panel);
						panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
						
						JPanel p2 = new JPanel();
						p2.setBackground(this.getBackground());
						p2.setLayout(new BoxLayout(p2, BoxLayout.X_AXIS));
						p2.add(new JLabel(taskList.get(i).name));
						p2.add(Box.createHorizontalStrut(20));
						p2.add(new JButton("x"));
						panel.add(p2);
						panel.add(pb);
						
						this.add(panel);
						//this.pack();
					}
					JProgressBar pb = progressBars.get(i);
					
					if (!taskList.get(i).indeterminate)
					{
						double prog = taskProgress.get(i);
						pb.setValue((int)(prog*100));
						if (prog >= 1)
						{
							taskList.get(i).done = true;
						}
					}
					
					
					
					

				}				
			}
			
			
			if (show)
			{
				parent.add(this, BorderLayout.NORTH);
			
				if (pack)
					parent.pack();
				parent.revalidate();
			}
			else
			{
				
				parent.remove(this);
				if (pack)
					parent.pack();
				parent.revalidate();
			}
			}
			
			
		}
		
	}
	
}
