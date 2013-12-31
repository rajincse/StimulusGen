package util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import perspectives.DefaultProperties.*;
import perspectives.Property;
import perspectives.PropertyType;
import perspectives.Viewer2D;

public class TileViewer extends Viewer2D{


	ImageTiler imageTiler = null;
	
	BufferedImage img = null;
	
	boolean setview = false;
	
	
	public TileViewer(String name) {
		super(name);
		
		try{
			OpenFilePropertyType f = new OpenFilePropertyType();
			Property<OpenFilePropertyType> p3 = new Property<OpenFilePropertyType>("Open.Image");
			p3.setValue(f);
			this.addProperty(p3);	
			
			OpenFilePropertyType f2 = new OpenFilePropertyType();
			f2.onlyDirectories = true;
			Property<OpenFilePropertyType> p4 = new Property<OpenFilePropertyType>("Open.Tiles");
			p4.setValue(f2);
			this.addProperty(p4);	
						
			
		}catch(Exception e)
		{
			
		}
	}

	@Override
	public <T extends PropertyType> void propertyUpdated(Property p, T newvalue) {
		if (p.getName() == "Open.Image")
		{
			try {
			    img = ImageIO.read(new File(((OpenFilePropertyType)newvalue).path));
			    
			    imageTiler = null;
			    
			    this.setZoom(800./Math.max(img.getWidth(), img.getHeight()));
			    this.setTranslation(-img.getWidth()/2, -img.getHeight()/2);
			    
		
			    if (this.getProperty("Tile Pyramid") == null)
			    {		
			    	OpenFilePropertyType f2 = new OpenFilePropertyType();
					f2.onlyDirectories = true;
					Property<OpenFilePropertyType> p4 = new Property<OpenFilePropertyType>("Tile Pyramid");
					p4.setValue(f2);
					try {
						this.addProperty(p4);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    }
			    setview = false;
				
			    
			} catch (IOException e) {
			}
		}
		else if (p.getName() == "Tile Pyramid")
		{
			imageTiler = new ImageTiler(img, this.getTaskObserverDialog());	
			imageTiler.createTilePyramid(256,0,0,img.getWidth(),img.getHeight(), 20, new File(((OpenFilePropertyType)newvalue).path));
			setview = false;
			
			
			this.removeProperty("Tile Pyramid");
		}
		else if (p.getName() == "Open.Tiles")
		{
			imageTiler = new ImageTiler(((OpenFilePropertyType)newvalue).path, this.getTaskObserverDialog());
			setview = false;
		}
		else
			super.propertyUpdated(p, newvalue);
	}
	
	
	
	@Override
	public void render(Graphics2D g) {
		
		if (imageTiler == null)
		{
			if (img != null)
				g.drawImage(img,0,0,null);
		}
		else
		{
			if (imageTiler.pyramidCreator != null && imageTiler.pyramidCreator.done)
			{
				if (setview == false)
				{
					int w = imageTiler.tiles[0].length * imageTiler.tileSize;
					int h = w;
					double z = 800./Math.max(w, h);
					
					System.out.println(w + " " + h + " " + z);
					
					
					
					  this.setZoom(z);
					 // this.setTranslation((int)((-w+800)/z), (int)((-h+800)/z));
					  this.setTranslation(-w+(int)(400/z), -h+(int)(400/z));
					  setview = true;
					   
				}
				imageTiler.render(g);
			}
			else if (imageTiler.pyramidCreator == null)
				imageTiler.render(g);
			
			
		}
	}

	@Override
	public void simulate() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Color backgroundColor()
	{
		return Color.black;
	}

}
