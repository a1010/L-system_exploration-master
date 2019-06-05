package tamori;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;

public class GraphData {
	HashMap<String,Node> map = new HashMap<String,Node>();

	public GraphData(){
		read();
	}

	public void drawAll(Graphics g){
		int radius = 2;
		double multiple = 0.3;
		
		
		ArrayDeque<Node> node = new ArrayDeque<Node>();
		node.add(map.get("12345678"));
		while(!node.isEmpty()){
			Node tmp = node.poll();
			g.setColor(Color.RED);
			g.fillOval(tmp.getX_i(multiple) - radius, tmp.getY_i(multiple) - radius, radius*2, radius*2);
			for(Edge edge:tmp.edge_in){
				g.setColor(Color.BLACK);
				g.drawLine(edge.source.getX_i(multiple), edge.source.getY_i(multiple), edge.target.getX_i(multiple), edge.target.getY_i(multiple));
				node.add(edge.source);
			}
		}

	}

	public HashMap<String, Node> read(){
		try{
			File file = new File("AllNode.gexf");
			BufferedReader br = new BufferedReader(new FileReader(file));

			String str;
			boolean read_node = false;
			boolean read_edge = false;
			String id = "";
			String position_x = "";
			String position_y = "";

			while((str = br.readLine()) != null){
				str = str.trim();

				if(str.equals("<nodes>"))
					read_node = true;
				else if(str.equals("</nodes>"))
					read_node = false;
				else if(str.equals("<edges>"))
					read_edge = true;
				else if(str.equals("</edges>"))
					read_edge = false;
				if(read_node){
					String[] sp_str = str.split("\"");
					if(sp_str[0].trim().equals("<node id=")){
						id = sp_str[1];
					}
					else if(sp_str[0].trim().equals("<viz:position x=")){
						position_x = sp_str[1];
						position_y = sp_str[3];
					}
					else if(sp_str[0].trim().equals("</node>")){
						Node node = new Node(id, Double.parseDouble(position_x), Double.parseDouble(position_y));
						if(map.containsKey(id)) System.out.println(id);
						map.put(id, node);
					}
				}
				if(read_edge){
					String[] sp_str = str.split("\"");
					if(sp_str[0].trim().equals("<edge source=")){
						Edge edge = new Edge(map.get(sp_str[1]), map.get(sp_str[3]));
						map.get(sp_str[1]).edge_out.add(edge);
						map.get(sp_str[3]).edge_in.add(edge);
					}

				}
			}

			//			for(Edge edge:map.get("12345678").edge_out){
			//				System.out.println(edge.source.id);	
			//			}

			br.close();
			return map;
		}catch(FileNotFoundException e){
			System.out.println(e);
		}catch(IOException e){
			System.out.println(e);
		}
		return map;
	}

}
