package perspectives;

public interface PropertyChangeListener {
	public void propertyAdded(PropertyManager pm, Property p);
	public void propertyRemoved(PropertyManager pm, Property p);
	public void propertyValueChanged(PropertyManager pm, Property p, PropertyType newValue);
	public void propertyReadonlyChanged(PropertyManager pm, Property p, boolean newReadOnly);
	public void propertyVisibleChanged(PropertyManager pm, Property p, boolean newVisible);
	public void propertyPublicChanged(PropertyManager pm, Property p, boolean newPublic);
	
}
