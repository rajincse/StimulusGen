package util;

public class Vector2D {
	
	public double x,y;
	
	public Vector2D(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	
	public Vector2D()
	{
		this.x = 0;
		this.y = 0;
	}
	
	public static Vector2D random()
	{
		return new Vector2D(Math.random(), Math.random());
	}
	
	public double length()
	{
		return Math.sqrt(x*x + y*y);
	}
	
	public void divide(double scalar)
	{
		x /= scalar;
		y /= scalar;
	}
	
	public void multiply(double scalar)
	{
		x *= scalar;
		y *= scalar;
	}
	
	public void add(double scalar)
	{
		x += scalar;
		y += scalar;
	}
	
	public void substract(double scalar)
	{
		add(-scalar);
	}
	
	public void add(Vector2D other)
	{
		x += other.x;
		y += other.y;
	}
	
	public void substract(Vector2D other)
	{
		x -= other.x;
		y -= other.x;
	}	
	
	public double dot(Vector2D other)
	{
		return x*other.x + y*other.y;
	}	
	
	
	

}
