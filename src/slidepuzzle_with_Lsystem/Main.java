package slidepuzzle_with_Lsystem;

import java.applet.Applet;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;
import processing.core.PApplet;

import lsystem.Lsystem;
import lsystem.SlidePuzzleLsystem;
import serachItem.SlidePuzzleNode;

public class Main implements KeyListener {

	private ArrayList<SlidePuzzleNode> rootNode;
	private Lsystem lsys;
	private ArrayDeque<SlidePuzzleNode> nodes;
	private ArrayDeque<SlidePuzzleNode> postNodes;
	private ArrayDeque<SlidePuzzleNode> deleteNodes;
	private ArrayDeque<SlidePuzzleNode> drawQueue;
	private SlidePuzzleNode goalNode = null;
	private boolean roop = true;
	private boolean stop = false;
	private boolean push = false;

	public Main(SlidePuzzleNode startNode, int max_node_count) {
		lsys = new SlidePuzzleLsystem(max_node_count);
		rootNode = new ArrayList<SlidePuzzleNode>();
		rootNode.add(startNode);

		nodes = new ArrayDeque<SlidePuzzleNode>();
		postNodes = new ArrayDeque<SlidePuzzleNode>();
		deleteNodes = new ArrayDeque<SlidePuzzleNode>();

		drawQueue = new ArrayDeque<SlidePuzzleNode>();

		SlidePuzzle.MAP.put(startNode.getBoardState(), true);

		JFrame frame = new JFrame("Controller");
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		frame.addKeyListener(this);
	}

	public void run() {
		int step_num = 0;
		while (roop) {
			stop = false;
			if (stop == false) {
				System.out.println(step_num + "ステップ開始");
				for (SlidePuzzleNode node : rootNode)
					nodes.add(node);
				step_num++;
				preStep();
				step();
				postStep();
				deleteStep();
				drawStep2D();
				System.out.println(step_num + "ステップ完了");
				stop = true;
			} else {
				continue;
			}
			Scanner scan = new Scanner(System.in);
		}
		System.out.println("ループ抜け");

		if (goalNode != null) {
			finalization();
		} else {
			System.out.println("探索終了");
			System.out.println("step = " + step_num);
			System.out.println("map size = " + SlidePuzzle.MAP.size());

		}
	}

	public void preStep() {

	}

	public void step() {

		while (!nodes.isEmpty()) {
			SlidePuzzleNode node = nodes.poll();
			lsys.apply(node);
			postNodes.add(node);
			drawQueue.add(node);
			for (SlidePuzzleNode child : node.getChildrenByArrayList()) {
				nodes.add(child);
			}
		}
		System.out.println("node size =" + postNodes.size());
		System.out.println("map size =" + SlidePuzzle.MAP.size());
	}

	public void postStep() {
		if (postNodes.isEmpty())
			roop = false;
		while (!postNodes.isEmpty()) {
			SlidePuzzleNode node = postNodes.poll();
			if (node.getState().equals("F")) {
				goalNode = node;
				roop = false;
			}
			node.update();
			if (node.getDeadFlag())
				deleteNodes.add(node);
		}
	}

	public void deleteStep() {
		while (!deleteNodes.isEmpty()) {
			SlidePuzzleNode node = deleteNodes.poll();
			node.delete();
		}
	}

	public void drawStep2D() {
		MySketch2D.setBuffer(drawQueue);
	}

	public void drawStep3D() {
		MySketch3D.setBuffer(drawQueue);
	}

	public void stop() {
		roop = false;
	}

	public void start() {
		roop = true;
	}

	// 終了処理
	public void finalization() {
		ArrayDeque<SlidePuzzleNode> reverse = new ArrayDeque<SlidePuzzleNode>();
		reverse.add(goalNode);
		SlidePuzzleNode node = goalNode;
		int count = 0;
		while (true) {
			count++;
			System.out.println(node.getBoardState());
			if (node.getParent() != null)
				node = node.getParent();
			else
				break;
		}
		System.out.println(count);
	}

	public static void main(String[] args) {
		SlidePuzzleNode node = new SlidePuzzleNode("0", "132406857", "2", null);
		SlidePuzzle sp = new SlidePuzzle(3, 3, node.getBoardState(), "123456780");
		Main main = new Main(node, 10000);

		PApplet.main(new String[] { "--location=100,100", "maze_with_Lsystem.MySketch3D" });

		// SlidePuzzle sp = new SlidePuzzle(4, 4);
		// SlidePuzzleNode node = new SlidePuzzleNode("0", "312456089c7bd1ef", "2",
		// null);
		// Main main = new Main(node,"1234567891bcdef0");
		main.run();

	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keycode = e.getKeyCode();
		if (keycode == KeyEvent.VK_SHIFT) {
			if (push == false) {
				push = true;
				System.out.println("push");
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

}
