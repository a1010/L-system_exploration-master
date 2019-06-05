package tamori;

import java.util.ArrayList;

public class Node {
	public String label = "";
	public int id = 0;
	public ArrayList<Edge> edge_out = new ArrayList<Edge>();
	public ArrayList<Edge> edge_in = new ArrayList<Edge>();
	private double position_x = 0;
	private double position_y = 0;
	
	public Node(int id){
		this.id = id;
		this.label = Integer.toString(id);
	}
	public Node(String label){
		this.id = Integer.parseInt(label);
		this.label = label;
	}
	public Node(String label,double position_x,double position_y){
		this.label = label;;
		this.position_x = position_x;
		this.position_y = position_y;
	}
	
	public int getX_i(){
		return (int)position_x;
	}
	public int getY_i(){
		return (int)position_y;
	}
	public int getX_i(double multiple){
		int origin_x = GraphDraw.SCREEN_WIDTH / 2;
		return (int)(position_x * multiple) + origin_x;
	}
	public int getY_i(double multiple){
		int origin_y = GraphDraw.SCREEN_HEIGHT / 2;
		return  origin_y - (int)(position_y * multiple);
	}
}
