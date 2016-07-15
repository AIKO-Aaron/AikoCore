package ch.aiko.engine.graphics;

import java.util.ArrayList;

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

	private ArrayList<Layer> layers = new ArrayList<Layer>();
	private int lastRendered, lastUpdated;

	public void render(Renderer r) {
		getRenderable().render(r);
		for (int i = lastRendered >= layers.size() ? layers.size() - 1 : lastRendered; i >= 0; i--) {
			r.setOffset(0, 0);
			if (layers.size() > i) layers.get(i).render(r);
		}
	}

	public void update(Screen s) {
		getUpdatable().update(s);
		for (int i = lastUpdated >= layers.size() ? layers.size() - 1 : lastUpdated; i >= 0; i--) {
			if (layers.size() > i) layers.get(i).update(s);
		}
	}

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

	public int getLowestRendered() {
		int startIndex = 0;
		for (int i = 0; i < layers.size(); i++) {
			if (layers.get(i).stopsRendering()) break;
			++startIndex;
		}
		return startIndex;
	}

	public int getLowestUpdated() {
		int startIndex = 0;
		for (int i = 0; i < layers.size(); i++) {
			if (layers.get(i).stopsUpdating()) break;
			++startIndex;
		}
		return startIndex;
	}

}
