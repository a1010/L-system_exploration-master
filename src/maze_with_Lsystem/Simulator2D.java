package maze_with_Lsystem;

import java.awt.Point;
import java.util.ArrayList;

import processing.core.PApplet;


public class Simulator2D extends PApplet {

	private ArrayList<Point> drawArray = new ArrayList<Point>();
	private static ArrayList<Point> drawBuffer = new ArrayList<Point>();
	private static boolean update_flag = false;

	public static boolean draw_searchMAP = false;

	public static boolean finish = false;

	private static boolean output = false;
	private static String filename = "";

	public void settings() {
		while(Maze.finishSetUp == false){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		size(Maze.width, Maze.height);
		drawArray = new ArrayList<Point>();
		drawBuffer = new ArrayList<Point>();
		update_flag = false;

		finish = false;

		output = false;
		filename = "";

	}

	public void setup() {
	}

	public void draw() {

		background(0);  

		update();

		//フェロモン（仮）
		if(draw_searchMAP){
			stroke(255, 0, 0);
			strokeWeight(1);
			for(int x = 0;x < Maze.width; x++){
				for(int y = 0;y < Maze.height; y++){
					if(Maze.getSearchResult(x, y) == true){
						point(x , y);
					}
				}
			}
		}

		//原点
		strokeWeight(1);
		stroke(0, 255, 0);
		point(0,0);

		//壁があるときの描画 中間なし
		if(Maze.getWallSetting() && !Maze.getCheckPointSetting()){
			for(int y = 0;y < Maze.height;y++)
			{
				for(int x = 0;x < Maze.width;x++){
					if(Maze.getWallPoint(x, y)){
						stroke(255, 0, 255, 255);
						//stroke(30,0,0);
						point(x , y );
					}
					else if(Maze.getstartPoint(x, y) != 0){
						stroke(255, 255, 0);
						point(x , y );
					}
					else if(Maze.getgoalPoint(x, y) != 0){
						stroke(255, 255, 255);
						point(x , y );
					}
				}
			}
		}
		//様々な障害物があるときの描画
		else if(Maze.getFieldSetting() && !Maze.getCheckPointSetting()){
			for(int y = 0;y < Maze.height;y++)
			{
				for(int x = 0;x < Maze.width;x++){
					if(Maze.getFieldHeightStep(x, y) == 100){
						stroke(255, 0, 255, 255);
						point(x, y);
					}
					else if(Maze.getFieldHeightStep(x, y) > 0){
						int alpha = (int) (255*(double)Maze.getFieldHeightStep(x, y)/(double)Maze.getMaxHeightStep());
						//stroke(30, 0, 0, alpha);
						stroke(alpha, 0, alpha);
						point(x , y );
					}
					else if(Maze.getFieldHeightStep(x, y) < 0){
						int alpha = (int) (255*(double)Maze.getFieldHeightStep(x, y)/(double)Maze.getMinHeightStep());
						//stroke(255, 0, 255, 255-alpha);
						//stroke(125, 0, alpha);
						stroke(255, 0, 255, 255);
						Maze.setFieldHeightStep(x, y, 100);
						point(x , y );
					}
					else if(Maze.getstartPoint(x, y) != 0){
						stroke(255, 255, 0);
						point(x , y );
					}
					else if(Maze.getgoalPoint(x, y) != 0){
						stroke(255, 255, 255);
						point(x, y);
					}
					/*
					else if(Maze.getgoalPoint(x, y) > 0){
						stroke(255, 0, 255);
						point(x , y );
					}
					*/
					/*
					else if(Maze.getgoalPoint(x, y) < 0){
						stroke(80, 100, 255);
						point(x , y );
					}
					*/
				}
			}
		}
		//壁がある時の描画　中間あり
		else if(Maze.getWallSetting() && Maze.getCheckPointSetting()){
			for(int y = 0;y < Maze.height;y++)
			{
				for(int x = 0;x < Maze.width;x++){
					
					if(Maze.getWallPoint(x, y)){
						stroke(255, 0, 255, 255);
						//stroke(30,0,0);
						point(x , y );
					}
					else if(Maze.getstartPoint(x, y) != 0){
						stroke(255, 255, 0);
						point(x , y );
					}
					else if(Maze.getgoalPoint(x, y) != 0){
						stroke(255, 255, 255);
						point(x , y );
					}
					else if(Maze.getCheckPoint(x, y) != 0){
						stroke(0, 255, 255);
						point(x, y);
					}
				}
			}
		}
		//山、中間あり
		else if(Maze.getFieldSetting() && Maze.getCheckPointSetting()){
			for(int y = 0;y < Maze.height;y++)
			{
				for(int x = 0;x < Maze.width;x++){
					if(Maze.getFieldHeightStep(x, y) == 100){
						stroke(30, 0, 0);
						point(x, y);
					}
					else if(Maze.getFieldHeightStep(x, y) > 0){
						int alpha = (int) (255*(double)Maze.getFieldHeightStep(x, y)/(double)Maze.getMaxHeightStep());
						stroke(30, 0, 0, alpha);
						//stroke(alpha, 0, alpha);
						point(x , y );
					}
					else if(Maze.getFieldHeightStep(x, y) < 0){
						int alpha = (int) (255*(double)Maze.getFieldHeightStep(x, y)/(double)Maze.getMinHeightStep());
						//stroke(255, 0, 255, 255-alpha);
						//stroke(125, 0, alpha);
						stroke(255, 0, 255, 255);
						Maze.setFieldHeightStep(x, y, 100);
						point(x , y );
					}
					else if(Maze.getstartPoint(x, y) != 0){
						stroke(100, 0, 0);
						point(x , y );
					}
					else if(Maze.getgoalPoint(x, y) != 0){
						stroke(0, 0, 20);
						point(x, y);
					}
					else if(Maze.getCheckPoint(x, y) != 0){
						stroke(50, 50, 0);
						point(x, y);
					}
				}
			}
		}
		stroke(0, 255, 0);//セルの移動描画

		for(Point p : drawArray){
			point(p.x , p.y );
		}

		if(output == true){
			save(filename);
			output = false;
		}
		if(finish == true)
			stopDraw();
	}

	public void stopDraw(){
		exit();
	}

	public static void saveImg(String filename){
		output = true;
		Simulator2D.filename = filename;
	}
	public static void finish(){
		finish = true;
	}

	public static void setBuffer(ArrayList<Point> points){
		if(update_flag == false){
			drawBuffer.clear();
			for(Point p:points){
				drawBuffer.add(p);
			}
			update_flag = true;
		}
	}

	private void update(){
		if(update_flag){
			drawArray.clear();
			//			for(Point p:drawBuffer){
			//				drawArray.add(p);
			//			}
			for(int i = 0;i < drawBuffer.size();i++){
				drawArray.add(drawBuffer.get(i));
			}
			update_flag = false;
		}
	}




	public static void main(String args[]) {
		//PApplet.main(new String[] { "--location=100,100","maze_with_Lsystem.Simulator2D"});
		Simulator2D testSimulator2d = new Simulator2D();
		testSimulator2d.runSketch(new String[] { "--location=100,100"});
		//PApplet.main(new String[] { "--location=100,100","maze_with_Lsystem.Simulator2D"});
	}
}