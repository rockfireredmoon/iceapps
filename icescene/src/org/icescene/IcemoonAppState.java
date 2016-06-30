package org.icescene;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

import org.icescripting.Scripts;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

import icetone.core.ElementManager;

public abstract class IcemoonAppState<T extends IcemoonAppState> extends AbstractAppState implements PreferenceChangeListener {

	private final static Logger LOG = Logger.getLogger(IcemoonAppState.class.getName());
	private List<String> keyPatterns = new ArrayList<String>();
	// private boolean inputRegistered;
	protected AppStateManager stateManager;
	protected IcesceneApp app;
	protected T parent;
	protected AssetManager assetManager;
	protected Node rootNode;
	protected InputManager inputManager;
	protected Camera camera;
	protected AppSettings settings;
	protected Node guiNode;
	protected ElementManager screen;
	protected Preferences prefs;
	protected Scripts scripts;

	protected IcemoonAppState(Preferences prefs) {
		this.prefs = prefs;
	}

	public Preferences getPreferences() {
		return prefs;
	}

	@Override
	public final void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);

		scripts = Scripts.get();

		LOG.info(String.format("Initialize %s", getClass().toString()));
		this.app = (IcesceneApp) app;
		this.screen = this.app.getScreen();
		this.stateManager = stateManager;
		this.assetManager = this.app.getAssetManager();
		this.rootNode = this.app.getRootNode();
		this.inputManager = this.app.getInputManager();
		this.camera = this.app.getCamera();
		this.settings = this.app.getContext().getSettings();
		this.guiNode = this.app.getGuiNode();

		parent = onInitialize(stateManager, this.app);
		postInitialize();

		prefs.addPreferenceChangeListener(this);

		// Do this appstates input
		doRegisterAllInput();
		// inputRegistered = true;
	}

	public IcesceneApp getApp() {
		return app;
	}

	public T getParent() {
		return parent;
	}

	public void error(Exception exception) {
		message(Level.SEVERE, null, exception);
	}

	public void error(String message) {
		error(message, null);
	}

	public void error(String message, Exception exception) {
		message(Level.SEVERE, message, exception);
	}

	public void info(String message) {
		message(Level.INFO, message, null);
	}

	public void message(Level level, String message) {
		app.getStateManager().getState(HUDMessageAppState.class).message(level, message);
	}

	public void message(Level level, String message, Exception exception) {
		LOG.log(level, message, exception);
		HUDMessageAppState hud = app.getStateManager().getState(HUDMessageAppState.class);
		if (hud != null) {
			hud.message(level, message, exception);
		}
	}

	protected void detachIfAttached(Class<? extends AppState> appState) {
		final AppState state = stateManager.getState(appState);
		if (state != null) {
			stateManager.detach(state);
		}
	}

	protected void postInitialize() {
		// Hook for initializing that needs parent appstate to be set
	}

	// /**
	// * Unregister this appstate, and the parent's and so one. Used for example
	// by the
	// * console that wants complete control of the keyboard.
	// */
	// public final void unregisterAllInput() {
	// if (parent != null) {
	// parent.unregisterAllInput();
	// } else {
	// app.unregisterAllInput();
	// }
	// if (inputRegistered) {
	// doUnregisterAllInput();
	// inputRegistered = false;
	// }
	// }
	//
	protected void doUnregisterAllInput() {
		// For sub-classes to unregister their own input
	}

	/**
	 * Register this appstate, and the parent's and so one. Used for example by
	 * the console that wants complete control of the keyboard.
	 */
	// public final void registerAllInput() {
	// if (parent != null) {
	// parent.registerAllInput();
	// } else {
	// app.registerAllInput();
	// }
	// if (!inputRegistered) {
	// doRegisterAllInput();
	// inputRegistered = true;
	// }
	// }
	//
	protected void doRegisterAllInput() {
		// For sub-classes to register their own input
	}

	protected void addPrefKeyPattern(String keyPattern) {
		keyPatterns.add(keyPattern);
	}

	protected T onInitialize(AppStateManager stateManager, IcesceneApp app) {
		this.app = (IcesceneApp) app;
		this.stateManager = app.getStateManager();
		return null;
	}

	@Override
	public final void cleanup() {
		super.cleanup();
		LOG.info(String.format("Cleaning up %s", getClass().toString()));
		// if (inputRegistered) {
		doUnregisterAllInput();
		// }
		// inputRegistered = false;
		try {
		prefs.removePreferenceChangeListener(this);
		}
		catch(IllegalArgumentException iae) {
		}
		onCleanup();
	}

	protected void handlePrefUpdatePrefThread(PreferenceChangeEvent evt) {
	}

	protected void handlePrefUpdateSceneThread(PreferenceChangeEvent evt) {
	}

	protected void onCleanup() {
	}

	public final void preferenceChange(final PreferenceChangeEvent evt) {
		for (String k : keyPatterns) {
			if (evt.getKey().matches(k)) {
				handlePrefUpdatePrefThread(evt);
				app.enqueue(new Callable<Void>() {
					public Void call() throws Exception {
						handlePrefUpdateSceneThread(evt);
						return null;
					}
				});
			}
		}
		onPreferenceChange(evt);
	}

	protected void onPreferenceChange(PreferenceChangeEvent pce) {
	}
}
