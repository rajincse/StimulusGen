/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ParallelCoord;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author mershack
 */
public class VerticalFields {

    private int x, y, h, w, alpha;
    private final int VIS_ALPHA = 255;
    private boolean fill;
    
   
    //private boolean beganDragging;

    public VerticalFields(int xx, int yy, int hh, int ww) {
        x = xx;
        y = yy;
        h = hh;
        w = ww;
        alpha = 0; //alpha will be zero in the beginning but will change when the field is selected
        fill = false;
        //dragFill = false;
        //beganDragging = false;
    }

    
    
    public void draw(Graphics2D g) {

       // System.out.println("BeginY "+ dragBeginY +"  and  EndY is"+ dragEndY);
        
        if (fill == true) {  //draw the box if fill is true
            
            g.setColor(new Color(102, 51, 0));
            g.drawRect(x, y, w, h);
            g.fillRect(x, y, w, h);
            
        }/* else if (dragFill == true) {
            
            g.setColor(new Color(0, 204, 204));
            //int hh = dragEndY - dragBeginY;
            
            g.drawRect(x, dragBeginY, w, h);
            g.fillRect(x, dragBeginY, w, h);
        } */



    }

    public void draw(Graphics2D g, int xx, int yy, int ww, int hh) {
        g.setColor(Color.darkGray);
        g.drawRect(xx, yy, ww, hh);
        g.fillRect(xx, yy, ww, hh);
    }

    public void setH(int hh) {
        h = hh;
    }

    public void setX(int xx) {
        x = xx;
    }

    public void setW(int ww) {
        w = ww;
    }

    public void setY(int yy) {
        y = yy;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getW() {
        return w;
    }

    public int getH() {
        return h;
    }

    public void VisibleAlpha() {
        alpha = VIS_ALPHA;
    }

    public void setFill(boolean f) {
        fill = f;
    }


}
