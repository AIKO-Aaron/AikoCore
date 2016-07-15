package ch.aiko.as;

import static ch.aiko.as.SerializationReader.readByte;
import static ch.aiko.as.SerializationReader.readChars;
import static ch.aiko.as.SerializationReader.readInt;
import static ch.aiko.as.SerializationReader.readShort;
import static ch.aiko.as.SerializationReader.readString;
import static ch.aiko.as.SerializationWriter.writeBytes;

public class ASString {

	public static final byte CONTAINER_TYPE = ContainerTypes.STRING;
	public short nameLength;
	public byte[] name;
	public int count;
	public int size = 1 + 2 + 4 + 4;

	private char[] characters;

	private ASString() {}
	
	public String toString() {
		return new String(characters);
	}

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

	public void updateSize() {
		size += Types.getSize(Types.CHAR) * count;
	}

	public int getBytes(byte[] dest, int pointer) {
		pointer = writeBytes(dest, pointer, CONTAINER_TYPE);
		pointer = writeBytes(dest, pointer, nameLength);
		pointer = writeBytes(dest, pointer, name);
		pointer = writeBytes(dest, pointer, size);
		pointer = writeBytes(dest, pointer, count);
		pointer = writeBytes(dest, pointer, characters);

		return pointer;
	}

	public static ASString Create(String name, char[] data) {
		ASString s = new ASString();
		s.setName(name);
		s.count = data.length;
		s.characters = data;
		s.updateSize();
		return s;
	}

	public static ASString Create(String name, String text) {
		return Create(name, text.toCharArray());
	}

	public static ASString deserialize(byte[] bytes, int pointer) {
		ASString result = new ASString();

		byte containertype = readByte(bytes, pointer);
		pointer++;
		if(containertype != CONTAINER_TYPE) return null;

		result.nameLength = readShort(bytes, pointer);
		pointer += 2;

		result.name = readString(bytes, pointer, result.nameLength).getBytes();
		pointer += result.nameLength;

		result.size = readInt(bytes, pointer);
		pointer += 4;

		result.count = readInt(bytes, pointer);
		pointer += 4;

		result.characters = readChars(bytes, pointer, result.count);

		return result;
	}
}
