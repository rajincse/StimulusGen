package Graph;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import perspectives.DefaultProperties;
import perspectives.Property;


public class Graph {
	
	ArrayList<String> nodes;
	
	ArrayList<String> edges1, edges2;
	
	ArrayList<Integer> edgesIndex1, edgesIndex2;
	
	HashMap<String, ArrayList<Property> > nodeProperties, edgeProperties;
	
	ArrayList<Property> graphProperties;
	
	private long lastUpdate;
	
	HashMap<String, Integer> cachedNodeDegrees;
	
	boolean directed;
	
	public Graph(boolean directed)
	{
		nodes = new ArrayList<String>();
		edges1 = new ArrayList<String>();
		edges2 = new ArrayList<String>();
		
		edgesIndex1 = null;
		edgesIndex2 = null;
		
		this.directed = directed;
				
		nodeProperties = new HashMap<String, ArrayList<Property> >();
		edgeProperties = new HashMap<String, ArrayList<Property> >();
		
		graphProperties = new ArrayList<Property>();
		
		cachedNodeDegrees = new HashMap<String, Integer>();
		
		lastUpdate = new Date().getTime();
	}

	
	public long lastUpdate()
	{
		return lastUpdate;
	}
	

	public void addNode(String id)
	{
		if (isNode(id))
			return;
		
		nodes.add(id);
		
		cachedNodeDegrees.put(id, new Integer(0));
		lastUpdate = new Date().getTime();
	}
	
	public boolean isNode(String id)
	{
		if (nodes.indexOf(id) >= 0)
			return true;
		return false;
	}
	
	public void deleteNode(String id)
	{
		if (!isNode(id))
			return;
		
		 nodes.remove(nodes.indexOf(id));
		 
		 for (int i=0; i<edges1.size(); i++)
			 if (edges1.get(i).equals(id))
			 {
				 edges1.remove(i);
				 edges2.remove(i);
				 i--;
				 
				 int d = cachedNodeDegrees.get(edges2.get(i));
				 cachedNodeDegrees.remove(edges2.get(i));
				 cachedNodeDegrees.put(edges2.get(i), new Integer(d-1));
			 }
			 else if (edges2.get(i).equals(id))
			 {
				 edges1.remove(i);
				 edges2.remove(i);
				 i--;
				 
				 int d = cachedNodeDegrees.get(edges1.get(i));
				 cachedNodeDegrees.remove(edges1.get(i));
				 cachedNodeDegrees.put(edges1.get(i), new Integer(d-1));				 
			 }
		 
		 lastUpdate = new Date().getTime();
	}
	
	public void addEdge(String n1, String n2)
	{
		if (isEdge(n1,n2))
			return;
		
		if (!isNode(n1)) addNode(n1);
		if (!isNode(n2)) addNode(n2);
		
		edges1.add(n1);
		edges2.add(n2);
		
		
		int d = cachedNodeDegrees.get(n1);
		cachedNodeDegrees.remove(n1);
		cachedNodeDegrees.put(n1, new Integer(d+1));		
		
		d = cachedNodeDegrees.get(n2);
		cachedNodeDegrees.remove(n2);
		cachedNodeDegrees.put(n2, new Integer(d+1));			
		
		lastUpdate = new Date().getTime();
	}
	
	public boolean isEdge(String n1, String n2)
	{
		for (int i=0; i<edges1.size(); i++)
			if ((edges1.get(i).equals(n1) && edges2.get(i).equals(n2)))
				return true;
			else if (!directed && (edges1.get(i).equals(n2) && edges2.get(i).equals(n1)))
				return true;
		return false;
	}
	
	public void deleteEdge(String n1, String n2)
	{
		for (int i=0; i<edges1.size(); i++)
			if ((edges1.get(i).equals(n1) && edges2.get(i).equals(n2)) || (edges1.get(i).equals(n2) && edges2.get(i).equals(n1)))
			{
				edges1.remove(i);
				edges2.remove(i);
				
				int d = cachedNodeDegrees.get(n1);
				cachedNodeDegrees.remove(n1);
				cachedNodeDegrees.put(n1, new Integer(d-1));		
				
				d = cachedNodeDegrees.get(n2);
				cachedNodeDegrees.remove(n2);
				cachedNodeDegrees.put(n2, new Integer(d-1));	
				
				lastUpdate = new Date().getTime();
				return;
			}
	}
	
	public ArrayList<String> getNodes()
	{
		ArrayList<String> n = new ArrayList<String>(nodes);
		return n;
	}
	
	public void getEdges(ArrayList<String> e1, ArrayList<String> e2)
	{
		if (e1 == null || e2 == null) return;
		
		e1.clear();
		e1.addAll(edges1);
		
		e2.clear();
		e2.addAll(edges2);
	}
	
	public void getEdgesAsIndeces(ArrayList<Integer> e1, ArrayList<Integer> e2)
	{
		if (e1 == null || e2 == null) return;
		
		if (edgesIndex1 == null || edgesIndex2 == null)
		{
			edgesIndex1 = new ArrayList<Integer>();
			edgesIndex2 = new ArrayList<Integer>();
			
			ArrayList<String> nodes = this.getNodes();
			for (int i=0; i<edges1.size(); i++)
			{
				int index1 = nodes.indexOf(edges1.get(i));
				int index2 = nodes.indexOf(edges2.get(i));
				edgesIndex1.add(index1);
				edgesIndex2.add(index2);
			}
		}
		
		e1.clear();
		e1.addAll(edgesIndex1);
		
		e2.clear();
		e2.addAll(edgesIndex2);
	}
	
	public int numberOfNodes()
	{
		return nodes.size();
	}
	
	public int numberOfEdges()
	{
		return edges1.size();
	}
	
	public ArrayList<String> neighbors(String n)
	{
		ArrayList<String> r = new ArrayList<String>();
		
		for (int i=0; i<edges1.size(); i++)
			if (edges1.get(i).equals(n)) r.add(edges2.get(i));
			else if (edges2.get(i).equals(n)) r.add(edges1.get(i));
		
		return r;
	}
	
	public int getNodeDegree(String n)
	{
		return cachedNodeDegrees.get(n);
	}
	
	
	public void addNodeProperty(String n, Property p)
	{
		if (nodeProperty(n,p.getName()) != null)
			return;
		
		if (!nodeProperties.containsKey(n))
			nodeProperties.put(n, new ArrayList<Property>());
		
		ArrayList<Property> pl = nodeProperties.get(n);
		
		pl.add(p);
		
		lastUpdate = new Date().getTime();
	}
	
	public Property nodeProperty(String n, String name)
	{
		if (!nodeProperties.containsKey(n))
			return null;
		
		ArrayList<Property> pl = nodeProperties.get(n);
		for (int i=0; i<pl.size(); i++)
			if (pl.get(i).getName().equals(name))
				return pl.get(i);
		
		return null;
	}
	
	public ArrayList<Property> allNodeProperties(String n)
	{
		if (!nodeProperties.containsKey(n))
			return null;
		
		return nodeProperties.get(n);
	}
	
	public void deleteNodeProperty(String n, String name)
	{
		if (!nodeProperties.containsKey(n))
			return;
		
		ArrayList<Property> pl = nodeProperties.get(n);
		for (int i=0; i<pl.size(); i++)
			if (pl.get(i).getName().equals(name))
			{
				pl.remove(i);
				return;
			}	
		
		lastUpdate = new Date().getTime();
	}
	
	public void addEdgeProperty(String n1, String n2, Property p)
	{
		if (edgeProperty(n1,n2,p.getName()) != null)
			return;
		
		if (!edgeProperties.containsKey(n1+","+n2) && !edgeProperties.containsKey(n2 + "," + n1))
			edgeProperties.put(n1+","+n2, new ArrayList<Property>());
		
		ArrayList<Property> pl = edgeProperties.get(n1 + "," + n2);
		if (pl == null) pl = edgeProperties.get(n2 + "," + n1);
		
		pl.add(p);
		
		lastUpdate = new Date().getTime();
	}
	
	
	public Property edgeProperty(String n1, String n2, String name)
	{
		if (!edgeProperties.containsKey(n1+","+n2) && !edgeProperties.containsKey(n2 + "," + n1))
			return null;
		
		ArrayList<Property> pl = edgeProperties.get(n1 + "," + n2);
		if (pl == null) pl = edgeProperties.get(n2 + "," + n1);
		for (int i=0; i<pl.size(); i++)
			if (pl.get(i).getName().equals(name))
				return pl.get(i);
		
		return null;		
	}
	
	public ArrayList<Property> allEdgeProperties(String n1, String n2)
	{
		if (!edgeProperties.containsKey(n1+","+n2) && !edgeProperties.containsKey(n2 + "," + n1))
			return null;
		
		ArrayList<Property> pl = edgeProperties.get(n1 + "," + n2);
		if (pl == null) pl = edgeProperties.get(n2 + "," + n1);
		return pl;
	}
	
	public void deleteEdgeProperty(String n1, String n2, String name)
	{
		if (!edgeProperties.containsKey(n1+","+n2) && !edgeProperties.containsKey(n2 + "," + n1))
			return;
		
		ArrayList<Property> pl = edgeProperties.get(n1 + "," + n2);
		if (pl == null) pl = edgeProperties.get(n2 + "," + n1);
		for (int i=0; i<pl.size(); i++)
			if (pl.get(i).getName().equals(name))
			{
				pl.remove(i);
				lastUpdate = new Date().getTime();
				return;
			}		
		
	}
	
	public void addGraphProperty(Property p)
	{
		if (graphProperty(p.getName()) != null)
			return;
		
		graphProperties.add(p);
		
		lastUpdate = new Date().getTime();
	}
	
	public Property graphProperty(String name)
	{
		for (int i=0; i<graphProperties.size(); i++)
			if (graphProperties.get(i).getName().equals(name))
				return graphProperties.get(i);
		return null;
	}
	
	public void deleteGraphProperty(String name)
	{
		for (int i=0; i<graphProperties.size(); i++)
			if (graphProperties.get(i).getName().equals(name))
			{
				graphProperties.remove(i);
				lastUpdate = new Date().getTime();
				return;	
			}
	}
	
	public boolean getDirected()
	{
		return directed;
	}
	
	
	public void fromEdgeList(File f)
	{
		try{
			 FileInputStream fstream = new FileInputStream(f);
			 DataInputStream in = new DataInputStream(fstream);
			 BufferedReader br = new BufferedReader(new InputStreamReader(in));
			 String s;
			 while ((s = br.readLine()) != null)
			 {
				s = s.trim();
				if (s.length()>1 && s.charAt(0) == '#')
					continue;
				
				String[] split = s.split("\t");
				
				if (split.length < 2) continue;
				
				//if (split[0].charAt(0) != ' ' || split[1].charAt(0) != ' ')
					//continue;
				
				this.addEdge(split[0].trim(), split[1].trim());				
			 }
			 lastUpdate = new Date().getTime();
			 in.close();
			}
			catch(Exception e)
			{
				
			}
	}
	public void toEdgeList(File f)
	{
		try{
			 PrintWriter bw = new PrintWriter(new FileWriter(f));
			 
			 ArrayList<String> e1 = new ArrayList<String>();
			 ArrayList<String> e2 = new ArrayList<String>();
			 this.getEdges(e1, e2);
			 
			 for (int i=0; i<e1.size(); i++)			
				 bw.println(e1.get(i) + "\t" + e2.get(i));			
			
			 bw.close();
			}
			catch(Exception e)
			{
				
			}		
	}
		
	public void fromGraphML(File f)
	{
		  try {
			  	lastUpdate = new Date().getTime();
			  	
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(f);
				doc.getDocumentElement().normalize();
				
				
				//load attributes/properties
				HashMap<String, Class> attributeTypes = new HashMap<String, Class>();
				HashMap<String, String> attributeNames = new HashMap<String, String>();
				
				NodeList keys = doc.getElementsByTagName("key");
				for (int i=0; i<keys.getLength(); i++)
				{
					Element e = (Element)keys.item(i);
					
					String t = e.getAttribute("attr.type");
					t = t.toUpperCase().charAt(0) + t.substring(1);
					t = "java.lang." + t;
					
					Class c = Class.forName(t);
					
					attributeTypes.put(e.getAttribute("id"), c);
					attributeNames.put(e.getAttribute("id"), e.getAttribute("attr.name"));
				}
		 			
						
				NodeList graphNode = doc.getElementsByTagName("graph");
				if (graphNode.getLength() == 0)
					return;
				
				NodeList graphElem = graphNode.item(0).getChildNodes();
				
				for (int i=0; i<graphElem.getLength(); i++)
				{
					if (graphElem.item(i).getNodeType() == Node.ELEMENT_NODE)
					{
						Element e = (Element)graphElem.item(i);
						
						if (e.getNodeName() == "node")
						{
							String nodeId = e.getAttribute("id");							
							this.addNode(nodeId);
							
							//now get attributes
							NodeList data = e.getElementsByTagName("data");
							for (int j=0; j<data.getLength(); j++)
							{
								Element de = (Element)data.item(j);
								
								String s = de.getChildNodes().item(0).getNodeValue();
								
								String key = de.getAttribute("key");
								
								Class c = attributeTypes.get(key);
															
								Property p = new Property(attributeNames.get(key));
								p.setValue(new DefaultProperties.StringPropertyType(s));
								this.addNodeProperty(nodeId, p);
							}
						}
						else if (e.getNodeName() == "edge")
						{
							String sId = e.getAttribute("source");
							String tId = e.getAttribute("target");
							
							if (this.isEdge(tId, sId))
								directed = true;
							
							this.addEdge(sId,tId);
							
							//now get attributes
							NodeList data = e.getElementsByTagName("data");
							for (int j=0; j<data.getLength(); j++)
							{
								Element de = (Element)data.item(j);
								
								String s = de.getChildNodes().item(0).getNodeValue();
								
								String key = de.getAttribute("key");
								
								Class c = attributeTypes.get(key);
															
								Property p = new Property(attributeNames.get(key));
								p.setValue(new DefaultProperties.StringPropertyType(s));	
								this.addEdgeProperty(sId, tId, p);
							}
						}
					}
				}	
				
			  } catch (Exception e) {
				e.printStackTrace();
			  }		
	}
	
	public void toGraphML(File f)
	{
		 try {
	            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
	            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
	            Document doc = docBuilder.newDocument();

	            Element re = doc.createElement("graphml");
	            re.setAttribute("xmlns","http://graphml.graphdrawing.org/xmlns");  
	            re.setAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
	            re.setAttribute("xsi:schemaLocation","http://graphml.graphdrawing.org/xmlns,http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd");
	            doc.appendChild(re);

	            //create child element, add an attribute, and add to root
	            Element ge = doc.createElement("graph");
	            ge.setAttribute("id", "graph");
	            ge.setAttribute("edgedefault", "undirected");
	            
	            
	            ArrayList<String> nodes = getNodes();
	            HashSet<String> nodeKeys = new HashSet<String>();
	            for (int i=0; i<nodes.size(); i++)
	            {
	            	String id = nodes.get(i);
	            	
	            	Element ne = doc.createElement("node");
	            	ne.setAttribute("id", id);
	            	ge.appendChild(ne);
	            	
	            	//properties
	            	ArrayList<Property> prop = allNodeProperties(id);
	            	for (int j=0; prop!=null && j<prop.size(); j++)
	            	{
	            		//first check for existing key
	            		if (!nodeKeys.contains(prop.get(j).getName()))
	            		{
	            			//add a key
	            			Element ke = doc.createElement("key");
	            			ke.setAttribute("id", prop.get(j).getName());
	            			ke.setAttribute("for", "node");
	            			ke.setAttribute("attr.name", prop.get(j).getName());
	            			
	            			String clas = prop.get(j).getValue().getClass().toString();
	            			clas = clas.substring(clas.lastIndexOf('.')+1);
	            			clas = clas.toLowerCase();
	            			ke.setAttribute("attr.type", clas);
	            			re.appendChild(ke);
	            			
	            			nodeKeys.add(prop.get(j).getName());
	            		}
	            		
	            		//then add the property
	            		Element pe = doc.createElement("data");
	            		pe.setAttribute("key", prop.get(j).getName());
	            		ne.appendChild(pe);
	            		
	            		Text text = doc.createTextNode(""+prop.get(j).getValue());
	                    pe.appendChild(text);	            		
	            	}	            	
	            }
	            
	            ArrayList<String> e1 = new ArrayList<String>();
	            ArrayList<String> e2 = new ArrayList<String>();
	            getEdges(e1,e2);
	            
	            for (int i=0; i<e1.size(); i++)
	            {
	            	String id1 = e1.get(i);
	            	String id2 = e2.get(i);
	            	
	            	Element ee = doc.createElement("edge");
	            	ee.setAttribute("source", id1);
	            	ee.setAttribute("target", id2);
	            	ge.appendChild(ee);
	            }

	            re.appendChild(ge);
	       
	            /////////////////
	            //Output the XML

	            //set up a transformer
	            TransformerFactory transfac = TransformerFactory.newInstance();
	            Transformer trans = transfac.newTransformer();
	            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	            trans.setOutputProperty(OutputKeys.INDENT, "yes");

	            //create string from xml tree
	            StringWriter sw = new StringWriter();
	            StreamResult result = new StreamResult(sw);
	            DOMSource source = new DOMSource(doc);
	            trans.transform(source, result);
	            String xmlString = sw.toString();

	            FileWriter fstream = new FileWriter(f);
	            BufferedWriter out = new BufferedWriter(fstream);
	            out.write(xmlString);
	            out.close();

	        } catch (Exception e) {
	            System.out.println(e);
	        }		
	}
}
