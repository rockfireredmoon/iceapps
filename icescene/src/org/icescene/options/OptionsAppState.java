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
import org.iceui.HPosition;
import org.iceui.VPosition;
import org.iceui.XTabPanelContent;
import org.iceui.controls.ElementStyle;
import org.iceui.controls.FancyButton;
import org.iceui.controls.FancyInputBox;
import org.iceui.controls.FancyPersistentWindow;
import org.iceui.controls.FancyWindow;
import org.iceui.controls.SaveType;
import org.iceui.controls.TabPanelContent;
import org.iceui.controls.UIUtil;
import org.iceui.controls.XTabControl;

import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
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

import icetone.controls.buttons.Button;
import icetone.controls.buttons.CheckBox;
import icetone.controls.lists.ComboBox;
import icetone.controls.lists.FloatRangeSliderModel;
import icetone.controls.lists.Slider;
import icetone.controls.lists.Table;
import icetone.controls.lists.Table.ColumnResizeMode;
import icetone.controls.lists.Table.SelectionMode;
import icetone.controls.lists.Table.TableRow;
import icetone.controls.text.Label;
import icetone.controls.text.TextField;
import icetone.controls.windows.TabControl;
import icetone.core.Container;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.Element.Orientation;
import icetone.core.layout.mig.MigLayout;

public class OptionsAppState extends IcemoonAppState<IcemoonAppState<?>> {

	private final class KeyInputBox extends FancyInputBox {
		private final RawInputListener raw;
		private int mods;
		private long lastKeyEv;
		private UserKeyMapping mapping;
		private int keyCode = -1;

		private KeyInputBox(UserKeyMapping mapping, ElementManager screen, Vector2f position, Size size, boolean closeable,
				RawInputListener raw) {
			super(screen, position, size, closeable);
			this.mapping = mapping;
			setDestroyOnHide(true);
			getDragBar().setFontColor(screen.getStyle("Common").getColorRGBA("warningColor"));
			setWindowTitle("Edit Keymap");
			setButtonOkText("Save");
			setIsResizable(false);
			setIsMovable(false);

			getContentArea().setLayoutManager(new MigLayout(screen, "fill,wrap 1", "", "[][][]"));
			getContentArea().removeChild(input);
			getContentArea().removeChild(buttons);
			getContentArea().addChild(new Label(String.format("%s is triggered by ", mapping.getSource().getMapping()), screen),
					"ax 50%");

			input = new TextField(screen, getUID() + ":text");
			input.setIsEnabled(false);
			input.setText(mapping.getDescription());

			getContentArea().addChild(input, "growx");
			getContentArea().addChild(buttons, "ax 50%");

			sizeToContent();
			UIUtil.center(screen, this);

			this.raw = raw;
		}

		@Override
		protected void onCloseWindow() {
			super.onCloseWindow();
			inputManager.removeRawInputListener(raw);
		}

		@Override
		public void onButtonCancelPressed(MouseButtonEvent evt, boolean toggled) {
			hideWindow();
			inputManager.removeRawInputListener(raw);
		}

		@Override
		public void onButtonOkPressed(MouseButtonEvent evt, String text, boolean toggled) {
			mapping.setTrigger(new KeyTrigger(keyCode));
			mapping.setModifiers(mods);
			mapping.update();
			hideWindow();
			inputManager.removeRawInputListener(raw);
			reloadKeys();
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
	private FancyPersistentWindow optionsWindow;
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
	private Table keyTable;
	private FancyButton resetKeymap;
	private FancyButton editKeyMap;
	private Slider<Float> light;

	public OptionsAppState(Preferences prefs) {
		super(prefs);
		addPrefKeyPattern(SceneConfig.TERRAIN_WIREFRAME + ".*");
	}

	@Override
	protected final IcemoonAppState<?> onInitialize(AppStateManager stateManager, IcesceneApp app) {
		screen = app.getScreen();
		createOptionsWindow();
		screen.addElement(optionsWindow, null, true);
		optionsWindow.showWindow();
		return null;
	}

	@Override
	protected final void onCleanup() {
		optionsWindow.hideWindow();
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
			wireframe.setIsCheckedNoCallback(Boolean.parseBoolean(evt.getNewValue()));
		}
	}

	protected void addAdditionalTabs() {
		// For subclasses to add more tabs
	}

	protected void setAdditionalDefaults() {
		// For subclasses to add more tabs
	}

	protected void keysTab() {

		TabPanelContent el = new XTabPanelContent(screen);
		el.setIsResizable(false);
		el.setIsMovable(false);
		el.setLayoutManager(new MigLayout(screen, "wrap 1", "[grow, fill]", "[grow, fill][shrink 0]"));

		keyTable = new Table(screen) {

			@Override
			public void onChange() {
				setAvailable();
			}
		};
		keyTable.setColumnResizeMode(ColumnResizeMode.AUTO_LAST);
		keyTable.setHeadersVisible(false);
		keyTable.setSelectionMode(SelectionMode.ROW);
		keyTable.addColumn("Category").setWidth(100);
		keyTable.addColumn("Mapping").setWidth(160);
		keyTable.addColumn("Key");

		reloadKeys();

		el.addChild(keyTable, "span 2");

		// Options
		Container c2 = new Container(screen);
		c2.setLayoutManager(new MigLayout(screen, "wrap 2", "push[][]push", "[]"));
		resetKeymap = new FancyButton(screen) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				TableRow sel = keyTable.getSelectedRow();
				if (sel.getValue() instanceof UserKeyMapping) {
					getApp().getKeyMapManager().resetMapping(((UserKeyMapping) sel.getValue()).getSource().getMapping());
					reloadKeys();
				}
			}
		};
		resetKeymap.setText("Reset");
		c2.addChild(resetKeymap);
		editKeyMap = new FancyButton(screen) {

			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
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
						keyEditDialog = new KeyInputBox(mapping, screen, new Vector2f(15, 15), FancyWindow.Size.LARGE, true, raw);
						screen.addElement(keyEditDialog, null, true);
						keyEditDialog.showAsModal(true);
					}
				}
			}
		};
		editKeyMap.setText("Change");
		c2.addChild(editKeyMap);
		el.addChild(c2);

		optionTabs.addTab("Keys");
		optionTabs.addTabChild(2, el);

		setAvailable();
	}

	private void setAvailable() {
		boolean mapping = keyTable.getSelectedRows().size() == 1 && keyTable.getSelectedRow().getValue() instanceof UserKeyMapping;
		resetKeymap.setIsEnabled(mapping);
		editKeyMap.setIsEnabled(mapping);
	}

	private void reloadKeys() {
		keyTable.removeAllRows();
		for (String category : app.getKeyMapManager().getCategories()) {
			TableRow row = new TableRow(screen, keyTable);
			row.setLeaf(false);
			row.addCell(category, category);
			row.addCell("", "");
			row.addCell("", "");
			keyTable.addRow(row, false);
			row.setExpanded(true);
			for (UserKeyMapping m : app.getKeyMapManager().getMappings(category)) {
				TableRow krow = new TableRow(screen, keyTable, m);
				krow.addCell("", "");
				krow.addCell(m.getSource().getMapping(), m.getSource().getMapping());
				krow.addCell(m.getDescription(), m.getTrigger());
				row.addRow(krow, false);
			}
		}
		keyTable.pack();
	}

	protected void videoTab() {
		TabPanelContent el = new XTabPanelContent(screen);
		el.setIsResizable(false);
		el.setIsMovable(false);
		el.setLayoutManager(new MigLayout(screen, "fill", "[150:150:150][]", "[][][][][][][][][]push"));

		// Terrain
		el.addChild(ElementStyle.medium(screen, createLabel("Terrain", "strongFont")), "span 2, width 100%, wrap, shrink");

		// Tri-planar
		el.addChild(createLabel("Tri-Planar"), "gapleft 16");
		el.addChild(triPlanar = create3WaySelect(SceneConfig.TERRAIN_TRI_PLANAR, SceneConfig.TERRAIN_TRI_PLANAR_DEFAULT), "wrap");

		// Checkbox container 1
		Container c1 = new Container(screen);
		c1.setLayoutManager(new MigLayout(screen, "wrap 2", "[][]", "[][][][]"));

		// Wireframe
		c1.addChild(highDetail = createCheckbox(SceneConfig.TERRAIN_HIGH_DETAIL, "High Detail",
				SceneConfig.TERRAIN_HIGH_DETAIL_DEFAULT));
		c1.addChild(lodControl = createCheckbox(SceneConfig.TERRAIN_LOD_CONTROL, "LOD Control",
				SceneConfig.TERRAIN_LOD_CONTROL_DEFAULT));
		c1.addChild(terrainLit = createCheckbox(SceneConfig.TERRAIN_LIT, "Lit Terrain", SceneConfig.TERRAIN_LIT_DEFAULT));
		c1.addChild(prettyWater = createCheckbox(SceneConfig.TERRAIN_PRETTY_WATER, "Pretty Water",
				SceneConfig.TERRAIN_PRETTY_WATER_DEFAULT));
		c1.addChild(smoothScaling = createCheckbox(SceneConfig.TERRAIN_SMOOTH_SCALING, "Smooth Scaling",
				SceneConfig.TERRAIN_SMOOTH_SCALING_DEFAULT));
		c1.addChild(wireframe = createCheckbox(SceneConfig.TERRAIN_WIREFRAME, "Wireframe", SceneConfig.TERRAIN_WIREFRAME_DEFAULT));

		el.addChild(c1, "gapleft 32, span 2, wrap");

		// Scene
		el.addChild(ElementStyle.medium(screen, createLabel("Scene", "strongFont")), "span 2, width 100%, wrap, shrink");

		el.addChild(createLabel("Clutter Shadows"), "gapleft 16");
		clutterShadowMode = new ComboBox<ShadowMode>(screen) {
			@Override
			public void onChange(int selectedIndex, ShadowMode value) {
				prefs.put(SceneConfig.SCENE_CLUTTER_SHADOW_MODE, value.toString());
			}
		};
		for (ShadowMode sm : ShadowMode.values()) {
			clutterShadowMode.addListItem(Icelib.toEnglish(sm.name()), sm);
		}
		el.addChild(clutterShadowMode, "growx, wrap");

		el.addChild(createLabel("Clutter Density"), "gapleft 16");
		clutterDensity = new Slider<Float>(screen, Orientation.HORIZONTAL, true) {
			@Override
			public void onChange(Float o) {
				prefs.putFloat(SceneConfig.SCENE_CLUTTER_DENSITY, ((Float) o).floatValue());
			}
		};
		clutterDensity.setSliderModel(new FloatRangeSliderModel(0, 1,
				(float) prefs.getFloat(SceneConfig.SCENE_CLUTTER_DENSITY, SceneConfig.SCENE_CLUTTER_DENSITY_DEFAULT), 0.1f));
		clutterDensity.setLockToStep(true);
		el.addChild(clutterDensity, "growx, wrap");

		// Distance
		el.addChild(createLabel("Distance"), "gapleft 16");
		distance = new Slider<Float>(screen, Orientation.HORIZONTAL, true) {
			@Override
			public void onChange(Float o) {
				prefs.putFloat(SceneConfig.SCENE_DISTANCE, ((Float) o).floatValue());
			}
		};
		distance.setSliderModel(new FloatRangeSliderModel(0, 1024,
				(float) prefs.getFloat(SceneConfig.SCENE_DISTANCE, SceneConfig.SCENE_DISTANCE_DEFAULT), 10f));
		el.addChild(distance, "growx, wrap");

		// Light
		el.addChild(createLabel("Light"), "gapleft 16");
		light = new Slider<Float>(screen, Orientation.HORIZONTAL, true) {
			@Override
			public void onChange(Float o) {
				prefs.putFloat(SceneConfig.SCENE_LIGHT_MULTIPLIER, ((Float) o).floatValue());
			}
		};
		light.setSliderModel(new FloatRangeSliderModel(0, 5,
				(float) prefs.getFloat(SceneConfig.SCENE_LIGHT_MULTIPLIER, SceneConfig.SCENE_LIGHT_MULTIPLIER_DEFAULT), 0.1f));
		el.addChild(light, "growx, wrap");

		// Checkbox container 1
		Container c2 = new Container(screen);
		c2.setLayoutManager(new MigLayout(screen, "wrap 2", "[][]", "[][]"));

		c2.addChild(
				lightBeams = createCheckbox(SceneConfig.SCENE_LIGHT_BEAMS, "Light beams", SceneConfig.SCENE_LIGHT_BEAMS_DEFAULT));
		c2.addChild(bloom = createCheckbox(SceneConfig.SCENE_BLOOM, "Bloom", SceneConfig.SCENE_BLOOM_DEFAULT));
		c2.addChild(shadows = createCheckbox(SceneConfig.SCENE_SHADOWS, "Shadows", SceneConfig.SCENE_SHADOWS_DEFAULT));
		c2.addChild(ambientOcclusion = createCheckbox(SceneConfig.SCENE_SSAO, "Ambient Occlusion", SceneConfig.SCENE_SSAO_DEFAULT));

		el.addChild(c2, "gapleft 32, span 2, wrap");

		// Dislay
		FancyButton display = new FancyButton(screen) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				DisplaySettingsWindow w = new DisplaySettingsWindow(screen, Vector2f.ZERO,
						((IcesceneApp) app).getAppSettingsName()) {
				};
				UIUtil.center(screen, w);
				w.showWithEffect();
			}
		};
		display.setText("Display");
		el.addChild(display, "ax 50%, span 2, wrap");

		optionTabs.addTab("Video");
		optionTabs.addTabChild(0, el);
	}

	private void audioTab() {

		// Volumes
		XTabPanelContent p = new XTabPanelContent(screen);
		p.setLayoutManager(new MigLayout(screen, "ins 10 0 0 0, fill",
				"push[align center,25%!]push[align center,25%!]push[align center,25%!]push[align center,25%!]", "[]4[grow]4[][]"));

		p.addChild(createLabel("Full", BitmapFont.Align.Center, screen.getStyle("Common").getFloat("smallFontSize")));
		p.addChild(createLabel("Full", BitmapFont.Align.Center, screen.getStyle("Common").getFloat("smallFontSize")));
		p.addChild(createLabel("Full", BitmapFont.Align.Center, screen.getStyle("Common").getFloat("smallFontSize")));
		p.addChild(createLabel("Full", BitmapFont.Align.Center, screen.getStyle("Common").getFloat("smallFontSize")), "wrap");

		p.addChild(masterVolume = createVolumeDial(SceneConfig.AUDIO_MASTER_VOLUME, SceneConfig.AUDIO_MASTER_VOLUME_DEFAULT),
				"width 24, growy");
		p.addChild(ambientVolume = createVolumeDial(SceneConfig.AUDIO_AMBIENT_VOLUME, SceneConfig.AUDIO_AMBIENT_VOLUME_DEFAULT),
				"width 24, growy");
		p.addChild(uiVolume = createVolumeDial(SceneConfig.AUDIO_UI_VOLUME, SceneConfig.AUDIO_UI_VOLUME_DEFAULT),
				"width 24, growy");
		p.addChild(musicVolume = createVolumeDial(SceneConfig.AUDIO_MUSIC_VOLUME, SceneConfig.AUDIO_MUSIC_VOLUME_DEFAULT),
				"width 24, wrap, growy");

		p.addChild(createLabel("Off", BitmapFont.Align.Center, screen.getStyle("Common").getFloat("smallFontSize")));
		p.addChild(createLabel("Off", BitmapFont.Align.Center, screen.getStyle("Common").getFloat("smallFontSize")));
		p.addChild(createLabel("Off", BitmapFont.Align.Center, screen.getStyle("Common").getFloat("smallFontSize")));
		p.addChild(createLabel("Off", BitmapFont.Align.Center, screen.getStyle("Common").getFloat("smallFontSize")), "wrap");
		p.addChild(createLabel("Master", BitmapFont.Align.Center));
		p.addChild(createLabel("Ambient", BitmapFont.Align.Center));
		p.addChild(createLabel("UI", BitmapFont.Align.Center));
		p.addChild(createLabel("Music", BitmapFont.Align.Center), "wrap");

		p.addChild(mute = createCheckbox(SceneConfig.AUDIO_MUTE, "Mute", SceneConfig.AUDIO_MUTE_DEFAULT),
				"gap 10px 10px, span 4, ax 50%");
		optionTabs.addTab("Audio", p);
	}

	protected Slider<Float> createVolumeDial(final String prefKey, float defaultValue) {
		Slider<Float> dial = new Slider<Float>(screen, Orientation.VERTICAL, true) {
			@Override
			public void onChange(Float o) {
				LOG.info(String.format("%s changed to %s", prefKey, o));
				prefs.putFloat(prefKey, ((Float) o));
			}
		};
		dial.setSliderModel(new FloatRangeSliderModel(0, 1, prefs.getFloat(prefKey, defaultValue), 0.05f));
		dial.setLockToStep(true);
		return dial;
	}

	protected Slider<Float> createFloatSlider(final String prefKey, float min, float max, float step, float defaultValue) {
		Slider<Float> slider = new Slider<Float>(screen, Orientation.HORIZONTAL, true) {
			@Override
			public void onChange(Float o) {
				prefs.putFloat(prefKey, ((Float) o));
			}
		};
		slider.setSliderModel(new FloatRangeSliderModel(min, max, (float) prefs.getFloat(prefKey, defaultValue), step));
		return slider;
	}

	protected Label createLabel(String text, String fontName) {
		return createLabel(text, BitmapFont.Align.Left, -1, fontName);
	}

	protected Label createLabel(String text) {
		return createLabel(text, BitmapFont.Align.Left);
	}

	protected Label createLabel(String text, BitmapFont.Align halign) {
		return createLabel(text, halign, -1);
	}

	protected Label createLabel(String text, BitmapFont.Align halign, float fontSize) {
		return createLabel(text, halign, fontSize, "defaultFont");
	}

	protected Label createLabel(String text, BitmapFont.Align halign, float fontSize, String fontName) {
		Label l = new Label(text, screen);
		// l.setFont(screen.getStyle("Font").getString(fontName));
		l.setTextVAlign(BitmapFont.VAlign.Center);
		l.setTextAlign(halign);
		if (fontSize != -1) {
			// l.setFontSize(fontSize);
		}
		return l;
	}

	protected ComboBox<Integer> create3WaySelect(final String prefKey, int defaultValue) {
		ComboBox<Integer> select = new ComboBox<Integer>(screen) {
			@Override
			public void onChange(int i, Integer o) {
				if (!adjusting) {
					LOG.info(String.format("%s changed to %s", prefKey, o));
					prefs.putInt(prefKey, (Integer) o);
				}
			}
		};
		select.addListItem("Default", SceneConfig.DEFAULT);
		select.addListItem("On", SceneConfig.TRUE);
		select.addListItem("Off", SceneConfig.FALSE);
		select.setSelectedByValue(prefs.getInt(prefKey, defaultValue), false);
		return select;
	}

	private void createOptionsWindow() {
		adjusting = true;
		optionsWindow = new FancyPersistentWindow(screen, SceneConfig.OPTIONS,
				screen.getStyle("Common").getInt("defaultWindowOffset"), VPosition.MIDDLE, HPosition.CENTER, new Vector2f(410, 530),
				FancyWindow.Size.SMALL, true, SaveType.POSITION, prefs) {
			@Override
			protected void onCloseWindow() {
				stateManager.detach(OptionsAppState.this);
			}
		};
		optionsWindow.setWindowTitle("Options");
		optionsWindow.setIsResizable(true);
		Element contentArea = optionsWindow.getContentArea();
		contentArea.setLayoutManager(new MigLayout(screen, "fill,wrap 1, ins 0", "[]", "[fill, grow][shrink 0]"));
		optionTabs = new XTabControl(screen);
		// optionTabs.setFixedTabWidth(120);
		videoTab();
		audioTab();
		keysTab();
		addAdditionalTabs();
		contentArea.addChild(optionTabs, "growx, growy");

		Button defaults = new FancyButton(screen) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				setDefaults();
			}
		};
		defaults.setText("Defaults");
		defaults.setToolTipText("Set all options to their default");
		contentArea.addChild(defaults, "ax 50%");
		adjusting = false;
	}

	protected CheckBox createCheckbox(final String prefKey, String text, boolean defaultValue) {
		CheckBox check = new CheckBox(screen) {
			@Override
			public void onMouseLeftReleased(MouseButtonEvent evt) {
				super.onMouseLeftReleased(evt);
				LOG.info(String.format("%s changed to %s", prefKey, getIsChecked()));
				prefs.putBoolean(prefKey, getIsChecked());
			}
		};
		check.setLabelText(text);
		check.setTextAlign(BitmapFont.Align.Left);
		check.setIsCheckedNoCallback(prefs.getBoolean(prefKey, defaultValue));
		return check;
	}

	protected final void setDefaults() {
		// Video defaults
		setVideoDefaults();

		// Audio defalts
		mute.setIsChecked(SceneConfig.AUDIO_MUTE_DEFAULT);
		masterVolume.setSelectedValueWithCallback(SceneConfig.AUDIO_MASTER_VOLUME_DEFAULT);
		ambientVolume.setSelectedValueWithCallback(SceneConfig.AUDIO_AMBIENT_VOLUME_DEFAULT);
		uiVolume.setSelectedValueWithCallback(SceneConfig.AUDIO_UI_VOLUME_DEFAULT);
		musicVolume.setSelectedValueWithCallback(SceneConfig.AUDIO_MASTER_VOLUME_DEFAULT);
		clutterShadowMode.setSelectedByValue(SceneConfig.SCENE_CLUTTER_SHADOW_MODE_DEFAULT, false);

		setAdditionalDefaults();
	}

	protected void setVideoDefaults() {
		light.setSelectedValueWithCallback(SceneConfig.SCENE_LIGHT_MULTIPLIER_DEFAULT);
		triPlanar.setSelectedIndexWithCallback(SceneConfig.TERRAIN_TRI_PLANAR_DEFAULT);
		lodControl.setIsChecked(SceneConfig.TERRAIN_LOD_CONTROL_DEFAULT);
		highDetail.setIsChecked(SceneConfig.TERRAIN_HIGH_DETAIL_DEFAULT);
		prettyWater.setIsChecked(SceneConfig.TERRAIN_PRETTY_WATER_DEFAULT);
		wireframe.setIsChecked(SceneConfig.TERRAIN_WIREFRAME_DEFAULT);
		smoothScaling.setIsChecked(SceneConfig.TERRAIN_SMOOTH_SCALING_DEFAULT);
		clutterDensity.setSelectedValueWithCallback(SceneConfig.SCENE_CLUTTER_DENSITY_DEFAULT);
		terrainLit.setIsChecked(SceneConfig.TERRAIN_LIT_DEFAULT);
		lightBeams.setIsChecked(SceneConfig.SCENE_LIGHT_BEAMS_DEFAULT);
		bloom.setIsChecked(SceneConfig.SCENE_BLOOM_DEFAULT);
		shadows.setIsChecked(SceneConfig.SCENE_SHADOWS_DEFAULT);
		ambientOcclusion.setIsChecked(SceneConfig.SCENE_SSAO_DEFAULT);
	}
}
