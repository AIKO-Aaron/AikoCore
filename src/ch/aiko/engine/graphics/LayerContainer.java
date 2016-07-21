package ch.aiko.engine.graphics;

import java.util.ArrayList;

public abstract class LayerContainer extends Layer implements Renderable, Updatable {

	public static LayerContainer create(int layer, String name, boolean stopsRender, boolean stopsUpdates) {
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
			public int getLevel() {
				return 0;
			}

			@Override
			public String getName() {
				return name;
			}
		};
	}

	protected ArrayList<Layer> layers = new ArrayList<Layer>();
	protected int lastRendered, lastUpdated;
	protected boolean resetOffset = true;

	public final void render(Renderer r) {
		layerRender(r);
		for (int i = lastRendered >= layers.size() ? layers.size() - 1 : lastRendered; i >= 0; i--) {
			if (resetOffset) r.setOffset(0, 0);
			if (layers.size() > i) layers.get(i).render(r);
		}
		postRender(r);
	}

	public final void update(Screen s) {
		layerUpdate(s);
		for (int i = lastUpdated >= layers.size() ? layers.size() - 1 : lastUpdated; i >= 0; i--) {
			if (layers.size() > i) layers.get(i).update(s);
		}
		postUpdate(s);
	}

	public void layerRender(Renderer r) {}

	public void layerUpdate(Screen s) {}

	public void postRender(Renderer r) {}

	public void postUpdate(Screen s) {}

	public Layer addLayer(Layer l) {
		if (l == null) return l;
		l.setParent(this);
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

	public Renderable getRenderable() {
		return this;
	}

	public Updatable getUpdatable() {
		return this;
	}

	public void removeLayer(Layer l) {
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
			if (layers.get(i).getRenderable() == r) layers.remove(i);
		}
		lastRendered = getLowestRendered();
		lastUpdated = getLowestUpdated();
	}

	public void removeUpdatable(Updatable r) {
		for (int i = 0; i < layers.size(); i++) {
			if (layers.get(i).getUpdatable() == r) layers.remove(i);
		}
		lastRendered = getLowestRendered();
		lastUpdated = getLowestUpdated();
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

	/**
	 * Searches for a layer with the renderable r
	 * 
	 * @param r
	 *            The renderable of the layer
	 * @return The layer or null if no layer was found
	 */
	public Layer getLayer(Renderable r) {
		for (int i = 0; i < layers.size(); i++) {
			if (layers.get(i).getRenderable() == r) return layers.get(i);
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
			if (layers.get(i).getUpdatable() == u) return layers.get(i);
		}
		return null;
	}

	public void removeAllLayers() {
		layers.clear();
		lastRendered = lastUpdated = 0;
	}
}
