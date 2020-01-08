package gameClient;

import Server.Game_Server;
import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.DGraph;
import gui.Graph_GUI;

public class MyGameGUI {

	public static void main(String[] args) {
		test1();

	}
	public static void test1() {
		game_service game = Game_Server.getServer(2); // you have [0,23] games
		String g = game.getGraph();
		
		DGraph graph = new DGraph();
		graph.init(g);
		Graph_GUI gui=new Graph_GUI(graph);
	}
}
