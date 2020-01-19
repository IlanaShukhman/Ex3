package gameClient;


import org.json.JSONObject;

import utils.Point3D;

/**
 * This class represents a Robot on a graph. It has an id, a position on the graph,
 * value, speed, and a fruit target. It also has a source node and a destination node.
 * @author Ilana
 *
 */

public class Robot {
	private int _id;
	private Point3D _pos;
	private double _value;
	private int _src;
	private int _dest;
	private double _speed;
	private Fruit target;



	/**
	 * Constructors
	 */
	public Robot(){
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

	/**
	 * This method receives a string in the JSON format, and initializes the robot from the string.
	 * @param json 
	 */
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

	public void set_pos(String _pos) {
		int count=0;
		String _x = "";
		String _y = "";

		for (int i = 0; i < _pos.length(); i++) {
			if(_pos.charAt(i)==',' && count==0) {
				_x = _pos.substring(0,i-1);
				count=i;
			}
			else if(_pos.charAt(i)==',' && count!=0) {
				_y=_pos.substring(count+1, i-1);
			}
		}
		double x=Double.parseDouble(_x);
		double y=Double.parseDouble(_y);
		Point3D p=new Point3D(x,y);

		this._pos=p;
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

	public Fruit getTarget() {
		return target;
	}

	public void setTarget(Fruit target) {
		this.target = target;
	}


	/**
	 * This method returns this fruit as a string in a JSON format
	 */
	public String toJSON() {
		String ans = "{\"Robot\":{\"id\":" + this._id + "," + "\"value\":" + this._value + "," + "\"src\":" + this._src + "," + "\"dest\":" + this._dest + "," + "\"speed\":" + this._speed + "," + "\"pos\":\"" + this._pos.toString() + "\"" + "}" + "}";
		return ans;
	}

	/**
	 * This method returns the String of a fruit.
	 */
	@Override
	public String toString() {
		//return "Robot:\tId="+this._id+"\tGrade="+this.get_value()+"\tLoc=["+this._src+","+this._dest+"]\n";
		return toJSON();
	}
	/**
	 * This method returns true if this robot and robot r are the same
	 * equals by id
	 * @param r
	 * @return
	 */
	public boolean equals(Robot r) {
		if(r!=null && this.get_id()==r.get_id())
			return true;
		return false;
	}

	public static void main(String[] a) {
		String s1="{\"Robot\":{\"id\":0,\"value\":0.0,\"src\":0,\"dest\":-1,\"speed\":1.0,\"pos\":\"35.18753053591606,32.10378225882353,0.0\"}}";
		System.out.println(s1+"\n");
		Robot r=new Robot();
		r.initFromJson(s1);
		System.out.println(r);
	}

}