import perspectives.DataSource;
import perspectives.DataSourceFactory;


public class FloatMatrixFactory extends DataSourceFactory{

	@Override
	public DataSource create(String name) {
		return new FloatMatrix(name);
	}

	@Override
	public String creatorType() {
		// TODO Auto-generated method stub
		return "FloatMatrix";
	}

}
