package ch.aiko.engine.sprite;

import java.awt.image.BufferedImage;

public class SpriteSheet {

	private int[] pixels;
	private BufferedImage img;
	private int spriteWidth;
	private int spriteHeight;
	public String path;

	private int entireWidth, entireHeight, xOff, yOff;

	public SpriteSheet(String imgpath, int sW, int sH) {
		BufferedImage img = ImageUtil.loadImageInClassPath(imgpath);

		this.path = imgpath;
		this.spriteWidth = sW;
		this.spriteHeight = sH;
		this.img = img;
		pixels = new int[img.getWidth() * img.getHeight()];
		pixels = img.getRGB(0, 0, img.getWidth(), img.getHeight(), pixels, 0, img.getWidth());

		entireWidth = img.getWidth();
		entireHeight = img.getHeight();
	}

	public SpriteSheet(String imgpath, int sW, int sH, int nW, int nH) {
		BufferedImage img = ImageUtil.loadImageInClassPath(imgpath);
		this.path = imgpath;
		this.spriteWidth = nW;
		this.spriteHeight = nH;

		int width = (int) ((float) img.getWidth() / (float) sW * (float) nW);
		int height = (int) ((float) img.getHeight() / (float) sH * (float) nH);

		img = ImageUtil.resize(img, width, height);

		this.img = img;
		pixels = new int[img.getWidth() * img.getHeight()];
		pixels = img.getRGB(0, 0, img.getWidth(), img.getHeight(), pixels, 0, img.getWidth());

		entireWidth = img.getWidth();
		entireHeight = img.getHeight();
	}

	public void scale(int nW, int nH) {
		BufferedImage img = ImageUtil.loadImageInClassPath(path);
		this.spriteWidth = nW;
		this.spriteHeight = nH;
		int width = (int) ((float) img.getWidth() / (float) spriteWidth * (float) nW);
		int height = (int) ((float) img.getHeight() / (float) spriteHeight * (float) nH);

		img = ImageUtil.resize(img, width, height);

		this.img = img;
		pixels = new int[img.getWidth() * img.getHeight()];
		pixels = img.getRGB(0, 0, img.getWidth(), img.getHeight(), pixels, 0, img.getWidth());

		entireWidth = img.getWidth();
		entireHeight = img.getHeight();
	}

	public SpriteSheet offset(int x, int y) {
		this.xOff = x;
		this.yOff = y;

		return this;
	}

	public SpriteSheet(Sprite s, int w, int h) {
		this.spriteWidth = w;
		this.spriteHeight = h;
		this.img = s.getImage();
		this.pixels = s.getPixels();

		entireWidth = img.getWidth();
		entireHeight = img.getHeight();
	}

	public Sprite getSprite(int x, int y) {
		if (img == null) return null;
		int xx = x;
		int yy = y;
		x = x * spriteWidth + xOff;
		y = y * spriteHeight + yOff;
		return new Sprite(img.getRGB(x, y, spriteWidth, spriteHeight, null, 0, spriteWidth), spriteWidth, spriteHeight).getScaledInstance(spriteWidth, spriteHeight).setSuper(this, xx, yy);
	}

	public Sprite getSprite(int i) {
		int w = img.getWidth() / spriteWidth;
		int x = (i % w) * spriteWidth + xOff;
		int y = (i / w) * spriteHeight + yOff;
		int xx = (i / w);
		int yy = (i % w);
		return new Sprite(img.getRGB(x, y, spriteWidth, spriteHeight, null, 0, spriteWidth), spriteWidth, spriteHeight).getScaledInstance(spriteWidth, spriteHeight).setSuper(this, xx, yy);
	}

	public int getSpriteWidth() {
		return spriteWidth;
	}

	public int getSpriteHeight() {
		return spriteHeight;
	}

	public int getSheetWidth() {
		return entireWidth;
	}

	public int getSheetHeight() {
		return entireHeight;
	}

	public SpriteSheet removeColor(int color) {
		for (int i = 0; i < pixels.length; i++) {
			if (pixels[i] == color) {
				pixels[i] = 0x00000000;
				img.setRGB(i % entireWidth, i / entireWidth, 0x00000000);
			}
		}
		return this;
	}

	public SpriteSheet replaceColor(int color, int newC) {
		for (int i = 0; i < pixels.length; i++) {
			if (pixels[i] == color) {
				pixels[i] = newC;
				img.setRGB(i % entireWidth, i / entireWidth, newC);
			}
		}
		return this;
	}

	public int getSpriteCount() {
		// return (getSheetWidth() / getSpriteWidth()) * (getSheetWidth() / getSpriteWidth());
		return (getSheetWidth() * getSheetHeight()) / (getSpriteWidth() * getSpriteHeight());
	}
}
