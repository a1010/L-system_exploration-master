package slidepuzzle_with_Lsystem;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;

import processing.core.*;
import processing.event.MouseEvent;
import serachItem.SlidePuzzleNode;
import slidepuzzle_with_Lsystem.SlidePuzzle;

public class MySketch2D extends PApplet {

	ArrayList<SlidePuzzleNode> drawArray = new ArrayList<SlidePuzzleNode>();
	private static ArrayDeque<SlidePuzzleNode> drawBuffer = new ArrayDeque<SlidePuzzleNode>();
	private static boolean update_flag = false;

	public void settings() {
		size(600, 600);
		// プロットの色と大きさ

	}

	public void setup() {
	}

	public void draw() {

		background(0);  

		update();

		//四隅
		strokeWeight(10);
		point(0, width);
		point(width, 0);
		point(0, 0);
		point(width, width);
		
		//原点
		strokeWeight(10);
		stroke(255, 0, 0);
		point(0,0);

		stroke(0, 255, 0);

		for(SlidePuzzleNode node : drawArray){
			int[] dist = SlidePuzzle.calcDistance(node.getBoardState(), SlidePuzzle.getStartBoardState());
			point(dist[0]*10,dist[1]*10);
		}
	}

	public static void setBuffer(ArrayDeque<SlidePuzzleNode> nodes){
		while(!nodes.isEmpty()){
			SlidePuzzleNode node = nodes.poll();
			//System.out.println(node.getPoint());
			drawBuffer.add(node);
		}
		update_flag = true;
	}

	private void update(){
		if(update_flag){
			drawArray.clear();
			while(!drawBuffer.isEmpty()){
				drawArray.add(drawBuffer.poll());
			}
			update_flag = false;
		}
	}



	public static void main(String args[]) {
		PApplet.main(new String[] { "--location=100,100","draw.MySketch2D"});
	}
}