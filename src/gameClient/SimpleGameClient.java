package gameClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

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
	private static List<Robot> robots_Priority;
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

		//Create Graph
		String s=chooseScenarioFromList();

		//if the user decided to cancel
		if(s==null)
			return;

		int scenario_num =Integer.valueOf(s);
		game_service game = Game_Server.getServer(scenario_num); // you have [0,23] games
		String g = game.getGraph();
		gameGraph = new DGraph();
		gameGraph.init(g);

		//Create the lists of robots and fruits
		robots=new ArrayList<Robot>();
		fruits=new ArrayList<Fruit>();
		robots_Priority=new ArrayList<Robot>();
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
		System.out.println(robots.toString());
		robots_Priority.addAll(robots);
		gui=new MyGameGUI(gameGraph, robots, fruits);
		game.startGame();
		gui.setIsRunning(true);
		gui.setLevel(scenario_num);
		gui.setMap(gameServer.get_data());
		System.out.println(gameServer.get_data());
		// should be a Thread!!!


		KML_Logger kmlFile=new KML_Logger(scenario_num, gameGraph, robots, fruits, game);


		while(game.isRunning()) {
			moveRobots(game, gameGraph);
		}//while

		System.out.println(robots);
		gui.setIsRunning(false);
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
			gui.setTimeToEnd(t/1000);
			for(int i=0;i<log.size();i++) {
				String info = game.toString();
				GameServer gameServer=new GameServer();
				gameServer.initFromJson(info);
				gui.setScore(gameServer.get_grade());
				String robot_json = log.get(i);
				Robot robot=new Robot();
				robot.initFromJson(robot_json);
				int rid = robot.get_id();
				int src = robot.get_src();
				int dest = robot.get_dest();
				Point3D pos = robot.get_pos();
				robots.get(i).initFromJson(robot_json);;
				//if it is automatic

				if(dest==-1 && gui.getState()==1) {

					if(gui.getState()==1) {


						dest = nextNodeAuto(graph, src, robots.get(i));
						robot.set_dest(dest);	
						game.chooseNextEdge(rid, dest);
						updateFruites(game,fruits);
						System.out.println("Robot is:"+robot.get_id()+" Turn to node: "+dest+"  time to end:"+(t/1000));
						System.out.println("Turn to node: "+dest+"  time to end:"+(t/1000));
						System.out.println(robot.toJSON());

						//					System.out.println("Robot is:"+robot.get_id()+" Turn to node: "+dest+"  time to end:"+(t/1000));
						//					System.out.println("Turn to node: "+dest+"  time to end:"+(t/1000));
						//					System.out.println(robot.toJSON());

					}//if

					//if it is manual
					else if(gui.getState()==0) {
						robot=gui.getSelectedRobot();
						dest=gui.getSelectedNode();

						//after the user clicked 
						if(robot!=null && dest!=-1) {
							if(okayToGo(dest)) {
								robot.set_dest(dest);		
							}
							int d = nextNodeManual(src, robots.get(i).get_dest());
							game.chooseNextEdge(rid, d);
							updateFruites(game,fruits);
						}
					}//else if

					
					
				}

			}//for
		}//if
	}//moveRobots


	/**
	 * Pop up window to determine which scenario the client wants
	 * @return string of the chosen value
	 */
	private static String chooseScenarioFromList() {
		String[] choices = new String [24];
		for (int i = 0; i < choices.length; i++) {
			choices[i]=String.valueOf(i);
		}//for
		String input = (String) JOptionPane.showInputDialog(null, "Please choose the level from [0,23]",
				"The Maze Of Waze", JOptionPane.QUESTION_MESSAGE, null,choices,choices[0]);
		return input;
	}//chooseFromList



	/**
	 * Extract information of the fruites from server in Json language and Update them
	 * @param game
	 */
	private static void updateFruites(game_service game,List<Fruit> fruits) {
		List<String> fruitInformation=game.getFruits();
		for (int i = 0; i < fruitInformation.size(); i++) {
			fruits.get(i).initFromJson(fruitInformation.get(i));
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
	 * Automatic next robot step by the recalculating path
	 * @param g
	 * @param src
	 * @return
	 */
	private static int nextNodeAuto(graph g, int src,Robot robot) {	
		Fruit close_fruit=choose_Close_Fruites(robot,g);
		Ex3_Algo algo=new Ex3_Algo();
		close_fruit.setEdge(algo.fetchFruitToEdge(close_fruit, g));
		g_algo=new Graph_Algo(g);
		List<node_data> path=g_algo.shortestPath(src, close_fruit.getEdge().getDest());
		int dest=path.get(1).getKey();
		//		if(g.getNode(dest).getInfo().equals(String.valueOf(src)))
		//		{
		//			//return changeDirection(g, src,dest, close_fruit);
		//		}//if
//		if(src==dest)
//			return close_fruit.getEdge().getSrc();
		System.out.println("PATH:**"+path);
		g.getNode(dest).setInfo(String.valueOf(src));
		robot.setTarget(close_fruit);
		return dest;
	}


	/**
	 * Choosing the fruit with the lowest distanse and highest value by proportion 
	 * @param robot
	 * @param g
	 * @return
	 */
	private static Fruit choose_Close_Fruites(Robot robot,graph g) {
		
		int src=robot.get_src();
		float shortestpath=0;
		g_algo=new Graph_Algo(g);
		Fruit target=robot.getTarget();
		float min=(float) ((g_algo.shortestPathDist(src,target.getEdge().getSrc())+g.getNode(target.getEdge().getSrc()).getLocation().distance2D(target.getLocation()))/target.getValue());
		for (Fruit fruit : fruits) {
			if(alreadyTargeted(fruit)==-1 || (robot.get_id()!=robots.get(alreadyTargeted(fruit)).get_id() && priority(robot)) )
			{
				double innerDistance=g.getNode(fruit.getEdge().getSrc()).getLocation().distance3D(fruit.getLocation());
				shortestpath=(float) (float) ((g_algo.shortestPathDist(src,fruit.getEdge().getSrc())+innerDistance)/fruit.getValue());
				if(min>shortestpath )
				{
					System.out.println("Change the min was: "+min+" Now: "+shortestpath);
					min=shortestpath;
					target=fruit;
				}//if
			}//else
		}//for
		robot.setTarget(target);
		return target;
	}//choose_Close_Fruites
/**
 * 
 * @param robot
 * @return
 */
	private static boolean priority(Robot robot) {
		for (Robot r : robots) {
			if(r.get_id()!=robot.get_id() && robot.get_speed()>=r.get_speed())
				return false;
		}//for
		return true;
	}//true

	/**
	 * Check if this fruit is on target of some another robot
	 * @param f
	 * @return
	 */
	private static int alreadyTargeted(Fruit f) {
		for (int i=0;i<robots.size();i++) {
			if(robots.get(i).getTarget().equals(f))
				return i;
		}//for
		return -1;
	}//alreadyTarget

	/**
	 * return false if dest is a node that a robot is on-
	 * that means the user probably tried to pick up the robot, and not the node.
	 * @param dest is the node the user clicked on
	 * @return false if there is a robot on dest - else, true, and it is okay to go there.
	 */
	private static boolean okayToGo(int dest) {
		if(robots.size()==1)
			return true;
		for(Robot robot : robots) {
			if(gameGraph.get_Node_Hash().get(dest).getLocation().equals(robot.get_pos())) {
				return false;
			}				
		}


		return true;
	}
	/**
	 * Returns the robot's next node in a manual mode.
	 * @param src is the robot's source node
	 * @param dest is the destination node
	 * @return the next node in the robot's path to the dest node.
	 */
	private static int nextNodeManual(int src, int dest) {
		if(dest==-1)
			return -1;
		g_algo=new Graph_Algo(gameGraph);
		List<node_data> path=g_algo.shortestPath(src, dest);
		List<Integer> path_key=g_algo.NodeToKeyConverter(path);

		if(path_key.size()==0) {
			return -1;
		}
		else if(path_key.size()==1) {
			return path_key.get(0);
		}
		return path_key.get(1);
	}

}
