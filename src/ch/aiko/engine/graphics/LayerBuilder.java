package ch.aiko.engine.graphics;

import java.util.Random;

public class LayerBuilder {

	private final Random rand = new Random();

	private Renderable r = (Renderer r) -> nul();
	private Updatable u = (Screen s) -> nul();
	private int layer;
	private String name = "";
	private boolean stopsRendering = false;
	private boolean stopsUpdating = false;

	public LayerBuilder() {
		int nameLength = rand.nextInt(16);
		while (nameLength-- > 0) {
			name += (char) rand.nextInt(Character.MAX_VALUE);
		}
	}

	private void nul() {}

	public LayerBuilder setRenderable(Renderable r) {
		this.r = r;
		return this;
	}

	public LayerBuilder setUpdatable(Updatable u) {
		this.u = u;
		return this;
	}

	public LayerBuilder setLayer(int layer) {
		this.layer = layer;
		return this;
	}

	public LayerBuilder setStopsRendering(boolean stop) {
		this.stopsRendering = stop;
		return this;
	}

	public LayerBuilder setStopsUpdating(boolean stop) {
		this.stopsUpdating = stop;
		return this;
	}
	
	public LayerBuilder setName(String name) {
		this.name = name;
		return this;
	}

	public Layer toLayer() {
		return Layer.createLayer(r, u, layer, name, stopsRendering, stopsUpdating);
	}

	public LayerContainer toContainer() {
		return LayerContainer.create(layer, name, stopsRendering, stopsUpdating);
	}

}
