package util;

public class Vector3D {
	
	public float x,y,z;
	public Vector3D(float x, float y, float z)
	{
		this.x = x ;
		this.y = y;
		this.z = z;
	}

	public Vector3D minus(Vector3D other)
	{
		return new Vector3D(x-other.x, y-other.y, z-other.z);
	}
	
	public Vector3D plus(Vector3D other)
	{
		return new Vector3D(x+other.x, y+other.y, z+other.z);
	}
	
	public Vector3D times(float s)
	{
		return new Vector3D(x*s, y*s, z*s);
	}
	
	public Vector3D div(float s)
	{
		return new Vector3D(x/s, y/s, z/s);
	}
	
	public float length()
	{
		return (float)Math.sqrt(x*x + y*y + z*z);
	}
	
	public void normalize()
	{
		float l = length();
		x /= l;
		y /= l;
		z /= l;
	}
	
	public Vector3D cross(Vector3D other)
	{
        float tx = (y * other.z) - (z * other.y);
        float ty = (z * other.x) - (x * other.z);
        float tz = (x * other.y) - (y * other.x);
       
       return new Vector3D(tx,ty,tz);
	}
	
	public float dot(Vector3D other)
	{
		return x*other.x + y*other.y  + z*other.z;
	}
	
	public float magnitude()
	{
		return (float) Math.sqrt( x*x+y*y+z*z);
	}
}
