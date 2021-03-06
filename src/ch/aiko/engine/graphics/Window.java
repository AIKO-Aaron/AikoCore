package ch.aiko.engine.graphics;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Window extends JFrame {

	private Screen screen;

	@Deprecated
	public Window(int w, int h, String title) {
		setTitle(title);

		if (System.getProperty("os.name").contains("Mac OS X")) getRootPane().putClientProperty("apple.awt.fullscreenable", Boolean.valueOf(true));

		screen = new Screen(w, h);
		add(screen);
		pack();

		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

	}
	
	public void start() {
		screen.startThreads();
		screen.startCommandLineReader();
		setVisible(true);
	}

	public Window(String title, Screen s) {
		setTitle(title);

		if (System.getProperty("os.name").contains("Mac OS X")) getRootPane().putClientProperty("apple.awt.fullscreenable", Boolean.valueOf(true));

		screen = s;
		add(screen);
		pack();

		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	public Window setScreen(Screen s) {
		remove(screen);
		screen = s;
		add(screen);
		return this;
	}

	public void addLayer(Layer l) {
		screen.addLayer(l);
	}

	public Screen getScreen() {
		return screen;
	}

	public void setClearing(boolean b) {
		screen.setClearing(b);
	}

	public void setClearColor(int color) {
		screen.setClearColor(color);
	}

	public void quit() {
		screen.stopThreads();
		setVisible(false);
		dispose();
		System.exit(0);
	}

	public void removeLayer(Layer l2) {
		screen.removeLayer(l2);
	}

	public void removeRenderable(Renderable c) {
		screen.removeRenderable(c);
	}

}
