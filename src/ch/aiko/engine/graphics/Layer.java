package ch.aiko.engine.graphics;

public abstract class Layer {

	public static Layer createLayer(Renderable r, Updatable u, int level, boolean stopsRendering, boolean stopsUpdating) {
		return new Layer() {
			public int getLevel() {
				return level;
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
				r.render(renderer);
			}

			public void update(Screen screen) {
				u.update(screen);
			}
		};
	}

	private Layer parent;

	public Layer() {
		parent = null;
	}

	public Layer(Layer parent) {
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

	public void render(Renderer r) {
		getRenderable().render(r);
	}

	public void update(Screen s) {
		getUpdatable().update(s);
	}

	public String toString() {
		return "Level [level:" + getLevel() + "]";
	}

}
