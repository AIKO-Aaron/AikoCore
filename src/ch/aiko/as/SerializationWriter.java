package ch.aiko.as;

public class SerializationWriter {

	public static final byte[] HEADER = "SOASF".getBytes();
	public static final short VERSION = 0x0100;
	public static final byte flags = 0;
	public static final byte[] FOOTER = "EASF".getBytes();

	public static int writeBytes(byte[] dest, int pointer, byte[] value) {
		assert (dest.length > pointer + value.length + 1);
		for (byte b : value)
			dest[pointer++] = b;
		return pointer;
	}

	public static int writeBytes(byte[] dest, int pointer, short[] value) {
		assert (dest.length > pointer + value.length + 1);
		for (short b : value)
			pointer = writeBytes(dest, pointer, b);
		return pointer;
	}

	public static int writeBytes(byte[] dest, int pointer, char[] value) {
		assert (dest.length > pointer + value.length + 1);
		for (char b : value)
			pointer = writeBytes(dest, pointer, b);
		return pointer;
	}

	public static int writeBytes(byte[] dest, int pointer, int[] value) {
		assert (dest.length > pointer + value.length + 1);
		for (int b : value)
			pointer = writeBytes(dest, pointer, b);
		return pointer;
	}

	public static int writeBytes(byte[] dest, int pointer, long[] value) {
		assert (dest.length > pointer + value.length + 1);
		for (long b : value)
			pointer = writeBytes(dest, pointer, b);
		return pointer;
	}

	public static int writeBytes(byte[] dest, int pointer, float[] value) {
		assert (dest.length > pointer + value.length + 1);
		for (float b : value)
			pointer = writeBytes(dest, pointer, b);
		return pointer;
	}

	public static int writeBytes(byte[] dest, int pointer, double[] value) {
		assert (dest.length > pointer + value.length + 1);
		for (double b : value)
			pointer = writeBytes(dest, pointer, b);
		return pointer;
	}

	public static int writeBytes(byte[] dest, int pointer, String[] value) {
		for (String s : value)
			pointer = writeBytes(dest, pointer, s);
		return pointer;
	}

	public static int writeBytes(byte[] dest, int pointer, byte value) {
		assert (dest.length > pointer + 1);
		dest[pointer++] = value;
		return pointer;
	}

	public static int writeBytes(byte[] dest, int pointer, short value) {
		assert (dest.length > pointer + 2);
		dest[pointer++] = (byte) ((value >> 8) & 0xFF);
		dest[pointer++] = (byte) ((value >> 0) & 0xFF);
		return pointer;
	}

	public static int writeBytes(byte[] dest, int pointer, char value) {
		assert (dest.length > pointer + 2);
		dest[pointer++] = (byte) ((value >> 8) & 0xFF);
		dest[pointer++] = (byte) ((value >> 0) & 0xFF);
		return pointer;
	}

	public static int writeBytes(byte[] dest, int pointer, int value) {
		assert (dest.length > pointer + 4);
		dest[pointer++] = (byte) ((value >> 24) & 0xFF);
		dest[pointer++] = (byte) ((value >> 16) & 0xFF);
		dest[pointer++] = (byte) ((value >> 8) & 0xFF);
		dest[pointer++] = (byte) ((value >> 0) & 0xFF);
		return pointer;
	}

	public static int writeBytes(byte[] dest, int pointer, long value) {
		assert (dest.length > pointer + 8);
		dest[pointer++] = (byte) ((value >> 56) & 0xFF);
		dest[pointer++] = (byte) ((value >> 48) & 0xFF);
		dest[pointer++] = (byte) ((value >> 40) & 0xFF);
		dest[pointer++] = (byte) ((value >> 32) & 0xFF);
		dest[pointer++] = (byte) ((value >> 24) & 0xFF);
		dest[pointer++] = (byte) ((value >> 16) & 0xFF);
		dest[pointer++] = (byte) ((value >> 8) & 0xFF);
		dest[pointer++] = (byte) ((value >> 0) & 0xFF);
		return pointer;
	}

	public static int writeBytes(byte[] dest, int pointer, float val) {
		assert (dest.length > pointer + 4);
		int value = Float.floatToIntBits(val);
		return writeBytes(dest, pointer, value);
	}

	public static int writeBytes(byte[] dest, int pointer, double val) {
		assert (dest.length > pointer + 8);
		long value = Double.doubleToLongBits(val);
		return writeBytes(dest, pointer, value);
	}

	public static int writeBytes(byte[] dest, int pointer, boolean value) {
		assert (dest.length > pointer + 1);
		dest[pointer++] = (byte) (value ? 1 : 0);
		return pointer;
	}

	public static int writeBytes(byte[] dest, int pointer, String value) {
		pointer = writeBytes(dest, pointer, (short) value.length());
		pointer = writeBytes(dest, pointer, value.getBytes());
		return pointer;
	}

	public static int writeBytes(byte[] dest, int pointer, boolean[] bitfield) {
		int numOfBytes = 1;
		int ind = 0;
		int i = 0;
		while (ind < bitfield.length) {
			i += (bitfield[i] ? 1 : 0) << i;
			ind++;
			if (ind % 8 == 0) numOfBytes++;
		}
		assert (dest.length > pointer + numOfBytes);
		for (int l = 0; l < numOfBytes; l++) {
			dest[pointer++] = (byte) ((i >> 8 * l) & 0xFF);
		}
		return pointer;
	}
}
