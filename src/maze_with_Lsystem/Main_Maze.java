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

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import processing.core.PApplet;

import New.DecidePoint;
import fractal.CalcFractalDimension;
import lsystem.Lsystem;
import lsystem.MazeLsystem;
import serachItem.MazeNode;

public class Main_Maze extends JFrame implements MouseListener {

	private ArrayList<MazeNode> rootNode;
	private Lsystem lsys;
	private ArrayDeque<MazeNode> nodes;
	private ArrayDeque<MazeNode> postNodes;
	private ArrayDeque<MazeNode> deleteNodes;
	private ArrayList<Point> drawPointQueue;
	private boolean roop = true;
	private int max_node_count;
	private int sight;

	/** trueになると終了状態ノード以外を全て強制死滅状態にする */
	private boolean close = false;
	/** trueになるとシミュレーションが終了する */
	private boolean finish = false;

	private boolean outputLogFile;
	private int goal_num;
	private int close_step;
	private int frame_rate;
	private boolean drawing;
	private boolean calc_fractalDimension;
	private String result_dir;

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

	public Main_Maze(MazeNode startNode, int max_node_count, int sight, boolean log, int goal_num, int close_step,
			int frame_rate, boolean drawing, boolean calc_fractalDimension, String result_dir, int pa, int pb, int pc) {
		this.max_node_count = max_node_count;
		this.sight = sight;
		lsys = new MazeLsystem(max_node_count, sight, pa, pb, pc);
		rootNode = new ArrayList<MazeNode>();
		rootNode.add(startNode);

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

		Maze.setNode(startNode.getPoint().x, startNode.getPoint().y, startNode);

		outputLogFile = log;
		ID = System.currentTimeMillis();
		this.goal_num = goal_num;
		this.close_step = close_step;
		this.frame_rate = frame_rate;
		this.drawing = drawing;
		this.result_dir = result_dir;
		this.calc_fractalDimension = calc_fractalDimension;
	}

	public void run() {
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

				// System.out.println(step_num+"ステップ開始");
				for (MazeNode node : rootNode)
					nodes.add(node);
				preStep();
				step();
				postStep();
				deleteStep();
				drawStep2D();
				// System.out.println(step_num+"ステップ完了");
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
		System.out.println("ループ抜け");

		if (drawing == false) {
			try {
				PApplet.main(new String[] { "--location=100,100", "maze_with_Lsystem.Simulator2D" });
				Simulator2D.draw_searchMAP = false;
				drawStep2D();
				Thread.sleep(300);
				Simulator2D.saveImg(result_dir + ID + "_search=" + Simulator2D.draw_searchMAP + ".png");
				Thread.sleep(1500);

				Simulator2D.draw_searchMAP = true;
				drawStep2D();
				Thread.sleep(700);
				Simulator2D.saveImg(result_dir + ID + "_search=" + Simulator2D.draw_searchMAP + ".png");
				Thread.sleep(1500);
				Simulator2D.finish = true;

				// フラクタル次元の計算
				if (calc_fractalDimension == true) {
					CalcFractalDimension cf = new CalcFractalDimension(
							result_dir + ID + "_search=" + Simulator2D.draw_searchMAP + ".png",
							result_dir + "fractalDimension");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

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
		String result_dir = "./result/";
		// フラクタル次元の計算
		boolean calc_fractalDimension = false;
		// 分岐確率(整数)
		int pa = 5;
		int pb = 1;
		int pc = 1;
		// スタート位置
		int start_point_x = 20;
		int start_point_y = 20;

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

		// スタート位置、ゴール位置を好きなように決める、壁のみ
		DecidePoint dp = new DecidePoint(maze_file);
		// 中間地点を設定する場合
		// DecidePoint dp = new DecidePoint(maze_file, 1);

		dp.setVisible(true);
		while (!dp.isSetPoint()) {
			System.out.print("");
		}
		Point startPoint = dp.getStartPoint();
		Point goalPoint = dp.getGoalPoint();
		// Point checkPoint = dp.getCheckPoint();

		// ただの壁あり
		Maze sp = new Maze(maze_file, startPoint, goalPoint, 6);
		// 中間地点あり
		// Maze sp = new Maze(maze_file, startPoint, goalPoint, checkPoint, 6);
		// 山あり
		// Maze sp = new Maze(maze_file, maze_file_detail, startPoint, goalPoint, 6);
		// 山あり 中間あり
		// Maze sp = new Maze(maze_file, maze_file_detail, startPoint, goalPoint,
		// checkPoint, 6);
		System.out.println("start:" + startPoint + "," + "goal" + goalPoint);

		// 何もない空間
		// Maze sp = new Maze(400, 400, new Point(390,390));
		// 壁だけ
		// Maze sp = new Maze(maze_file);
		// Maze sp = new Maze(maze_file, startPoint, goalPoint, 6);
		// 障害物いろいろ
		// Maze sp = new Maze(maze_file,maze_file_detail);
		// TODO スタート位置はここで決めてる（変えるべき）
		MazeNode node = new MazeNode("0", "2", null, startPoint, 0);
		// MazeNode node = new MazeNode("0", "2", null, new
		// Point(start_point_x,start_point_y),0);
		// MazeNode node = new MazeNode("0", "2", null, new Point(378,134),0);
		// MazeNode node = new MazeNode("0", "2", null, new Point(10,200),0);
		// MazeNode node = new MazeNode("0", "2", null, startPoint, goalPoint., 0);
		Main_Maze main = new Main_Maze(node, max_node_size, sight, logFile, goal_num, close_step, frame_rate, drawing,
				calc_fractalDimension, result_dir, pa, pb, pc);
		// Simulator2D simulator2d = new Simulator2D();
		if (drawing == true)
			PApplet.main(new String[] { "--location=100,100", "maze_with_Lsystem.Simulator2D" });
		main.run();

	}

	// テスト用
	double sigmoid(double x, double gain) {
		return 1.0 / (1.0 + Math.exp(-gain * (x * 2 - 1)));
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
