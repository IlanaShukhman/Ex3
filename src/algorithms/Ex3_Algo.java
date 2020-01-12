package algorithms;

import java.util.Collection;
import java.util.List;

import dataStructure.DGraph;
import dataStructure.edge_data;
import dataStructure.node_data;
import gameClient.Fruit;
import utils.Point3D;

public class Ex3_Algo {
	private Double EPS=0.0001;
	/**
	 * Searching all the edges of the graph and check the right edge of given fruit 
	 * @param fruit
	 * @return
	 */
	public edge_data fetchFruitToEdge(Fruit fruit,DGraph g)
	{
		Point3D mid=fruit.getLocation();
		Collection<node_data> nodes=g.getV();
		for (node_data node : nodes) {
			Collection<edge_data> edges=g.getE(node.getKey());
			for (edge_data edge : edges) {
				Point3D start=node.getLocation();
				node_data dest=g.getNode(edge.getDest());
				Point3D end=dest.getLocation();
				//Check if it's on the right edge by definition and math
				if(fruitOnEdge(start, end, mid) && (( node.getKey()-dest.getKey()>0 &&fruit.getType()==1)||( node.getKey()-dest.getKey()<0 &&fruit.getType()==-1)))
					return edge;
			}//for
		}//for
		return null;
	}//fetchFruitToEdge
	
	private boolean fruitOnEdge(Point3D start,Point3D end,Point3D mid)
	{
		return (start.distance3D(mid)+mid.distance3D(end)<=start.distance3D(end)+EPS);
	}//fruitOnEdge
	
}
