package util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import perspectives.Property;
import perspectives.Task;
import perspectives.TaskObserver;
import perspectives.Viewer;
import perspectives.Viewer2D;
import perspectives.ViewerFactory;

public class ImageTiler{


	private BufferedImage img = null;
	
	File[][][] tiles = null;	
	BufferedImage[][][] tilesI = null;
	
	File[][][] fixTiles = null;
	
	
	private Viewer2D viewer;
	
	int tileSize;
	
	TaskObserver taskObserver;
	
	Task tileLoader = null;
	Task pyramidCreator = null;
	
	boolean fixingTileSize = false;
	
	public ImageTiler(Viewer2D v, TaskObserver t)
	{
		this.viewer = v;
		taskObserver = t;
	}

	public ImageTiler(BufferedImage img, TaskObserver t)
	{
		this.img = img;
		taskObserver = t;
	}	
	public ImageTiler(String fold, TaskObserver tt)
	{	
		taskObserver = tt;
		
		final String foldd = fold;
		tileLoader = new Task("Tile Loading...")
		{

			@Override
			public void task() {
				File folder = new File(foldd);
				int maxX = 0;
				int maxY = 0;
				int maxZ = 0;
				File[] fileEntries = folder.listFiles();				
				for (int i=0; i<fileEntries.length; i++) {
				    if (fileEntries[i].isDirectory()) {
				        continue;
				    } else {
				    	
				    	this.setProgress(0.5*((double)i/fileEntries.length));
				        String f = fileEntries[i].getName();
				        
				        int[] tc = processTileName(f);
				        
				        if (tc == null) continue;
				        
				        if (tc[2] == 0 && tc[0] > maxX) maxX = tc[0];
				        if (tc[2] == 0 && tc[1] > maxY) maxY = tc[1];
				        if (tc[2] > maxZ) maxZ = tc[2];
				    }
				}
				maxX++;
				maxY++;
				maxZ++;
				
				
				
				maxX = (int)Math.ceil(Math.log(maxX)/Math.log(2));
				maxY = (int)Math.ceil(Math.log(maxY)/Math.log(2));
				
				maxX = Math.max(maxX, maxY);
				maxX = (int)Math.pow(2, maxX);
				maxY = maxX;
				
				
				//int levels = Math.log(Math.max(maxX, maxY))/ Math.log(2);
				
				tiles = new File[maxZ][][];
				tilesI = new BufferedImage[maxZ][][];
				for (int z=0; z<tiles.length; z++)
				{
					int size = maxX/ (int)Math.pow(2,z);
					tiles[z] = new File[size][];
					tilesI[z] = new BufferedImage[size][];
					for (int i=0; i<tiles[z].length; i++)
					{
						tiles[z][i] = new File[size];
						tilesI[z][i] = new BufferedImage[size];
					}
				}
				
		
				
							
				for (int i=0; i<fileEntries.length; i++) {
				    if (fileEntries[i].isDirectory()) {
				        continue;
				    } else {
				    	
				    	this.setProgress(0.5 + 0.5*((double)i/fileEntries.length));
				    	
				        String f = fileEntries[i].getName();
				        
				        int[] tc = processTileName(f);
				        
				        if (tc == null) continue;
				        
				       
				        int x = tc[0];
				        int y = tc[1];
				        int z = tc[2];
				        y = tiles[z][x].length - 1 - tc[1];
				        
				        
				       tiles[z][x][y] = fileEntries[i];
				       tilesI[z][x][y] = null;
				    }
				}
				
				createTilePyramid(256, 0, 0, 0, 0, 20, new File(foldd));
			}			
		};		
		
		tileLoader.startTask(taskObserver);
		
		
		
		
	}		
	
	private int[] processTileName(String f)
	{
        if (!f.endsWith(".JPG") && !f.endsWith(".jpg") && !f.endsWith(".PNG") && !f.endsWith(".png"))
        	return null;
        
        f = f.substring(0,f.length()-4);
        
        String z = "0";
        if (f.startsWith("z"))
        {
	        f=f.substring(1);
	        int ind = f.indexOf("-");
	        if (ind < 1) return null;
	        z = f.substring(0, ind);
	        f = f.substring(ind+1);
        }
        
        if (!f.startsWith("x"))
        	return null;
        
        
        f = f.substring(1);
        int ind = f.indexOf("-");
        if (ind < 1)
        	return null;
        
     	String x = f.substring(0, ind);
        f = f.substring(ind+1);
        if (!f.startsWith("y"))
        	return null;
        		
        f = f.substring(1);
        String y = f;
        
        int xi = Integer.parseInt(x);
        int yi = Integer.parseInt(y);
        
        int[] ret = new int[3];
        ret[0] = xi; ret[1] = yi; ret[2] = Integer.parseInt(z);
        
        return ret;        	
	}
	
	public void createTilePyramid(int tileSize, int x, int y, int width, int height, int maxLevels, File folder)
	{
		this.tileSize = tileSize;
		
		final ImageTiler th = this;
		final int width_ = width;
		final int height_= height;
		final int x_ = x;
		final int y_ = y;
		final int maxLevels_ = maxLevels;
		final File folder_ = folder;
		
		
		pyramidCreator = new Task("Pyramid Creator...")
		{

			@Override
			public void task() {
				
				boolean createdHere = false;
				
				if (tileLoader != null)
					while (!tileLoader.done)
					{
						try {
							Thread.sleep(250);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				
				int tileSize = th.tileSize;				
		

				if ( (tiles == null && (img!=null || viewer != null)) || (tiles != null && th.fixingTileSize))
				{
					createdHere = true;
					
					double size = Math.max(width_, height_);
					size = Math.ceil(Math.log(size)/Math.log(2));
					
					size = Math.pow(2,size)/tileSize;
					
					int maxSize = (int)Math.pow(2, maxLevels_);
					double f = maxSize/size;
					size = Math.min(size, maxSize);
					
					tiles = new File[1][][];
					tiles[0] = new File[(int)size][];
					
					tilesI = new BufferedImage[1][][];
					tilesI[0] = new BufferedImage[(int)size][];
					
					if (img != null)
					{
						AffineTransform at = AffineTransform.getScaleInstance(Math.min(1, f), Math.min(1, f));
						BufferedImage img2 = new BufferedImage((int)size*tileSize,(int)size*tileSize,BufferedImage.TYPE_INT_ARGB);
						
						Graphics2D g = img2.createGraphics();
						g.setTransform(at);
						g.drawImage(img.getSubimage(x_, y_, width_, height_),0, 0,null);
						
						for (int i=0; i<size; i++)
						{
							this.setProgress(0.5*i/size);
							
							tiles[0][i] = new File[(int)size];
							tilesI[0][i] = new BufferedImage[(int)size];
							for (int j=0; j<size; j++)
							{
								String fname = folder_ + "\\z0-" + "x" + i + "-" + "y" + j + ".PNG";
								tiles[0][i][j] = new File(fname);
								tilesI[0][i][j] = null;				
								
								try {
									ImageIO.write(img2.getSubimage(i*tileSize, j*tileSize, tileSize, tileSize),"PNG",tiles[0][i][j]);
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							}
						}
					}
					else if (viewer != null || th.fixingTileSize)
					{
						
						int bigTileSize = 1024;
						
						
						AffineTransform at = AffineTransform.getScaleInstance(Math.min(1, f), Math.min(1, f));
						at.translate(-x_, -(y_+height_));
						at.translate(0, bigTileSize);
						
						int nrBigTiles = (int)Math.ceil(((256*size)/bigTileSize));
						
						for (int i=0;i<tiles[0].length; i++)
						{
							tiles[0][i] = new File[(int)size];
							tilesI[0][i] = new BufferedImage[(int)size];
						}
						
						for (int i=0; i< nrBigTiles; i++)
						{
							for (int j=0; j< nrBigTiles; j++)
							{
								
		
								
								BufferedImage img2 = new BufferedImage(bigTileSize,bigTileSize,BufferedImage.TYPE_INT_ARGB);						
								Graphics2D g = img2.createGraphics();
								g.setColor(viewer.backgroundColor());
								g.fillRect(0, 0, bigTileSize, bigTileSize);
								
				            	
				            	    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				            	                         RenderingHints.VALUE_ANTIALIAS_ON);
		
				            	    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				            	                         RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				            	
								
								g.setTransform(at);
								if (viewer!= null)
									viewer.render(g);
								else if (th.fixingTileSize)
									th.renderFixing(g);
								
								
								for (int l=0; l<bigTileSize/tileSize; l++)
									for (int m=0; m<bigTileSize/tileSize; m++)
									{
										int absX = (j*bigTileSize/tileSize + m);
										int absY = (i*bigTileSize/tileSize + (bigTileSize/tileSize - l - 1));
										
										String fname = folder_.getAbsolutePath() + "\\z0-" + "x" + absX + "-" + "y" + absY + ".PNG";
										tiles[0][absX][(int)size-absY-1] = new File(fname);
										tilesI[0][absX][(int)size-absY-1] = null;				
										
										try {
											ImageIO.write(img2.getSubimage(m*tileSize, l*tileSize, tileSize, tileSize),"PNG",tiles[0][absX][(int)size-absY-1]);
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
								
								at.translate(-bigTileSize, 0);
							}
							at.translate(bigTileSize*nrBigTiles, bigTileSize);
						}
					}
				}
		
				double pp1 = (int)(Math.log(tiles[tiles.length-1].length)/Math.log(2));
				
				
				//make sure the tiles are tileSize
				boolean wrongSize = false;
				int h = 0;
				int w = 0;
				for (int x=0; !createdHere &&  x<tiles[0].length; x++)
					for (int y=0; !wrongSize && y<tiles[0][x].length; y++)
					{
						try {
							System.out.println(tiles[0][x][y]);
							if (tiles[0][x][y] == null)
								continue;
							
							BufferedImage im = ImageIO.read(tiles[0][x][y]);
							h = Math.max(h,im.getHeight() * tiles[0][x].length);
							w = Math.max(w,im.getWidth() * tiles[0].length);
							
							if (im.getHeight() != tileSize || im.getWidth() != tileSize)
							{
								wrongSize = true;
								break;
							}
						} catch (IOException e) {
							e.printStackTrace();
						}				
					}	
				
				if (wrongSize)	//argh
				{				
					th.fixingTileSize = true;
					th.fixTiles = th.tiles;
					th.createTilePyramid(tileSize, 0, 0, Math.max(w, h), Math.max(w,h), 20, folder_);		
					return;
				}
				
				while (true)
				{
						int newSize = tiles[tiles.length-1].length / 2;
						
						double pp2 = (int)(Math.log(newSize)/Math.log(2));
						
						double p = (pp1-pp2)/(pp1-1);
						
						this.setProgress(0.5 + 0.5*p);
						
						
						
						if (newSize <= 1)
							break;
						
						File[][] nfl = new File[newSize][];
						BufferedImage[][] nbil = new BufferedImage[newSize][];
						
				//		int tileSize = 256;
						
						for (int xx=0; xx<newSize; xx++)
						{
							nfl[xx] = new File[newSize];
							nbil[xx] = new BufferedImage[newSize];
							
							for (int yy=0; yy<newSize; yy++)
							{
								String fname = "z" + tiles.length + "-" + "x"+ xx + "-" + "y" + (newSize-1-yy)+ ".PNG";
								
								int z = tiles.length;
								
								BufferedImage im1 = null, im2 = null, im3 = null, im4 = null;
								try {
									if (tiles[z-1][2*xx][2*yy] == null)
										im1 = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
									else	im1 = ImageIO.read(tiles[z-1][2*xx][2*yy]);
									
									if (tiles[z-1][2*xx+1][2*yy] == null)
										im2 = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
									else 	im2 = ImageIO.read(tiles[z-1][2*xx+1][2*yy]);
									
									if (tiles[z-1][2*xx][2*yy+1] == null)
										im3 = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
									else im3 = ImageIO.read(tiles[z-1][2*xx][2*yy+1]);
									
									if (tiles[z-1][2*xx+1][2*yy+1] == null)
										im4 = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
									else
										im4 = ImageIO.read(tiles[z-1][2*xx+1][2*yy+1]);
								} catch (IOException e1) {
									e1.printStackTrace();
								}
								
								
								BufferedImage imnew = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
								
								
								Graphics g = imnew.createGraphics();
								g.drawImage(im1.getScaledInstance(tileSize/2, tileSize/2, Image.SCALE_AREA_AVERAGING), 0,0, null);
								g.drawImage(im2.getScaledInstance(tileSize/2, tileSize/2, Image.SCALE_AREA_AVERAGING), tileSize/2,0, null);
								g.drawImage(im3.getScaledInstance(tileSize/2, tileSize/2, Image.SCALE_AREA_AVERAGING), 0,tileSize/2, null);
								g.drawImage(im4.getScaledInstance(tileSize/2, tileSize/2, Image.SCALE_AREA_AVERAGING), tileSize/2,tileSize/2, null);
								
								String parent = null;
								if (tiles[z-1][2*xx][2*yy] != null) parent = tiles[z-1][2*xx][2*yy].getParent();
								else if (tiles[z-1][2*xx+1][2*yy] != null) parent = tiles[z-1][2*xx+1][2*yy].getParent();
								else if (tiles[z-1][2*xx][2*yy+1] != null) parent = tiles[z-1][2*xx][2*yy+1].getParent();
								else if (tiles[z-1][2*xx+1][2*yy+1] != null) parent = tiles[z-1][2*xx+1][2*yy+1].getParent();
								
								if (parent != null)
									nfl[xx][yy] = new File(parent + "//" + fname);
								else nfl[xx][yy] = null;
								nbil[xx][yy] = null;						
								
								
								if (nfl[xx][yy] != null)
								{
									try {
										
										ImageIO.write(imnew, "PNG", nfl[xx][yy] );
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}
						}
						
						File[][][] tilesNew = new File[tiles.length+1][][];
						BufferedImage[][][] tilesINew = new BufferedImage[tiles.length+1][][];
						for (int i=0; i<tiles.length; i++)
						{
							tilesNew[i] = tiles[i];
							tilesINew[i] = tilesI[i];
						}
						tilesNew[tiles.length] = nfl;
						tilesINew[tiles.length] = nbil;
						tiles = tilesNew;
						tilesI = tilesINew;
						
						
						if (newSize == 1)
							break;
				}
		
			}
		};
		
		pyramidCreator.startTask(taskObserver);
	}	
	
	
	
	public void render(Graphics2D g) {		

		if (pyramidCreator != null && !pyramidCreator.done)
			return;
		
		if (tiles != null)
		{
			double scale = g.getTransform().getScaleX();
			int level = 0;
			while (2*scale <= 1)
			{
				level++;
				scale*=2;
			}			
			int f = (int)Math.pow(2, level);
			
			if (level >= tiles.length-1) level = tiles.length-1;
			
			Point2D.Double pmin = new Point2D.Double();
			Point2D.Double pmax = new Point2D.Double();
			
			try {
				g.getTransform().inverseTransform(new Point2D.Double(0,0), pmin);
				g.getTransform().inverseTransform(new Point2D.Double(2000,1000), pmax);
				
				int tileSize = 256 ;
				
				int xmin = (int)Math.floor(pmin.x/tileSize/f);
				int ymin = (int)Math.floor(pmin.y/tileSize/f);
				int xmax = (int)Math.ceil(pmax.x/tileSize/f);
				int ymax = (int)Math.ceil(pmax.y/tileSize/f);
				
				for (int i=0; i<tilesI.length; i++)
				{
					if (level == i) continue;
					for (int j=0; j<tiles[i].length; j++)
						for (int k=0; k<tiles[i][j].length; k++)
							tilesI[i][j][k] = null;
				}
				
				for (int i=0; i<tilesI[level].length; i++)
				{
					for (int j=0; j<tilesI[level][i].length; j++)
					{
						if ( (i<xmin-2 || j<ymin-2 || i>xmax+2 || j>ymax+2))
							tilesI[level][i][j] = null;
						else
						{
							if (tilesI[level][i][j] == null)
							{
								if (tiles[level][i][j] != null)
								{
									//System.out.println("tile " + level + " " + i + " " + j);
									tilesI[level][i][j] = ImageIO.read(tiles[level][i][j]);
									
									AffineTransform at = AffineTransform.getTranslateInstance(i*tileSize*f, j*tileSize*f);
									at.scale(f,f);
									
									g.drawImage(tilesI[level][i][j],at, null);

									
								}
								else
								{
									g.setColor(Color.BLACK);
									g.fillRect(i*tileSize*f, j*tileSize*f, tileSize, tileSize);
								}
							}
							else
							{
								AffineTransform at = AffineTransform.getTranslateInstance(i*tileSize*f, j*tileSize*f);						
								at.scale(f,f);
								
								g.drawImage(tilesI[level][i][j],at, null);
								
							}
									
						}
					}
				}			
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
	}
	
	
	private void renderFixing(Graphics2D g) {		

		if (!this.fixingTileSize)
			return;
		
		tileSize = 4096;
		
		if (fixTiles != null)
		{	
			
			int level = 0;
			
			Point2D.Double pmin = new Point2D.Double();
			Point2D.Double pmax = new Point2D.Double();
			
			try {
				g.getTransform().inverseTransform(new Point2D.Double(0,0), pmin);
				g.getTransform().inverseTransform(new Point2D.Double(2000,1000), pmax);
				
				//int tileSize = 256 ;
				
				int xmin = (int)Math.floor(pmin.x/tileSize);
				int ymin = (int)Math.floor(pmin.y/tileSize);
				int xmax = (int)Math.ceil(pmax.x/tileSize);
				int ymax = (int)Math.ceil(pmax.y/tileSize);
				
				for (int i=0; i<tilesI.length; i++)
				{
					if (level == i) continue;
					for (int j=0; j<fixTiles[i].length; j++)
						for (int k=0; k<fixTiles[i][j].length; k++)
							tilesI[i][j][k] = null;
				}
				
				for (int i=0; i<fixTiles[level].length; i++)
				{
					for (int j=0; j<fixTiles[level][i].length; j++)
					{
						if ( (i<xmin-2 || j<ymin-2 || i>xmax+2 || j>ymax+2))
							tilesI[level][i][j] = null;
						else
						{
							if (tilesI[level][i][j] == null)
							{
								if (fixTiles[level][i][j] != null)
								{
									//System.out.println("tile " + level + " " + i + " " + j);
									tilesI[level][i][j] = ImageIO.read(fixTiles[level][i][j]);
									
									AffineTransform at = AffineTransform.getTranslateInstance(i*tileSize, j*tileSize);
									g.drawImage(tilesI[level][i][j],at, null);	
								}
								else
								{
									g.setColor(Color.BLACK);
									g.fillRect(i*tileSize, j*tileSize, tileSize, tileSize);
								}
							}
							else
							{
								AffineTransform at = AffineTransform.getTranslateInstance(i*tileSize, j*tileSize);				
								g.drawImage(tilesI[level][i][j],at, null);
								
							}
									
						}
					}
				}			
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
	}


}

