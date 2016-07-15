package ch.aiko.engine.graphics;

import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class PixelImage {

	private int[] pixels;
	protected BufferedImage img;
	
	public PixelImage(int width, int height) {
		img = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
		pixels = ((DataBufferInt)(img.getRaster().getDataBuffer())).getData();
	}
	
	public void setPixel(int index, int newColor) {
		if(index < 0 || index >= pixels.length) return;
		pixels[index] = newColor;
	}
	
	public void setPixel(int x, int y, int newColor) {
		if(x < 0 || x >= img.getWidth()) return;
		setPixel(x + y * img.getWidth(), newColor);
	}
	
	public int[] getPixels() {
		return pixels;
	}
	
	
	public int getWidth() {
		return img.getWidth();
	}
	
	public int getHeight() {
		return img.getHeight();
	}
}
