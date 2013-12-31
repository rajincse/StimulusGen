package data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.table.DefaultTableModel;

import perspectives.DataSource;
import perspectives.DefaultProperties.*;
import perspectives.Property;

/**
 *
 * @author mershack
 */
public class TableData extends DataSource {

    protected TableDistances table;
    boolean valid;
    
    

    public TableData(String name) {
        super(name);

        valid = false;
        

        table = new TableDistances();
        
        createProperties();
        
    }
    
    public void setTable(TableDistances t)
    {
    	table = t;
    }
    
    protected void createProperties()
    {

        try {

        	OpenFilePropertyType f = new OpenFilePropertyType();
            f.dialogTitle = "Open Data File";
            f.extensions = new String[3];
            f.extensions[0] = "*";
            f.extensions[1] = "txt";
            f.extensions[2] = "xml";



            Property<BooleanPropertyType> p0 = new Property<BooleanPropertyType>("JSON File");
            p0.setValue(new BooleanPropertyType(false));
            addProperty(p0);


            Property<BooleanPropertyType> p2 = new Property<BooleanPropertyType>("Col Headers?");
            p2.setValue(new BooleanPropertyType(true));
            this.addProperty(p2);
            
            Property<BooleanPropertyType> p21 = new Property<BooleanPropertyType>("Row Headers?");
            p21.setValue(new BooleanPropertyType(true));
            this.addProperty(p21);

            Property<OptionsPropertyType> p3 = new Property<OptionsPropertyType>("Delimiter");
            OptionsPropertyType o = new OptionsPropertyType();
            o.options = new String[3];
            o.options[0] = "TAB";
            o.options[1] = "SPACE";
            o.options[2] = "COMMA";
            o.selectedIndex = 0;
            p3.setValue(o);
            this.addProperty(p3);

            Property<OpenFilePropertyType> p1 = new Property<OpenFilePropertyType>("Tabular File");
            p1.setValue(f);
            addProperty(p1);




        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public <T> void propertyUpdated(Property p, T newvalue) {
        boolean js = ((BooleanPropertyType) getProperty("JSON File").getValue()).boolValue();
        
        if (js) {
            //se;
            this.removeProperty("Col Headers?");
            this.removeProperty("Row Headers?");
            this.removeProperty("Delimiter");

        }

        if (p.getName() == "Tabular File") {

           if(!js){
                boolean ch = ((BooleanPropertyType) getProperty("Col Headers?").getValue()).boolValue();
                boolean rh = ((BooleanPropertyType) getProperty("Row Headers?").getValue()).boolValue();


              //hide the others if it is a json file

             int delim = ((OptionsPropertyType) (getProperty("Delimiter").getValue())).selectedIndex;
            OpenFilePropertyType f = ((OpenFilePropertyType) newvalue);
             String d = "\t";   //default selection
             if (delim == 1) {
                d = " ";
              } else if (delim == 2) {
                d = ",";
              }

              table.fromFile(f.path, d, ch, rh);  //Function to Get the Data from File
           }
           else{   //get the data from a JSON format
               
           } 
            //Determine the properties of the file such as number of rows and columns and print them on the screen
            if (table.getColumnCount() != 0) {
                this.setLoaded(true);

                this.removeProperty("Tabular File");
                this.removeProperty("Delimiter");
                this.removeProperty("Col Headers?");
                this.removeProperty("JSON File");

                try {
                    Property<IntegerPropertyType> p1 = new Property<IntegerPropertyType>("# Columns");
                    p1.setValue(new IntegerPropertyType(table.getColumnCount()));
                    p1.setReadOnly(true);
                    this.addProperty(p1);

                    Property<IntegerPropertyType> p2 = new Property<IntegerPropertyType>("# Rows");
                    p2.setValue(new IntegerPropertyType(table.getRowCount()));
                    p2.setReadOnly(true);
                    this.addProperty(p2);

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /* Method to get the table */
    public TableDistances getTable() {
        return table;
    }

    /* method to set that the table is valid */
    public boolean isValid() {
        return valid;
    }
    
  
    
    

}
