package perspectives;
/**
 * 
 * @author rdjianu
 * For each new type of DataSource that is created a factory must be defined too. This factory will be added to the Perspectives Environment so that users can create this type of DataSources.
 */
public abstract class DataSourceFactory {
	
	public abstract DataSource create(String name);
	
	/**
	 * 
	 * @return This String will be displayed to users as the type of DataSource that can be created.
	 */
	public abstract String creatorType();

	public String description() {
		return "No Description";
	}
	
}
