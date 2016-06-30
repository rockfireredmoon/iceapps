package org.icescene.configuration.creatures;

import java.util.ArrayList;
import java.util.List;

import org.icelib.beans.MappedMap;
import org.icescene.animation.AnimationOption;
import org.icescene.configuration.attachments.AttachmentPointSet;
import org.icescene.scene.AlignToFloorMode;

import com.jme3.math.Vector3f;

public class ModelDefinition extends AbstractCreatureDefinition {

	private static final long serialVersionUID = 1L;

	private String animationHandler;
	private String effectsPackage;
	private AttachmentPointSet attachmentPointSet;
	private String model;
	private float size;
	private float blend = 0.2f;
	private List<String> baseMeleeAnimations = new ArrayList<>();
	private MappedMap<String, AnimationOption> animations = new MappedMap<>(String.class, AnimationOption.class);
	private MappedMap<String, MovementOption> movement = new MappedMap<>(String.class, MovementOption.class);
	private AlignToFloorMode alignToFloorMode;
	private boolean noFade;
	private String mesh;

	private String clickMesh;
	private Vector3f clickMeshOffset;
	private Vector3f clickMeshScale;

	public ModelDefinition(CreatureKey key) {
		super(key);
	}

	public String getMesh() {
		return mesh;
	}

	public void setMesh(String mesh) {
		this.mesh = mesh;
	}

	public String getClickMesh() {
		return clickMesh;
	}

	public void setClickMesh(String clickMesh) {
		this.clickMesh = clickMesh;
	}

	public Vector3f getClickMeshOffset() {
		return clickMeshOffset;
	}

	public void setClickMeshOffset(Vector3f clickMeshOffset) {
		this.clickMeshOffset = clickMeshOffset;
	}

	public Vector3f getClickMeshScale() {
		return clickMeshScale;
	}

	public void setClickMeshScale(Vector3f clickMeshScale) {
		this.clickMeshScale = clickMeshScale;
	}

	public AlignToFloorMode getAlignToFloorMode() {
		return alignToFloorMode;
	}

	public boolean isNoFade() {
		return noFade;
	}

	public void setNoFade(boolean noFade) {
		this.noFade = noFade;
	}

	public void setAlignToFloorMode(AlignToFloorMode alignToFloorMode) {
		this.alignToFloorMode = alignToFloorMode;
	}

	public MappedMap<String, MovementOption> getMovement() {
		return movement;
	}

	public MappedMap<String, AnimationOption> getAnimations() {
		return animations;
	}

	public float getBlend() {
		return blend;
	}

	public void setBlend(float blend) {
		this.blend = blend;
	}

	public String getAnimationHandler() {
		return animationHandler;
	}

	public void setAnimationHandler(String animationHandler) {
		this.animationHandler = animationHandler;
	}

	public String getEffectsPackage() {
		return effectsPackage;
	}

	public void setEffectsPackage(String effectsPackages) {
		this.effectsPackage = effectsPackages;
	}

	public AttachmentPointSet getAttachmentPointSet() {
		return attachmentPointSet;
	}

	public void setAttachmentPointSet(AttachmentPointSet attachmentPointSet) {
		this.attachmentPointSet = attachmentPointSet;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = size;
	}

	public List<String> getBaseMeleeAnimations() {
		return baseMeleeAnimations;
	}

}