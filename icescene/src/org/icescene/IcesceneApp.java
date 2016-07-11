package org.icescene;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.io.IOUtils;
import org.icelib.AppInfo;
import org.icelib.Color;
import org.icelib.QueueExecutor;
import org.icelib.RGB;
import org.icelib.beans.ObjectMapping;
import org.icescene.assets.Assets;
import org.icescene.assets.ImageLoader;
import org.icescene.audio.AudioAppState;
import org.icescene.camera.ExtendedFlyByCam;
import org.icescene.io.KeyMapManager;
import org.icescene.props.ComponentManager;
import org.icescene.scripting.ScriptAssetLoader;
import org.icescene.ui.WindowManagerAppState;
import org.icescripting.Scripts;
import org.iceui.IceUI;
import org.iceui.controls.XScreen;
import org.lwjgl.opengl.Display;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.VideoRecorderAppState;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderContext;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.lwjgl.LwjglRenderer;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeSystem;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;

import icemoon.iceloader.ServerAssetManager;
import icetone.core.Container;
import icetone.core.Element;
import icetone.core.Element.ZPriority;
import icetone.core.ElementManager;
import icetone.core.layout.AbstractLayout;
import icetone.core.layout.FillLayout;
import icetone.core.layout.GUIExplorerAppState;
import icetone.core.layout.LUtil;
import icetone.style.Style;

public class IcesceneApp extends SimpleApplication implements PreferenceChangeListener {

	private static final Logger LOG = Logger.getLogger(IcesceneApp.class.getName());

	private final static String MAPPING_RECORDER = "Recorder";
	private final static String MAPPING_ENGINE_DEBUG = "EngineDebug";
	private final static String MAPPING_RESET = "Reset";
	private final static String MAPPING_APPSTATE_DUMP = "AppStateDump";
	private final static String MAPPING_GUI_EXPLORER = "GUIExplorer";

	public static void defaultMain(String[] args, Class<? extends IcesceneApp> clazz, String appSettingsName)
			throws Exception {
		AppInfo.context = clazz;

		// Parse command line
		Options opts = createOptions();
		Assets.addOptions(opts);

		CommandLine cmdLine = parseCommandLine(opts, args);
		IcesceneApp app = clazz.getConstructor(CommandLine.class).newInstance(cmdLine);
		startApp(app, cmdLine, AppInfo.getName() + " - " + AppInfo.getVersion(), appSettingsName);
	}

	protected static AppSettings createSettings(IcesceneApp app) throws IOException {
		AppSettings settings = new AppSettings(true);
		// Our own custom defaults
		settings.putBoolean(SceneConstants.APPSETTINGS_RESIZABLE, true);
		settings.setWidth(1024);
		settings.setWidth(800);
		settings.setUseInput(true);
		// settings.setIcons(new Image[]{
		// ImageIO.read(app.getClass().getResource("/Interface/icon.png")),
		// ImageIO.read(app.getClass().getResource("/Interface/icon-32.png")),
		// ImageIO.read(app.getClass().getResource("/Interface/icon-16.png"))});
		return settings;
	}

	public interface AppListener {

		void reshape(int w, int h);
	}

	static {
		try {
			// System.setProperty("java.util.logging.SimpleFormatter.format",
			// "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s %5$s%6$s%n");
			// System.setProperty("java.util.logging.SimpleFormatter.format",
			// "%4$s %5$s%6$s%n");
			System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s %5$s%6$s%n");
		} catch (SecurityException se) {
			System.err.println("WARNING: Could not adjust logging settings due to security settings");
		}
	}

	public static Options createOptions() {
		Options opts = new Options();
		opts.addOption("r", "reset", false, "When present, game preferences will be reset.");
		opts.addOption("d", "settings-dialog", false,
				"When present, the default settings dialog will be displayed. Use this if you are having trouble with video modes.");
		opts.addOption("V", "verbose", false, "Verbose log output.");
		opts.addOption("D", "debug", false, "Debug log output.");
		opts.addOption("T", "trace", false, "Trace log output (everything).");
		opts.addOption("Q", "quiet", false, "Warnings and errors only.");
		opts.addOption("S", "silent", false, "No output at all.");
		opts.addOption("A", "assets", true, "Asset loader configuration resource path.");
		return opts;
	}

	public static CommandLine parseCommandLine(Options opts, String[] args) throws ParseException {
		CommandLineParser parse = new GnuParser();
		CommandLine cmdLine = parse.parse(opts, args);

		// Set logging level
		Level level = Level.WARNING;
		if (cmdLine.hasOption('S')) {
			level = Level.OFF;
		} else if (cmdLine.hasOption('Q')) {
			level = Level.WARNING;
		} else if (cmdLine.hasOption('V')) {
			level = Level.INFO;
		} else if (cmdLine.hasOption('D')) {
			level = Level.FINE;
		} else if (cmdLine.hasOption('T')) {
			level = Level.FINEST;
		}
		Logger root = Logger.getLogger("");
		Handler[] handlers = root.getHandlers();
		for (Handler h : handlers) {
			root.setLevel(level);
			h.setLevel(level);
		}
		return cmdLine;
	}

	protected static void startApp(IcesceneApp app, CommandLine cmdLine, String title, String appSettingsName)
			throws IOException {
		AppSettings settings = createSettings(app);

		// Whether to show recovery settings dialog
		app.setSettings(settings);
		if (cmdLine != null) {
			app.setShowSettings(cmdLine.hasOption('d'));

			// Load existing settings
			if (!cmdLine.hasOption('r')) {
				try {
					LOG.info(String.format("Loading settings %s", appSettingsName));
					settings.load(appSettingsName);
				} catch (BackingStoreException bse) {
					LOG.info("No settings, using defaults");
				}
			}
		} else {
			try {
				LOG.info(String.format("Loading settings %s", appSettingsName));
				settings.load(appSettingsName);
			} catch (BackingStoreException bse) {
				LOG.info("No settings, using defaults");
			}
		}
		settings.setTitle(title);
		app.setResizable(settings.getBoolean(SceneConstants.APPSETTINGS_RESIZABLE));

		LOG.info("Environment :-");
		for (Map.Entry<String, String> en : System.getenv().entrySet()) {
			LOG.info(String.format("    %s = %s", en.getKey(), en.getValue()));
		}
		LOG.info("System Properties :-");
		for (Map.Entry<Object, Object> en : System.getProperties().entrySet()) {
			LOG.info(String.format("    %s = %s", en.getKey(), en.getValue()));
		}

		// Start!
		app.start();

		// Save settings
		try {
			LOG.info(String.format("Saving settings %s", appSettingsName));
			settings.save(appSettingsName);
		} catch (BackingStoreException bse) {
			LOG.log(Level.SEVERE, "Failed to save settings.", bse);
		}
	}

	private Assets assets;
	protected Preferences prefs;
	private final CommandLine commandLine;
	private QueueExecutor worldLoaderExecutorService;
	private FileMonitor monitor;
	private boolean useUI;
	private String stylePath = SceneConstants.UI_DEFAULT_THEME;
	protected XScreen screen;
	private boolean resizable = false;
	private List<AppListener> appListeners = new ArrayList<AppListener>();
	private ThreadLocal<Boolean> sceneThread = new ThreadLocal<Boolean>();
	private String assetsResource = "META-INF/SceneAssets.cfg";
	private Alarm alarm;
	private ImageSwapper backgroundSwapper;
	private ViewPort backgroundViewport;
	private float ga;
	private Map<ZPriority, Element> layers = new HashMap<>();
	private final String appSettingsName;
	private KeyMapManager keyMapManager;
	private Map<Class<? extends IcesceneService>, IcesceneService> services = new HashMap<Class<? extends IcesceneService>, IcesceneService>();
	private List<String> initScripts = new ArrayList<String>();

	private ActionListener actionListener = new ActionListener() {
		private File recordingFile;

		public void onAction(String name, boolean isPressed, float tpf) {
			if (!isPressed) {
				if (name.equals(MAPPING_GUI_EXPLORER)) {
					GUIExplorerAppState state = stateManager.getState(GUIExplorerAppState.class);
					if (state == null) {
						stateManager.attach(new GUIExplorerAppState(screen));
					} else {
						stateManager.detach(state);
					}
				} else if (name.equals(MAPPING_ENGINE_DEBUG)) {
					int current = prefs.getInt(SceneConfig.DEBUG_ENGINE, SceneConfig.DEBUG_ENGINE_DEFAULT);
					current++;
					if (current > SceneConfig.DEBUG_ENGINE_FULLSTATS) {
						current = SceneConfig.FALSE;
					}
					prefs.putInt(SceneConfig.DEBUG_ENGINE, current);
					checkEngineDebug();
				} else if (name.equals(MAPPING_APPSTATE_DUMP)) {
					try {
						Method m = AppStateManager.class.getDeclaredMethod("getStates");
						m.setAccessible(true);
						for (AppState a : (AppState[]) m.invoke(stateManager)) {
							System.err.println(a.getClass() + " = " + a);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (name.equals(MAPPING_RECORDER)) {
					HUDMessageAppState has = stateManager.getState(HUDMessageAppState.class);
					VideoRecorderAppState rec = stateManager.getState(VideoRecorderAppState.class);
					if (rec != null) {
						LOG.info(String.format("Stopped recording to ", recordingFile));
						stateManager.detach(rec);
						if (has != null) {
							has.message(Level.INFO, "Stopped Recording!");
						}
					} else {
						File dir = new File(assets.getExternalAssetsFolder(), "Videos");
						if (!dir.exists() && !dir.mkdirs()) {
							LOG.severe(String.format("Failed to create videos directory %s", dir));
							if (has != null) {
								has.message(Level.SEVERE, String.format("Failed to create videos directory %s", dir));
							}
						} else {
							recordingFile = new File(dir,
									new SimpleDateFormat("ddMMyy-HHmmss").format(new Date()) + ".mjpeg");
							LOG.info(String.format("Recording to %s", recordingFile));
							if (has != null) {
								has.message(Level.INFO, "Recording!");
							}
							stateManager.attach(new VideoRecorderAppState(recordingFile));
						}
					}
				} else if (name.equals(MAPPING_RESET)) {
					for (Element el : screen.getElements()) {
						screen.updateZOrder(el);
					}
					LOG.info("Reset all Z");
				}
			}
		}
	};
	private Scripts scripts;
	private ComponentManager componentManager;

	private Texture2D backgroundTexture;

	protected IcesceneApp() {
		this(SceneConfig.get(), null, SceneConstants.APPSETTINGS_NAME, null);
	}

	protected IcesceneApp(Preferences prefs, CommandLine commandLine, String appSettingsName,
			String defaultAssetConfigurationResourcePath) {
		super();

		initScripts.add("Scripts/init.js");
		initScripts.add("Scripts/init.nut");

		this.prefs = prefs;
		this.commandLine = commandLine;
		this.appSettingsName = appSettingsName;

		if (commandLine != null && commandLine.hasOption('A')) {
			assetsResource = commandLine.getOptionValue('A');
		} else {
			assetsResource = defaultAssetConfigurationResourcePath == null ? "META-INF/SceneAssets.cfg"
					: defaultAssetConfigurationResourcePath;
		}
		if (assetsResource == null) {
			throw new IllegalArgumentException("No asset loader configuration resource provided.");
		}

		// Some custom converters for mapping script-to-java systems
		ObjectMapping.addObjectConverter(String.class, ColorRGBA.class, new Transformer<String, ColorRGBA>() {
			@Override
			public ColorRGBA transform(String input) {
				return IceUI.toRGBA(new Color(input));
			}
		});
		ObjectMapping.addObjectConverter(String.class, Color.class, new Transformer<String, Color>() {
			@Override
			public Color transform(String input) {
				return new Color(input);
			}
		});
		ObjectMapping.addObjectConverter(String.class, RGB.class, new Transformer<String, RGB>() {
			@Override
			public RGB transform(String input) {
				return new Color(input);
			}
		});
	}

	@SuppressWarnings("unchecked")
	public <T extends IcesceneService> T getService(Class<T> serviceClass) {
		return (T) services.get(serviceClass);
	}

	/**
	 * Register the global basic input mappings.
	 */
	public void registerAllInput() {
		keyMapManager.addMapping(MAPPING_RESET);
		keyMapManager.addMapping(MAPPING_GUI_EXPLORER);
		keyMapManager.addMapping(MAPPING_ENGINE_DEBUG);
		keyMapManager.addMapping(MAPPING_RECORDER);
		keyMapManager.addMapping(MAPPING_APPSTATE_DUMP);
		keyMapManager.addListener(actionListener, new String[] { MAPPING_GUI_EXPLORER, MAPPING_ENGINE_DEBUG,
				MAPPING_RECORDER, MAPPING_RESET, MAPPING_APPSTATE_DUMP });
	}

	/**
	 * Unregister the global basic input mappings.
	 */
	public void unregisterAllInput() {
		keyMapManager.deleteMapping(MAPPING_GUI_EXPLORER);
		keyMapManager.deleteMapping(MAPPING_RESET);
		keyMapManager.deleteMapping(MAPPING_ENGINE_DEBUG);
		keyMapManager.deleteMapping(MAPPING_RECORDER);
		keyMapManager.deleteMapping(MAPPING_APPSTATE_DUMP);
		keyMapManager.removeListener(actionListener);
	}

	@Override
	public void restart() {
		Display.setResizable(settings.getBoolean(SceneConstants.APPSETTINGS_RESIZABLE));
		super.restart();
	}

	public AppSettings getSettings() {
		return settings;

	}

	/**
	 * Convenience method to get a properly casted extended fly-by camera
	 * reference.
	 * 
	 * @return extended fly-by camera
	 */
	public ExtendedFlyByCam getExtendedFlyByCamera() {
		return (ExtendedFlyByCam) flyCam;
	}

	@Override
	public void start() {
		if (settings != null) {
			JmeSystem.initialize(settings);

			LOG.info(String.format("Screen size settings of %d x %d", settings.getWidth(), settings.getHeight()));
			if (settings.getBoolean(SceneConstants.APPSETTINGS_RESIZABLE)) {
				final int wx = settings.getInteger(SceneConstants.APPSETTINGS_WINDOW_X);
				final int wy = settings.getInteger(SceneConstants.APPSETTINGS_WINDOW_Y);
				LOG.info(String.format("Positioning window at %d, %d", wx, wy));
				Display.setLocation(wx, wy);

			}
		}

		super.start();
	}

	/**
	 * Get the command line provided when the game was launched.
	 * 
	 * @return command line
	 */
	public CommandLine getCommandLine() {
		return commandLine;
	}

	public Preferences getPreferences() {
		return prefs;
	}

	public Element getLayers(ZPriority priority) {
		Element el = layers.get(priority);
		if (el == null) {
			el = new Container(screen);
			el.setLayoutManager(new FillLayout());
			el.setDimensions(LUtil.getScreenSize(screen));
			el.setPriority(priority);
			screen.addElement(el);
			layers.put(priority, el);
		}
		return el;
	}

	@Override
	public void reshape(int w, int h) {
		super.reshape(w, h);
		if (screen != null) {
			// layers.dirtyLayout(true);
			screen.dirtyLayout();
			screen.layoutChildren();

			// layers.setDimensions(LUtil.getScreenSize(screen));

			// Make sure no top level components are now outside of the screen
			// area
			// for (Element el : screen.getElements()) {
			// if (el.getX() + el.getWidth() >= screen.getWidth()) {
			// el.setX(screen.getWidth() - el.getWidth());
			// }
			// if (el.getY() + el.getHeight() >= screen.getHeight()) {
			// el.setY(screen.getHeight() - el.getHeight());
			// }
			// }
		}
	}

	public List<String> getInitScripts() {
		return initScripts;
	}

	public Assets getAssets() {
		return assets;
	}

	public Alarm getAlarm() {
		return alarm;
	}

	public synchronized FileMonitor getMonitor() {
		if (monitor == null) {
			try {
				monitor = new FileMonitor();
				monitor.start();
			} catch (IOException ex) {
				throw new RuntimeException("Cannot monitor files for changes.", ex);
			}
		}
		return monitor;
	}

	public XScreen getScreen() {
		if (!useUI) {
			throw new IllegalStateException("UI not in use.");
		}
		return screen;
	}

	public String getAssetsResource() {
		return assetsResource;
	}

	public void addListener(AppListener appListener) {
		appListeners.add(appListener);
	}

	public void removeListener(AppListener appListener) {
		appListeners.remove(appListener);
	}

	public boolean isResizable() {
		return resizable;
	}

	public void setResizable(boolean resizable) {
		this.resizable = resizable;
	}

	public void setStylePath(String stylePath) {
		this.stylePath = stylePath;
	}

	public boolean isUseUI() {
		return useUI;
	}

	public void setUseUI(boolean useUI) {
		this.useUI = useUI;
	}

	public Scripts getScripts() {
		return scripts;
	}

	@SuppressWarnings("unchecked")
	@Override
	public final void initialize() {

		sceneThread.set(Boolean.TRUE);
		try {
			LOG.info(String.format("Configuring asset loaders and locators from %s", assetsResource));
			assetManager = new ServerAssetManager(
					Thread.currentThread().getContextClassLoader().getResource(assetsResource));

			// Watch for preference changes
			String dir = prefs.get(SceneConfig.ASSETS_EXTERNAL_LOCATION, SceneConfig.ASSETS_EXTERNAL_LOCATION_DEFAULT)
					.trim();
			prefs.addPreferenceChangeListener(this);
			assets = new Assets(this, dir, commandLine);
			configureAssetManager((ServerAssetManager) assetManager);

			alarm = new Alarm(this);
			worldLoaderExecutorService = new QueueExecutor(SceneConstants.MAX_WORLD_LOAD_THREADS);

			// This will create the asset indexes for all locators that support
			// indexing.
			// Also allows fast freshness check for remote assets
			((ServerAssetManager) assetManager).index();

			// Load all of the services
			// TODO should move all ice* stuff into org.ice.
			try {
				Reflections reflections = new Reflections("org", new FieldAnnotationsScanner(),
						new TypeAnnotationsScanner());
				LOG.info("Loading services");
				for (Class<?> c : reflections.getTypesAnnotatedWith(Service.class)) {
					IcesceneService service = (IcesceneService) c.newInstance();
					service.init(this);
					LOG.info(String.format("Initialising %s", c.getName()));
					services.put((Class<? extends IcesceneService>) c, service);
				}

				for (Field f : reflections.getFieldsAnnotatedWith(ServiceRef.class)) {
					LOG.info(String.format("Injecting %s", f));
					f.setAccessible(true);
					f.set(null, services.get(f.getType()));
				}
			} catch (Exception e1) {
				throw new RuntimeException("Could not initialise services.", e1);
			}

			// Now can create the scripting system
			try {
				scripts = new Scripts(new ScriptAssetLoader((ServerAssetManager) assetManager));
				for (Map.Entry<Class<? extends IcesceneService>, IcesceneService> en : services.entrySet()) {
					String bindName = "__" + en.getKey().getSimpleName();
					LOG.info(String.format("Binding %s to %s", bindName, en.getValue()));
					scripts.getBindings().put(bindName, en.getValue());
					scripts.getBindings().put(en.getKey().getSimpleName(), en.getValue());
				}

				// Load the first script
				for (String initScript : initScripts) {
					scripts.eval(initScript);
				}
			} catch (Exception e1) {
				throw new RuntimeException("Could not initialise script system.", e1);
			}

			Display.setResizable(resizable);

			/*
			 * To work around the fact that when dragging is disabled in the
			 * middle of click, the view continues draggings. This is not good
			 * for {@link BuildAppState} for when we want to use dragging for
			 * resizing, moving and rotating.
			 */
			FlyCamAppState fa = stateManager.getState(FlyCamAppState.class);
			stateManager.detach(fa);

			// Thread.setDefaultUncaughtExceptionHandler(new
			// Thread.UncaughtExceptionHandler() {
			// public void uncaughtException(Thread t, Throwable e) {
			// new ErrorReport(e);
			// }
			// });

			// super.initialize();
			// DesktopAssetManager dam = (DesktopAssetManager)
			// getAssetManager();
			// dam.registerLoader(ImageLoader.class, "png");

			preInitialize();

			super.initialize();
			try {
				keyMapManager = new KeyMapManager(AppInfo.getName(), prefs.node("keymap"),
						(ServerAssetManager) assetManager, inputManager);
			} catch (IOException e) {
				throw new RuntimeException("Failed to configure key map.", e);
			}
			if (flyCam != null && flyCam instanceof ExtendedFlyByCam) {
				((ExtendedFlyByCam) flyCam).registerWithKeyMapManager(keyMapManager);
			}

			//
			getInputManager().deleteMapping(INPUT_MAPPING_EXIT);
			checkEngineDebug();
			registerAllInput();

			onInitialize();

			try {
				Field f = LwjglRenderer.class.getDeclaredField("context");
				f.setAccessible(true);
				RenderContext ctx = (RenderContext) f.get(getRenderer());
				ctx.boundTextures = new Image[32];
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "Could not raise number of texture units.", e);
			}

			// TODO this doesnt seem right...
			// super.update();
		} finally {
			sceneThread.remove();
		}
	}

	public KeyMapManager getKeyMapManager() {
		return keyMapManager;
	}

	public QueueExecutor getWorldLoaderExecutorService() {
		return worldLoaderExecutorService;
	}

	@Override
	public void destroy() {
		saveSettings();
		super.destroy();
		if (monitor != null) {
			LOG.info("Stopping monitor");
			alarm.stop();
		}
		if (alarm != null) {
			LOG.info("Stopping alarm");
			alarm.stop();
		}
		worldLoaderExecutorService.shutdown();
		LOG.info("Destroyed application");
	}

	protected void configureAssetManager(ServerAssetManager serverAssetManager) {
		// For applications to configure asset locations and stuff
	}

	public void run(final Runnable runnable) {
		enqueue(new Callable<Void>() {
			public Void call() throws Exception {
				runnable.run();
				return null;
			}
		});
	}

	@Override
	public final void simpleUpdate(float tpf) {
		if (Display.wasResized()) {
			final int width = Display.getWidth();
			final int height = Display.getHeight();
			LOG.info(String.format("Window resized to %d X %d", width, height));
			this.settings.setWidth(width);
			this.settings.setHeight(height);
			reshape(this.settings.getWidth(), this.settings.getHeight());
			for (AppListener l : appListeners) {
				l.reshape(width, height);
			}
			screen.dirtyLayout();
			screen.layoutChildren();
			// if (screen instanceof LayoutAware && screen.getLayoutManager() !=
			// null) {
			// ((LayoutAware) screen).getLayoutManager().layoutScreen(screen);
			// }
			saveSettings();
			onResize();
		}

		if (backgroundSwapper != null) {
			backgroundSwapper.updateLogicalState(tpf);
		}

		onUpdate(tpf);
	}

	/**
	 * Get if the current thread is the scene thread.
	 * 
	 * @return is scene thread
	 */
	public boolean isSceneThread() {
		return Boolean.TRUE.equals(sceneThread.get());
	}

	@Override
	public void update() {
		sceneThread.set(Boolean.TRUE);
		try {
			try {
				super.update();
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "Main loop failed.", e);
				throw new RuntimeException("Main loop failed.", e);
			}
		} finally {
			sceneThread.remove();
		}
	}

	/**
	 * Remove the current background picture.
	 * 
	 * @see #setBackgroundPicture(java.lang.String)
	 */
	public void removeBackgroundPicture() {
		if (backgroundSwapper == null)
			LOG.warning(String.format("Request to remove picture when none is set"));
		else {
			backgroundViewport.detachScene(backgroundSwapper);
			getRenderManager().removePreView(backgroundViewport);
			backgroundSwapper = null;
			getViewPort().setClearFlags(true, true, true);
		}
	}

	/**
	 * Set an image to use as a background to the scene. Used in the lobby.
	 * 
	 * @param path
	 *            path
	 */
	public void setBackgroundPicture(Texture2D texture) {
		setBackgroundPicture(texture, true);
	}

	public void setBackgroundPicture(Texture2D texture, boolean fadeIn) {
		if (backgroundSwapper == null) {
			backgroundSwapper = new ImageSwapper(new Vector2f(screen.getWidth(), screen.getHeight()), assetManager);
		}
		if (!Objects.equals(texture, backgroundTexture)) {
			backgroundTexture = texture;
			backgroundSwapper.setPicture(texture, fadeIn);
			backgroundViewport = getRenderManager().createPreView("background", getCamera());
			backgroundViewport.setClearFlags(true, true, true);
			backgroundViewport.attachScene(backgroundSwapper);
			getViewPort().setClearFlags(false, true, true);
			backgroundSwapper.updateGeometricState();
		}
	}

	/**
	 * Set an image to use as a background to the scene. Used in the lobby.
	 * 
	 * @param path
	 *            path
	 */
	public void setBackgroundPicture(String path) {
		setBackgroundPicture(path, true);
	}

	public void setBackgroundPicture(String path, boolean fadeIn) {
		LOG.info(String.format("Setting background picture %s", path));
		setBackgroundPicture((Texture2D) getAssetManager().loadTexture(path), fadeIn);
	}

	public void loadExternalBackground(URL url, String cacheName, boolean fade, boolean onlyIfCached)
			throws IOException {
		final Texture2D tex2D = loadExternalCachableTexture(url, cacheName, onlyIfCached);
		enqueue(new Callable<Void>() {
			@Override
			public Void call() throws Exception {
				setBackgroundPicture(tex2D, fade);
				return null;
			}
		});
	}

	public Texture2D loadExternalCachableTexture(URL url, String cacheName, boolean onlyIfCached) throws IOException {
		// Do we have a locally cached copy?
		File cacheDir = new File(new File(System.getProperty("java.io.tmpdir")), AppInfo.getName());
		if (!cacheDir.exists() && !cacheDir.mkdirs())
			throw new IOException("Could not create cache directory.");

		File cacheFile = new File(cacheDir, URLEncoder.encode(cacheName, "UTF-8") + ".jpg");
		if (onlyIfCached && !cacheFile.exists())
			throw new IOException("Not cached, and load onlyIfCached was required.");

		LOG.info(String.format("Loading background from %s", url));
		HttpURLConnection conx = (HttpURLConnection) url.openConnection();
		conx.setDoInput(true);
		conx.setConnectTimeout(10000);
		if (cacheFile.exists()) {
			conx.setIfModifiedSince(cacheFile.lastModified());
		}
		int resp = conx.getResponseCode();
		if (resp == 200) {
			FileOutputStream fos = new FileOutputStream(cacheFile);
			try {
				IOUtils.copy(conx.getInputStream(), fos);
			} finally {
				fos.close();
			}
		} else if (resp == 304) {
			// Not modified, use cache
		} else if (resp == 404) {
			throw new FileNotFoundException(String.format("Could not find %s", url));
		} else
			throw new IOException(String.format("Unexpected server response %d.", resp));

		ImageLoader il = new ImageLoader();
		InputStream in = new FileInputStream(cacheFile);
		final Texture2D tex2D = new Texture2D(il.load(in, true));
		return tex2D;
	}

	// public void setBackgroundPicture(Picture picture, boolean fadeIn) {
	// if (this.picture != null) {
	//
	// if(fadeIn) {
	// if(fadeOutBackgroundViewport != null) {
	// fadeOutBackgroundViewport.detachScene(fadeOutPicture);
	// getRenderManager().removePreView(fadeOutBackgroundViewport);
	// fadeOutBackgroundViewport = null;
	// fadeOutPicture = null;
	// }
	//
	// fadeOutBackgroundViewport = backgroundViewport;
	// fadeOutPicture = picture;
	// }
	// else {
	// backgroundViewport.detachScene(picture);
	// getRenderManager().removePreView(backgroundViewport);
	// }
	// }
	//
	// this.picture = picture;
	// backgroundViewport = getRenderManager().createPreView("background",
	// getCamera());
	// backgroundViewport.setClearFlags(true, true, true);
	// backgroundViewport.attachScene(picture);
	// // if (foregroundPicture == null) {
	// getViewPort().setClearFlags(false, true, true);
	// // }
	// picture.updateGeometricState();
	// }

	/**
	 * Reload the current background picture, resizing it the size of the screen
	 */
	public void reloadBackgroundPicture() {
		if (backgroundSwapper != null) {
			backgroundSwapper.setSize(new Vector2f(screen.getWidth(), screen.getHeight()));
			backgroundSwapper.reloadBackgroundPicture();
			// backgroundViewport.detachScene(picture);
			// getRenderManager().removePreView(backgroundViewport);
			// Picture picture = new Picture("background");
			// picture.setTexture(getAssetManager(),
			// (Texture2D)
			// this.picture.getMaterial().getTextureParam("Texture").getTextureValue(),
			// false);
			// picture.setWidth(screen.getWidth());
			// picture.setHeight(screen.getHeight());
			// picture.setPosition(0, 0);
			// setBackgroundPicture(picture, false);
		}
	}

	public void preferenceChange(PreferenceChangeEvent evt) {
		if (evt.getKey().equals(SceneConfig.ASSETS_EXTERNAL_LOCATION)) {
			assets.setAssetsExternalLocation(evt.getNewValue());
			((ServerAssetManager) assetManager).index();
		} else if (evt.getKey().equals(SceneConfig.AUDIO_UI_VOLUME)
				|| evt.getKey().equals(SceneConfig.AUDIO_MASTER_VOLUME)) {
			screen.setUIAudioVolume(AudioAppState.get().getActualUIVolume());
		}
	}

	protected void onUpdate(float tpf) {
	}

	protected void onResize() {
		// Invoked when the window is resized (when resizing is enabled)
	}

	protected void preInitialize() {
	}

	protected void onInitialize() {
	}

	@Override
	public final void simpleInitApp() {

		// Default XHTML renderer configuration
		System.setProperty("xr.conf", getClass().getResource("/Interface/Styles/Gold/XHTML/xmlrender.conf").toString());

		// Login dialog
		if (useUI) {
			createScreen();
			configureScreen();
			getGuiNode().addControl(screen);
		}

		flyCam = new ExtendedFlyByCam(cam);
		flyCam.registerWithInput(inputManager);
		flyCam.setEnabled(false);

		// screen.getStylesheets().add(assetManager.loadAsset(new
		// AssetKey<Stylesheet>("Interface/Css/default.css")));

		onSimpleInitApp();
	}

	protected void onSimpleInitApp() {
	}

	protected void configureScreen() {
		screen.setUseToolTips(true);
		screen.setUseCustomCursors(true);
		screen.setUseUIAudio(true);
		screen.setGlobalAlpha(0.9f);
	}

	protected void createScreen() {
		screen = new XScreen(this, stylePath) {

			@Override
			protected void defaultClick() {
				if (getTabFocusElement() != null) {
					WindowManagerAppState was = stateManager.getState(WindowManagerAppState.class);
					if (was != null) {
						was.deselectAllWindows();
					}
				}

				super.defaultClick();
			}
		};
		Style style = screen.getStyle("Common");
		if (style == null) {

			throw new RuntimeException("Style map '" + stylePath + "' could not be found.\n"
					+ "This could mean a number of things. For example, the style map is not on the\n"
					+ "class path, and it could not be retrieved through any other asset locators\n"
					+ "that may be in use (such as retrieval from a server, a cache, or other service).");
		}

		screen.setLayoutManager(new AppLayout());
	}

	protected void saveSettings() {
		try {
			LOG.info(String.format("Saving settings %s", appSettingsName));
			settings.putInteger(SceneConstants.APPSETTINGS_WINDOW_X, Display.getX());
			settings.putInteger(SceneConstants.APPSETTINGS_WINDOW_Y, Display.getY());
			settings.save(appSettingsName);
		} catch (BackingStoreException bse) {
			LOG.log(Level.SEVERE, "Failed to save settings.", bse);
		}
	}

	private void checkEngineDebug() {
		int cfg = prefs.getInt(SceneConfig.DEBUG_ENGINE, SceneConfig.DEBUG_ENGINE_DEFAULT);
		setDisplayFps(cfg != SceneConfig.FALSE);
		setDisplayStatView(cfg == SceneConfig.DEBUG_ENGINE_FULLSTATS);
	}

	public String getAppSettingsName() {
		return appSettingsName;
	}

	public ComponentManager getComponentManager() {
		if (componentManager == null) {
			componentManager = new ComponentManager(this);
		}
		return componentManager;
	}

	class AppLayout extends AbstractLayout {

		@Override
		public Vector2f minimumSize(Element parent) {
			return new Vector2f(screen.getWidth(), screen.getHeight());
		}

		@Override
		public Vector2f maximumSize(Element parent) {
			return minimumSize(parent);
		}

		@Override
		public Vector2f preferredSize(Element parent) {
			return preferredSize(parent);
		}

		@Override
		public void layoutScreen(ElementManager screen) {
			for (Map.Entry<ZPriority, Element> en : layers.entrySet()) {
				LUtil.setBounds(en.getValue(), 0, 0, screen.getWidth(), screen.getHeight());
			}
			for (Element el : screen.getElements()) {
				if (el.getX() + el.getWidth() >= screen.getWidth()) {
					el.setX(screen.getWidth() - el.getWidth());
				}
				if (el.getY() + el.getHeight() >= screen.getHeight()) {
					el.setY(screen.getHeight() - el.getHeight());
				}
			}
		}

		@Override
		public void constrain(Element child, Object constraints) {
		}

		@Override
		public void remove(Element child) {
		}

		@Override
		public void layout(Element container) {
		}

	}
}
