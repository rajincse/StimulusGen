package stress;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Random;

import perspectives.Viewer2D;

public class StressViewer extends Viewer2D{
	public static final int TOTAL_NUMBERS = 500;
	
	private int[] numbers;
	private int[] x;
	private int[] y;
	private int[] fontSize;
	private Color[] color;
	public StressViewer(String name) {
		super(name);
		// TODO Auto-generated constructor stub
		this.numbers = new int[TOTAL_NUMBERS];
		this.x = new int[TOTAL_NUMBERS];
		this.y = new int[TOTAL_NUMBERS];
		this.fontSize=new int[TOTAL_NUMBERS];
		this.color=new Color[TOTAL_NUMBERS];
		
		Random rn = new Random();
		int maximum=600;
		int minimum=400;
		int n = maximum - minimum + 1;
		
		for(int i=1;i<TOTAL_NUMBERS;i++){
			int j = rn.nextInt() % n;
			int randomNum =  minimum + j;
			float r = rn.nextFloat();
			float g = rn.nextFloat();
			float b = rn.nextFloat();
			Color randomColor = new Color(r, g, b);
			int k=i+i;
			this.numbers[i] =k;
			this.x [i] = randomNum+(i*10);
			this.y[i] = randomNum-(i*15);
			this.fontSize[i]=(i%10)+10;
			this.color[i]=randomColor;
		}
	}

	@Override
	public void render(Graphics2D g) {
		// TODO Auto-generated method stub
		g.setColor(Color.BLACK);
		Font font = new Font("Verdana", Font.BOLD, 16);
		g.setFont(font);
		g.drawString("3 X 5 = ?", 530, 400);
		
		
		
		for(int i=0;i<TOTAL_NUMBERS;i++)
		{
			Font font1 = new Font("Verdana", Font.BOLD, this.fontSize[i]);
			g.setFont(font1);
			g.setColor(this.color[i]);
			g.drawString(""+this.numbers[i], this.x[i], this.y[i]);
		}
	}

	@Override
	public void simulate() {
		// TODO Auto-generated method stub
		
	}

}
