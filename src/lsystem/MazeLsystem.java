package lsystem;

import java.awt.Point;

import maze_with_Lsystem.Maze;
import serachItem.MazeNode;
import sun.security.util.Length;
import maze_with_Lsystem.Main_Maze;
import serachItem.Cell;
import maze_with_Lsystem.Simulator2D;

public class MazeLsystem extends Lsystem {

	// ノードの数
	public int max_node_count;
	// ノードを中心とする視野
	// 視野内で未探索の方向に指向性を持つ
	public int sight;

	// 分岐の比率
	private double pa_rate;
	private double pb_rate;
	private double pc_rate;

	// 分岐確率
	private double pd;
	private double pa;
	private double pb;
	private double pc;
	private double P;

	public static int debug_state_0 = 0;
	public static int debug_state_1 = 0;
	public static int debug_state_2 = 0;
	public static int debug_state_3 = 0;
	public static int debug_state_d = 0;
	public static int debug_state_D = 0;
	public static int debug_dead_count = 0;
	public static int debug_rule_2 = 0;
	public static int debug_rule_3 = 0;
	public static int debug_apply_0 = 0;
	public static int debug_apply_1 = 0;
	public static int debug_apply_2 = 0;
	public static int debug_apply_3 = 0;
	public static int debug_apply_d = 0;
	public static int debug_apply_D = 0;
	public static int debug_generate_0 = 0;
	public static int debug_generate_1 = 0;
	public static int debug_generate_2 = 0;
	public static int debug_generate_3 = 0;
	public static int debug_generate_d = 0;
	public static int debug_generate_D = 0;

	private static double deleteTime;
	private static boolean isChangeRate;
	private boolean calc_fractalDim = false;

	public MazeLsystem(int max_node_count, int sight) {
		this.sight = sight;
		this.max_node_count = max_node_count;
		this.pa_rate = -1;
		this.pb_rate = -1;
		this.pc_rate = -1;
	}

	public MazeLsystem(int max_node_count, int sight, int pa, int pb, int pc, boolean calc_fD) {
		this.sight = sight;
		this.max_node_count = max_node_count;
		this.pa_rate = pa;
		this.pb_rate = pb;
		this.pc_rate = pc;
		this.calc_fractalDim = calc_fD;
	}

	@Override
	public void apply(Cell node) {
		MazeNode mNode = (MazeNode) node;
		if (checkFinish(mNode)) {
			if (Maze.getCheckPointSetting()) {
				if (mNode.isCheck)
					finish(mNode);
			}
			if (!Maze.getCheckPointSetting()) {
				finish(mNode);
				// TODO: ゴール時のフラクタル次元記録用
				Simulator2D.draw_searchMAP = false;
				Main_Maze.screenShot(Main_Maze.result_dir + "fractalDim_" + Main_Maze.ID + ".png");
				Simulator2D.draw_searchMAP = true;
			}
		}

		// 中間地点チェック
		if (Maze.getCheckPointSetting() && checkCheckPoint(mNode)) {
			changeRate(mNode);
		}

		// 距離が離れると消える
		// deleteOutRange(mNode);

		// 中間地点を通過したノード以外の経路を消す
		deleteNotThroughCheck(mNode);

		// コンストラクタの引数で与えられてない場合（適当に数値入れてる）
		if (pa_rate == -1 && pb_rate == -1 && pc_rate == -1) {
			pa_rate = 8;
			pb_rate = 1;
			pc_rate = 1;
		}

		// シグモイド関数
		// ＿/￣みたいな関数
		double sig = sigmoid((double) Maze.getNodeCount() / (double) max_node_count, 4);
		P = 1 - sig;
		// System.out.println("P: " + P);
		pd = sig;
		pa = P * pa_rate / (pa_rate + pb_rate + pc_rate);
		pb = P * pb_rate / (pa_rate + pb_rate + pc_rate);
		pc = P * pc_rate / (pa_rate + pb_rate + pc_rate);

		String state = mNode.getState();
		switch (state) {
			case "0":
				rule_0(mNode);
				debug_state_0++;
				break;
			case "1":
				rule_1(mNode);
				debug_state_1++;
				break;
			case "2":
				rule_2(mNode);
				debug_state_2++;
				break;
			case "3":
				rule_3(mNode);
				debug_state_3++;
				break;
			case "E":
				rule_E(mNode);
				break;
			case "I":
				rule_I(mNode);
				break;
			case "D":
				rule_D(mNode);
				debug_state_D++;
				break;
			case "d":
				rule_d(mNode);
				debug_state_d++;
				break;
			default:
				break;
		}
	}

	// 終了状態チェック
	@Override
	public boolean checkFinish(Cell node) {
		MazeNode mNode = (MazeNode) node;
		if (Maze.checkGoalArrival(mNode)) {
			return true;
		}
		return false;
	}

	// ゴール地点から遠いもののノードの状態をEにする
	public void deleteOutRange(MazeNode node) {
		double range = Maze.getGoalRange(node);
		if (range >= Maze.getRangeStartTogoal() + 10) {
			node.endProcess("D");
		}
	}

	// 中間地点チェック
	public boolean checkCheckPoint(Cell node) {
		MazeNode mNode = (MazeNode) node;
		if (Maze.checkPointArrival(mNode)) {
			mNode.isCheck = true;
			return true;
		}
		return false;
	}

	// 終了処理(全てのノードの状態を"E"にする)
	public void finish(MazeNode node) {
		SaveInfo(node);
		node.endProcess("E");
		Maze.deleteGoal(node.getPoint().x, node.getPoint().y);
		//////// System.out.println("aaaaaaa");
	}

	// TODO 機能の見直し by titanis
	// Main_Mazeに依存したメソッド
	//
	private void SaveInfo(MazeNode node) {
		int step = Main_Maze.step_num;
		long ID = Main_Maze.ID;
		int DIV = Main_Maze.div_num;
		double sigB = Main_Maze.sig_bias;
		int MaxNodeCount = Main_Maze.max_node_count;
		int Length = node.getLength();
		int SearchArea = Maze.getSearchCount();
		String info = "ID" + ID + "," + DIV + "," + sigB + "," + step + "," + Length + "," + SearchArea + ","
				+ MaxNodeCount;
		Main_Maze.solution_info.add(info);
	}

	// 中間地点通過後の処理(粒度を変更する)
	public void changeRate(MazeNode node) {
		if (!isChangeRate) {
			setPa(0);
			setPb(0);
			setPc(1);
		}
	}

	// 粒度が変わったかチェック
	public boolean checkChangeRate() {
		return isChangeRate;
	}

	// 中間地点をどこかが通過したら通過していないやつは消える
	public void deleteNotThroughCheck(MazeNode mNode) {
		if (Maze.getCheckPointSetting() && Maze.getIsCheck() && !mNode.isCheck)
			mNode.endProcess("D");
	}

	// ゴールの方向を検出する
	public String getDirectionToGoal(MazeNode node) {
		String direction;
		Point point = new Point(node.getPoint().x, node.getPoint().y);
		Point goal = Maze.getGoalPoint();
		double ud = point.y - goal.y;
		double lr = point.x - goal.x;
		boolean width = false;
		boolean height = false;
		if (Math.abs(ud) >= Math.abs(lr))
			height = true;
		else
			width = true;

		if (height) {
			if (ud >= 0)
				direction = "BOTOM";
			else
				direction = "TOP";
		} else {
			if (lr >= 0)
				direction = "RIGHT";
			else
				direction = "LEFT";
		}
		return direction;
	}

	// デバック用
	public static void reset_debug_num() {
		debug_state_0 = 0;
		debug_state_1 = 0;
		debug_state_2 = 0;
		debug_state_3 = 0;
		debug_state_d = 0;
		debug_state_D = 0;
		debug_dead_count = 0;
		debug_rule_2 = 0;
		debug_rule_3 = 0;
		debug_apply_0 = 0;
		debug_apply_1 = 0;
		debug_apply_2 = 0;
		debug_apply_3 = 0;
		debug_apply_d = 0;
		debug_apply_D = 0;
		debug_generate_0 = 0;
		debug_generate_1 = 0;
		debug_generate_2 = 0;
		debug_generate_3 = 0;
		debug_generate_d = 0;
		debug_generate_D = 0;
	}

	/**
	 * 0 → 3 ; 1 0 → d
	 * 
	 * @param node
	 */
	private void rule_0(MazeNode node) {
		double random = Math.random();
		debug_apply_0++;
		if (random > pd) {
			debug_generate_0++;
			node.setState("3");
			String direction = changeDirection(node, "TOP");
			// String direction = changeDirection(node, getDirectionToGoal(node));
			Point next_point = changePosition(node, direction);
			if (next_point.x >= 0 && next_point.y >= 0 && next_point.x < Maze.width && next_point.y < Maze.height) {
				MazeNode node2 = new MazeNode("1", direction, node, next_point, node.getLength());
				node.addChild(node2);
				if (node.isCheck)
					node2.isCheck = true;
			}
		} else {
			node.setState("d");
		}
	}

	/**
	 * 1 → 2 1 → 3 ; 1
	 * 
	 * @param node
	 */
	private void rule_1(MazeNode node) {
		double random = Math.random();
		debug_apply_1++;
		if (random > (pa / P)) {
			node.setState("2");
		} else {
			debug_generate_1++;
			node.setState("3");
			String direction = changeDirection(node, "TOP");
			Point next_point = changePosition(node, direction);
			if (next_point.x >= 0 && next_point.y >= 0 && next_point.x < Maze.width && next_point.y < Maze.height) {
				MazeNode node2 = new MazeNode("1", direction, node, next_point, node.getLength());
				node.addChild(node2);
				if (node.isCheck)
					node2.isCheck = true;
			}
		}
	}

	/**
	 * 2 → 3 [1 ]1 ; 2 → 3 [0 ]0
	 * 
	 * @param node
	 */
	private void rule_2(MazeNode node) {
		double random = Math.random();
		int[] num;
		if (random <= 0.3334)
			num = new int[] { 0, 1, 2 };
		else if (random <= 0.6667)
			num = new int[] { 1, 2, 0 };
		else
			num = new int[] { 2, 0, 1 };

		debug_apply_2++;
		debug_generate_2++;
		// 子を３つ生成
		if (random > (pb / (pb + pc))) {
			debug_rule_3++;
			node.setState("3");
			Point[] next_point = new Point[3];
			String[] direction = new String[3];
			direction[num[0]] = changeDirection(node, "TOP");
			next_point[num[0]] = changePosition(node, direction[num[0]]);
			direction[num[1]] = changeDirection(node, "LEFT");
			next_point[num[1]] = changePosition(node, direction[num[1]]);
			direction[num[2]] = changeDirection(node, "RIGHT");
			next_point[num[2]] = changePosition(node, direction[num[2]]);
			for (int i = 0; i < next_point.length; i++) {
				if (next_point[i].x >= 0 && next_point[i].y >= 0 && next_point[i].x < Maze.width
						&& next_point[i].y < Maze.height) {
					MazeNode node2;
					if (i == 0)
						node2 = new MazeNode("1", direction[i], node, next_point[i], node.getLength());
					else
						node2 = new MazeNode("1", direction[i], node, next_point[i], node.getLength());
					node.addChild(node2);
					if (node.isCheck)
						node2.isCheck = true;
				}
			}
		}
		// 子を２つ生成
		else {
			debug_rule_2++;
			node.setState("3");
			Point[] next_point = new Point[2];
			String[] direction = new String[2];
			direction[0] = changeDirection(node, "LEFT");
			next_point[0] = changePosition(node, direction[0]);
			direction[1] = changeDirection(node, "RIGHT");
			next_point[1] = changePosition(node, direction[1]);
			for (int i = 0; i < next_point.length; i++) {
				if (next_point[i].x >= 0 && next_point[i].y >= 0 && next_point[i].x < Maze.width
						&& next_point[i].y < Maze.height) {
					MazeNode node2 = new MazeNode("0", direction[i], node, next_point[i], node.getLength());
					node.addChild(node2);
					if (node.isCheck)
						node2.isCheck = true;
				}
			}
		}
	}

	/**
	 * 3$ → d
	 * 
	 * @param node
	 */
	private void rule_3(MazeNode node) {
		debug_apply_3++;
		double ph = Maze.getArroundSearchResultRate(node.getPoint().x, node.getPoint().y, sight);
		double random = Math.random();
		// 視野0（評価関数無し）の場合
		if (sight == 0)
			node.setState("d");
		else if (random > ph) {
			// node.setState("3");
		} else {
			node.setState("d");
		}
	}

	/**
	 * 終了状態
	 * 
	 * @param node
	 */
	private void rule_E(MazeNode node) {
		MazeNode parent = node.getParent();
		if (parent != null) {
			parent.endProcess("E");
		}
	}

	/**
	 * 死滅ルール .D$ → d
	 * 
	 * @param node
	 */
	private void rule_D(MazeNode node) {
		node.setState("D");
		// 子ノードが存在しない場合のみ
		if (node.getChildrenByArrayList().size() == 0) {
			MazeNode parent = node.getParent();
			if (parent != null) {
				if (node.getChildrenByArrayList().size() == 0) {
					if (!parent.getState().equals("E"))
						parent.setState("d");
					node.dead();
					debug_dead_count++;
					debug_apply_D++;
					debug_generate_D++;
				}
			}
		}
	}

	/**
	 * 特別死滅ルール2 .I$ → I
	 * 
	 * @param node
	 */
	private void rule_I(MazeNode node) {
		node.setState("I");
		// 子ノードが存在しない場合のみ
		if (node.getChildrenByArrayList().size() == 0) {
			MazeNode parent = node.getParent();
			if (parent != null) {
				node.dead();
			}
		}
	}

	/**
	 * 成長と死滅の中間状態 問題に合わせた評価関数で次の状態を決めたい d → D d → 0
	 * 
	 * @param node
	 */
	private void rule_d(MazeNode node) {
		debug_apply_d++;
		double ph = Maze.getArroundSearchResultRate(node.getPoint().x, node.getPoint().y, sight);
		double random = Math.random();
		// 視野0（評価関数無し）の場合
		if (sight == 0)
			node.setState("D");
		else if (random > ph) {
			node.setState("0");
		} else {
			node.setState("D");
		}
	}

	public String changeDirection(MazeNode node, String change_type) {
		String direction = "";
		switch (node.getDirection()) {
			case "8":
				if (change_type.equals("TOP"))
					direction = "8";
				else if (change_type.equals("RIGHT"))
					direction = "6";
				else if (change_type.equals("LEFT"))
					direction = "4";
				else if (change_type.equals("BOTOM"))
					direction = "2";
				break;
			case "6":
				if (change_type.equals("TOP"))
					direction = "6";
				else if (change_type.equals("RIGHT"))
					direction = "2";
				else if (change_type.equals("LEFT"))
					direction = "8";
				else if (change_type.equals("BOTOM"))
					direction = "4";
				break;
			case "4":
				if (change_type.equals("TOP"))
					direction = "4";
				else if (change_type.equals("RIGHT"))
					direction = "8";
				else if (change_type.equals("LEFT"))
					direction = "2";
				else if (change_type.equals("BOTOM"))
					direction = "6";
				break;
			case "2":
				if (change_type.equals("TOP"))
					direction = "2";
				else if (change_type.equals("RIGHT"))
					direction = "4";
				else if (change_type.equals("LEFT"))
					direction = "6";
				else if (change_type.equals("BOTOM"))
					direction = "8";
				break;
			default:
				System.err.println("direction error");
				break;
		}
		return direction;
	}

	public Point changePosition(MazeNode node, String direction) {
		Point next_point;
		int x = node.getPoint().x;
		int y = node.getPoint().y;
		switch (direction) {
			case "2":
				next_point = new Point(x, y + 1);
				break;
			case "4":
				next_point = new Point(x - 1, y);
				break;
			case "6":
				next_point = new Point(x + 1, y);
				break;
			case "8":
				next_point = new Point(x, y - 1);
				break;
			default:
				next_point = new Point(x, y);
				System.err.println("想定外の方向を指定しています\n");
				break;
		}

		return next_point;
	}

	// xが0~1のシグモイド関数
	double sigmoid(double x, double gain) {
		return 1.0 / (1.0 + Math.exp(-gain * (x * 2 - 1)));
	}

	// paをセット
	public void setPa(double pa) {
		this.pa_rate = pa;
	}

	// pbをセット
	public void setPb(double pb) {
		this.pb_rate = pb;
	}

	// pcをセット
	public void setPc(double pc) {
		this.pc_rate = pc;
	}

}
