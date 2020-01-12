package gameClient;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;

import org.json.JSONException;
import org.json.JSONObject;

import Server.Game_Server;
import Server.game_service;
import algorithms.Graph_Algo;
import dataStructure.DGraph;
import dataStructure.edge_data;
import dataStructure.graph;
import gui.Graph_GUI;
import utils.Point3D;
import utils.Range;
import utils.StdDraw;

public class MyGameGUI  extends JFrame implements ActionListener, MouseListener,Runnable{


	private DGraph graph;

	private int width;
	private int height;
	private Range rx;
	private Range ry;
	double proportionX;
	double proportionY;

	private List<Robot> robots;
	private List<Fruit> fruits;




	public static void main(String[] a) {
		int scenario_num = 2;
		game_service game = Game_Server.getServer(scenario_num);
		String g = game.getGraph();
		DGraph gameGraph = new DGraph();
		gameGraph.init(g);
		MyGameGUI gui=new MyGameGUI(gameGraph, new ArrayList<>(), new ArrayList<>());
	}

	/**
	 * Constructors
	 */
	public MyGameGUI(){
		this(new DGraph(),new ArrayList<Robot>(), new ArrayList<Fruit>());
	}

	public MyGameGUI(DGraph g, List<Robot> robots,List<Fruit> fruits){
		width=1000;
		height=600;
		graph=g;

		rx=rangeX();
		ry=rangeY();

		proportionX=width/rx.get_length();
		proportionY=height/ry.get_length();


		this.robots=robots;
		this.fruits=fruits;

		StdDraw.setCanvasSize(width, height);
		Thread t=new Thread(this);
		t.start();

	}//Graph_GUI

	public void initGUI() {
		this.addMouseListener(this);
		draw();
	}//initGui

	private double updateX(double x) {
		return (x-rx.get_min())*proportionX;
	}//updateX

	private double updateY(double y) {
		return (y-ry.get_min())*proportionY;
	}//upsateY

	private void drawVer() {
		for(Integer v : graph.get_Node_Hash().keySet()) {
			Point3D src=graph.get_Node_Hash().get(v).getLocation();
			double x0=updateX(src.x());
			double y0=updateY(src.y());

			StdDraw.filledCircle(x0, y0, 5);
			StdDraw.text(x0, y0+15, Integer.toString(graph.get_Node_Hash().get(v).getKey()));

		}//for
	}//drawVer

	private void drawEdges() {
		for( Integer v : graph.get_Edge_Hash().keySet() ) {
			for ( Integer u : graph.get_Edge_Hash().get(v).keySet() ) {
				double x0=updateX(graph.get_Node_Hash().get(v).getLocation().x());
				double y0=updateY(graph.get_Node_Hash().get(v).getLocation().y());

				double x1=updateX(graph.get_Node_Hash().get(u).getLocation().x());
				double y1=updateY(graph.get_Node_Hash().get(u).getLocation().y());

				StdDraw.line(x0, y0, x1, y1);
//				drawPolygon();
//				StdDraw.filledPolygon(x, y);
				StdDraw.text(x1, y1+15, Integer.toString(graph.get_Node_Hash().get(u).getKey()));

				//add the weight of the edge
				double weight=graph.get_Edge_Hash().get(v).get(u).getWeight();
				weight = (double) ((int) (weight * 10)) / (10);

				StdDraw.text(x1*3/4 + x0*1/4, y1*3/4 + y0*1/4, Double.toString(weight));
			}//for
		}//for
	}//draw edges

	private void drawPolygon() {
		// TODO Auto-generated method stub
		
	}

	private void updateRobots() {
		Color[] color= {Color.blue,Color.darkGray,Color.green,Color.magenta,Color.pink};
		int i=0;
		for(Robot robot : robots) {
			StdDraw.setPenColor(color[i]);
			double xr=updateX(robot.get_pos().x());
			double yr=updateY(robot.get_pos().y());

			StdDraw.filledCircle(xr, yr, 10);
			i++;
		}
	}

	private void updateFruits() {
		for(Fruit fruit: fruits) {

			double xr=updateX(fruit.getLocation().x());
			double yr=updateY(fruit.getLocation().y());



			if(fruit.getType()==-1) {
				StdDraw.picture(xr, yr, "banana.png", 25, 25);
			}
			else if(fruit.getType()==1) {
				StdDraw.picture(xr, yr, "apple.png", 25, 25);
			}
		}

	}



	public void draw() {
		StdDraw.enableDoubleBuffering();
		StdDraw.clear();

		StdDraw.setXscale(0,1000);
		StdDraw.setYscale(0,600);

		StdDraw.setPenColor(Color.BLACK);

		drawVer();
		drawEdges();
		updateRobots();
		updateFruits();

		StdDraw.show();

	}

	/**
	 * Finding the limits of x coordinate for Screen creator
	 * @return
	 */
	private Range rangeX() {
		double max=Integer.MIN_VALUE;
		double min=Integer.MAX_VALUE;

		//default range for an empty graph
		if(graph.get_Node_Hash().isEmpty()) {
			Range rx=new Range(35.185,35.215);
			return rx;
		}

		for(Integer node : graph.get_Node_Hash().keySet()) {
			if(graph.get_Node_Hash().get(node).getLocation().x()>max)
				max=graph.get_Node_Hash().get(node).getLocation().x();

			if(graph.get_Node_Hash().get(node).getLocation().x()<min)
				min=graph.get_Node_Hash().get(node).getLocation().x();

		}//for each

		max+=0.001;
		min-=0.001;
		Range rx=new Range(min,max);
		return rx;
	}//RangeX


	/**
	 * Finding the limits of y coordinate for Screen creator
	 * @return
	 */
	private Range rangeY() {
		double max=Integer.MIN_VALUE;
		double min=Integer.MAX_VALUE;

		//default range for an empty graph
		if(graph.get_Node_Hash().isEmpty()) {
			Range rx=new Range(32.095, 32.113);
			return rx;
		}

		for(Integer node : graph.get_Node_Hash().keySet()) {
			if(graph.get_Node_Hash().get(node).getLocation().y()>max)
				max=graph.get_Node_Hash().get(node).getLocation().y();

			if(graph.get_Node_Hash().get(node).getLocation().y()<min)
				min=graph.get_Node_Hash().get(node).getLocation().y();
		}//for each

		max+=0.001;
		min-=0.001;
		Range ry=new Range(min,max);
		return ry;
	}//rangeY


	@Override
	public void run() {
		initGUI();
		while(true)
		{
			synchronized (this) {
				draw();
			}
		}//while
	}//run


	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}


}

