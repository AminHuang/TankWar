
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList;


public class TankClient extends JFrame {
	
	private static final long serialVersionUID = 8372319530032586739L;
	
	//画布大小
	public static final int GAME_WIDTH = 800, GAME_HEIGHT = 600;
	
	Tank myTank = new Tank(50, 50, true, this);
	Wall w1 = new Wall(200,200,10,60,this);
	
	List<Tank> tanks = new ArrayList<Tank> ();
	List<Missile> missiles = new ArrayList<Missile> ();
	List<Explode> explodes = new ArrayList<Explode> ();
	
	//虚拟画布
	Image offScreenImage = null;
	
	public void paint(Graphics g) {
		super.paint(g);
		
		g.drawString("Missiles count:" + missiles.size(), 10, 50);
		g.drawString("Explodes count:" + explodes.size(), 10, 70);
		g.drawString("Tanks count:" + tanks.size(), 10, 90);
		
		myTank.draw(g);
		w1.draw(g);
		
		for(int i = 0; i < missiles.size(); i++) {
			Missile m = missiles.get(i);
			m.hitTanks(tanks);
			m.hitTank(myTank);
			m.hitWall(w1);
		    m.draw(g);			
		}
		
		for(int i = 0; i < explodes.size(); i++) {
			Explode e = explodes.get(i);
		    e.draw(g);			
		}
		
		for(int i = 0; i < tanks.size(); i++) {
			Tank t = tanks.get(i);
			t.collidesWithWall(w1);
			t.collidesWithOthers(tanks);
		    t.draw(g);			
		}
	}
	
	/*
	public void update(Graphics g) {
		//super.update(g);
		if(offScreenImage == null){
			offScreenImage = this.createImage(800, 600);
		}
		Graphics offScreenG = offScreenImage.getGraphics();
		paint(offScreenG);
		g.drawImage(offScreenImage, 0, 0, null);
	}
	*/

	//创建窗口
	public void launchFrame() {
		for(int i = 0; i < 10; i++) {
			Tank t = new Tank(70+i*40, 70+i*20, false, this);
			tanks.add(t);
		}
		
		
		this.getContentPane().setBackground(new Color(0x00FF7F));
		this.setLocation(400,300);
		this.setSize(GAME_WIDTH, GAME_HEIGHT);
		this.setTitle("TankWar");
		
		this.addKeyListener(new KeyMonitor());
		
		this.setVisible(true);
		this.setResizable(false);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		new Thread(new PaintThread()).start();
		
	}

	//main函数
	public static void main(String[] args) {
		TankClient tc = new TankClient();
		tc.launchFrame();
	}
	
	//线程，重画
	private class PaintThread implements Runnable {
		public void run() {
			while (true) {
				repaint();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	//事件监听
	private class KeyMonitor extends KeyAdapter {
		public void keyReleased(KeyEvent e) {
			myTank.keyReleased(e);
		}

		public void keyPressed(KeyEvent e) {
			myTank.keyPressed(e);
		}
		
	}
}
