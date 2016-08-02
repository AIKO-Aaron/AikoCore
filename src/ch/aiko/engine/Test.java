package ch.aiko.engine;

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

import ch.aiko.engine.geometry.Circle;
import ch.aiko.engine.graphics.Layer;
import ch.aiko.engine.graphics.LayerBuilder;
import ch.aiko.engine.graphics.Renderer;
import ch.aiko.engine.graphics.Screen;
import ch.aiko.engine.graphics.Window;
import ch.aiko.engine.input.Input;

public class Test {

	private static final int menuWidth = 250, numMenuItems = 6, menuSpeed = 8;
	private static final Font f = new Font("arial", 0, 25);
	private static final Random rand = new Random();
	private static Runnable[] r = new Runnable[numMenuItems];
	private static String[] texts = new String[numMenuItems];

	private static boolean closed;
	private static int index = 0;
	private static int x, y, u = menuWidth;
	private static int speed = 6;
	private static Window w;
	private static Layer l1, l2;

	private static Circle c1;
	private static float size = 5.0F;
	private static int[] colors;

	private static ArrayList<Circle> cir = new ArrayList<Circle>();

	public static void main(String[] args) {
		for (int i = 0; i < 26; i++) {
			System.out.println((char) (i + 97));
		}
		Screen s1 = new Screen(960, 540);
		l1 = Layer.createLayer((Renderer r) -> render(r), (Screen s, Layer l) -> update(s), 2, "Test1", false, false);
		l2 = Layer.createLayer((Renderer r) -> render2(r), (Screen s, Layer l) -> update2(s), 3, "test1", false, true);
		System.out.println(new LayerBuilder().toLayer().getName());
		w = new Window("Testing-window", s1);
		w.setClearing(true);
		w.setClearColor(0xFFFFFFFF);
		w.addLayer(l1);
		w.addLayer(l2);

		colors = new int[w.getScreen().getRenderer().getSize() / 256 + 16];
		for (int i = 0; i < colors.length; i++) {
			colors[i] = rand.nextInt(0xFFFFFF) + 0xFF000000;
		}

		r[0] = () -> test1();
		r[numMenuItems - 1] = () -> closeMenu();

		c1 = new Circle(25, 25, 25, 0xFF00CC00);

		// w.getScreen().addRenderable(box2);
		w.getScreen().addRenderable(c1, 10);

		closeMenu();

		w.start();
	}

	static int tileSize = 32;

	public static void render(Renderer r) {
		// if(c1 != null) c1.render(r);
		r.drawLine(0, 0, 100, 100, 0xFF00FF00, 5);
	}

	private static void test1() {

	}

	private static void closeMenu() {
		closed = true;
	}

	public static void update(Screen r) {
		Input i = l1.getInput();
		if (i.isKeyPressed(KeyEvent.VK_UP)) y += -speed;
		if (i.isKeyPressed(KeyEvent.VK_DOWN)) y += speed;
		if (i.isKeyPressed(KeyEvent.VK_LEFT)) x += -speed;
		if (i.isKeyPressed(KeyEvent.VK_RIGHT)) x += speed;

		if (i.popKeyPressed(KeyEvent.VK_NUMPAD2)) y += 1;
		if (i.popKeyPressed(KeyEvent.VK_NUMPAD5)) y -= 1;
		if (i.popKeyPressed(KeyEvent.VK_NUMPAD3)) x += 1;
		if (i.popKeyPressed(KeyEvent.VK_NUMPAD1)) x -= 1;

		if (i.popKeyPressed(KeyEvent.VK_L)) w.getScreen().getRenderer().scale(2, 2);
		if (i.popKeyPressed(KeyEvent.VK_S)) w.getScreen().getRenderer().scale(0.5F, 0.5F);

		if (i.isKeyPressed(KeyEvent.VK_B)) {
			Circle c = (new Circle(rand.nextInt(960), rand.nextInt(540), 2 + rand.nextInt(5), rand.nextInt(0xFFFFFF) | 0xFF000000));
			cir.add(c);
			w.getScreen().addRenderable(c, 10);
		}

		for (int h = 0; h < cir.size(); h++) {
			Circle c = cir.get(h);
			if (c != null && c1.collision(c, -x, -y)) {
				w.removeRenderable(c);
				cir.remove(c);
				c1.setWidth((int) size);
				size += 0.1F;
				c1.reloadPoints();
			}
		}

		if (i.popKeyPressed(KeyEvent.VK_O)) speed += 5;
		if (i.popKeyPressed(KeyEvent.VK_K)) speed -= 5;

		if (i.popKeyPressed(KeyEvent.VK_ESCAPE)) w.quit();
		if (i.popKeyPressed(KeyEvent.VK_X)) {
			closed = !closed;
			w.addLayer(l2);
		}
		if (x == 0 && y == 0) return;

		c1.move(x, y);

		x = y = 0;
	}

	public static void render2(Renderer r) {
		int v = r.getHeight() / numMenuItems;
		for (int i = 0; i < numMenuItems; i++) {
			r.drawRect(r.getWidth() - menuWidth + u, (int) (v * i), menuWidth, (int) v, index == i ? 0xFF00CC00 : 0xFF0000FF, 5);
			r.drawText(texts[i] == null ? (" ") : texts[i], f, r.getWidth() - menuWidth + 15 + u, (int) (v * i) + v / 2 - f.getSize(), 0xFF0000FF);
		}
	}

	public static void update2(Screen s) {
		if (l2.getInput().popKeyPressed(KeyEvent.VK_ESCAPE)) closed = true;
		if (!closed) {
			if (u > 0) u -= menuSpeed;
			if (u < 0) u = 0;
		} else {
			if (u < menuWidth) u += menuSpeed;
			if (u >= menuWidth) {
				u = menuWidth;
				w.removeLayer(l2);
			}
		}

		texts[0] = "FPS: " + s.lastFPS;
		texts[1] = "UPS: " + s.lastUPS;
		if (c1 != null) texts[2] = "Circle-X: " + c1.getX();
		if (c1 != null) texts[3] = "Circle-Y: " + c1.getY();
		texts[4] = "Balls: " + cir.size();
		texts[5] = "Exit";

		if (l2.getInput().popKeyPressed(KeyEvent.VK_X)) closed = !closed;
		if (l2.getInput().popKeyPressed(KeyEvent.VK_DOWN)) index += (index >= numMenuItems - 1 ? -numMenuItems + 1 : 1);
		if (l2.getInput().popKeyPressed(KeyEvent.VK_UP)) index -= (index <= 0 ? -numMenuItems + 1 : 1);
		if (l2.getInput().popKeyPressed(KeyEvent.VK_ENTER) || l2.getInput().popKeyPressed(KeyEvent.VK_SPACE)) {
			if (r[index] != null) r[index].run();
		}
	}

}
