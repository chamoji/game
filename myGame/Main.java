package myGame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Main extends JPanel implements KeyListener {

	public static int id = 0;
	public static boolean title = true;

	private BufferedImage img;
	BufferedImage sprite[] = new BufferedImage[8];
	BufferedImage stage[] = new BufferedImage[6];
	private static Graphics g;
	private Random rand = new Random();

	public static final int RES_WIDTH = 768;
	public static final int RES_HEIGHT = 1024;


	public static long start;
	public static long stop;
	public static double minutes, rseconds, seconds, survive;

	static int tick = 0;

	private static double START_X = (RES_WIDTH / 2) - 16;
	private static double START_Y = (RES_HEIGHT - 128);
	private double x = START_X;
	private double y = START_Y;

	private double xspeed = 0;
	private double yspeed = 0;
	private boolean powerUp;
	private int textTime;
	private int cardTime;
	private boolean cardDone = false;
	private int level = 0;
	private static double ppm = 6;

	private static boolean pressedUp = false;
	private static boolean pressedDown = false;
	private static boolean pressedLeft = false;
	private static boolean pressedRight = false;
	private static boolean pressedShot = false;
	private static boolean gameOver = false;

	private static int delay = 0;
	private static ArrayList<Bullet> bullets = new ArrayList<Bullet>(1024);

	private static final int OPTIONS_LIMIT = 10;
	private static ArrayList<Options> options = new ArrayList<Options>(100);
	private static ArrayList<Items> items = new ArrayList<Items>(100);

	public static int ANGLE_STEP = 16;
	public static int PROXY = 50;

	public static int aid = 0;
	public static int bid = 0;
	public static int cid = 0;
	static ArrayList<Bullet> layer1 = new ArrayList<Bullet>(1024);
	static ArrayList<Bullet> layer2 = new ArrayList<Bullet>(1024);
	static ArrayList<Bullet> layer3 = new ArrayList<Bullet>(1024);
	static ArrayList<Bullet> layer4 = new ArrayList<Bullet>(1024);

	private static boolean pressedSpace;

	static JFrame frame;
	static Point windowLocation;

	public static int select = 0;

	public static void main(String args[]) {
		frame = new JFrame("Hello World!");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(RES_WIDTH, RES_HEIGHT);

		Main m = new Main();
		KeyListener l = m;
		m.addKeyListener(l);
		m.setFocusable(true);
		frame.setResizable(true);
		frame.setContentPane(m);
		frame.getContentPane().setBackground(Color.white);
		frame.setVisible(true);

		while (true) {
			tick++;
			delay--;
			m.repaint();
			m.collision();
			m.movement();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			m.level1();
		}
	}

	public Main() {
		super();
		try {
			sprite[0] = ImageIO.read(new File("./src/bullet_blue.bmp"));
			stage[0] = ImageIO.read(new File("./src/stage0.png"));
			img = sprite[0];
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	// helper functions

	public static double pointToPoint(double x1, double y1, double x2, double y2) {
		double dx, dy;
		dx = x2 - x1;
		dy = y2 - y1;
		double angle = Math.atan2(dy, dx);
		// System.out.println("angle: " + angle);
		return Math.toDegrees(angle);
	}

	public double distance(double x, double y, double xx, double yy) {
		double dx, dy;
		double result;
		dx = (xx) - x;
		dy = (yy) - y;
		result = Math.sqrt(Math.pow(dx, 2) + (Math.pow(dy, 2)));

		return result;
	}

	public void checkBounds() {
		// make sure player stays within the bounds
		if (x <= 0)
			x = 0;
		if (x >= RES_WIDTH)
			x = RES_WIDTH;
		if (y <= 0)
			y = 0;
		if (y >= RES_HEIGHT)
			y = RES_HEIGHT;
	}

	public void collision() {
		// collision for player
		for (int i = 0; i < layer1.size(); i++) {
			Bullet b = layer1.get(i);
			if (b.x >= x - 4 && b.x <= x + 4 && b.y >= y - 4 && b.y <= y + 4) {
				//gameOver = true;
			}
		}

		for (int i = 0; i < items.size(); i++) {
			Items it = items.get(i);
			if (it.x >= x - 32 && it.x <= x + 32 && it.y >= y - 32
					&& it.y <= y + 32) {
				if (id > 0 && id < OPTIONS_LIMIT) {
					options.add(new Options(sprite[0], options.get(id - 1).x,
							options.get(id - 1).y, 0, ppm, 0, id));
					id++;
				} else if (id == 0) {
					options.add(new Options(sprite[0], x, y, 0, ppm, 0, id));
					id++;
				}
				it.destroy();
				powerUp = true;
				textTime = 100;
			}
		}
	}

	public void movement() {

		if (pressedUp || pressedDown || pressedRight || pressedLeft) {
			for (int i = 0; i < options.size(); i++) {
				Options o = options.get(i);
				o.move(x, y, options);
			}
		}

		if (pressedShot && delay < 0) {
			Bullet b = new Bullet(sprite[0], x, y, 270, 20, 0, 0);
			bullets.add(b);
			for (int i = 0; i < options.size(); i++) {
				Options o = options.get(i);
				Bullet c = new Bullet(sprite[0], o.x, o.y, 270, 20, 0, 0);
				bullets.add(c);
			}
			delay = 4;
		}

		// movement logic for player
		if (xspeed < 0 && x > 0) {
			x = (x + xspeed);
		} else if (xspeed > 0 && x < RES_WIDTH - 12)
			x = (x + xspeed);
		else
			xspeed = 0;

		if (yspeed < 0 && y > 0)
			y = (y + yspeed);
		else if (yspeed > 0 && y < RES_HEIGHT - 68)
			y = (y + yspeed);
		else
			yspeed = 0;
	}

	public void cleanBullets() {
		// remove bullets if they are "offscreen"
		for (int i = 0; i < bullets.size() - 1; i++) {
			Bullet b = bullets.get(i);
			if (b.x < 0 || b.x > RES_WIDTH || b.y < 0 || b.y > RES_HEIGHT - 42) {
				b.destroy();
			}
		}

		for (int i = 0; i < layer1.size() - 1; i++) {
			Bullet b = layer1.get(i);
			if (b.x < 0 || b.x > RES_WIDTH || b.y < -10
					|| b.y > RES_HEIGHT - 42) {
				b.destroy();
			}
		}

	}

	public void drawNote(String s, Graphics g) {
		Font f = new Font(Font.SERIF, Font.BOLD, 18);
		g.setFont(f);
		g.drawString(s, (int) x - 40, (int) y - 16);

	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// level title cards
		if (level == 1 && tick >= 25 && tick <= 350) {
			g.drawImage(stage[0], 0, 0, null);
			g.setColor(new Color(25, 0, 0, 0));
			cardTime--;
			if (cardTime < 0) {
				cardDone = true;
			}
		}

		if (powerUp && textTime > 0) {
			drawNote("POWERUP!", g);
			textTime--;
		} else {
			powerUp = false;
		}

		for (Items i : items) {
			i.move();
			i.draw(g);
		}

		for (int i = 0; i < bullets.size(); i++) {
			Bullet b = bullets.get(i);
			b.draw(g);
		}
		for (int i = 0; i < options.size(); i++) {
			Options o = options.get(i);
			o.draw(g);
		}

		// player bullets
		for (int i = 0; i < bullets.size(); i++) {
			Bullet b = bullets.get(i);
			b.move();
		}

		// enemy bullet layers to draw

		for (int i = 0; i < layer1.size(); i++) {
			Bullet b = layer1.get(i);
			b.draw(g);
		}
		for (int i = 0; i < layer2.size(); i++) {
			Bullet b = layer2.get(i);
			b.draw(g);
		}
		for (int i = 0; i < layer3.size(); i++) {
			Bullet b = layer3.get(i);
			b.draw(g);
		}
		for (int i = 0; i < layer4.size(); i++) {
			Bullet b = layer4.get(i);
			b.draw(g);
		}

		// handle game over

		if (gameOver) {
			g.setColor(Color.black);
			windowLocation = frame.getLocation();
			g.setFont(new Font("Serif", Font.BOLD, 36));
			g.drawString("Game is Over", (RES_WIDTH / 2) - 100,
					(RES_HEIGHT / 2) - 100);
		}
		// draw the player
		g.drawImage(img, (int) x, (int) y, null);

	}

	// keyboard events
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			yspeed = -ppm;
			pressedUp = true;
			System.out.println("UP ");
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			yspeed = ppm;
			pressedDown = true;
			System.out.println("DOWN ");

		}
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			xspeed = -ppm;
			pressedLeft = true;
			System.out.println("LEFT ");
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			xspeed = ppm;
			pressedRight = true;
			System.out.println("RIGHT ");
		}
		if (e.getKeyCode() == KeyEvent.VK_Z) {
			pressedShot = true;
			System.out.println("SHOOT");
		}
		if (e.getKeyCode() == KeyEvent.VK_X) {
			if (id > 0) {
				options.remove(id - 1);
				id--;
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_C) {
			if (id > 0 && id < OPTIONS_LIMIT) {
				options.add(new Options(sprite[0], options.get(id - 1).x,
						options.get(id - 1).y, 0, ppm, 0, id));
				id++;
			} else if (id == 0) {
				options.add(new Options(sprite[0], x, y, 0, ppm, 0, id));
				id++;
			}

		}
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			items.add(new Items(sprite[0], rand.nextInt(RES_WIDTH), 0, 90, 1,
					0, 1));
			System.out.println("RESET");
			pressedSpace = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			pressedUp = false;
			yspeed = 0;
			if (pressedDown)
				yspeed = ppm;
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			pressedDown = false;
			yspeed = 0;
			if (pressedUp)
				yspeed = -ppm;
		}
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			pressedLeft = false;
			xspeed = 0;
			if (pressedRight)
				xspeed = ppm;
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			pressedRight = false;
			xspeed = 0;
			if (pressedLeft)
				xspeed = -ppm;
		}
		if (e.getKeyCode() == KeyEvent.VK_Z) {
			pressedShot = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			pressedSpace = false;
		}
	}

	public void level1() {
		level = 1;

		for (Bullet b : layer1) {
			b.move();
		}
		if (tick == 300) {
			for (int i = 0; i < 360; i += 8) {
				Bullet b = new Bullet(sprite[0], RES_WIDTH / 2, RES_HEIGHT / 2,
						i, 1, 0, 2);
				layer1.add(b);
			}
			for (int i = 0; i < 360; i += 8) {
				Bullet b = new Bullet(sprite[0], RES_WIDTH / 2, RES_HEIGHT / 2,
						i, 0.5, -0.05, 2);
				layer1.add(b);
			}
			items.add(new Items(sprite[0], rand.nextInt(RES_WIDTH), 0, 90, 1,
					0, 1));
		}
	}
}
