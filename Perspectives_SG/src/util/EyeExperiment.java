package util;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;

import javax.imageio.ImageIO;

import perspectives.DefaultProperties.*;
import perspectives.Property;
import perspectives.PropertyType;
import perspectives.Viewer2D;



class GazeServerEyeExperiment implements Runnable
{
	public static final int PORT = 9876;
	EyeExperiment fgv;
	
	Socket socket;
	private ServerSocket serverSocket;
	public GazeServerEyeExperiment(EyeExperiment fgv)
	{
		this.fgv = fgv;
		
		
		this.serverSocket = null;
		    try {
		       serverSocket = new ServerSocket(GazeServer.PORT);}
		    catch (IOException se) 
		    {
		       System.err.println("Can not start listening on port " + GazeServer.PORT);
		       se.printStackTrace();
		       System.exit(-1);
		    }
		    
		 
	}
	
	  
	    private BufferedReader mIn;
	    private String message;
	    private String decoded = null;


	   

	@Override
	   public void run() {

		if(this.serverSocket != null)
		{
	       try  
	       {
	           socket = serverSocket.accept();
	           mIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	       }
	       catch (IOException ioe) 
	       {
	           ioe.printStackTrace();
	       }
		}
		System.out.println("here");
        
        while (true) {
            try {
                message = mIn.readLine();
               // System.out.println("Got Gaze: "+message);
                if (message == null)
                {
                	continue;
                }
                    
                try {
                    decoded = URLDecoder.decode(message, "UTF-8");
                    fgv.gotSample(decoded);
                } catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                }
                catch(Exception ex)
                {
                	ex.printStackTrace();
                }
               // System.out.println(decoded);
                
                
	     
	              

            } 

            catch (IOException e) {
                break;
            }

        }

      
    }	
}
	

public class EyeExperiment extends Viewer2D {
	
	ArrayList<String> events = null;
	
	String folder = "c:\\";
	
	int step = 0;
	
	Color bgColor;
	
	long nextTime = -1;
	
	int prevsec = -1;
	
	public EyeExperiment(String name) {
		super(name);
		
		try {
			
			events = new ArrayList<String>();
			
			OpenFilePropertyType f2 = new OpenFilePropertyType();
			f2.onlyDirectories = true;
			Property<OpenFilePropertyType> p4 = new Property<OpenFilePropertyType>("Folder");
			p4.setValue(f2);
			this.addProperty(p4);
			
			Property<IntegerPropertyType> stp = new Property<IntegerPropertyType>("Next");
			stp.setValue(new IntegerPropertyType(0));
			this.addProperty(stp);
			
			GazeServerEyeExperiment s = new GazeServerEyeExperiment(this);
			Thread t = new Thread(s);
			t.start();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public void startTimer(int sec)
	{
		nextTime = new Date().getTime() + sec*1000;
	}
	
	
	@Override
	public <T extends PropertyType> void propertyUpdated(Property p, T newvalue) {
		if (p.getName() == "Folder")
		{
			folder = ((OpenFilePropertyType)newvalue).path;
		}
		else if (p.getName() == "Next")
		{
			
			nextStep();
		}
		else
			super.propertyUpdated(p, newvalue);
	}
	

	@Override
	public void render(Graphics2D g) {
		
		g.setColor(Color.black);
		
		long now = new Date().getTime();
		
		g.setFont(g.getFont().deriveFont(148.0f));
		
		if (nextTime >= 0)
		{
			int secleft = (int)((nextTime - now)/1000);
			
			if (prevsec != secleft && secleft < 5)
				Toolkit.getDefaultToolkit().beep();
			
			if (secleft % 2 == 0)
			{
				g.setFont(g.getFont().deriveFont(48.0f));				
			}
			else
				g.setFont(g.getFont().deriveFont(38.0f));
			
			if (secleft <= 0)
			{
				secleft = 0;
				g.setColor(Color.red);
				Toolkit.getDefaultToolkit().beep();
			}
			
			prevsec = secleft;
			

			
			g.drawString(""+secleft, 200, 50);
			
		}
		
		g.setFont(g.getFont().deriveFont(148.0f));
		g.setColor(Color.black);
		if (step == 1)
		{
			bgColor = new Color(255,255,255);
			g.drawString("3", 400, 400);
		}
		else if (step == 2)			
			g.drawString("8", 400, 400);
		else if (step == 3)			
			g.drawString("2", 400, 400);
		else if (step == 4)			
			g.drawString("1", 400, 400);
		else if (step == 5)			
			g.drawString("5", 400, 400);
		else if (step == 6)			
			g.drawString("2", 400, 400);
		else if (step == 7)			
			g.drawString("9", 400, 400);
		else if (step == 8)			
			g.drawString("7", 400, 400);
		else if (step == 9)			
			g.drawString("6", 400, 400);
		else if (step == 10)			
			g.drawString("1", 400, 400);
		else if (step == 11)			
			g.drawString("4", 400, 400);
		else if (step == 12)			
			g.drawString("9", 400, 400);
		else if (step == 13)			
			g.drawString("0", 400, 400);
		else if (step == 14)			
			g.drawString("3", 400, 400);
		else if (step == 15)			
			g.drawString("6", 400, 400);
		else if (step == 16)			
			g.drawString("7", 400, 400);
		else if (step == 17)			
			g.drawString("5", 400, 400);	
		else if (step == 18)			
			g.drawString("1", 400, 400);	
		else if (step == 19)			
			g.drawString("2", 400, 400);	
		else if (step == 20)			
			g.drawString("8", 400, 400);
		else if (step == 21)			
			g.drawString("945", 400, 400);
		else if (step == 22)			
			g.drawString("321", 400, 400);
		else if (step == 23)			
			g.drawString("873", 400, 400);
		else if (step == 24)			
			g.drawString("258", 400, 400);
		else if (step == 25)			
			g.drawString("483", 400, 400);
		else if (step == 26)			
			g.drawString("294", 400, 400);
		else if (step == 27)			
			g.drawString("673", 400, 400);
		else if (step == 28)			
			g.drawString("312", 400, 400);
		else if (step == 29)			
			g.drawString("907", 400, 400);
		else if (step == 30)			
			g.drawString("122", 400, 400);
		else if (step == 31)			
			g.drawString("145", 400, 400);
		else if (step == 32)			
			g.drawString("361", 400, 400);
		else if (step == 33)			
			g.drawString("973", 400, 400);
		else if (step == 34)			
			g.drawString("208", 400, 400);
		else if (step == 35)			
			g.drawString("481", 400, 400);
		else if (step == 36)			
			g.drawString("231", 400, 400);
		else if (step == 37)			
			g.drawString("693", 400, 400);
		else if (step == 38)			
			g.drawString("300", 400, 400);
		else if (step == 39)			
			g.drawString("107", 400, 400);
		else if (step == 40)			
			g.drawString("102", 400, 400);	
		else if (step == 41)
		{	
			bgColor = new Color(150,150,150);	
			g.drawString("3", 400, 400);
		}
		else if (step == 42)			
			g.drawString("8", 400, 400);
		else if (step == 43)			
			g.drawString("2", 400, 400);
		else if (step == 44)			
			g.drawString("1", 400, 400);
		else if (step == 45)			
			g.drawString("5", 400, 400);
		else if (step == 46)			
			g.drawString("2", 400, 400);
		else if (step == 47)			
			g.drawString("9", 400, 400);
		else if (step == 48)			
			g.drawString("7", 400, 400);
		else if (step == 49)			
			g.drawString("6", 400, 400);
		else if (step == 50)			
			g.drawString("1", 400, 400);
		else if (step == 51)			
			g.drawString("4", 400, 400);
		else if (step == 52)			
			g.drawString("9", 400, 400);
		else if (step == 53)			
			g.drawString("0", 400, 400);
		else if (step == 54)			
			g.drawString("3", 400, 400);
		else if (step == 55)			
			g.drawString("6", 400, 400);
		else if (step == 56)			
			g.drawString("7", 400, 400);
		else if (step == 57)			
			g.drawString("5", 400, 400);	
		else if (step == 58)			
			g.drawString("1", 400, 400);	
		else if (step == 59)			
			g.drawString("2", 400, 400);	
		else if (step == 60)			
			g.drawString("8", 400, 400);
		else if (step == 61)			
			g.drawString("945", 400, 400);
		else if (step == 62)			
			g.drawString("321", 400, 400);
		else if (step == 63)			
			g.drawString("873", 400, 400);
		else if (step == 64)			
			g.drawString("258", 400, 400);
		else if (step == 65)			
			g.drawString("483", 400, 400);
		else if (step == 66)			
			g.drawString("294", 400, 400);
		else if (step == 67)			
			g.drawString("673", 400, 400);
		else if (step == 68)			
			g.drawString("312", 400, 400);
		else if (step == 69)			
			g.drawString("907", 400, 400);
		else if (step == 70)			
			g.drawString("122", 400, 400);
		else if (step == 71)			
			g.drawString("145", 400, 400);
		else if (step == 72)			
			g.drawString("361", 400, 400);
		else if (step == 73)			
			g.drawString("973", 400, 400);
		else if (step == 74)			
			g.drawString("208", 400, 400);
		else if (step == 75)			
			g.drawString("481", 400, 400);
		else if (step == 76)			
			g.drawString("231", 400, 400);
		else if (step == 77)			
			g.drawString("693", 400, 400);
		else if (step == 78)			
			g.drawString("300", 400, 400);
		else if (step == 79)			
			g.drawString("107", 400, 400);
		else if (step == 80)			
			g.drawString("102", 400, 400);	
		else if (step == 81)			
			g.drawString("2*16", 400, 400);
		else if (step == 82)			
			g.drawString("9*14", 400, 400);
		else if (step == 83)			
			g.drawString("4*17", 400, 400);
		else if (step == 84)			
			g.drawString("6*12", 400, 400);
		else if (step == 85)			
			g.drawString("7*18", 400, 400);
		else if (step == 86)			
			g.drawString("8*11", 400, 400);
		else if (step == 87)			
			g.drawString("3*17", 400, 400);	
		else if (step == 88)			
			g.drawString("4*16", 400, 400);	
		else if (step == 89)			
			g.drawString("8*14", 400, 400);	
		else if (step == 90)			
			g.drawString("5*17", 400, 400);
		else if (step == 91)			
			g.drawString("9*4", 400, 400);
		else if (step == 92)			
			g.drawString("7*3", 400, 400);
		else if (step == 93)			
			g.drawString("8*9", 400, 400);
		else if (step == 94)			
			g.drawString("3*6", 400, 400);
		else if (step == 95)			
			g.drawString("5*8", 400, 400);
		else if (step == 96)			
			g.drawString("6*7", 400, 400);
		else if (step == 97)			
			g.drawString("3*9", 400, 400);
		else if (step == 98)			
			g.drawString("2*8", 400, 400);
		else if (step == 99)			
			g.drawString("7*8", 400, 400);
		else if (step == 100)			
			g.drawString("4*7", 400, 400);
		else if (step == 101)			
			g.drawString("21*11", 400, 400);
		else if (step == 102)			
			g.drawString("17*15", 400, 400);
		else if (step == 103)			
			g.drawString("15*12", 400, 400);
		else if (step == 104)			
			g.drawString("14*15", 400, 400);
		else if (step == 105)			
			g.drawString("11*12", 400, 400);
		else if (step == 106)			
			g.drawString("15*21", 400, 400);
		else if (step == 107)			
			g.drawString("14*16", 400, 400);
		else if (step == 108)			
			g.drawString("15*31", 400, 400);
		else if (step == 109)			
			g.drawString("25*44", 400, 400);
		else if (step == 110)			
			g.drawString("16*12", 400, 400);			
		else if (step == 111)			
			g.drawString("Sentence 1", 400, 400);
		else if (step == 112)			
			g.drawString("Sentence 1", 400, 400);
		else if (step == 113)			
			g.drawString("Sentence 1", 400, 400);
		else if (step == 114)			
			g.drawString("Sentence 1", 400, 400);
		else if (step == 115)			
			g.drawString("Sentence 1", 400, 400);
		else if (step == 116)			
			g.drawString("Sentence 1", 400, 400);
		else if (step == 117)			
			g.drawString("Sentence 1", 400, 400);
		else if (step == 118)			
			g.drawString("Sentence 1", 400, 400);
		else if (step == 119)			
			g.drawString("Sentence 1", 400, 400);
		else if (step == 120)			
			g.drawString("Question 1", 400, 400);
		else if (step == 121)	
			g.drawString("3", 400, 400);
		else if (step == 122)			
			g.drawString("8", 400, 400);
		else if (step == 123)			
			g.drawString("2", 400, 400);
		else if (step == 124)			
			g.drawString("1", 400, 400);
		else if (step == 125)			
			g.drawString("5", 400, 400);
		else if (step == 126)			
			g.drawString("2", 400, 400);
		else if (step == 127)			
			g.drawString("9", 400, 400);
		else if (step == 128)			
			g.drawString("7", 400, 400);
		else if (step == 129)			
			g.drawString("6", 400, 400);
		else if (step == 130)			
			g.drawString("1", 400, 400);
		else if (step == 131)			
			g.drawString("4", 400, 400);
		else if (step == 132)			
			g.drawString("9", 400, 400);
		else if (step == 133)			
			g.drawString("0", 400, 400);
		else if (step == 134)			
			g.drawString("3", 400, 400);
		else if (step == 135)			
			g.drawString("6", 400, 400);
		else if (step == 136)			
			g.drawString("7", 400, 400);
		else if (step == 137)			
			g.drawString("5", 400, 400);	
		else if (step == 138)			
			g.drawString("1", 400, 400);	
		else if (step == 139)			
			g.drawString("2", 400, 400);	
		else if (step == 140)			
			g.drawString("8", 400, 400);
		else if (step == 141)			
			g.drawString("945", 400, 400);
		else if (step == 142)			
			g.drawString("321", 400, 400);
		else if (step == 143)			
			g.drawString("873", 400, 400);
		else if (step == 144)			
			g.drawString("258", 400, 400);
		else if (step == 145)			
			g.drawString("483", 400, 400);
		else if (step == 146)			
			g.drawString("294", 400, 400);
		else if (step == 147)			
			g.drawString("673", 400, 400);
		else if (step == 148)			
			g.drawString("312", 400, 400);
		else if (step == 149)			
			g.drawString("907", 400, 400);
		else if (step == 150)			
			g.drawString("122", 400, 400);
		else if (step == 151)			
			g.drawString("145", 400, 400);
		else if (step == 152)			
			g.drawString("361", 400, 400);
		else if (step == 153)			
			g.drawString("973", 400, 400);
		else if (step == 154)			
			g.drawString("208", 400, 400);
		else if (step == 155)			
			g.drawString("481", 400, 400);
		else if (step == 156)			
			g.drawString("231", 400, 400);
		else if (step == 157)			
			g.drawString("693", 400, 400);
		else if (step == 158)			
			g.drawString("300", 400, 400);
		else if (step == 159)			
			g.drawString("107", 400, 400);
		else if (step == 160)			
			g.drawString("102", 400, 400);	
		else if (step == 161)			
			g.drawString("2*16", 400, 400);
		else if (step == 162)			
			g.drawString("9*14", 400, 400);
		else if (step == 163)			
			g.drawString("4*17", 400, 400);
		else if (step == 164)			
			g.drawString("6*12", 400, 400);
		else if (step == 165)			
			g.drawString("7*18", 400, 400);
		else if (step == 166)			
			g.drawString("8*11", 400, 400);
		else if (step == 167)			
			g.drawString("3*17", 400, 400);	
		else if (step == 168)			
			g.drawString("4*16", 400, 400);	
		else if (step == 169)			
			g.drawString("8*14", 400, 400);	
		else if (step == 170)			
			g.drawString("5*17", 400, 400);
		else if (step == 171)			
			g.drawString("9*4", 400, 400);
		else if (step == 172)			
			g.drawString("7*3", 400, 400);
		else if (step == 173)			
			g.drawString("8*9", 400, 400);
		else if (step == 174)			
			g.drawString("3*6", 400, 400);
		else if (step == 175)			
			g.drawString("5*8", 400, 400);
		else if (step == 176)			
			g.drawString("6*7", 400, 400);
		else if (step == 177)			
			g.drawString("3*9", 400, 400);
		else if (step == 178)			
			g.drawString("2*8", 400, 400);
		else if (step == 179)			
			g.drawString("7*8", 400, 400);
		else if (step == 180)			
			g.drawString("4*7", 400, 400);
		else if (step == 181)			
			g.drawString("21*11", 400, 400);
		else if (step == 182)			
			g.drawString("17*15", 400, 400);
		else if (step == 183)			
			g.drawString("15*12", 400, 400);
		else if (step == 184)			
			g.drawString("14*15", 400, 400);
		else if (step == 185)			
			g.drawString("11*12", 400, 400);
		else if (step == 186)			
			g.drawString("15*21", 400, 400);
		else if (step == 187)			
			g.drawString("14*16", 400, 400);
		else if (step == 188)			
			g.drawString("15*31", 400, 400);
		else if (step == 189)			
			g.drawString("25*44", 400, 400);
		else if (step == 190)			
			g.drawString("16*12", 400, 400);
		else if (step == 191)			
			g.drawString("Sentence 1", 400, 400);
		else if (step == 192)			
			g.drawString("Sentence 1", 400, 400);
		else if (step == 193)			
			g.drawString("Sentence 1", 400, 400);
		else if (step == 194)			
			g.drawString("Sentence 1", 400, 400);
		else if (step == 195)			
			g.drawString("Sentence 1", 400, 400);
		else if (step == 196)			
			g.drawString("Sentence 1", 400, 400);
		else if (step == 197)			
			g.drawString("Sentence 1", 400, 400);
		else if (step == 198)			
			g.drawString("Sentence 1", 400, 400);
		else if (step == 199)			
			g.drawString("Sentence 1", 400, 400);
		else if (step == 200)			
			g.drawString("Question 1", 400, 400);
		else 
			g.drawString("Done!", 400, 400);

		
	}
	
	public Color backgroundColor()
	{
		
		return bgColor;
	}

	@Override
	public void simulate() {		
		
			
	}
	
	public void gotSample(String sample)
	{
		String[] split = sample.split(",");
		double lx = Double.parseDouble(split[0]);
		double ly = Double.parseDouble(split[1]);
		double ldiam = Double.parseDouble(split[2]);

		double rx = Double.parseDouble(split[3]);
		double ry = Double.parseDouble(split[4]);
		double rdiam = Double.parseDouble(split[5]);
		
		events.add("" + lx + "\t" + ly + "\t" + rx + "\t" + ry + "\t" + ldiam + "\t" + rdiam);
	}
	
	public void saveEvents()
	{
        try {
        BufferedWriter out = new BufferedWriter(new FileWriter(folder + "\\events.txt"));
            for (int i = 0; i < events.size(); i++) {
               out.write(events.get(i));
               out.newLine();
            }
            out.close();
        } catch (IOException e) {}
	}

	@Override
	public void keyPressed(int keycode) {
		nextStep();
		super.keyPressed(keycode);
	}
	

	
	public void nextStep()
	{
		step++;
		events.add("Step\t"+step);
		
		saveEvents();
		
		if (step >= 161 && step <= 170)
			startTimer(10);
		else if (step >= 171 && step <= 180)
			startTimer(3);
		else if (step >= 181 && step <= 190)
			startTimer(15);
		else if (step == 200)
			startTimer(60);
	}
	

	
}

