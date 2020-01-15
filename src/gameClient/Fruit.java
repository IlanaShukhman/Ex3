package gameClient;

import org.json.JSONArray;
import org.json.JSONObject;

import algorithms.Ex3_Algo;
import dataStructure.EdgeData;
import dataStructure.edge_data;
import utils.Point3D;

public class Fruit {
    private Point3D _pos;
    private double _value;
    private int _type;
    private edge_data edge;

   
	public Fruit() {
    }
	
    public Fruit(double v, Point3D p, int t) {
        this._value = v;
        this._pos = new Point3D(p);
        this._type = t;
    }

    public edge_data getEdge() {
		return edge;
	}

	public void setEdge(edge_data edge) {
		this.edge = edge;
	}

	public int getType() {
       return this._type;
    }

    public Point3D getLocation() {
        return new Point3D(this._pos);
    }

    public void set_pos(Point3D _pos) {
		this._pos = _pos;
	}

    public double getValue() {
        return this._value;
    }

    public void initFromJson(String json)
    {
    	try {
    	 JSONObject text = new JSONObject(json);
         //JSONArray fruits = text.getJSONArray("Fruit");
         
         this._value = text.getJSONObject("Fruit").getDouble("value");
         String pos= text.getJSONObject("Fruit").getString("pos");
         Point3D p = new Point3D(pos);
         this._pos=p;
         this._type=text.getJSONObject("Fruit").getInt("type");
    	}
    	catch (Exception e) {
    		e.printStackTrace();
		}
    }//initFromJson

   
    
    public String toJSON1() {
        String ans = "{\"Fruit\":{\"value\":10,\"type\":1,\"pos\":\"35.187615443099276,32.103800431932775,0.0\"}}";
        return ans;
    }

    public String toString() {
        return this.toJSON();
    }

    public String toJSON() {
        String ans = "{\"Fruit\":{\"value\":" + this._value + "," + "\"type\":" + this._type + "," + "\"pos\":\"" + this._pos.toString() + "\"" + "}" + "}";
        return ans;
    }
    public boolean equals(Fruit f)
    {
    	if(f.getLocation().equals(this._pos))
    		return true;
    	return false;
    }
    
   
    public static void main(String[] a) {
        double v = 10.0D;
        Point3D p = new Point3D(1.0D, 2.0D, 3.0D);
        Fruit f = new Fruit(v, p, -1);
        String s = f.toJSON();
        System.out.println(s+"\n");
        f.initFromJson(s);
        System.out.println(f);
    }//main
    
    
}
