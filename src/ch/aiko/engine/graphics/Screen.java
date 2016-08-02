package ch.aiko.engine.graphics;

import java.awt.AlphaComposite;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import ch.aiko.engine.command.CommandHandler;
import ch.aiko.engine.command.StopCommand;
import ch.aiko.engine.input.Input;

@SuppressWarnings("serial")
public class Screen extends Canvas {

	public int ups, fps, lastFPS, lastUPS, clearColor = 0xFF000000;
	protected Thread commandLineReader;
	protected boolean isClearing = true, init = false;
	protected PixelImage pixelImg;
	protected Renderer renderer;
	protected Input input;
	protected boolean resetOffset = true;
	protected ScheduledThreadPoolExecutor exe;
	protected ScheduledFuture<?> update, render, disp;

	private ArrayList<Layer> layers = new ArrayList<Layer>();
	public PrintStream ps = System.out;

	private int lastRendered, lastUpdated;

	public Screen(int width, int height) {
		setPreferredSize(new Dimension(width, height));
		pixelImg = new PixelImage(width, height);
		renderer = new Renderer(this);
		this.input = new Input(this);

		new StopCommand().register();

		requestFocus();
	}

	public static Screen createAndStart(int w, int h) {
		Screen s = new Screen(w, h);
		s.startThreads();
		return s;
	}

	public final void preRender() {
		try {
			BufferStrategy bs = getBufferStrategy();
			if (bs == null) {
				if (isDisplayable()) createBufferStrategy(3);
				return;
			}
			Graphics g = bs.getDrawGraphics();

			if (g instanceof Graphics2D) {
				((Graphics2D) g).setComposite(AlphaComposite.Src);
			}

			++fps;

			if (isClearing) renderer.clear(clearColor);

			for (int i = lastRendered >= layers.size() ? layers.size() - 1 : lastRendered; i >= 0; i--) {
				if (resetOffset) renderer.setOffset(0, 0);
				if (layers.size() > i) layers.get(i).render(renderer);
			}

			g.drawImage(pixelImg.img, 0, 0, getWidth(), getHeight(), null);

			g.dispose();
			bs.show();
		} catch (Throwable t) {
			t.printStackTrace(ps);
		}
	}

	public void setResetOffset(boolean b) {
		resetOffset = b;
	}

	public final void preUpdate() {
		try {
			if (!hasFocus() && !init) requestFocus();
			else if (!init) init = true;
			++ups;
			for (int i = lastUpdated >= layers.size() ? layers.size() - 1 : lastUpdated; i >= 0; i--) {
				if (layers.size() > i) layers.get(i).update(this);
			}
		} catch (Throwable t) {
			t.printStackTrace(ps);
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
		ScheduledThreadPoolExecutor exe = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(3);
		update = exe.scheduleAtFixedRate(() -> preUpdate(), 0, 1000000000 / 60, TimeUnit.NANOSECONDS);
		render = exe.scheduleAtFixedRate(() -> preRender(), 1, 1, TimeUnit.NANOSECONDS);
		disp = exe.scheduleAtFixedRate(() -> {
			lastFPS = fps;
			lastUPS = ups;

			ps.println("FPS: " + fps + ", UPS: " + ups);
			fps = 0;
			ups = 0;
		} , 0, 1, TimeUnit.SECONDS);
		return this;
	}

	public Screen startCommandLineReader() {
		commandLineReader = new Thread(() -> {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			while (isVisible()) {
				try {
					String line = reader.readLine();
					if (line == null || line.trim().replace(" ", "").equalsIgnoreCase("")) continue;
					executeCommand(line, 5);
				} catch (Exception e) {
					e.printStackTrace(ps);
				}
			}
		});
		commandLineReader.start();
		return this;
	}

	public void executeCommand(String line, int perms) {
		CommandHandler.performAction(line, perms, this);
	}

	/**
	 * Stops the application from running and returns void
	 */
	public void stopThreads() {
		render.cancel(false);
		update.cancel(false);
		disp.cancel(true);
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
		l.setParent(null);
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

		l.onOpen();

		return l;
	}

	public Layer getTopLayer(String name) {
		for (int i = layers.size() - 1; i >= 0; i--) {
			if (layers.get(i).getName().equals(name)) return layers.get(i);
		}
		return null;
	}

	public ArrayList<Layer> getLayers(String name) {
		ArrayList<Layer> ret = new ArrayList<Layer>();
		for (int i = layers.size() - 1; i >= 0; i--) {
			if (layers.get(i).getName().equals(name)) ret.add(layers.get(i));
		}
		return ret;
	}

	/**
	 * Remove the layer l from the stack, so it doesn't get rendered and updated anymore
	 * 
	 * @param l
	 */
	public void removeLayer(Layer l) {
		l.onClose();
		if (layers.contains(l)) layers.remove(l);
		lastRendered = getLowestRendered();
		lastUpdated = getLowestUpdated();
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
			if (layers.get(i).getRenderable() == r) {
				layers.get(i).onClose();
				layers.remove(i);
			}
		}
		lastRendered = getLowestRendered();
		lastUpdated = getLowestUpdated();
	}

	public void removeUpdatable(Updatable r) {
		for (int i = 0; i < layers.size(); i++) {
			if (layers.get(i).getUpdatable() == r) {
				layers.get(i).onClose();
				layers.remove(i);
			}
		}
		lastRendered = getLowestRendered();
		lastUpdated = getLowestUpdated();
	}

	/**
	 * Searches for a layer with the renderable r
	 * 
	 * @param r
	 *            The renderable of the layer
	 * @return The layer or null if no layer was found
	 */
	public Layer getLayer(Renderable r) {
		for (int i = 0; i < layers.size(); i++) {
			Layer l = layers.get(i);
			if (l.getRenderable() == r) return l;
			if (l instanceof LayerContainer) {
				Layer found = ((LayerContainer) l).getLayer(r);
				if (found != null) return found;
			}
		}
		return null;
	}

	/**
	 * Searches for a layer with the updatable u
	 * 
	 * @param u
	 *            The updatable of the layer
	 * @return The layer or null if no layer was found
	 */
	public Layer getLayer(Updatable u) {
		for (int i = 0; i < layers.size(); i++) {
			Layer l = layers.get(i);
			if (l.getUpdatable() == u) return l;
			if (l instanceof LayerContainer) {
				Layer found = ((LayerContainer) l).getLayer(u);
				if (found != null) return found;
			}
		}
		return null;
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

	public int getMouseXInFrame() {
		return input.getMouseX() * renderer.getWidth() / getWidth();
	}

	public int getMouseYInFrame() {
		return input.getMouseY() * renderer.getHeight() / getHeight();
	}

	public boolean isMouseKeyPressed(int keyCode) {
		return input.isMouseKeyPressed(keyCode);
	}

	public boolean popMouseKey(int keyCode) {
		return input.popMouseKey(keyCode);
	}

	public boolean isKeyPressed(int keyCode) {
		return input.isKeyPressed(keyCode);
	}

	public boolean popKeyPressed(int keyCode) {
		return input.popKeyPressed(keyCode);
	}

	public int getFrameWidth() {
		return renderer.getWidth();
	}

	public int getFrameHeight() {
		return renderer.getHeight();
	}

	public void removeAllLayers() {
		layers.clear();
		lastRendered = lastUpdated = 0;
	}
}