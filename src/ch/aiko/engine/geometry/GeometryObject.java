package ch.aiko.engine.geometry;

import java.awt.Point;
import java.util.ArrayList;

import ch.aiko.engine.graphics.Renderable;
import ch.aiko.engine.graphics.Renderer;

public abstract class GeometryObject implements Renderable {

	protected int x, y, w, h, color;
	protected ArrayList<Point> points = new ArrayList<Point>();
	private int[][] points_array;

	public GeometryObject(int x, int y, int w, int h, int color) {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.color = color;

		reloadPoints();
	}

	public abstract void loadPoints();
	
	public void reloadPoints() {
		loadPoints();

		points_array = new int[points.size()][];
		for (int i = 0; i < points.size(); i++) {
			Point p = points.get(i);
			points_array[i] = new int[] { p.x, p.y };
		}
	}

	public abstract boolean collision(int x, int y, int w, int h);

	public abstract boolean collision(int x, int y);

	public boolean collision(GeometryObject other) {
		return collision(other, 0, 0);
	}

	public boolean collision(GeometryObject other, int x, int y) {
		int xx = other.x - x;
		int yy = other.y - y;
		for(int[] p : other.getPoints()) {
			if(collision(p[0] + xx, p[1] + yy)) return true;
		}
		return false;
	}

	public void render(Renderer re) {
		re.drawGeometryObject(this);
	}

	public int getWidth() {
		return w;
	}

	public void setWidth(int w) {
		this.w = w;
	}

	public int getHeight() {
		return h;
	}

	public void setHeight(int h) {
		this.h = h;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void move(int x, int y) {
		this.x += x;
		this.y += y;
	}

	public int[][] getPoints() {
		return points_array;
	}

}
