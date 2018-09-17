package org.icescene.options;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.Preferences;

import org.icelib.Icelib;
import org.icescene.DisplaySettingsWindow;
import org.icescene.IcemoonAppState;
import org.icescene.IcesceneApp;
import org.icescene.SceneConfig;
import org.icescene.io.ModifierKeysAppState;
import org.icescene.io.UserKeyMapping;
import org.iceui.controls.ElementStyle;
import org.iceui.controls.TabPanelContent;

import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapFont.VAlign;
import com.jme3.input.KeyInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Vector2f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;

import icetone.controls.buttons.CheckBox;
import icetone.controls.buttons.PushButton;
import icetone.controls.containers.TabControl;
import icetone.controls.lists.ComboBox;
import icetone.controls.lists.FloatRangeSliderModel;
import icetone.controls.lists.Slider;
import icetone.controls.table.TableRow;
import icetone.controls.text.Label;
import icetone.controls.text.TextField;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Orientation;
import icetone.core.Size;
import icetone.core.StyledContainer;
import icetone.core.layout.ScreenLayoutConstraints;
import icetone.core.layout.mig.MigLayout;
import icetone.extras.windows.InputBox;
import icetone.extras.windows.PersistentWindow;
import icetone.extras.windows.SaveType;

public class OptionsAppState extends IcemoonAppState<IcemoonAppState<?>> {

	private final class KeyInputBox extends InputBox {
		private final RawInputListener raw;
		private int mods;
		private long lastKeyEv;
		private UserKeyMapping mapping;
		private int keyCode = -1;

		private KeyInputBox(UserKeyMapping mapping, BaseScreen screen, Vector2f position, boolean closeable,
				RawInputListener raw) {
			super(screen, position, closeable);
			this.mapping = mapping;
			setDestroyOnHide(true);
			ElementStyle.warningColor(getDragBar());
			setWindowTitle("Edit Keymap");
			setButtonOkText("Save");
			setResizable(false);
			setMovable(false);

			getContentArea().setLayoutManager(new MigLayout(screen, "fill,wrap 1", "", "[][][]"));
			getContentArea().removeElement(input);
			getContentArea().removeElement(buttons);
			getContentArea().addElement(
					new Label(String.format("%s is triggered by ", mapping.getSource().getMapping()), screen),
					"ax 50%");

			input = new TextField(screen);
			input.setEnabled(false);
			input.setText(mapping.getDescription());

			getContentArea().addElement(input, "growx");
			getContentArea().addElement(buttons, "ax 50%");

			this.raw = raw;
		}

		@Override
		protected void onCloseWindow() {
			super.onCloseWindow();
			inputManager.removeRawInputListener(raw);
		}

		@Override
		public void onButtonCancelPressed(MouseButtonEvent evt, boolean toggled) {
			hide();
			inputManager.removeRawInputListener(raw);
		}

		@Override
		public void onButtonOkPressed(MouseButtonEvent evt, String text, boolean toggled) {
			mapping.setTrigger(new KeyTrigger(keyCode));
			mapping.setModifiers(mods);
			mapping.update();
			hide();
			inputManager.removeRawInputListener(raw);
			keyTable.reloadKeys();
		}

		public void keyEvent(KeyInputEvent evt) {
			try {
				if (!evt.isRepeating()) {
					LOG.info("Processing raw key event: " + evt);
					int newMods = 0;
					int newKeyCode = evt.getKeyCode();
					switch (newKeyCode) {
					case KeyInput.KEY_LSHIFT:
						newMods += ModifierKeysAppState.L_SHIFT_MASK;
						break;
					case KeyInput.KEY_RSHIFT:
						newMods += ModifierKeysAppState.R_SHIFT_MASK;
						break;
					case KeyInput.KEY_LMENU:
						newMods += ModifierKeysAppState.L_ALT_MASK;
						break;
					case KeyInput.KEY_RMENU:
						newMods += ModifierKeysAppState.R_ALT_MASK;
						break;
					case KeyInput.KEY_LCONTROL:
						newMods += ModifierKeysAppState.L_CTRL_MASK;
						break;
					case KeyInput.KEY_RCONTROL:
						newMods += ModifierKeysAppState.R_CTRL_MASK;
						break;
					case KeyInput.KEY_LMETA:
						newMods += ModifierKeysAppState.L_META_MASK;
						break;
					case KeyInput.KEY_RMETA:
						newMods += ModifierKeysAppState.R_META_MASK;
						break;
					}
					if (evt.isPressed()) {
						if (System.currentTimeMillis() > lastKeyEv + 2000) {
							// No key press for 5 seconds, reset modifiers
							mods = 0;
						}
						if (newMods == 0) {
							// This was not a modifier press, so must be a key
							keyCode = newKeyCode;
						} else {
							if ((mods & newMods) == 0) {
								// Mask this modifier key onto the current
								// modifiers
								mods = mods | newMods;
							}
						}
						String description = UserKeyMapping.getKeyDescription(mods, keyCode);

						input.setText(description);
					} else if (evt.isReleased()) {
						// if(keyCode == -1) {
						// keyCode = mapping.getTrigger().getKeyCode();
						// }
					}
				}
				lastKeyEv = System.currentTimeMillis();
				evt.setConsumed();
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "Error processing key event.", e);
			}

		}
	}

	private final static Logger LOG = Logger.getLogger(OptionsAppState.class.getName());
	private PersistentWindow optionsWindow;
	private ComboBox<?> triPlanar;
	private CheckBox lodControl;
	private CheckBox prettyWater;
	private CheckBox smoothScaling;
	private Slider<Float> clutterDensity;
	private ComboBox<ShadowMode> clutterShadowMode;
	private CheckBox terrainLit;
	private CheckBox lightBeams;
	private CheckBox shadows;
	private boolean adjusting;
	private CheckBox highDetail;
	private Slider<Float> distance;
	private CheckBox wireframe;
	private CheckBox mute;
	private Slider<Float> masterVolume;
	private Slider<Float> ambientVolume;
	private Slider<Float> uiVolume;
	private Slider<Float> musicVolume;
	private KeyInputBox keyEditDialog;

	protected TabControl optionTabs;
	protected CheckBox bloom;
	protected CheckBox ambientOcclusion;
	protected DisplaySettingsWindow displaySettingsWindow;
	private KeyTable keyTable;
	private PushButton resetKeymap;
	private PushButton editKeyMap;
	private Slider<Float> light;

	public OptionsAppState(Preferences prefs) {
		super(prefs);
		addPrefKeyPattern(SceneConfig.TERRAIN_WIREFRAME + ".*");
	}

	@Override
	protected final IcemoonAppState<?> onInitialize(AppStateManager stateManager, IcesceneApp app) {
		screen = app.getScreen();
		createOptionsWindow();
		screen.showElement(optionsWindow);
		return null;
	}

	@Override
	protected final void onCleanup() {
		if (displaySettingsWindow != null)
			displaySettingsWindow.destroy();
		optionsWindow.hide();
		onOptionsCleanup();
	}

	protected void onOptionsCleanup() {
	}

	public AssetManager getAssetManager() {
		return assetManager;
	}

	@Override
	public void update(float tpf) {
	}

	@Override
	protected void handlePrefUpdateSceneThread(PreferenceChangeEvent evt) {
		if (evt.getKey().equals(SceneConfig.TERRAIN_WIREFRAME)) {
			wireframe.runAdjusting(() -> wireframe.setChecked(Boolean.parseBoolean(evt.getNewValue())));
		}
	}

	protected void addAdditionalTabs() {
		// For subclasses to add more tabs
	}

	protected void setAdditionalDefaults() {
		// For subclasses to add more tabs
	}

	protected void showDisplaySettingsWindow() {
		if (displaySettingsWindow == null) {
			displaySettingsWindow = new DisplaySettingsWindow(screen, Vector2f.ZERO,
					((IcesceneApp) app).getAppSettingsName());
		}
		displaySettingsWindow.show();
		displaySettingsWindow.centerToParent();
	}

	protected void keysTab() {

		TabPanelContent el = new TabPanelContent(screen, "keys-tab-panel-content");
		el.setResizable(false);
		el.setMovable(false);
		el.setLayoutManager(new MigLayout(screen, "wrap 1", "[grow, fill]", "[grow, fill][shrink 0]"));

		keyTable = new KeyTable(screen, app.getKeyMapManager());
		keyTable.onChanged(evt -> setAvailable());
		keyTable.reloadKeys();

		el.addElement(keyTable, "span 2");

		// Options
		StyledContainer c2 = new StyledContainer(screen);
		c2.setLayoutManager(new MigLayout(screen, "wrap 2", "push[][]push", "[]"));
		resetKeymap = new PushButton(screen) {
			{
				setStyleClass("fancy");
			}
		};
		resetKeymap.onMouseReleased(evt -> {
			TableRow sel = keyTable.getSelectedRow();
			if (sel.getValue() instanceof UserKeyMapping) {
				getApp().getKeyMapManager().resetMapping(((UserKeyMapping) sel.getValue()).getSource().getMapping());
				keyTable.reloadKeys();
			}
		});
		resetKeymap.setText("Reset");
		c2.addElement(resetKeymap);
		editKeyMap = new PushButton(screen) {
			{
				setStyleClass("fancy");
			}
		};
		editKeyMap.onMouseReleased(evt -> {
			TableRow sel = keyTable.getSelectedRow();
			if (sel.getValue() instanceof UserKeyMapping) {
				final RawInputListener raw = new RawInputListener() {

					@Override
					public void beginInput() {
					}

					@Override
					public void endInput() {
					}

					@Override
					public void onJoyAxisEvent(JoyAxisEvent arg0) {
					}

					@Override
					public void onJoyButtonEvent(JoyButtonEvent arg0) {
					}

					@Override
					public void onKeyEvent(KeyInputEvent arg0) {
						keyEditDialog.keyEvent(arg0);
					}

					@Override
					public void onMouseButtonEvent(MouseButtonEvent arg0) {
					}

					@Override
					public void onMouseMotionEvent(MouseMotionEvent arg0) {
					}

					@Override
					public void onTouchEvent(TouchEvent arg0) {
					}
				};
				inputManager.addRawInputListener(raw);
				if (sel != null) {
					UserKeyMapping mapping = (UserKeyMapping) sel.getValue();
					keyEditDialog = new KeyInputBox(mapping, screen, new Vector2f(15, 15), true, raw);
					keyEditDialog.setModal(true);
					screen.showElement(keyEditDialog, ScreenLayoutConstraints.center);
				}
			}
		});
		editKeyMap.setText("Change");
		c2.addElement(editKeyMap);
		el.addElement(c2);

		optionTabs.addTab("Keys");
		optionTabs.addTabChild(2, el);

		setAvailable();
	}

	private void setAvailable() {
		boolean mapping = keyTable.getSelectedRows().size() == 1
				&& keyTable.getSelectedRow().getValue() instanceof UserKeyMapping;
		resetKeymap.setEnabled(mapping);
		editKeyMap.setEnabled(mapping);
	}

	protected void videoTab() {
		TabPanelContent el = new TabPanelContent(screen, "video-tab-panel-content");
		el.setResizable(false);
		el.setMovable(false);
		el.setLayoutManager(new MigLayout(screen, "fill", "[150:150:150][]", "[][][][][][][][][]push"));

		// Terrain
		el.addElement(ElementStyle.medium(createLabel("Terrain"), true, false), "span 2, width 100%, wrap, shrink");

		// Tri-planar
		el.addElement(createLabel("Tri-Planar"), "gapleft 16");
		el.addElement(
				triPlanar = create3WaySelect(SceneConfig.TERRAIN_TRI_PLANAR, SceneConfig.TERRAIN_TRI_PLANAR_DEFAULT),
				"wrap");

		// Checkbox container 1
		StyledContainer c1 = new StyledContainer(screen);
		c1.setLayoutManager(new MigLayout(screen, "wrap 2", "[][]", "[][][][]"));

		// Wireframe
		c1.addElement(highDetail = createCheckbox(SceneConfig.TERRAIN_HIGH_DETAIL, "High Detail",
				SceneConfig.TERRAIN_HIGH_DETAIL_DEFAULT));
		c1.addElement(lodControl = createCheckbox(SceneConfig.TERRAIN_LOD_CONTROL, "LOD Control",
				SceneConfig.TERRAIN_LOD_CONTROL_DEFAULT));
		c1.addElement(
				terrainLit = createCheckbox(SceneConfig.TERRAIN_LIT, "Lit Terrain", SceneConfig.TERRAIN_LIT_DEFAULT));
		c1.addElement(prettyWater = createCheckbox(SceneConfig.TERRAIN_PRETTY_WATER, "Pretty Water",
				SceneConfig.TERRAIN_PRETTY_WATER_DEFAULT));
		c1.addElement(smoothScaling = createCheckbox(SceneConfig.TERRAIN_SMOOTH_SCALING, "Smooth Scaling",
				SceneConfig.TERRAIN_SMOOTH_SCALING_DEFAULT));
		c1.addElement(wireframe = createCheckbox(SceneConfig.TERRAIN_WIREFRAME, "Wireframe",
				SceneConfig.TERRAIN_WIREFRAME_DEFAULT));

		el.addElement(c1, "gapleft 32, span 2, wrap");

		// Scene
		el.addElement(ElementStyle.medium(createLabel("Scene"), true, false), "span 2, width 100%, wrap, shrink");

		el.addElement(createLabel("Clutter Shadows"), "gapleft 16");
		clutterShadowMode = new ComboBox<ShadowMode>(screen);
		clutterShadowMode.onChange(evt -> {
			if (!evt.getSource().isAdjusting())
				prefs.put(SceneConfig.SCENE_CLUTTER_SHADOW_MODE, evt.getNewValue().toString());
		});
		for (ShadowMode sm : ShadowMode.values()) {
			clutterShadowMode.addListItem(Icelib.toEnglish(sm.name()), sm);
		}
		el.addElement(clutterShadowMode, "growx, wrap");

		el.addElement(createLabel("Clutter Density"), "gapleft 16");
		clutterDensity = new Slider<Float>(screen, Orientation.HORIZONTAL);
		clutterDensity.onChanged(evt -> prefs.putFloat(SceneConfig.SCENE_CLUTTER_DENSITY, evt.getNewValue()));
		clutterDensity.setSliderModel(new FloatRangeSliderModel(0, 1,
				(float) prefs.getFloat(SceneConfig.SCENE_CLUTTER_DENSITY, SceneConfig.SCENE_CLUTTER_DENSITY_DEFAULT),
				0.1f));
		clutterDensity.setLockToStep(true);
		el.addElement(clutterDensity, "growx, wrap");

		// Distance
		el.addElement(createLabel("Distance"), "gapleft 16");
		distance = new Slider<Float>(screen, Orientation.HORIZONTAL);
		distance.onChanged(evt -> prefs.putFloat(SceneConfig.SCENE_DISTANCE, evt.getNewValue()));
		distance.setSliderModel(new FloatRangeSliderModel(0, 1024,
				(float) prefs.getFloat(SceneConfig.SCENE_DISTANCE, SceneConfig.SCENE_DISTANCE_DEFAULT), 10f));
		el.addElement(distance, "growx, wrap");

		// Light
		el.addElement(createLabel("Light"), "gapleft 16");
		light = new Slider<Float>(screen, Orientation.HORIZONTAL);
		light.onChanged(evt -> prefs.putFloat(SceneConfig.SCENE_LIGHT_MULTIPLIER, evt.getNewValue()));
		light.setSliderModel(new FloatRangeSliderModel(0, 5,
				(float) prefs.getFloat(SceneConfig.SCENE_LIGHT_MULTIPLIER, SceneConfig.SCENE_LIGHT_MULTIPLIER_DEFAULT),
				0.1f));
		el.addElement(light, "growx, wrap");

		// Checkbox container 1
		StyledContainer c2 = new StyledContainer(screen);
		c2.setLayoutManager(new MigLayout(screen, "wrap 2", "[][]", "[][]"));

		c2.addElement(lightBeams = createCheckbox(SceneConfig.SCENE_LIGHT_BEAMS, "Light beams",
				SceneConfig.SCENE_LIGHT_BEAMS_DEFAULT));
		c2.addElement(bloom = createCheckbox(SceneConfig.SCENE_BLOOM, "Bloom", SceneConfig.SCENE_BLOOM_DEFAULT));
		c2.addElement(
				shadows = createCheckbox(SceneConfig.SCENE_SHADOWS, "Shadows", SceneConfig.SCENE_SHADOWS_DEFAULT));
		c2.addElement(ambientOcclusion = createCheckbox(SceneConfig.SCENE_SSAO, "Ambient Occlusion",
				SceneConfig.SCENE_SSAO_DEFAULT));

		el.addElement(c2, "gapleft 32, span 2, wrap");

		// Dislay
		PushButton display = new PushButton(screen) {
			{
				setStyleClass("fancy");
			}
		};
		display.onMouseReleased(evt -> showDisplaySettingsWindow());
		display.setText("Display");
		el.addElement(display, "ax 50%, span 2, wrap");

		optionTabs.addTab("Video");
		optionTabs.addTabChild(0, el);
	}

	private void audioTab() {

		// Volumes
		TabPanelContent p = new TabPanelContent(screen, "audio-tab-panel-content");
		p.setTextPadding(0);
		p.setLayoutManager(new MigLayout(screen, "gap 0, ins 0, fill",
				"[align center,32:25%:][align center,32:25%:][align center,32:25%:][align center,32:25%:]",
				"[]4[grow]4[][]"));

		p.addElement(createLabel("Full", BitmapFont.Align.Center));
		p.addElement(createLabel("Full", BitmapFont.Align.Center));
		p.addElement(createLabel("Full", BitmapFont.Align.Center));
		p.addElement(createLabel("Full", BitmapFont.Align.Center), "wrap");

		p.addElement(masterVolume = createVolumeDial(SceneConfig.AUDIO_MASTER_VOLUME,
				SceneConfig.AUDIO_MASTER_VOLUME_DEFAULT), "growy");
		p.addElement(ambientVolume = createVolumeDial(SceneConfig.AUDIO_AMBIENT_VOLUME,
				SceneConfig.AUDIO_AMBIENT_VOLUME_DEFAULT), "growy");
		p.addElement(uiVolume = createVolumeDial(SceneConfig.AUDIO_UI_VOLUME, SceneConfig.AUDIO_UI_VOLUME_DEFAULT),
				"growy");
		p.addElement(
				musicVolume = createVolumeDial(SceneConfig.AUDIO_MUSIC_VOLUME, SceneConfig.AUDIO_MUSIC_VOLUME_DEFAULT),
				"wrap, growy");

		p.addElement(createLabel("Off", BitmapFont.Align.Center));
		p.addElement(createLabel("Off", BitmapFont.Align.Center));
		p.addElement(createLabel("Off", BitmapFont.Align.Center));
		p.addElement(createLabel("Off", BitmapFont.Align.Center), "wrap");
		p.addElement(createLabel("Master", BitmapFont.Align.Center));
		p.addElement(createLabel("Ambient", BitmapFont.Align.Center));
		p.addElement(createLabel("UI", BitmapFont.Align.Center));
		p.addElement(createLabel("Music", BitmapFont.Align.Center), "wrap");

		p.addElement(mute = createCheckbox(SceneConfig.AUDIO_MUTE, "Mute", SceneConfig.AUDIO_MUTE_DEFAULT),
				"gap 10px 10px, span 4, ax 50%");
		optionTabs.addTab("Audio", p);
	}

	protected Slider<Float> createVolumeDial(final String prefKey, float defaultValue) {
		Slider<Float> dial = new Slider<Float>(screen, Orientation.VERTICAL);
		dial.onChanged(evt -> prefs.putFloat(prefKey, evt.getNewValue()));
		dial.setSliderModel(new FloatRangeSliderModel(0, 1, prefs.getFloat(prefKey, defaultValue), 0.05f));
		dial.setLockToStep(true);
		dial.setReversed(true);
		return dial;
	}

	protected Slider<Float> createFloatSlider(final String prefKey, float min, float max, float step,
			float defaultValue) {
		Slider<Float> slider = new Slider<Float>(screen, Orientation.HORIZONTAL);
		slider.onChanged(evt -> prefs.putFloat(prefKey, evt.getNewValue()));
		slider.setSliderModel(new FloatRangeSliderModel(min, max, (float) prefs.getFloat(prefKey, defaultValue), step));
		return slider;
	}

	protected Label createLabel(String text) {
		return createLabel(text, BitmapFont.Align.Left);
	}

	protected Label createLabel(String text, BitmapFont.Align halign) {
		Label l = new Label(text, screen);
		l.setTextVAlign(BitmapFont.VAlign.Center);
		l.setTextAlign(halign);
		return l;
	}

	protected ComboBox<Integer> create3WaySelect(final String prefKey, int defaultValue) {
		ComboBox<Integer> select = new ComboBox<Integer>(screen);
		select.addListItem("Default", SceneConfig.DEFAULT);
		select.addListItem("On", SceneConfig.TRUE);
		select.addListItem("Off", SceneConfig.FALSE);
		select.setSelectedByValue(prefs.getInt(prefKey, defaultValue));
		select.onChange(evt -> {
			if (!adjusting) {
				LOG.info(String.format("%s changed to %s", prefKey, evt.getNewValue()));
				prefs.putInt(prefKey, evt.getNewValue());
			}
		});
		return select;
	}

	private void createOptionsWindow() {
		adjusting = true;
		optionsWindow = new PersistentWindow(screen, SceneConfig.OPTIONS, 0, VAlign.Center, Align.Center,
				new Size(410, 530), true, SaveType.POSITION, prefs) {
			@Override
			protected void onCloseWindow() {
				stateManager.detach(OptionsAppState.this);
			}
		};
		optionsWindow.setWindowTitle("Options");
		optionsWindow.setResizable(true);
		BaseElement contentArea = optionsWindow.getContentArea();
		contentArea.setLayoutManager(new MigLayout(screen, "fill,wrap 1, ins 0", "[]", "[fill, grow][shrink 0]"));
		optionTabs = new TabControl(screen);
		// optionTabs.setFixedTabWidth(120);
		videoTab();
		audioTab();
		keysTab();
		addAdditionalTabs();
		contentArea.addElement(optionTabs, "growx, growy");

		PushButton defaults = new PushButton(screen) {
			{
				setStyleClass("fancy");
			}
		};
		defaults.onMouseReleased(evt -> setDefaults());
		defaults.setText("Defaults");
		defaults.setToolTipText("Set all options to their default");
		contentArea.addElement(defaults, "ax 50%");
		adjusting = false;
	}

	protected CheckBox createCheckbox(final String prefKey, String text, boolean defaultValue) {
		CheckBox check = new CheckBox(screen);
		check.setChecked(prefs.getBoolean(prefKey, defaultValue));
		check.onChange(evt -> {
			if (!evt.getSource().isAdjusting()) {
				LOG.info(String.format("%s changed to %s", prefKey, evt.getNewValue()));
				prefs.putBoolean(prefKey, evt.getNewValue());
			}
		});
		check.setText(text);
		check.setTextAlign(BitmapFont.Align.Left);
		return check;
	}

	protected final void setDefaults() {
		// Video defaults
		setVideoDefaults();

		// Audio defalts
		mute.setChecked(SceneConfig.AUDIO_MUTE_DEFAULT);
		masterVolume.setSelectedValue(SceneConfig.AUDIO_MASTER_VOLUME_DEFAULT);
		ambientVolume.setSelectedValue(SceneConfig.AUDIO_AMBIENT_VOLUME_DEFAULT);
		uiVolume.setSelectedValue(SceneConfig.AUDIO_UI_VOLUME_DEFAULT);
		musicVolume.setSelectedValue(SceneConfig.AUDIO_MASTER_VOLUME_DEFAULT);
		clutterShadowMode.runAdjusting(
				() -> clutterShadowMode.setSelectedByValue(SceneConfig.SCENE_CLUTTER_SHADOW_MODE_DEFAULT));

		setAdditionalDefaults();
	}

	protected void setVideoDefaults() {
		light.setSelectedValue(SceneConfig.SCENE_LIGHT_MULTIPLIER_DEFAULT);
		triPlanar.setSelectedIndex(SceneConfig.TERRAIN_TRI_PLANAR_DEFAULT);
		lodControl.setChecked(SceneConfig.TERRAIN_LOD_CONTROL_DEFAULT);
		highDetail.setChecked(SceneConfig.TERRAIN_HIGH_DETAIL_DEFAULT);
		prettyWater.setChecked(SceneConfig.TERRAIN_PRETTY_WATER_DEFAULT);
		wireframe.setChecked(SceneConfig.TERRAIN_WIREFRAME_DEFAULT);
		smoothScaling.setChecked(SceneConfig.TERRAIN_SMOOTH_SCALING_DEFAULT);
		clutterDensity.setSelectedValue(SceneConfig.SCENE_CLUTTER_DENSITY_DEFAULT);
		terrainLit.setChecked(SceneConfig.TERRAIN_LIT_DEFAULT);
		lightBeams.setChecked(SceneConfig.SCENE_LIGHT_BEAMS_DEFAULT);
		bloom.setChecked(SceneConfig.SCENE_BLOOM_DEFAULT);
		shadows.setChecked(SceneConfig.SCENE_SHADOWS_DEFAULT);
		ambientOcclusion.setChecked(SceneConfig.SCENE_SSAO_DEFAULT);
	}
}
