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
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

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

	private double score;
	private long timeToEnd;
	private int level;
	private boolean isRunning;	

	//this is used to determine if it is automatic or manual
	private int state;
	private Robot selectedRobot;
	private int selectedNode;

	private String map;


	/**
	 * Constructors
	 */
	public MyGameGUI(){
		this(new DGraph(),new ArrayList<Robot>(), new ArrayList<Fruit>());
	}

	public MyGameGUI(DGraph g, List<Robot> robots,List<Fruit> fruits){
		width=1000;
		height=600;
		this.graph=g;

		this.rx=rangeX();
		this.ry=rangeY();

		this.proportionX=width/rx.get_length();
		this.proportionY=height/ry.get_length();

		this.robots=robots;
		this.fruits=fruits;

		this.score=0;
		this.level=0;
		this.timeToEnd=0;
		this.isRunning=false;

		StdDraw.setCanvasSize(width, height);	
		this.state=JOptionPane.showConfirmDialog(this, "Manual?");
		this.selectedNode=-1;
		this.selectedRobot=null;
		
		KML_Logger kmlFile=new KML_Logger(this.level, this.graph, robots, fruits);
		kmlFile.createKMLfile();
		
		

		Thread t=new Thread(this);
		t.start();

	}//Graph_GUI





	public void initGUI() {
		draw();
	}//initGui

	public void setMap(String map) {
		this.map=map;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public void setIsRunning(boolean b) {
		this.isRunning=b;

	}
	public void setTimeToEnd(long timeToEnd) {
		this.timeToEnd = timeToEnd;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	public int getState() {
		return this.state;
	}

	public int getSelectedNode() {
		return this.selectedNode;
	}

	public Robot getSelectedRobot() {
		return this.selectedRobot;
	}

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
			if(v.equals(selectedNode))
				StdDraw.circle(x0,y0,7);

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
				StdDraw.text(x1, y1+15, Integer.toString(graph.get_Node_Hash().get(u).getKey()));

				//add the weight of the edge
				double weight=graph.get_Edge_Hash().get(v).get(u).getWeight();
				weight = (double) ((int) (weight * 10)) / (10);

				StdDraw.text(x1*3/4 + x0*1/4, y1*3/4 + y0*1/4, Double.toString(weight));
			}//for
		}//for
	}//draw edges



	private void drawRobots() {
		Color[] color= {Color.blue,Color.darkGray,Color.green,Color.magenta,Color.pink};
		int i=0;
		for(Robot robot : robots) {
			StdDraw.setPenColor(color[i]);
			double xr=updateX(robot.get_pos().x());
			double yr=updateY(robot.get_pos().y());

			StdDraw.filledCircle(xr, yr, 10);
			if(robot.equals(selectedRobot))
				StdDraw.circle(xr,yr,12);

			i++;
		}//for
	}//updateRobots

	private void drawFruits() {
		for(Fruit fruit: fruits) {

			double xr=updateX(fruit.getLocation().x());
			double yr=updateY(fruit.getLocation().y());


			StdDraw.text(xr,yr-20 , "Value: "+Double.toString(fruit.getValue()));
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
		drawFruits();
		drawRobots();
		if(isRunning==false)
			drawEndFrame();
		drawGameInfo();
		StdDraw.show();

	}//Draw

	/**
	 * End Frame: Game Is Over
	 */
	private void drawEndFrame() {
		StdDraw.setPenColor(Color.RED);
		StdDraw.text(497,497, "Game Is Over");

	}

	/**
	 * Drawing the score of the game
	 */
	private void drawGameInfo() {
		StdDraw.text(230.0, 570.0 , "Score: "+Double.toString(this.score));
		StdDraw.text(50.0, 570.0 , "Level: "+Double.toString(this.level));
		StdDraw.text(140.0, 570.0 , "Time: "+Double.toString(this.timeToEnd));
	}//drawScore

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
				if(StdDraw.isMousePressed())
					pressed();
			}
		}//while
	}//run


	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	private boolean isClose(Point3D node1, Point3D node2) {
		if(node1.distance2D(node2)<0.0005)
			return true;
		return false;
	}

	/**
	 * This method converts from pixels to map-coordinates
	 * @param x
	 * @return
	 */
	private double reUpdateX(double x) {
		return (x/proportionX)+rx.get_min();
	}

	/**
	 * This method converts from pixels to map-coordinates
	 * @param y
	 * @return
	 */
	private double reUpdateY(double y) {
		return (y/proportionY)+ry.get_min();
	}

	private void pressed() {
		if(state==0) {//means it is manual
			double x= reUpdateX(StdDraw.mouseX());
			double y= reUpdateY(StdDraw.mouseY());
			System.out.println("X: "+StdDraw.mouseX()+" Y:"+StdDraw.mouseX());
			Point3D p=new Point3D(x,y);
			for(Integer node : graph.get_Node_Hash().keySet()) {
				Point3D loc=graph.get_Node_Hash().get(node).getLocation();
				if(/*selectedRobot!=null &&*/ isClose(p,loc) ) {
					selectedNode=node;
				}
			}

			//click on the robot.
			for (int i = 0; i < robots.size(); i++) {
				Point3D loc=robots.get(i).get_pos();
				if(isClose(p,loc)) {
					selectedRobot=robots.get(i);
				}
			}

		}
	}



	@Override
	public void mousePressed(MouseEvent e) {

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
		
	}


	public static void main(String[] a) {
		int scenario_num = 2;
		game_service game = Game_Server.getServer(scenario_num);
		String g = game.getGraph();
		DGraph gameGraph = new DGraph();
		gameGraph.init(g);
		//		MyGameGUI gui=new MyGameGUI(gameGraph, new ArrayList<>(), new ArrayList<>());
	}

}

