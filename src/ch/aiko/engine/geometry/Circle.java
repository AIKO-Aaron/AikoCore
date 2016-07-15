package ch.aiko.engine.geometry;

import java.awt.Point;

public class Circle extends GeometryObject {

	public Circle(int x, int y, int w, int color) {
		super(x, y, w, w, color);
	}

	public boolean collision(int x, int y, int w, int h) {
		for (int xx = 0; xx <= w; xx++) {
			for (int yy = 0; yy <= h; yy++) {
				if (collision(xx + x, yy + y)) return true;
			}
		}
		return false;
	}
	
	public void setWidth(int w) {
		this.w = w;
		this.h = w;
	}

	/**public boolean collision(Box other, int xOff, int yOff) {
		return collision(other.x + xOff, other.y + yOff, other.w, other.h);
	}*/

	public boolean collision(int x, int y) {
		x -= this.x;
		y -= this.y;
		return x * x + y * y <= w * w;
	}

	public void loadPoints() {
		for (int i = -w; i <= w; i++) {
			for (int j = -h; j <= h; j++) {
				if (i * i + j * j <= w * w) points.add(new Point(i, j));
			}
		}
	}

}
