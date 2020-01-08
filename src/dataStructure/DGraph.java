package dataStructure;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.management.RuntimeErrorException;

import gui.Graph_GUI;

/**
 * This class represents a directional weighted graph.
 * The interface has a road-system or communication network in mind - and should support a large number of nodes (over 100,000).
 * The implementation is based on HashMap, which allows most of the functions to run in O(1) time.
 *
 */
public class DGraph implements graph,Serializable{
	private HashMap<Integer, node_data> Node_Hash;
	private HashMap<Integer, HashMap<Integer, edge_data>> Edge_Hash;
	private int ModeCount;
	private int EdgeHashSize;
	
	boolean IOException;
	boolean ClassNotFoundException;

	/**
	 * Empty Constructor
	 */
	public DGraph() {
		this.Node_Hash=new HashMap<Integer, node_data>();
		this.Edge_Hash=new HashMap<Integer, HashMap<Integer, edge_data>>();
		this.ModeCount=0;
		this.EdgeHashSize=0;
	}

	/**
	 * Shallow copy of given vertices and edges
	 * @param Node_Hash
	 * @param Edge_Hash
	 */
	public DGraph(HashMap<Integer, node_data> Node_Hash, HashMap<Integer, HashMap<Integer, edge_data>>Edge_Hash) {
		this.Node_Hash=Node_Hash;
		this.Edge_Hash = Edge_Hash;


	}//DGraph

	/**
	 * Shallow copy of given Graph
	 * @param g
	 */
	public DGraph(DGraph g) {

		this.Node_Hash=g.get_Node_Hash(); 
		this.Edge_Hash=g.get_Edge_Hash();
		this.ModeCount = g.getMC();
		this.EdgeHashSize = g.edgeSize();
	}//DGraph
	
	/**
	 * Init a graph from file
	 * @param file_name
	 */
	public void init(String file_name) {
		this.IOException=false;
		this.ClassNotFoundException=false;
		DGraph g = new DGraph(); 

		try
		{    
			FileInputStream file = new FileInputStream(file_name); 
			ObjectInputStream in = new ObjectInputStream(file); 

			g = (DGraph)in.readObject();  
			in.close(); 
			file.close(); 
			this.Node_Hash=g.get_Node_Hash(); 
			this.Edge_Hash=g.get_Edge_Hash();
			this.ModeCount = g.getMC();
			this.EdgeHashSize = g.edgeSize();
			//System.out.println("Object has been deserialized"); 
		}//try 

		catch(IOException ex) 
		{ 
			IOException=true;
			ex.printStackTrace();
			System.out.println("IOException");
		} 

		catch(ClassNotFoundException ex) 
		{ 
			this.ClassNotFoundException=true;
			//ex.printStackTrace();
			System.out.println("ClassNotFoundException");
		} 

	}//init

	/**
	 * Getters.
	 */
	public HashMap<Integer, node_data> get_Node_Hash(){
		return this.Node_Hash;
	}
	public HashMap<Integer, HashMap<Integer, edge_data>> get_Edge_Hash(){
		return this.Edge_Hash;
	}


	/**
	 * Return the node_data by the node_id,
	 * @param key - the node_id
	 * @return the node_data by the node_id, null if none.
	 */
	public node_data getNode(int key) {
		if(!Node_Hash.containsKey(key))
			return null;
		return Node_Hash.get(key);
	}

	/**
	 * return the data of the edge (src,dest), null if none.
	 * Note: this method should run in O(1) time.
	 * @param src
	 * @param dest
	 * @return the data of the edge (src,dest), null if none.
	 */
	public edge_data getEdge(int src, int dest) {
		if(!Node_Hash.containsKey(src)||!Node_Hash.containsKey(dest))
			return null;

		if(this.Edge_Hash.containsKey(src)) {
			HashMap<Integer,edge_data> h=Edge_Hash.get(src);
			if(h.containsKey(dest)) {
				return h.get(dest);
			}//if
		}//if	
		return null;
	}//getEdge

	/**
	 * add a new node to the graph with the given node_data.
	 * Note: this method should run in O(1) time.
	 * @param n
	 */
	public void addNode(node_data n) {
		int key=n.getKey();
		if(n.getWeight()<0)
			throw new RuntimeException("ERR: Weight cannot be negative");
		if(Node_Hash.containsKey(key)) {
			//throw new RuntimeException("ERR: This node already exist");
			throw new RuntimeException("ERR:Cannot connect between unknown node");
		}
		Node_Hash.put(key,n);
		this.ModeCount++;
	}//addNode

	/**
	 * Connect an edge with weight w between node src to node dest.
	 * * Note: this method should run in O(1) time.
	 * @param src - the source of the edge.
	 * @param dest - the destination of the edge.
	 * @param w - positive weight representing the cost (aka time, price, etc) between src-->dest.
	 */
	public void connect(int src, int dest, double w) {
		//Wrong inputs
		if(src==dest)
			throw new RuntimeException("ERR: This isn't multy graph");
		if(w<=0)
			throw new RuntimeException("ERR: Weight cannot be 0 or negetive");

		//if the nodes don't exist
		if(!Node_Hash.containsKey(dest) || !Node_Hash.containsKey(src))
			throw new RuntimeException("ERR:Cannot connect between unknown node");

		EdgeData edge=new EdgeData(src,dest,w);

		//if the src exist in the Edge_Hash
		if(this.Edge_Hash.containsKey(src)) {
			//if we want to change an existing edge, remove it and add it later
			if(getEdge(src,dest)!=null){
				this.Edge_Hash.get(src).remove(dest);
			}//if

			this.Edge_Hash.get(src).put(dest, edge);
			this.ModeCount++;
		}//if

		else {//if the src doesn't exist in the Edge_Hash
			HashMap<Integer, edge_data> h=new HashMap<Integer,edge_data>();
			h.put(dest, edge);
			this.Edge_Hash.put(src, h);			
		}
		this.EdgeHashSize++;
		this.ModeCount++;
	}//connect

	/**
	 * This method return a pointer (shallow copy) for the
	 * collection representing all the nodes in the graph. 
	 * Note: this method should run in O(1) time.
	 * @return Collection<node_data>
	 */
	public Collection<node_data> getV() {
		return this.Node_Hash.values();
	}//getV

	/**
	 * This method return a pointer (shallow copy) for the
	 * collection representing all the edges getting out of 
	 * the given node (all the edges starting (source) at the given node). 
	 * Note: this method should run in O(1) time.
	 * @return Collection<edge_data>
	 */
	public Collection<edge_data> getE(int node_id) {
		Collection<edge_data> c=null;
		if(this.Edge_Hash.get(node_id)!=null)
			c=this.Edge_Hash.get(node_id).values();
		return c;
	}//getE

	/**
	 * Delete the node (with the given ID) from the graph -
	 * and removes all edges which starts or ends at this node.
	 * This method should run in O(n), |V|=n, as all the edges should be removed.
	 * @return the data of the removed node (null if none). 
	 * @param key
	 */
	public node_data removeNode(int key) {
		if(!this.Edge_Hash.isEmpty()) {
			//remove all the edges that key is the source:
			this.EdgeHashSize-=this.Edge_Hash.get(key).size();
			this.Edge_Hash.remove(key);

			//remove all the edges that key is the destination:
			Iterator<Integer> it = this.Edge_Hash.keySet().iterator();
			while (it.hasNext()) {
				Integer src = it.next();
				if(this.Edge_Hash.get(src).get(key)!=null) {
					this.Edge_Hash.get(src).remove(key);
					this.EdgeHashSize--;
				}//if
			}//while
		}//if
		this.ModeCount++;
		return this.Node_Hash.remove(key);
	}//removeNode


	/**
	 * Delete the edge from the graph, 
	 * Note: this method should run in O(1) time.
	 * @param src
	 * @param dest
	 * @return the data of the removed edge (null if none).
	 */
	public edge_data removeEdge(int src, int dest) {
		this.ModeCount++;
		this.EdgeHashSize--;
		return this.Edge_Hash.get(src).remove(dest);
	}//removeEdge

	/** return the number of vertices (nodes) in the graph.
	 * Note: this method should run in O(1) time. 
	 * @return
	 */
	public int nodeSize() {
		return Node_Hash.size();
	}//nodeSize

	/** 
	 * return the number of edges (assume directional graph).
	 * Note: this method should run in O(1) time.
	 * @return
	 */
	public int edgeSize() {
		return this.EdgeHashSize;
	}//edgeSize


	/**
	 * return the Mode Count - for testing changes in the graph.
	 * @return
	 */
	public int getMC() {
		return this.ModeCount;
	}//getMC

	/**
	 * Compare Graphs.
	 */
	@Override
	public boolean equals(Object a)
	{
		if(a instanceof DGraph)
		{
			if(((DGraph) a).get_Edge_Hash().equals(this.Edge_Hash) && ((DGraph) a).get_Node_Hash().equals(this.Node_Hash))
				return true;
			return false;
		}//if
		return false;
	}//equals
	/**
	 * This function will pop the graph presentation
	 */
	public void show() {
		Graph_GUI window=new Graph_GUI(this);
		window.setVisible(true);
	}//show
	@Override
	public String toString() {
		return "DGraph [Node_Hash=" + Node_Hash + "\nEdge_Hash=" + Edge_Hash + "\nModeCount=" + ModeCount
				+ "\nEdgeHashSize=" + EdgeHashSize + "]";
	}
}
