package tamori;

import java.util.ArrayList;

public class Edge {
	public Node source;
	public Node target;
	public String label = "";
	
	public Edge(Node source,Node target){
		this.source = source;
		this.target = target;
	}
	public Edge(Node source,Node target,String label){
		this.source = source;
		this.target = target;
		this.label = label;
	}
}
