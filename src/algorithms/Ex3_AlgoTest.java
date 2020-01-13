package algorithms;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

import Server.Game_Server;
import Server.game_service;
import dataStructure.DGraph;
import dataStructure.EdgeData;
import dataStructure.edge_data;
import gameClient.Fruit;
import gameClient.GameServer;
import gameClient.MyGameGUI;
import gameClient.Robot;
import utils.Point3D;

class Ex3_AlgoTest {

	private static List<Robot> robots;
	private static List<Fruit> fruits;

	@Test
	final void testFetchFruitToEdge() {
		//Choose scenario num
		int scenario_num = 0;
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


		MyGameGUI gui=new MyGameGUI(gameGraph, new ArrayList<>(), new ArrayList<>());

		Fruit fruit= fruits.get(0);
		
		Ex3_Algo a=new Ex3_Algo();

		edge_data EXPECTED=new EdgeData(8,9,1.8);
		edge_data ACTUAL=a.fetchFruitToEdge(fruit, gameGraph);
		assertEquals(EXPECTED.getSrc(),ACTUAL.getSrc(),"ERR: failed to return true when the src nodes are the same");
		assertEquals(EXPECTED.getDest(),ACTUAL.getDest(),"ERR: failed to return true when the dst nodes are the same");
		



	}

}
