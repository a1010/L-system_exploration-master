package tamori;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.HashMap;

import org.omg.CORBA.PUBLIC_MEMBER;

public class GEXFWriter {
	public HashMap<String,Node> map = new HashMap<String,Node>();
	public String[] commands = new String[]{"LS1","LS2","LS3","LS4","LR1","LR2","LR3","LR4"};
	int cnt = 0;

	public void run(String id){
		System.out.println(Runtime.getRuntime().maxMemory());
		System.out.println(Runtime.getRuntime().totalMemory());
		Node start = new Node(id);
		map.put(start.label, start);
		ArrayDeque<Node> deque = new ArrayDeque<>();
		deque.add(start);
		while(!deque.isEmpty()){
			Node nowNode = deque.poll();
			testLsys(nowNode);
			for(Edge edge:nowNode.edge_out){
				if(!map.containsValue(edge.target)){
					map.put(edge.target.label, edge.target);
					deque.add(edge.target);
				}
			}

		}
		deque.clear();
		map.clear();
		System.out.println(map.size() +","+cnt);
		try {
			//ヘッダーの書き込み
			FileWriter fw = new FileWriter("test.gexf", false);
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
			//ヘッダー
			pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			pw.println("<gexf xmlns=\"http://www.gexf.net/1.1draft\" version=\"1.1\" xmlns:viz=\"http://www.gexf.net/1.1draft/viz\" "
					+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.gexf.net/1.1draft http://www.gexf.net/1.1draft/gexf.xsd\">");
			pw.println("<meta lastmodifieddate=\"2014-08-28\">");
			pw.println("<creator>Gephi 0.8</creator>");
			pw.println("<description></description>");
			pw.println("</meta>");
			pw.println("<graph defaultedgetype=\"directed\" timeformat=\"double\" mode=\"dynamic\">");
			//ファイルに書き出す
			pw.close();
			fw.close();
			//終了メッセージを画面に出力する
			System.out.println("出力が完了しました。1/3");
			
			
			//ノード情報の書き込み
			FileWriter fw2 = new FileWriter("test.gexf", true);
			PrintWriter pw2 = new PrintWriter(new BufferedWriter(fw2));
			//ノード出力
			pw2.println("<nodes>");
			deque.add(start);
			map.put(start.label, start);
			while(!deque.isEmpty()){
				Node nowNode = deque.poll();
				pw2.println("<node id=\""+nowNode.id+"\" label=\""+nowNode.label+"\" />");
				for(Edge edge : nowNode.edge_out){
					if(!map.containsValue(edge.target)){
						map.put(edge.target.label, edge.target);
						deque.add(edge.target);
					}
				}
			}
			pw2.println("</nodes>");
			//ファイルに書き出す
			pw2.close();
			fw2.close();
			deque.clear();
			map.clear();
			//終了メッセージを画面に出力する
			System.out.println("出力が完了しました。2/3");
			
			
			//エッジ情報の書き込み
			FileWriter fw3 = new FileWriter("test.gexf", true);
			PrintWriter pw3 = new PrintWriter(new BufferedWriter(fw3));
			//エッジ出力
			pw3.println("<edges>");
			deque.add(start);
			map.put(start.label, start);
			while(!deque.isEmpty()){
				Node nowNode = deque.poll();
				for(Edge edge : nowNode.edge_out){
					pw3.println("<edge label=\""+edge.label+"\" source=\""+edge.source.label+"\" target=\""+edge.target.label+"\" />");
					if(!map.containsValue(edge.target)){
						map.put(edge.target.label, edge.target);
						deque.add(edge.target);
					}
				}
			}
			pw3.println("</edges>");
			pw3.println("</graph>");
			pw3.println("</gexf>");
			//ファイルに書き出す
			map.clear();
			deque.clear();
			pw3.close();
			fw3.close();
			//終了メッセージを画面に出力する
			System.out.println("出力が完了しました。3/3");

		} catch (IOException ex) {
			//例外時処理
			ex.printStackTrace();
		}
	}

	public void testLsys(Node node){
		//全状態生成
		for(String command : commands){
			String newLabel = deform(node.label, command);
			//既にキーが入っている場合はスキップ
			if(!map.containsKey(newLabel)){
				Node newNode = new Node(newLabel);
				Edge newEdge = new Edge(node, newNode, command);
				node.edge_out.add(newEdge);
				newNode.edge_in.add(newEdge);
				cnt++;
			}
			else{
				Node existNode = map.get(newLabel);
				Edge newEdge = new Edge(node, existNode, command);
				node.edge_out.add(newEdge);
				existNode.edge_in.add(newEdge);
				cnt++;
			}
		}
		if(node.edge_out.size() > 8){
			System.out.println("");
		}
	}
	public String deform(String label,String command){
		String tmp = "";
		String result = "";
		switch (command) {
		case "LR1":
			tmp = label.substring(0, 2) + label.substring(label.length() - 1, label.length());
			result = tmp.substring(1,2) + tmp.substring(2,3) + label.substring(2,label.length() - 1) + tmp.substring(0,1);
			//System.out.println(result);
			break;
		case "LR2":
			tmp = label.substring(1, 4);
			result = label.substring(0,1) + tmp.substring(1 ,2) + tmp.substring(2 ,3) + tmp.substring(0 ,1) +label.substring(4 ,label.length()); 
			//System.out.println(result);
			break;
		case "LR3":
			tmp = label.substring(3, 6);
			result = label.substring(0,3) + tmp.substring(1 ,2) + tmp.substring(2 ,3) + tmp.substring(0 ,1) +label.substring(6 ,label.length());  
			break;
		case "LR4":
			tmp = label.substring(5, 8);
			result = label.substring(0,5) + tmp.substring(1 ,2) + tmp.substring(2 ,3) + tmp.substring(0 ,1); 
			break;
		case "LS1":
			tmp = label.substring(0, 3);
			result = tmp.substring(1,2) + tmp.substring(2, 3) + tmp.substring(0, 1) + label.substring(3,label.length());
			break;
		case "LS2":
			tmp = label.substring(2, 5);
			result = label.substring(0, 2) + tmp.substring(1,2) + tmp.substring(2, 3) + tmp.substring(0, 1) + label.substring(5,label.length());
			break;
		case "LS3":
			tmp = label.substring(4, 7);
			result = label.substring(0, 4) + tmp.substring(1,2) + tmp.substring(2, 3) + tmp.substring(0, 1) + label.substring(label.length() - 1,label.length());
			break;
		case "LS4":
			tmp = label.substring(0, 1) + label.substring(6, 8);
			result = tmp.substring(1, 2) + label.substring(1, 6) + tmp.substring(2, 3) + tmp.substring(0, 1);
			break;
		default:
			System.out.println("No command!!!!");
			break;

		}

		return result;
	}


	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ
		GEXFWriter gexfWriter = new GEXFWriter();
		gexfWriter.run("86543217");
		//gexfWriter.deform("12345678", "LR2");
	}

}
