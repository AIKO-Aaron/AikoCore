package ch.aiko.as;

public abstract class ASDataType {

	public String name;
	protected ASObject object;

	/**
	 * public ASDataType(ASObject c, String s) { if (c != null) init(c); else init(s); }
	 * 
	 * public ASDataType(String s) { init(s); }
	 * 
	 * public ASDataType(ASObject obj) { init(obj); }
	 */

	protected final void init(ASObject c) {
		object = c;
		if (object != null) {
			name = new String(c.name);
			load(object);
		} else init("\0\1\0");
	}

	protected final void init(String name) {
		this.name = name;
		reload();
	}

	protected final void init(ASObject obj, String name) {
		if (obj != null) init(obj);
		else init(name);
	}

	public void reload() {
		object = new ASObject(name);
		getData(object);
		load(object);
	}

	public void removeObject(String name2) {
		object.removeObject(name2);
	}

	public abstract void load(ASObject c);

	public int getSize() {
		return object.getSize();
	}

	public void reloadData() {
		object = new ASObject(new String(object.name));
		getData(object);
	}

	public abstract void getData(ASObject thisObject);

	public int getBytes(byte[] dest, int pointer) {
		return object.getBytes(dest, pointer);
	}

	public void addObject(ASObject obj) {
		object.addObject(obj);
	}

	public void addObject(ASDataType obj) {
		object.addObject(obj.object);
	}

	public ASObject toObject() {
		return object;
	}

}
