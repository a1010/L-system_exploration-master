package maze_with_Lsystem;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import processing.core.PApplet;

import fractal.CalcFractalDimension;
// import lsystem.Lsystem;
import lsystem.MazeLsystem;
import serachItem.MazeNode;

public class Main_Maze extends JFrame implements MouseListener {

	// 描画用
	private static Simulator2D mainApplet = null;
	// private static Simulator2D sim2D = null;

	private static Maze sp;
	private ArrayList<MazeNode> rootNode;
	private MazeLsystem lsys;
	private ArrayDeque<MazeNode> nodes;
	private ArrayDeque<MazeNode> postNodes;
	private ArrayDeque<MazeNode> deleteNodes;
	private ArrayList<Point> drawPointQueue;
	private boolean roop = true;
	public static int max_node_count = 0;
	private int sight;

	/** trueになると終了状態ノード以外を全て強制死滅状態にする */
	private boolean close = false;
	/** trueになるとシミュレーションが終了する */
	private boolean finish = false;

	private boolean outputLogFile;
	private int goal_num;
	public static int close_step;
	private int frame_rate;
	private boolean drawing;
	public static String result_dir;
	private boolean calc_fractalDimension;
	private static double fd; // 画像全体でのフラクタル次元
	private static double[][] fractalSETs; // 画像を分割した各領域でのフラクタル次元
	public static int div_num;
	public static int div_size;
	private static int sim_num;
	private static int sim_count;
	public static double sig_bias;
	private static boolean save_resultIMG;
	public static boolean fd_manual;

	public static int step_num;
	public static ArrayList<String> solution_info;
	public static long ID;
	private ArrayList<Integer> length;

	Image img;
	Toolkit toolkit;
	static String maze_file;
	private static Point startPosition;
	private static Point goalPosition;

	enum Select {
		start, goal, finish
	};

	static Select s;

	public Main_Maze(Maze _map, MazeNode _startNode, int _max_node_count, int _sight, boolean _log, int _goal_num,
			int _close_step, int _frame_rate, boolean _drawing, boolean _calc_fractalDimension, String _result_dir,
			int _pa, int _pb, int _pc, boolean _fd_manual) {

		sp = _map;
		outputLogFile = _log;
		ID = System.currentTimeMillis();
		goal_num = _goal_num;
		close_step = _close_step;
		frame_rate = _frame_rate;
		drawing = _drawing;
		result_dir = _result_dir;
		calc_fractalDimension = _calc_fractalDimension;
		max_node_count = _max_node_count;
		this.sight = _sight;
		fd_manual = _fd_manual;

		lsys = new MazeLsystem(_max_node_count, _sight, _pa, _pb, _pc, _calc_fractalDimension);
		rootNode = new ArrayList<MazeNode>();
		rootNode.add(_startNode);

		roop = true;
		close = false;
		finish = false;
		step_num = 0;
		solution_info = new ArrayList<String>();
		length = new ArrayList<Integer>();

		nodes = new ArrayDeque<MazeNode>();
		postNodes = new ArrayDeque<MazeNode>();
		deleteNodes = new ArrayDeque<MazeNode>();

		drawPointQueue = new ArrayList<Point>();

		Maze.setNode(_startNode.getPoint().x, _startNode.getPoint().y, _startNode);

	}

	public void run() {
		long start_time = System.currentTimeMillis();
		try {
			FileWriter outFile = null;
			BufferedWriter outBuffer = null;
			String data = "";

			if (outputLogFile) {
				outFile = new FileWriter("log_MaxNodes=" + max_node_count + "_sight=" + sight + "_" + Maze.width + "x"
						+ Maze.height + "_" + System.currentTimeMillis() + ".csv");
				outBuffer = new BufferedWriter(outFile);
				outBuffer.write("step" + "," + "node" + "," + "sig" + "," + "state_0" + "," + "state_1" + ","
						+ "state_2" + "," + "state_3" + "," + "state_d" + "," + "tate_D" + "," + "DEAD" + ","
						+ "true_count" + "\n");
				data = max_node_count + "," + sight + "\n";
				// outBuffer.write(data);
			}

			while (roop) {

				// System.out.println(step_num + "ステップ開始");
				for (MazeNode node : rootNode)
					nodes.add(node);
				preStep();
				step();
				postStep();
				deleteStep();
				drawStep2D();
				if (step_num % 1000 == 0) {
					System.out.println(step_num + "ステップ完了");
				}
				step_num++;

				// 打ち切りステップ
				if (step_num == close_step) {
					close = true;
				}
				if (finish == true) {
					roop = false;
				}
				try {
					if (outputLogFile) {
						double sig = sigmoid((double) Maze.getNodeCount() / (double) max_node_count, 4);
						data = step_num + "," + Maze.getNodeCount() + "," + sig + "," + MazeLsystem.debug_apply_0 + ","
								+ MazeLsystem.debug_apply_1 + "," + MazeLsystem.debug_apply_2 + ","
								+ MazeLsystem.debug_apply_3 + "," + MazeLsystem.debug_apply_d + ","
								+ MazeLsystem.debug_apply_D + "," + MazeLsystem.debug_dead_count + ","
								+ (double) Maze.getSearchMAPTrue() + ","
								+ (double) Maze.getNodeCount() / (double) max_node_count + ","
								+ (double) Maze.getSearchMAPTrue() / (double) Maze.getTotalCell() + "\n";
						outBuffer.write(data);
					}
					MazeLsystem.reset_debug_num();
					Thread.sleep(frame_rate);

				} catch (InterruptedException e) {
				}
			}
			if (outputLogFile) {
				outBuffer.flush();
				outBuffer.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (save_resultIMG) {
			System.out.println("描くよ!");
			// if (sim_count == 0 && div_num == 1)
			// mainApplet.runSubWindow(new String[] { "--location=100,100", "SubSketch2D"
			// });
			Simulator2D.draw_searchMAP = false;
			drawStep2D();
			screenShot(result_dir + ID + "_search=" + Simulator2D.draw_searchMAP + ".png");

			Simulator2D.draw_searchMAP = true;
			drawStep2D();
			screenShot(result_dir + ID + "_search=" + Simulator2D.draw_searchMAP + ".png");
			Simulator2D.finish();
		}

		System.out.println("ループ抜け");

		long end_time = System.currentTimeMillis();
		long time = end_time - start_time;
		System.out.println("処理時間：" + time / 1000 + "[s]");

		System.out.println("探索終了");
		System.out.println("step = " + step_num);

		try {
			FileWriter result = new FileWriter(result_dir + "result.csv", true);
			BufferedWriter resultBuffer = new BufferedWriter(result);

			for (String s : solution_info)
				resultBuffer.write(s + "\n");

			resultBuffer.flush();
			resultBuffer.close();
			result.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void screenShot(String fname) {
		// 描画時間のsleep含む
		Simulator2D.saveImg(fname);
		// try {
		// Thread.sleep(1500);
		// Simulator2D.saveImg(fname);
		// Thread.sleep(1500);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
	}

	public void preStep() {
		drawPointQueue.clear();
		if (close) {
			close_process();
		}
		if (solution_info.size() >= goal_num) {
			close_process();
		}
	}

	public void step() {
		while (!nodes.isEmpty()) {
			MazeNode node = nodes.poll();
			// フラクタル次元の適用
			double[] P;
			if (this.calc_fractalDimension) {
				if (div_num == 1) { // 画像全体で計算したフラクタル次元を使う
					P = fractalD2proP(fd, sig_bias);
					lsys.setPa(P[0]);
					lsys.setPb(P[1]);
					lsys.setPc(P[2]);
					// System.out.println("fd:" + fd + ", " + P[0] + ", " + P[1] + ", " + P[2]);
				} else if (div_num > 1) {
					P = fractalD2proP(fractalSETs[node.getPoint().x / div_size][node.getPoint().y / div_size],
							sig_bias);
					lsys.setPa(P[0]);
					lsys.setPb(P[1]);
					lsys.setPc(P[2]);
				}
			} else if (fd_manual) {
				P = fractalD2proP(fd, sig_bias);
				lsys.setPa(P[0]);
				lsys.setPb(P[1]);
				lsys.setPc(P[2]);
			} else {
				lsys.setPa(1);
				lsys.setPb(1);
				lsys.setPc(8);
			}
			lsys.apply(node);
			postNodes.add(node);
			drawPointQueue.add(node.getPoint());
			for (MazeNode child : node.getChildrenByArrayList()) {
				nodes.add(child);
			}
		}
		// System.out.println("node size ="+postNodes.size());
	}

	public void postStep() {
		boolean unspread = true;
		int node_size = postNodes.size();

		if (postNodes.isEmpty())
			roop = false;
		while (!postNodes.isEmpty()) {
			MazeNode node = postNodes.poll();
			// ノードの更新
			node.update();
			if (unspread) {
				// 状態が全てEのとき収束したとみなし、終了
				if (!node.getState().equals("E"))
					unspread = false;
				// rootノード一つだけ残った場合、終了
				if (node_size == 1 && (node.getState().equals("I") || node.getState().equals("D"))) {
					unspread = true;
				}
			}
			// 死滅リスト追加
			if (node.getDeadFlag())
				deleteNodes.add(node);
		}

		// 終了処理
		if (unspread == true) {
			finish = true;
			// rootからleafへの長さを取得
			for (MazeNode node : rootNode)
				nodes.add(node);
			while (!nodes.isEmpty()) {
				MazeNode node = nodes.poll();
				if (node.getChildrenByArrayList().size() == 0)
					length.add(node.getLength());
				for (MazeNode child : node.getChildrenByArrayList()) {
					nodes.add(child);
				}
			}

		}
	}

	public void deleteStep() {
		while (!deleteNodes.isEmpty()) {
			MazeNode node = deleteNodes.poll();
			node.delete();
		}
	}

	public void drawStep2D() {
		Simulator2D.setBuffer(drawPointQueue);
	}

	public void close_process() {
		nodes.clear();
		for (MazeNode node : rootNode)
			nodes.add(node);

		while (!nodes.isEmpty()) {
			MazeNode node = nodes.poll();
			if (!node.getState().equals("E")) {
				node.setStateImmediately("I");
			}
			for (MazeNode child : node.getChildrenByArrayList()) {
				nodes.add(child);
			}
		}
		close = false;
		for (MazeNode node : rootNode)
			nodes.add(node);
	}

	public void DecidePoint(String file_name) {
		File f = new File(file_name);
		BufferedImage read;
		int w;
		int h;
		try {
			read = ImageIO.read(f);
			w = read.getWidth();
			h = read.getHeight();

			setTitle("スタートとゴールを決めよう");
			setSize(w, h);
			Display panel = new Display();
			getContentPane().setLayout(null);
			Container pane = getContentPane();
			pane.add(panel);
			panel.setBounds(0, 0, w, h);
			panel.setBackground(Color.white);
			panel.addMouseListener(this);
			toolkit = Toolkit.getDefaultToolkit();
			img = toolkit.getImage(maze_file);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	class Display extends JPanel {
		public void paint(Graphics g) {
			g.drawImage(img, 0, 0, this);
		}
	}

	// 終了処理
	public void finalization() {
	}

	public static void main(String[] args) {

		// 探索中の描画
		boolean drawing = true;
		// 探索済みセルの描画
		Simulator2D.draw_searchMAP = true;
		// フレームレート
		int frame_rate = 30;
		// 最大ノード数
		int max_node_size = 5000;
		// 視野
		int sight = 5;
		// ログファイルの出力
		boolean logFile = false;
		// ゴールの個数
		int goal_num = 10;
		// 探索打ち切りステップ数
		int close_step = 5000;
		// 迷路ファイル
		String maze_file = "./map6.png";
		// 詳細迷路ファイル
		String maze_file_detail = "./resources/ConvertMap_Max=4_step=1_grade=5_Maze2.csv";
		// 結果保存フォルダ
		String result_dir = "./result/div_theo/";
		// フラクタル次元の計算
		boolean calc_fractalDimension = false;
		// 分岐確率(整数)
		int pa = 5;
		int pb = 1;
		int pc = 1;
		// スタート位置
		int start_point_x = 20;
		int start_point_y = 20;
		// 動的探索のための縦横分割数（div_num<2:分割しない）
		// div_num = 2 : 画像を4分割
		int div_num = 1;
		// シミュレーションの回数
		int sim_num = 1;
		// シグモイドのバイアス(def:1.58)
		double bias = 1.58;
		// フラクタル次元の手動指定
		// -1 で指定しない
		boolean fd_manual = false;

		// コンフィグファイルから設定を読み込む
		String file_name = "./config.txt";
		try {
			File csv_file = new File(file_name);
			FileInputStream fis = new FileInputStream(csv_file);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			String line;
			while ((line = br.readLine()) != null) {
				if (line.charAt(0) == '/')
					continue;
				String[] cols = line.split(":");
				if (cols.length >= 2) {
					String var = cols[0].trim();
					String val = cols[1].trim();
					System.out.printf(var + ":" + val);
					System.out.println();
					switch (var) {
						case "drawing":
							drawing = Boolean.valueOf(val);
							break;
						case "save_resultIMG":
							save_resultIMG = Boolean.valueOf(val);
						case "draw_searchMAP":
							Simulator2D.draw_searchMAP = Boolean.valueOf(val);
							break;
						case "calc_fractalDimension":
							calc_fractalDimension = Boolean.valueOf(val);
							break;
						case "frame_rate":
							frame_rate = Integer.valueOf(val);
							break;
						case "max_node_size":
							max_node_size = Integer.valueOf(val);
							break;
						case "sight":
							sight = Integer.valueOf(val);
							break;
						case "logFile":
							logFile = Boolean.valueOf(val);
							break;
						case "goal_num":
							goal_num = Integer.valueOf(val);
							break;
						case "close_step":
							close_step = Integer.valueOf(val);
							break;
						case "maze_file":
							maze_file = val;
							break;
						case "maze_file_detail":
							maze_file_detail = val;
							break;
						case "img_dir":
							result_dir = val;
							break;
						case "pa":
							pa = Integer.valueOf(val);
							break;
						case "pb":
							pb = Integer.valueOf(val);
							break;
						case "pc":
							pc = Integer.valueOf(val);
							break;
						case "start_point_x":
							start_point_x = Integer.valueOf(val);
							break;
						case "start_point_y":
							start_point_y = Integer.valueOf(val);
							break;
						case "div_num":
							Main_Maze.div_num = Integer.valueOf(val);
						case "sim_num":
							sim_num = Integer.valueOf(val);
						case "sig_bias":
							bias = Double.valueOf(val);
						case "fd_manual":
							fd_manual = Boolean.valueOf(val);
						default:
							break;
					}
				}
			}
			if (drawing == false)
				frame_rate = 0;
			fis.close();
			isr.close();
			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		// ループ
		System.out.println("-----------------------------------------------");
		int numbers[] = { 1, 2, 4, 8 };
		int max_node_size_def = max_node_size;
		String result_fold = result_dir;
		for (int num = 0; num <= 4; num++) {
			// Main_Maze.div_num = num;

			fd = 1.80 + num * 0.05;
			result_dir = result_fold + String.format("%.2f", fd) + "/";

			// max_node変化によるメモリ上限テスト
			// max_node_size = max_node_size_def;
			// while (max_node_size > 0) {

			int sim_count1 = 0;
			sim_count = 0;
			while (sim_count < sim_num) {
				max_node_size = max_node_size_def + sim_count * 1000;
				sim_count1 = 0;
				while (sim_count1 < sim_num) {
					System.out.println("---- シミュレーション " + (sim_count1 * 1 + sim_count * 10 + 1) + " 回目	 ----");
					System.out.println("---- 最大リソース数　 " + max_node_size + " 	 ----");
					Maze.clear();

					// スタート位置、ゴール位置を好きなように決める、壁のみ
					// DecidePoint dp = new DecidePoint(maze_file);

					// dp.setVisible(true);
					// while (!dp.isSetPoint()) {
					// System.out.print("");
					// }
					// Point startPoint = dp.getStartPoint();
					// Point goalPoint = dp.getGoalPoint();
					// Point checkPoint = dp.getCheckPoint();

					// maze
					Point startPoint = new Point(5, 5);
					Point goalPoint = new Point(395, 395);

					// 2160
					// Point startPoint = new Point(60, 60);
					// Point goalPoint = new Point(2025, 1727);
					// Point goalPoint = new Point(2050, 800);

					System.out.println("start:" + startPoint + "," + "goal:" + goalPoint);

					// ただの壁あり
					Maze sp = new Maze(maze_file, startPoint, goalPoint, 6);

					// 画像をdiv_num x div_numに分割してフラクタル次元をそれぞれ求める
					// 同時にフラクタル次元の平均値を計算してシグモイド曲線の変曲点を決める
					double fdSUM = 0.0;
					double fdAVE = 0.0;
					int count_void = 0; // 領域内に図形が含まれない場合をカウント
					// ここまで変曲点導出用変数
					Main_Maze.fractalSETs = new double[Main_Maze.div_num][Main_Maze.div_num];
					if (calc_fractalDimension) {
						if (Main_Maze.div_num > 1) {
							// 画像サイズからdiv_sizeを計算して、各エリアごとのフラクタル次元を計算する
							Main_Maze.div_size = Maze.width / Main_Maze.div_num;
							boolean[][] dArea = new boolean[Main_Maze.div_size][Main_Maze.div_size];
							for (int Ay = 0; Ay < Main_Maze.div_num; Ay++) {
								for (int Ax = 0; Ax < Main_Maze.div_num; Ax++) {
									// wallMAPからdAriaにコピー
									for (int dx = 0; dx < Main_Maze.div_size; dx++) {
										dArea[dx] = Arrays.copyOfRange(Maze.wallMAP[Ax * Main_Maze.div_size + dx],
												Ay * Main_Maze.div_size, (Ay + 1) * Main_Maze.div_size);
									}
									CalcFractalDimension cf = new CalcFractalDimension(dArea,
											result_dir + "fractalDimension");
									cf.run();
									// フラクタル次元が計算できなかった場合、フラクタル次元が1未満あるいは2を超過する場合
									if (Double.isNaN(cf.get_fractalDimension()) || cf.get_fractalDimension() < 1) {
										Main_Maze.fractalSETs[Ax][Ay] = -1.0;
										count_void++;
										System.out.println(count_void);
									} else {
										Main_Maze.fractalSETs[Ax][Ay] = cf.get_fractalDimension();
										fdSUM += Main_Maze.fractalSETs[Ax][Ay];
									}
									System.out.println("(" + Ax + "," + Ay + ") = " + Main_Maze.fractalSETs[Ax][Ay]);
								}
							}
							System.out.println(Double.toString(fractalSETs[0].length));
							fdAVE = fdSUM / (fractalSETs[0].length * fractalSETs[0].length - count_void);
							Main_Maze.sig_bias = fdAVE;
						} else {
							// 画像を分割しないでフラクタル次元を計算する
							CalcFractalDimension cf0 = new CalcFractalDimension(maze_file,
									result_dir + "fractalDimension");
							cf0.run();
							fd = cf0.get_fractalDimension();
							System.out.println("fd: " + fd);
							// シグモイド曲線バイアス用
							Main_Maze.sig_bias = 1.58;
						}
					}
					System.out.printf("fd: %.2f\n", fd);

					// TODO スタート位置はここで決めてる（変えるべき）
					MazeNode node = new MazeNode("0", "2", null, startPoint, 0);
					Main_Maze main = new Main_Maze(sp, node, max_node_size, sight, logFile, goal_num, close_step,
							frame_rate, drawing, calc_fractalDimension, result_dir, pa, pb, pc, fd_manual);
					if (main.drawing == true && mainApplet == null) {
						// mainAppletの用意
						mainApplet = new Simulator2D();
						PApplet.runSketch(new String[] { "--location=100,100", "main" }, mainApplet);
					}
					main.run();
					sim_count1++;
					mainApplet.clear();
				}
				sim_count++;
			}

			// max_node_size -= 1000;
			// }

		}
		System.out.println("おしまい！！");
	}

	// テスト用
	double sigmoid(double x, double gain) {
		return 1.0 / (1.0 + Math.exp(-gain * (x * 2 - 1)));
	}

	// フラクタル次元から確率パラメータの導出
	public static double[] fractalD2proP(double fractalDim, double bias) {
		// 傾き
		double a = 13.5;
		// バイアス
		// double b = 1.58;
		double b = bias;
		// シグモイド関数
		double sig = 1.0 / (1.0 + Math.exp(-a * (fractalDim - b)));
		double pa_rate = -0.7 * sig + 0.8;
		double pb_rate = 0.1;
		double pc_rate = 0.7 * sig + 0.1;

		double[] P = { pa_rate, pb_rate, pc_rate };
		return P;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Point point = e.getPoint();

		switch (s) {
			case start: {
				startPosition = point;
				s = Select.goal;
				System.out.println("ゴール位置をクリックしてください");
				break;
			}
			case goal: {
				goalPosition = point;
				s = Select.finish;
				break;
			}
		}
		System.out.println("start:" + startPosition + "  goal:" + goalPosition);
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}
