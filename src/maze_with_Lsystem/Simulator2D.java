package maze_with_Lsystem;

import java.awt.Point;
import java.util.ArrayList;

import processing.core.PApplet;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;

@SuppressWarnings("serial")
public class Simulator2D extends PApplet {
	Method method = null;

	ArrayList<Point> drawArray = new ArrayList<Point>();
	static ArrayList<Point> drawBuffer = new ArrayList<Point>();
	static boolean update_flag = false;

	public static boolean draw_searchMAP = false;

	static boolean finish = false;

	static boolean output = false;
	static String filename = "";

	public void settings() {
		System.out.println("Simulator2D: setting");
		size(Maze.width, Maze.height);

		// fullScreen();

		clear();
	}

	public void setup() {

		// Window 操作イベントを書き換え
		redefineExitEvent();

		while (Maze.finishSetUp == false) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void draw() {
		// System.out.println("Simulator2D: draw");
		background(255);
		update();

		// フェロモン（仮）
		if (draw_searchMAP) {
			stroke(255, 0, 0);
			strokeWeight(1);
			for (int x = 0; x < Maze.width; x++) {
				for (int y = 0; y < Maze.height; y++) {
					if (Maze.getSearchResult(x, y) == true) {
						point(x, y);
					}
				}
			}
		}

		// 原点
		strokeWeight(1);
		stroke(0, 0, 0);
		point(0, 0);

		// // 壁があるときの描画 中間なし
		// if (Maze.getWallSetting() && !Maze.getCheckPointSetting()) {
		// for (int y = 0; y < Maze.height; y++) {
		// for (int x = 0; x < Maze.width; x++) {
		// if (Maze.getWallPoint(x, y)) {
		// stroke(0, 0, 0);
		// // stroke(30,0,0);
		// point(x, y);
		// } else if (Maze.getstartPoint(x, y) != 0) {
		// stroke(255, 255, 0);
		// point(x, y);
		// } else if (Maze.getgoalPoint(x, y) != 0) {
		// stroke(255, 255, 0);
		// point(x, y);
		// }
		// }
		// }
		// }
		// // 様々な障害物があるときの描画
		// else if (Maze.getFieldSetting() && !Maze.getCheckPointSetting()) {
		// for (int y = 0; y < Maze.height; y++) {
		// for (int x = 0; x < Maze.width; x++) {
		// if (Maze.getFieldHeightStep(x, y) == 100) {
		// stroke(255, 0, 255, 255);
		// point(x, y);
		// } else if (Maze.getFieldHeightStep(x, y) > 0) {
		// int alpha = (int) (255 * (double) Maze.getFieldHeightStep(x, y)
		// / (double) Maze.getMaxHeightStep());
		// // stroke(30, 0, 0, alpha);
		// stroke(alpha, 0, alpha);
		// point(x, y);
		// } else if (Maze.getFieldHeightStep(x, y) < 0) {
		// // int alpha = (int) (255 * (double) Maze.getFieldHeightStep(x, y)
		// // / (double) Maze.getMinHeightStep());
		// // stroke(255, 0, 255, 255-alpha);
		// // stroke(125, 0, alpha);
		// stroke(255, 0, 255, 255);
		// Maze.setFieldHeightStep(x, y, 100);
		// point(x, y);
		// } else if (Maze.getstartPoint(x, y) != 0) {
		// stroke(255, 255, 0);
		// point(x, y);
		// } else if (Maze.getgoalPoint(x, y) != 0) {
		// stroke(255, 255, 255);
		// point(x, y);
		// }
		// /*
		// * else if(Maze.getgoalPoint(x, y) > 0){ stroke(255, 0, 255); point(x , y ); }
		// */
		// /*
		// * else if(Maze.getgoalPoint(x, y) < 0){ stroke(80, 100, 255); point(x , y );
		// }
		// */
		// }
		// }
		// }
		// // 壁がある時の描画 中間あり
		// else if (Maze.getWallSetting() && Maze.getCheckPointSetting()) {
		// for (int y = 0; y < Maze.height; y++) {
		// for (int x = 0; x < Maze.width; x++) {

		// if (Maze.getWallPoint(x, y)) {
		// stroke(255, 0, 255, 255);
		// // stroke(30,0,0);
		// point(x, y);
		// } else if (Maze.getstartPoint(x, y) != 0) {
		// stroke(255, 255, 0);
		// point(x, y);
		// } else if (Maze.getgoalPoint(x, y) != 0) {
		// stroke(255, 255, 255);
		// point(x, y);
		// } else if (Maze.getCheckPoint(x, y) != 0) {
		// stroke(0, 255, 255);
		// point(x, y);
		// }
		// }
		// }
		// }
		// // 山、中間あり
		// else if (Maze.getFieldSetting() && Maze.getCheckPointSetting()) {
		// for (int y = 0; y < Maze.height; y++) {
		// for (int x = 0; x < Maze.width; x++) {
		// if (Maze.getFieldHeightStep(x, y) == 100) {
		// stroke(30, 0, 0);
		// point(x, y);
		// } else if (Maze.getFieldHeightStep(x, y) > 0) {
		// int alpha = (int) (255 * (double) Maze.getFieldHeightStep(x, y)
		// / (double) Maze.getMaxHeightStep());
		// stroke(30, 0, 0, alpha);
		// // stroke(alpha, 0, alpha);
		// point(x, y);
		// } else if (Maze.getFieldHeightStep(x, y) < 0) {
		// // int alpha = (int) (255 * (double) Maze.getFieldHeightStep(x, y)
		// // / (double) Maze.getMinHeightStep());
		// // stroke(255, 0, 255, 255-alpha);
		// // stroke(125, 0, alpha);
		// stroke(255, 0, 255, 255);
		// Maze.setFieldHeightStep(x, y, 100);
		// point(x, y);
		// } else if (Maze.getstartPoint(x, y) != 0) {
		// stroke(100, 0, 0);
		// point(x, y);
		// } else if (Maze.getgoalPoint(x, y) != 0) {
		// stroke(0, 0, 20);
		// point(x, y);
		// } else if (Maze.getCheckPoint(x, y) != 0) {
		// stroke(50, 50, 0);
		// point(x, y);
		// }
		// }
		// }
		// }

		stroke(0, 255, 0);// セルの移動描画
		strokeWeight(1);

		for (Point p : drawArray) {
			point(p.x, p.y);
		}

		if (output == true) {
			save(filename);
			output = false;
		}
		if (finish == true)
			stopDraw();
	}

	public void stopDraw() {
		exit();
	}

	public static void saveImg(String filename) {
		output = true;
		Simulator2D.filename = filename;
	}

	public static void finish() {
		finish = true;
	}

	public static void setBuffer(ArrayList<Point> points) {
		// System.out.println("Simulator2D: setBuffer");
		if (update_flag == false) {
			drawBuffer.clear();
			for (Point p : points) {
				drawBuffer.add(p);
			}
			update_flag = true;
		}
	}

	private void update() {
		// System.out.println("Simulator2D: update");
		if (update_flag) {
			drawArray.clear();
			for (int i = 0; i < drawBuffer.size(); i++) {
				drawArray.add(drawBuffer.get(i));
			}
			update_flag = false;
		}
	}

	public void clear() {
		System.out.println("Simulator2D: clear");
		drawArray = new ArrayList<Point>();
		drawBuffer = new ArrayList<Point>();
		update_flag = false;

		finish = false;

		output = false;
		filename = "";
	}

	public void closeWindow() {
		// 自分自身のWindowを閉じる
		JFrame frame = getJFrame();
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}

	// PSurface がもつ Windowフレームを取得
	private JFrame getJFrame() {
		return (JFrame) ((processing.awt.PSurfaceAWT.SmoothCanvas) getSurface().getNative()).getFrame();
	}

	// Window操作用のイベントを再定義する
	private void redefineExitEvent() {
		// Windowフレームを取得する
		JFrame frame = getJFrame();

		// 該当Windowから、全てのWindow操作用イベントを削除し
		// 新しいイベントに書き換え
		for (final java.awt.event.WindowListener evt : frame.getWindowListeners()) {
			// イベントを削除
			frame.removeWindowListener(evt);
			// Window Close 動作を再定義
			// → 登録されている任意の WindowListener を呼び出したあとで
			// 自動的にフレームを隠して破棄
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			// 新しいWindow 操作イベントをセット
			frame.addWindowListener(new WindowManage(this));
		}
	}

	public static void main(String args[]) {
	}
}