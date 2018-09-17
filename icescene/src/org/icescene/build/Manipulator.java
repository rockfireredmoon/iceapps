package org.icescene.build;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.icescene.EditDirection;
import org.icescene.Icescene;
import org.icescene.assets.ExtendedMaterialListKey;
import org.icescene.assets.ExtendedMaterialListKey.Lighting;
import org.icescene.assets.ExtendedOgreMeshKey;
import org.icescene.entities.EntityContext;
import org.icescene.materials.MaterialUtil;
import org.icescene.props.AbstractProp;

import com.jme3.material.Material;
import com.jme3.material.MaterialList;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 * The manipulator sceneryItem.
 */
public class Manipulator extends AbstractProp {

	static final Logger LOG = Logger.getLogger(Manipulator.class.getName());
	static final ColorRGBA DESELECTED_COLOR = ColorRGBA.Cyan.mult(0.75f);

	private Spatial upSpatial;
	private Spatial downSpatial;
	private Spatial leftSpatial;
	private Spatial rightSpatial;
	private Spatial backwardSpatial;
	private Spatial forwardSpatial;
	private Spatial rotateYaw1;
	private Spatial rotateYaw2;
	private Spatial rotateYaw4;
	private Spatial rotateYaw3;
	private Spatial rotatePitch;
	private Spatial rotateRoll;

	public Manipulator(EntityContext app) {
		super("EditCursor", app);
		spatial.setShadowMode(RenderQueue.ShadowMode.Off);
		spatial.setQueueBucket(RenderQueue.Bucket.Translucent);
	}

	@Override
	public void onConfigureProp() {
		float dis = 10;
		float scale = 0.75f;

		// Up
		upSpatial = createSpatial("Up", "Manipulator/Manipulator-Movement_Arrow.mesh.xml");
		spatial.attachChild(upSpatial);
		upSpatial.move(0, dis, 0);
		upSpatial.setLocalScale(scale);
		propSpatials.add(upSpatial);

		// Down
		downSpatial = createSpatial("Down", "Manipulator/Manipulator-Movement_Arrow.mesh.xml");
		downSpatial.setLocalScale(scale);
		downSpatial.move(0, -dis, 0);
		downSpatial.rotate(0, 0, FastMath.PI);
		propSpatials.add(downSpatial);
		spatial.attachChild(downSpatial);

		// Left
		leftSpatial = createSpatial("Left", "Manipulator/Manipulator-Movement_Arrow.mesh.xml");
		leftSpatial.setLocalScale(scale);
		leftSpatial.move(-dis, 0, 0);
		leftSpatial.rotate(0, 0, FastMath.HALF_PI);
		propSpatials.add(leftSpatial);
		spatial.attachChild(leftSpatial);

		// Right
		rightSpatial = createSpatial("Right", "Manipulator/Manipulator-Movement_Arrow.mesh.xml");
		rightSpatial.setLocalScale(scale);
		rightSpatial.move(dis, 0, 0);
		rightSpatial.rotate(0, 0, -FastMath.HALF_PI);
		propSpatials.add(rightSpatial);
		spatial.attachChild(rightSpatial);

		// Backward
		backwardSpatial = createSpatial("Backward", "Manipulator/Manipulator-Movement_Arrow.mesh.xml");
		backwardSpatial.setLocalScale(scale);
		backwardSpatial.move(0, 0, dis);
		backwardSpatial.rotate(FastMath.HALF_PI, 0, 0);
		propSpatials.add(backwardSpatial);
		spatial.attachChild(backwardSpatial);

		// Forward
		forwardSpatial = createSpatial("Forward", "Manipulator/Manipulator-Movement_Arrow.mesh.xml");
		forwardSpatial.setLocalScale(scale);
		forwardSpatial.move(0, 0, -dis);
		forwardSpatial.rotate(-FastMath.HALF_PI, 0, 0);
		propSpatials.add(forwardSpatial);
		spatial.attachChild(forwardSpatial);

		// Rotate Yaw
		rotateYaw1 = createSpatial("RotateYaw1", "Manipulator/Manipulator-Rotation_Arrow.mesh.xml");
		rotateYaw1.setLocalScale(scale);
		rotateYaw1.rotate(0, FastMath.QUARTER_PI, 0);
		propSpatials.add(rotateYaw1);
		spatial.attachChild(rotateYaw1);

		// Rotate Yaw
		rotateYaw2 = createSpatial("RotateYaw2", "Manipulator/Manipulator-Rotation_Arrow.mesh.xml");
		rotateYaw2.setLocalScale(scale);
		rotateYaw2.rotate(0, -FastMath.QUARTER_PI, 0);
		propSpatials.add(rotateYaw2);
		spatial.attachChild(rotateYaw2);

		// Rotate Yaw
		rotateYaw3 = createSpatial("RotateYaw3", "Manipulator/Manipulator-Rotation_Arrow.mesh.xml");
		rotateYaw3.setLocalScale(scale);
		rotateYaw3.rotate(0, FastMath.QUARTER_PI * 3f, 0);
		propSpatials.add(rotateYaw3);
		spatial.attachChild(rotateYaw3);

		// Rotate Yaw
		rotateYaw4 = createSpatial("RotateYaw4", "Manipulator/Manipulator-Rotation_Arrow.mesh.xml");
		rotateYaw4.setLocalScale(scale);
		rotateYaw4.rotate(0, -FastMath.QUARTER_PI * 3f, 0);
		propSpatials.add(rotateYaw4);
		spatial.attachChild(rotateYaw4);

		// Rotate Pitch
		rotatePitch = createSpatial("RotatePitch", "Manipulator/Manipulator-Rotation_Arrow.mesh.xml");
		rotatePitch.setLocalScale(scale);
		rotatePitch.rotate(FastMath.HALF_PI, FastMath.HALF_PI, FastMath.QUARTER_PI);
		propSpatials.add(rotatePitch);
		spatial.attachChild(rotatePitch);

		// Rotate Roll
		rotateRoll = createSpatial("RotateRoll", "Manipulator/Manipulator-Rotation_Arrow.mesh.xml");
		rotateRoll.setLocalScale(scale);
		rotateRoll.rotate(FastMath.HALF_PI, 0, FastMath.QUARTER_PI);
		propSpatials.add(rotateRoll);
		spatial.attachChild(rotateRoll);

	}

	private Spatial createSpatial(String name, String path) {
		String meshPath = Icescene.checkAssetPath(path);
		LOG.info(String.format("Loading %s", meshPath));
		ExtendedOgreMeshKey mk = new ExtendedOgreMeshKey(meshPath) {

			@Override
			protected void configureMaterialKey(ExtendedMaterialListKey key) {
				super.configureMaterialKey(key);
				key.setLighting(Lighting.UNLIT);
			}

		};
		Spatial spat = context.getAssetManager().loadModel(mk);
		spat.setName("EditCursor-" + name);
		MaterialUtil.setColor(mk.getMaterialList().values().iterator().next(), DESELECTED_COLOR);
		return spat;
	}

	public List<Spatial> getSpatialsForDirection(EditDirection direction) {
		switch (direction) {
		case X:
			return Arrays.asList(leftSpatial, rightSpatial);
		case Y:
			return Arrays.asList(upSpatial, downSpatial);
		case Z:
			return Arrays.asList(forwardSpatial, backwardSpatial);
		case RY:
			return Arrays.asList(rotateYaw1, rotateYaw2, rotateYaw3, rotateYaw4);
		case RP:
			return Arrays.asList(rotatePitch);
		case RR:
			return Arrays.asList(rotateRoll);
		default:
			return Collections.emptyList();
		}
	}

	@Override
	public AbstractProp clone() {
		throw new UnsupportedOperationException();
	}
}