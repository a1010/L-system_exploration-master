package slidepuzzle_with_Lsystem;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

import lsystem.Lsystem;
import lsystem.SlidePuzzleLsystem;
import serachItem.SlidePuzzleNode;

public class SlidePuzzle {

	static int vertical;
	static int horizon;
	static String goal;
	static String start;
	public static HashMap<String, Boolean> MAP;
	public static ArrayDeque<SlidePuzzleNode> drawBuffer = new ArrayDeque<SlidePuzzleNode>();
	public static ArrayList<SlidePuzzleNode> drawList = new ArrayList<SlidePuzzleNode>();
	public static boolean buffer_lock = false;
	public SlidePuzzle(int vertical,int horizon){
		SlidePuzzle.vertical = vertical;
		SlidePuzzle.horizon = horizon;
		MAP = new HashMap<String, Boolean>();
	}
	public SlidePuzzle(int vertical,int horizon,String start,String goal){
		SlidePuzzle.vertical = vertical;
		SlidePuzzle.horizon = horizon;
		SlidePuzzle.goal = goal;
		SlidePuzzle.start = start;
		MAP = new HashMap<String, Boolean>();
	}

	/**
	 * 空白マスと指定したマスの交換
	 * @param node
	 * @param change_type
	 * @return
	 */
	public static String change(SlidePuzzleNode node,String change_type){
		String state = "";
		switch (node.getDirection()) {
		case "8":
			if(change_type.equals("TOP")) state = changeTop(node.getBoardState());
			else if(change_type.equals("RIGHT")) state = changeRight(node.getBoardState());
			else if(change_type.equals("LEFT")) state = changeLeft(node.getBoardState());
			else if(change_type.equals("BOTOM")) state = changeBotom(node.getBoardState());
			break;
		case "6":
			if(change_type.equals("LEFT")) state = changeTop(node.getBoardState());
			else if(change_type.equals("TOP")) state = changeRight(node.getBoardState());
			else if(change_type.equals("BOTOM")) state = changeLeft(node.getBoardState());
			else if(change_type.equals("RIGHT")) state = changeBotom(node.getBoardState());
			break;
		case "4":
			if(change_type.equals("RIGHT")) state = changeTop(node.getBoardState());
			else if(change_type.equals("BOTOM")) state = changeRight(node.getBoardState());
			else if(change_type.equals("TOP")) state = changeLeft(node.getBoardState());
			else if(change_type.equals("LEFT")) state = changeBotom(node.getBoardState());
			break;
		case "2":
			if(change_type.equals("BOTOM")) state = changeTop(node.getBoardState());
			else if(change_type.equals("LEFT")) state = changeRight(node.getBoardState());
			else if(change_type.equals("RIGHT")) state = changeLeft(node.getBoardState());
			else if(change_type.equals("TOP")) state = changeBotom(node.getBoardState());
			break;
		default:
			System.out.println("direction error");
			break;
		}
		return state;	
	}

	//上と交換
	private static String changeTop(String state){
		StringBuilder sb = new StringBuilder(state);
		int zero_index = sb.indexOf("0");
		int y = zero_index / horizon;
		int x = zero_index % horizon;
		if(y == 0){
			return null;
		}
		int target_index = (y-1)*horizon + x;
		char target_num = sb.charAt(target_index);
		sb.setCharAt(zero_index, target_num);
		sb.setCharAt(target_index, '0');

		return new String(sb);
	}

	//下と交換
	private static String changeBotom(String state){
		StringBuilder sb = new StringBuilder(state);
		int zero_index = sb.indexOf("0");
		int y = zero_index / horizon;
		int x = zero_index % horizon;
		if(y == vertical - 1){
			return null;
		}
		int target_index = (y+1)*horizon + x;
		char target_num = sb.charAt(target_index);
		sb.setCharAt(zero_index, target_num);
		sb.setCharAt(target_index, '0');

		return new String(sb);
	}

	//左と交換
	private static String changeLeft(String state){
		StringBuilder sb = new StringBuilder(state);
		int zero_index = sb.indexOf("0");
		int y = zero_index / horizon;
		int x = zero_index % horizon;
		if(x == 0){
			return null;
		}
		int target_index = (y)*horizon + x - 1;
		char target_num = sb.charAt(target_index);
		sb.setCharAt(zero_index, target_num);
		sb.setCharAt(target_index, '0');

		return new String(sb);
	}

	//右と交換
	private static String changeRight(String state){
		StringBuilder sb = new StringBuilder(state);
		int zero_index = sb.indexOf("0");
		int y = zero_index / horizon;
		int x = zero_index % horizon;
		if(x == horizon - 1){
			return null;
		}
		int target_index = (y)*horizon + x + 1;
		char target_num = sb.charAt(target_index);
		sb.setCharAt(zero_index, target_num);
		sb.setCharAt(target_index, '0');

		return new String(sb);
	}

	/***
	 * 2つの盤面のマンハッタン距離の計算
	 * @param now
	 * @param goal
	 * @return
	 */
	public static int[] calcDistance(SlidePuzzleNode now ,SlidePuzzleNode goal){
		String nowString = now.getBoardState();
		String goalString = goal.getBoardState();
		int distance_x = 0;
		int distance_y = 0;
		for(int i = 0;i < vertical*horizon-1;i++){
			int now_index = nowString.indexOf(Integer.toString(i));
			int goal_index = goalString.indexOf(Integer.toString(i));

			int y = now_index / horizon;
			int x = now_index % horizon;
			int goal_y = goal_index / horizon;
			int goal_x = goal_index % horizon;

			distance_x += Math.abs(goal_x - x);
			distance_y += Math.abs(goal_y - y);

			System.out.println("|"+x +" - "+goal_x+"|  ,  |"+y +" - "+goal_y+"|");
			System.out.println(distance_x +" , "+distance_y);
		}
		return new int[]{distance_x,distance_y};
	}
	/**
	 * ２つの盤面のマンハッタン距離を計算
	 * @param nowString
	 * @param goalString
	 * @return int[]{distance_x,distance_y}
	 */
	public static int[] calcDistance(String nowString ,String goalString){
		int distance_x = 0;
		int distance_y = 0;
		for(int i = 0;i < vertical*horizon-1;i++){
			int now_index = nowString.indexOf(Integer.toString(i));
			int goal_index = goalString.indexOf(Integer.toString(i));

			int y = now_index / horizon;
			int x = now_index % horizon;
			int goal_y = goal_index / horizon;
			int goal_x = goal_index % horizon;

			distance_x += Math.abs(goal_x - x);
			distance_y += Math.abs(goal_y - y);

			//System.out.println("|"+x +" - "+goal_x+"|  ,  |"+y +" - "+goal_y+"|");
			//System.out.println(distance_x +" , "+distance_y);
		}
		return new int[]{distance_x,distance_y};
	}

	public static String getGoalBoardState(){
		return goal;
	}
	public static String getStartBoardState(){
		return start;
	}
	public static void main(String args[]){
		SlidePuzzleNode node = new SlidePuzzleNode("0", "123406785", "8", null);
		SlidePuzzle sp = new SlidePuzzle(3, 3,node.getBoardState(),"123456780");
		Lsystem lsys = new SlidePuzzleLsystem(20000);
		lsys.apply(node);
		node.update();
		java.util.Iterator<SlidePuzzleNode> sNode = node.getChildren();
		System.out.println("test");
		//System.out.println(node.children.size());
		while(sNode.hasNext()){
			String state = sNode.next().getBoardState();
			state = state.substring(0,3)+"\n"+state.substring(3,6)+"\n"+state.substring(6,9);
			System.out.println(state);
		}
		System.out.println(MAP.size());
	}
}
