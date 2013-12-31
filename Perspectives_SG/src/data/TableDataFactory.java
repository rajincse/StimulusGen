/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package data;


import perspectives.DataSource;
import perspectives.DataSourceFactory;

/**
 *
 * @author mershack
 */
public class TableDataFactory extends DataSourceFactory {
   @Override
	public DataSource create(String name) {
		return new TableData(name);
	}

	@Override
	public String creatorType() {
		// TODO Auto-generated method stub
		return "TableData";
	} 
}
