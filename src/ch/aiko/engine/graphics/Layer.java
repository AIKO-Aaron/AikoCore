package ch.aiko.engine.graphics;

import ch.aiko.engine.input.Input;

public abstract class Layer {

	/**
	 * Creates a new Layer with the given parameters
	 * 
	 * @param r
	 *            The Renderable (Drawing method)
	 * @param u
	 *            The Updatable (Update method)
	 * @param level
	 *            The position of this layer (top / bottom --> higher numbers are more on the top) can be negative
	 * @param name
	 *            The name of the Layer. If you want to find it in another object, it should be unique
	 * @param stopsRendering
	 *            If this layer prevents the lower layers from being rendered
	 * @param stopsUpdating
	 *            If this layer prevents the lower layers from being updated
	 * @return
	 */
	public static Layer createLayer(Renderable r, Updatable u, int level, String name, boolean stopsRendering, boolean stopsUpdating, boolean needsInput) {
		return new Layer(needsInput) {
			public int getLevel() {
				return level;
			}

			public String getName() {
				return name;
			}

			public boolean stopsRendering() {
				return stopsRendering;
			}

			public boolean stopsUpdating() {
				return stopsUpdating;
			}

			public Renderable getRenderable() {
				return r;
			}

			public Updatable getUpdatable() {
				return u;
			}

			public void render(Renderer renderer) {
				if (r != null) r.render(renderer);
			}

			public void update(Screen screen, Layer l) {
				if (u != null) u.update(screen, l);
			}
		};
	}

	protected Layer parent;
	protected Input input;
	protected boolean isOpen = false;
	protected boolean needsInput = true;

	public Layer() {
		parent = null;
	}

	public Layer(boolean needs) {
		parent = null;
		needsInput = needs;
	}

	public Layer(Screen s, Layer parent) {
		this.parent = parent;
	}

	/**
	 * Wether or not the layer is inside another layer (LayerContainer) or not (Screen)
	 * 
	 * @return If the Layer is inside a LayerContainer
	 */
	public boolean isSubLayer() {
		return parent != null;
	}

	/**
	 * Only works if parent is not a screen In general: only works if this layer is inside a layercontainer
	 * 
	 * @return The parent if available
	 */
	public Layer getParent() {
		return parent;
	}

	public void setParent(Layer parent) {
		this.parent = parent;
	}

	public abstract Renderable getRenderable();

	public abstract Updatable getUpdatable();

	public abstract int getLevel();

	public abstract boolean stopsRendering();

	public abstract boolean stopsUpdating();

	public abstract String getName();

	public void render(Renderer r) {
		getRenderable().render(r);
	}

	public void update(Screen s, Layer l) {
		getUpdatable().update(s, l);
	}

	public String toString() {
		return "Level [level:" + getLevel() + "]";
	}

	public void onOpen() {}

	public void onClose() {}

	public void onOpen(Screen s) {
		if (needsInput) input = new Input(s);
		isOpen = true;
	}

	public void onClose(Screen s) {
		isOpen = false;
		input.remove(s);
	}

	public void setNeedsInput(boolean b) {
		needsInput = b;
	}

	public boolean needsInput() {
		return needsInput;
	}

	public int getMouseXInFrame(Screen screen) {
		return input == null ? 0 : input.getMouseX() * screen.getRenderer().getWidth() / screen.getWidth();
	}

	public int getMouseYInFrame(Screen screen) {
		return input == null ? 0 : input.getMouseY() * screen.getRenderer().getHeight() / screen.getHeight();
	}

	public boolean isMouseKeyPressed(int keyCode) {
		return input == null ? false : input.isMouseKeyPressed(keyCode);
	}

	public boolean popMouseKey(int keyCode) {
		return input == null ? false : input.popMouseKey(keyCode);
	}

	public boolean isKeyPressed(int keyCode) {
		return input == null ? false : input.isKeyPressed(keyCode);
	}

	public boolean popKeyPressed(int keyCode) {
		return input == null ? false : input.popKeyPressed(keyCode);
	}

	public Input getInput() {
		return input;
	}

	public void setInput(Input input) {
		this.input = input;
	}

}
