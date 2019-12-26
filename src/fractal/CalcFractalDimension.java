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

	String f_in_name = "";
	File f_in;
	String f_out = "./result/fractalDimension";
	// for output .csv pattern because String type list
	ArrayList<String> outputData = new ArrayList<String>();
	int width = 0;
	int height = 0;
	// MAP
	boolean[][] wallMAP;
	// fractal
	double fractalDimension = -1.0;
	//
	ArrayList<Double> dimension_x;
	ArrayList<Double> count_y;
	ArrayList<Integer> box;

	/**
	 * フラクタル次元の計算 計算対象が配列
	 * 
	 * @param wallMAP  n*n square matrix
	 * @param file_out
	 */
	public CalcFractalDimension(boolean[][] wallMAP, String file_out) {
		this.wallMAP = wallMAP;
		// .csvの拡張子とる
		String[] f_outs = file_out.split(".");
		if (f_outs.length > 1)
			f_out = f_outs[0];
		else
			f_out = file_out;
		width = height = wallMAP.length;
	}

	/**
	 * フラクタル次元の計算 計算対象が画像ファイル 出力は.csvのみ
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
		inputIMG();
	}

	public void run() {
		calc();
		calc_slope();
		// output_slope();
	}

	/**
	 * 入力画像のクロップと地図情報への変換
	 */
	public void inputIMG() {
		try {
			BufferedImage read;
			read = ImageIO.read(f_in);
			width = read.getWidth();
			height = read.getHeight();

			wallMAP = new boolean[width][height];

			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {

					int c = read.getRGB(x, y);
					int r = r(c);
					int g = g(c);
					int b = b(c);
					// Non-black pixels
					if (r == 0 && g == 0 && b == 0)
						wallMAP[x][y] = true;
					else
						wallMAP[x][y] = false;
				}
			}
			System.out.println("読み込み完了");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void calc() {
		// init
		dimension_x = new ArrayList<Double>();
		count_y = new ArrayList<Double>();
		box = new ArrayList<Integer>();

		int length = width;
		boolean find = false;

		// 約数を計算により求める場合は以下を使う
		for (int i = 1; i <= length; i++) {
			double amari = length % i;
			if (amari == 0) {
				box.add(i);
			}
		}

		// BOXcounting-method
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
							// box内に壁があったらboxから抜ける
							if (wallMAP[x][y] == true) {
								find = true;
								break;
							}
						}
						// box内に壁があったらboxから抜ける
						if (find == true)
							break;
					}
					// box内に壁があればカウントアップ
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
	}

	// フラクタル次元の計算(最小二乗法でフィッティング)
	public void calc_slope() {
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

		this.fractalDimension = -a;
	}

	// フラクタル次元を.csv形式で出力
	public void output_slope() {
		try {
			FileWriter outFile = new FileWriter(f_out + "Slope.csv", true);
			BufferedWriter outBuffer = new BufferedWriter(outFile);

			// ファイルパスからファイル名だけを抽出する方法
			// String[] f_path = this.f_in_name.split("/");
			// String f_name = f_path[f_path.length-1];

			// フラクタル次元とともに画像ファイルパスを記録
			if (this.f_in_name == "") {
				// 入力が画像ファイルじゃない場合
				outBuffer.write(this.fractalDimension + "\n");
			} else {
				outBuffer.write(this.fractalDimension + "," + f_in_name + "\n");
			}
			outBuffer.flush();
			outBuffer.close();
			outFile.close();
			// System.out.println("書き出し完了");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public double get_fractalDimension() {
		return fractalDimension;
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
			outFile.close();
			System.out.println("書き出し完了");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		CalcFractalDimension cDimension = new CalcFractalDimension("C:/LAB/FLSpy/images/Bangkok, Thailand_2.png",
				"./result/fractalDimension");
		cDimension.run();
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
