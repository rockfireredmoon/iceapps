package org.icescene.camera;

import java.util.logging.Logger;

import org.icescene.io.KeyMapManager;

import com.jme3.collision.MotionAllowedListener;
import com.jme3.input.FlyByCamera;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.renderer.Camera;

public class ExtendedFlyByCam extends FlyByCamera {

	private static String[] mappings = new String[] { "FLYCAM_Left", "FLYCAM_Right", "FLYCAM_Up", "FLYCAM_Down",
			"FLYCAM_StrafeLeft", "FLYCAM_StrafeRight", "FLYCAM_Forward", "FLYCAM_Backward", "FLYCAM_ZoomIn", "FLYCAM_ZoomOut",
			"FLYCAM_Rise", "FLYCAM_Lower", "FLYCAM_InvertY" };

	private final static Logger LOG = Logger.getLogger(ExtendedFlyByCam.class.getName());
	private boolean externalDragging;
	private boolean allowRiseAndLower = true;

	public ExtendedFlyByCam(Camera cam) {
		super(cam);
	}

	public boolean isCanRotate() {
		return canRotate;
	}

	public boolean isAllowRiseAndLower() {
		return allowRiseAndLower;
	}

	public void setAllowRiseAndLower(boolean allowRiseAndLower) {
		this.allowRiseAndLower = allowRiseAndLower;
	}

	@Override
	public void setMotionAllowedListener(MotionAllowedListener listener) {
		super.setMotionAllowedListener(listener);
	}

	public void setExternalDragging(boolean externalDragging) {
		this.externalDragging = externalDragging;
	}

	@Override
	public void onAction(String name, boolean value, float tpf) {
		if (!enabled) {
			return;
		}
		if (!allowRiseAndLower && (name.equals("FLYCAM_Rise") || name.equals("FLYCAM_Lower"))) {
			return;
		}
		super.onAction(name, value, tpf);
	}

	@Override
	public void onAnalog(String name, float value, float tpf) {
		if (!enabled) {
			return;
		}
		if (externalDragging
				&& (name.equals("FLYCAM_Left") || name.equals("FLYCAM_Right") || name.equals("FLYCAM_Up") || name
						.equals("FLYCAM_Down"))) {
			return;
		}

		super.onAnalog(name, value, tpf);
	}

	public void registerWithKeyMapManager(KeyMapManager keyMapManager) {

		/*
		 * A bit hacky, but we remove all the existing mappings and add our own
		 * provided by KeyMapManager
		 */

		inputManager.deleteMapping("FLYCAM_Left");
		inputManager.deleteMapping("FLYCAM_Right");
		inputManager.deleteMapping("FLYCAM_Up");
		inputManager.deleteMapping("FLYCAM_Down");
		inputManager.deleteMapping("FLYCAM_ZoomIn");
		inputManager.deleteMapping("FLYCAM_ZoomOut");
		inputManager.deleteMapping("FLYCAM_StrafeLeft");
		inputManager.deleteMapping("FLYCAM_StrafeRight");
		inputManager.deleteMapping("FLYCAM_Forward");
		inputManager.deleteMapping("FLYCAM_Backward");
		inputManager.deleteMapping("FLYCAM_Rise");
		inputManager.deleteMapping("FLYCAM_Lower");

		// both mouse and button - rotation of cam
		keyMapManager.addMapping("FLYCAM_Left", new MouseAxisTrigger(MouseInput.AXIS_X, true));
		keyMapManager.addMapping("FLYCAM_Right", new MouseAxisTrigger(MouseInput.AXIS_X, false));
		keyMapManager.addMapping("FLYCAM_Up", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
		keyMapManager.addMapping("FLYCAM_Down", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
		keyMapManager.addMapping("FLYCAM_ZoomIn", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
		keyMapManager.addMapping("FLYCAM_ZoomOut", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
		keyMapManager.addMapping("FLYCAM_StrafeLeft");
		keyMapManager.addMapping("FLYCAM_StrafeRight");
		keyMapManager.addMapping("FLYCAM_Forward");
		keyMapManager.addMapping("FLYCAM_Backward");
		keyMapManager.addMapping("FLYCAM_Rise");
		keyMapManager.addMapping("FLYCAM_Lower");

		keyMapManager.addListener(this, mappings);

	}
}
