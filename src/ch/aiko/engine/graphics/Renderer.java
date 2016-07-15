package ch.aiko.engine.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

import ch.aiko.engine.geometry.GeometryObject;

public class Renderer {
	private PixelImage pixelImg;
	private Screen screen;

	private int xOffset, yOffset;

	protected Renderer(Screen screen) {
		this.pixelImg = screen.getImage();
		this.screen = screen;
	}

	public void clear() {
		Arrays.fill(pixelImg.getPixels(), 0xFF000000);
	}

	public void clear(int color) {
		Arrays.fill(pixelImg.getPixels(), color);
	}

	public int[] getPixels() {
		return pixelImg.getPixels();
	}

	public int getWidth() {
		return pixelImg.getWidth();
	}

	public int getHeight() {
		return pixelImg.getHeight();
	}

	public int getRealWidth() {
		return screen.getWidth();
	}

	public int getRealHeight() {
		return screen.getHeight();
	}

	public Screen getScreen() {
		return screen;
	}

	public void fillRect(int x, int y, int w, int h, int col) {
		x += xOffset;
		y += yOffset;
		for (int xx = x; xx <= x + w; xx++) {
			for (int yy = y; yy <= y + h; yy++) {
				pixelImg.setPixel(xx, yy, col);
			}
		}
	}

	public void drawImage(BufferedImage img, int x, int y) {
		x += xOffset;
		y += yOffset;
		int[] pixels = ((DataBufferInt) (img.getRaster().getDataBuffer())).getData();
		if (pixels == null) img.getRGB(0, 0, img.getWidth(), img.getHeight(), pixels, 0, img.getWidth());
		for (int xx = 0; xx < img.getWidth() && xx + x < getWidth(); xx++) {
			for (int yy = 0; yy < img.getHeight() && yy + y < getHeight(); yy++) {
				if (((pixels[xx + yy * img.getWidth()] >> 24) & 0xFF) != 0) pixelImg.setPixel(xx + x, yy + y, pixels[xx + yy * img.getWidth()]);
			}
		}
	}

	public void drawText(String text, String font, int fontsize, int fontmodifiers, int x, int y, int col) {
		Font f = new Font(font, fontmodifiers, fontsize);
		drawText(text, f, x, y, col);
	}

	public void drawText(String text, Font f, int x, int y, int col) {
		x += xOffset;
		y += yOffset;
		FontMetrics metrics = screen.getGraphics().getFontMetrics(f);
		Rectangle2D r = metrics.getStringBounds(text, screen.getGraphics());
		BufferedImage img = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage((int) r.getWidth(), (int) r.getHeight() * 2, Transparency.TRANSLUCENT);
		Graphics g = img.getGraphics();
		g.setColor(new Color(col));
		g.setFont(f);
		g.drawString(text, 0, (int) r.getHeight());
		drawImage(img, x, y);
	}

	public void drawRect(int x, int y, int w, int h, int color) {
		x += xOffset;
		y += yOffset;
		for (int i = 0; i < w; i++) {
			pixelImg.setPixel(x + i, y, color);
			pixelImg.setPixel(x + i, y + h, color);
		}
		for (int i = 0; i < h; i++) {
			pixelImg.setPixel(x, y + i, color);
			pixelImg.setPixel(x + w, y + i, color);
		}
	}

	public void drawRect(int x, int y, int w, int h, int color, int lw) {
		x += xOffset;
		y += yOffset;
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < lw; j++) {
				pixelImg.setPixel(x + i, y + j, color);
				pixelImg.setPixel(x + i, y + h - j - 1, color);
			}
		}
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < lw; j++) {
				pixelImg.setPixel(x + j, y + i, color);
				pixelImg.setPixel(x + w - j - 1, y + i, color);
			}
		}
	}

	public void fillCircle(int x, int y, int r, int color) {
		x += xOffset;
		y += yOffset;
		for (int xx = x - r; xx <= x + r; xx++) {
			for (int yy = y - r; yy <= y + r; yy++) {
				if ((xx - x) * (xx - x) + (yy - y) * (yy - y) <= r * r) pixelImg.setPixel(xx, yy, color);
			}
		}
	}

	public void drawGeometryObject(GeometryObject o) {
		int x = o.getX();
		int y = o.getY();
		x += xOffset;
		y += yOffset;
		int col = o.getColor();
		int[][] ps = o.getPoints();
		for (int i = 0; i < ps.length; i++) {
			int[] p = ps[i];
			if (p != null) pixelImg.setPixel(p[0] + x, p[1] + y, 0xFF000000 | col);
		}

	}

	public void setOffset(int i, int j) {
		xOffset = i;
		yOffset = j;
	}

	public void addOffset(int i, int j) {
		xOffset += i;
		yOffset += j;
	}
}