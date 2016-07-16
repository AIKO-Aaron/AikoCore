package ch.aiko.engine;

public class AikoCore {

	public static final int MAJOR = 0;
	public static final int MINOR = 1;
	public static final int PATCH = 1;
	public static final char TREE = 'B';

	public static final String getVersion() {
		return "AikoCore version: " + MAJOR + "." + MINOR + "." + PATCH + TREE;
	}

	public static final boolean DEBUG = false;

}
