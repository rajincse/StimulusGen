package eyetrack.stimulusgen;

import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class BlurringHelper {
	public BufferedImage getBlurredImage(BufferedImage sourceImage,int blurringAmount)
	{		
		return sourceImage;
		//return this.getRadialDistanceBlurredImage(sourceImage, blurringAmount);
	}
	private BufferedImage getRadialDistanceBlurredImage(BufferedImage sourceImage,int blurringAmount)
	{
		for(int x=0;x<sourceImage.getWidth();x++)
		{
			for(int y=0;y<sourceImage.getHeight();y++)
			{
				int rgb = sourceImage.getRGB(x, y);
				int blurredRGB = this.getBlurredRGB(sourceImage, x, y, blurringAmount);
				sourceImage.setRGB(x, y,blurredRGB);
				System.out.println("("+x+","+y+")=>("+Integer.toHexString(rgb)+","+Integer.toHexString(blurredRGB)+")");
			}
		}
		return sourceImage;
	}
	
	private int getBlurredRGB(BufferedImage sourceImage,int x, int y, int blurringAmount)
	{
		if(x== 699 && y == 699)
		{
			System.out.println("Hi");
		}
		int sumAlpha =0;
		int sumRed =0;
		int sumGreen=0;
		int sumBlue=0;
		int count=0;
		for(int i=x-blurringAmount;i<=x+blurringAmount;i++)
		{
			for(int j=y-blurringAmount;j<=y+blurringAmount;j++)
			{
				if(i>=0 && j>=0 && i < sourceImage.getWidth() && j< sourceImage.getHeight())
				{
					int distance = this.getDistance(i, j, x, y);
					if(distance < blurringAmount)
					{
						
						int factor = blurringAmount - distance;
						int rgb = sourceImage.getRGB(i, j);
						int alpha = (rgb >> 24) & 0x000000FF;
						int red = (rgb >> 16) & 0x000000FF;
						int green = (rgb >> 8) & 0x000000FF;
						int blue = (rgb) & 0x000000FF;
						sumAlpha += alpha* factor;
						sumRed+= red * factor;
						sumGreen += green * factor;
						sumBlue += blue * factor;
						count+= factor;
					}
					
				}
			}
		}
		int averageAlpha = sumAlpha/ count;
		int averageRed = sumRed/ count;
		int averageGreen = sumGreen / count;
		int averageBlue = sumBlue / count;
		int rgb =averageAlpha;
		rgb =( rgb << 8 ) | ( averageRed & 0x00FF);
		rgb =( rgb << 8 ) | ( averageGreen & 0x00FF);
		rgb = ( rgb << 8 ) | ( averageBlue & 0x00FF);
		rgb = rgb & 0x0FFFFFFFF;
		return rgb;
				
	}
	private int getDistance(int x1, int y1,int x2, int y2)
	{
		return (int) Math.floor( Math.sqrt((x1 - x2) * (x1- x2) + (y1 - y2) * (y1 - y2)));
	}
	
	private boolean insideCircle(int centerX, int centerY, int x, int y , int radius)	
	{
		int distance =this.getDistance(centerX, centerY, x, y);
		if(distance <= radius)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	private BufferedImage getConvolveBlurredImage(BufferedImage sourceImage,int blurringAmount)
	{
		BufferedImage dstImage = null;
		float[] matrix  = this.getBlurringMatrix(blurringAmount);
		Kernel kernel = new Kernel(blurringAmount, blurringAmount, matrix);
		ConvolveOp op = new ConvolveOp(kernel);
		dstImage = op.filter(sourceImage, dstImage);
		return dstImage;
	}
	private float[] getBlurringMatrix(int dimension)
	{
		float[] matrix = new float[dimension * dimension];
		for(int i=0;i<matrix.length;i++)
		{
			matrix[i] =(float) 1.0f/ matrix.length;
		}

		return matrix;
	}
}
