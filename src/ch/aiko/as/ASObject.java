package ch.aiko.as;

import static ch.aiko.as.SerializationReader.readByte;
import static ch.aiko.as.SerializationReader.readInt;
import static ch.aiko.as.SerializationReader.readShort;
import static ch.aiko.as.SerializationReader.readString;
import static ch.aiko.as.SerializationWriter.writeBytes;

import java.util.ArrayList;
import java.util.List;

public class ASObject {

	public static final byte CONTAINER_TYPE = ContainerTypes.OBJECT;
	public short nameLength;
	public byte[] name;

	private int size = 15; // 1 + 2 + 4 + 2 + 2 + 2 + 2

	public short fieldCount;
	public List<ASField> fields = new ArrayList<ASField>();
	public short arrayCount;
	public List<ASArray> arrays = new ArrayList<ASArray>();
	public short stringCount;
	public List<ASString> strings = new ArrayList<ASString>();
	public short objectCount;
	public List<ASObject> objects = new ArrayList<ASObject>();

	public ASObject(String name) {
		setName(name);
	}

	private ASObject() {}

	public void setName(String name) {
		assert (name.length() < Short.MAX_VALUE);

		if (this.name != null) size -= this.name.length;

		nameLength = (short) name.length();
		this.name = name.getBytes();

		size += name.length();
	}

	public void addField(ASField field) {
		size += field.getSize();
		fields.add(field);
		fieldCount = (short) fields.size();
	}

	public void addArray(ASArray array) {
		size += array.getSize();
		arrays.add(array);
		arrayCount = (short) arrays.size();
	}

	public void addString(ASString string) {
		size += string.getSize();
		strings.add(string);
		stringCount = (short) strings.size();
	}

	public void addObject(ASObject obj) {
		size += obj.getSize();
		objects.add(obj);
		objectCount = (short) objects.size();
	}

	public ASArray getArray(String name) {
		for (int i = 0; i < arrays.size(); i++) {
			if (arrays.get(i) == null) continue;
			if (new String(arrays.get(i).name).equalsIgnoreCase(name)) return arrays.get(i);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T extends ASDataType> ASDataArray<T> getArrayWithType(String name, Class<T> clazz) {
		for (int i = 0; i < arrays.size(); i++) {
			if (arrays.get(i) == null) continue;
			if (new String(arrays.get(i).name).equalsIgnoreCase(name)) return (ASDataArray<T>) arrays.get(i);
		}
		return null;
	}

	public ASField getField(String name) {
		for (int i = 0; i < fields.size(); i++) {
			if (new String(fields.get(i).name).equalsIgnoreCase(name)) return fields.get(i);
		}
		return null;
	}

	public ASString getString(String name) {
		for (int i = 0; i < strings.size(); i++) {
			if (new String(strings.get(i).name).equalsIgnoreCase(name)) return strings.get(i);
		}
		return null;
	}

	public ASObject getObject(String name) {
		for (int i = 0; i < objects.size(); i++) {
			if (new String(objects.get(i).name).equalsIgnoreCase(name)) return objects.get(i);
		}
		return null;
	}

	public int getSize() {
		return size;
	}

	public int getBytes(byte[] dest, int pointer) {
		pointer = writeBytes(dest, pointer, CONTAINER_TYPE);
		pointer = writeBytes(dest, pointer, nameLength);
		pointer = writeBytes(dest, pointer, name);
		pointer = writeBytes(dest, pointer, size);
		pointer = writeBytes(dest, pointer, fieldCount);
		for (ASField field : fields)
			pointer = field.getBytes(dest, pointer);
		pointer = writeBytes(dest, pointer, arrayCount);
		for (ASArray array : arrays)
			pointer = array.getBytes(dest, pointer);
		pointer = writeBytes(dest, pointer, stringCount);
		for (ASString string : strings)
			pointer = string.getBytes(dest, pointer);
		pointer = writeBytes(dest, pointer, objectCount);
		for (ASObject obj : objects)
			pointer = obj.getBytes(dest, pointer);
		return pointer;
	}

	public static ASObject deserialize(byte[] data, int pointer) {
		ASObject result = new ASObject();

		byte containertype = readByte(data, pointer);
		pointer++;
		if (containertype != CONTAINER_TYPE) return null;

		result.nameLength = readShort(data, pointer);
		pointer += 2;

		result.name = readString(data, pointer, result.nameLength).getBytes();
		pointer += result.nameLength;

		result.size = readInt(data, pointer);
		pointer += 4;

		result.fieldCount = readShort(data, pointer);
		pointer += 2;

		for (int i = 0; i < result.fieldCount; i++) {
			ASField field = ASField.deserialize(data, pointer);
			if (field == null) continue;
			result.fields.add(field);
			pointer += field.getSize();
		}

		result.arrayCount = readShort(data, pointer);
		pointer += 2;

		for (int i = 0; i < result.arrayCount; i++) {
			ASArray array = ASArray.deserialize(data, pointer);
			if (array == null) continue;
			result.arrays.add(array);
			pointer += array.getSize();
		}

		result.stringCount = readShort(data, pointer);
		pointer += 2;

		for (int i = 0; i < result.stringCount; i++) {
			ASString string = ASString.deserialize(data, pointer);
			if (string == null) continue;
			result.strings.add(string);
			pointer += string.getSize();
		}

		result.objectCount = readShort(data, pointer);
		pointer += 2;

		for (int i = 0; i < result.objectCount; i++) {
			ASObject obj = ASObject.deserialize(data, pointer);
			if (obj == null) continue;
			result.objects.add(obj);
			pointer += obj.getSize();
		}

		return result;
	}

	public void removeObject(String name2) {
		for (int i = 0; i < objects.size(); i++) {
			if (objects.get(i).getName().equalsIgnoreCase(name2)) {
				size -= objects.get(i).getSize();
				objectCount = (short) objects.size();
				objects.remove(i);
			}
		}
	}

	public String getName() {
		return new String(name);
	}

}
