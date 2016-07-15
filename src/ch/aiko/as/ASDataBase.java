package ch.aiko.as;

import static ch.aiko.as.SerializationReader.getByte;
import static ch.aiko.as.SerializationReader.readByte;
import static ch.aiko.as.SerializationReader.readBytes;
import static ch.aiko.as.SerializationReader.readInt;
import static ch.aiko.as.SerializationReader.readShort;
import static ch.aiko.as.SerializationReader.readString;
import static ch.aiko.as.SerializationWriter.FOOTER;
import static ch.aiko.as.SerializationWriter.HEADER;
import static ch.aiko.as.SerializationWriter.VERSION;
import static ch.aiko.as.SerializationWriter.writeBytes;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ASDataBase {

	public static final byte CONTAINER_TYPE = ContainerTypes.DATABASE;
	public short nameLength;
	public byte[] name;
	private int size = 1 + 2 + 4 + 2 + 1 + HEADER.length + 2 + FOOTER.length; // == 17
	public short objectCount;
	public List<ASObject> objects = new ArrayList<ASObject>();

	public ASDataBase(String name) {
		setName(name);
	}

	private ASDataBase() {}

	public void setName(String name) {
		assert (name.length() < Short.MAX_VALUE);

		if (this.name != null) size -= this.name.length;

		nameLength = (short) name.length();
		this.name = name.getBytes();

		size += name.length();
	}

	public void addObject(ASObject obj) {
		objects.add(obj);
		size += obj.getSize();
		objectCount = (short) objects.size();
	}

	public int getSize() {
		return size;
	}

	public int getBytes(byte[] dest, int pointer) {
		int start = pointer;
		pointer = writeBytes(dest, pointer, HEADER);
		pointer = writeBytes(dest, pointer, VERSION);
		pointer = writeBytes(dest, pointer, CONTAINER_TYPE);
		pointer = writeBytes(dest, pointer, nameLength);
		pointer = writeBytes(dest, pointer, name);
		pointer = writeBytes(dest, pointer, size);
		pointer = writeBytes(dest, pointer, objectCount);
		for (ASObject obj : objects)
			pointer = obj.getBytes(dest, pointer);
		pointer = writeBytes(dest, pointer, generateChecksum(dest, start, size - FOOTER.length - 1, VERSION));
		pointer = writeBytes(dest, pointer, FOOTER);
		return pointer;
	}

	public static final byte generateChecksum(byte[] d, int pointStart, int size, short version) {
		if (version == 0x0100) {
			int ret = 0;
			for (int i = 0; i < size; i++) {
				byte b = d[i + pointStart];
				ret += (b - 27) % 0xFFFF;
			}
			ret /= size + 18;
			return (byte) (ret & 0xFF);
		}
		System.err.println("None of the versions found! Returning checksum 0");
		return 0;
	}

	public static ASDataBase createFromBytes(byte[] b) {
		int pointer = 0;
		if (b == null) return null;
		// Read Header
		String Header = readString(b, pointer, HEADER.length);
		if (HEADER == Header.getBytes()) {
			System.err.println("Wrong header!");
			return null;
		}
		pointer += HEADER.length;

		final short version = readShort(b, pointer);
		pointer += 2;

		ASDataBase result = new ASDataBase();

		// Read Container-type
		byte containerType = getByte(b, pointer);
		if (containerType != CONTAINER_TYPE) {
			System.err.println("No DataBase at root!");
			return null;
		}
		pointer++;

		// Read namelength
		result.nameLength = readShort(b, pointer);
		pointer += 2;

		// Read name with namelength in the result object
		result.name = readString(b, pointer, result.nameLength).getBytes();
		pointer += result.nameLength;

		// Read stored size (of the file)
		result.size = readInt(b, pointer);
		if (b.length != result.size) {
			System.err.println("Size mismatch!");
			return null;
		}
		pointer += 4;

		// Read the object count
		result.objectCount = readShort(b, pointer);
		pointer += 2;
		// Read the objects

		for (int i = 0; i < result.objectCount; i++) {
			ASObject obj = ASObject.deserialize(b, pointer);
			if (obj == null) continue;
			result.objects.add(obj);
			pointer += obj.getSize();
		}

		byte CHECKSUM = generateChecksum(b, 0, pointer, version);
		byte checksum = readByte(b, pointer);
		pointer++;

		// System.out.println(checksum);
		// System.out.println(CHECKSUM);
		// System.out.println(checksum == CHECKSUM);

		if (checksum != CHECKSUM) {
			System.err.println("Should be: " + CHECKSUM + " is " + checksum);
			System.err.println("Checksum mismatch! Returning");
			return null;
		}

		byte[] footer = readBytes(b, pointer, FOOTER.length);
		if (new String(footer).equalsIgnoreCase(new String(FOOTER))) ;

		return result;
	}

	public static ASDataBase createFromFile(String path) {
		byte[] buffer = null;
		try {
			BufferedInputStream in = new BufferedInputStream(new FileInputStream(path));
			buffer = new byte[in.available()];
			in.read(buffer);
			in.close();
		} catch (Exception e) {
			return null;
		}
		return createFromBytes(buffer);
	}

	public static ASDataBase createFromResource(String path) {
		byte[] buffer = null;
		try {
			if (!path.startsWith("/")) path = "/" + path;
			BufferedInputStream in = new BufferedInputStream(ASDataBase.class.getResourceAsStream(path));
			buffer = new byte[in.available()];
			in.read(buffer);
			in.close();
		} catch (Exception e) {
			return null;
		}
		return createFromBytes(buffer);
	}

	public void saveToFile(String path) {
		try {
			byte[] bytes = new byte[size];
			getBytes(bytes, 0);
			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(path));
			stream.write(bytes);
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addObject(ASDataType level) {
		addObject(level.object);
	}

	public ASObject getObject(String name) {
		for (int i = 0; i < objects.size(); i++) {
			if (new String(objects.get(i).name).equalsIgnoreCase(name)) return objects.get(i);
		}
		return null;
	}

}
