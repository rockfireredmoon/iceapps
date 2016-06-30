package org.icescene;

import com.jme3.math.Vector3f;

public class SceneConstants {

	//
	// Appsettings
	//
	/**
	 * Custom property for whether the window is resizable
	 */
	public static String APPSETTINGS_RESIZABLE = "Resizable";
	/**
	 * X Position of the window when resizable
	 */
	public static String APPSETTINGS_WINDOW_X = "WindowX";
	/**
	 * Y Position of the window when resizable
	 */
	public static String APPSETTINGS_WINDOW_Y = "WindowY";
	/**
	 * Maximum number of prop tiles surrounding the player to load
	 */
	public static int SCENERY_TILE_LOAD_RADIUS = 1;
	/**
	 * Maximum number of creatures load threads
	 */
	public static int MAX_CREATURE_LOAD_THREADS = 1;
	/**
	 * Maximum number of world load threads
	 */
	public static int MAX_WORLD_LOAD_THREADS = 1;
	/**
	 * Default Tonegodgui Style Map
	 */
	public static String UI_DEFAULT_THEME = "Interface/Styles/Gold/style_map.gui.xml";
	/**
	 * A fake delay for "cog" testing
	 */
	public static long CREATURE_LOAD_DELAY = 1000;
	//
	// Player and other mobs
	//
	/**
	 * GLOBAL SPEED MULTIPLIER. All movement speeds, animations and other time
	 * dependent
	 * systems will mulitply their relative speeds by this amount.
	 */
	public static float GLOBAL_SPEED_FACTOR = 1.0F;
	//
	// HUD messages
	/**
	 * How long HUD messages are displayed for before being scrolled off.
	 */
	public static float HUD_MESSAGE_TIMEOUT = 10f;
	/**
	 * Base ambient brightness.
	 */
	public static float AMBIENT_BRIGHTNESS = 1f;
	/**
	 * Base directional brightness.
	 */
	public static float DIRECTIONAL_BRIGHTNESS = 1f;
	/**
	 * The preference key app settings are stored under
	 */
	public static String APPSETTINGS_NAME = "icescene";
	/**
	 * Default sun direction
	 */
	public static Vector3f DEFAULT_SUN_DIRECTION = new Vector3f(-0.8f, -0.6f, -0.08f);
	/**
	 * Distance of directional light source
	 */
	public static float DIRECTIONAL_LIGHT_SOURCE_DISTANCE = 2100f;

	//
	// Game Engine Parameters
	//

	/**
	 * The number of rows/columns in a clutter subtile. So if this is 3, the
	 * total number
	 * of cells the terrain is divided into will be 9. Must be an odd number
	 */
	public static int CLUTTER_TILES = 11;
	/**
	 * The radius clutter subtile pages around the players current location that
	 * will be
	 * loaded when the player crosses the subtile boundary
	 */
	public static int CLUTTER_LOAD_RADIUS = 3;
	/**
	 * A global factor for clutter. The configured clutter amount will be
	 * mulitplied by
	 * this for the total number of possible clutter items
	 */
	public static float CLUTTER_GLOBAL_DENSITY = 2.5f;

	/**
	 * The radius from the current position at which point terrain pages should
	 * be
	 * unloaded.
	 */
	public static final int UNLOAD_PAGE_RADIUS = 2;

	/**
	 * Global max radius (terrain, clutter, props)
	 */
	public static int GLOBAL_MAX_LOAD = Integer.parseInt(System.getProperty("icescene.globalMaxRadius", "1"));
	/**
	 * Maximum distance of possibly visible worlds units. This value is
	 * appropriate
	 * for terrain editing.
	 * 
	 * TODO should be made configurable in game
	 */
	public static float WORLD_FRUSTUM = 9000f;

	/**
	 * Repeat delay of keyboard actions (where used)
	 */
	public static float KEYBOARD_REPEAT_DELAY = 0.75f;
	/**
	 * Repeat interval of keyboard actions (where used)
	 */
	public static float KEYBOARD_REPEAT_INTERVAL = 0.2f;

	/**
	 * When fading audio, fade by how much per interval (as a base value).
	 */
	public static float AUDIO_FADE_AMOUNT = 0.25f;

	/**
	 * How many undo / redo operations a second when repeating (i.e. holding
	 * down
	 * Undo button or key)
	 */
	public static float UNDO_REDO_REPEAT_INTERVAL = 100f;

	/**
	 * Global maximum number of results to return when searching (e.g. a prop
	 * search)
	 */
	public static final int MAX_SEARCH_RESULTS = 50;
	/**
	 * The 'GlobalAnimSpeed' used in the scrolling shader (for water etc)
	 */
	public static final float SCROLL_SHADER_SPEED = 0.25f;
	/**
	 * Default path where terrain textures are found.
	 */
	public static String TERRAIN_PATH = "Terrain";
	/**
	 * Default path where music files are found
	 */
	public static final String MUSIC_PATH = "Music";
	/**
	 * Default path where sound files are found
	 */
	public static final String SOUND_PATH = "Sound";
	/**
	 * Default path where material files are found
	 */
	public static final String MATERIALS_PATH = "Materials";
}
