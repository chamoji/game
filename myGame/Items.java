package myGame;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Items extends Bullet {

	public Items(BufferedImage s, double x, double y, double direction,
			double initVelocity, double accel, int id) {
		super(s, x, y, direction, initVelocity, accel, id);
	}

	public void draw(Graphics g) {
		Font font = new Font("SERIF", Font.BOLD, 64);
		g.setFont(font);
		g.drawString("P", (int) x, (int) y);
	}

	public void destroy() {
		super.destroy();
	}

}
