package ch.aiko.engine.geometry;

import java.awt.Point;

public class Box extends GeometryObject {

	public Box(int x, int y, int w, int h, int color) {
		super(x, y, w, h, color);
	}

	public boolean collision(int x, int y, int w, int h) {
		for (int xx = 0; xx <= w; xx++) {
			for (int yy = 0; yy <= h; yy++) {
				if (collision(xx + x, yy + y)) return true;
			}
		}
		return false;
	}

	public boolean collision(Box other) {
		return collision(other.x, other.y, other.w, other.h);
	}

	public boolean collision(int x2, int y2) {
		if (x2 >= x && x2 <= x + w && y2 >= y && y2 <= y + h) return true;
		return false;
	}

	public void loadPoints() {
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				points.add(new Point(i, j));
			}
		}
	}

}
