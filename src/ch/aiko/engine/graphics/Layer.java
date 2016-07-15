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

	public Layer() {}

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
