/*
 * 进行到2.2
 */

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.*;

public class Tank {
	//移动速度
	public static final int xSpeed = 5;
	public static final int ySpeed = 5;
	
	//坦克宽度，高度
	public static final int WIDTH = 30;
	public static final int HEIGHT = 30;
	
	private static Random r = new Random();
	private int step = r.nextInt(14) + 4;
	
	TankClient tc;
	
	private int x, y; // location
	private int oldX, oldY;
	private boolean good; // 坦克是自己人还是敌人
	private boolean live = true; // 坦克是否存活
	
	
	public void setLive(boolean live) {
		this.live = live;
	}
	
	public boolean isLive() {
		return this.live;
	}
	
	public boolean isGood() {
		return this.good;
	}

	//方向布尔量
	private boolean bL = false, bU = false, bR = false, bD = false;
	enum direction{L, LU, U, RU, R, RD, D, LD, STOP};
	
	private direction dir = direction.STOP;
	private direction gunDir = direction.D;
	
	public Tank(int x, int y, boolean good) {
		this.x = x;
		this.y = y;
		this.oldX = x;
		this.oldY = y;
		this.good = good;
	}
	
	public Tank(int x, int y, boolean good, TankClient tc) {
		this(x,y,good);
		this.tc = tc;
		
		if(!good) {
			direction dirs[] = direction.values();
			int num = r.nextInt(dirs.length);
			dir = dirs[num];
		}
	}
	
	//重画
	public void draw(Graphics g){
		if(!live) {
			if(!good)
			    tc.tanks.remove(this);
			
			return ;
		}
		
		Color c = g.getColor();
		
		if(good)    g.setColor(Color.RED);
		else        g.setColor(Color.BLUE);
		
		g.fillOval(x, y, WIDTH, HEIGHT);
		g.setColor(c);
		
		//把炮筒画出来
		drawGun(g);
		
		move();
	}
	
	//画出炮筒
	void drawGun(Graphics g) {
		switch(gunDir) {
		case L:
			g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x, y+Tank.HEIGHT/2);
			break;
		case LU:
			g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x, y); 
			break;
		case U:
			g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x+Tank.WIDTH/2, y); 
			break;
		case RU:
			g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x+Tank.WIDTH, y);
			break;
		case R:
			g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x+Tank.WIDTH, y+Tank.HEIGHT/2); 
			break;
		case RD:
			g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x+Tank.WIDTH, y+Tank.HEIGHT); 
			break;
		case D:
			g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x+Tank.WIDTH/2, y+Tank.HEIGHT); 
			break;
		case LD:
			g.drawLine(x+Tank.WIDTH/2, y+Tank.HEIGHT/2, x, y+Tank.HEIGHT); 
			break;
		}
	}
	
	
	//移动坦克
	void move() {
		oldX = x;
		oldY = y;
		switch(dir) {
		case L:
			x -= xSpeed;break;
		case LU:
			x -= xSpeed; y -= ySpeed; break;
		case U:
			y -= ySpeed; break;
		case RU:
			x += xSpeed; y -= ySpeed; break;
		case R:
			x += xSpeed; break;
		case RD:
			x += xSpeed; y += ySpeed; break;
		case D:
			y += ySpeed; break;
		case LD:
			x -= xSpeed; y += ySpeed; break;
		case STOP: break;
		}
		
		if(this.dir != direction.STOP) {
			this.gunDir = this.dir;
		}
		
		if(x < 0) x = 0;
		if(y < 25) y = 25;
		if(x + Tank.WIDTH > TankClient.GAME_WIDTH) x = TankClient.GAME_WIDTH - Tank.WIDTH;
		if(y + Tank.HEIGHT > TankClient.GAME_HEIGHT) y = TankClient.GAME_HEIGHT - Tank.HEIGHT;
	
		if(!good) {
			if(step == 0){
			    direction dirs[] = direction.values();
			    int num = r.nextInt(dirs.length - 1);
			    dir = dirs[num];
			    
			    step = r.nextInt(14) + 4;
			}
			else {
				step--;
				if(r.nextInt(100) > 80)
					fire();
			}
		}
	}
	
	//键按下改变方向布尔量
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		switch(key){
		case KeyEvent.VK_LEFT:
			bL = true; break;
		case KeyEvent.VK_UP:
			bU = true; break;
		case KeyEvent.VK_RIGHT:
			bR = true; break;
		case KeyEvent.VK_DOWN:
			bD = true; break;
		}
		locateDirection();
	}
	
	//重构位置
	void locateDirection() {
		if(bL && !bU && !bR && !bD) dir = direction.L;
		else if(bL && bU && !bR && !bD) dir = direction.LU;
		else if(!bL && bU && !bR && !bD) dir = direction.U;
		else if(!bL && bU && bR && !bD) dir = direction.RU;
		else if(!bL && !bU && bR && !bD) dir = direction.R;
		else if(!bL && !bU && bR && bD) dir = direction.RD;
		else if(!bL && !bU && !bR && bD) dir = direction.D;
		else if(bL && !bU && !bR && bD) dir = direction.LD;
		else if(!bL && !bU && !bR && !bD) dir = direction.STOP;
	}
	
	//键松开改变方向布尔量
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		switch(key){
		case KeyEvent.VK_CONTROL:
			fire(); 
			break;
		case KeyEvent.VK_LEFT:
			bL = false; break;
		case KeyEvent.VK_UP:
			bU = false; break;
		case KeyEvent.VK_RIGHT:
			bR = false; break;
		case KeyEvent.VK_DOWN:
			bD = false; break;
		}
		locateDirection();
	}

	public Missile fire() {
		if(!live) return null;
		
		int x = this.x + Tank.HEIGHT / 2 - Missile.WIDTH / 2;
		int y = this.y + Tank.HEIGHT / 2 - Missile.HEIGHT / 2;
		Missile m = new Missile(x,y,good,gunDir,tc);
		tc.missiles.add(m);
		return m;
	}
	
	public Rectangle getRect(){
		return new Rectangle(x,y,WIDTH,HEIGHT);
	}
	
	public void stay() {
		x = oldX;
		y = oldY;
	}
	
	public boolean collidesWithWall(Wall w) {
		if(this.getRect().intersects(w.getRect()) && this.live) {
			stay();
			return true;
		}
		else
			return false;
	}
	
	public boolean collidesWithOthers(java.util.List<Tank> tanks) {
		for(int i = 0; i < tanks.size(); i++) {
			Tank t = tanks.get(i);
			if(this.getRect().intersects(t.getRect()) && this.live && t != this) {
				this.stay();
				t.stay();
				return true;
			}
		}
		
			return false;
	}
	
	

}
