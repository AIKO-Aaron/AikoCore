package ch.aiko.as;

public class Types {

	public static final byte NOT_DEFINED 	= 0x0;
	public static final byte BYTE 			= 0x1;
	public static final byte CHAR 			= 0x2;
	public static final byte SHORT 			= 0x3;
	public static final byte INT 			= 0x4;
	public static final byte LONG 			= 0x5;
	public static final byte FLOAT 			= 0x6;
	public static final byte DOUBLE 			= 0x7;
	public static final byte BOOLEAN 		= 0x8;
	
	/**Experimental*/
	public static final byte STRING 			= 0x10; 
	public static final byte AS_DATA_TYPE	= 0x7F; 

	public static int getSize(byte type) {
		switch (type) {
			case BYTE:			return 1;
			case CHAR:			return 2;
			case SHORT:			return 2;
			case INT:			return 4;
			case LONG:			return 8;
			case FLOAT:			return 4;
			case DOUBLE:			return 8;
			case BOOLEAN:		return 1;
			case STRING:			{System.err.println("String size requested! nothing returned");return 0;}
			case AS_DATA_TYPE:	{System.err.println("DATATYPE size requested! nothing returned");return 0;}
		}
		assert (false);
		return 0;
	}

}
