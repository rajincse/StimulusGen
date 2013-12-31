package Graph;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

public class ForceDirectedEdgeBundler extends EdgeBundler{

	Point2D.Double[][] edgePoly = null;
	
	public ForceDirectedEdgeBundler(GraphViewer g) {
		super(g);
	
	}

	@Override
	public void compute() {
		
		//Nahid: this function is called whenever the "edge bundle" spinbox is incremented in the GraphViewer interface.
		//put your code here (right now this dummy code just adds the endpoints of the edge to polyline
		
		int[] e1 = this.graphViewer.getEdgeSources();
		int[] e2 = this.graphViewer.getEdgeTargets();
		
		edgePoly = new Point2D.Double[e1.length][];
		
		for (int i=0; i<edgePoly.length; i++)
		{
			edgePoly[i] = new Point2D.Double[2];
			edgePoly[i][0] = new Point2D.Double(graphViewer.getNodeX(e1[i]), graphViewer.getNodeY(e1[i]));
			edgePoly[i][1] = new Point2D.Double(graphViewer.getNodeX(e2[i]), graphViewer.getNodeY(e2[i]));
		}
		
		
	}

	@Override
	public Point2D.Double[] getEdgePolyline(int edgeIndex) {
		
		if (edgePoly == null)
			return null;
		return edgePoly[edgeIndex];
	}

}
