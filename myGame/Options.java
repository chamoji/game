package myGame;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Options extends Bullet {

	private double px;
	private double py;
	private int id;
	private double STEP_SIZE = 16;
	private int OPTIONS_WIDTH = 8;
	private int OPTIONS_HEIGHT = 8;

	public Queue<myPoint> path = new LinkedList<myPoint>();

	public Options(BufferedImage s, double x, double y, double direction,
			double initVelocity, double accel, int id) {
		super(s, x, y, direction, initVelocity, accel, id);
		this.id = id;
	}

	public void move(double px, double py, ArrayList<Options> options) {
		time++;
		System.out.println("ID: " + id);

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

	public void draw(Graphics g) {
		g.drawOval((int) x, (int) y, OPTIONS_WIDTH, OPTIONS_HEIGHT);
	}
}