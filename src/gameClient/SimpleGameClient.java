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
import utils.Point3D;
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

	public static void main(String[] a) {
		test1();
	}
	public static void test1() {
		//Choose scenario num
		int scenario_num = 13;
		game_service game = Game_Server.getServer(scenario_num); // you have [0,23] games
		//Create Graph
		String g = game.getGraph();
		DGraph gameGraph = new DGraph();
		gameGraph.init(g);
		//Create the lists of robots and fruits
		robots=new ArrayList<Robot>();
		fruits=new ArrayList<Fruit>();
		//Game Server information such as:fruites,moves,grade,robots,graph,data
		String info = game.toString();


			GameServer gameServer=new GameServer();
			gameServer.initFromJson(info);
			int numRobots = gameServer.get_robots_number();

			System.out.println(info);
			System.out.println(g);

			// the list of fruits should be considered in your solution
			Iterator<String> f_iter = game.getFruits().iterator();
			while(f_iter.hasNext()) {System.out.println(f_iter.next());}

			int src_node = 0;  // arbitrary node, you should start at one of the fruits

			for(int a = 0;a<numRobots;a++) {
				game.addRobot(src_node+a);

				Robot r=new Robot();
				r.initFromJson(game.getRobots().get(a));
				robots.add(r);
			}//for

			int numFruits = gameServer.get_fruits_number();
			for (int i = 0; i < numFruits; i++) {
				Fruit fruit=new Fruit();
				fruit.initFromJson(game.getFruits().get(i));
				fruits.add(fruit);

			}//for
			
		game.startGame();

		MyGameGUI gui=new MyGameGUI(gameGraph, robots, fruits);
		
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
	 * @param gg
	 * @param log
	 */
	private static void moveRobots(game_service game, graph gg) {
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
					if(dest==-1) {	
						dest = nextNode(gg, src);
						stopAndThink();
						game.chooseNextEdge(rid, dest);
						System.out.println("Turn to node: "+dest+"  time to end:"+(t/1000));
						System.out.println(robot.toJSON());
					}//if
				updateFruites(game);
			}//for
		}//if
	}//moveRobots
	/**
	 * Simple Thread stop for illustrate thinking of robot direction
	 */
	private static void stopAndThink() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Extract information of the fruites from server and Update them
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
	private static int nextNode(graph g, int src) {
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
	}//nextNode

}
