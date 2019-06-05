package tamori;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class GEXFParser {


	static public HashMap<String, Node> read(){
		HashMap<String,Node> map = new HashMap<String,Node>();
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
	
	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ
		read();
	}

}
