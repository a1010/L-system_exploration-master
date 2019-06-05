package tamori;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;



public class GraphDraw extends JFrame implements Runnable {

    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 960;
    
    private int window_width,window_height;
    private Insets insets;
    private Thread thread;
   // private Chara chara;
    private VolatileImage  bufferimage = null;
    private Graphics off = null;
    
    private GraphData gd;

    public GraphDraw() {
    	
    	gd = new GraphData();
    	
        //タイトル画面を表示
        setTitle("メイン画面");
        //閉じるボタンを押したら閉じる
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //ウィンドウのサイズを変更不可にする
        setResizable(false);
        insets = getInsets();
        //画面の横の大きさ
        window_width=SCREEN_WIDTH+insets.left+insets.right;
        //画面の縦の大きさ
        window_height=SCREEN_HEIGHT+insets.top+insets.bottom;
        //ウィンドウのサイズを設定する
        setSize(window_width,window_height);
        //ウィンドウを画面の中央に設置
        setLocationRelativeTo(null);
        thread = new Thread(this);
        thread.start();
    }
    
    
    //スレッド
    public void run()
    {
          //オフスクリーンに描画処理
        while(true)
        {
             try 
             {
                 //オフスクリーン作成
                 createOffScreen();
                 //ゲーム情報の更新
                 graphUpdate();
                 do 
                 {
                     //VRAMが消失していた場合
                     resetVolatileImage();
                     //オフスクリーンに描画処理
                     graphOffDraw();
                     //表に全て表示する
                     graphUpDraw();
                 } while(bufferimage.contentsLost());
             }catch(NullPointerException e){}

            try {
                Thread.sleep(20);
            } 
            catch (InterruptedException e) 
            {
                e.printStackTrace();
            }
        }
    }
    
    public void resetVolatileImage()
    {
        GraphicsConfiguration gc = this.getGraphicsConfiguration();
        
        //VRAMが消失したい場合
        if (bufferimage.validate(gc) == VolatileImage.IMAGE_INCOMPATIBLE)
        {
            bufferimage = createVolatileImage(SCREEN_WIDTH,SCREEN_HEIGHT);
        }
    }

    //ゲームの更新処理
    public void graphUpdate()
    {
        //chara.move();
    }
    
    public void createOffScreen()
    {
        if (bufferimage == null)
        {
            //オフスクリーンを指定のサイズ作成する
            bufferimage = createVolatileImage(SCREEN_WIDTH,SCREEN_HEIGHT);
            //オフスクリーンが作られていないなら
            if (bufferimage == null)
            {
                System.out.println("era");
                return;
            }
            else 
            {
                //オフスクリーンに描画処理をする
            	System.out.println("draw");
                off = bufferimage.getGraphics();
            }
        }
    }
    

    //オフスクリーンに描画処理
    public void graphOffDraw()
    {
        
        //オフスクリーン用のグラフィックオブジェクトが作成されていた場合
        if (off != null)
        {
            //赤にする
            off.setColor(Color.WHITE);
            //オフスクリーンを赤で塗りつぶす
            off.fillRect(0,0,SCREEN_WIDTH,SCREEN_HEIGHT);
        
            gd.drawAll(off);
        }
        else
        {
            return;
        }
    }
    
    //オフスクリーンの内容をすべて表に転送する
    public void graphUpDraw()
    {
        //グラフィックオブジェクト取得
        Graphics g = getGraphics(); 
        //グラフィックオブジェクトが取得されていて
        //オフスクリーンが作成されていた場合
        if ((g != null) && (bufferimage != null))
        {
            //バックバッファの内容をすべて表に描画する
            g.drawImage(bufferimage,0,insets.top,this);
        }
        Toolkit.getDefaultToolkit().sync();

        
        //グラフィックオブジェクトが取得されている
        if (g != null)
        {
            //グラフィックオブジェクトの削除
            g.dispose();
        }
    }
    
    
    //ここから処理が開始する
    public static void main(String[] args) {
        GraphDraw f = new GraphDraw();
        //画面を表示する
        f.setVisible(true);
    }
            
}
