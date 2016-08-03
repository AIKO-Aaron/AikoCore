package ch.aiko.engine.input;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.Arrays;

import ch.aiko.engine.graphics.Screen;

public class Input {

	public boolean isAvailable = true;
	public Screen screen;
	private int mouseX, mouseY, mxos, myos, mouseWheel;
	private ArrayList<Integer> mouse_pressed = new ArrayList<Integer>();
	private ArrayList<Integer> mouse_popped = new ArrayList<Integer>();
	private ArrayList<Integer> keys = new ArrayList<Integer>();
	private ArrayList<Integer> keys_popped = new ArrayList<Integer>();
	private listeners l;

	public void reset() {
		mouseX = mouseY = mxos = myos = mouseWheel = 0;
		mouse_pressed.clear();
		mouse_popped.clear();
		keys.clear();
		keys_popped.clear();
	}

	public Input(Screen w) {
		l = new listeners();

		screen = w;

		w.addKeyListener(l);
		w.addMouseListener(l);
		w.addMouseMotionListener(l);
		w.addMouseWheelListener(l);
		w.addComponentListener(l);
	}

	public boolean isKeyPressed(int keyCode) {
		return keys_popped.contains(keyCode);
	}

	public boolean popKeyPressed(int keyCode) {
		if (keys.contains(keyCode)) {
			while (keys.contains(keyCode))
				keys.remove((Object) keyCode);
			return true;
		}
		return false;
	}

	public boolean isMouseKeyPressed(int keyCode) {
		return mouse_pressed.contains(keyCode);
	}

	public boolean popMouseKey(int keyCode) {
		if (mouse_popped.contains(keyCode)) {
			while (mouse_popped.contains(keyCode))
				mouse_popped.remove((Object) keyCode);
			return true;
		}
		return false;
	}

	public int getMouseX() {
		return mouseX;
	}

	public int getMouseY() {
		return mouseY;
	}

	public int getMouseXOnScreen() {
		return mxos;
	}

	public int getMouseYOnScreen() {
		return myos;
	}

	public int getMouseWheel() {
		return mouseWheel;
	}

	public int popMouseWheel() {
		int m = mouseWheel;
		mouseWheel = 0;
		return m;
	}

	private class listeners implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener, ComponentListener {
		public void componentResized(ComponentEvent e) {}

		public void componentMoved(ComponentEvent e) {}

		public void componentShown(ComponentEvent e) {}

		public void componentHidden(ComponentEvent e) {}

		public void mouseWheelMoved(MouseWheelEvent e) {
			if (!isAvailable) return;
			mouseWheel += e.getPreciseWheelRotation();
		}

		public void mouseDragged(MouseEvent e) {
			if (!isAvailable) return;
			mouseX = e.getX();
			mouseY = e.getY();
			mxos = e.getXOnScreen();
			myos = e.getYOnScreen();
		}

		public void mouseMoved(MouseEvent e) {
			if (!isAvailable) return;
			mouseX = e.getX();
			mouseY = e.getY();
			mxos = e.getXOnScreen();
			myos = e.getYOnScreen();
		}

		public void mouseClicked(MouseEvent e) {}

		public void mousePressed(MouseEvent e) {
			if (!isAvailable) return;
			if (!mouse_popped.contains(e.getButton())) {
				mouse_pressed.add(e.getButton());
				mouse_popped.add(e.getButton());
			}
		}

		public void mouseReleased(MouseEvent e) {
			if (!isAvailable) return;
			if (mouse_pressed.contains(e.getButton())) mouse_pressed.remove((Object) e.getButton());
			if (mouse_popped.contains(e.getButton())) mouse_popped.remove((Object) e.getButton());
		}

		public void mouseEntered(MouseEvent e) {}

		public void mouseExited(MouseEvent e) {}

		public void keyTyped(KeyEvent e) {}

		public void keyPressed(KeyEvent e) {
			if (!isAvailable) return;
			if (!keys_popped.contains(e.getKeyCode())) {
				keys.add(e.getKeyCode());
				keys_popped.add(e.getKeyCode());
			}
		}

		public void keyReleased(KeyEvent e) {
			if (!isAvailable) return;
			while (keys.contains(e.getKeyCode()))
				keys.remove((Object) e.getKeyCode());
			while (keys_popped.contains(e.getKeyCode()))
				keys_popped.remove((Object) e.getKeyCode());
		}
	}

	public void remove(Screen s) {
		if (Arrays.asList(s.getKeyListeners()).contains(l)) s.removeKeyListener(l);
		if (Arrays.asList(s.getMouseListeners()).contains(l)) s.removeMouseListener(l);
		if (Arrays.asList(s.getMouseMotionListeners()).contains(l)) s.removeMouseMotionListener(l);
		if (Arrays.asList(s.getMouseWheelListeners()).contains(l)) s.removeMouseWheelListener(l);
		if (Arrays.asList(s.getComponentListeners()).contains(l)) s.removeComponentListener(l);
	}

}
