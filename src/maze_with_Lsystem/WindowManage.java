package maze_with_Lsystem;

import processing.core.PApplet;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

//-----------------------
// Window 操作イベントクラス
//-----------------------
class WindowManage extends WindowAdapter {
    PApplet subApp;

    // コンストラクタ
    public WindowManage(PApplet _subApp) {
        subApp = _subApp;
    }

    // Closeされた直後に動作
    public void windowClosed(WindowEvent e) {
        // 自分を終了
        subApp.noLoop();
        subApp.getSurface().stopThread();
    }
}