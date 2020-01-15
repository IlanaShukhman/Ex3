package gameClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Comparator;
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
	private static Graph_Algo g_algo;
	
	
	public static void main(String[] a) {
		test1();
	}
	public static void test1() {
		//Choose scenario num
		Ex3_Algo ex3_alg=new Ex3_Algo();
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
			edge_data edge=ex3_alg.fetchFruitToEdge(fruit, gameGraph);
			fruit.setEdge(edge);
			fruits.add(fruit);
		}//for
		Comparator<Fruit> compare=new Comparator<Fruit>() {
			
			@Override
			public int compare(Fruit f1, Fruit f2) {
				int dp =(int)(f2.getValue()-f1.getValue());
				return dp;
			}
		};
		fruits.sort(compare);
		System.out.println(fruits.toString());
		

		for(int i = 0;i<numRobots;i++) {
			game.addRobot(fruits.get(i).getEdge().getSrc());
			Robot r=new Robot();
			r.initFromJson(game.getRobots().get(i));
			robots.add(i, r);
			robots.get(i).setTarget(fruits.get(i));
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
					
					dest = nextNodeAuto(graph, src,robots.get(i));
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
	private static int nextNodeRandom(graph g, int src,int dest) {
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
		if(ans==dest)
			nextNodeRandom(g, src,dest);
		return ans;
	}//nextNodeRandom
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
	private static int nextNodeAuto(graph g, int src,Robot robot) {
			//Finding the close fruit
			Fruit close_fruit=choose_Close_Fruites(robot,g);
			//Fetching edge to fruit
			Ex3_Algo algo=new Ex3_Algo();
			edge_data edge_close_fruit=algo.fetchFruitToEdge(close_fruit, g);

			Graph_Algo g_Algo=new Graph_Algo(g);

			//Calculating the shortest path between src and node_src
			List<node_data> path=g_Algo.shortestPath(src, edge_close_fruit.getSrc());
			
			//Convert the nodes path to Keys path
			List<Integer> path_key=g_Algo.NodeToKeyConverter(path);
			path_key.add(edge_close_fruit.getDest());
			System.out.println("Path: "+path_key+" Path weight: "+g.getNode(edge_close_fruit.getSrc()).getWeight());
			
			if(path_key.size()<=2)
				{
					System.out.println("Path is EMPTY");
					return nextNodeRandom(g, src);
				}//while
			int dest=path_key.get(1);
			if(g.getNode(dest).getInfo().equals(String.valueOf(src)))
			{
				System.out.println("Repeating on the same edge");
				//return nextNodeRandom(g, src,dest);
				//return changeDirection(g, src,dest, close_fruit);
			}
			
			g.getNode(dest).setInfo(String.valueOf(src));
			robot.setTarget(close_fruit);
			return dest;
			
		}
		
	private static Fruit choose_Close_Fruites(Robot robot,graph g) {
		int src=robot.get_src();
		double min=Double.MAX_VALUE;
		Fruit target=new Fruit();
		g_algo=new Graph_Algo(g);
		for (Fruit fruit : fruits) {
				double shortestpath=g_algo.shortestPathDist(src, fruit.getEdge().getSrc());
				if(min>shortestpath)
				{
					min=shortestpath;
					target=fruit;
			}//if
		}//for
		return target;
	}//choose_Close_Fruites

	
	private static boolean alreadyTarget(Fruit f) {
		for (Robot r : robots) {
			if(r.getTarget().equals(f))
				return true;
		}//for
		return false;
	}//alreadyTarget
	private static int nextNodeManual(graph g, int src) {
		g_algo=new Graph_Algo(g);
		List<node_data> path=g_algo.shortestPath(src, gui.getSelectedNode());
		List<Integer> path_key=g_algo.NodeToKeyConverter(path);
		if(path_key.size()==1) {
			return path_key.get(0);
		}

		return path_key.get(1);
	}
	


}
