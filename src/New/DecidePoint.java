package New;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.management.ConstructorParameters;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class DecidePoint extends JFrame implements MouseListener{
	Image img;
	Toolkit toolkit;
	Display panel;
	Container pane;

	static String maze_file;
	private String startIcon;
	private String goalIcon;
	private String checkIcon;

	private Point startPosition;
	private Point goalPosition;
	private Point checkPosition;
	private List<Point> mountPointList;

	enum Select {start, goal, finish, check};
	static Select s;

	Point point;
	private File f;
	private File csv;
	private BufferedImage read;
	private int w;
	private int h;
	
	private int p;
	
	private boolean isCheck;//中間地点を置くかどうか

	public DecidePoint(String maze_file){
		//System.out.println(maze_file);
		f = new File(maze_file);
		isCheck = false;
		try {
			read = ImageIO.read(f);
			w = read.getWidth();
			h = read.getHeight();

			setTitle("スタートとゴールを決めよう");
			setSize(w, h+40);
			printImage(maze_file, new Point(0, 0));
			System.out.println("スタートの位置をクリックしてください");
			s = Select.start;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public DecidePoint(String maze_file, int num){
		f = new File(maze_file);
		isCheck = true;
		try {
			read = ImageIO.read(f);
			w = read.getWidth();
			h = read.getHeight();

			setTitle("スタートとゴールと中間地点を決めよう");
			setSize(w, h+40);
			printImage(maze_file, new Point(0, 0));
			System.out.println("スタートの位置をクリックしてください");
			s = Select.start;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	class Display extends JPanel{
		public void paint(Graphics g){
			g.drawImage(img, 0, 0, this);
		}
	}

	public boolean isSetPoint(){
		if(s == Select.finish)
			return true;
		else
			return false;
	}

	public Point getStartPoint(){
		return startPosition;
	}
	
	public Point getGoalPoint(){
		return goalPosition;
	}
	
	public Point getCheckPoint(){
		return checkPosition;
	}
	
	//画像を指定の座標に表示する
	private void printImage(String name, Point point){
		f = new File(name);
		try {
			read = ImageIO.read(f);
			w = read.getWidth();
			h = read.getHeight();
			panel = new Display();
			getContentPane().setLayout(null);
			pane = getContentPane();
			pane.add(panel);
			panel.setBounds(point.x, point.y, w, h);
			panel.setBackground(Color.white);
			panel.addMouseListener(this);
			toolkit = Toolkit.getDefaultToolkit();
			img = toolkit.getImage(name);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		point = e.getPoint();

		switch(s){
		case start :{
			startPosition = point;
			s = Select.goal;
			System.out.println("ゴール位置をクリックしてください");
			startIcon = "./start.png";
			printImage(startIcon, startPosition);
			break;
		}
		case goal :{
			goalPosition = point;
			if(!isCheck)
				s = Select.finish;
			else
				s = Select.check;
			goalIcon = "./goal.png";
			printImage(goalIcon, goalPosition);
			break;
		}
		case finish :{
			break;
		}
		case check :{
			System.out.println("中間地点をクリックしてください");
			checkPosition = point;
			checkIcon = "./check.png";
			printImage(checkIcon, checkPosition);
			s = Select.finish;
			break;
		}
		}
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
