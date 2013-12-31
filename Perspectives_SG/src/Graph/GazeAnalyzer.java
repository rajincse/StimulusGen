package Graph;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Date;

class FixationWindow
{
    private int maxEntry;
    private ArrayList<Point> gazeLocationQueue;
    private ArrayList<Long> times;
    private long startTime;
    
    public FixationWindow(int maxEntry)
    {
        this.maxEntry = maxEntry;
        this.gazeLocationQueue = new ArrayList<Point>();
        this.times = new ArrayList<Long>();
        
        startTime = -1;
    }

    public void add(int x, int y)
    {
        this.gazeLocationQueue.add(new Point(x,y));
        this.times.add(new Date().getTime());
        if (gazeLocationQueue.size() > this.maxEntry)
        {
        	if (startTime == -1)
        		startTime = times.get(0);
        	
        	gazeLocationQueue.remove(0);
        	times.remove(0);
        }
    }
    public void clear()
    {
        this.gazeLocationQueue.clear();
        this.times.clear();
    }

    
    public int getCount()
    {
        return this.gazeLocationQueue.size();
    }
    
    public Point weightedAverageFixation()
    {
    	int dX = 0;
    	int dY = 0; 
    	int ct = 0;
    	for (int i=0; i<gazeLocationQueue.size(); i++)
    	{
    		dX += (i+1) * gazeLocationQueue.get(i).x;
            dY += (i+1) * gazeLocationQueue.get(i).y;
            ct += i+1;
       }
       int x = dX / ct;
       int y = dY / ct;

       return new Point(x,y);
    }
    
    public void clean()
    {
    	Point w = this.weightedAverageFixation();
    	
    	for (int i=0; i<gazeLocationQueue.size()-1; i++)
    	{
    		Point p = gazeLocationQueue.get(i);
    		double d = Math.sqrt((w.x - p.x)*(w.x-p.x) + (w.y-p.y)*(w.y-p.y));
    		if (d > 100)
    		{
    			gazeLocationQueue.remove(i);
    			times.remove(i);
    			i--;
    		}
    	}
    }
    
    public long startTime()
    {
    	if (startTime != -1)
    		return startTime;
    	else
    		return times.get(0).longValue();
    }
    
    public long endTime()
    {
    	return times.get(times.size()-1);
    }
}



public class GazeAnalyzer
{
    private  static int MAX_ENTRY = 40;
    private static int POTENTIAL_MAX_ENTRY = 5;
    private static int THRESHOLD = 40;
    private static int DWELLTIME = 100;
    private FixationWindow currentWindow;
    private FixationWindow potentialWindow;
    
    GazeAnalyzerListener listener;
    
    private boolean fixationReported = false;
    
    public GazeAnalyzer(GazeAnalyzerListener listener, int max_entries, int pot_max_entry, int threshold, int dwellTime)
    {
    	this.MAX_ENTRY = max_entries;
    	this.POTENTIAL_MAX_ENTRY = pot_max_entry;
    	this.THRESHOLD = threshold;
    	this.DWELLTIME = dwellTime;
    	
        this.currentWindow = new FixationWindow(MAX_ENTRY);
        this.potentialWindow = new FixationWindow(MAX_ENTRY);
        
        this.listener = listener;
    }
    
    public Point currentFixation()
    {
    	return currentWindow.weightedAverageFixation();
    }
    
    public void AddGazeLocation(int x, int y)
    {
        if (this.currentWindow.getCount() == 0)
            this.currentWindow.add(x,y);
 
        else
        {
            Point cf = this.currentWindow.weightedAverageFixation();
            double d = Math.sqrt((cf.x - x)*(cf.x-x) + (cf.y-y)*(cf.y-y)); 
            
            if (d > THRESHOLD)
            {
                this.potentialWindow.add(x,y);
                if (this.potentialWindow.getCount() > POTENTIAL_MAX_ENTRY)
                {
                	this.potentialWindow.clean();
                	
                	if (this.potentialWindow.getCount() > POTENTIAL_MAX_ENTRY)
                	{
                		if (currentWindow.getCount() == this.MAX_ENTRY && new Date().getTime() - currentWindow.startTime() >= this.DWELLTIME)
                			listener.fixationEnded(cf, currentWindow.endTime());
                		this.currentWindow = this.potentialWindow;
                		this.potentialWindow = new FixationWindow(MAX_ENTRY);
                		fixationReported = false;
                	}
                }
            }
            else
            {
            	this.currentWindow.add(x, y);
            	
            	if (!fixationReported && currentWindow.getCount() >= this.MAX_ENTRY && new Date().getTime() - currentWindow.startTime() >= this.DWELLTIME)
            	{
            		fixationReported = true;
            		listener.fixationDetected(currentWindow.weightedAverageFixation(), currentWindow.startTime());
            	}
            	else
            		this.currentWindow.add(x,y);
            	
            		
            }
        }
        
    }

}