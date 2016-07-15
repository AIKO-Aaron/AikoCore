package ch.aiko.pokemon.sprite;

import java.awt.image.BufferedImage;

import ch.aiko.engine.graphics.Renderable;
import ch.aiko.engine.graphics.Renderer;

public class Tile implements Renderable {

	public Sprite sprite;
	public int x, y;
	public boolean solid;
	public int w, h;

	public static final String SOLID = "S";
	public static final String NOT_SOLID = "V";

	public Tile(Sprite sprite, int x, int y, boolean solid) {
		this.sprite = sprite;
		this.x = x;
		this.y = y;
		this.solid = solid;
		if (sprite != null) {
			this.w = sprite.getWidth();
			this.h = sprite.getHeight();
		}
	}

	public Tile(Sprite sprite, int x, int y, int w, int h, boolean solid) {
		this.sprite = sprite.setImage((BufferedImage) sprite.getImage().getScaledInstance(w, h, BufferedImage.SCALE_SMOOTH));
		this.x = x;
		this.y = y;
		this.solid = solid;
		this.w = w;
		this.h = h;
	}

	public Tile(String t) {
		if(t == null) {
			sprite = new Sprite(0xFFFF00FF, 16, 16);
			solid = false;
			return;
		}
		solid = t.substring(0, 1).equalsIgnoreCase(SOLID);
		t = t.substring(t.indexOf("|") + 1);
		boolean isSpriteSheet = t.substring(0, 1).equalsIgnoreCase(Sprite.SPRITE_SHEET);
		t = t.substring(t.indexOf("|") + 1);
		//System.out.println(t);
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
			if(path == null || path.equalsIgnoreCase("null")) sprite = new Sprite(0xFFFF00FF, 16, 16);
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
		String t = (solid ? SOLID : NOT_SOLID) + "|" + sprite.serialize();
		return t;
	}

	public void render(Renderer renderer) {
		renderer.drawImage(sprite.getImage(), x, y);
	}

}
