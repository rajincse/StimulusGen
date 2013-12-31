package Graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;






public class Dijkstra {
	public static final int INFINITY = Integer.MAX_VALUE;
	public static final int UNDEFINED = -1;
	
	
	private Graph graph;
	private int[] distance;
	private boolean[] visited;
	private int[] previous;
	private PriorityQueue<DijkStraNode> priorityQueue;
	private int source;
	private int target;
	public Dijkstra(Graph graph)
	{
		this.graph = graph;
		int nodeSize = this.graph.nodes.size();
		this.distance = new int[nodeSize];
		this.visited = new boolean[nodeSize];
		this.previous = new int[nodeSize];
		this.priorityQueue = new PriorityQueue<DijkStraNode>();
	}
	private void initDijkstra()
	{
		for(int i=0;i<this.distance.length;i++)
		{
			this.distance[i] = Dijkstra.INFINITY;
			this.previous[i] = Dijkstra.UNDEFINED;
			this.visited[i] = false;
		}
		this.distance[source] =0;
		
		for(int i=0;i<distance.length;i++)
		{
			this.priorityQueue.add(new DijkStraNode(i, this.distance[i]));
		}
	}
	public ArrayList<Integer> executeDijkstra(int source, int target)
	{
		this.source = source;
		this.target = target;
		this.initDijkstra();
		
		while(!this.priorityQueue.isEmpty())
		{
			DijkStraNode node = this.priorityQueue.poll();
			this.visited[node.getIndex()] = true;
			for(int i=0;i<this.graph.edgesIndex1.size();i++)
			{
				int edge1 = this.graph.edgesIndex1.get(i).intValue();
				int edge2 = this.graph.edgesIndex2.get(i).intValue();
				int neighbor =Dijkstra.UNDEFINED;
				
				if(edge1 == node.getIndex())
				{
					neighbor = edge2;
				}
				else if(edge2 == node.getIndex())
				{
					neighbor = edge1;
				}
				
				if(neighbor != Dijkstra.UNDEFINED && this.visited[neighbor] == false)
				{
					this.relax(node.getIndex(), neighbor, 1);
				}
			}
		}
		ArrayList<Integer> result = this.getResult(source, target);
		return result;
		
	}
	private ArrayList<Integer> getResult(int source, int target)
	{
		ArrayList<Integer> result = new ArrayList<Integer>();
		if(previous[target] == Dijkstra.UNDEFINED)
		{
			return result;
		}
		else if(previous[target] == source)
		{	
			result.add(source);
			result.add(target);
			return result;
		}
		else 
		{
			result.addAll(getResult(source, previous[target]));
			result.add(target);
			return result;
		}
	}
	private void relax(int u,int v, int w)
	{
		int tempDistance = this.distance[u]+ w;
		if(tempDistance < this.distance[v])
		{
			this.distance[v] = tempDistance;
			this.previous[v] =u;
			this.reorderQueue();
		}
	}
	private void reorderQueue()
	{
		LinkedList<DijkStraNode> list = new LinkedList<DijkStraNode>();
		while(!this.priorityQueue.isEmpty())
		{
			DijkStraNode node = this.priorityQueue.poll();
			list.add(node);
		}
		
		while(!list.isEmpty())
		{
			DijkStraNode node = list.poll();
			this.priorityQueue.add(new DijkStraNode(node.getIndex(), this.distance[node.getIndex()]));
		}
	}
	
}
class DijkStraNode implements Comparable<DijkStraNode>{
	private int index;
	private int distance;
	public DijkStraNode(int index, int distance)
	{
		this.index = index;
		this.distance = distance;
	}
	public int getIndex()
	{
		return this.index;
	}
	public int getDistance()
	{
		return this.distance;
	}
	@Override
	public int compareTo(DijkStraNode another) {
		// TODO Auto-generated method stub
		return (this.distance-another.getDistance());
	}
}