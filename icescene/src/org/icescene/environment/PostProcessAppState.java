package org.icescene.environment;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.Preferences;

import org.icescene.DisableableFilterPostProcessor;
import org.icescene.IcemoonAppState;
import org.icescene.IcesceneApp;
import org.icescene.SceneConfig;
import org.icescene.SceneConstants;

import com.google.common.base.Objects;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.Filter;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.FogFilter;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.post.ssao.SSAOFilter;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.water.WaterFilter;

public class PostProcessAppState extends IcemoonAppState<IcemoonAppState> implements PropertyChangeListener {

	public enum FogFilterMode {
		JME3, OGL
	}

	private final static Logger LOG = Logger.getLogger(PostProcessAppState.class.getName());
	private DisableableFilterPostProcessor postProcessor;
	private BloomFilter bloomFilter;
	private Filter fogFilter;
	private LightScatteringFilter sunLightFilter;
	private SSAOFilter ssaoFilter;
	private DirectionalLightShadowFilter shadowFilter;
	private EnvironmentLight light;
	private FogFilterMode fogFilterMode = FogFilterMode.OGL;

	public PostProcessAppState(Preferences prefs, EnvironmentLight light) {
		super(prefs);
		this.light = light;
		addPrefKeyPattern(SceneConfig.SCENE_BLOOM);
		addPrefKeyPattern(SceneConfig.TERRAIN_PRETTY_WATER);
		addPrefKeyPattern(SceneConfig.SCENE_LIGHT_BEAMS);
		addPrefKeyPattern(SceneConfig.SCENE_SSAO);
		addPrefKeyPattern(SceneConfig.SCENE_SHADOWS);
	}

	public EnvironmentLight getLight() {
		return light;
	}

	@Override
	protected IcemoonAppState onInitialize(AppStateManager stateManager, IcesceneApp app) {

		light.addPropertyChangeListener(this);
		postProcessor = new DisableableFilterPostProcessor(assetManager);

		// Fog
		fogFilter = new org.icescene.fog.FogFilter();
		postProcessor.addFilter(fogFilter);
		fogFilter.setEnabled(false);

		// Sunlight
		sunLightFilter = new LightScatteringFilter(
				new Vector3f(SceneConstants.DEFAULT_SUN_DIRECTION).mult(SceneConstants.DIRECTIONAL_LIGHT_SOURCE_DISTANCE)) {
			@Override
			public void setLightPosition(Vector3f lightPosition) {
				super.setLightPosition(lightPosition);
				if (material != null) {
					material.setVector3("LightPosition", lightPosition);
				}
			}
		};

		// Shadows
		shadowFilter = new DirectionalLightShadowFilter(assetManager, 1024, 2);
		shadowFilter.setLight(light.getSun());
		shadowFilter.setEnabled(true);

		// SSAO
		ssaoFilter = new SSAOFilter(12.94f, 43.94f, .33f, .60f);

		// Bloom
		bloomFilter = new BloomFilter(BloomFilter.GlowMode.SceneAndObjects);

		// Set initial configuration
		setLightBeams(prefs.getBoolean(SceneConfig.SCENE_LIGHT_BEAMS, SceneConfig.SCENE_LIGHT_BEAMS_DEFAULT));
		setShadows(prefs.getBoolean(SceneConfig.SCENE_LIGHT_BEAMS, SceneConfig.SCENE_LIGHT_BEAMS_DEFAULT));
		setSSAO(prefs.getBoolean(SceneConfig.SCENE_SSAO, SceneConfig.SCENE_SSAO_DEFAULT));
		setBloom(prefs.getBoolean(SceneConfig.SCENE_BLOOM, SceneConfig.SCENE_BLOOM_DEFAULT));

		return null;
	}

	protected boolean shouldEnable() {
		int en = 0;
		for (Filter f : postProcessor.getFilterList()) {
			if (f.isEnabled()) {
				en++;
			}
		}
		return en > 0;
	}

	public void checkIfShouldEnable() {
		boolean shouldEnable = shouldEnable();
		if (shouldEnable && !app.getViewPort().getProcessors().contains(postProcessor)) {
			LOG.info("Enabling post processor");
			app.getViewPort().addProcessor(postProcessor);
		} else if (!shouldEnable && app.getViewPort().getProcessors().contains(postProcessor)) {
			LOG.info("Disabling post processor");
			app.getViewPort().removeProcessor(postProcessor);
		}
	}

	public void setSunColor(ColorRGBA color) {
	}

	public DisableableFilterPostProcessor getPostProcessor() {
		return postProcessor;
	}

	public Filter getFogFilter() {
		return fogFilter;
	}

	public FogFilterMode getFogFilterMode() {
		return fogFilterMode;
	}

	public void setFogFilterMode(FogFilterMode fogMode) {
		if (!Objects.equal(fogMode, this.fogFilterMode)) {
			boolean enabled = fogFilter.isEnabled();

			postProcessor.removeFilter(fogFilter);

			switch (fogMode) {
			case OGL:
				fogFilter = new org.icescene.fog.FogFilter();
				break;
			default:
				fogFilter = new FogFilter();
				break;
			}
			postProcessor.addFilter(fogFilter);

			fogFilter.setEnabled(enabled);
		}
	}

	@Override
	protected void onCleanup() {
		light.removePropertyChangeListener(this);
		super.onCleanup();
		setLightBeams(false);
		setBloom(false);
		app.getViewPort().removeProcessor(postProcessor);
	}

	@Override
	protected void handlePrefUpdateSceneThread(PreferenceChangeEvent evt) {
		if (evt.getKey().equals(SceneConfig.SCENE_BLOOM)) {
			setBloom(Boolean.parseBoolean(evt.getNewValue()));
		} else if (evt.getKey().equals(SceneConfig.SCENE_LIGHT_BEAMS)) {
			setLightBeams(Boolean.parseBoolean(evt.getNewValue()));
		} else if (evt.getKey().equals(SceneConfig.SCENE_SHADOWS)) {
			setShadows(Boolean.parseBoolean(evt.getNewValue()));
		} else if (evt.getKey().equals(SceneConfig.SCENE_SSAO)) {
			setSSAO(Boolean.parseBoolean(evt.getNewValue()));
		} else if (evt.getKey().equals(SceneConfig.TERRAIN_PRETTY_WATER)) {
			checkIfWaterShouldBeEnabled(evt);
		}
	}

	private void checkIfWaterShouldBeEnabled(PreferenceChangeEvent evt) {
		boolean newWater = Boolean.parseBoolean(evt.getNewValue());
		if (newWater) {
			for (Filter f : postProcessor.getFilterList()) {
				if (f instanceof WaterFilter) {
					f.setEnabled(true);
				}
			}
		} else {
			disableBeautifulWater();
		}
		checkIfShouldEnable();
	}

	@Override
	public void update(float tpf) {
		super.update(tpf);
		if (sunLightFilter.isEnabled()) {
			sunLightFilter.setLightPosition(light.getLightSourcePosition());
		}
	}

	protected void setBloom(boolean bloom) {
		if (bloom && postProcessor.getFilter(BloomFilter.class) == null) {
			postProcessor.addFilter(bloomFilter);
		} else if (!bloom && postProcessor.getFilter(BloomFilter.class) != null) {
			postProcessor.removeFilter(bloomFilter);
		}
		checkIfShouldEnable();
	}

	protected void setLightBeams(boolean lightBeams) {
		sunLightFilter.setEnabled(lightBeams);
		if (lightBeams && postProcessor.getFilter(LightScatteringFilter.class) == null && light.isDirectionalAllowed()) {
			postProcessor.addFilter(sunLightFilter);
			sunLightFilter.setLightPosition(light.getLightSourcePosition());
		} else if ((!lightBeams || !light.isDirectionalAllowed()) && postProcessor.getFilter(LightScatteringFilter.class) != null) {
			postProcessor.removeFilter(sunLightFilter);
		}
		checkIfShouldEnable();
	}

	protected void setSSAO(boolean ssao) {
		if (ssao && postProcessor.getFilter(SSAOFilter.class) == null && light.isAmbientEnabled()) {
			postProcessor.addFilter(ssaoFilter);
		} else if ((!ssao || !light.isAmbientEnabled()) && postProcessor.getFilter(SSAOFilter.class) != null) {
			postProcessor.removeFilter(ssaoFilter);
		}
		checkIfShouldEnable();
	}

	protected void setShadows(boolean shadows) {
		if (shadows && postProcessor.getFilter(DirectionalLightShadowFilter.class) == null && light.isDirectionalAllowed()) {
			postProcessor.addFilter(shadowFilter);
		} else if ((!shadows || !light.isDirectionalAllowed())
				&& postProcessor.getFilter(DirectionalLightShadowFilter.class) != null) {
			postProcessor.removeFilter(shadowFilter);
		}
		checkIfShouldEnable();
	}

	private void disableBeautifulWater() {
		for (Filter f : postProcessor.getFilterList()) {
			if (f instanceof WaterFilter) {
				f.setEnabled(false);
			}
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(EnvironmentLight.PROP_SUN_POSITION)) {
			sunLightFilter.setLightPosition(light.getLightSourcePosition());
		} else if (evt.getPropertyName().equals(EnvironmentLight.PROP_SUN_ENABLED)) {
			setShadows(prefs.getBoolean(SceneConfig.SCENE_SHADOWS, SceneConfig.SCENE_SHADOWS_DEFAULT));
			setLightBeams(prefs.getBoolean(SceneConfig.SCENE_LIGHT_BEAMS, SceneConfig.SCENE_LIGHT_BEAMS_DEFAULT));
		} else if (evt.getPropertyName().equals(EnvironmentLight.PROP_SUN_COLOR)) {
			setShadows(prefs.getBoolean(SceneConfig.SCENE_SHADOWS, SceneConfig.SCENE_SHADOWS_DEFAULT));
			setLightBeams(prefs.getBoolean(SceneConfig.SCENE_LIGHT_BEAMS, SceneConfig.SCENE_LIGHT_BEAMS_DEFAULT));
		} else if (evt.getPropertyName().equals(EnvironmentLight.PROP_AMBIENT_ENABLED)) {
			setSSAO(prefs.getBoolean(SceneConfig.SCENE_SSAO, SceneConfig.SCENE_SSAO_DEFAULT));
		} else if (evt.getPropertyName().equals(SceneConfig.TERRAIN_PRETTY_WATER)) {
			checkIfShouldEnable();
		}
	}

	public void addFilter(Filter water) {
		postProcessor.addFilter(water);
		checkIfShouldEnable();
	}
}
