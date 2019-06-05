package serachItem;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;

import maze_with_Lsystem.Maze;

public class MazeNode extends Cell{
	//親セル
	private MazeNode parent;
	//子セル
	private ArrayList<MazeNode> children;
	//
	private MazeNode next_parent;
	//
	private ArrayList<MazeNode> next_children;

	private ArrayList<String> next_state_list;

	private Point point;
	private Point startPoint;
	private Point goalPoint;

	private String direction = "";
	
	private int length = 0;

	//終了状態(updateしない、固定状態)になっているか
	private boolean finish = false;

	//現在の位置している高さ。この高さ以下の障害物は乗り越えられる
	private int height_step = 0;

	public boolean wall = false;
	
	//中間を通過済みかどうか
	public boolean isCheck = false;
	
	/**
	 * @param state
	 * @param board
	 * @param direction
	 * @param parent parentがnullの場合はルートノード
	 * 
	 */
	public MazeNode(String state,String direction,MazeNode parent,Point point,int length){
		super();
		this.state = state;
		children = new ArrayList<MazeNode>();
		next_parent = parent;
		next_children = new ArrayList<MazeNode>();
		next_state_list = new ArrayList<String>();
		this.direction = direction;
		this.parent = parent;
		this.point = point;
		this.length = length + 1;
		if(Maze.getFieldSetting())
			height_step = Maze.getFieldHeightStep(point.x,point.y);
	}
	
	public MazeNode(String state, String direction, MazeNode parent, Point startPoint, Point goalPoint, int length){
		super();
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
		if(Maze.getFieldSetting())
			height_step = Maze.getFieldHeightStep(startPoint.x,goalPoint.y);
	}

	public void setDirection(String direction){
		this.direction = direction;
	}
	public String getDirection(){
		return direction;
	}
	public MazeNode getParent(){
		return parent;
	}
	public Iterator<MazeNode> getChildren(){
		return children.iterator();
	}
	public ArrayList<MazeNode> getChildrenByArrayList(){
		return children;
	}

	public void addChild(MazeNode node){
		if(node != null) next_children.add(node);
		else{
			System.out.println("childにnull追加");
		}
	}
	public void setParent(MazeNode parent){
		this.next_parent = parent;
	}
	public void setPoint(Point p){
		this.point = p;
	}
	public void setPoint(int x,int y){
		this.point = new Point(x ,y);
	}
	public Point getPoint(){
		return point;
	}
	public void dead(){
		dead = true;
	}
	public boolean getDeadFlag(){
		return dead;
	}
	public int getHeightStep(){
		return height_step;
	}
	public int getLength(){
		return length;
	}
	public void delete(){
		if(dead){
			if(parent != null)
				parent.children.remove(this);
			for(MazeNode child : children){
				child.parent = null;
			}
			//TODO 怪しい
			Maze.setNode(point.x, point.y, null);
			parent = null;
			children.clear();
			next_children.clear();
			next_parent = null;
			next_state_list.clear();
		}
	}

	/**
	 * 障害物のチェック
	 * @param wall_geight_x
	 * @param wall_height_y
	 * @return
	 */
	private boolean hazard_check(int wall_geight_x,int wall_height_y){
		if(Maze.getWallSetting()){
			if(Maze.getWallPoint(wall_geight_x, wall_height_y) == false)
				return true;
			else 
				return false;
		}
		else if(Maze.getFieldSetting()){
			int next_height = Maze.getFieldHeightStep(wall_geight_x,wall_height_y);
			//高さが100以上の場合は壁
			if(next_height >= 100)
				return false;
			if(next_height <= height_step){
				return true;
			}
			else{
				//一段登って何もしない
				height_step++;
				length++;
				return false;
			}
		}
		else 
			return true;
	}

	//TODO 親の処理まだ甘い.(root化の部分)
	@Override
	public void update(){
		//System.out.println(height_step);
		if(finish == false){
			parent = next_parent;
			if(next_state == null) System.out.println("nextstate指定されていない");
			state = next_state;
			for(MazeNode node : next_children){
				Point cPoint = node.point;
				if(Maze.getNode(cPoint.x, cPoint.y) == null){
					if(hazard_check(cPoint.x, cPoint.y)){
						children.add(node);
						Maze.setNode(cPoint.x, cPoint.y, node);
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
	 * @param state
	 */
	public void setStateImmediately(String state){
		this.state = state; 
		this.next_state = state;
		next_children.clear();
		next_state_list.clear();
	}
	public void endProcess(String state){
		finish = true;
		this.state = state;
	}
	
	public void setIsCheck(boolean bool){
		isCheck = bool;
	}
}
