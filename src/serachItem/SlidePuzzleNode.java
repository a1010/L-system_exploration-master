package serachItem;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import slidepuzzle_with_Lsystem.SlidePuzzle;

public class SlidePuzzleNode extends Cell {
	String boardState;
	// 親セル
	private SlidePuzzleNode parent;
	// 子セル
	private ArrayList<SlidePuzzleNode> children;
	//
	private SlidePuzzleNode next_parent;
	//
	private ArrayList<SlidePuzzleNode> next_children;

	private ArrayList<String> next_state_list;

	private Point point;

	private String direction = "";

	private int variation_x = 0;
	private int variation_y = 0;
	private int step = 0;

	/**
	 * @param state
	 * @param board
	 * @param direction
	 * @param parent    parentがnullの場合はルートノード
	 * 
	 */
	public SlidePuzzleNode(String state, String board, String direction, SlidePuzzleNode parent) {
		super();
		this.state = state;
		children = new ArrayList<SlidePuzzleNode>();
		next_parent = parent;
		next_children = new ArrayList<SlidePuzzleNode>();
		next_state_list = new ArrayList<String>();
		this.boardState = board;
		this.direction = direction;
		this.parent = parent;
		this.point = new Point(0, 0);

		if (parent != null) {
			Point parent_Point = parent.getPoint();
			if (direction.equals("8"))
				setPoint(parent_Point.x + 0, parent_Point.y + 1);
			else if (direction.equals("4"))
				setPoint(parent_Point.x + 1, parent_Point.y + 0);
			else if (direction.equals("6"))
				setPoint(parent_Point.x + 1, parent_Point.y + 0);
			else if (direction.equals("2"))
				setPoint(parent_Point.x + 0, parent_Point.y + 1);
			else
				System.out.println("方向が設定されていない");
			this.step = parent.step + 1;
		}

	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getDirection() {
		return direction;
	}

	public SlidePuzzleNode getParent() {
		return parent;
	}

	public Iterator<SlidePuzzleNode> getChildren() {
		return children.iterator();
	}

	public ArrayList<SlidePuzzleNode> getChildrenByArrayList() {
		return children;
	}

	public void setBoardState(String boardState) {
		this.boardState = boardState;
	}

	public String getBoardState() {
		return boardState;
	}

	public void addChild(SlidePuzzleNode node) {
		if (node != null)
			next_children.add(node);
		else {
			System.out.println("childにnull追加");
		}
	}

	public void setParent(SlidePuzzleNode parent) {
		this.next_parent = parent;
	}

	public void setPoint(Point p) {
		this.point = p;
	}

	public void setPoint(int x, int y) {
		this.point = new Point(x, y);
	}

	public Point getPoint() {
		return point;
	}

	public int getVariationX() {
		return variation_x;
	}

	public int getVariationY() {
		return variation_y;
	}

	public void dead() {
		deadflag = true;
	}

	public boolean getDeadFlag() {
		return deadflag;
	}

	public int getStep() {
		return step;
	}

	public void delete() {
		if (deadflag) {
			// SlidePuzzle.MAP.remove(this.boardState);
			if (parent != null)
				parent.children.remove(this);
			for (SlidePuzzleNode child : children) {
				child.parent = null;
			}
			// TODO 動作要確認
			SlidePuzzle.MAP.remove(this.boardState);
			parent = null;
			children.clear();
			next_children.clear();
			next_parent = null;
			next_state_list.clear();
		}
	}

	// TODO 親の処理改良すべき
	@Override
	public void update() {
		parent = next_parent;
		state = next_state;
		for (SlidePuzzleNode node : next_children) {
			if (SlidePuzzle.MAP.get(node.getBoardState()) == null) {
				children.add(node);
				SlidePuzzle.MAP.put(node.getBoardState(), true);
			}

		}
		next_children.clear();
		next_state_list.clear();
	}

	@Override
	public void setState(String state) {
		next_state_list.add(state);
		next_state = state;
		// if(next_state_list.size() > 1)
		// System.out.println("next_stateが2個以上");

		// if(next_state_list.size() > 1){
		// if(!next_state_list.get(0).equals(next_state_list.get(1)))
		// System.out.println("next_stateが2個以上");
		// }
	}

}
