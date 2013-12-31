package userstudy;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class UserStudyUtility {
	public static final int TASK_TYPE_NEIGHBOR =0;
	public static final int TASK_TYPE_PATH =1;
	public static final int TASK_TYPE_GAZE_CORRECTION =2;
	
	public static final int TASK_DURATION_DEFAULT = 3000;
	public static final int TASK_ALERT_DURATION_DEFAULT = 10000;
	
	public static final int UNDEFINED = -1;
	
	public static final String HEADER_PREFIX ="#";
	public int taskType;
	public long taskDuration;
	public long alertDuration;
	public double selectionRatio;
	public ArrayList<Integer> selectedNodeIndices;
	public int totalGazeSelection;
	public int initiallyUnselected;
	
	public boolean startWithEyeTrack;
	public boolean startWithGazeCorrection;

	public long lastTime;
	public int pass;
	public String filePath;
	private String fileOutputBuffer;
	
	public UserStudyUtility()
	{
		this.taskType= TASK_TYPE_NEIGHBOR;
		this.taskDuration = TASK_DURATION_DEFAULT;
		this.alertDuration = TASK_ALERT_DURATION_DEFAULT;
		this.selectionRatio =0;
		this.fileOutputBuffer ="";
		this.selectedNodeIndices =new ArrayList<Integer>();
		this.totalGazeSelection =0;
		this.initiallyUnselected =0;
		this.startWithEyeTrack = true;
		this.startWithGazeCorrection = true;
	}
	public void parseHeaderLine(String headerLine)
	{
		if(headerLine.startsWith(UserStudyUtility.HEADER_PREFIX) && headerLine.contains("="))
		{
			String keyValuePair = headerLine.substring(1);
			String[] split = keyValuePair.split("=");
			String key = split[0].trim();
			String value = split[1].trim();
			this.fileOutputBuffer ="";
			if(key.equalsIgnoreCase("type"))
			{
				this.taskType = Integer.parseInt(value);
			}
			else if(key.equalsIgnoreCase("duration"))
			{
				this.taskDuration = Long.parseLong(value);
			}
			else if(key.equalsIgnoreCase("alert-duration"))
			{
				this.alertDuration = Long.parseLong(value);
			}
			else if(key.equalsIgnoreCase("selection-ratio"))
			{
				this.selectionRatio = Double.parseDouble(value);
			}
			else if(key.equalsIgnoreCase("start-with-gaze-correction"))
			{
				this.startWithGazeCorrection = value.toUpperCase().contains("Y");
			}
			else if(key.equalsIgnoreCase("start-with-eye-tracker"))
			{
				this.startWithEyeTrack = value.toUpperCase().contains("Y");
			}
		}
	}
	
	public void addToOutputBuffer(String resultLine)
	{
		this.fileOutputBuffer+= "\r\n"+resultLine;
	}
	private String getBooleanYesNo(boolean val)
	{
		return val?"Y":"N";
	}
	public void addResultLineForTaskGazeCorrecton(boolean withGazeCorrection, int totalNode, int initiallyUnselected,int userSelected, double percentile, long responseTime)
	{
		String result =getBooleanYesNo(withGazeCorrection)+"\t";
		result+= totalNode+"\t";
		result+= initiallyUnselected+"\t";
		result+= userSelected+"\t";
		result+= String.format("%1$,.2f", percentile)+"\t";
		
		result+= responseTime;
		
		this.addToOutputBuffer(result);
	}
	public void addResultLineForTaskNeighbor(boolean withEyeTracking, int node1, int node2,boolean hasEdge, boolean userCorrect, long responseTime)
	{
		String result =getBooleanYesNo(withEyeTracking)+"\t";
		result+= node1+"\t";
		result+= node2+"\t";
		result+= getBooleanYesNo(hasEdge)+"\t";
		boolean usersAnswer = false;
		if(userCorrect)
		{
			usersAnswer = hasEdge;
		}
		else
		{
			usersAnswer = ! hasEdge;
		}
			
		result+= getBooleanYesNo(usersAnswer)+"\t";
		result+= responseTime;
		
		this.addToOutputBuffer(result);
	}
	
	public void addResultLineForTaskpath(
			boolean withEyeTracking, 
			int node1, 
			int node2,
			int shortestPathSize,
			String shortestPath,
			String userPath,
			boolean isUserPathValid,
			long responseTime)
	{
		String result =getBooleanYesNo(withEyeTracking)+"\t";
		result+= node1+"\t";
		result+= node2+"\t";
		result+= shortestPathSize+"\t";
		result+= "\""+shortestPath+"\"\t";
		result+= "\""+userPath+"\"\t";
		result+= getBooleanYesNo(isUserPathValid)+"\t";
		result+= responseTime;
		
		this.addToOutputBuffer(result);
	}
	private String getHeader()
	{
		if(this.taskType == UserStudyUtility.TASK_TYPE_NEIGHBOR)
		{
			return "withEyeTracking?(y/n)\tnode1\tnode2\thasEdge?(y/n)\tresponse_from_user(y/n)\ttime";
		}
		else if(this.taskType == UserStudyUtility.TASK_TYPE_PATH)
		{
			return "withEyeTracking?(y/n)\tnode1\tnode2\tshortestPathSize\tshortestPath\tuserPath\tisUserPathValid?(y/n)\ttime";
		}
		else if(this.taskType == UserStudyUtility.TASK_TYPE_GAZE_CORRECTION)
		{
			return "withGazeCorrection?(y/n)\ttotalNode\tinitiallyUnselected\tuserSelected\tpercentile\ttime";
		}
		else
		{
			return "";
		}
	}
	public void OutputResultToFile()
	{
		String outputFilePath = this.filePath+".result";
		try {
			FileWriter fWriter = new FileWriter(outputFilePath);
			BufferedWriter bWriter = new BufferedWriter(fWriter);
			bWriter.write(this.getHeader());
			bWriter.write(this.fileOutputBuffer);
			bWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
