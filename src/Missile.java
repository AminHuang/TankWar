
import java.awt.*;
import java.util.List;



public class Missile {
	//移动速度
	public static final int xSpeed = 18;
	public static final int ySpeed = 18;
	
	//子弹宽度，高度
	public static final int WIDTH = 10;
	public static final int HEIGHT = 10;
	
	private int x, y; // location
	private boolean live = true; //子弹是否出界
	private boolean good; // 是好坦克的子弹还是自己人的子弹
	
	public boolean isLive() {
		return live;
	}

	Tank.direction dir;
	private TankClient tc;
	
	public Missile(int x, int y, Tank.direction dir) {
		this.x = x;
		this.y = y;
		this.dir = dir;
	}
	
	public Missile(int x, int y, Tank.direction dir, TankClient tc) {
		this.x = x;
		this.y = y;
		this.dir = dir;
		this.tc = tc;
	}
	
	public Missile(int x, int y, boolean good, Tank.direction dir, TankClient tc) {
		this.x = x;
		this.y = y;
		this.dir = dir;
		this.tc = tc;
		this.good = good;
		
	}
	
	//重画
	public void draw(Graphics g){
		if(!live) {
			tc.missiles.remove(this);
			return ;
		}
		Color c = g.getColor();
		g.setColor(Color.BLACK);
		g.fillOval(x, y, WIDTH, HEIGHT);
		g.setColor(c);
		move();
	}
	
	//移动
	void move() {
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
		}
		
		if(x < 0 || y < 0 || x > TankClient.GAME_WIDTH || y > TankClient.GAME_HEIGHT) {
			live = false;
		}
	}
	
	public Rectangle getRect() {
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}
	
	public boolean hitTank(Tank t) {
		if(this.good == t.isGood()) return false;
		
		if(this.getRect().intersects(t.getRect()) && t.isLive() && this.live) {
			t.setLive(false);
			this.live = false;
			Explode e = new Explode(x, y, tc);
			tc.explodes.add(e);
			return true;
		}
		else
			return false;
	}
	
	public boolean hitTanks(List<Tank> tanks) {
		for(int i = 0; i < tanks.size(); i++) {
			if(hitTank(tanks.get(i)))
				return true;
		}
		return false;
	}
	
	public boolean hitWall(Wall w) {
		if(this.getRect().intersects(w.getRect()) && this.live) {
			this.live = false;
			return true;
		}
		else
			return false;
	}

}
