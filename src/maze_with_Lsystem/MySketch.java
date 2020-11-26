package maze_with_Lsystem;

import processing.core.PApplet;

// --------------------------------
// メインウィンドウ側
// --------------------------------
public class MySketch extends PApplet {
    private int exitCode = -1; // サブ側終了ステータス
    private float mainTextX = 0;

    private Simulator2D sim2D = null;
    // private SubSketch sa = null;

    public void settings() {
        size(800, 600);
    }

    public void setup() {
        textSize(24);
        textAlign(LEFT, TOP);
    }

    public void draw() {
        background(0);
        fill(255);

        if (sim2D != null && !sim2D.getSurface().isStopped()) {
            // サブウィンドウが生きている
            text("Main", mainTextX++, height / 2);
            if (mainTextX > width) {
                mainTextX = 0;
            }
        } else {
            // サブウィンドウは停止している
            text("SubApplet is Stop.\nDode=" + exitCode, 0, height / 2);
            if (sim2D != null) {
                sim2D.dispose();
                sim2D = null;
            }
        }

        // if (sa != null && !sa.getSurface().isStopped()) {
        // // サブウィンドウが生きている
        // text("Main", mainTextX++, height / 2);
        // if (mainTextX > width) {
        // mainTextX = 0;
        // }
        // } else {
        // // サブウィンドウは停止している
        // text("SubApplet is Stop.\nDode=" + exitCode, 0, height / 2);
        // if (sa != null) {
        // sa.dispose();
        // sa = null;
        // }
        // }
    }

    // サブウィンドウから終了ステータスを受け取るメソッド
    public void GetSubStatus(int status) {
        exitCode = status;
    }

    // public void mouseClicked() {
    // // サブウィンドウを開く
    // sa = new SubSketch(this);
    // PApplet.runSketch(new String[] { "SubSketch" }, sa);
    // }

    // サブウィンドウを作成する
    public Simulator2D runSubWindow(String[] args) {
        // サブウィンドウを開く
        // sim2D = new Simulator2D(this);
        PApplet.runSketch(args, sim2D);
        return sim2D;
    }

    // サブウィンドウを作成する
    public Simulator2D runSubWindow() {
        return runSubWindow(new String[] { "Simulator2D" });
    }

    public void runSubWindow(Simulator2D _sim2D) {
        sim2D = _sim2D;
    }

    // サブウィンドウの終了
    public void closeSubWindow() {
        sim2D.clear();
        sim2D.closeWindow();
        sim2D = null;
    }

    public static void main(String[] args) {
        MySketch tSketch = new MySketch();
        PApplet.runSketch(new String[] { "MySketch" }, tSketch);
    }
}