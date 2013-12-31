/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ParallelCoord;

/**
 *
 * @author mershack
 */
public class VerticalFieldDragged extends VerticalFields {
   private boolean dragged;
   private boolean beganDragging;
   
   private int dragBeginY;
  // private int dragEndY;
   private boolean dragFill;
    
   public VerticalFieldDragged(int xx, int yy, int hh,int ww){
       super(xx, yy,hh, ww);
       //initiate the dragged variable to false
       
       dragged = false;
   }
    
   public void setDragged(boolean drgd){
      dragged = drgd;
   }
   
   public boolean getDragged(){
       return dragged;
   }
   
  /* public void setDragBegan(boolean db){
       dragBegan = db;
   }
   
   public boolean getDragBegan(){
       return dragBegan;
   } */
   
       public void setDragFill(boolean df) {
        dragFill = df;
    }

    public boolean getDragFill() {
        return dragFill;
    }

   public void setDragBeginY(int y) {
        dragBeginY = y;
        setY(y);
    }

 /*    public void setDragEndY(int y) {
        dragEndY = y;
    } */
    
    public void setBeganDragging(boolean bg){
        beganDragging = bg;
    }
    
    public boolean getBeganDragging(){
        return beganDragging;
    } 
    
  public int getDragBeginY(){
        return dragBeginY;
    }
    
    /*  public int getDragEndY(){
        return dragEndY;
    } */
}
