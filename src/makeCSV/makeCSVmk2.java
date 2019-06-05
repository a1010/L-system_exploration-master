package makeCSV;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.imageio.ImageIO;

public class makeCSVmk2 {
	private static String pngFILE;
	private static String CSVFILE;

	private static int[][] Field;
	private static int[][] WallField;

	private static int wall;//越えられない壁
	private static int step;//段差
	private static int grade;//段数ｰ
	private static int mount;//山

	private static boolean isWall;//trueなら最大高さの位置を壁にする

	public static void init(){
		pngFILE = "map4.png";
		CSVFILE = "meiro.csv";
		wall = -1;
		mount = 7;
		step = 1;
		grade = 7;
		isWall = true;
	}

	public static void main(String[] args) {
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

		//山生成		
		Field = new int[w][h];
		WallField = new int[w][h];
		for(int y = 0;y < h;y++){
			for(int x = 0;x < w;x++){

				int c = read.getRGB(x, y);
				int r = r(c);
				int g = g(c);
				int b = b(c);

				if(r==0 && g==0 && b==0)
					WallField[x][y] = wall;
				else if(r==255 && g==0 && b==0){
					Field[x][y] = mount;
				}
				else{
					Field[x][y] = 0;
				}
			}
		}

		//勾配生成
		for(int g = 0; g < grade; g++){
			for(int y = 0; y < h; y++){
				for(int x = 0; x < w; x++){
					if(Field[x][y] != 0){

						//上
						for(int i=1; i<=step; i++){
							//if(y-i < 0)
							//	break;
							if(y-i >= 0 && Field[x][y] > Field[x][y-i])
								Field[x][y-i] = Field[x][y] - step;
						}

						//下
						for(int i=1; i<=step; i++){
							//if(y+i > h)
							//	break;
							if(y+i < h && Field[x][y] > Field[x][y+i])
								Field[x][y+i] = Field[x][y] - step;
						}

						//左
						for(int i=1; i<=step; i++){
							//if(x-i < 0)
							//	break;
							if(x-i >= 0 && Field[x][y] > Field[x-i][y])
								Field[x-i][y] = Field[x][y] - step;
						}

						//右
						for(int i=1; i<=step; i++){
							//if(x+i > w)
							//	break;
							if(x+i < w && Field[x][y] > Field[x+i][y]){
								Field[x+i][y] = Field[x][y] - step;
							}
						}
					}
				}
			}
		}

		//isWall==trueならmaxHeightの位置を壁にする
		if(isWall){
			for(int y = 0; y < h; y++){
				for(int x = 0; x < w; x++){
					if(WallField[x][y] == wall){
						Field[x][y] = wall;
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
