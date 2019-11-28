package serachItem;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import maze_with_Lsystem.Maze;

public class MazeNode extends Cell {

	public Maze sp;
	private MazeNode parent;
	private ArrayList<MazeNode> children;
	private MazeNode next_parent;
	private ArrayList<MazeNode> next_children;
	private ArrayList<String> next_state_list;

	private Point point;
	private Point startPoint;
	private Point goalPoint;

	private String direction = "";

	private int length = 0;

	// 終了状態(updateしない、固定状態)になっているか
	private boolean finish = false;

	// 現在の位置している高さ。この高さ以下の障害物は乗り越えられる
	private int height_step = 0;

	public boolean wall = false;

	// 中間を通過済みかどうか
	public boolean isCheck = false;

	/**
	 * @param sp        対応するMaze
	 * @param state
	 * @param board
	 * @param direction
	 * @param parent    parentがnullの場合はルートノード
	 * 
	 */
	public MazeNode(Maze sp, String state, String direction, MazeNode parent, Point point, int length) {
		super();
		this.sp = sp;
		this.state = state;
		children = new ArrayList<MazeNode>();
		next_parent = parent;
		next_children = new ArrayList<MazeNode>();
		next_state_list = new ArrayList<String>();
		this.direction = direction;
		this.parent = parent;
		this.point = point;
		this.length = length + 1;
		if (Maze.getFieldSetting())
			height_step = Maze.getFieldHeightStep(point.x, point.y);
	}

	// コンストラクタ
	public MazeNode(Maze sp, String state, String direction, MazeNode parent, Point startPoint, Point goalPoint,
			int length) {
		super();
		this.sp = sp;
		this.state = state;
		children = new ArrayList<MazeNode>();
		next_parent = parent;
		next_children = new ArrayList<MazeNode>();
		next_state_list = new ArrayList<String>();
		this.direction = direction;
		this.parent = parent;
		this.startPoint = startPoint;
		this.goalPoint = goalPoint;
		this.length = length + 1;
		if (Maze.getFieldSetting())
			height_step = Maze.getFieldHeightStep(startPoint.x, goalPoint.y);
	}

	// 方向の変更
	public void setDirection(String direction) {
		this.direction = direction;
	}

	// 方向の取得
	public String getDirection() {
		return direction;
	}

	// 親ノードの取得
	public MazeNode getParent() {
		return parent;
	}

	// Iteratorとして子ノードを取得
	public Iterator<MazeNode> getChildren() {
		return children.iterator();
	}

	// ArrayListとして子ノードを取得
	public ArrayList<MazeNode> getChildrenByArrayList() {
		return children;
	}

	// 子ノードの追加
	public void addChild(MazeNode node) {
		if (node != null)
			next_children.add(node);
		else {
			System.out.println("childにnull追加");
		}
	}

	// 親ノードの設定
	public void setParent(MazeNode parent) {
		this.next_parent = parent;
	}

	// 位置の設定
	public void setPoint(Point p) {
		this.point = p;
	}

	// 位置の設定
	public void setPoint(int x, int y) {
		this.point = new Point(x, y);
	}

	// 位置の取得
	public Point getPoint() {
		return point;
	}

	// 死滅フラグ
	public void dead() {
		deadflag = true;
	}

	// 死滅フラグの取得
	public boolean getDeadFlag() {
		return deadflag;
	}

	// 現在位置の高さを取得
	public int getHeightStep() {
		return height_step;
	}

	// 長さを取得
	public int getLength() {
		return length;
	}

	// 死滅フラグが立っている場合に実行
	// TODO 再帰処理したほうがよくね？ by titanis
	// あるいは継承クラスを作ってそっちで処理
	public void delete() {
		if (deadflag) {
			if (parent != null)
				parent.children.remove(this);
			for (MazeNode child : children) {
				child.parent = null;
			}
			// TODO 怪しい
			sp.setNode(point.x, point.y, null);
			parent = null;
			children.clear();
			next_children.clear();
			next_parent = null;
			next_state_list.clear();
		}
	}

	/**
	 * 障害物のチェック
	 * 
	 * @param wall_height_x
	 * @param wall_height_y
	 * @return
	 */
	private boolean hazard_check(int wall_height_x, int wall_height_y) {
		if (sp.getWallSetting()) {
			if (sp.getWallPoint(wall_height_x, wall_height_y) == false)
				return true;
			else
				return false;
		} else if (Maze.getFieldSetting()) {
			int next_height = Maze.getFieldHeightStep(wall_height_x, wall_height_y);
			// 高さが100以上の場合は壁
			if (next_height >= 100)
				return false;
			if (next_height <= height_step) {
				return true;
			} else {
				// 一段登って何もしない
				height_step++;
				length++;
				return false;
			}
		} else
			return true;
	}

	// TODO 親の処理まだ甘い.(root化の部分)
	@Override
	public void update() {
		// System.out.println(height_step);
		if (finish == false) {
			parent = next_parent;
			if (next_state == null)
				System.out.println("nextstate指定されていない");
			state = next_state;
			for (MazeNode node : next_children) {
				Point cPoint = node.point;
				if (sp.getNode(cPoint.x, cPoint.y) == null) {
					if (hazard_check(cPoint.x, cPoint.y)) {
						children.add(node);
						sp.setNode(cPoint.x, cPoint.y, node);
					}
				}

			}
			next_children.clear();
			next_state_list.clear();
		}
	}

	@Override
	public void setState(String state) {
		next_state_list.add(state);
		next_state = state;
	}

	/**
	 * updateを通さず直ちに状態を変更する
	 * 
	 * @param state
	 */
	public void setStateImmediately(String state) {
		this.state = state;
		this.next_state = state;
		next_children.clear();
		next_state_list.clear();
	}

	public void endProcess(String state) {
		finish = true;
		this.state = state;
	}

	public void setIsCheck(boolean bool) {
		isCheck = bool;
	}
}
