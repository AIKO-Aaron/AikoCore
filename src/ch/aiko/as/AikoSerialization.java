package ch.aiko.as;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class AikoSerialization {

	static Random rand = new Random();

	public static final String VERSION = "0.1";

	static void printBytes(byte[] data) {
		for (byte b : data)
			System.out.printf("0x%x ", b);
		System.out.println();
	}

	/**public static void main(String[] args) {
		boolean b = false;
		if (b) testSerialization();
		else testDeserialization();
	}

	private static void testDeserialization() {
		ASDataBase db = ASDataBase.createFromFile(System.getProperty("user.home") + "/Desktop/test.bin");
		for(ASObject obj : db.objects) {
			System.out.println(new String(obj.name) + ": " + obj.fieldCount);
		}
	}

	static void testSerialization() {
		ASDataBase db = new ASDataBase("DataBase");

		ASField x = ASField.Integer("xPos", 8);
		ASField y = ASField.Integer("yPos", 8);
		ASString name = ASString.Create("name", "TestName");

		ASObject entity = new ASObject("entity");
		entity.addField(x);
		entity.addField(y);
		entity.addString(name);

		
		
		for (int i = 0; i < 199; i++) {
			ASObject o = new ASObject("Nr." + i);
			o.addField(ASField.Integer("Number", i));
			db.addObject(o);
		}

		db.addObject(entity);

		byte[] stream = new byte[db.getSize()];
		db.getBytes(stream, 0);
		saveToFile(System.getProperty("user.home") + "/Desktop/test.bin", stream);
	}*/
	
	public static void saveToFile(String path, ASDataBase data) {
		try {
			byte[] bytes = new byte[data.getSize()];
			data.getBytes(bytes, 0);
			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(path));
			stream.write(bytes);
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveToFile(String path, byte[] data) {
		try {
			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(path));
			stream.write(data);
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
