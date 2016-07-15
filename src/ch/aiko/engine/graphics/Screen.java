package ch.aiko.engine.graphics;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.io.PrintStream;
import java.util.ArrayList;

import ch.aiko.engine.input.Input;

@SuppressWarnings("serial")
public class Screen extends Canvas {

	public int ups, fps, lastFPS, lastUPS, clearColor = 0xFF000000;
	private boolean isRendering, isUpdating, isClearing = true;
	private Thread renderThread = new Thread(() -> startRendering(), "RenderThread"), updateThread = new Thread(() -> startUpdating(60), "UpdateThread");
	private PixelImage pixelImg;
	private Renderer renderer;
	private Input input;

	private ArrayList<Layer> layers = new ArrayList<Layer>();
	public PrintStream ps;

	private int lastRendered, lastUpdated;

	public Screen(int width, int height) {
		setPreferredSize(new Dimension(width, height));
		pixelImg = new PixelImage(width, height);
		renderer = new Renderer(this);

		this.input = new Input(this);

		requestFocus();
		startThreads();
	}

	public void startRendering() {
		if (isRendering) return;
		isRendering = true;

		while (isRendering) {
			fps++;
			preRender();
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void startUpdating(int d_fps) {
		if (isUpdating) return;
		isUpdating = true;

		long second = 1000000000;
		long wait_time = second / d_fps;
		long lastTime = System.nanoTime();

		while (!hasFocus()) {
			requestFocus();
		}

		while (isUpdating) {
			long start = System.nanoTime();
			preUpdate();
			ups++;
			long end = System.nanoTime();
			while (end - start < wait_time) {
				end = System.nanoTime();
			}
			if (System.nanoTime() - lastTime >= second) {
				lastFPS = fps;
				lastUPS = ups;

				ps.println("FPS: " + fps + ", UPS: " + ups);
				fps = 0;
				ups = 0;
				lastTime = System.nanoTime();
			}
		}
	}

	public final void preRender() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			if (isDisplayable()) createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();

		if (isClearing) renderer.clear(clearColor);

		for (int i = lastRendered >= layers.size() ? layers.size() - 1 : lastRendered; i >= 0; i--) {
			renderer.setOffset(0, 0);
			if(layers.size() > i) layers.get(i).render(renderer);
		}

		g.drawImage(pixelImg.img, 0, 0, getWidth(), getHeight(), null);

		g.dispose();
		bs.show();
	}

	public final void preUpdate() {
		for (int i = lastUpdated >= layers.size() ? layers.size() - 1 : lastUpdated; i >= 0; i--) {
			if(layers.size() > i) layers.get(i).update(this);
		}
	}

	/**
	 * Searches for the layer that is still getting rendered
	 * 
	 * @return The index of the lowest layer rendered (the one that disables all layers below)
	 */
	public int getLowestRendered() {
		int startIndex = 0;

		for (int i = 0; i < layers.size(); i++) {
			if (layers.get(i).stopsRendering()) break;
			++startIndex;
		}

		return startIndex;
	}

	/**
	 * Searches for the layer that is still getting updated
	 * 
	 * @return The index of the lowest layer updated (the one that disables all layers below)
	 */
	public int getLowestUpdated() {
		int startIndex = 0;

		for (int i = 0; i < layers.size(); i++) {
			if (layers.get(i).stopsUpdating()) break;
			++startIndex;
		}

		return startIndex;
	}

	/**
	 * Looks through the layers if they are in the right order (layer-levels)
	 * 
	 * @return Wether or not they are sorted
	 */
	public boolean isSorted() {
		int lastOne = Integer.MIN_VALUE;
		for (Layer l : layers) {
			int r = l.getLevel();
			if (r < lastOne) return false;
			lastOne = r;
		}
		return true;
	}

	/**
	 * Start the render and update thread and show this
	 * 
	 * @return the Screen, so you can keep modifying it
	 */
	public Screen startThreads() {
		renderThread.start();
		updateThread.start();
		return this;
	}

	/**
	 * Stops the application from running and returns void
	 */
	public void stopThreads() {
		isRendering = false;
		isUpdating = false;
	}

	public PixelImage getImage() {
		return pixelImg;
	}

	public Renderer getRenderer() {
		return renderer;
	}

	public Screen setRenderer(Renderer renderer) {
		this.renderer = renderer;
		return this;
	}

	public boolean isRendering() {
		return isRendering;
	}

	public boolean isUpdating() {
		return isUpdating;
	}

	public void setPixelImage(PixelImage pixelImg) {
		this.pixelImg = pixelImg;
	}

	/**
	 * Add a layer
	 * 
	 * @param l
	 *            The layer, which can contain renderables and updatables
	 * @return The layer so you can keep modifying it
	 */
	public Layer addLayer(Layer l) {
		if (l == null) return l;
		if (layers.size() <= 0) {
			layers.add(l);
		} else {
			for (int i = 0; i < layers.size(); i++) {
				if (layers.get(i).getLevel() <= l.getLevel()) {
					layers.add(i, l);
					break;
				}
			}
		}

		lastRendered = getLowestRendered();
		lastUpdated = getLowestUpdated();

		return l;
	}

	/**
	 * Remove the layer l from the stack, so it doesn't get rendered and updated anymore
	 * 
	 * @param l
	 */
	public void removeLayer(Layer l) {
		if (layers.contains(l)) layers.remove(l);
	}

	public Layer addRenderable(Renderable r) {
		return addRenderable(r, 0, false);
	}

	public Layer addRenderable(Renderable r, int level) {
		return addRenderable(r, level, false);
	}

	public Layer addRenderable(Renderable r, int level, boolean stopsRenders) {
		return addLayer(new LayerBuilder().setRenderable(r).setLayer(level).setStopsRendering(stopsRenders).toLayer());
	}

	public Layer addUpdatable(Updatable r, int priority, boolean stopsUpdates) {
		return addLayer(new LayerBuilder().setUpdatable(r).setLayer(priority).setStopsUpdating(stopsUpdates).toLayer());
	}

	public Layer addUpdatable(Updatable u) {
		return addUpdatable(u, 0, false);
	}

	public Layer addUpdatable(Updatable r, int level) {
		return addUpdatable(r, level, false);
	}

	public void removeRenderable(Renderable r) {
		for (int i = 0; i < layers.size(); i++) {
			if (layers.get(i).getRenderable() == r) layers.remove(i);
		}
	}

	public void removeUpdatable(Updatable r) {
		for (int i = 0; i < layers.size(); i++) {
			if (layers.get(i).getUpdatable() == r) layers.remove(i);
		}
	}

	public Input getInput() {
		return input;
	}

	public void setInput(Input input) {
		this.input = input;
	}

	public int getClearColor() {
		return clearColor;
	}

	public void setClearColor(int clearColor) {
		this.clearColor = clearColor;
	}

	public boolean isClearing() {
		return isClearing;
	}

	public void setClearing(boolean isClearing) {
		this.isClearing = isClearing;
	}
}