/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ParallelCoord;

import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author mershack
 */
public class SmallOvals {    
       
   private int x, y, w, h;
   private String label;

   public SmallOvals()
   {
       
   }
   public SmallOvals(int xx, int yy, int ww, int hh)
   {
          x = xx; 
          y = yy; 
          w = ww; 
          h = hh;
   }

   
   public void draw(Graphics g)
   {
      g.setColor(Color.ORANGE);
      g.drawOval(x, y, w, h);
      g.fillOval(x, y, w, h);
      //fill(g, Color.BLUE);
   }

   public void fill(Graphics g, Color color)
   {
      g.setColor(color);
      g.fillOval(x, y, w, h);
      draw(g);
   }
   

   public void clear(Graphics g)
   {
      g.setColor(Color.white);
      g.fillOval(x, y, w, h);
      draw(g);
   }

   public int getX()
   {
      return x;
   }

   public int getY()
   {
      return y;
   }

   public int getW()
   {
      return w;
   }

   public int getH()
   {
      return h;
   }
   
   public void setLabel(String lab){
       label = lab;
   }
   
   public String getLabel(){
       return label;
   }

}

