package Graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

import perspectives.DefaultProperties;
import perspectives.DefaultProperties.OpenFilePropertyType;
import perspectives.Property;
import perspectives.PropertyType;
import util.BubbleSets;
import util.ImageTiler;
import util.Util;
import util.Points2DViewer.PointAspectType;

public class ClusterGraphViewer extends GraphViewer {
	
	
	String[] clusters;	
	ArrayList<String> clusterTypes;

	public ClusterGraphViewer(String name, GraphData g) {
		super(name, g);
		
	try {
	
		OpenFilePropertyType fff = new OpenFilePropertyType();
	Property<OpenFilePropertyType> p333 = new Property<OpenFilePropertyType>("Load Clusters");
	p333.setValue(fff);
	this.addProperty(p333);	
	
	DefaultProperties.OpenFilePropertyType ffff = new DefaultProperties.OpenFilePropertyType();
	Property<DefaultProperties.OpenFilePropertyType> p3333 = new Property<DefaultProperties.OpenFilePropertyType>("Load Cluster Colors");
	p3333.setValue(ffff);
	this.addProperty(p3333);
	}
	catch (Exception e) {		
		e.printStackTrace();
	}
	}
	
	public <T extends PropertyType> void propertyUpdated(Property p, T newvalue)
	{
		if (p.getName() == "Load Clusters")
		{
			ArrayList<String> nodes = graph.getNodes();
			
			clusters = new String[nodes.size()];
			
			clusterTypes = new ArrayList<String>();
			
			try{
			 FileInputStream fstream = new FileInputStream(((OpenFilePropertyType)newvalue).path);
			 DataInputStream in = new DataInputStream(fstream);
			 BufferedReader br = new BufferedReader(new InputStreamReader(in));
			 String s;
			 while ((s = br.readLine()) != null)
			 {
				s = s.trim();
				
				String[] split = s.split("\t");
				
				if (split.length < 2) continue;
				
				int index = nodes.indexOf(split[0].trim());
				if (index < 0) continue;
				
				String c = split[1].trim();
				if (clusterTypes.indexOf(c) < 0)
					clusterTypes.add(c);
				
				clusters[index] = c;
			 }
			 in.close();
			 
			 Random r = new Random();
			 for (int i=0; i<clusterTypes.size(); i++)
			 {
				 Color c = new Color(r.nextFloat(), r.nextFloat(), r.nextFloat());
				 for (int j=0; j<clusters.length;j++)
					 if (clusters[j] == null)
						 this.setColor(j, Color.red);
					 else if (clusters[j].equals(clusterTypes.get(i)))
						 this.setColor(j, c);

			 }
			}
			catch(Exception e){}		
		}
		else if (p.getName() == "Load Cluster Colors")
		{
			if (clusters != null)
			{
				ArrayList<String> nodes = graph.getNodes();
								
				try{
				 FileInputStream fstream = new FileInputStream(((OpenFilePropertyType)newvalue).path);
				 DataInputStream in = new DataInputStream(fstream);
				 BufferedReader br = new BufferedReader(new InputStreamReader(in));
				 String s;
				 while ((s = br.readLine()) != null)
				 {
					s = s.trim();
					
					String[] split = s.split("\t");
					
					int index = clusterTypes.indexOf(split[0]);
					
					if (index < 0) continue;
					
					String[] cs = split[1].split(",");
					
					// Color c = new Color(Integer.parseInt(cs[0])-20, Integer.parseInt(cs[1])-20, Integer.parseInt(cs[2])-20);
					 Color c = new Color(Integer.parseInt(cs[0]), Integer.parseInt(cs[1]), Integer.parseInt(cs[2]));
					 
					 for (int j=0; j<clusters.length;j++)
						 if (clusters[j] == null)
							 this.setColor(j, Color.red);
						 else if (clusters[j].equals(clusterTypes.get(index)))
							 this.setColor(j, c);
				 }
				 in.close();
				}catch(Exception e){}	
			}
		}
		else 
			super.propertyUpdated(p,newvalue);
	}
}
