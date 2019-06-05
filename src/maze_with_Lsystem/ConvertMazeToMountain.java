package maze_with_Lsystem;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/**
 * 2値の画像ファイルを迷図CSVファイルに変換
 * CSVファイルにはそれぞれの画素の高低差を記述
 *
 */
public class ConvertMazeToMountain {
	//山の頂点の高さ
	int Max_height = 100;
	//一段ごとの高さ
	int step = 100;
	//一段ごとの幅
	int breadth = 0;
	//File f_in = new File("./resources/Maze2_2.png");
	File f_in = new File("./resources/monochrome2_map.bmp");
	//String f_out = "./resources/ConvertMap_Max="+Max_height+"_step="+step+"_grade="+breadth+"_Maze2"+".csv";
	String f_out = "./resources/monochrome2.csv";
	
	int width = 0;
	int height = 0;
	int[][] WALL;

	public void run(){
		input();
		convert();
		output();
	}

	public void input(){
		try {
			BufferedImage read;
			read = ImageIO.read(f_in);
			width = read.getWidth();
			height = read.getHeight();


			WALL = new int[width][height];


			for(int y = 0;y < height;y++){
				for(int x = 0;x < width;x++){

					int c = read.getRGB(x, y);
					int r = r(c);
					int g = g(c);
					int b = b(c);

					//黒のマス
					if(r==0 && g==0 && b==0){
						WALL[x][y] = Max_height;
					}
				}
			}
			System.out.println("読み込み完了");
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} 
	}

	public void convert(){
		int current_height = Max_height;

		while(current_height != 0){
			for(int y = 0;y < height;y++){
				for(int x = 0;x < width;x++){
					
					int[] left = new int[breadth];
					int[] right = new int[breadth];
					int[] up = new int[breadth];
					int[] down = new int[breadth];
					ArrayList<Point> p = new ArrayList<Point>();
					for(int i = 0;i < breadth;i++){
						left[i] = x - 1 - i;
						right[i] = x + 1 + i;
						up[i] = y - 1 -i;
						down[i] = y + 1 + i;
						if(left[i] < 0) left[i] = 0;
						if(right[i] >= width) right[i] = width - 1;
						if(up[i] < 0) up[i] = 0;
						if(down[i] >= height) down[i] = height - 1;
						p.add(new Point(left[i],y));
						p.add(new Point(right[i],y));
						p.add(new Point(x,up[i]));
						p.add(new Point(x,down[i]));
					}

					if(WALL[x][y] == current_height){
						for(int i = 0;i < p.size();i++){
							if(WALL[p.get(i).x][p.get(i).y] == 0){
								WALL[p.get(i).x][p.get(i).y]  = current_height - step;
							}
						}
					}

				}
			}
			current_height-=step;
		}
		System.out.println("コンバート");
	}

	public void output(){
		try {
			FileWriter outFile = new FileWriter(f_out);
			BufferedWriter outBuffer = new BufferedWriter(outFile);

			for(int y = 0;y < height;y++){
				StringBuffer buf = new StringBuffer();
				for(int x = 0;x < width;x++){
					String data = Integer.toString(WALL[x][y]);
					buf.append(data);
					buf.append(",");
				}
				buf.append("\n");
				String s = buf.toString();
				outBuffer.write(s);
			}



			outBuffer.flush();
			outBuffer.close();
			System.out.println("書き出し完了");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		ConvertMazeToMountain  cmtm = new ConvertMazeToMountain();
		cmtm.run();
	}
	public static int a(int c){
		return c>>>24;
	}
	public static int r(int c){
		return c>>16&0xff;
	}
	public static int g(int c){
		return c>>8&0xff;
	}
	public static int b(int c){
		return c&0xff;
	}
	public static int rgb(int r,int g,int b){
		return 0xff000000 | r <<16 | g <<8 | b;
	}
	public static int argb(int a,int r,int g,int b){
		return a<<24 | r <<16 | g <<8 | b;
	}

}
