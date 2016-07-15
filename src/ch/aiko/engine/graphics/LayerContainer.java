package ch.aiko.engine.graphics;

public abstract class LayerContainer extends Layer {

	public static LayerContainer create(Renderable r, Updatable u, int layer, boolean stopsRender, boolean stopsUpdates) {
		return new LayerContainer() {

			@Override
			public boolean stopsUpdating() {
				return stopsUpdates;
			}

			@Override
			public boolean stopsRendering() {
				return stopsRender;
			}

			@Override
			public Updatable getUpdatable() {
				return u;
			}

			@Override
			public Renderable getRenderable() {
				return r;
			}

			@Override
			public int getLevel() {
				return 0;
			}
		};
	}
	
	public void render(Renderer r) {
		getRenderable().render(r);
	}

	public void update(Screen s) {
		getUpdatable().update(s);
	}

}
