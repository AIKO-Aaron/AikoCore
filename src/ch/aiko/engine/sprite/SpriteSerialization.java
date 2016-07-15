package ch.aiko.engine.sprite;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import ch.aiko.as.ASArray;
import ch.aiko.as.ASDataBase;
import ch.aiko.as.ASObject;
import ch.aiko.engine.sprite.Sprite;
import ch.aiko.engine.sprite.SpriteSheet;

public class SpriteSerialization {

	// "ZA\$name\$path/to/img.png\$xSize\$ySize\$"
	// "ZA\$name\$path/to/img.png\$xSize\$ySize\$xOffset\$yOffset\$"

	// "YB\$name\$path/to/img.png\$"

	// "XC\$name\$spriteSheetName\$xPos\$yPos\$"
	// "XC\$name\$spriteSheetName\$xPos\$yPos\$xOffset\$yOffset\$"

	// "WD\$name\$path/to/img.png\$xSize\$ySize\$xPos\$yPos\$"
	// "WD\$name\$path/to/img.png\$xSize\$ySize\$xPos\$yPos\$xOffset\$yOffset\$"

	public static int TILE_SIZE = 32;

	protected static final String SPRITE_SHEET = "ZA";
	protected static final String SINGLE_SPRITE = "YB";
	protected static final String SPRITE_FROM_SHEET = "XC";
	protected static final String DIRECT_SPRITE_FROM_SHEET = "WD";

	protected static final String SEPERATOR = "\\$";

	public static boolean isStringSprite(String s) {
		return s.startsWith(SINGLE_SPRITE) || s.startsWith(SPRITE_FROM_SHEET) || s.startsWith(DIRECT_SPRITE_FROM_SHEET);
	}

	public static boolean isStringSpriteSheet(String s) {
		return s.startsWith(SPRITE_SHEET);
	}

	protected static int INDEX = 0;
	public static final HashMap<Integer, String> SERIALIZED = new HashMap<Integer, String>();
	public static final HashMap<Integer, Sprite> SPRITES = new HashMap<Integer, Sprite>();

	protected static int SS_INDEX = 0;
	public static final HashMap<Integer, String> SS_SERIALIZED = new HashMap<Integer, String>();
	public static final HashMap<Integer, SpriteSheet> SPRITE_SHEETS = new HashMap<Integer, SpriteSheet>();

	public static Sprite getSprite(int id) {
		return SPRITES.get(id);
	}

	public static SpriteSheet getSpriteSheet(int id) {
		return SPRITE_SHEETS.get(id);
	}

	// ===============
	// Add to the list
	// ===============

	public static void addSprite(Sprite sprite, String pathToImage, int index) {
		SPRITES.put(index, sprite);
		SERIALIZED.put(index, spriteToString(sprite, pathToImage, null, 0, 0, false));
	}

	public static Sprite addSprite(Sprite sprite, SpriteSheet loadedFrom, int x, int y, boolean instant, int index) {
		SPRITES.put(index, sprite);
		SERIALIZED.put(index, spriteToString(sprite, null, loadedFrom, x, y, instant));
		return sprite;
	}

	public static void addSpriteSheet(SpriteSheet sprite, String pathToImage, int width, int height, int index) {
		SPRITE_SHEETS.put(index, sprite);
		SS_SERIALIZED.put(index, spriteSheetToString(sprite, pathToImage, width, height));
	}

	// ============
	// Create & add
	// ============

	public static Sprite createFromImage(String pathToImage) {
		return createFromImage(pathToImage, INDEX++);
	}

	public static Sprite createFromImage(String pathToImage, int index) {
		Sprite s = new Sprite(pathToImage);
		addSprite(s, pathToImage, index);
		return s;
	}

	public static SpriteSheet createFromImage(String pathToImage, int sw, int sh) {
		return createFromImage(pathToImage, sw, sh, SS_INDEX++);
	}

	public static SpriteSheet createFromImage(String pathToImage, int sw, int sh, int index) {
		SpriteSheet sheet = new SpriteSheet(pathToImage, sw, sh);
		addSpriteSheet(sheet, pathToImage, sw, sh, index);
		return sheet;
	}

	public static SpriteSheet createFromImage(String pathToImage, int sw, int sh, int nw, int nh) {
		SpriteSheet sheet = new SpriteSheet(pathToImage, sw, sh, nw, nh);
		addSpriteSheet(sheet, pathToImage, sw, sh, SS_INDEX++);
		return sheet;
	}

	public static SpriteSheet createFromImage(String pathToImage, int sw, int sh, int nw, int nh, int index) {
		SpriteSheet sheet = new SpriteSheet(pathToImage, sw, sh, nw, nh);
		addSpriteSheet(sheet, pathToImage, sw, sh, index);
		return sheet;
	}

	public static Sprite createFromSerializedData(String data) {
		int t = 0;
		try {
			t = Integer.parseInt(data);
			return SpriteSerialization.getSprite(t);
		} catch (Exception e) {
			return SpriteSerialization.getSprite(0);
		}
	}

	// ==========
	// Get ID
	// ==========

	public static int getSpriteID(Sprite s) {
		for (Entry<Integer, Sprite> e : SPRITES.entrySet()) {
			if (e.getValue() == s) return e.getKey();
		}
		return 0;
	}

	public static int getSpriteSheetID(SpriteSheet s) {
		for (Entry<Integer, SpriteSheet> e : SPRITE_SHEETS.entrySet()) {
			if (e.getValue() == s) return e.getKey();
		}
		return 0;
	}

	// ==========
	// to String
	// ==========

	public static String spriteToString(Sprite sprite, String pathToImg, SpriteSheet loadedFrom, int x, int y, boolean directLoad) {
		if (loadedFrom != null) {
			if (directLoad) {
				return DIRECT_SPRITE_FROM_SHEET + SEPERATOR + getSpriteID(sprite) + SEPERATOR;
			} else {
				return SPRITE_FROM_SHEET + SEPERATOR + getSpriteID(sprite) + SEPERATOR + getSpriteSheetID(loadedFrom) + SEPERATOR + x + SEPERATOR + y + SEPERATOR;
			}
		} else {
			return SINGLE_SPRITE + SEPERATOR + getSpriteID(sprite) + SEPERATOR + pathToImg + SEPERATOR;
		}
	}

	public static String spriteSheetToString(SpriteSheet sprite, String pathToImg, int w, int h) {
		return SPRITE_SHEET + SEPERATOR + getSpriteSheetID(sprite) + SEPERATOR + pathToImg + SEPERATOR + w + SEPERATOR + h + SEPERATOR;
	}

	// ==========
	// Serialize
	// ==========

	public static ASObject serializeSprites() {
		ASObject obj = new ASObject("SpriteData");

		// SpriteSheets
		String[] spriteSheets = new String[SS_SERIALIZED.size()];
		for (int i = 0; i < spriteSheets.length; i++) {
			spriteSheets[i] = SS_SERIALIZED.get(SS_SERIALIZED.keySet().toArray()[i]);
		}
		ASArray ssArray = ASArray.String("SpriteSheets", spriteSheets);
		obj.addArray(ssArray);

		// Sprites
		String[] sprites = new String[SERIALIZED.size()];
		for (int i = 0; i < sprites.length; i++) {
			sprites[i] = SERIALIZED.get(SERIALIZED.keySet().toArray()[i]);
		}
		ASArray sArray = ASArray.String("Sprites", sprites);
		obj.addArray(sArray);

		return obj;
	}

	// ===========
	// Deserialize
	// ===========

	public static void deserializeSprites(ASDataBase db) {
		ASObject spriteData = db.getObject("SpriteData");
		String[] sheets = spriteData.getArray("SpriteSheets").getStringData();
		for (String sd : sheets) {
			String[] sdp = sd.split(Pattern.quote(SEPERATOR));
			sdp[0] = SPRITE_SHEET;
			int index = Integer.parseInt(sdp[1]);
			String pathToImage = sdp[2];
			int w = Integer.parseInt(sdp[3]);
			int h = Integer.parseInt(sdp[4]);
			createFromImage(pathToImage, w, h, TILE_SIZE, TILE_SIZE, index);
		}

		String[] s = spriteData.getArray("Sprites").getStringData();
		for (String sd : s) {
			String[] sdp = sd.split(Pattern.quote(SEPERATOR));
			if (sdp[0].equalsIgnoreCase(SINGLE_SPRITE)) {
				int index = Integer.parseInt(sdp[1]);
				String path = sdp[2];
				createFromImage(path, index);
			} else if (sdp[0].equalsIgnoreCase(SPRITE_FROM_SHEET)) {
				int index = Integer.parseInt(sdp[1]);
				int sheetIndex = Integer.parseInt(sdp[2]);
				int x = Integer.parseInt(sdp[3]);
				int y = Integer.parseInt(sdp[4]);
				SpriteSheet sheet = getSpriteSheet(sheetIndex);
				addSprite(sheet.getSprite(x, y), sheet, x, y, false, index);
			} else if (sdp[0].equalsIgnoreCase(DIRECT_SPRITE_FROM_SHEET)) {

			}
		}
	}

}
