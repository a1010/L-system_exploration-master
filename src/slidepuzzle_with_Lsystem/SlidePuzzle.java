package slidepuzzle_with_Lsystem;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

import lsystem.Lsystem;
import lsystem.SlidePuzzleLsystem;
import serachItem.SlidePuzzleNode;

// 君は何をまとめたクラスなんだい??????
// スライドパズルに関係してるのかい??
public class SlidePuzzle {

	// 垂直、は?何のこと??
	static int vertical;
	// 水平、いい加減説明してくれ
	static int horizon;
	// なんで文字列型?????
	// L-systemの文法的なこと??どういうことなの??
	// 君の意見が聞きたい
	static String goal;
	static String start;
	// このMAPは何の文字列と真偽を格納しているの??
	public static HashMap<String, Boolean> MAP;
	// ArrayDequeって何??そもそもSlidePuzzleNodeって何??
	// drawって何を描くの??
	public static ArrayDeque<SlidePuzzleNode> drawBuffer = new ArrayDeque<SlidePuzzleNode>();
	// drawのリストって何??何がしたいの??
	public static ArrayList<SlidePuzzleNode> drawList = new ArrayList<SlidePuzzleNode>();
	// バッファをlockって何??なんでロックするの??
	public static boolean buffer_lock = false;

	// わかんないよぉ......他の変数は初期化しなくていいのぉ???????
	public SlidePuzzle(int vertical, int horizon) {
		SlidePuzzle.vertical = vertical;
		SlidePuzzle.horizon = horizon;
		MAP = new HashMap<String, Boolean>();
	}

	// ??????たすけてstartって何goalって何??????
	// お前らは何のstartとgoalを示しているんだい??????
	public SlidePuzzle(int vertical, int horizon, String start, String goal) {
		SlidePuzzle.vertical = vertical;
		SlidePuzzle.horizon = horizon;
		SlidePuzzle.goal = goal;
		SlidePuzzle.start = start;
		MAP = new HashMap<String, Boolean>();
	}

	/**
	 * 空白マスと指定したマスの交換 ↑↑↑は????お前はスライドパズルやってんのか????Lsystemの研究しろカス
	 * Lsystemに使ってんのかよ!!意味わかんねぇよ!!!キレた…許さねぇからな……!!! 使ってんなら論文に参考文献として記しておけカス!!ぶっ〇すぞ!!
	 * 
	 * @param node
	 * @param change_type 2:TOP, 4:LEFT, 6:RIGHT, 8:BOTTOM
	 * @return
	 */
	// 数字の意味を示せえええええええええ!!!!!!!!
	//
	// 以下において空白マスが[5]の時に2，4，6，8に位置するマスのみ交換可能
	// １ ２ ３
	// ４ ５ ６
	// ７ ８ ９
	// 交換の分岐条件としてこの数字を用いている
	//
	// だろうがあああああああああああ!!!!!!!!!!!!!
	// てか、2,4,6,8で分かれって無理があるだろ。列挙型使え間抜け
	// direction = {TOP, RIGHT, LEFT, BOTTOM}でいいだろうが頭使え
	// ハッ!?さては貴様Java6環境の民……いやねぇわ、アップデートしとけ能無し
	public static String change(SlidePuzzleNode node, String change_type) {
		// state???お前使う意味ねぇだろ
		// returnまで何も変化ねぇじゃねぇか
		// しかもなんで返り値が文字列なんだよ!交換出来たか否かの真偽で十分だろ
		// てか交換できなかった場合は「こうかんできなかった」で終わりかよ返却しろ!仕事しろ!何がしたいかわからんわボケ!!!
		String state = "";
		switch (node.getDirection()) {
		// 下のセルと交換、だろうがそれくらい書けやボケ
		case "8":
			// getBoardState()ってオイ……マジかよ……!!お前マジでスライドパズルやってんのかよぉ!!!!!!!
			if (change_type.equals("TOP"))
				state = changeTop(node.getBoardState());
			else if (change_type.equals("RIGHT"))
				state = changeRight(node.getBoardState());
			else if (change_type.equals("LEFT"))
				state = changeLeft(node.getBoardState());
			else if (change_type.equals("BOTTOM"))
				state = changeBottom(node.getBoardState());
			break;
		// 右のセルと交換、な小学生でも書けるぞつまりそういうことだ
		case "6":
			if (change_type.equals("LEFT"))
				state = changeTop(node.getBoardState());
			else if (change_type.equals("TOP"))
				state = changeRight(node.getBoardState());
			else if (change_type.equals("BOTTOM"))
				state = changeLeft(node.getBoardState());
			else if (change_type.equals("RIGHT"))
				state = changeBottom(node.getBoardState());
			break;
		// 左のセルと交換、どうしてこれだけのことをあなたは書けなかったんですか?明日までに考えてきてください
		// ほないただきます
		case "4":
			if (change_type.equals("RIGHT"))
				state = changeTop(node.getBoardState());
			else if (change_type.equals("BOTTOM"))
				state = changeRight(node.getBoardState());
			else if (change_type.equals("TOP"))
				state = changeLeft(node.getBoardState());
			else if (change_type.equals("LEFT"))
				state = changeBottom(node.getBoardState());
			break;
		// 上のセルと交換、なぁこれこの数字で表現してるのあほすぎないか?理解を求めてないだろ。もっとわかりやすく書けカス
		case "2":
			if (change_type.equals("BOTTOM"))
				state = changeTop(node.getBoardState());
			else if (change_type.equals("LEFT"))
				state = changeRight(node.getBoardState());
			else if (change_type.equals("RIGHT"))
				state = changeLeft(node.getBoardState());
			else if (change_type.equals("TOP"))
				state = changeBottom(node.getBoardState());
			break;
		// そりゃディレクションエラーも起きるわ分かりづら過ぎることを理解しろ
		default:
			// 交換できなかったことは返却しなくていいんですかねぇ?????????
			System.out.println("direction error");
			break;
		}
		// 君が返却されることを待っている奴はどこにもいないみたいです
		return state;
	}

	// 上と交換
	private static String changeTop(String state) {
		// StringBuilderってなんでつかぼきに教えてくだちい(ﾟ∀｡)
		//
		// Javaの文字列は固定長だぞ♡。＋で文字列つなげると新たにインスタンス作らんとあかんのよ
		// めっちゃ文字列つなげたいときはStringBuilderを使うと無駄にメモリ食わなくていいぞ♡
		StringBuilder sb = new StringBuilder(state);
		// 説明しとけハゲ。
		//
		// sb.indexOf("0")は文字列sbの中で初めに"0"が現れる位置を返す
		//
		// くらい書けるだろボケ自分が分かればいいと思ってるんじゃねぇ自己中野郎
		// しかも"0"が存在しない場合の例外処理してねぇじゃねぇかファッキン!!!返却値"-1"を考えろボケナス
		int zero_index = sb.indexOf("0");
		// ????意味が分からんどういった経緯で御社はこのような計算を行っているのか説明責任を果たしてもらっても...
		// ……怪奇式なんだよ!!!!!説明しろボケ!!!!!割り算と剰余算ということしか意味を受け取れねぇんだよカス
		//
		// ０ １ ２
		// ３ ４ ５
		// ６ ７ ８
		// horizon(水平長さ??列数でいいだろハゲ) = 3なので
		// "0"が出現する位置をhorizonで割ったときの商が行数
		// "0"が出現する位置をhorizonで割ったときの余りが列数
		//
		// 0 0:(0,0) 1:(0,1) 2:(0,2)
		// 1 3:(1,0) 4:(1,1) 5:(1,2)
		// 2 6:(2,0) 7:(2,1) 8:(2,2)
		// (y,x) 0 1 2
		//
		// くらい書かないと即座に理解できないわボケ〇すぞ!!
		int y = zero_index / horizon;
		int x = zero_index % horizon;
		// なんで最上段にあったらnullを返却するんだよ意味わかんねぇよ
		if (y == 0) {
			return null;
		}
		// y=1,2,3ならそれでいいがy=1,2しかねぇぞ
		int target_index = (y - 1) * horizon + x;
		char target_num = sb.charAt(target_index);
		sb.setCharAt(zero_index, target_num);
		sb.setCharAt(target_index, '0');

		return new String(sb);
	}

	// 下と交換
	// BotomじゃねぇんだよBottomだろうがハゲ野郎
	private static String changeBottom(String state) {
		StringBuilder sb = new StringBuilder(state);
		int zero_index = sb.indexOf("0");
		int y = zero_index / horizon;
		int x = zero_index % horizon;
		if (y == vertical - 1) {
			return null;
		}
		int target_index = (y + 1) * horizon + x;
		char target_num = sb.charAt(target_index);
		sb.setCharAt(zero_index, target_num);
		sb.setCharAt(target_index, '0');

		return new String(sb);
	}

	// 左と交換
	private static String changeLeft(String state) {
		StringBuilder sb = new StringBuilder(state);
		int zero_index = sb.indexOf("0");
		int y = zero_index / horizon;
		int x = zero_index % horizon;
		if (x == 0) {
			return null;
		}
		int target_index = (y) * horizon + x - 1;
		char target_num = sb.charAt(target_index);
		sb.setCharAt(zero_index, target_num);
		sb.setCharAt(target_index, '0');

		return new String(sb);
	}

	// 右と交換
	private static String changeRight(String state) {
		StringBuilder sb = new StringBuilder(state);
		int zero_index = sb.indexOf("0");
		int y = zero_index / horizon;
		int x = zero_index % horizon;
		if (x == horizon - 1) {
			return null;
		}
		int target_index = (y) * horizon + x + 1;
		char target_num = sb.charAt(target_index);
		sb.setCharAt(zero_index, target_num);
		sb.setCharAt(target_index, '0');

		return new String(sb);
	}

	/***
	 * 2つの盤面のマンハッタン距離の計算
	 * 
	 * @param now
	 * @param goal
	 * @return
	 */
	public static int[] calcDistance(SlidePuzzleNode now, SlidePuzzleNode goal) {
		String nowString = now.getBoardState();
		String goalString = goal.getBoardState();
		int distance_x = 0;
		int distance_y = 0;
		for (int i = 0; i < vertical * horizon - 1; i++) {
			int now_index = nowString.indexOf(Integer.toString(i));
			int goal_index = goalString.indexOf(Integer.toString(i));

			int y = now_index / horizon;
			int x = now_index % horizon;
			int goal_y = goal_index / horizon;
			int goal_x = goal_index % horizon;

			distance_x += Math.abs(goal_x - x);
			distance_y += Math.abs(goal_y - y);

			System.out.println("|" + x + " - " + goal_x + "|  ,  |" + y + " - " + goal_y + "|");
			System.out.println(distance_x + " , " + distance_y);
		}
		return new int[] { distance_x, distance_y };
	}

	/**
	 * ２つの盤面のマンハッタン距離を計算
	 * 
	 * @param nowString
	 * @param goalString
	 * @return int[]{distance_x,distance_y}
	 */
	public static int[] calcDistance(String nowString, String goalString) {
		int distance_x = 0;
		int distance_y = 0;
		for (int i = 0; i < vertical * horizon - 1; i++) {
			int now_index = nowString.indexOf(Integer.toString(i));
			int goal_index = goalString.indexOf(Integer.toString(i));

			int y = now_index / horizon;
			int x = now_index % horizon;
			int goal_y = goal_index / horizon;
			int goal_x = goal_index % horizon;

			distance_x += Math.abs(goal_x - x);
			distance_y += Math.abs(goal_y - y);

			// System.out.println("|"+x +" - "+goal_x+"| , |"+y +" - "+goal_y+"|");
			// System.out.println(distance_x +" , "+distance_y);
		}
		return new int[] { distance_x, distance_y };
	}

	public static String getGoalBoardState() {
		return goal;
	}

	public static String getStartBoardState() {
		return start;
	}

	public static void main(String args[]) {
		SlidePuzzleNode node = new SlidePuzzleNode("0", "123406785", "8", null);
		SlidePuzzle sp = new SlidePuzzle(3, 3, node.getBoardState(), "123456780");
		Lsystem lsys = new SlidePuzzleLsystem(20000);
		lsys.apply(node);
		node.update();
		java.util.Iterator<SlidePuzzleNode> sNode = node.getChildren();
		System.out.println("test");
		// System.out.println(node.children.size());
		while (sNode.hasNext()) {
			String state = sNode.next().getBoardState();
			state = state.substring(0, 3) + "\n" + state.substring(3, 6) + "\n" + state.substring(6, 9);
			System.out.println(state);
		}
		System.out.println(MAP.size());
	}
}
