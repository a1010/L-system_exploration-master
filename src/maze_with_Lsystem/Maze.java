package maze_with_Lsystem;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import serachItem.MazeNode;

public class Maze {
	public static int width;
	public static int height;
	public static ArrayList<Point> goal = new ArrayList<Point>();
	public static boolean finishSetUp;

	private static MazeNode[][] MAP;
	private static boolean[][] searchMAP;
	public static boolean[][] wallMAP;
	private static boolean[][] wallMAP2;

	private static byte[][] startPoint;
	private static byte[][] goalPoint;
	private static byte[][] checkPoint;
	private static int[][] Field;

	private static boolean wall_setting;
	private static boolean field_setting;
	private static boolean checkPoint_setting;

	private static int searchCount;
	private static int node_count;
	private static int wall_count;
	private static int max_height_step;
	private static int min_height_step;
	private static int max_node_count;

	private static Point point_start;
	private static Point point_goal;
	private static Point point_check;

	// 中間地点を通過したかどうか
	private static boolean isCheck;

	public Maze(int width, int height, Point goal) {
		init();

		Maze.width = width;
		Maze.height = height;
		Maze.goal.add(goal);
		MAP = new MazeNode[width][height];
		searchMAP = new boolean[width][height];
		Maze.finishSetUp = true;
	}

	/**
	 * 迷路中に乗り越えられない壁がある
	 * 
	 * @param filename
	 */
	public Maze(String filename) {
		init();

		wall_setting = true;
		try {
			File f = new File(filename);
			BufferedImage read;
			read = ImageIO.read(f);
			int w = read.getWidth();
			int h = read.getHeight();
			Maze.width = w;
			Maze.height = h;

			MAP = new MazeNode[width][height];
			searchMAP = new boolean[width][height];
			wallMAP = new boolean[width][height];
			startPoint = new byte[width][height];
			goalPoint = new byte[width][height];
			checkPoint = new byte[width][height];

			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {

					int c = read.getRGB(x, y);
					// int r = 255-r(c);
					// int g = 255-g(c);
					// int b = 255-b(c);
					int r = r(c);
					int g = g(c);
					int b = b(c);

					if (r == 0 && g == 0 && b == 0) {
						wallMAP[x][y] = true;
						wall_count++;
					} else if (r >= 255 && g <= 1 && b <= 1) {
						wallMAP[x][y] = false;
						startPoint[x][y] = 1;
					} else if (r <= 1 && g <= 1 && b >= 255) {
						wallMAP[x][y] = false;
						goalPoint[x][y] = 1;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Maze.finishSetUp = true;
	}

	/***
	 * 迷路中の障害物が山になっている（乗り越え可能）
	 * 
	 * @param img_filename
	 * @param csv_filename
	 */
	public Maze(String img_filename, String csv_filename) {
		init();

		field_setting = true;
		// wall_settingはfalseで
		// wall_setting = true;
		try {

			// 画像読み込み
			File f = new File(img_filename);
			BufferedImage read;
			read = ImageIO.read(f);
			int w = read.getWidth();
			int h = read.getHeight();
			Maze.width = w;
			Maze.height = h;

			MAP = new MazeNode[width][height];
			Field = new int[width][height];
			searchMAP = new boolean[width][height];
			wallMAP = new boolean[width][height];
			startPoint = new byte[width][height];
			goalPoint = new byte[width][height];

			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {

					int c = read.getRGB(x, y);
					int r = r(c);
					int g = g(c);
					int b = b(c);

					if (r == 0 && g == 0 && b == 0) {
						wallMAP[x][y] = true;
						wall_count++;
					} else if (r >= 255 && g <= 1 && b <= 1) {
						startPoint[x][y] = 1;
					} else if (r <= 1 && g <= 1 && b >= 255) {
						goalPoint[x][y] = 1;
					} else if (r <= 1 && g <= 255 && b <= 1) {
						checkPoint[x][y] = 1;
					}
				}
			}

			// csv読み込み
			File csv_file = new File(csv_filename);
			FileInputStream fis = new FileInputStream(csv_file);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			String line;
			int col_count = 0;
			int row_count = 0;
			while ((line = br.readLine()) != null) {
				String[] cols = line.split(",");
				for (String s : cols) {
					Field[col_count][row_count] = Integer.parseInt(s);
					// 最大高さの更新
					if (max_height_step < Field[col_count][row_count])
						max_height_step = Field[col_count][row_count];
					// 最小高さの更新
					if (min_height_step > Field[col_count][row_count])
						min_height_step = Field[col_count][row_count];
					col_count++;
					// row_count++;
				}
				col_count = 0;
				row_count++;
				// row_count = 0;
				// col_count++;
			}
			fis.close();
			isr.close();
			br.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		Maze.finishSetUp = true;
	}

	/**
	 * 迷路中に乗り越えられない壁がある スタート、ゴールを自分で設定する
	 * 
	 * @param filename starPoint goalPoint radius
	 */
	public Maze(String filename, Point start, Point goal, int radius) {
		init();

		wall_setting = true;
		try {
			File f = new File(filename);
			BufferedImage read;
			read = ImageIO.read(f);
			int w = read.getWidth();
			int h = read.getHeight();

			// フラクタル次元を計算する都合、動的探索のために分割する都合で720にクロップ
			// Maze.width = 2160;
			// Maze.height = 2160;

			Maze.width = w;
			Maze.height = h;

			MAP = new MazeNode[width][height];
			searchMAP = new boolean[width][height];
			wallMAP = new boolean[width][height];
			wallMAP2 = new boolean[width][height];
			startPoint = new byte[width][height];
			goalPoint = new byte[width][height];

			// MazeNode[][] MAP_t = new MazeNode[width * 3][height * 3];
			// boolean[][] searchMAP_t = new boolean[width * 3][height * 3];
			// boolean[][] wallMAP_t = new boolean[width * 3][height * 3];
			byte[][] startPoint_t = new byte[width * 3][height * 3];
			byte[][] goalPoint_t = new byte[width * 3][height * 3];

			startPoint[start.x][start.y] = 1;
			goalPoint[goal.x][goal.y] = 1;

			startPoint_t[start.x * 3][start.y * 3] = 1;
			goalPoint_t[goal.x * 3][goal.y * 3] = 1;

			point_start = start;
			point_goal = goal;

			/* スタート地点と似ている色を道とする */
			int sc = read.getRGB(start.x, start.y);
			int sr = r(sc);
			int sg = g(sc);
			int sb = b(sc);
			// System.out.printf(sr+":"+sg+":"+sb);

			for (int y = 0; y < h; y++) {
				// boolean way = false;
				for (int x = 0; x < w; x++) {

					int c = read.getRGB(x, y);
					// int r = 255-r(c);
					// int g = 255-g(c);
					// int b = 255-b(c);
					int r = r(c);
					int g = g(c);
					int b = b(c);

					if (sr == r && sg == g && sb == b) {
						wallMAP2[x][y] = true;
					}

					// else if(r>=255 && g<=1 && b<=1){0
					// startPoint[x][y] = 1;
					// }
					// else if(r<=1 && g<=1 && b>=255){
					// goalPoint[x][y] = 1;
					// }

					if ((start.x - radius) <= x && x <= (start.x + radius) && (start.y - radius) <= y
							&& y <= (start.y + radius)) {
						startPoint[x][y] = 1;
					}
					if ((goal.x - radius) <= x && x <= (goal.x + radius) && (goal.y - radius) <= y
							&& y <= (goal.y + radius)) {
						goalPoint[x][y] = 1;
					}
				}
			}

			for (int y = 1; y < h - 1; y++) {
				for (int x = 1; x < w - 1; x++) {
					if (wallMAP2[x][y] == true) {
						for (int i = -1; i < 2; i++) {
							for (int j = -1; j < 2; j++) {
								if (wallMAP2[x + j][y + i] == false) {
									wallMAP[x + j][y + i] = true;
									wall_count++;
								}
							}
						}
					}
				}
			}
			for (int y = 0; y < w; y++) {
				if (wallMAP[y][0] == false) {
					wallMAP[y][0] = true;
					wall_count++;
				}
				if (wallMAP[y][h - 1] == false) {
					wallMAP[y][h - 1] = true;
					wall_count++;
				}
			}

			for (int y = 0; y < h; y++) {
				if (wallMAP[0][y] == false) {
					wallMAP[0][y] = true;
					wall_count++;
				}
				if (wallMAP[w - 1][y] == false) {
					wallMAP[w - 1][y] = true;
					wall_count++;
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		Maze.finishSetUp = true;
	}

	/**
	 * 迷路中に乗り越えられない壁がある スタート、ゴールを自分で設定する、山設定
	 * 
	 * @param filename starPoint goalPoint radius
	 */
	public Maze(String filename, String csv_filename, Point start, Point goal, int radius) {
		init();

		// wall_setting = true;
		field_setting = true;
		try {
			File f = new File(filename);
			BufferedImage read;
			read = ImageIO.read(f);
			int w = read.getWidth();
			int h = read.getHeight();
			Maze.width = w;
			Maze.height = h;

			MAP = new MazeNode[width][height];
			Field = new int[width][height];
			searchMAP = new boolean[width][height];
			wallMAP = new boolean[width][height];
			startPoint = new byte[width][height];
			goalPoint = new byte[width][height];

			// MazeNode[][] MAP_t = new MazeNode[width * 3][height * 3];
			// boolean[][] searchMAP_t = new boolean[width * 3][height * 3];
			// boolean[][] wallMAP_t = new boolean[width * 3][height * 3];
			byte[][] startPoint_t = new byte[width * 3][height * 3];
			byte[][] goalPoint_t = new byte[width * 3][height * 3];

			startPoint[start.x][start.y] = 1;
			goalPoint[goal.x][goal.y] = 1;

			startPoint_t[start.x * 3][start.y * 3] = 1;
			goalPoint_t[goal.x * 3][goal.y * 3] = 1;

			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {

					int c = read.getRGB(x, y);
					// int r = 255-r(c);
					// int g = 255-g(c);
					// int b = 255-b(c);
					int r = r(c);
					int g = g(c);
					int b = b(c);

					if (r == 0 && g == 0 && b == 0) {
						wallMAP[x][y] = true;
						wall_count++;
					}
					// else if(r>=255 && g<=1 && b<=1){
					// startPoint[x][y] = 1;
					// }
					// else if(r<=1 && g<=1 && b>=255){
					// goalPoint[x][y] = 1;
					// }

					if ((start.x - radius) <= x && x <= (start.x + radius) && (start.y - radius) <= y
							&& y <= (start.y + radius)) {
						startPoint[x][y] = 1;
					}
					if ((goal.x - radius) <= x && x <= (goal.x + radius) && (goal.y - radius) <= y
							&& y <= (goal.y + radius)) {
						goalPoint[x][y] = 1;
					}
				}
			}
			// csv読み込み
			File csv_file = new File(csv_filename);
			FileInputStream fis = new FileInputStream(csv_file);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			String line;
			int col_count = 0;
			int row_count = 0;
			while ((line = br.readLine()) != null) {
				String[] cols = line.split(",");
				for (String s : cols) {
					// System.out.println(Field[col_count][row_count]);
					Field[col_count][row_count] = Integer.parseInt(s);
					// 最大高さの更新
					if (max_height_step < Field[col_count][row_count])
						max_height_step = Field[col_count][row_count];
					// 最小高さの更新
					if (min_height_step > Field[col_count][row_count])
						min_height_step = Field[col_count][row_count];
					col_count++;
					// row_count++;
				}
				col_count = 0;
				row_count++;
				// row_count = 0;
				// col_count++;
			}
			fis.close();
			isr.close();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Maze.finishSetUp = true;
	}

	/**
	 * 迷路中に乗り越えられない壁がある スタート、ゴール、チェックポイントを設定する
	 * 
	 * @param filename starPoint goalPoint check radius
	 */
	public Maze(String filename, Point start, Point goal, Point check, int radius) {
		init();

		wall_setting = true;
		checkPoint_setting = true;
		try {
			File f = new File(filename);
			BufferedImage read;
			read = ImageIO.read(f);
			int w = read.getWidth();
			int h = read.getHeight();
			Maze.width = w;
			Maze.height = h;

			MAP = new MazeNode[width][height];
			searchMAP = new boolean[width][height];
			wallMAP = new boolean[width][height];
			startPoint = new byte[width][height];
			goalPoint = new byte[width][height];
			checkPoint = new byte[width][height];

			// MazeNode[][] MAP_t = new MazeNode[width * 3][height * 3];
			// boolean[][] searchMAP_t = new boolean[width * 3][height * 3];
			// boolean[][] wallMAP_t = new boolean[width * 3][height * 3];
			byte[][] startPoint_t = new byte[width * 3][height * 3];
			byte[][] goalPoint_t = new byte[width * 3][height * 3];
			byte[][] checkPoint_t = new byte[width * 3][height * 3];

			startPoint[start.x][start.y] = 1;
			goalPoint[goal.x][goal.y] = 1;
			checkPoint[check.x][check.y] = 1;

			startPoint_t[start.x * 3][start.y * 3] = 1;
			goalPoint_t[goal.x * 3][goal.y * 3] = 1;
			checkPoint_t[check.x * 3][check.y * 3] = 1;

			point_start = start;
			point_goal = goal;
			point_check = check;

			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {

					int c = read.getRGB(x, y);
					// int r = 255-r(c);
					// int g = 255-g(c);
					// int b = 255-b(c);
					int r = r(c);
					int g = g(c);
					int b = b(c);

					if (r == 0 && g == 0 && b == 0) {
						wallMAP[x][y] = true;
						wall_count++;
					}
					// else if(r>=255 && g<=1 && b<=1){
					// startPoint[x][y] = 1;
					// }
					// else if(r<=1 && g<=1 && b>=255){
					// goalPoint[x][y] = 1;
					// }

					if ((start.x - radius) <= x && x <= (start.x + radius) && (start.y - radius) <= y
							&& y <= (start.y + radius)) {
						startPoint[x][y] = 1;
					}
					if ((goal.x - radius) <= x && x <= (goal.x + radius) && (goal.y - radius) <= y
							&& y <= (goal.y + radius)) {
						goalPoint[x][y] = 1;
					}
					if ((check.x - radius) <= x && x <= (check.x + radius) && (check.y - radius) <= y
							&& y <= (check.y + radius)) {
						checkPoint[x][y] = 1;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Maze.finishSetUp = true;
	}

	/**
	 * 迷路中に乗り越えられない壁がある スタート、ゴール、チェックポイントを設定する 山設定
	 * 
	 * @param filename starPoint goalPoint check radius
	 */
	public Maze(String filename, String csv_filename, Point start, Point goal, Point check, int radius) {
		init();

		// wall_setting = true;
		field_setting = true;
		checkPoint_setting = true;
		try {
			File f = new File(filename);
			BufferedImage read;
			read = ImageIO.read(f);
			int w = read.getWidth();
			int h = read.getHeight();
			Maze.width = w;
			Maze.height = h;

			MAP = new MazeNode[width][height];
			Field = new int[width][height];
			searchMAP = new boolean[width][height];
			wallMAP = new boolean[width][height];
			startPoint = new byte[width][height];
			goalPoint = new byte[width][height];
			checkPoint = new byte[width][height];

			// MazeNode[][] MAP_t = new MazeNode[width * 3][height * 3];
			// boolean[][] searchMAP_t = new boolean[width * 3][height * 3];
			// boolean[][] wallMAP_t = new boolean[width * 3][height * 3];
			byte[][] startPoint_t = new byte[width * 3][height * 3];
			byte[][] goalPoint_t = new byte[width * 3][height * 3];
			byte[][] checkPoint_t = new byte[width * 3][height * 3];

			startPoint[start.x][start.y] = 1;
			goalPoint[goal.x][goal.y] = 1;
			checkPoint[check.x][check.y] = 1;

			startPoint_t[start.x * 3][start.y * 3] = 1;
			goalPoint_t[goal.x * 3][goal.y * 3] = 1;
			checkPoint_t[check.x * 3][check.y * 3] = 1;

			point_start = start;
			point_goal = goal;
			point_check = check;

			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {

					int c = read.getRGB(x, y);
					// int r = 255-r(c);
					// int g = 255-g(c);
					// int b = 255-b(c);
					int r = r(c);
					int g = g(c);
					int b = b(c);

					if (r == 0 && g == 0 && b == 0) {
						wallMAP[x][y] = true;
						wall_count++;
					}
					// else if(r>=255 && g<=1 && b<=1){
					// startPoint[x][y] = 1;
					// }
					// else if(r<=1 && g<=1 && b>=255){
					// goalPoint[x][y] = 1;
					// }

					if ((start.x - radius) <= x && x <= (start.x + radius) && (start.y - radius) <= y
							&& y <= (start.y + radius)) {
						startPoint[x][y] = 1;
					}
					if ((goal.x - radius) <= x && x <= (goal.x + radius) && (goal.y - radius) <= y
							&& y <= (goal.y + radius)) {
						goalPoint[x][y] = 1;
					}
					if ((check.x - radius) <= x && x <= (check.x + radius) && (check.y - radius) <= y
							&& y <= (check.y + radius)) {
						checkPoint[x][y] = 1;
					}
				}
			}
			// csv読み込み
			File csv_file = new File(csv_filename);
			FileInputStream fis = new FileInputStream(csv_file);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			String line;
			int col_count = 0;
			int row_count = 0;
			while ((line = br.readLine()) != null) {
				String[] cols = line.split(",");
				for (String s : cols) {
					// System.out.println(Field[col_count][row_count]);
					Field[col_count][row_count] = Integer.parseInt(s);
					// 最大高さの更新
					if (max_height_step < Field[col_count][row_count])
						max_height_step = Field[col_count][row_count];
					// 最小高さの更新
					if (min_height_step > Field[col_count][row_count])
						min_height_step = Field[col_count][row_count];
					col_count++;
					// row_count++;
				}
				col_count = 0;
				row_count++;
				// row_count = 0;
				// col_count++;
			}
			fis.close();
			isr.close();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Maze.finishSetUp = true;
	}

	private static void init() {
		searchCount = 0;
		node_count = 0;
		wall_count = 0;
		max_node_count = 0;
		max_height_step = 0;
		min_height_step = 0;
		wall_setting = false;
		field_setting = false;
		finishSetUp = false;
	}

	public static void setNode(int x, int y, MazeNode node) {
		if (node != null) {
			node_count++;
			MAP[x][y] = node;
			if (searchMAP[x][y] == false) {
				searchCount++;
			}
			searchMAP[x][y] = true;
		} else {
			node_count--;
			MAP[x][y] = node;
		}
		if (node_count > max_node_count)
			max_node_count = node_count;
	}

	public static MazeNode getNode(int x, int y) {
		return MAP[x][y];
	}

	/**
	 * 現在のノード数を返す
	 * 
	 * @return
	 */
	public static int getNodeCount() {
		return node_count;
	}

	public static int getMaxNodeCount() {
		return max_node_count;
	}

	public static int getFieldHeightStep(int x, int y) {
		return Field[x][y];
	}

	public static int getMaxHeightStep() {
		return max_height_step;
	}

	public static int getMinHeightStep() {
		return min_height_step;
	}

	public static boolean getSearchResult(int x, int y) {
		return searchMAP[x][y];
	}

	public static boolean getWallPoint(int x, int y) {
		if (wall_setting) {
			return wallMAP[x][y];
		} else
			return false;
	}

	public static byte getstartPoint(int x, int y) {
		if (wall_setting || field_setting) {
			return startPoint[x][y];
		} else
			return 0;
	}

	public static byte getgoalPoint(int x, int y) {
		if (wall_setting || field_setting) {
			return goalPoint[x][y];
		} else
			return 0;
	}

	public static byte getCheckPoint(int x, int y) {
		return checkPoint[x][y];
	}

	public static Point getStartPoint() {
		return point_start;
	}

	public static Point getGoalPoint() {
		return point_goal;
	}

	public static boolean checkGoalArrival(MazeNode node) {
		int x = node.getPoint().x;
		int y = node.getPoint().y;
		if (getgoalPoint(x, y) == 1)
			return true;
		else
			return false;
	}

	// 中間地点チェック
	public static boolean checkPointArrival(MazeNode node) {
		int x = node.getPoint().x;
		int y = node.getPoint().y;
		if (getCheckPoint(x, y) == 1) {
			isCheck = true;
			return true;
		} else
			return false;
	}

	public static double getGoalRange(MazeNode node) {
		Point goalPoint = Maze.getGoalPoint();
		double a = Math.abs(node.getPoint().x - goalPoint.x);
		double b = Math.abs(node.getPoint().y - goalPoint.y);
		double range = Math.sqrt(a * a + b * b);

		return range;
	}

	public static double getRangeStartTogoal() {
		Point startPoint = Maze.getStartPoint();
		Point goalPoint = Maze.getGoalPoint();
		double a = Math.abs(startPoint.x - goalPoint.x);
		double b = Math.abs(startPoint.y - goalPoint.y);
		double range = Math.sqrt(a * a + b * b);

		return range;
	}

	public static boolean getIsCheck() {
		return isCheck;
	}

	// Fieldをセットする
	public static void setFieldHeightStep(int x, int y, int value) {
		Field[x][y] = value;
	}

	/**
	 * 到達したゴールに隣接するゴール地点の削除
	 */
	public static void deleteGoal(int x, int y) {
		ArrayDeque<Point> next = new ArrayDeque<>();
		next.add(new Point(x, y));
		while (!next.isEmpty()) {
			Point p = next.poll();
			if (goalPoint[p.x][p.y] > 0) {
				goalPoint[p.x][p.y] = -1;
				int left = p.x - 1;
				int right = p.x + 1;
				int up = p.y - 1;
				int down = p.y + 1;
				if (left >= 0)
					next.add(new Point(left, p.y));
				if (right < width)
					next.add(new Point(right, p.y));
				if (up >= 0)
					next.add(new Point(p.x, up));
				if (down < height)
					next.add(new Point(p.x, down));
			}
		}

	}

	public static int getSearchCount() {
		return searchCount;
	}

	public static boolean getWallSetting() {
		return wall_setting;
	}

	public static boolean getFieldSetting() {
		return field_setting;
	}

	public static boolean getCheckPointSetting() {
		return checkPoint_setting;
	}

	public static int getSearchMAPTrue() {
		int cnt = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (searchMAP[x][y] == true)
					cnt++;
			}
		}
		return cnt;
	}

	public static int getTotalCell() {
		return width * height - wall_count;
	}

	/**
	 * 周囲の探索済みセルの割合を取得する
	 * 
	 * @param x
	 * @param y
	 * @param radius
	 * @return
	 */
	public static double getArroundSearchResultRate(int x, int y, int radius) {
		int total = 0;
		int count = 0;
		int x_min = x - radius;
		while (x_min < 0) {
			x_min++;
		}
		int x_max = x + radius;
		while (x_max >= width) {
			x_max--;
		}
		int y_min = y - radius;
		while (y_min < 0) {
			y_min++;
		}
		int y_max = y + radius;
		while (y_max >= height) {
			y_max--;
		}
		for (int j = y_min; j <= y_max; j++) {
			for (int i = x_min; i <= x_max; i++) {
				if (j != y && i != x) {
					total++;
					if (searchMAP[i][j] == true) {
						count++;
					}
				}
			}
		}
		if (total == 0)
			return 0;
		else
			return (double) count / (double) total;
	}

	/**
	 * 周囲のノードの割合を取得する
	 * 
	 * @param x
	 * @param y
	 * @param radius
	 * @return
	 */
	public static double getArroundNodeRate(int x, int y, int radius) {
		int total = 0;
		int count = 0;
		int x_min = x - radius;
		while (x_min < 0) {
			x_min++;
		}
		int x_max = x + radius;
		while (x_max >= width) {
			x_max--;
		}
		int y_min = y - radius;
		while (y_min < 0) {
			y_min++;
		}
		int y_max = y + radius;
		while (y_max >= height) {
			y_max--;
		}
		for (int j = y_min; j <= y_max; j++) {
			for (int i = x_min; i <= x_max; i++) {
				if (j != y && i != x) {
					total++;
					if (MAP[i][j] != null) {
						count++;
					}
				}
			}
		}
		if (total == 0)
			return 0;
		else
			return (double) count / (double) total;
	}

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

	public static void main(String args[]) {
		Maze maze = new Maze("./resources/meiro.png");
		System.out.println(maze.getTotalCell());
	}
}
