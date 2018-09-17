package org.icescene.fog;

import java.io.IOException;

import com.jme3.asset.AssetManager;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.post.Filter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;

/**
 * 
 * A filter to render the GLSL implementation of a fog effect
 * 
 * @author t0neg0d
 * 
 * TODO
 * 
 * some improvements here ...
 * 
 * http://in2gpu.com/2014/07/22/create-fog-shader/
 */

public class FogFilter extends Filter {

	public static enum FogMode {
		LINEAR, EXP2_CAM_TO_DISTANCE, EXP2_DISTANCE_TO_INFINITY
	}

	private ColorRGBA fogColor = ColorRGBA.White.clone();
	private float fogDensity = 0.1f;
	private float fogStartDistance = 200f;
	private float fogEndDistance = 500f;
	private boolean excludeSky = false;
	private FogMode fogMode = FogMode.LINEAR;

	/**
	 * Creates a FogFilter
	 */
	public FogFilter() {
		super("FogFilter");
	}

	/**
	 * Create a fog filter
	 * 
	 * @param fogMode
	 *            the mode to use for rendering fog (default is
	 *            EXP2_CAM_TO_DISTANCE)
	 * 
	 * @param fogColor
	 *            the color of the fog (default is white)
	 * 
	 * @param fogDensity
	 *            the density of the fog (default is 1.0)
	 * 
	 * @param fogStartDistance
	 *            Start distance is the absolute distance of the fog in mode
	 *            EXP2_CAM_TO_DISTANCE and the start dstance in modes LINEAR and
	 *            EXP2_DISTANCE_TO_INFINITY (default is 200)
	 * 
	 * @param fogEndDistance
	 *            End distance (100% density) in mode LINEAR
	 */

	public FogFilter(FogMode fogMode, ColorRGBA fogColor, float fogDensity, float fogStartDistance, float fogEndDistance) {
		this();
		this.fogMode = fogMode;
		this.fogColor = fogColor;
		this.fogDensity = fogDensity;
		this.fogStartDistance = fogStartDistance;
		this.fogEndDistance = fogEndDistance;
	}

	@Override
	protected boolean isRequiresDepthTexture() {
		return true;
	}

	@Override
	protected void initFilter(AssetManager manager, RenderManager renderManager, ViewPort vp, int w, int h) {
		material = new Material(manager, "MatDefs/Filters/Fog.j3md");
		material.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		material.getAdditionalRenderState().setDepthTest(true);
		material.getAdditionalRenderState().setDepthWrite(true);
		material.setInt("FogMode", fogMode.ordinal());
		material.setColor("FogColor", fogColor);
		material.setFloat("FogDensity", fogDensity);
		material.setFloat("FogStartDistance", fogStartDistance);
		material.setFloat("FogEndDistance", fogEndDistance);
		material.setBoolean("ExcludeSky", excludeSky);
	}

	@Override
	protected Material getMaterial() {
		return material;
	}

	/**
	 * 
	 * Sets the fog mode (default is {@link FogMode#EXP2_CAM_TO_DISTANCE}E)
	 * 
	 * @param fogMode
	 */

	public void setFogMode(FogMode fogMode) {
		if (material != null) {
			material.setInt("FogMode", fogMode.ordinal());
		}
		this.fogMode = fogMode;
	}

	/**
	 * 
	 * Returns the fog mode (default is {@link FogMode#EXP2_CAM_TO_DISTANCE})
	 * 
	 * @return fogMode
	 */

	public FogMode getFogMode() {
		return this.fogMode;
	}

	/**
	 * Returns the fog color
	 * 
	 * @return fogColor
	 */

	public ColorRGBA getFogColor() {
		return fogColor;
	}

	/**
	 * 
	 * Sets the color of the fog
	 * 
	 * @param fogColor
	 */

	public void setFogColor(ColorRGBA fogColor) {
		if (material != null) {
			material.setColor("FogColor", fogColor);
		}
		this.fogColor = fogColor;
	}

	/**
	 * Returns the fog density
	 * 
	 * @return
	 */

	public float getFogDensity() {
		return fogDensity;
	}

	/**
	 * 
	 * Sets the density of the fog, a high value gives a thick fog
	 * 
	 * @param fogDensity
	 */

	public void setFogDensity(float fogDensity) {
		if (material != null) {
			material.setFloat("FogDensity", fogDensity);
		}
		this.fogDensity = fogDensity;
	}

	/**
	 * Returns the fog start distance
	 * 
	 * @return fogStartDistance
	 */

	public float getFogStartDistance() {
		return fogStartDistance;
	}

	/**
	 * 
	 * Start distance is the absolute distance of the fog in mode
	 * EXP2_CAM_TO_DISTANCE and the start dstance in modes LINEAR and
	 * EXP2_DISTANCE_TO_INFINITY (default is 200)
	 * 
	 * @param fogStartDistance
	 */

	public void setFogStartDistance(float fogStartDistance) {
		if (material != null) {
			material.setFloat("FogStartDistance", fogStartDistance);
		}
		this.fogStartDistance = fogStartDistance;
	}

	/**
	 * 
	 * returns the fog end distance
	 * 
	 * @return fogEndDistance
	 */

	public float getFogEndDistance() {
		return fogEndDistance;

	}

	/**
	 * 
	 * End distance (100% density) in mode LINEAR
	 * 
	 * @param fogEndDistance
	 */

	public void setFogEndDistance(float fogEndDistance) {

		if (material != null) {

			material.setFloat("FogEndDistance", fogEndDistance);

		}

		this.fogEndDistance = fogEndDistance;

	}

	/**
	 * 
	 * Sets the exclude sky flag
	 * 
	 * @param excludeSky
	 */

	public void setExcludeSky(boolean excludeSky) {

		if (material != null) {

			material.setBoolean("ExcludeSky", excludeSky);

		}

		this.excludeSky = excludeSky;

	}

	/**
	 * 
	 * returns the exclude sky flag
	 * 
	 * @return
	 */

	public boolean getExcludeSky() {
		return excludeSky;
	}

	@Override
	public void write(JmeExporter ex) throws IOException {
		super.write(ex);
		OutputCapsule oc = ex.getCapsule(this);
		oc.write(fogColor, "fogColor", ColorRGBA.White.clone());
		oc.write(fogDensity, "fogDensity", 0.7f);
		oc.write(fogStartDistance, "fogStartDistance", 200f);
		oc.write(fogEndDistance, "fogEndDistance", 500f);
		oc.write(excludeSky, "excludeSky", false);
	}

	@Override
	public void read(JmeImporter im) throws IOException {
		super.read(im);
		InputCapsule ic = im.getCapsule(this);
		fogColor = (ColorRGBA) ic.readSavable("fogColor", ColorRGBA.White.clone());
		fogDensity = ic.readFloat("fogDensity", 0.7f);
		fogStartDistance = ic.readFloat("fogStartDistance", 200);
		fogEndDistance = ic.readFloat("fogEndDistance", 500);
		excludeSky = ic.readBoolean("excludeSky", false);
	}

}