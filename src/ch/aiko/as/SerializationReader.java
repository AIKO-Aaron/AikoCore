package ch.aiko.as;

import java.nio.ByteBuffer;

public class SerializationReader {

	public static byte getByte(byte[] bytes, int position) {
		return bytes[position];
	}

	public static byte[] readBytes(byte[] bytes, int position, int length) {
		byte[] by = new byte[length];
		for (int i = 0; i < length; i++)
			by[i] = bytes[position++];
		return by;
	}

	public static short[] readShorts(byte[] bytes, int position, int length) {
		short[] by = new short[length];
		for (int i = 0; i < length; i++) {
			by[i] = readShort(bytes, position);
			position += 2;
		}
		return by;
	}
	
	public static char[] readChars(byte[] bytes, int position, int length) {
		char[] by = new char[length];
		for (int i = 0; i < length; i++) {
			by[i] = readChar(bytes, position);
			position += 2;
		}
		return by;
	}
	
	public static int[] readIntegers(byte[] bytes, int position, int length) {
		int[] by = new int[length];
		for (int i = 0; i < length; i++) {
			by[i] = readInt(bytes, position);
			position += 4;
		}
		return by;
	}
	
	public static long[] readLongs(byte[] bytes, int position, int length) {
		long[] by = new long[length];
		for (int i = 0; i < length; i++) {
			by[i] = readLong(bytes, position);
			position += 8;
		}
		return by;
	}
	
	public static float[] readFloats(byte[] bytes, int position, int length) {
		float[] by = new float[length];
		for (int i = 0; i < length; i++) {
			by[i] = readFloat(bytes, position);
			position += 4;
		}
		return by;
	}
	
	public static double[] readDoubles(byte[] bytes, int position, int length) {
		double[] by = new double[length];
		for (int i = 0; i < length; i++) {
			by[i] = readDouble(bytes, position);
			position += 8;
		}
		return by;
	}
	
	public static boolean[] readBooleans(byte[] bytes, int position, int length) {
		boolean[] by = new boolean[length];
		for (int i = 0; i < length; i++) {
			by[i] = readBoolean(bytes, position);
			position += 1;
		}
		return by;
	}
	
	public static String[] readStrings(byte[] bytes, int position, int length) {
		String[] by = new String[length];
		for(int i = 0; i < length; i++) {
			short s = readShort(bytes, position);
			position+=2;
			by[i] = readString(bytes, position, s);
			position += s;
		}
		return by;
	}

	public static byte readByte(byte[] src, int pointer) {
		return src[pointer];
	}

	public static short readShort(byte[] src, int pointer) {
		return ByteBuffer.wrap(src, pointer, 2).getShort();
	}

	public static char readChar(byte[] src, int pointer) {
		return ByteBuffer.wrap(src, pointer, 2).getChar();
	}

	public static int readInt(byte[] src, int pointer) {
		return ByteBuffer.wrap(src, pointer, 4).getInt();
	}

	public static long readLong(byte[] src, int pointer) {
		return ByteBuffer.wrap(src, pointer, 8).getLong();
	}

	public static float readFloat(byte[] src, int pointer) {
		return ByteBuffer.wrap(src, pointer, 4).getFloat();
	}

	public static double readDouble(byte[] src, int pointer) {
		return ByteBuffer.wrap(src, pointer, 8).getDouble();
	}

	public static boolean readBoolean(byte[] src, int pointer) {
		return src[pointer] != 0;
	}

	public static String readString(byte[] bytes, int pointer, int length) {
		return new String(bytes, pointer, length);
	}

}
