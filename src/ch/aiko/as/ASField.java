package ch.aiko.as;

import static ch.aiko.as.SerializationWriter.*;
import static ch.aiko.as.SerializationReader.*;

public class ASField {

	public static final byte CONTAINER_TYPE = ContainerTypes.FIELD;
	public short nameLength;
	public byte[] name;
	public byte type;
	public byte[] data;
	
	public void setName(String name) {
		assert(name.length() < Short.MAX_VALUE);
		nameLength = (short)name.length();
		this.name = name.getBytes();
	}
	
	private ASField() {}
	
	public int getBytes(byte[] dest, int pointer) {
		assert(data.length == Types.getSize(type) && name.length == nameLength);
		pointer = writeBytes(dest, pointer, CONTAINER_TYPE);
		pointer = writeBytes(dest, pointer, nameLength);
		pointer = writeBytes(dest, pointer, name);
		pointer = writeBytes(dest, pointer, type);
		pointer = writeBytes(dest, pointer, data);
		return pointer;
	}
	
	public int getSize() {
		assert(data.length == Types.getSize(type) && name.length == nameLength);
		return 1 + 2 + name.length + 1 + data.length;
	}
	
	public static ASField Byte(String name, byte value) {
		ASField f = new ASField();
		f.setName(name);
		f.type = Types.BYTE;
		f.data = new byte[Types.getSize(f.type)];
		writeBytes(f.data, 0, value);
		return f;
	}
	
	public static ASField Short(String name, short value) {
		ASField f = new ASField();
		f.setName(name);
		f.type = Types.SHORT;
		f.data = new byte[Types.getSize(f.type)];
		writeBytes(f.data, 0, value);
		return f;
	}
	
	public static ASField Character(String name, char value) {
		ASField f = new ASField();
		f.setName(name);
		f.type = Types.CHAR;
		f.data = new byte[Types.getSize(f.type)];
		writeBytes(f.data, 0, value);
		return f;
	}
	
	public static ASField Integer(String name, int value) {
		ASField f = new ASField();
		f.setName(name);
		f.type = Types.INT;
		f.data = new byte[Types.getSize(f.type)];
		writeBytes(f.data, 0, value);
		return f;
	}
	
	public static ASField Float(String name, float value) {
		ASField f = new ASField();
		f.setName(name);
		f.type = Types.FLOAT;
		f.data = new byte[Types.getSize(f.type)];
		writeBytes(f.data, 0, value);
		return f;
	}
	
	public static ASField Long(String name, long value) {
		ASField f = new ASField();
		f.setName(name);
		f.type = Types.LONG;
		f.data = new byte[Types.getSize(f.type)];
		writeBytes(f.data, 0, value);
		return f;
	}
	
	public static ASField Double(String name, double value) {
		ASField f = new ASField();
		f.setName(name);
		f.type = Types.DOUBLE;
		f.data = new byte[Types.getSize(f.type)];
		writeBytes(f.data, 0, value);
		return f;
	}
	
	public static ASField Boolean(String name, boolean value) {
		ASField f = new ASField();
		f.setName(name);
		f.type = Types.BOOLEAN;
		f.data = new byte[Types.getSize(f.type)];
		writeBytes(f.data, 0, value);
		return f;
	}

	public static ASField deserialize(byte[] bytes, int pointer) {
		ASField result = new ASField();
		
		byte containertype = readByte(bytes, pointer);
		pointer++;
		if(containertype != CONTAINER_TYPE) return null;
		
		result.nameLength = readShort(bytes, pointer);
		pointer+=2;
		
		result.setName(readString(bytes, pointer, result.nameLength));
		pointer += result.nameLength;
		
		result.type = readByte(bytes, pointer);
		pointer++;
			
		result.data = readBytes(bytes, pointer, Types.getSize(result.type));
		
		return result;
	}
		
}
