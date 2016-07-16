package ch.aiko.engine.graphics;

import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class PixelImage {

	private int[] pixels;
	protected BufferedImage img;
	private double width, height;
	
	public PixelImage(double width, double height) {
		img = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage((int) width, (int) height, Transparency.TRANSLUCENT);
		this.width = width;
		this.height = height;
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
	
	
	public double getWidth() {
		return width;
	}
	
	public double getHeight() {
		return height;
	}
}
