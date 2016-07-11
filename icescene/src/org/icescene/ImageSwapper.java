package org.icescene;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.jme3.shader.VarType;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;

public class ImageSwapper extends Node {

	private AssetManager assetManager;
	private Vector2f size;
	private Picture picture;

	public ImageSwapper(Vector2f size, AssetManager assetManager) {
		this.assetManager = assetManager;
		this.size = size;
	}

	public void setSize(Vector2f size) {
		this.size = size;
	}

	public void setPicture(Texture2D texture) {
		setPicture(texture, true);
	}

	public void setPicture(Texture2D texture, boolean fadeIn) {
		Picture picture = new Picture("background");
		Material mat = new Material(assetManager, "icetone/shaders/Unshaded.j3md");
		mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		mat.setColor("Color", ColorRGBA.White);
		mat.setTexture("ColorMap", texture);
		picture.setMaterial(mat);
		picture.setWidth(size.x);
		picture.setHeight(size.y);
		picture.setPosition(0, 0);
		setPicture(picture, fadeIn);
	}

	public void setPicture(String path) {
		setPicture(path, true);
	}

	public void setPicture(String path, boolean fadeIn) {
		setPicture((Texture2D) assetManager.loadTexture(path), fadeIn);
	}

	private void setPicture(Picture picture, boolean fadeIn) {

		if (this.picture != null) {
			// If still fading in, stop it
			if (this.picture.getControl(FadeInControl.class) != null) {
				this.picture.removeControl(FadeInControl.class);
			}

			// If still fading out, complete it
			if (this.picture.getControl(FadeOutControl.class) != null) {
				this.picture.getControl(FadeOutControl.class).complete();
			}

			// Now fade out
			if (fadeIn) {
				this.picture.addControl(new FadeOutControl());
			} else {
				this.picture.removeFromParent();
			}
		}

		this.picture = picture;
		if (fadeIn) {
			setPictureAlpha(picture, 0);
			picture.addControl(new FadeInControl());
		} else {
			setPictureAlpha(picture, 1);
		}
		attachChild(picture);
		// updateGeometricState();
	}

	public void reloadBackgroundPicture() {
		if (picture != null) {
			Picture picture = new Picture("background");
			Material mat = new Material(assetManager, "icetone/shaders/Unshaded.j3md");
			mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
			mat.setColor("Color", ColorRGBA.White);
			mat.setTexture("ColorMap",
					(Texture2D) this.picture.getMaterial().getTextureParam("ColorMap").getTextureValue());
			picture.setMaterial(mat);
			picture.setWidth(size.x);
			picture.setHeight(size.y);
			picture.setPosition(0, 0);
			setPicture(picture, false);
			updateGeometricState();

		}
	}

	void setPictureAlpha(Picture picture, float alpha) {
		picture.getMaterial().setParam("GlobalAlpha", VarType.Float, alpha);
	}

	float getPictureAlpha(Picture picture) {
		return ((Number) picture.getMaterial().getParam("GlobalAlpha").getValue()).floatValue();
	}

	class FadeInControl extends AbstractControl {

		@Override
		protected void controlUpdate(float tpf) {
			Picture pic = (Picture) getSpatial();
			float alpha = getPictureAlpha(pic);
			if (alpha < 1) {
				alpha += tpf;
				setPictureAlpha(pic, Math.min(1, alpha));
			} else
				complete();
		}

		public void complete() {
			Picture pic = (Picture) getSpatial();
			setPictureAlpha(pic, 1);
			pic.removeControl(this);
		}

		@Override
		protected void controlRender(RenderManager rm, ViewPort vp) {
		}

	}

	class FadeOutControl extends AbstractControl {

		@Override
		protected void controlUpdate(float tpf) {
			Picture pic = (Picture) getSpatial();
			float alpha = getPictureAlpha(pic);
			if (alpha > 0) {
				alpha -= tpf;
				setPictureAlpha(pic, Math.max(0, alpha));
			}
			if (alpha <= 0)
				complete();
		}

		public void complete() {
			Picture pic = (Picture) getSpatial();
			pic.removeFromParent();
			updateGeometricState();
		}

		@Override
		protected void controlRender(RenderManager rm, ViewPort vp) {
		}

	}

}
