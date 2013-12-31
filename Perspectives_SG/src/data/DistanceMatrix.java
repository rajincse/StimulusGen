package data;

import java.util.Date;

public class DistanceMatrix implements DistancedPoints {
	
	float [][] distances = null;
	String[] ids = null;
	
	private long lastUpdate;
	
	public DistanceMatrix(int nrElem)
	{
		distances = new float[nrElem][];
		for (int i=1; i<distances.length; i++)
			distances[i] = new float[i];
		
		ids = new String[nrElem];

		lastUpdate = (new Date()).getTime();
		
	}


	@Override
	public int getCount() {
		return distances.length;
	}

	@Override
	public float getDistance(int index1, int index2) {		
		if (index1 < index2)	return distances[index2][index1];
		else return distances[index1][index2];
	}
	
	public void setDistance(int index1, int index2, float v)
	{
		if (index1 < index2)	distances[index2][index1] = v;
		else distances[index1][index2] = v;
		lastUpdate = (new Date()).getTime();
	}

	@Override
	public String getPointId(int index) {
		// TODO Auto-generated method stub
		return ids[index];
	}
	
	public void setPointId(int index, String id)
	{
		ids[index] = id;
	}

	@Override
	public long getLastUpdateTime() {
		// TODO Auto-generated method stub
		return 0;
	}

}
