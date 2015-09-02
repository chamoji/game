package myGame;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class Bullet {
	public double direction = 0;
	public double x = 0;
	public double y = 0;
	public double xspeed = 0;
	public double yspeed = 0;
	public double speed = 0;
	public double velocity = 0;
	public double initVelocity = 0;
	public double accel = 0;
	public double ax;
	public double ay;
	public double time = 0;
	private double px;
	private double py;
	int id;

	public double prevDist = 0;
	BufferedImage img = null;

	private double STEP_SIZE = 16;
	public static final double MAX_VELOCITY = 20;
	public static final double MIN_VELOCITY = -20;

	public Queue<myPoint> path = new LinkedList<myPoint>();

	public Bullet(BufferedImage s, double x, double y, double direction,
			double initVelocity, double accel, int id) {
		img = s;
		this.x = x;
		this.y = y;
		this.direction = direction;
		this.initVelocity = initVelocity;
		this.accel = accel;
		this.id = id;
	}

	public void snake(double px, double py, ArrayList<Bullet> options) {
		if (id == 0)
			path.add(new myPoint(px, py));
		else
			path.add(new myPoint(options.get(id - 1).x, options.get(id - 1).y));
		if (path.size() > STEP_SIZE) {
			x = path.peek().x;
			y = path.peek().y;
			path.poll();
			options.set(id, this);
		}
	}

	public void move() {
		time++;
		velocity = initVelocity + accel * time;
		if (velocity > MAX_VELOCITY)
			velocity = MAX_VELOCITY;
		if (velocity < MIN_VELOCITY)
			velocity = MIN_VELOCITY;
		x = x + velocity * Math.cos(Math.toRadians(direction));
		y = y + velocity * Math.sin(Math.toRadians(direction));
	}

	public void rotate(double cx, double cy, double radius) {

		// circular motion!
		double other = Math.abs(pointToPoint(x, y, cx, cy) + 180);

		x = (cx) + radius * Math.cos(Math.toRadians(other + velocity));
		y = (cy) + radius * Math.sin(Math.toRadians(other + velocity));
		direction = other;
	}

	public double distance(double x, double y, double xx, double yy) {
		double dx, dy;
		double result;
		dx = (xx) - x;
		dy = (yy) - y;
		result = Math.sqrt(Math.pow(dx, 2) + (Math.pow(dy, 2)));

		return result;
	}

	public static double pointToPoint(double x1, double y1, double x2, double y2) {
		double dx, dy;
		dx = x2 - x1;
		dy = y2 - y1;
		double angle = Math.atan2(dy, dx);
		// System.out.println("angle: " + angle);
		return Math.toDegrees(angle);
	}

	public void destroy() {
		img = null;
		x = Main.RES_WIDTH + 100;
		y = Main.RES_HEIGHT + 100;
		xspeed = 0;
		yspeed = 0;
		speed = 0;

	}

	public void draw(Graphics g) {
		if (id == 0)
			g.drawOval((int) x - 4, (int) y - 4, 16, 16);
		else
			g.drawImage(img, (int) x, (int) y, null);
	}
}
