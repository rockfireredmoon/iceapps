package org.icescene.camera;

import java.util.prefs.Preferences;

import org.icescene.SceneConfig;
import org.icescene.io.ModifierKeysAppState;
import org.icescene.io.ModifierKeysAppState.Listener;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.FlyByCamera;

public class CameraSpeedAppState extends AbstractAppState implements Listener {

	private ModifierKeysAppState mods;
	private FlyByCamera flyCam;
	private Preferences prefs;
	private float ctrlShiftSpeed = 0.25f;
	private float shiftSpeed = 5f;
	private float ctrlSpeed = 15f;
	private float rotSpeed;
	private float zoomSpeed;
	private float moveSpeed;

	public CameraSpeedAppState(FlyByCamera flyCam, Preferences prefs) {
		this.flyCam = flyCam;
		this.prefs = prefs;
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);

		mods = stateManager.getState(ModifierKeysAppState.class);
		if (mods == null) {
			throw new IllegalStateException(
					getClass().getName() + " requires " + ModifierKeysAppState.class.getName() + " to be attached.");
		}

		rotSpeed = flyCam.getRotationSpeed();
		zoomSpeed = flyCam.getZoomSpeed();
		moveSpeed = flyCam.getMoveSpeed();

		// For now these are not adjusted
		flyCam.setRotationSpeed(prefs.getFloat(SceneConfig.BUILD_ROTATE_SPEED, SceneConfig.BUILD_ROTATE_SPEED_DEFAULT));
		flyCam.setZoomSpeed(prefs.getFloat(SceneConfig.BUILD_ZOOM_SPEED, SceneConfig.BUILD_ZOOM_SPEED_DEFAULT));

		setSpeedForModifiers();
		mods.addListener(this);
	}

	@Override
	public void cleanup() {
		super.cleanup();

		flyCam.setRotationSpeed(rotSpeed);
		flyCam.setMoveSpeed(moveSpeed);
		flyCam.setZoomSpeed(zoomSpeed);

		mods.removeListener(this);
	}

	@Override
	public void modifiersChange(int newMods) {
		setSpeedForModifiers();
	}

	private void setSpeedForModifiers() {
		float f = 1;
		if (mods.isCtrl() && mods.isShift()) {
			f = ctrlShiftSpeed;
		} else if (mods.isCtrl()) {
			f = ctrlSpeed;
		} else if (mods.isShift()) {
			f = shiftSpeed;
		}

		flyCam.setMoveSpeed(prefs.getFloat(SceneConfig.BUILD_MOVE_SPEED, SceneConfig.BUILD_MOVE_SPEED_DEFAULT) * f);
	}
}
