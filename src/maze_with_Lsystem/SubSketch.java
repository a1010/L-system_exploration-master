package maze_with_Lsystem;

import processing.core.PApplet;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

// --------------------------------
// サブウィンドウ側
// --------------------------------
public class SubSketch extends PApplet {
    PApplet mainApplet;
    int exitStatus = 0;
    Method method = null;
    private float subTextX = 0;

    // コンストラクタ
    public SubSketch(PApplet _mainApplet) {
        mainApplet = _mainApplet;
    }

    public void settings() {
        size(400, 400);
    }

    public void setup() {
        // メインウィンドウ側の終了ステータス受信メソッドを取得
        try {
            method = mainApplet.getClass().getMethod("GetSubStatus", int.class);
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        }

        // 操作イベントを書き換え
        redefineExitEvent();

        textSize(24);
        textAlign(LEFT, TOP);
    }

    public void draw() {
        // 適当なものを表示
        background(255);
        fill(0);
        text("Sub", subTextX++, height / 2);
        if (subTextX > width) {
            subTextX = 0;
        }

        text("FrameCount:" + frameCount, 0, 0);
    }

    public void mouseClicked() {
        // メインウィンドウのメソッドを呼び出す
        try {
            // 終了コード（ここではフレームカウント）を渡す
            if (method != null) {
                method.invoke(mainApplet, frameCount);
            }
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        }

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

    public static void main(String[] args) {
    }
}
