package ch.aiko.engine.graphics;

public class LayerBuilder {

	private Renderable r = (Renderer r) -> nul();
	private Updatable u = (Screen s) -> nul();
	private int layer;
	private boolean stopsRendering = false;
	private boolean stopsUpdating = false;

	public LayerBuilder() {}

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

	public Layer toLayer() {
		return Layer.createLayer(r, u, layer, stopsRendering, stopsUpdating);
	}
	
	public LayerContainer toContainer() {
		return LayerContainer.create(layer, stopsRendering, stopsUpdating);
	}

}
