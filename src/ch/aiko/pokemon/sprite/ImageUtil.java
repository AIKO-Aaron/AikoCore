package ch.aiko.pokemon.sprite;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

class ImageUtil {

	/**
	 * Loads an Image from a given File and returns it as BufferedImage
	 * 
	 * @param f
	 *            The File which is a picture
	 * @return The BufferedImage from the File
	 */
	public static BufferedImage loadImage(File f) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(f);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("File not found" + f.getAbsolutePath());
		}
		return img;
	}



	/**
	 * Second Method to load an Image in your ClassPath. Only use other when you have to!
	 * 
	 * @param path
	 *            The Path to the Image from your classPath ( Don't forget the slash at the beginning)
	 * @return The BufferedImage which was loaded
	 * @see LoadImageInClassPath
	 */
	public static BufferedImage loadImageInClassPath(String path) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(ImageUtil.class.getResource(path));
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("File not found: " + path);
		}
		return img;
	}

	/**
	 * Gets all Pixels from the Picture and returns it as one-dimensional Array
	 * 
	 * @param img
	 *            The Picture which is getting into the pixel array
	 * @return The Array of Pixels
	 */
	public static int[] getPixels(BufferedImage img) {
		int[] ret = new int[img.getWidth() * img.getHeight()];
		ret = img.getRGB(0, 0, img.getWidth(), img.getHeight(), ret, 0, img.getWidth());
		return ret;
	}

	/**
	 * Gets all Pixels from the Picture and returns it as one-dimensional Array
	 * 
	 * @param img
	 *            The Picture which is getting into the pixel array
	 * @return The Array of Pixels
	 */
	public static int[] getPixels(Image i) {
		BufferedImage img = toBufferedImage(i);

		return getPixels(img);
	}

	/**
	 * Converts a given Image into a BufferedImage
	 *
	 * @param img
	 *            The Image to be converted
	 * @return The converted BufferedImage
	 */
	public static BufferedImage toBufferedImage(Image img) {
		if (img instanceof BufferedImage) return (BufferedImage) img;

		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();
		return bimage;
	}

	/**
	 * Creates a BufferedImage from pixels
	 * 
	 * @param pixels
	 *            All pixels
	 * @param width
	 *            The Width of the Image. If this isn't right the picture looks weird
	 * @param height
	 *            The Height of the Image. If this isn't right the picture looks weird
	 * @return The BufferedImage from all pixels
	 */
	public static BufferedImage loadImage(int[] pixels, int width, int height) {
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		img.setRGB(0, 0, width, height, pixels, 0, width);
		return img;
	}

	public static BufferedImage loadImageFromWebsite(URL website) {
		try {
			return ImageIO.read(website);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static BufferedImage loadImageFromWebsite(String website) {
		try {
			return ImageIO.read(new URL(website));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Gets the RGB value of a certain Pixel
	 * 
	 * @param img
	 *            The Image which the pixel should be gotten
	 * @param x
	 *            The X Coordinate
	 * @param y
	 *            The Y Coordinate
	 * @return The RGB Value of the Pixel X/Y
	 */
	public static int getRGB(BufferedImage img, int x, int y) {
		return img.getRGB(x, y);
	}

	/**
	 * Sets the RGB Value of a certain Pixel
	 * 
	 * @param img
	 *            The Image which should be returned after the Color of the Pixel has changed
	 * @param x
	 *            The X Coordinate
	 * @param y
	 *            The Y Coordinate
	 * @param pixelColor
	 *            The Color, which the new Pixel should get
	 * @return
	 */
	public static BufferedImage setRGB(BufferedImage img, int x, int y, int pixelColor) {
		img.setRGB(x, y, pixelColor);
		return img;
	}

	/**
	 * gets the Width of the Screen
	 * 
	 * @return The Width of the Screen
	 */
	public static int getScreenWidth() {
		return Toolkit.getDefaultToolkit().getScreenSize().width;
	}

	/**
	 * gets the Height of the Screen
	 * 
	 * @return The Height of the Screen
	 */
	public static int getScreenHeight() {
		return Toolkit.getDefaultToolkit().getScreenSize().height;
	}

	/**
	 * Takes a ScreenCapture over the entire Screen
	 * 
	 * @return A BufferedImage from the ScreenCapture
	 */
	public static BufferedImage takeScreenCapture() {
		BufferedImage img = null;
		try {
			Robot r = new Robot();
			img = r.createScreenCapture(new Rectangle(0, 0, getScreenWidth(), getScreenHeight()));
		} catch (AWTException e) {
			e.printStackTrace();
		}

		return img;
	}

	/**
	 * Takes a ScreenCapture over the entire Screen
	 * 
	 * @param x
	 *            The X Coordinate where the Capture should start
	 * @param y
	 *            The Y Coordinate where the Capture should start
	 * @param width
	 *            The width of the Capture
	 * @param height
	 *            The Height of the Capture
	 * @return A BufferedImage from the ScreenCapture
	 */
	public static BufferedImage takeScreenCapture(int x, int y, int width, int height) {
		BufferedImage img = null;
		try {
			Robot r = new Robot();
			img = r.createScreenCapture(new Rectangle(x, y, width + x, height + y));
		} catch (AWTException e) {
			e.printStackTrace();
		}

		return img;
	}

	/**
	 * Creates a resized instance of the Image Caution! Does not copy Alpha Channels
	 * 
	 * @param img
	 *            The old Image
	 * @param newWidth
	 *            The new Width of the Image
	 * @param newHeight
	 *            The new Height of the Image
	 * @return The Resized Instance of the Image
	 */
	public static BufferedImage resize(BufferedImage img, int newWidth, int newHeight, int color) {
		BufferedImage newImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = newImg.createGraphics();

		g.setColor(new Color(color));
		g.drawImage(img, 0, 0, newWidth, newHeight, 0, 0, img.getWidth(), img.getHeight(), new Color(color), null);

		return newImg;
	}

	public static BufferedImage resize(BufferedImage img, int newWidth, int newHeight) {
		BufferedImage newImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = newImg.createGraphics();

		g.drawImage(img, 0, 0, newWidth, newHeight, null);

		return newImg;
	}

	public static BufferedImage resize2(BufferedImage img, float nw, float nh) {
		AffineTransform af = new AffineTransform();
		af.scale(nw, nh);

		AffineTransformOp operation = new AffineTransformOp(af, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		BufferedImage bufferedThumb = operation.filter(img, null);
		return bufferedThumb;
	}

	public static BufferedImage takeScreenCaptureAll() {
		Rectangle screenRect = new Rectangle(0, 0, 0, 0);
		for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
			screenRect = screenRect.union(gd.getDefaultConfiguration().getBounds());
		}
		try {
			return new Robot().createScreenCapture(screenRect);
		} catch (AWTException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static BufferedImage takeScreenCapture(int monitor) {
		monitor %= getNumberMonitors();
		Rectangle screenRect = new Rectangle(0, 0, 0, 0);
		int i = 0;
		for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
			if (i == monitor) {
				screenRect = gd.getDefaultConfiguration().getBounds();
			}
			i++;
		}
		try {
			return new Robot().createScreenCapture(screenRect);
		} catch (AWTException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static int getNumberMonitors() {
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length;
	}

	public static BufferedImage replaceColor(BufferedImage src, int oldCol, int newCol) {
		int[] pixels = new int[src.getWidth() * src.getHeight()];
		pixels = src.getRGB(0, 0, src.getWidth(), src.getHeight(), pixels, 0, src.getWidth());
		for(int i = 0; i < pixels.length; i++) {
			if(pixels[i] == oldCol) pixels[i] = newCol;
		}
		src.setRGB(0, 0, src.getWidth(), src.getHeight(), pixels, 0, src.getWidth());
		return src;
	}
}
