package perspectives;

public abstract class PropertyType{
	public abstract <T extends PropertyType> T copy();
	public abstract String typeName();
	public abstract String serialize();
	public abstract PropertyType deserialize(String s);
}
