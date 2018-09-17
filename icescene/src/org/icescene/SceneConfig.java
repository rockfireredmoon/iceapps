package org.icescene;

import java.io.File;
import java.util.prefs.Preferences;

import org.icelib.AbstractConfig;

import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;

public class SceneConfig extends AbstractConfig {
	
	/**
	 * Landmarks
	 */
    public final static String LANDMARKS = "landmarks";
    
    /*
     * Audio
     */
    public final static String AUDIO = "audio";
    // Music Volume
    public final static String AUDIO_MUSIC_VOLUME = AUDIO + "MusicVolume";
    public final static float AUDIO_MUSIC_VOLUME_DEFAULT = 1f;
    // Ambient Volume
    public final static String AUDIO_AMBIENT_VOLUME = AUDIO + "AmbientVolume";
    public final static float AUDIO_AMBIENT_VOLUME_DEFAULT = 1f;
    // UI Volume
    public final static String AUDIO_UI_VOLUME = AUDIO + "UIVolume";
    public final static float AUDIO_UI_VOLUME_DEFAULT = 1f;
    // Master Volume
    public final static String AUDIO_MASTER_VOLUME = AUDIO + "MasterVolume";
    public final static float AUDIO_MASTER_VOLUME_DEFAULT = 1f;
    // Mute
    public final static String AUDIO_MUTE = AUDIO + "Mute";
    public final static boolean AUDIO_MUTE_DEFAULT = false;

    /*
     * Debug
     */ 
    public final static String DEBUG = "debug";
    public final static String DEBUG_ENGINE = DEBUG + "Engine";
    public final static int DEBUG_ENGINE_DEFAULT = FALSE;
    public final static int DEBUG_ENGINE_FPS = 1;
    public final static int DEBUG_ENGINE_FULLSTATS = 2;
    
    // Debug particles
    public final static String DEBUG_GRID = DEBUG + "Grid";
    public final static boolean DEBUG_GRID_DEFAULT = true;
    // Debug particles
    public final static String DEBUG_VIEWPORT_COLOUR = DEBUG + "ViewportColour";
    public final static ColorRGBA DEBUG_VIEWPORT_COLOUR_DEFAULT = ColorRGBA.Black;
    
    
    /*
     * Options (window position only)
     */
    public final static String OPTIONS = "options";
    
    /*
     * Scene
     */
    public final static String SCENE = "scene";
    public final static String SCENE_BLOOM = SCENE + "Bloom";
    public final static boolean SCENE_BLOOM_DEFAULT = true;
    public final static String SCENE_LIGHT_BEAMS = SCENE + "LightBeams";
    public final static boolean SCENE_LIGHT_BEAMS_DEFAULT = true;
    public final static String SCENE_SHADOWS = SCENE + "Shadows";
    public final static boolean SCENE_SHADOWS_DEFAULT = true;
    public final static String SCENE_HARDWARE_SKINNING = SCENE + "HardwareSkinning";
    public final static boolean SCENE_HARDWARE_SKINNING_DEFAULT = false;
    public final static String SCENE_SSAO = SCENE + "SSAO";
    public final static boolean SCENE_SSAO_DEFAULT = false;
    public final static String SCENE_CLUTTER_DENSITY = SCENE + "DensityOfClutter";
    public final static float SCENE_CLUTTER_DENSITY_DEFAULT = 0.2f;
    public final static String SCENE_DISTANCE = SCENE + "Distance";
    public final static float SCENE_DISTANCE_DEFAULT = 500f;
    public final static String SCENE_CLUTTER_SHADOW_MODE = SCENE + "ClutterShadowMode";
    public final static ShadowMode SCENE_CLUTTER_SHADOW_MODE_DEFAULT = ShadowMode.CastAndReceive;
    public final static String SCENE_LIGHT_MULTIPLIER = SCENE + "LightMultiplier";
    public final static float SCENE_LIGHT_MULTIPLIER_DEFAULT = 1f;
    /*
     * Terrain
     */
    public final static String TERRAIN = "terrain";
    // Nice Water
    public final static String TERRAIN_PRETTY_WATER = TERRAIN + "PrettyWater";
    public final static boolean TERRAIN_PRETTY_WATER_DEFAULT = false;
    // Tri-Planar
    public final static String TERRAIN_TRI_PLANAR = SceneConfig.TERRAIN + "TriPlanar";
    public final static int TERRAIN_TRI_PLANAR_DEFAULT = DEFAULT;
    // Wireframe
    public final static String TERRAIN_WIREFRAME = SceneConfig.TERRAIN + "Wireframe";
    public final static boolean TERRAIN_WIREFRAME_DEFAULT = false;
    // Snap brush to quad
    public final static String TERRAIN_SNAP_BRUSH_TO_QUAD = SceneConfig.TERRAIN + "SnapBrushToQuad";
    public final static boolean TERRAIN_SNAP_BRUSH_TO_QUAD_DEFAULT = true;
    // LOD control
    public final static String TERRAIN_LOD_CONTROL = SceneConfig.TERRAIN + "LODControl";
    public final static boolean TERRAIN_LOD_CONTROL_DEFAULT = true;
    // High Details Terrain 
    public final static String TERRAIN_HIGH_DETAIL = SceneConfig.TERRAIN + "HighDetail";
    public final static boolean TERRAIN_HIGH_DETAIL_DEFAULT = true;
    // Lit Texture 
    public final static String TERRAIN_LIT = SceneConfig.TERRAIN + "Lit";
    public final static boolean TERRAIN_LIT_DEFAULT = true;
    // Smoothing of terrain texture scalng
    public final static String TERRAIN_SMOOTH_SCALING = SceneConfig.TERRAIN + "SmoothScaling";
    public final static boolean TERRAIN_SMOOTH_SCALING_DEFAULT = false;
    /*
     * Assets
     */
    public final static String ASSETS = "build";
    // External Assets
    public final static String ASSETS_EXTERNAL_LOCATION = ASSETS + "ExternalLocation";
    public final static String ASSETS_EXTERNAL_LOCATION_DEFAULT = "";
    /*
     * Clutter definition
     */
    public final static String CLUTTER_DEFINITION = "clutterDefinition";
    /*
     * Build mode
     */
    public final static String BUILD = "build";
    // Camera move speed (build mode)
    public final static String BUILD_MOVE_SPEED = BUILD + "MoveSpeed";
    public final static float BUILD_MOVE_SPEED_DEFAULT = 400f;
    // Camera zoom speed
    public final static String BUILD_ZOOM_SPEED = BUILD + "ZoomSpeed";
    public final static float BUILD_ZOOM_SPEED_DEFAULT = 30f;
    // Camera rotate speed
    public final static String BUILD_ROTATE_SPEED = BUILD + "RotateSpeed";
    public final static float BUILD_ROTATE_SPEED_DEFAULT = 10f;
    
    public final static String BUILD_SNAP = BUILD + "Snap";
    // Location snapping
    public final static String BUILD_LOCATION_SNAP = BUILD_SNAP + "Location";
    public final static float BUILD_LOCATION_SNAP_DEFAULT = 0.1f;
    // Euler rotation snapping
    public final static String BUILD_EULER_ROTATION_SNAP = BUILD_SNAP + "EulerRotation";
    public final static float BUILD_EULER_ROTATION_SNAP_DEFAULT = 1f;
    // Scale snapping
    public final static String BUILD_SCALE_SNAP = BUILD_SNAP + "Scale";
    public final static float BUILD_SCALE_SNAP_DEFAULT = 0.1f;
    // Snap to floor
    public final static String BUILD_SNAP_TO_FLOOR = BUILD_SNAP + "ToFloor";
    public final static boolean BUILD_SNAP_TO_FLOOR_DEFAULT = true;
    /*
     * General application
     */
    public final static String APP = "app";
    public final static String APP_THEME = APP + "Theme";
    public final static String APP_THEME_DEFAULT = "Gold";
    public final static String APP_WORKSPACE_DIR = APP + "WorkspaceDir";
    public final static String APP_WORKSPACE_DIR_DEFAULT = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "Icedata";

    public static Object getDefaultValue(String key) {
        return AbstractConfig.getDefaultValue(SceneConfig.class, key);
    }

    public static Preferences get() {
        return Preferences.userRoot().node(SceneConstants.APPSETTINGS_NAME).node("game");
    }
}
