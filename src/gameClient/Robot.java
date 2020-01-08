package gameClient;

import org.json.JSONObject;

import dataStructure.edge_data;
import dataStructure.node_data;
import oop_dataStructure.oop_graph;
import utils.Point3D;

public class Robot {
    private int _id;
    private Point3D _pos;
    private double _value;
    private int _src;
    private int _dest;
    private double _speed;
    
    public Robot()
    {
    	
    }

    public Robot(int _id, Point3D _pos, double _value, int _src, int _dest, double _speed) {
		super();
		this._id = _id;
		this._pos = _pos;
		this._value = _value;
		this._src = _src;
		this._dest = _dest;
		this._speed = _speed;
	}

	public void initFromJson(String json)
    {
    	try {
    	 JSONObject text = new JSONObject(json);
         this._id = text.getJSONObject("Robot").getInt("id");
         this._src = text.getJSONObject("Robot").getInt("src");
         this._dest = text.getJSONObject("Robot").getInt("dest");
         String pos= text.getJSONObject("Robot").getString("pos");
         this._pos= new Point3D(pos);
         this._speed = text.getJSONObject("Robot").getDouble("speed");
         this._value = text.getJSONObject("Robot").getDouble("value");
    	}
    	catch (Exception e) {
    		e.printStackTrace();
		}
    }//initFromJson



/**
 * Getters & Setters:
 * @return
 */
	public int get_id() {
		return _id;
	}




	public void set_id(int _id) {
		this._id = _id;
	}




	public Point3D get_pos() {
		return _pos;
	}




	public void set_pos(Point3D _pos) {
		this._pos = _pos;
	}




	public double get_value() {
		return _value;
	}




	public void set_value(double _value) {
		this._value = _value;
	}




	public int get_src() {
		return _src;
	}




	public void set_src(int _src) {
		this._src = _src;
	}




	public int get_dest() {
		return _dest;
	}


	public void set_dest(int _dest) {
		this._dest = _dest;
	}

	public double get_speed() {
		return _speed;
	}


	public void set_speed(double _speed) {
		this._speed = _speed;
	}
	 public String toJSON() {
	        String ans = "{\"Robot\":{\"id\":" + this._id + "," + "\"value\":" + this._value + "," + "\"src\":" + this._src + "," + "\"dest\":" + this._dest + "," + "\"speed\":" + this._speed + "," + "\"pos\":\"" + this._pos.toString() + "\"" + "}" + "}";
	        return ans;
	 }
	 
	 @Override
	public String toString() {
		return toJSON();
	}

	public static void main(String[] a) {
	        String s1="{\"Robot\":{\"id\":0,\"value\":0.0,\"src\":0,\"dest\":-1,\"speed\":1.0,\"pos\":\"35.18753053591606,32.10378225882353,0.0\"}}";
	        System.out.println(s1+"\n");
	        Robot r=new Robot();
	        r.initFromJson(s1);
	        System.out.println(r);
	    }
   
}