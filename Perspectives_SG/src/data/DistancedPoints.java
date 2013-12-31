package data;

public interface DistancedPoints 
{
	public abstract int getCount();
	public abstract float getDistance(int index1, int index2);
	
	public abstract String getPointId(int index);
	
	public abstract long getLastUpdateTime();
}
