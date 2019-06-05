package New;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;

public class makeHeightCSV {
	
	private static String pngFILE;
	private static String CSVFILE;
	
	private static int height;
	
	private static boolean[][] Field; 
	private static int[][] HeightField;

	private static boolean isWall;//trueなら最大高さの位置を壁にする

	public static void init(){
		pngFILE = "meiro.png";
		CSVFILE = "meiro.csv";
		height = 1;
	}
	
	

	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ
		init();

		//画像読み込み
		File f = new File(pngFILE);
		BufferedImage read = null;
		try {
			read = ImageIO.read(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		int w = read.getWidth();
		int h = read.getHeight();
		//Maze.width = w;
		//Maze.height = h;

		//pngの情報を書き込み		
		Field = new boolean[w][h];
		for(int y = 0;y < h;y++){
			for(int x = 0;x < w;x++){

				int c = read.getRGB(x, y);
				int r = r(c);
				int g = g(c);
				int b = b(c);

				if(r==0 && g==0 && b==0)
					Field[x][y] = true;
				else{
					Field[x][y] = false;
				}
			}
		}
		
		//
		HeightField = new int[w][h];
		for(int y = 0; y < h; y++){
			for(int x = 0; x < w; x++){
				if(Field[x][y] && HeightField[x][y] == 0){
					for(int i = x; i < 0; i--){
						
					}
				}
			}
		}

		
		//csv読み込み
		File csv = new File(CSVFILE);
		try {
			FileWriter fw = new FileWriter(csv, false);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw);

			for(int y = 0; y < h; y++){
				String line = "";
				for(int x = 0; x < w; x++){
					line += String.valueOf(Field[x][y]);
					if(x <= w-1)
						line += ",";
				}
				pw.print(line);
				pw.println();
			}
			pw.close();
			System.out.println("書き込み完了");

		} catch (IOException e) {
			// TODO 閾ｪ蜍慕函謌舌＆繧後◆ catch 繝悶Ο繝・け
			e.printStackTrace();
		}

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
