package gameClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import dataStructure.DGraph;

public class KML_Logger {

	private int level;
	private DGraph graph;
	private static List<Robot> robots;
	private static List<Fruit> fruits;


	public KML_Logger(int level, DGraph graph, List<Robot> robots, List<Fruit> fruits) {
		this.level=level; 
		this.graph=new DGraph(graph);
		this.robots=robots;
		this.fruits=fruits;
	}

	public String createKMLfile() {
		String file=
				//first line
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
				//second line
				"<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:gx=\"http://www.google.com/kml/ext/2.2\" xmlns:kml=\"http://www.opengis.net/kml/2.2\" xmlns:atom=\"http://www.w3.org/2005/Atom\">\r\n"
				+ "<Document>\n" + 
				//name of the kml file
				"<name>"+ this.level + ".kml</name>\r\n";
		 
		file+=addRobots();	
		file+=addPoints();	
				
				
		file+=	"<Style id=\"orange-5px\">" +
				"<LineStyle>" + 
				//color
				"<color>ff00aaff</color>"
				//width
				+ "<width>2</width>"+ 
				"</LineStyle>" +
				"</Style>" + 
				
				
				"<Placemark>\r\n" + 
				"<name>"+ this.level +"</name>\r\n" + 

				"<styleUrl>#orange-5px</styleUrl>\r\n" + 
				"<LineString>\r\n" + 
				"<tessellate>1</tessellate>\r\n" + 
				"<coordinates>\n";
		for( Integer v : graph.get_Edge_Hash().keySet() ) {
			for ( Integer u : graph.get_Edge_Hash().get(v).keySet() ) {		
				file+=graph.get_Node_Hash().get(v).getLocation();
				file+="\r\n";
				file+=graph.get_Node_Hash().get(u).getLocation();
				file+="\r\n";
			}
		}

		file+="</coordinates>\r\n" +  
				"</LineString>\r\n" + 
				"</Placemark>\r\n" +  
				"</Document>\r\n" + 
				"</kml>\r\n";

		System.out.println(file);

		try {
			saveToFile(file);
		} catch (Exception e) {
			e.printStackTrace();
		}


		return file;
	}
	
	

	private String addRobots() {
		String str="";
		
		for(Robot robot: robots) {
			str+=	"<Placemark>\r\n"+
					"<Style id=\"mycustommarker\">\r\n" + 
					"<IconStyle>\r\n" + 
					"<Icon>\r\n" + 
					"<href>http://maps.google.com/mapfiles/ms/icons/hiker.png</href>" + 
					"</Icon>\r\n" + 
					"</IconStyle>\r\n" + 
					"</Style>\r\n"+
					"<Point>\r\n"+  
					"<coordinates>"+ robot.get_pos() + "</coordinates>\r\n"+  
					"</Point>\r\n"+
					"</Placemark>\r\n";
		}
		
		
		
		return str;
	}
	
	
	private String addPoints() {
		String str="";
		for(Integer node : graph.get_Node_Hash().keySet()) {
			str+=	"<Placemark>\r\n"+
					"<color>ff00aaff</color>"+
					"<Point>\r\n"+  
					"<coordinates>"+ graph.get_Node_Hash().get(node).getLocation() + "</coordinates>\r\n"+  
					"</Point>\r\n"+
					"</Placemark>\r\n";
		}
		
		
		return str;
	}

	public void saveToFile(String file) throws IOException {
		try {
			File f=new File(this.level+".kml");
			PrintWriter print=new PrintWriter(f);

			print.write(file);
			print.close();


		} catch (Exception e) {
			System.out.println("ERR: failing to save this list.");
		}
	}//SaveToFile

}
