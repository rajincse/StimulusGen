//**********************************************************
// Assignment: Graph drawing
// Course: Reasearch work
//
// Author: (Muminul Islam)
//
// Honor Code: I pledge that this file represents my own
//   program code. I received help from (Dr Radu Jianu) in designing and refactoring my program.
//*********************************************************

package Graph;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JOptionPane;


class Operation
{
    public static final int OCCUPY=1;
    public static final int VACCATE=2;
    public static final int COUNT=3;
}


public class SimAnnealingGraphDrawer extends GraphDrawer {

        // Member variables declaration 
        double edgeEdgePenalty;
        double nodeEdgePenalty;
        double temperature;
        double max_step;
        double temp_fact;
        int[][] edgesThroughCenterCount;
        int[][] edgesInCellCount;
        boolean[][] cellOccupied;
        boolean[] pinnedNode;
        ArrayList<Integer> e1;
        ArrayList<Integer> e2;
        ArrayList<Integer> mobileNodes;
        int iteration,NoOfNodes;
        double tempX,tempY;
        double radius;
        int res_size;
        int no_jump;
        double rad_per;
        double nodeWidth;
        double energy;
        int gridWidth,gridHeight;
        int zoomFactor =40;
        int oldX;
        int oldY;
        int totalSteps;
        int count;
        boolean done=false;
        Random rn;
        
        public SimAnnealingGraphDrawer(Graph g)
        {
        	
            super(g);
            rn = new Random();
            
            e1 = new ArrayList<Integer>();
            e2 = new ArrayList<Integer>();
            g.getEdgesAsIndeces(e1, e2);
            nodeEdgePenalty = 9;
           // edgeEdgePenalty = 3;
            
            edgeEdgePenalty = 300;
            
            NoOfNodes = graph.numberOfNodes();
           // gridWidth = (int)(Math.ceil(3.2* Math.sqrt(NoOfNodes)));
           // gridHeight = (int)(Math.ceil(2.5* Math.sqrt(NoOfNodes)));
            
            gridWidth = (int)(Math.ceil(5.2* Math.sqrt(NoOfNodes)));
            gridHeight = (int)(Math.ceil(5.5* Math.sqrt(NoOfNodes)));
            
            //gridWidth = 3;
            //gridHeight =3;
            //this is used for storing pinned node.at first all the nodes are unpinned
            pinnedNode = new boolean[NoOfNodes];
            //At first all the nodes are mobile node case we want every node in mobile so that they can move
            mobileNodes = new ArrayList<Integer>();
            //for storing the count of edges go throug this cell
            edgesInCellCount = new int[gridWidth+1][gridHeight+1];
            //for storing the count of edges go throug this node or touching the node
            edgesThroughCenterCount= new int[gridWidth+1][gridHeight+1];
            // boolean array to store each cell containc a node or empty to use move if any cell is empty we can move the node tinto this cell
            // if the cell is not empty we have to find another empty cell.no more than one node can be occupied in one cell
            cellOccupied = new boolean[gridWidth+1][gridHeight+1];
            nodes = g.getNodes();
            //In perspective all the nodes in o,o at first time so we assing this value
            edgesInCellCount[0][0]=e1.size();
            cellOccupied[0][0]=true;
            SaGraphLayout();
            cellOccupied[0][0]=false;
            totalSteps = 3000*50*NoOfNodes;
            count =0;
            
        }
        //This function was cretaed for debuging purpose
        void printOccupied()
        {
            String out="";
            for(int i=0;i<gridHeight+1;i++)
            {
                for(int j=0;j<gridWidth+1;j++)
                {
                    out += cellOccupied[j][i]+":";
                }
                out +="\n";
            }
            System.out.println(out);
        }
        void printEdgesInCell()
        {
            String out="";
            for(int i=0;i<gridHeight+1;i++)
            {
                for(int j=0;j<gridWidth+1;j++)
                {
                    out += edgesInCellCount[j][i]+":";
                }
                out +="\n";
                
            }
            System.out.println(out);
        }
        /**
         * Method name : setNodeWidth
         * This method is called by graph viewer to set the node width since i have used a zooming factor to view the grid larger
         *
         * @return  void
         * 
         */
        public void setNodeWidth(int node_width)
        {
            this.nodeWidth=node_width/zoomFactor;
        }
        /**
         * Method name : getZoomFactor
         * 
         *
         * @return  int
         * 
         */
        public int getZoomFactor()
        {
            return zoomFactor;
        }
           /**
         * Method name : drawGrid
         * 
         *This function is called for drawing grid.Is called from graph viewer render function
         * @return  void
         * 
         */
        public void drawGrid(Graphics2D g)
        {
            g.setColor(Color.orange);
            int x1,y1,x2,y2;
            y1=zoomFactor/2;
            y2=gridHeight*zoomFactor+zoomFactor/2;
            for(int i=zoomFactor/2;i<=gridWidth*zoomFactor+zoomFactor/2;i=i+zoomFactor)
            {
                g.drawLine(i, y1, i, y2);   
            }
            x1=zoomFactor/2;
            x2=gridWidth*zoomFactor+zoomFactor/2;
            for(int i=zoomFactor/2;i<=gridHeight*zoomFactor+zoomFactor/2;i=i+zoomFactor)
            {
                g.drawLine(x1, i, x2, i);
            }
            
            for (int i=0; i<gridWidth; i++)
            	for (int j=0; j<gridHeight; j++)
            	{
            		g.drawString(""+edgesInCellCount[i][j], i*zoomFactor, j*zoomFactor);
            	}
            
        }
        /**
         * Method name : iteration
         * This method is called by graph viewer in a loop until a nice graph drawing is done
         * Here termination condition must be maintained
         *
         * @return  void
         * 
         */
        public void iteration() {
            
        	long time = System.currentTimeMillis();
        	for (int i=0; i<10000; i++)
            iterateSteps();
        	long t = System.currentTimeMillis()-time;
        	if (t != 0)
        		System.out.println(System.currentTimeMillis()-time);
            //System.out.println("Iteration steps " + count);
        }
           /**
         * Method name : iteration
         * I have overloaded another function for testing my code : now it is not used
         *
         * @return  void
         * 
         */
         public void iteration(Graphics2D g) {
            
            //iterateSteps();
             //drawGrid(g);
            //System.out.println("Iteration steps " + count);
        }
            /**
         * Method name : iterateSteps
         * 
         * This function is called in each iteration steps
         * @return  void
         * 
         */
        private void iterateSteps()
        {
            
            double coolingFactor =0.9;
            int n,i,j;
            if(count>=totalSteps)
            {
                done=true;
                return;
            }
            //get a random node for moving
            n=mobileNodes.get(getRandom(0, mobileNodes.size()-1));
            energy = 0;
            //calculate the energy
            Energy(n);
            //save the energy
            double oldEnergy= energy;
            //Now move the node
            MoveNode(n);
            energy = 0;
            Energy(n);
            //Calculte the change of energy
            double improvement=oldEnergy-energy;
            
            double p = Math.exp(improvement/temperature);
           // p = 1-p;
            
           // System.out.println("prob"  + p);
            
            if(improvement>0 || Math.random() < p)
            {
                //accept change : no need to dao anything cause change was already done in moveNode call
            }
            else
            {
                UniformGrid_Remove(n);
                //restore the position
                setX(n, oldX);
                setY(n, oldY);
                //Again add the node and c update data structore for counting edges cell count and cell occupied etc
                UniformGrid_Add(n);
            }

            if (count % (100*this.NoOfNodes) == 0)
            {
            temperature = (int)(temperature * coolingFactor);
            System.out.println("temp " + temperature);
            }
            count++;
        }
           /**
         * Method name : ManhattanDistance
         * 
         *This function returns the manhattan distance between two points
         * @return  int
         * 
         */
        private int  ManhattanDistance(int ax,int ay,int bx,int by)
        {
            int ret= Math.abs(ax-bx) + Math.abs(ay-by);
            return ret;
        }
           /**
         * Method name : ManhattanEdgeLength
         * this function returns the total edge distance for a particular node
         *
         * @return  int
         * 
         */
        private double ManhattanEdgeLength(int node)
        {
            int lEdgeLength = 0;
            for ( int i=0; i<e1.size(); i++)
            {  
                int id1 = e1.get(i);	
                int id2 = e2.get(i);
                if(id1==node || id2==node)
                {
                    int x1 = getX(id1)/zoomFactor;
                    int y1 = getY(id1)/zoomFactor;
                    int x2 = getX(id2)/zoomFactor;
                    int y2 = getY(id2)/zoomFactor;
                    double d= ManhattanDistance(x1, y1, x2, y2);
                    lEdgeLength += (d*d);
                }
            }
            return lEdgeLength;
        }
        private double BioFunctionGrouping(int node)
        {
            //Not sure
            return 0;
        }
           /**
         * Method name : ModBresenHamVisit
         * This function is called when a node is added or removed from the grid or for counting the the cross in each cell
         * here visit each cell passed by an edge and populate the data structure 
         * opType may be three diff function paased when it is called :
         * when we call add:Operation Occupied is called. Remove : --vaccate  COunt crossing-->Count
         *
         * @return  void
         * 
         */
        private void ModBresenHamVisit(int x0,int y0,int x1,int y1,int opType)
        {
            double delSteep= Math.abs(y1-y0)-Math.abs(x1-x0);
            //interchnage x,y from which we wan to move along the points
            if(delSteep>0)
            {
                int temp=x0;
                x0=y0;
                y0=temp;
                temp=x1;
                x1=y1;
                y1=temp;
            }
            //interchange the x values cause we want to loop throu x values
            if(x0>x1)
            {
                int temp=x1;
                x1=x0;
                x0=temp;
                temp=y1;
                y1=y0;
                y0=temp;
            }
            double deltax=x1-x0;
            double deltay=Math.abs(y1-y0);
            double deltaerror=deltay/deltax;
            
           // if (deltax == 0)
          //  	System.out.println("errroooooooooooooooooooooooooo");
            
            
            double error=0;
            int x,y;
            y=y0;
            int ysteep;
            if(y0<y1)
            {
                ysteep=1;
            }
            else
            {
                ysteep =-1;
            }
            for(x=x0;x<=x1;x++)
            {
                if(delSteep>0)
                {
                    operation(y, x, Math.abs(error), opType);
                }
                else
                {
                    operation(x, y, Math.abs(error), opType);
                }
                error += deltaerror;
                if(error>= 0.5)
                {
                    if(x<x1)
                    {
                        if(error==1)
                        {
                            //go thour the corner no need any operation just go anouter cell 
                            y += ysteep;
                        }
                        else if(error>1)
                        {
                            //avobe the true line
                            y += ysteep;
                            if(delSteep>0)
                            {
                                operation(y, x, error, opType);
                            }
                            else
                            {
                                operation(x, y, error, opType);
                            }
                        }
                        else
                        {
                            //bellow the true line
                            if(delSteep>0)
                            {
                                operation(y, x+1, error, opType);
                            }
                            else
                            {
                                operation(x+1, y, error, opType);
                            }
                            y += ysteep;
                        }
                    }
                    error= error-1;
                    //System.out.println("Error "+error);
                }
            }
        }
           /**
         * Method name : operation
         * 
         * This function is called from ModBresenHamVisit in different time based on pOperationType type
         * @return  void
         * 
         */
        private void operation(int x,int y,double error,int pOperationType)
        {
            switch(pOperationType)
            {
                case Operation.OCCUPY:
                    if(error<=nodeWidth)
                    {
                        //System.out.println(" Through center ++ x:"+x+" y:"+y+" error:"+error+" nw:"+nodeWidth);
                        edgesThroughCenterCount[x][y]= edgesThroughCenterCount[x][y]+1;
                    }
                    edgesInCellCount[x][y]=edgesInCellCount[x][y] + 1;
                break;
                case Operation.VACCATE:
                    if(error<=nodeWidth)
                    {
                        //System.out.println(" Through center ++ x:"+x+" y:"+y+" error:"+error+" nw:"+nodeWidth);
                        edgesThroughCenterCount[x][y] = edgesThroughCenterCount[x][y]-1;
                    }
                    edgesInCellCount[x][y] = edgesInCellCount[x][y] -1;
                break;
                case Operation.COUNT:
                if(error<nodeWidth)
                {
                    if(cellOccupied[x][y])
                    {
                       // energy += nodeEdgePenalty;
                    }
                }
               if (edgesInCellCount[x][y] > 1)
            	   energy += edgesInCellCount[x][y]*edgeEdgePenalty;
                break;
            }
        }
           /**
         * Method name : Energy
         * this function calculate the energy when a node is being moved
         *
         * @return  void
         * 
         */
        void Energy(int node)
        {
            UniformGrid_CountCrossings(node);
           // System.out.println(node + " -- " + energy);
            
            
            
            energy += 5*ManhattanEdgeLength(node);
            energy += BioFunctionGrouping(node);
            
            for (int i=0; i<this.NoOfNodes; i++)
            {
            	if (i==node)continue;
            	
            	double d = this.ManhattanDistance(getX(node), getY(node), getX(i), getY(i));
            	d = d/100;
            	d = d*d;            	
            	energy += 100*1./d;
            }
           
        }
          /**
         * Method name : UniformGrid_Remove
         * 
         * This function is called when a node is removed 
         * then for each edge it calls ModBresenHamVisit function for updating data structure
         * @return  void
         * 
         */
        private  void UniformGrid_Remove(int node)
        {
            int id1;
            int id2;
            for(int i=0;i<e1.size();i++)
            {
                id1= e1.get(i);
                id2= e2.get(i);
                if(id1==node || id2==node)
                {
                    ModBresenHamVisit(getX(id1)/zoomFactor, getY(id1)/zoomFactor, getX(id2)/zoomFactor, getY(id2)/zoomFactor, Operation.VACCATE);
                }
            }
            int xpos,ypos;
            xpos=getX(node)/zoomFactor;
            ypos=getY(node)/zoomFactor;
            cellOccupied[xpos][ypos]=false ;
        }
           /**
         * Method name : UniformGrid_Add
         * 
         *
         * @return  void
         * 
         */
            private void UniformGrid_Add(int node)
            {
                int id1;
                int id2;
                for(int i=0;i<e1.size();i++)
                {
                    id1= e1.get(i);
                    id2= e2.get(i);
                    if(id1==node || id2==node)
                    {
                        ModBresenHamVisit(getX(id1)/zoomFactor, getY(id1)/zoomFactor, getX(id2)/zoomFactor, getY(id2)/zoomFactor, Operation.OCCUPY);
                    }
                }
                int xx,yy;
                xx=getX(node)/zoomFactor;
                yy=getY(node)/zoomFactor;
                cellOccupied[xx][yy]=true ;
            }
               /**
         * Method name : UniformGrid_CountCrossings
         * 
         *
         * @return  void
         * 
         */
            private void UniformGrid_CountCrossings(int node)
            {
                int id1;
                int id2;
                id1=0;
                for(int i=0;i<e1.size();i++)
                {
                    id1= e1.get(i);
                    id2= e2.get(i);
                    if(id1==node || id2==node)
                    {
                    	double bef = energy;
                    	
                    	int x1 = getX(id1)/zoomFactor;
                    	int y1 = getY(id1)/zoomFactor;
                    	int x2 = getX(id2)/zoomFactor;
                    	int y2 = getY(id2)/zoomFactor;
                    	
                        ModBresenHamVisit(x1, y1, x2, y2, Operation.COUNT);
                        
                       // System.out.println(nodes.get(id1) + "," + x1 + "," + y1 + "--" + nodes.get(id2) + "," + x2 + "," + y2 + "--" + (energy-bef) );
                    }
                }
                int xx=getX(node)/zoomFactor;
                int yy=getY(node)/zoomFactor;
                //System.out.println("Cross " + edgesInCellCount[xx][yy] +" pos " +xx+":"+yy);
                //if(edgesThroughCenterCount[xx][yy]>=0)
                {
                //energy +=edgesThroughCenterCount[xx][yy]*edge_cross;
               // energy +=edgesThroughCenterCount[xx][yy]*nodeEdgePenalty;
                
                }
            }
               /**
         * Method name : SaGraphLayout
         * 
         * This function is original SA graph layout i have split this function this is called at the first time other 
         * portion is called in each iteration
         * @return  void
         * 
         */
            private void SaGraphLayout()
            {
                int i;
                for(i=0;i<nodes.size();i++)
                {
                    MoveNode(i);
                }
                for(i=0;i<nodes.size();i++)
                {
                    energy = 0;
                    Energy(i);
                    temperature += energy/nodes.size();
                }
                for(i=0;i<nodes.size();i++)
                {
                    if(!pinnedNode[i])
                    {
                        mobileNodes.add(i);
                    }
                }
                return;
             
            }
               /**
         * Method name : MoveNode
         * 
         * This function is called when the simulation performs a move into a random position
         * @return  void
         * 
         */
            private void MoveNode(int node)
            {
                //first remove the node cause we want to move the node so that after removing the node previous values can be updated throug remove
                
                UniformGrid_Remove(node);
                
                //assign the xy co ordiante limit
                int yMin=1,yMax=gridHeight;
                int newXPos=0,newYPos=0;
                do
                {
                    newXPos = getRandom(1, gridWidth);
                    newYPos = getRandom(yMin, yMax);
                    
                }
                while(cellOccupied[newXPos][newYPos]);
                // before move save the current so that we can restore and reject the change
                oldX = getX(node);
                oldY = getY(node);
                //cellOccupied[(getX(node)/zoomFactor)][(getY(node)/zoomFactor)]=false ;
                setX(node, newXPos*zoomFactor);
                setY(node, newYPos*zoomFactor);
                //cellOccupied[newXPos][newYPos]=true ;
                // Calculted the random position so add the node into that position
                UniformGrid_Add(node);
                
                
            }
            /**
             * Method name : getRandomdth
             *this function returns a random value within the limit 
             *
             * @return  int
             * 
             */
            private int getRandom(int lowerBound,int upperBound)
            {
               
                 return rn.nextInt(upperBound - lowerBound + 1) + lowerBound;
            }
        
}
