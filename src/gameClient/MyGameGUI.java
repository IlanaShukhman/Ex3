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

public class MyGameGUI  extends JFrame implements ActionListener, MouseListener,Runnable{


	private DGraph graph;
	private int width;
	private int height;
	private Range rx;
	private Range ry;
	private Integer mc;
	private List<Robot> robots;
	private List<Fruit> fruits;
	

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
		mc=graph.getMC();
		rx=new Range(35.185,35.215);
		ry=new Range(32.095, 32.113);
		
		this.robots=robots;
		this.fruits=fruits;

		Thread t=new Thread(this);
		t.start();
	}//Graph_GUI

	public void initGUI() {
		//set the window
		this.setSize(width,height);
		this.setBackground(Color.WHITE);
		this.setLayout(null);
		this.setVisible(true);

		this.addMouseListener(this);
		repaint();
	}

	public void paint(Graphics g){
		
		super.paint(g);

		double proportionX=width/rx.get_length();
		double proportionY=(0-height)/ry.get_length();
		g.setColor(Color.BLACK);



		Iterator<Integer> it = graph.get_Node_Hash().keySet().iterator();
		while (it.hasNext()) {
			Integer v = it.next();
			Point3D src=graph.get_Node_Hash().get(v).getLocation();
			int x0= (int) ((src.x()-rx.get_min())*proportionX);
			int y0=(int) ((src.y()-ry.get_max())*proportionY);

			//draw all the nodes


			g.fillOval(x0, y0, 5, 5);
			g.drawString(Integer.toString(graph.get_Node_Hash().get(v).getKey()), x0, y0+20);


			//it is in try and catch because not all the nodes are in the edge list
			//so it might throw an error, we would like to avoid it
			try {
				Iterator<Integer> neighbors = graph.get_Edge_Hash().get(v).keySet().iterator();
				while(neighbors.hasNext()) {

					Integer u=neighbors.next();

					Point3D dest=graph.get_Node_Hash().get(u).getLocation();
					int x1=(int) ((dest.x()-rx.get_min())*proportionX);
					int y1=(int) ((dest.y()-ry.get_max())*proportionY);


					g.drawLine(x0, y0, x1, y1);
					g.drawString(Integer.toString(graph.get_Node_Hash().get(u).getKey()), x1, y1+20);

					//add the weight of the edge
					double weight=graph.get_Edge_Hash().get(v).get(u).getWeight();
					weight = (double) ((int) (weight * 10)) / (10);
					g.drawString(Double.toString(weight), x1*3/4 + x0*1/4, y1*3/4 + y0*1/4);


				}//Inner while
			}//try
			catch(Exception e){//don't do anything
			}//catch
			
			
			//draw the position of the robots and fruits
			for(Robot robot : robots) {
				double xr=(robot.get_pos().x()-rx.get_min())*proportionX;
				double yr=(robot.get_pos().y()-ry.get_max())*proportionY;
		
				g.fillOval((int)xr, (int)yr, 10, 10);
			}
			
//			for(Fruit fruit : fruits) {
//				double xr=fruit.getLocation().x()-rx.get_min()*proportionX;
//				double yr=fruit.getLocation().y()-ry.get_max()*proportionY;
//			}

		}//while
	}
	
	public Integer getMC() {
		return this.mc;
	}

	@Override
	public void run() {
		initGUI();
		while(true)
		{
			synchronized (this) {
				repaint();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
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

