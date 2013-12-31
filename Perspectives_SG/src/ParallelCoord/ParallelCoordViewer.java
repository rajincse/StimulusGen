/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ParallelCoord;

import java.awt.Color;
import java.awt.Graphics2D;

import perspectives.DefaultProperties;
import perspectives.DefaultProperties.ColorPropertyType;
import perspectives.DefaultProperties.IntegerPropertyType;
import perspectives.DefaultProperties.PercentPropertyType;
import perspectives.Property;
import perspectives.Viewer2D;
import data.TableData;
//import FloatMatrix;

/**
 *
 * @author mershack
 */
public class ParallelCoordViewer extends ParallelCoordDrawer {

   

    public ParallelCoordViewer(String name, TableData tb) {

        super(name,tb);
        
        try{
                      
			Property<ColorPropertyType> p1 = new Property<ColorPropertyType>("Appearance.Vertical Lines Color");
			p1.setValue(new ColorPropertyType(new Color(200,150,150)));
			this.addProperty(p1);
			
			Property<ColorPropertyType> p2 = new Property<ColorPropertyType>("Appearance.Data edge Color");
			p2.setValue(new ColorPropertyType(new Color(200,150,150)));
			this.addProperty(p2);
			
                        Property<PercentPropertyType> p6 = new Property<PercentPropertyType>("Appearance.Header Angle");
                        p6.setValue(new PercentPropertyType(0));
                        this.addProperty(p6);

			
			Property<PercentPropertyType> p4 = new Property<DefaultProperties.PercentPropertyType>("Appearance.Edge Alpha");
			p4.setValue(new DefaultProperties.PercentPropertyType(1.0));
			this.addProperty(p4);
                        
                        
			Property<IntegerPropertyType> p = new Property<IntegerPropertyType>("Appearance.Width");
			p.setValue(new IntegerPropertyType(100));
			this.addProperty(p);		
			
                        
             Property<IntegerPropertyType> p5 = new Property<IntegerPropertyType>("Appearance.Height");
			p5.setValue(new IntegerPropertyType(300));
			this.addProperty(p5);		
			
                                    
        }catch(Exception e){
            e.printStackTrace();
        }
                
        
        
        
    }
    
    public <T> void propertyUpdated(Property p, T newvalue)
	{
		if (p.getName() == "Appearance.Vertical Lines Color"){
                    this.setVerticalLinesColor((Color) newvalue);
                }
			
		if (p.getName() == "Appearance.Data edge Color")
                    this.setDataLinesColor((Color) newvalue);                
                
               if (p.getName() == "Appearance.Edge Alpha")
		{
			int alpha = (int)(255.*((PercentPropertyType)newvalue).getRatio());
			this.setDataLinesAlpha(alpha);
			
		}  
                
		if(p.getName() == "Appearance.Width"){
                    this.setVerticalLinesSeparation((Integer) newvalue);
                    
                }
                
                if(p.getName() == "Appearance.Height")
                {
                    this.setVerticalLinesHeight((Integer) newvalue );
                }

                if(p.getName().equals("Appearance.Header Angle")){
                	double angle = ((PercentPropertyType)newvalue).getRatio() * 90;
                    this.setHeaderAngle((int)angle);
                }
                
	}
    
    
    

    @Override
    public void simulate() {
        /*
         if (size <= 97)
         dir = -dir;
         else if (size >= 103)
         dir = -dir;
		
         size = size + dir; */
    }
}
