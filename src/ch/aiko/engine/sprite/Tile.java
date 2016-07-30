package ch.aiko.engine.sprite;

import java.awt.image.BufferedImage;

import ch.aiko.engine.graphics.Renderable;
import ch.aiko.engine.graphics.Renderer;

public class Tile implements Renderable {

	public Sprite sprite;
	public int x, y;
	public int layer; // 0 = ground, 1 = solid (most times) --> player can go up and down
	public int w, h;

	public boolean isSolid(int x, int y, int layer) {
		return sprite.getAlpha(x, y) == 0xFF && layer < this.layer;
	}

	public Tile(Sprite sprite, int x, int y, int layer) {
		this.sprite = sprite;
		this.x = x;
		this.y = y;
		this.layer = layer;
		if (sprite != null) {
			this.w = sprite.getWidth();
			this.h = sprite.getHeight();
		}
	}

	public Tile(Sprite sprite, int x, int y, int w, int h, int layer) {
		this.sprite = sprite.setImage((BufferedImage) sprite.getImage().getScaledInstance(w, h, BufferedImage.SCALE_SMOOTH));
		this.x = x;
		this.y = y;
		this.layer = layer;
		this.w = w;
		this.h = h;
	}

	public Tile(String t) {
		if (t == null) {
			sprite = new Sprite(0xFFFF00FF, 16, 16);
			layer = 0;
			return;
		}
		layer = Integer.parseInt(t.substring(0, t.indexOf("|")));
		System.out.println(layer);
		t = t.substring(t.indexOf("|") + 1);
		boolean isSpriteSheet = t.substring(0, 1).equalsIgnoreCase(Sprite.SPRITE_SHEET);
		t = t.substring(t.indexOf("|") + 1);
		// System.out.println(t);
		if (isSpriteSheet) {
			int spw = Integer.parseInt(t.substring(0, t.indexOf(",")));
			int sph = Integer.parseInt(t.substring(t.indexOf(",") + 1, t.indexOf("|")));
			t = t.substring(t.indexOf("|") + 1);
			int sx = Integer.parseInt(t.substring(0, t.indexOf(",")));
			int sy = Integer.parseInt(t.substring(t.indexOf(",") + 1, t.indexOf("|")));
			t = t.substring(t.indexOf("|") + 1);
			String path = t;
			sprite = new SpriteSheet(path, spw, sph).getSprite(sx, sy);
		} else {
			String path = t;
			if (path == null || path.equalsIgnoreCase("null")) sprite = new Sprite(0xFFFF00FF, 16, 16);
			else sprite = new Sprite(path);
		}

	}

	public int[] getPixels() {
		if (sprite == null) return new int[0];
		return sprite.getPixels();
	}

	public int getWidth() {
		return w;
	}

	public int getHeight() {
		return h;
	}

	public String toString() {
		String t = layer + "|" + sprite.serialize();
		return t;
	}

	public void render(Renderer renderer) {
		renderer.drawImage(sprite.getImage(), x, y);
	}

}
