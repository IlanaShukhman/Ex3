package gameClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import Server.Game_Server;
import Server.game_service;
import dataStructure.DGraph;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;
import utils.Point3D;
import algorithms.*;;
/**
 * This class represents a simple example for using the GameServer API:
 * the main file performs the following tasks:
 * 1. Creates a game_service [0,23] (line 36)
 * 2. Constructs the graph from JSON String (lines 37-39)
 * 3. Gets the scenario JSON String (lines 40-41)
 * 4. Prints the fruits data (lines 49-50)
 * 5. Add a set of robots (line 52-53) // note: in general a list of robots should be added
 * 6. Starts game (line 57)
 * 7. Main loop (should be a thread) (lines 59-60)
 * 8. move the robot along the current edge (line 74)
 * 9. direct to the next edge (if on a node) (line 87-88)
 * 10. prints the game results (after "game over"): (line 63)
 *  
 * @author boaz.benmoshe
 *
 */
public class SimpleGameClient {
	private static List<Robot> robots;
	private static List<Fruit> fruits;

	private static MyGameGUI gui;
	private static DGraph gameGraph;

	public static void main(String[] a) {
		test1();
	}
	public static void test1() {
		//Choose scenario num
		int scenario_num =23;
		game_service game = Game_Server.getServer(scenario_num); // you have [0,23] games
		//Create Graph
		String g = game.getGraph();
		gameGraph = new DGraph();
		gameGraph.init(g);

		//Create the lists of robots and fruits
		robots=new ArrayList<Robot>();
		fruits=new ArrayList<Fruit>();

		//Game Server information such as:fruites,moves,grade,robots,graph,data
		String info = game.toString();
		GameServer gameServer=new GameServer();
		gameServer.initFromJson(info);
		int numRobots = gameServer.get_robots_number();

		System.out.println(gameServer);
		System.out.println(g);

		// update and displaying the fruites
		int numFruits = gameServer.get_fruits_number();
		for (int i = 0; i < numFruits; i++) {
			Fruit fruit=new Fruit();
			fruit.initFromJson(game.getFruits().get(i));
			fruits.add(fruit);
		}//for

		int src_node = 0;  // arbitrary node, you should start at one of the fruits

		for(int a = 0;a<numRobots;a++) {
			game.addRobot(src_node+a);
			Robot r=new Robot();
			r.initFromJson(game.getRobots().get(a));
			robots.add(r);
		}//for
		gui=new MyGameGUI(gameGraph, robots, fruits);
		game.startGame();


		// should be a Thread!!!
		while(game.isRunning()) {
			moveRobots(game, gameGraph);
		}



		String results = game.toString();
		System.out.println("Game Over: "+results);
	}


	/** 
	 * Moves each of the robots along the edge, 
	 * in case the robot is on a node the next destination (next edge) is chosen (randomly).
	 * @param game
	 * @param graph
	 * @param log
	 */
	private static void moveRobots(game_service game, graph graph) {
		List<String> log = game.move();
		if(log!=null) {
			long t = game.timeToEnd();
			for(int i=0;i<log.size();i++) {
				String robot_json = log.get(i);
				Robot robot=new Robot();
				robot.initFromJson(robot_json);
				int rid = robot.get_id();
				int src = robot.get_src();
				int dest = robot.get_dest();
				Point3D pos = robot.get_pos();
				robots.get(i).set_pos(pos);

				//if it is automatic
				if(dest==-1 && gui.getState()==1) {	
					dest = nextNodeAuto(graph, src);
					game.chooseNextEdge(rid, dest);
					System.out.println("Turn to node: "+dest+"  time to end:"+(t/1000));
					System.out.println(robot.toJSON());
				}//if

				//if it is manual
				else if(gui.getState()==0) {
					robot=gui.getSelectedRobot();
					dest=gui.getSelectedNode();
					if(dest!=-1) {
						//if the robot hasn't reached the destination node
						if(robot!=null && dest!=robot.get_src()) {
							robot.set_dest(dest);
							dest = nextNodeManual(graph, src);
							game.chooseNextEdge(robot.get_id(), dest);
						}//if				
					}//if

				}//else if
				updateFruites(game);
			}//for
		}//if
	}//moveRobots
	
	
	
	
	/**
	 * Extract information of the fruites from server in Json language and Update them
	 * @param game
	 */
	private static void updateFruites(game_service game) {
		List<String> fruitInformation=game.getFruits();
		for (int i = 0; i < fruitInformation.size(); i++) {
			Fruit fruit=new Fruit();
			fruit.initFromJson(fruitInformation.get(i));
			fruits.get(i).set_pos(fruit.getLocation());
		}//for
	}//updateFruites


	/**
	 * a very simple random walk implementation!
	 * @param g
	 * @param src
	 * @return
	 */
	private static int nextNodeRandom(graph g, int src) {
		int ans = -1;
		Collection<edge_data> ee = g.getE(src);
		if(ee!=null) {
			Iterator<edge_data> itr = ee.iterator();
			int s = ee.size();
			int r = (int)(Math.random()*s);
			int i=0;
			while(i<r) {itr.next();i++;}
			ans = itr.next().getDest();
		}//if
		return ans;
	}//nextNodeRandom
	
	/**
	 * if there is multiple robots on the same line
	 * @param robot_dest
	 * @param robot_id
	 * @return
	 */
	private static boolean multipleRobotsSameLine(int d,int s, int i) {
		for (int j=0;j<robots.size();j++) {
			int id=robots.get(i).get_id();
			int dest=robots.get(i).get_dest();
			int src=robots.get(i).get_src();
			if(((s==dest || s==src) && (d==src || d==dest))  && i!=j)
				return true;
		}//for
		return false;
	}//multipleRobotsSameLine
	/**
	 * Automatic next robot step by the recalculating path
	 * @param g
	 * @param src
	 * @return
	 */
	private static int nextNodeAuto(graph g, int src) {
		//Finding the close fruit
		Fruit close_fruit=choose_Close_Fruites(g.getNode(src).getLocation(),fruits);

		//System.out.println("Close Fruit "+close_fruit);
		//Fetching edge to fruit
		Ex3_Algo algo=new Ex3_Algo();
		edge_data edge_close_fruit=algo.fetchFruitToEdge(close_fruit, g);

		//System.out.println("Fetch edge "+edge_close_fruit);
		Graph_Algo g_Algo=new Graph_Algo(g);

		//Calculating the shortest path between src and node_src
		List<node_data> path=g_Algo.shortestPath(src, edge_close_fruit.getSrc());
		System.out.println("Path weight: "+g.getNode(edge_close_fruit.getSrc()).getWeight());
		//Convert the nodes path to Keys path
		List<Integer> path_key=g_Algo.NodeToKeyConverter(path);
		path_key.add(edge_close_fruit.getDest());
		//path_key.remove(0);

		if(path_key.size()==1)
			{
				System.out.println("Path is EMPTY");
				return nextNodeRandom(g, src);
			}//while
		int dest=path_key.get(1);
		if(g.getNode(dest).getInfo().equals(String.valueOf(src)))
		{
			System.out.println("Change Direction");
			return nextNodeRandom(g, src);
		}
		g.getNode(dest).setInfo(String.valueOf(src));
		return dest;
	}//nextNodeAuto

	private static int nextNodeManual(graph g, int src) {
		Graph_Algo g_Algo=new Graph_Algo(g);
		List<node_data> path=g_Algo.shortestPath(src, gui.getSelectedNode());
		List<Integer> path_key=g_Algo.NodeToKeyConverter(path);
		if(path_key.size()==1) {
			return path_key.get(0);
		}

		return path_key.get(1);
	}
	/**
	 * Listing the shortest path to the closest fruit
	 * @param g
	 * @param src
	 * @return
	 */
	private static List<Integer> nodesPath(graph g,int src)
	{
		//Finding the close fruit
		Fruit close_fruit=choose_Close_Fruites(g.getNode(src).getLocation(),fruits);

		//Fetching edge to fruit
		Ex3_Algo algo=new Ex3_Algo();
		edge_data edge_close_fruit=algo.fetchFruitToEdge(close_fruit, g);
		Graph_Algo g_Algo=new Graph_Algo(g);

		//Calculating the shortest path between src and node_src
		List<node_data> path=g_Algo.shortestPath(src, edge_close_fruit.getSrc());

		//Convert the nodes path to Keys path
		List<Integer> path_key=g_Algo.NodeToKeyConverter(path);

		//Adding the end of the path
		path_key.add(edge_close_fruit.getDest());
		return path_key;

	}//path_key


	/**
	 * Choosing the closest fruit by distance
	 * @param src - the src node of robot
	 * @param fruits - all the fruites
	 * @return Fruit - the close one
	 */
	private static Fruit choose_Close_Fruites(Point3D src, List<Fruit> fruits) {
		double min=Double.MAX_VALUE;
		Fruit f=new Fruit();
		for (Fruit fruit : fruits) {
			Point3D dest=fruit.getLocation();
			double distance=src.distance3D(dest);
			if(min>distance)
			{
				min=distance;
				f=fruit;
			}//if
		}//for
		return f;
	}//nodePath
	/**
	 * Choosing the closest fruit by distance except the given fruit
	 * @param src - the src node of robot
	 * @param fruits - all the fruites
	 * @return Fruit - the close one
	 */
	private static Fruit choose_Close_Fruites(Point3D src, List<Fruit> fruits,Fruit except) {
		double min=Double.MAX_VALUE;
		Fruit f=new Fruit();
		for (Fruit fruit : fruits) {
			if(!except.equals(fruit))
			{
				Point3D dest=fruit.getLocation();
				double distance=src.distance3D(dest);
				if(min>distance)
				{
					min=distance;
					f=fruit;
				}//if
			}//if
		}//for
		return f;
	}//nodePath
	
}
