package ch.aiko.engine.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

import ch.aiko.engine.geometry.GeometryObject;
import ch.aiko.engine.sprite.Sprite;

public class Renderer {

	private int[] clearPixels;
	private PixelImage pixelImg;
	private Screen screen;
	private boolean supportAlpha = true;
	private int xOffset, yOffset;
	public boolean needsReset = true;

	protected Renderer(Screen screen) {
		this.pixelImg = screen.getImage();
		this.screen = screen;
	}

	public void setHaveAlpha(boolean b) {
		supportAlpha = b;
	}

	public void clear() {
		if (!needsReset) {
			needsReset = true;
			return;
		}
		if (clearPixels == null) Arrays.fill(pixelImg.getPixels(), 0xFF000000);
		else for (int i = 0; i < clearPixels.length; i++) {
			int[] pi = pixelImg.getPixels();
			pi[i] = clearPixels[i];
		}
	}

	public void clear(int color) {
		if (!needsReset) {
			needsReset = true;
			return;
		}
		if (clearPixels == null) Arrays.fill(pixelImg.getPixels(), color);
		else for (int i = 0; i < clearPixels.length; i++) {
			int[] pi = pixelImg.getPixels();
			pi[i] = clearPixels[i];
		}
	}

	public void removeClearImage() {
		clearPixels = null;
	}

	public void setClearImage(BufferedImage img) {
		if (img.getWidth() != getWidth() || img.getHeight() != getHeight()) {
			img = toBufferedImage(img.getScaledInstance(getWidth(), getHeight(), BufferedImage.SCALE_SMOOTH));
		}
		int[] pixels = new int[img.getWidth() * img.getHeight()];
		pixels = img.getRGB(0, 0, img.getWidth(), img.getHeight(), pixels, 0, img.getWidth());
		clearPixels = pixels;
	}

	public void setClearPixels(int[] pix) {
		if (pix.length != getWidth() * getHeight()) {
			System.err.println("Size doesn't match!");
			return;
		}
		clearPixels = pix;
	}

	private BufferedImage toBufferedImage(Image img) {
		if (img instanceof BufferedImage) return (BufferedImage) img;
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();
		return bimage;
	}

	public int[] getPixels() {
		return pixelImg.getPixels();
	}

	public int getWidth() {
		return (int) pixelImg.getWidth();
	}

	public void scale(double x, double y) {
		pixelImg = new PixelImage(pixelImg.getWidth() * x, pixelImg.getHeight() * y);
		screen.pixelImg = pixelImg;
	}

	public int getHeight() {
		return (int) pixelImg.getHeight();
	}

	public int getSize() {
		return (int) (pixelImg.getWidth() * pixelImg.getHeight());
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
		if (x + w < 0 || y + h < 0 || x > getWidth() || y > getHeight()) return;
		if (!supportAlpha) col |= 0xFF000000;
		for (int xx = x; xx <= x + w; xx++) {
			for (int yy = y; yy <= y + h; yy++) {
				pixelImg.setPixel(xx, yy, col);
			}
		}
	}

	public void drawImage(BufferedImage img, int x, int y) {
		x += xOffset;
		y += yOffset;
		if (x + img.getWidth() < 0 || y + img.getHeight() < 0 || x > getWidth() || y > getHeight()) return;
		int[] pixels = ((DataBufferInt) (img.getRaster().getDataBuffer())).getData();
		if (pixels == null) img.getRGB(0, 0, img.getWidth(), img.getHeight(), pixels, 0, img.getWidth());
		for (int xx = 0; xx < img.getWidth() && xx + x < getWidth(); xx++) {
			for (int yy = 0; yy < img.getHeight() && yy + y < getHeight(); yy++) {
				if (((pixels[xx + yy * img.getWidth()] >> 24) & 0xFF) != 0) pixelImg.setPixel(xx + x, yy + y, pixels[xx + yy * img.getWidth()]);
			}
		}
	}

	public void drawImage(BufferedImage img, int x, int y, boolean renderNoAlpha) {
		x += xOffset;
		y += yOffset;
		if (x + img.getWidth() < 0 || y + img.getHeight() < 0 || x > getWidth() || y > getHeight()) return;
		int[] pixels = ((DataBufferInt) (img.getRaster().getDataBuffer())).getData();
		if (pixels == null) img.getRGB(0, 0, img.getWidth(), img.getHeight(), pixels, 0, img.getWidth());
		for (int xx = 0; xx < img.getWidth() && xx + x < getWidth(); xx++) {
			for (int yy = 0; yy < img.getHeight() && yy + y < getHeight(); yy++) {
				if (!renderNoAlpha && ((pixels[xx + yy * img.getWidth()] >> 24) & 0xFF) != 0) pixelImg.setPixel(xx + x, yy + y, pixels[xx + yy * img.getWidth()]);
			}
		}
	}

	public void drawSprite(Sprite s, int x, int y) {
		int[] pixels = s.getPixels();
		x += xOffset;
		y += yOffset;
		if (x + s.getWidth() < 0 || y + s.getHeight() < 0 || x > getWidth() || y > getHeight()) return;
		if (pixels == null) return;
		for (int xx = 0; xx < s.getWidth() && xx + x < getWidth(); xx++) {
			for (int yy = 0; yy < s.getHeight() && yy + y < getHeight(); yy++) {
				if (((pixels[xx + yy * s.getWidth()] >> 24) & 0xFF) != 0) pixelImg.setPixel(xx + x, yy + y, pixels[xx + yy * s.getWidth()]);
			}
		}
	}

	public void drawText(String text, String font, int fontsize, int fontmodifiers, int x, int y, int col) {
		Font f = new Font(font, fontmodifiers, fontsize);
		drawText(text, f, x, y, col);
	}

	public void drawText(String text, Font f, int x, int y, int col) {
		if (!supportAlpha) col |= 0xFF000000;
		if (text.equalsIgnoreCase("")) return;
		FontMetrics metrics = screen.getGraphics().getFontMetrics(f);
		Rectangle2D r = metrics.getStringBounds(text, screen.getGraphics());
		if(r.getWidth() <= 0 || r.getHeight() <= 0) return;
		BufferedImage img = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage((int) r.getWidth() * 2, (int) r.getHeight() * 2, Transparency.TRANSLUCENT);
		Graphics g = img.getGraphics();
		g.setColor(new Color(col));
		g.setFont(f);
		g.drawString(text, 0, (int) r.getHeight());
		drawImage(img, x, y);
	}

	public void drawRect(int x, int y, int w, int h, int color) {
		x += xOffset;
		y += yOffset;
		if (!supportAlpha) color |= 0xFF000000;
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
		if (!supportAlpha) color |= 0xFF000000;
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
		if (!supportAlpha) color |= 0xFF000000;
		for (int xx = x - r; xx <= x + r; xx++) {
			for (int yy = y - r; yy <= y + r; yy++) {
				if ((xx - x) * (xx - x) + (yy - y) * (yy - y) <= r * r) pixelImg.setPixel(xx, yy, color);
			}
		}
	}

	public void drawLine(int xs, int ys, int xe, int ye, int color) {
		drawLine(xs, ys, xe, ys, color, 1);
	}

	public void drawLine(int xStart, int yStart, int xEnd, int yEnd, int color, int thickness) {
		if (xStart > xEnd) {
			int xx = xEnd;
			xEnd = xStart;
			xStart = xx;
		}
		if (yStart > yEnd) {
			int yy = yEnd;
			yEnd = yStart;
			yStart = yy;
		}
		if (!supportAlpha) color |= 0xFF000000;
		float m = (float) (yEnd - yStart) / (float) (xEnd - xStart);
		float b = (float) yStart - (m * (float) xStart);
		for (int xx = xStart; xx <= xEnd; xx++) {
			float yfor = m * xx + b;
			int l = thickness;
			for (int i = thickness / 2; l > 0; i--) {
				l--;
				pixelImg.setPixel(xx + xOffset, (int) yfor + i + yOffset, color);
			}
		}
	}

	public void drawOval(int x, int y, int w, int h, int color, int thickness) {
		if (!supportAlpha) color |= 0xFF000000;
		BufferedImage gr = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(w, h, Transparency.TRANSLUCENT);
		Graphics g = gr.getGraphics();
		g.setColor(new Color(color));
		g.fillOval(0, 0, w, h);
		g.setColor(new Color(0));
		g.fillOval(thickness, thickness, w - 2 * thickness, h - 2 * thickness);
		drawImage(gr, x, y);
	}

	public void fillOval(int x, int y, int w, int h, int color) {
		if (!supportAlpha) color |= 0xFF000000;
		BufferedImage gr = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(w, h, Transparency.TRANSLUCENT);
		Graphics g = gr.getGraphics();
		g.setColor(new Color(color));
		g.fillOval(0, 0, w, h);
		drawImage(gr, x, y);
	}

	public void drawOnGraphics(GraphicRenderable rendr, int x, int y) {
		BufferedImage gr = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(rendr.getWidth(), rendr.getHeight(), Transparency.TRANSLUCENT);
		Graphics g = gr.getGraphics();
		rendr.render(g);
		drawImage(gr, x, y);
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

	public int getXOffset() {
		return xOffset;
	}

	public int getYOffset() {
		return yOffset;
	}
}