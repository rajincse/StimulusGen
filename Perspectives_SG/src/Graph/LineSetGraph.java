package Graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import perspectives.DefaultProperties;
import perspectives.DefaultProperties.IntegerPropertyType;
import perspectives.DefaultProperties.PercentPropertyType;
import perspectives.Property;
import perspectives.PropertyType;
import util.BubbleSets;
import util.SplineFactory;

public class LineSetGraph extends ClusterGraphViewer{
	
	int[][] clusterTours;
	int lineThick = 5;
	double lineAlpha = 0.5;

	public LineSetGraph(String name, GraphData g) {
		super(name, g);
		// TODO Auto-generated constructor stub
		
		try {			
			
			Property<IntegerPropertyType> p44 = new Property<IntegerPropertyType>("Compute");
			p44.setValue(new IntegerPropertyType(0));
			this.addProperty(p44);	
			
			Property<IntegerPropertyType> p45 = new Property<IntegerPropertyType>("LineThick");
			p45.setValue(new IntegerPropertyType(5));
			this.addProperty(p45);	
			
			Property<PercentPropertyType> p46 = new Property<PercentPropertyType>("LineAlpha");
			p46.setValue(new PercentPropertyType(0.5));
			this.addProperty(p46);	
			
			}
			catch (Exception e) {		
				e.printStackTrace();
			}
	}
	
	public <T extends PropertyType> void propertyUpdated(Property p, T newvalue)
	{			
		if (p.getName() == "Compute")
		{
			exportClusters();
		}
		else if (p.getName() == "LineThick")
		{
			lineThick = ((IntegerPropertyType)newvalue).intValue();
		}
		else if (p.getName() == "LineAlpha")
		{
			lineAlpha = ((PercentPropertyType)newvalue).getRatio();
		}
		else 
			super.propertyUpdated(p,newvalue);
	}
	
	private void exportClusters()
	{
		clusterTours = new int[clusterTypes.size()][];
		for (int i=0; i<this.clusterTypes.size(); i++)
		{
			FileWriter fw;
			
			int[] members = null;
			try {
				fw = new FileWriter("./c"+i+".txt");
			
			BufferedWriter bw = new BufferedWriter(fw);
			
			int ct = 0;
			for (int j=0; j<clusters.length; j++)
				if (clusters[j].equals(clusterTypes.get(i)))
					ct++;
			
			fw.write("NAME : " + i + "\n");
			fw.write("COMMENT : for line sets\n");
			fw.write("TYPE : TSP\n");
			fw.write("DIMENSION : "+ct+"\n");
			fw.write("EDGE_WEIGHT_TYPE : EUC_2D\n");
			fw.write("NODE_COORD_SECTION\n");		
			
			members = new int[ct];
			ct = 1;			
			
			for (int j=0; j<clusters.length; j++)
			{
				if (clusters[j].equals(clusterTypes.get(i)))
				{
					int x = getNodeX(j);
					int y = getNodeY(j);
					
					bw.write(""+ct+" " + x + " " + y + "\n");
					members[ct-1] = j;
					ct++;
					
				}
			}
			bw.write("EOF\n");	
			
			bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			FileWriter fw2;
			try {
				fw2 = new FileWriter("./p"+i+".txt");
			
			BufferedWriter bw2 = new BufferedWriter(fw2);
			fw2.write("PROBLEM_FILE= c" + i + ".txt\n");
			fw2.write("OPTIMUM 378032\n");
			fw2.write("MOVE_TYPE = 5\n");
			fw2.write("PATCHING_C = 3\n");
			fw2.write("PATCHING_A = 2\n");
			fw2.write("RUNS = 10\n");
			fw2.write("OUTPUT_TOUR_FILE = output" + i + ".txt\n");
			
			bw2.close();
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try
	        {            
	            Runtime rt = Runtime.getRuntime();
	            Process proc = rt.exec("lkh.exe p"+i+".txt");
	            InputStream stdin = proc.getInputStream();
	            InputStreamReader isr = new InputStreamReader(stdin);
	           
	            String line = null;
	            System.out.println("<OUTPUT>");
	           // while ( (line = br.readLine()) != null)
	            //    System.out.println(line);
	            System.out.println("</OUTPUT>");
	           // int exitVal = proc.waitFor();            
	           // System.out.println("Process exitValue: " + exitVal);
	            
	            FileReader fr = new FileReader("output" + i + ".txt");
	            BufferedReader br = new BufferedReader(fr);
	            
	            boolean coords = false;
	            int ct = 0;
	            clusterTours[i] = new int[members.length];
	            while ((line = br.readLine()) != null)
	            {
	            	if (coords)
	            	{
	            		int c = Integer.parseInt(line);
	            		if (c < 0) break;
	            		c--;
	            		clusterTours[i][ct] = members[c];
	            		ct++;
	            	}
	            	else if (line.equals("TOUR_SECTION"))
	            		coords = true;
	            }
	            
	            br.close();
	        } catch (Throwable t)
	          {
	            t.printStackTrace();
	          }

		}
	}
	
	public void render(Graphics2D g) {
		if (clusterTours != null)
		{
			for (int i=0; i<clusterTours.length; i++)
			{
				double[] control = new double[3*clusterTours[i].length];
				
				for (int j=0; j<clusterTours[i].length; j++)
				{
					control[3*j] = getNodeX(clusterTours[i][j]);
					control[3*j+1] = getNodeY(clusterTours[i][j]);
					control[3*j+2] = 0;
				}
				
				double[] spline1 = SplineFactory.createCatmullRom(control, 10);
				
				int[] sx = new int[spline1.length/3];
				int[] sy = new int[spline1.length/3];
				
				for (int j=0; j<sx.length; j++)
				{
					sx[j] = (int)spline1[3*j];
					sy[j] = (int)spline1[3*j+1];
				}
				
				g.setStroke(new BasicStroke(lineThick));
				Color c = this.getColor(clusterTours[i][0]);
				Color c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), (int)(lineAlpha*255));
				g.setColor(c2);
				
				g.drawPolyline(sx, sy, sx.length);
			}				
		}
		super.render(g);
	}

}
