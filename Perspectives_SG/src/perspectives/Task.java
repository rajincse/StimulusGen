package perspectives;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.SwingWorker;

public abstract class Task{
	
	double progress = 0;
	
	MySwingWorker worker = null;
	
	TaskObserver taskObs = null;
	
	public boolean done;
	
	public String name;
	
	public boolean indeterminate = false;
	
	public Task(String name)
	{
		this.name = name;
	}
	
	protected void setProgress(double d)
	{
		progress = d;	
		if (taskObs != null)
			taskObs.progressChanged(this, d);
	}
	
	public abstract void task();
	
	
	public void startTask(TaskObserver t)
	{
		done = false;
		taskObs = t;		
		worker = new MySwingWorker();
		worker.execute();
		
		if (taskObs != null)
			taskObs.addTask(this);
					
	}
	
	public void cancelTask()
	{
		worker.cancel(true);
		done = true;
	}
	
	
	//swingworker class doing simulation and rendering in the background for some classes of viewers
	class MySwingWorker extends SwingWorker<Integer, Void>
	{
		public MySwingWorker()
		{
		}		
		 
 	    public Integer doInBackground() {
 	    	
 	    	task(); 
 	    	return new Integer(0);
 	       
 	    }

 	    @Override
 	    public void done() {
 	    	done = true; 	    	
 	    }
		
	}
	
	
}
