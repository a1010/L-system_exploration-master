package fractal;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;

/**
 * 画像のフラクタル次元を計算する 現状、正方形以外の画像は計算できない
 * 
 * @author pp
 *
 */
public class CalcFractalDimension {

	String f_in_name = "./resources/black.png";
	File f_in = new File(f_in_name);
	String f_out = "./result/fractalDimension";
	// for output .csv pattern because String type list
	ArrayList<String> outputData = new ArrayList<String>();
	int width = 0;
	int height = 0;
	// wall
	int[][] WALL;
	//
	ArrayList<Double> dimension_x = new ArrayList<Double>();
	ArrayList<Double> count_y = new ArrayList<Double>();

	public CalcFractalDimension() {
	}

	/**
	 * フラクタル次元の計算 出力は.csvのみ
	 * 
	 * @param file_in
	 * @param file_out
	 */
	public CalcFractalDimension(String file_in_name, String file_out) {
		this.f_in_name = file_in_name;
		this.f_in = new File(file_in_name);
		// .csvの拡張子とる
		String[] f_outs = file_out.split(".");
		if (f_outs.length > 1)
			f_out = f_outs[0];
		else
			f_out = file_out;
	}

	public void run(int div_num) {
		input();
		calc(div_num);
		output_slope();
		// output_allData();
	}

	/**
	 * 入力画像のクロップと地図情報への変換
	 */
	public void input() {
		try {
			BufferedImage read;
			read = ImageIO.read(f_in);
			width = read.getWidth();
			height = read.getHeight();

			System.out.println("width,height: " + width + "," + height);

			// TODO 特別措置として320,320でクロップしてる。画像全体の次元を求める場合にはクロップ必要ない
			// width = 320;
			// height = 320;

			// TODO 画像を分割するため特定の大きさを要求する720x720でクロップ
			// 約数が多い120の倍数の画像を使用する
			// 120の約数は 1, 2, 3, 4, 5, 6, 8, 10, 12, 15, 20, 24, 30, 40, 60, 120 の16個
			// フラクタル次元を計算するために約数が30個存在する720を使う
			width = 720;
			height = 720;

			// 画像の縦横で短いほうの1桁目を切り捨てた値でクロップしたいとき用
			// if (width < height) {
			// width /= 10;
			// height = width *= 10;
			// } else {
			// height /= 10;
			// width = height *= 10;
			// }

			System.out.println("クロップ後サイズ\nwidth,height: " + width + "," + height);

			WALL = new int[width][height];

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {

					int c = read.getRGB(x, y);
					int r = r(c);
					int g = g(c);
					int b = b(c);
					// 黒以外のマス
					if (r > 0 || g > 0 || b > 0) {
						WALL[x][y] = 1;
					}

				}
			}
			System.out.println("読み込み完了");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void calc() {
		this.calc(0);
	}

	public void calc(int div_num) {
		int length = width;
		boolean find = false;

		// 地図画像を720x720のサイズとした場合の約数
		ArrayList<Integer> box = new ArrayList<Integer>(Arrays.asList(720, 360, 240, 180, 144, 120, 90, 80, 72, 60, 48,
				45, 40, 36, 30, 24, 20, 18, 16, 15, 12, 10, 9, 8, 6, 5, 4, 3, 2, 1));

		// 約数を計算により求める場合は以下を使う
		// ArrayList<Integer> box = new ArrayList<Integer>();
		// // 約数
		// for (int i = 1; i <= length; i++) {
		// double amari = length % i;
		// if (amari == 0) {
		// box.add(i);
		// }
		// }

		// 拡張for文
		// size:box size
		for (int size : box) {
			int count = 0;
			int sho = length / size;
			// boxのyループ
			for (int Ly = 0; Ly < sho; Ly++) {
				// boxのxループ
				for (int Lx = 0; Lx < sho; Lx++) {
					// box内のyループ
					for (int y = Ly * size; y < (Ly + 1) * size; y++) {
						// box内のxループ
						for (int x = Lx * size; x < (Lx + 1) * size; x++) {

							if (WALL[x][y] > 0) {
								find = true;
								break;
							}

						}
						if (find == true)
							break;
					}
					// box内に目標があれば
					if (find == true) {
						count++;
						find = false;
					}
				}
			}
			// リストにデータ追加
			double log_size = Math.log(size);
			double log_count = Math.log(count);
			dimension_x.add(log_size);
			count_y.add(log_count);
			outputData.add(log_size + "," + log_count);
		}
		System.out.println("計算完了");
	}

	// log_size,log_countの全データを.csv形式で出力
	public void output_allData() {
		try {
			FileWriter outFile = new FileWriter(f_out + "AllData.csv");
			BufferedWriter outBuffer = new BufferedWriter(outFile);

			for (int i = 0; i < outputData.size(); i++) {
				String s = outputData.get(i);
				outBuffer.write(s + "\n");
			}

			outBuffer.flush();
			outBuffer.close();
			System.out.println("書き出し完了");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// フラクタル次元の計算(最小二乗法でフィッティング)
	public double calc_slope() {
		int i;

		double a = 0, b = 0;
		double sum_xy = 0, sum_x = 0, sum_y = 0, sum_x2 = 0;

		int data_size = dimension_x.size();

		for (i = 0; i < data_size; i++) {
			sum_xy += dimension_x.get(i) * count_y.get(i);
			sum_x += dimension_x.get(i);
			sum_y += count_y.get(i);
			sum_x2 += Math.pow(dimension_x.get(i), 2);
		}

		a = (data_size * sum_xy - sum_x * sum_y) / (data_size * sum_x2 - Math.pow(sum_x, 2));
		b = (sum_x2 * sum_y - sum_xy * sum_x) / (data_size * sum_x2 - Math.pow(sum_x, 2));

		System.out.println("fractalDimension: " + (-a));

		return a;
	}

	// フラクタル次元を.csv形式で出力
	public void output_slope() {
		try {
			FileWriter outFile = new FileWriter(f_out + "Slope.csv", true);
			BufferedWriter outBuffer = new BufferedWriter(outFile);

			// // ファイルパスからファイル名を抽出
			// String[] f_path = this.f_in_name.split("/");
			// String f_name = f_path[f_path.length-1];

			// フラクタル次元とともに画像ファイルパスを記録
			outBuffer.write(calc_slope() * (-1) + "," + f_in_name + "\n");
			outBuffer.write("\n");
			outBuffer.flush();
			outBuffer.close();
			System.out.println("書き出し完了");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		int div_num = 0;
		// CalcFractalDimension cDimension = new CalcFractalDimension();
		CalcFractalDimension cDimension = new CalcFractalDimension("C:/LAB/FLSpy/images/Bangkok, Thailand_2.png",
				"./result/fractalDimension");
		cDimension.run(div_num);
	}

	// 色の補正
	public static int a(int c) {
		return c >>> 24;
	}

	public static int r(int c) {
		return c >> 16 & 0xff;
	}

	public static int g(int c) {
		return c >> 8 & 0xff;
	}

	public static int b(int c) {
		return c & 0xff;
	}

	public static int rgb(int r, int g, int b) {
		return 0xff000000 | r << 16 | g << 8 | b;
	}

	public static int argb(int a, int r, int g, int b) {
		return a << 24 | r << 16 | g << 8 | b;
	}
}
