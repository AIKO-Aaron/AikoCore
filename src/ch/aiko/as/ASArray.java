package ch.aiko.as;

import static ch.aiko.as.SerializationReader.readBooleans;
import static ch.aiko.as.SerializationReader.readByte;
import static ch.aiko.as.SerializationReader.readBytes;
import static ch.aiko.as.SerializationReader.readChars;
import static ch.aiko.as.SerializationReader.readDoubles;
import static ch.aiko.as.SerializationReader.readFloats;
import static ch.aiko.as.SerializationReader.readInt;
import static ch.aiko.as.SerializationReader.readIntegers;
import static ch.aiko.as.SerializationReader.readLongs;
import static ch.aiko.as.SerializationReader.readShort;
import static ch.aiko.as.SerializationReader.readShorts;
import static ch.aiko.as.SerializationReader.*;
import static ch.aiko.as.SerializationWriter.writeBytes;

public class ASArray {

	public static final byte CONTAINER_TYPE = ContainerTypes.ARRAY;
	public short nameLength;
	public byte[] name;
	public int size = 1 + 2 + 4 + 4 + 1;// + count * Types.getSize(type);
	public byte type;
	public int count;
	public byte[] data;

	private String[] stringData;
	private short[] shortData;
	private char[] charData;
	private int[] intData;
	private long[] longData;
	private float[] floatData;
	private double[] doubleData;
	private boolean[] booleanData;

	protected ASArray() {}

	public void setName(String name) {
		assert (name.length() < Short.MAX_VALUE);

		if (this.name != null) size -= this.name.length;

		nameLength = (short) name.length();
		this.name = name.getBytes();

		size += name.length();
	}

	public int getSize() {
		return size;
	}

	private void updateSize() {
		if (type == Types.STRING) {
			for(String s : stringData) size += s.length()+2;
		} else size += count * Types.getSize(type);
	}

	public int getBytes(byte[] dest, int pointer) {
		//assert (data.length == Types.getSize(type) && name.length == nameLength);
		pointer = writeBytes(dest, pointer, CONTAINER_TYPE);
		pointer = writeBytes(dest, pointer, nameLength);
		pointer = writeBytes(dest, pointer, name);
		pointer = writeBytes(dest, pointer, type);
		pointer = writeBytes(dest, pointer, size);
		pointer = writeBytes(dest, pointer, count);
		switch (type) {
			case Types.BYTE:
				pointer = writeBytes(dest, pointer, data);
				break;
			case Types.SHORT:
				pointer = writeBytes(dest, pointer, shortData);
				break;
			case Types.CHAR:
				pointer = writeBytes(dest, pointer, charData);
				break;
			case Types.INT:
				pointer = writeBytes(dest, pointer, intData);
				break;
			case Types.LONG:
				pointer = writeBytes(dest, pointer, longData);
				break;
			case Types.FLOAT:
				pointer = writeBytes(dest, pointer, floatData);
				break;
			case Types.DOUBLE:
				pointer = writeBytes(dest, pointer, doubleData);
				break;
			case Types.BOOLEAN:
				pointer = writeBytes(dest, pointer, booleanData);
				break;
			case Types.STRING:
				pointer = writeBytes(dest, pointer, stringData);
				break;
		}
		return pointer;
	}

	/**
	 * Experimental
	 * 
	 * @param name The name of the object
	 * @param data The Strings to store
	 * @return The ASArray with the given objects
	 */
	public static ASArray String(String name, String[] data) {
		ASArray array = new ASArray();
		array.setName(name);
		array.type = Types.STRING;
		array.count = data.length;
		array.stringData = data.clone();
		array.updateSize();
		return array;
	}
	
	public static ASArray Byte(String name, byte[] data) {
		ASArray array = new ASArray();
		array.setName(name);
		array.type = Types.BYTE;
		array.count = data.length;
		array.data = data;
		array.updateSize();
		return array;
	}

	public static ASArray Short(String name, short[] data) {
		ASArray array = new ASArray();
		array.setName(name);
		array.type = Types.SHORT;
		array.count = data.length;
		array.shortData = data;
		array.updateSize();
		return array;
	}

	public static ASArray Char(String name, char[] data) {
		ASArray array = new ASArray();
		array.setName(name);
		array.type = Types.CHAR;
		array.count = data.length;
		array.charData = data;
		array.updateSize();
		return array;
	}

	public static ASArray Integer(String name, int[] data) {
		ASArray array = new ASArray();
		array.setName(name);
		array.type = Types.INT;
		array.count = data.length;
		array.intData = data;
		array.updateSize();
		return array;
	}

	public static ASArray Long(String name, long[] data) {
		ASArray array = new ASArray();
		array.setName(name);
		array.type = Types.LONG;
		array.count = data.length;
		array.longData = data;
		array.updateSize();
		return array;
	}

	public static ASArray Float(String name, float[] data) {
		ASArray array = new ASArray();
		array.setName(name);
		array.type = Types.FLOAT;
		array.count = data.length;
		array.floatData = data;
		array.updateSize();
		return array;
	}

	public static ASArray Double(String name, double[] data) {
		ASArray array = new ASArray();
		array.setName(name);
		array.type = Types.DOUBLE;
		array.count = data.length;
		array.doubleData = data;
		array.updateSize();
		return array;
	}

	public static ASArray Boolean(String name, boolean[] data) {
		ASArray array = new ASArray();
		array.setName(name);
		array.type = Types.BOOLEAN;
		array.count = data.length;
		array.booleanData = data;
		array.updateSize();
		return array;
	}

	/**
	 * public int getBytes(byte[] dest, int pointer) { assert (data.length == Types.getSize(type) && name.length == nameLength); pointer = writeBytes(dest, pointer, CONTAINER_TYPE); pointer = writeBytes(dest, pointer, nameLength); pointer = writeBytes(dest, pointer, name); pointer = writeBytes(dest, pointer, type); pointer = writeBytes(dest, pointer, size); pointer = writeBytes(dest, pointer, count);
	 * 
	 * +data??????? }
	 */
	public static ASArray deserialize(byte[] bytes, int pointer, Class<? extends ASDataType> c) {
		byte containertype = readByte(bytes, pointer);
		if(containertype == ContainerTypes.CUSTOM_ARRAY) {
			return ASDataArray.Create(bytes, pointer, c);
		} else if(containertype == ContainerTypes.ARRAY) {
			return deserialize(bytes, pointer);
		} else {
			return null;
		}
	}
	
	public static ASArray deserialize(byte[] bytes, int pointer) {
		ASArray result = new ASArray();

		byte containertype = readByte(bytes, pointer);
		if(containertype == 3) {
			return ASDataArray.Create(bytes, pointer);
		}
		pointer++;
		
		if(containertype != CONTAINER_TYPE) return null;

		result.nameLength = readShort(bytes, pointer);
		pointer += 2;

		result.name = readString(bytes, pointer, result.nameLength).getBytes();
		pointer += result.nameLength;

		result.type = readByte(bytes, pointer);
		pointer++;

		result.size = readInt(bytes, pointer);
		pointer += 4;

		result.count = readInt(bytes, pointer);
		pointer += 4;

		switch (result.type) {
			case Types.BYTE:
				result.data = readBytes(bytes, pointer, result.count);
				break;
			case Types.SHORT:
				result.shortData = readShorts(bytes, pointer, result.count);
				break;
			case Types.CHAR:
				result.charData = readChars(bytes, pointer, result.count);
				break;
			case Types.INT:
				result.intData = readIntegers(bytes, pointer, result.count);
				break;
			case Types.LONG:
				result.longData = readLongs(bytes, pointer, result.count);
				break;
			case Types.FLOAT:
				result.floatData = readFloats(bytes, pointer, result.count);
				break;
			case Types.DOUBLE:
				result.doubleData = readDoubles(bytes, pointer, result.count);
				break;
			case Types.BOOLEAN:
				result.booleanData = readBooleans(bytes, pointer, result.count);
				break;
			case Types.STRING:
				result.stringData = readStrings(bytes, pointer, result.count);
				break;
		}

		return result;
	}

	public short[] getShortData() {
		if(shortData == null) System.err.println("No Short data here! This is: " + type);
		return shortData;
	}

	public String[] getStringData() {
		return stringData;
	}

	public char[] getCharData() {
		return charData;
	}

	public int[] getIntData() {
		return intData;
	}

	public long[] getLongData() {
		return longData;
	}

	public float[] getFloatData() {
		return floatData;
	}

	public double[] getDoubleData() {
		return doubleData;
	}

	public boolean[] getBooleanData() {
		return booleanData;
	}
}
