package ch.aiko.as;

import static ch.aiko.as.SerializationReader.readByte;
import static ch.aiko.as.SerializationReader.readInt;
import static ch.aiko.as.SerializationReader.readShort;
import static ch.aiko.as.SerializationReader.readString;
import static ch.aiko.as.SerializationWriter.writeBytes;

import java.util.ArrayList;

public class ASDataArray<T extends ASDataType> extends ASArray {

	public ASDataType[] data;
	public Class<? extends ASDataType> clazz;

	public static final byte CONTAINER_TYPE = ContainerTypes.CUSTOM_ARRAY;
	public short classLength;
	public byte[] className;
	// public int size = 1 + 2 + 4 + 4 + 1 + 2;// + count * Types.getSize(type);

	@SuppressWarnings("unchecked")
	public T getData(int index) {
		if (index + 1 > data.length) index = data.length - 1;
		return (T) data[index];
	}
	
	@SuppressWarnings("unchecked")
	public T[] getData() {
		return (T[])data;
	}

	private ASDataArray(Class<T> clazz) {
		super();
		size = 1 + 2 + 4 + 4 + 1 + 2;
		this.clazz = clazz;
		this.className = clazz.getName().getBytes();
		this.classLength = (short) className.length;
		size += classLength;
		type = Types.AS_DATA_TYPE;
	}

	@SuppressWarnings("unchecked")
	public static <T extends ASDataType> ASDataArray<T> Create(String name, T[] data) {
		if (data.length == 0) {
			System.err.println("Array length 0");
			return null;
		}
		ASDataArray<T> array = new ASDataArray<T>((Class<T>) data.getClass().getComponentType());
		array.setName(name);
		array.data = data;
		array.count = data.length;
		array.updateSize();
		return array;
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

	private void updateSize() {
		for (ASDataType s : data)
			size += s.getSize();
	}

	public int getBytes(byte[] dest, int pointer) {
		// assert (data.length == Types.getSize(type) && name.length == nameLength);
		pointer = writeBytes(dest, pointer, CONTAINER_TYPE);
		pointer = writeBytes(dest, pointer, nameLength);
		pointer = writeBytes(dest, pointer, name);
		pointer = writeBytes(dest, pointer, classLength);
		pointer = writeBytes(dest, pointer, className);
		pointer = writeBytes(dest, pointer, type);
		pointer = writeBytes(dest, pointer, size);
		pointer = writeBytes(dest, pointer, count);
		for (ASDataType t : data)
			pointer = t.getBytes(dest, pointer);
		return pointer;
	}

	public static <T extends ASDataType> ASDataArray<T> Create(byte[] bytes, int pointer, Class<T> clazz) {
		ASDataArray<T> result = new ASDataArray<T>(clazz);

		byte containertype = readByte(bytes, pointer);
		pointer++;
		if (containertype != CONTAINER_TYPE) return null;

		result.nameLength = readShort(bytes, pointer);
		pointer += 2;

		result.name = readString(bytes, pointer, result.nameLength).getBytes();
		pointer += result.nameLength;

		result.classLength = readShort(bytes, pointer);
		pointer += 2;

		result.className = readString(bytes, pointer, result.classLength).getBytes();
		pointer += result.classLength;

		result.type = readByte(bytes, pointer);
		pointer++;

		result.size = readInt(bytes, pointer);
		pointer += 4;

		result.count = readInt(bytes, pointer);
		pointer += 4;

		ArrayList<T> l = new ArrayList<T>();
		for (int i = 0; i < result.count; i++) {
			ASObject obj = ASObject.deserialize(bytes, pointer);
			pointer += obj.getSize();

			T t = null;
			try {
				t = clazz.getConstructor(ASObject.class).newInstance(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (t != null) l.add(t);
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public static ASArray Create(byte[] bytes, int pointer) {
		ASDataArray<? extends ASDataType> result = new ASDataArray<ASDataType>(ASDataType.class);

		byte containertype = readByte(bytes, pointer);
		pointer++;
		if (containertype != CONTAINER_TYPE) return null;

		result.nameLength = readShort(bytes, pointer);
		pointer += 2;

		result.name = readString(bytes, pointer, result.nameLength).getBytes();
		pointer += result.nameLength;
		
		result.classLength = readShort(bytes, pointer);
		pointer += 2;
		
		result.className = readString(bytes, pointer, result.classLength).getBytes();
		pointer += result.classLength;
		
		try {
			result.clazz = (Class<? extends ASDataType>) Class.forName(new String(result.className));
		} catch (ClassNotFoundException e1) {
			System.err.println("ClassNotFound: " + new String(result.className));
		}
		
		result.type = readByte(bytes, pointer);
		pointer++;

		result.size = readInt(bytes, pointer);
		pointer += 4;

		result.count = readInt(bytes, pointer);
		pointer += 4;

		ArrayList<ASDataType> l = new ArrayList<ASDataType>();
		for (int i = 0; i < result.count; i++) {
			ASObject obj = ASObject.deserialize(bytes, pointer);
			pointer += obj.getSize();

			ASDataType t = null;
			try {
				t = result.clazz.getConstructor(ASObject.class).newInstance(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (t != null) l.add(t);
		}
		result.data = l.toArray(new ASDataType[l.size()]);

		return result;
	}

}
