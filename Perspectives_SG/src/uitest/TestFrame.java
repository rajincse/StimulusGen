/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uitest;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author Muminul
 */
public class TestFrame extends javax.swing.JFrame {
    /**
     * Creates new form TestFrame
     */
    public TestFrame() {
        initComponents();
       
    }
    public static Point2D.Double[][] getMarchingSquare(double[][] pGrid,double cellSize,Point2D.Double min, double threshold)
    {
        int [][] gridThreshHold;
        
        ArrayList<Line2D> retValue = new ArrayList<Line2D>();
        gridThreshHold= new int[pGrid.length][];
        for(int i=0;i<gridThreshHold.length;i++)
        {
            gridThreshHold[i]= new int[pGrid[i].length];
        }
        for(int i=0;i<pGrid.length;i++)
        {
            for(int j=0;j<pGrid[i].length;j++)
            {
                double degd = pGrid[i][j];
                if (degd < threshold) 
                {
                    gridThreshHold[i][j]=1;
                }
                else if(degd >= threshold)
                {
                    gridThreshHold[i][j] = 0;
                }
                
            }
        }
        int intValue;
        double dx = cellSize;
        double dy = cellSize;
      //  dx = (max.x-min.)/cellSize;
      //  dy = (minMax[3]-minMax[1])/cellSize;
        double x1,y1,x2,y2,x3,y3,x4,y4;
        Line2D l1;
        for(int i=0;i<gridThreshHold.length-1;i++)
        {
            for(int j=0;j<gridThreshHold[i].length-1;j++)
            {
                x1=min.x + i*dx;
                y1=min.x + j*dy;
                x2=min.x + (i+1)*dx;
                y2=min.x + j*dy;
                x3=min.x + (i+1)*dx;
                y3=min.x + (j+1)*dy;
                x4=min.x + i*dx;
                y4=min.x + (j+1)*dy;
                String binaSt="";
                binaSt +=gridThreshHold[i][j];
                binaSt +=gridThreshHold[i+1][j];
                binaSt +=gridThreshHold[i+1][j+1];
                binaSt +=gridThreshHold[i][j+1];
                intValue= Integer.parseInt(binaSt,2);
                switch(intValue)
                {
                    case 0:
                    break;
                    case 1:
                        l1 = new Line2D.Double(x1, (y1+y4)/2, (x3+x4)/2, y4);
                        retValue.add(l1);
                        break;
                    case 2:
                        l1 = new Line2D.Double(x2, (y2+y3)/2, (x3+x4)/2, y4);
                        retValue.add(l1);
                        break;
                    case 3:
                        l1 = new Line2D.Double(x1, (y1+y4)/2, x3, (y1+y4)/2);
                        retValue.add(l1);
                        break;
                    case 4:
                        l1 = new Line2D.Double((x1+x2)/2, y1, x2, (y2+y3)/2);
                        retValue.add(l1);
                        break;
                    case 5:
                        l1 = new Line2D.Double(x1, (y1+y4)/2, (x1+x2)/2, y1);
                        retValue.add(l1);
                        l1 = new Line2D.Double(x3, (y2+y3)/2, (x3+x4)/2, y4);
                        retValue.add(l1);
                        break;
                    case 6:
                        l1 = new Line2D.Double((x1+x2)/2, y1, (x1+x2)/2, y4);
                        retValue.add(l1);
                        break;
                    case 7:
                        //Smae as fist line of case 5
                        l1 = new Line2D.Double(x1, (y1+y4)/2, (x1+x2)/2, y1);
                        retValue.add(l1);
                        break;
                    case 8:
                        //Smae as fist line of case 5
                        l1 = new Line2D.Double(x1, (y1+y4)/2, (x1+x2)/2, y1);
                        retValue.add(l1);
                        break;
                    case 9:
                        l1 = new Line2D.Double((x1+x2)/2, y1, (x1+x2)/2, y4);
                        retValue.add(l1);
                        break;
                    case 10:
                         l1 = new Line2D.Double((x1+x2)/2, y1, (x3+x4)/2, y4);
                        retValue.add(l1);
                        l1 = new Line2D.Double(x1, (y1+y4)/2, (x3+x4)/2, y4);
                        retValue.add(l1);
                        break;
                    case 11:
                        l1 = new Line2D.Double((x1+x2)/2, y1, x3, (y2+y3)/2);
                        retValue.add(l1);
                        break;
                    case 12:
                        l1 = new Line2D.Double(x1, (y1+y4)/2, x3, (y1+y4)/2);
                        retValue.add(l1);
                        break;
                    case 13:
                        l1 = new Line2D.Double(x3, (y2+y3)/2, (x3+x4)/2, y4);
                        retValue.add(l1);
                        break;
                    case 14:
                        l1 = new Line2D.Double(x1, (y1+y4)/2, (x3+x4)/2, y4);
                        retValue.add(l1);
                        break;
                    case 15:
                        break;
                }
            }
        }
        
        
        //order them
        ArrayList< ArrayList<Point2D.Double> > polylines = new ArrayList< ArrayList<Point2D.Double> >();
        polylines.add(new ArrayList<Point2D.Double>());
        polylines.get(0).add((Point2D.Double) retValue.get(0).getP1());
        polylines.get(0).add((Point2D.Double) retValue.get(0).getP2());
        retValue.remove(0);
        int adding = 0;
        while (retValue.size() > 0)
        {
        	boolean found = false;
        	Point2D.Double lastp = polylines.get(adding).get(polylines.get(adding).size()-1);
        	
        	   for (int i=0; i<retValue.size(); i++)
               {
               	Point2D.Double p1 = (Point2D.Double) retValue.get(i).getP1();
               	Point2D.Double p2 = (Point2D.Double) retValue.get(i).getP2();
               	
               		if (lastp.distance(p1) < 20)
               		{
               			polylines.get(adding).add(p2);
               			retValue.remove(i);
               			lastp = p2;
               			found = true;
               			i--;
               			
               		}
               		else if (lastp.distance(p2) < 20)
               		{
               			polylines.get(adding).add(p1);
               			retValue.remove(i);
               			lastp = p1;
               			found = true;
               			i--;
               			
               		}
               }
        	   if (found || retValue.size() <= 0) continue;
        	   
        	   polylines.add(new ArrayList<Point2D.Double>());
        	   adding++;
               polylines.get(adding).add((Point2D.Double) retValue.get(0).getP1());
               polylines.get(adding).add((Point2D.Double) retValue.get(0).getP2());	 
               retValue.remove(0);
        }
        
        Point2D.Double[][] ret = new Point2D.Double[polylines.size()][];
        for (int i=0; i<ret.length; i++)
        {
        	ret[i] = new Point2D.Double[polylines.get(i).size()];
        	for (int j=0; j<ret[i].length; j++)
        		ret[i][j] = polylines.get(i).get(j);
        }
        
        
        return ret;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMaximizedBounds(new java.awt.Rectangle(0, 0, 500, 500));

        jButton1.setText("jButton1");
        jButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(450, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(124, 124, 124))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(420, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addGap(30, 30, 30))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1MouseClicked
        // TODO add your handling code here:
        int minX,minY;
       int celSize=0;
       minX=200;
       minY=200;
       double[][] grid=null;
       int cellSize;
        try
        {
            FileInputStream fstream = new FileInputStream("input.txt");
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            //Read File Line By Line
            strLine = br.readLine();
            cellSize = Integer.parseInt(strLine);
            grid = new double[cellSize][];
            for(int i=0;i<grid.length;i++)
            {
                grid[i] = new double[cellSize];
            }
            
            String[] strArray;
            for(int i=0;i<cellSize;i++)
            {
                strLine = br.readLine();
                int j=0;
                strArray= strLine.split("\t");
                for(j=0;j<strArray.length;j++)
                {
                    grid[j][i]=Integer.parseInt(strArray[j]);
                }
                 
            }
        
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
  
        
        Graphics2D g = (Graphics2D) this.getGraphics();
       g.setColor(Color.red);
       
       
       int x1,y1,x2,y2;
       x2=minX;
       y1=minY;
       
       celSize = grid.length;
       for(int i=0;i<grid.length;i++)
       {
           x1= minX + 30*i;
           
           y2=minY+180;
          g.drawLine(x1, y1, x1, y2);
           
           
       }
       x1=minX;
       
       for(int i=0;i<grid.length;i++)
       {
           y1= minY + 30*i;
           
           x2=minX+(30*(celSize-1));
           g.drawLine(x1, y1, x2, y1);
           
       }
       double[] minm={minX,minY,minX+(30*(celSize)),minY+(30*(celSize))};
       Point2D.Double[][] lin=getMarchingSquare(grid, 30., new Point2D.Double(minX,minY),2.);
       g.setColor(Color.BLUE);
       for(int i=0;i<lin.length;i++)
       {
    	   for (int j=1; j<lin[i].length; j++)
    	   {    	
           g.drawLine((int)lin[i][j-1].x,(int)lin[i][j-1].y,(int)lin[i][j].x,(int)lin[i][j].y);
           g.drawString("" + j, (int)lin[i][j-1].x + ((int)lin[i][j].x - (int)lin[i][j-1].x)/2, (int)lin[i][j-1].y + ((int)lin[i][j].y - (int)lin[i][j-1].y)/2);
    	   }
       }
    }//GEN-LAST:event_jButton1MouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TestFrame().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    // End of variables declaration//GEN-END:variables
}
