package data;

public class TableDistances extends Table implements DistancedPoints {
	
	float [][] distances = null;

	public int getCount() {
		return this.getRowCount();
	}

	public float getDistance(int index1, int index2) {
		
		if (distances == null)
			computeDistances();
		
		if (index1 < index2)	return distances[index2][index1];
		else return distances[index1][index2];
	}

	public String getPointId(int index) {
		return getRowName(index);
	}
	
	public void computeDistances()
	{
		distances = new float[getCount()][];
		
		for (int i=1; i<distances.length; i++)
		{
			distances[i] = new float[i];
			for (int j=0; j<i; j++)
				distances[i][j] = computeDistance(i,j);
		}		
	}
	
	public float computeDistance(int i1, int i2)
	{
		double d = 0;
		for (int i=0; i<this.getColumnCount(); i++)
		{
			Object v1 = this.getValueAt(i1, i);
			Object v2 = this.getValueAt(i2, i);
			
			if (v1.getClass() == String.class && v2.getClass() == String.class)
			{
				if (!v1.equals(v2))
					d = d + 1;
			}
			else if (v1.getClass() == Double.class && v2.getClass() == Double.class)
			{
				d = d + (((Double)v1).doubleValue() - ((Double)v2).doubleValue()) * (((Double)v1).doubleValue() - ((Double)v2).doubleValue());
			}				
		}
		
		return (float) Math.sqrt(d);
	}

}
