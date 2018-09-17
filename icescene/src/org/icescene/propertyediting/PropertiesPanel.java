package org.icescene.propertyediting;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import org.icelib.Icelib;
import org.icescene.SceneConfig;
import org.icescene.audio.AudioField;
import org.icescene.audio.AudioQueue;
import org.icescene.propertyediting.Property.Hint;
import org.icescene.propertyediting.PropertyInfo.Range;
import org.icescene.props.AbstractProp;
import org.icescene.props.Options;
import org.iceui.IceUI;
import org.iceui.controls.ElementStyle;
import org.iceui.controls.ImageFieldControl;
import org.iceui.controls.QuaternionControl;
import org.iceui.controls.SoundFieldControl;

import com.jme3.font.LineWrapMode;
import com.jme3.input.KeyInput;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;

import icemoon.iceloader.ServerAssetManager;
import icetone.controls.buttons.CheckBox;
import icetone.controls.lists.ComboBox;
import icetone.controls.lists.FloatRangeSpinnerModel;
import icetone.controls.lists.IntegerRangeSpinnerModel;
import icetone.controls.lists.Spinner;
import icetone.controls.text.Label;
import icetone.controls.text.TextField;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Form;
import icetone.core.Orientation;
import icetone.core.Element;
import icetone.core.ToolKit;
import icetone.core.layout.mig.MigLayout;
import icetone.core.undo.UndoManager;
import icetone.core.undo.UndoableCommand;
import icetone.extras.chooser.ColorFieldControl;
import icetone.extras.chooser.StringChooserModel;
import icetone.extras.controls.Vector2fControl;
import icetone.extras.controls.Vector3fControl;
import icetone.extras.controls.Vector4fControl;
import icetone.extras.controls.Vector4fControl.Type;

public class PropertiesPanel<T extends PropertyBean> extends Element implements PropertyChangeListener {

	private final static Logger LOG = Logger.getLogger(PropertiesPanel.class.getName());
	private TreeMap<String, PropertyInfo<T>> properties;
	private T prop;
	private Form propertyForm;
	protected Map<String, BaseElement> propertyComponents = new TreeMap<String, BaseElement>();
	private final Preferences prefs;
	private UndoManager undoManager;

	public PropertiesPanel(BaseScreen screen, Preferences prefs, UndoManager undoManager) {
		this(screen, prefs);
		this.undoManager = undoManager;
	}

	public PropertiesPanel(BaseScreen screen, Preferences prefs) {
		super(screen);
		this.prefs = prefs;
		setAsContainerOnly();
		setLayoutManager(new MigLayout(screen, "", "[][]", "[shrink 0]"));
	}

	public void setUndoManager(UndoManager undoManager) {
		this.undoManager = undoManager;
	}

	public T getObject() {
		return prop;
	}

	public void setObject(T prop) {
		if (this.prop != null) {
			this.prop.removePropertyChangeListener(this);
		}
		this.prop = prop;
		if (this.prop != null) {
			this.prop.addPropertyChangeListener(this);
		}
		rebuild();
	}

	protected void setAvailable() {
	}

	protected void onPropertyChange(PropertyInfo<T> info, T object, Object value) {
	}

	private void buildPropertyRow(final PropertyInfo<T> info) {
		int y = 0;
		LOG.fine(String.format("Doing property %s", info));

		if (info.isWriteable()) {
			if (boolean.class.isAssignableFrom(info.getClazz())) {
				addBooleanControl(info);
			} else {
				addLabel(info);
				if (int.class.isAssignableFrom(info.getClazz())) {
					addIntControl(info);
				} else if (float.class.isAssignableFrom(info.getClazz())) {
					addFloatControl(info);
				} else if (Vector3f.class.isAssignableFrom(info.getClazz())) {
					addVector3fControl(info);
				} else if (Vector4f.class.isAssignableFrom(info.getClazz())) {
					addVector4fControl(info);
				} else if (Quaternion.class.isAssignableFrom(info.getClazz())) {
					addQuaternionControl(info);
				} else if (Vector2f.class.isAssignableFrom(info.getClazz())) {
					addVector2fControl(info);
				} else if (ColorRGBA.class.isAssignableFrom(info.getClazz())) {
					addColorControl(info);
				} else {
					if (info.getOptions() != null) {
						addSelectControl(info);
					} else if (String.class.isAssignableFrom(info.getClazz())) {

						switch (info.getHint()) {
						case TEXTURE_PATH:
							addImageControl(info);
							break;
						case SOUND_PATH:
							addSoundControl(info);
							break;
						default:
							addStringControl(info);
							break;
						}
					} else {
						addValueLabel(info);
					}
				}
			}
		} else if (info.isReadable()) {
			if (info.getHint().equals(Hint.LABEL)) {
				ElementStyle.normal(addValueLabel(info, "span 2, wrap"), true, false);
			} else {
				addLabel(info);
				addValueLabel(info);
			}
		}
	}

	private void gatherPropProperties() throws SecurityException, RuntimeException {
		// First gather up all the properties so we can determine what is
		// readable/writeable etc
		properties = new TreeMap<String, PropertyInfo<T>>();
		if (prop != null) {
			for (Method method : prop.getClass().getMethods()) {
				Property property = method.getAnnotation(Property.class);
				if (property != null) {
					// Work out ID from method name
					boolean writer = false;
					String id = method.getName();
					if (id.startsWith("get")) {
						id = id.substring(3);
					} else if (id.startsWith("set")) {
						id = id.substring(3);
						writer = true;
					} else if (id.startsWith("is")) {
						id = id.substring(2);
					}

					// ID's may occur more than once in case of getter/setter.
					// Only one needs
					// to be annotated with
					PropertyInfo<T> info = properties.get(id);
					if (info == null) {
						info = new PropertyInfo();
						info.setId(id);
						info.setEventId(property.eventId());
						info.setObject(prop);
						LOG.fine(String.format("Adding property %s", id));
						properties.put(id, info);
					}

					// Readable / writeable
					if (writer) {
						info.setSetter(method);
					} else {
						info.setGetter(method);
					}

					// Hint
					if (info.getHint().equals(Hint.UNSET) && !property.hint().equals(Hint.UNSET)) {
						info.setHint(property.hint());
					}

					// Label
					String label = property.label();
					if (!label.equals("")) {
						info.setLabel(label);
					}

					// Weight
					if (property.weight() >= 0) {
						info.setWeight(property.weight());
					}

					// Ranges
					FloatRange fr = method.getAnnotation(FloatRange.class);
					if (fr != null) {
						Range<Float> range = new PropertyInfo.Range<Float>(fr.min(), fr.max(), fr.incr());
						if (fr.precision() != -1)
							range.setPrecision(fr.precision());
						info.setRange(range);
					}

					// Class
					if (info.getClazz() == null) {
						if (writer) {
							info.setClazz(method.getParameterTypes()[0]);
						} else {
							info.setClazz(method.getReturnType());
						}
					}
				}
			}

			// Some attributes may have options derived from prop at run time
			for (Method method : prop.getClass().getMethods()) {
				Options propertyOptions = method.getAnnotation(Options.class);
				if (propertyOptions != null) {
					String forProp = propertyOptions.forProperty();
					PropertyInfo pinfo = properties.get(forProp);
					try {
						pinfo.setOptions((List<String>) method.invoke(prop));
					} catch (Exception ex) {
						throw new RuntimeException(
								String.format("Could not get property options for %s", propertyOptions.forProperty()));
					}
				}
			}
		}
	}

	private List<PropertyInfo> getSortedProperties() {
		// Sort the map based on its values
		final List<PropertyInfo> infos = new ArrayList<PropertyInfo>(properties.values());
		Collections.sort(infos);
		return infos;
	}

	private void addLabel(PropertyInfo info) {
		Label label = new Label(getScreen());
		label.setText(info.getLabel().equals("") ? getComponentId(info) : info.getLabel());
		ElementStyle.normal(label);
		addElement(label);
	}

	@Override
	public BaseElement addElement(BaseElement el, Object constraints) {
		super.addElement(el, constraints);
		return this;

		// TODO no idea why this is required, but without it the panel does not
		// get
		// clipped properly
		// el.addClippingLayer(this.getElementParent().getElementParent().getElementParent());
	}

	private Label addValueLabel(PropertyInfo info) {
		return addValueLabel(info, "wrap");
	}

	private Label addValueLabel(PropertyInfo info, String contraints) {
		Label valueLabel = new Label(getScreen());
		try {
			final String t = String.valueOf(info.getGetter().invoke(info.getObject()));
			valueLabel.setText(t);
			valueLabel.setToolTipText(t);
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Failed to read property.", e);
			valueLabel.setText("Error!");
		}
		valueLabel.setTextWrap(LineWrapMode.Character);
		ElementStyle.normal(valueLabel);
		propertyComponents.put(getComponentId(info), valueLabel);
		addElement(valueLabel, contraints);
		return valueLabel;
	}

	private void addSoundControl(final PropertyInfo<T> info) {

		try {

			ChooserInfo chooserInfo = getChooserInfo(info);
			String pattern = chooserInfo == null ? "Sounds/.*" : chooserInfo.pattern();
			final String root = chooserInfo == null ? "" : chooserInfo.root();

			String val = (String) info.getGetter().invoke(prop);
			Set<String> images = ((ServerAssetManager) ToolKit.get().getApplication().getAssetManager())
					.getAssetNamesMatching(pattern);
			AudioField col = new AudioField(screen, org.iceui.controls.SoundFieldControl.Type.ALL, val, true, true,
					new StringChooserModel(images), prefs, AudioQueue.PREVIEWS) {
				@Override
				protected String getChooserPathFromValue() {
					if (root.equals("")) {
						return super.getChooserPathFromValue();
					} else {
						return value == null ? root
								: (value.startsWith(root) || value.indexOf('/') != -1 ? value : root + "/" + value);
					}
				}

				@Override
				protected void setValueFromChooserPath(String path) {
					if (root.equals("") || !path.startsWith(root)) {
						super.setValueFromChooserPath(path);
					} else {
						value = Icelib.getFilename(path);
					}
				}

				@Override
				protected void onResourceChosen(String newResource) {
					if (undoManager != null) {
						undoManager.storeAndExecute(new SetPropertyCommand(prop, info, getValue()) {
							@Override
							protected void updateUIValue(Object value) {
								setValue((String) value);
							}
						});
					} else {
						try {
							info.getSetter().invoke(prop, getValue());
						} catch (Exception ex) {
							LOG.log(Level.SEVERE, "Failed to set value.", ex);
						}
					}
				}
			};
			col.addToForm(propertyForm);
			propertyComponents.put(getComponentId(info), col);
			ElementStyle.normal(col);
			addElement(col, "growx, wrap");
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, "Failed to get value.", ex);
			addValueLabel(info);

		}
	}

	private ChooserInfo getChooserInfo(final PropertyInfo<T> info) {
		ChooserInfo chooserInfo = info.getSetter().getAnnotation(ChooserInfo.class);
		if (chooserInfo == null && info.getGetter() != null) {
			chooserInfo = info.getGetter().getAnnotation(ChooserInfo.class);
		}
		return chooserInfo;
	}

	private void addImageControl(final PropertyInfo<T> info) {

		try {

			ChooserInfo chooserInfo = getChooserInfo(info);
			String pattern = chooserInfo == null ? "Textures/Effects/.*" : chooserInfo.pattern();
			final String root = chooserInfo == null ? "" : chooserInfo.root();

			String val = (String) info.getGetter().invoke(prop);
			Set<String> images = ((ServerAssetManager) ToolKit.get().getApplication().getAssetManager())
					.getAssetNamesMatching(pattern);
			ImageFieldControl col = new ImageFieldControl(screen, val, new StringChooserModel(images), prefs) {
				@Override
				protected String getChooserPathFromValue() {
					if (root.equals("")) {
						return super.getChooserPathFromValue();
					} else {
						return value == null ? null : root + "/" + value;
					}
				}

				@Override
				protected void setValueFromChooserPath(String path) {
					if (root.equals("") || !path.startsWith(root)) {
						super.setValueFromChooserPath(path);
					} else {
						value = Icelib.getFilename(path);
					}
				}

				@Override
				protected void onResourceChosen(String newResource) {
					if (undoManager != null) {
						undoManager.storeAndExecute(new SetPropertyCommand(prop, info, getValue()) {
							@Override
							protected void updateUIValue(Object value) {
								setValue((String) value);
							}
						});
					} else {
						try {
							info.getSetter().invoke(prop, getValue());
						} catch (Exception ex) {
							LOG.log(Level.SEVERE, "Failed to set value.", ex);
						}
					}
				}
			};
			col.addToForm(propertyForm);
			propertyComponents.put(getComponentId(info), col);
			ElementStyle.normal(col);
			addElement(col, "growx, wrap");
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, "Failed to get value.", ex);
			addValueLabel(info);

		}
	}

	private void addColorControl(final PropertyInfo info) {
		float min = 0;
		float max = 65536;
		float incr = 1f;

		switch (info.getHint()) {
		case SCALE:
			max = 10;
			incr = 0.1f;
			break;
		}

		try {
			ColorRGBA val = (ColorRGBA) info.getGetter().invoke(prop);
			ColorFieldControl col = new ColorFieldControl(screen, val, false);
			col.onChange(evt -> {
				if (undoManager != null) {
					undoManager.storeAndExecute(new SetPropertyCommand(prop, info, evt.getNewValue()) {
						@Override
						protected void updateUIValue(Object value) {
							evt.getSource().setValue((ColorRGBA) value);
							setAvailable();
						}
					});
				} else {
					try {
						info.getSetter().invoke(prop, evt.getNewValue());
						setAvailable();
					} catch (Exception ex) {
						LOG.log(Level.SEVERE, "Failed to set value.", ex);
					}
				}
			});
			col.addToForm(propertyForm);
			propertyComponents.put(getComponentId(info), col);
			ElementStyle.normal(col);
			addElement(col, "growx, wrap");
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, "Failed to get value.", ex);
			addValueLabel(info);

		}
	}

	private void addStringControl(final PropertyInfo info) {
		try {
			String val = (String) info.getGetter().invoke(prop);
			TextField sp = new TextField(screen) {

				{
					onKeyboardFocusLost(evt -> setValue());
				}

				@Override
				public void onKeyRelease(KeyInputEvent evt) {
					super.onKeyRelease(evt);
					if (evt.getKeyCode() == KeyInput.KEY_RETURN) {
						setValue();
					}
				}

				private void setValue() {
					if (undoManager != null) {
						undoManager.storeAndExecute(new SetPropertyCommand(prop, info, getText()) {
							@Override
							protected void updateUIValue(Object value) {
								setText((String) value);
							}
						});
					} else {
						try {
							info.getSetter().invoke(prop, getText());
						} catch (Exception ex) {
							LOG.log(Level.SEVERE, "Failed to set value.", ex);
						}
					}
				}
			};
			sp.setText(val == null ? "" : val);
			propertyComponents.put(getComponentId(info), sp);
			ElementStyle.normal(sp);
			propertyForm.addFormElement(sp);
			addElement(sp, "growx, wrap");
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, "Failed to get value.", ex);
			addValueLabel(info);

		}
	}

	private void setProperty(final PropertyInfo info, Object value) {
		runAdjusting(() -> {
			try {
				info.getSetter().invoke(prop, value);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			setAvailable();
		});
	}

	private void addBooleanControl(final PropertyInfo info) {
		try {
			Boolean val = (Boolean) info.getGetter().invoke(prop);
			CheckBox sp = new CheckBox(screen);
			sp.setChecked(val);
			sp.onChange(evt -> {
				if (undoManager != null) {
					undoManager.storeAndExecute(new SetPropertyCommand(prop, info, evt.getNewValue()) {
						@Override
						protected void updateUIValue(Object value) {
							evt.getSource().runAdjusting(() -> evt.getSource().setState((Boolean) value));
							setAvailable();
						}
					});
				} else {
					try {
						setProperty(info, evt.getNewValue());
					} catch (Exception e) {
						LOG.log(Level.SEVERE, "Failed to set property.", e);
					}
				}
			});
			propertyComponents.put(getComponentId(info), sp);
			sp.setText(info.getLabel().equals("") ? getComponentId(info) : info.getLabel());
			propertyForm.addFormElement(sp);
			ElementStyle.normal(sp);
			addElement(sp, "span 2, growx, wrap");
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, "Failed to get value.", ex);
			addValueLabel(info);

		}
	}

	private String getComponentId(final PropertyInfo<T> info) {
		return info.getEventId().equals("") ? info.getId() : info.getEventId();
	}

	private void addIntControl(final PropertyInfo<T> info) {
		int min = 0;
		int max = 65536;
		int incr = 1;
		if (info.getRange() != null) {
			PropertyInfo.Range<Integer> f = (PropertyInfo.Range<Integer>) info.getRange();
			min = f.getMin();
			max = f.getMax();
			incr = f.getIncr();
		}

		switch (info.getHint()) {
		case DISTANCE:
			max = 1000;
			incr = 1;
			break;
		case RADIUS:
			max = 1000;
			incr = 1;
			break;
		case ANGLE:
			max = 360;
			incr = 1;
			break;
		case SCALE:
			max = 10;
			incr = 1;
			break;
		}

		try {
			int val = (Integer) info.getGetter().invoke(prop);
			Spinner<Integer> sp = new Spinner<Integer>(screen, Orientation.HORIZONTAL, false);
			sp.onChange(evt -> {
				if (isAdjusting()) {
					if (undoManager != null) {
						undoManager.storeAndExecute(new SetPropertyCommand(prop, info, evt.getNewValue()) {
							@Override
							protected void updateUIValue(Object value) {
								evt.getSource().setSelectedValue((Integer) value);
							}
						});
					} else {
						try {
							setProperty(info, evt.getNewValue());
						} catch (Exception e) {
							LOG.log(Level.SEVERE, "Failed to set property.", e);
						}
					}
				}
			});
			ElementStyle.normal(sp);
			propertyForm.addFormElement(sp);
			propertyComponents.put(getComponentId(info), sp);
			sp.setSpinnerModel(new IntegerRangeSpinnerModel(min, max, incr, val));
			addElement(sp, "growx, wrap");
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, "Failed to get value.", ex);
			addValueLabel(info);

		}
	}

	private void addFloatControl(final PropertyInfo<T> info) {
		float min = 0;
		float max = 65536;
		float incr = 1f;
		if (info.getRange() != null) {
			PropertyInfo.Range<Float> f = (PropertyInfo.Range<Float>) info.getRange();
			min = f.getMin();
			max = f.getMax();
			incr = f.getIncr();
		}

		switch (info.getHint()) {
		case DISTANCE:
			max = 1000f;
			incr = 1f;
			break;
		case RADIUS:
			max = 1000f;
			incr = 1f;
			break;
		case ANGLE:
			max = 360f;
			incr = 1f;
			break;
		case SCALE:
			max = 10;
			incr = 0.1f;
			break;
		}

		try {
			float val = (Float) info.getGetter().invoke(prop);
			Spinner<Float> sp = new Spinner<Float>(screen, Orientation.HORIZONTAL, false);
			sp.onChange(evt -> {
				if (!isAdjusting()) {
					if (undoManager != null) {
						undoManager.storeAndExecute(new SetPropertyCommand(prop, info, evt.getNewValue()) {
							@Override
							protected void updateUIValue(Object value) {
								evt.getSource().setSelectedValue((Float) value);
								setAvailable();
							}
						});
					} else {
						try {
							setProperty(info, evt.getNewValue());
						} catch (Exception e) {
							LOG.log(Level.SEVERE, "Failed to set property.", e);
						}
					}
				}
			});
			ElementStyle.normal(sp);
			propertyForm.addFormElement(sp);
			propertyComponents.put(getComponentId(info), sp);
			sp.setSpinnerModel(new FloatRangeSpinnerModel(min, max, incr, val));
			addElement(sp, "growx, wrap");
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, "Failed to get value.", ex);
			addValueLabel(info);

		}
	}

	private void addSelectControl(final PropertyInfo info) {
		try {
			String val = (String) info.getGetter().invoke(prop);
			ComboBox<String> sp = new ComboBox<String>(screen);
			sp.onChange(evt -> {
				if (!isAdjusting()) {

					if (undoManager != null) {
						undoManager.storeAndExecute(new SetPropertyCommand(prop, info, evt.getNewValue()) {
							@Override
							protected void updateUIValue(Object value) {
								PropertiesPanel.this.runAdjusting(() -> sp.setSelectedByValue((String) value));
								setAvailable();
							}
						});
					} else {
						try {
							setProperty(info, evt.getNewValue());
						} catch (Exception e) {
							LOG.log(Level.SEVERE, "Failed to set property.", e);
						}
					}
				}
			});
			final List<String> optionsX = info.getOptions();
			for (String opt : optionsX) {
				sp.addListItem(opt, opt);
			}
			propertyForm.addFormElement(sp);
			propertyComponents.put(getComponentId(info), sp);
			ElementStyle.normal(sp);
			addElement(sp, "growx, wrap");
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, "Failed to get value.", ex);
			addValueLabel(info);

		}
	}

	private void addVector4fControl(final PropertyInfo<T> info) {
		float min = 0;
		float max = 65536;
		float incr = 1f;
		if (info.getRange() != null) {
			PropertyInfo.Range<Float> f = (PropertyInfo.Range<Float>) info.getRange();
			min = f.getMin();
			max = f.getMax();
			incr = f.getIncr();
		}
		boolean cycle = false;
		boolean all = false;
		boolean snapToIncr = false;
		Type type = Vector4fControl.Type.VECTOR;

		switch (info.getHint()) {
		case ROTATION_DEGREES:
			cycle = true;
			max = 359;
			incr = prefs.getFloat(SceneConfig.BUILD_EULER_ROTATION_SNAP, SceneConfig.BUILD_EULER_ROTATION_SNAP_DEFAULT);
			if (incr == 0) {
				incr = 1;
			} else {
				snapToIncr = true;
			}
			break;
		case SCALE:
			max = 100;
			incr = prefs.getFloat(SceneConfig.BUILD_SCALE_SNAP, SceneConfig.BUILD_SCALE_SNAP_DEFAULT);
			if (incr == 0) {
				incr = 0.1f;
			} else {
				snapToIncr = true;
			}
			all = true;
			break;
		case WORLD_POSITION:
			incr = prefs.getFloat(SceneConfig.BUILD_LOCATION_SNAP, SceneConfig.BUILD_LOCATION_SNAP_DEFAULT);
			if (incr == 0) {
				incr = 1f;
			} else {
				snapToIncr = true;
			}
			break;
		case RGBA:
			type = Vector4fControl.Type.RGBA;
			break;
		}

		try {
			Vector4f val = (Vector4f) info.getGetter().invoke(prop);
			Vector4fControl v4 = new Vector4fControl(screen, min, max, incr, val, cycle, all);
			v4.onChange(evt -> {

				if (!isAdjusting()) {
					if (undoManager != null) {
						undoManager.storeAndExecute(new SetPropertyCommand(prop, info, evt.getNewValue().clone()) {
							@Override
							protected void updateUIValue(Object value) {
								v4.setValue((Vector4f) value);
								setAvailable();
							}
						});
					} else {
						try {
							setProperty(info, evt.getNewValue());
						} catch (Exception e) {
							LOG.log(Level.SEVERE, "Failed to set property.", e);
						}
					}
				}
			});
			v4.setType(type);
			propertyComponents.put(getComponentId(info), v4);
			v4.addToForm(propertyForm);
			if (snapToIncr) {
				v4.snapToIncr(true);
			}
			ElementStyle.normal(v4);
			addElement(v4, "growx, wrap");
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, "Failed to get value.", ex);
			addValueLabel(info);

		}
	}

	private void addQuaternionControl(final PropertyInfo<T> info) {
		float min = 0;
		float max = 1f;
		float incr = 0.01f;
		boolean cycle = false;
		boolean all = false;
		boolean snapToIncr = false;

		switch (info.getHint()) {
		case ROTATION_DEGREES:
			cycle = true;
			max = 359;
			incr = prefs.getFloat(SceneConfig.BUILD_EULER_ROTATION_SNAP, SceneConfig.BUILD_EULER_ROTATION_SNAP_DEFAULT);
			if (incr == 0) {
				incr = 1;
			} else {
				snapToIncr = true;
			}
			break;
		}

		try {
			final Quaternion val = (Quaternion) info.getGetter().invoke(prop);

			if (info.getHint() == Hint.ROTATION_DEGREES) {
				Vector3f rotVal = IceUI.toEulerDegrees(val, incr);
				Vector3fControl v3 = new Vector3fControl(screen, min, max, incr, rotVal, cycle, all);
				v3.onChange(evt -> {

					if (!isAdjusting()) {
						Quaternion newVal = IceUI.fromEulerDegrees(evt.getNewValue());
						if (undoManager != null) {
							undoManager.storeAndExecute(new SetPropertyCommand(prop, info, newVal) {
								@Override
								protected void updateUIValue(Object value) {
									float[] ang = ((Quaternion) value).toAngles(null);
									Vector3f rotVal = new Vector3f(ang[0] * FastMath.RAD_TO_DEG,
											ang[1] * FastMath.RAD_TO_DEG, ang[2] * FastMath.RAD_TO_DEG);
									v3.setValue(rotVal);
									setAvailable();
								}
							});
						} else {
							try {
								setProperty(info, newVal);
							} catch (Exception e) {
								LOG.log(Level.SEVERE, "Failed to set property.", e);
							}
						}
					}
				});
				propertyComponents.put(getComponentId(info), v3);
				v3.addToForm(propertyForm);
				if (snapToIncr) {
					v3.snapToIncr(true);
				}
				ElementStyle.normal(v3);
				addElement(v3, "growx, wrap");
			} else {

				QuaternionControl v4 = new QuaternionControl(screen, min, max, incr, val, cycle, all) {
					@Override
					protected void onChangeVector(Quaternion newValue) {
						if (!isAdjusting()) {
							if (undoManager != null) {
								undoManager.storeAndExecute(new SetPropertyCommand(prop, info, getValue().clone()) {
									@Override
									protected void updateUIValue(Object value) {
										setValue((Quaternion) value);
										setAvailable();
									}
								});
							} else {
								try {
									setProperty(info, newValue);
									setAvailable();
								} catch (Exception e) {
									LOG.log(Level.SEVERE, "Failed to set property.", e);
								}
							}
						}
					}
				};
				propertyComponents.put(getComponentId(info), v4);
				v4.addToForm(propertyForm);
				if (snapToIncr) {
					v4.snapToIncr(true);
				}
				ElementStyle.normal(v4);
				addElement(v4, "growx, wrap");
			}
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, "Failed to get value.", ex);
			addValueLabel(info);

		}
	}

	private void addVector3fControl(final PropertyInfo<T> info) {
		float min = 0;
		float max = 65536;
		float incr = 1f;
		int precision = -1;
		if (info.getRange() != null) {
			PropertyInfo.Range<Float> f = (PropertyInfo.Range<Float>) info.getRange();
			min = f.getMin();
			max = f.getMax();
			incr = f.getIncr();
			precision = f.getPrecision();
		}
		boolean cycle = false;
		boolean all = false;
		boolean snapToIncr = false;

		switch (info.getHint()) {
		case ROTATION_DEGREES:
			cycle = true;
			max = 359;
			incr = prefs.getFloat(SceneConfig.BUILD_EULER_ROTATION_SNAP, SceneConfig.BUILD_EULER_ROTATION_SNAP_DEFAULT);
			if (incr == 0) {
				incr = 1;
			} else {
				snapToIncr = true;
			}
			break;
		case SCALE:
			max = 100;
			incr = prefs.getFloat(SceneConfig.BUILD_SCALE_SNAP, SceneConfig.BUILD_SCALE_SNAP_DEFAULT);
			if (incr == 0) {
				incr = 0.1f;
			} else {
				snapToIncr = true;
			}
			all = true;
			break;
		case WORLD_POSITION:
			incr = prefs.getFloat(SceneConfig.BUILD_LOCATION_SNAP, SceneConfig.BUILD_LOCATION_SNAP_DEFAULT);
			if (incr == 0) {
				incr = 1f;
			} else {
				snapToIncr = true;
			}
			break;
		}

		try {
			Vector3f val = (Vector3f) info.getGetter().invoke(prop);
			Vector3fControl v3 = new Vector3fControl(screen, min, max, incr, val, cycle, all);
			v3.onChange(evt -> {

				if (!isAdjusting()) {
					if (undoManager != null) {
						undoManager.storeAndExecute(new SetPropertyCommand(prop, info, evt.getNewValue().clone()) {
							@Override
							protected void updateUIValue(Object value) {
								v3.setValue((Vector3f) value);
								setAvailable();
								propertyUpdate(info, value);
							}
						});
					} else {
						try {
							setProperty(info, evt.getNewValue());
							setAvailable();
							propertyUpdate(info, evt.getNewValue());
						} catch (Exception e) {
							LOG.log(Level.SEVERE, "Failed to set property.", e);
						}
					}
				}
			});
			if (precision != -1)
				v3.setPrecision(precision);
			propertyComponents.put(getComponentId(info), v3);
			v3.addToForm(propertyForm);
			if (snapToIncr) {
				v3.snapToIncr(false);
			}
			ElementStyle.normal(v3);
			addElement(v3, "growx, wrap");
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, "Failed to get value.", ex);
			addValueLabel(info);

		}
	}

	protected void propertyUpdate(PropertyInfo<T> info, Object value) {
	}

	private void addVector2fControl(final PropertyInfo info) {
		float min = 0;
		float max = 65536;
		float incr = 1f;
		boolean cycle = false;
		boolean all = false;
		boolean snapToIncr = false;

		switch (info.getHint()) {
		case ROTATION_DEGREES:
			cycle = true;
			max = 359;
			incr = prefs.getFloat(SceneConfig.BUILD_EULER_ROTATION_SNAP, SceneConfig.BUILD_EULER_ROTATION_SNAP_DEFAULT);
			if (incr == 0) {
				incr = 1;
			} else {
				snapToIncr = true;
			}
			break;
		case SCALE:
			max = 100;
			incr = prefs.getFloat(SceneConfig.BUILD_SCALE_SNAP, SceneConfig.BUILD_SCALE_SNAP_DEFAULT);
			if (incr == 0) {
				incr = 0.1f;
			} else {
				snapToIncr = true;
			}
			all = true;
			break;
		}

		try {
			Vector2f val = (Vector2f) info.getGetter().invoke(prop);
			Vector2fControl v2 = new Vector2fControl(screen, min, max, incr, val, cycle, all);
			v2.onChange(evt -> {
				if (!isAdjusting()) {
					if (undoManager != null) {
						undoManager.storeAndExecute(new SetPropertyCommand(prop, info, evt.getNewValue()) {
							@Override
							protected void updateUIValue(Object value) {
								v2.setValue((Vector2f) value);
								setAvailable();
							}
						});
					} else {
						try {
							setProperty(info, evt.getNewValue());
							setAvailable();
						} catch (Exception e) {
							LOG.log(Level.SEVERE, "Failed to set property.", e);
						}
					}
				}
			});
			propertyComponents.put(getComponentId(info), v2);
			v2.addToForm(propertyForm);
			if (snapToIncr) {
				v2.snapToIncr(false);
			}
			ElementStyle.normal(v2);
			addElement(v2, "growx, wrap");
		} catch (Exception ex) {
			LOG.log(Level.SEVERE, "Failed to get value.", ex);
			addValueLabel(info);

		}
	}

	public void rebuild() {
		runAdjusting(() -> {
			removeAllChildren();
			gatherPropProperties();
			propertyForm = new Form(screen);
			final List<PropertyInfo> sortedProperties = getSortedProperties();
			for (PropertyInfo k : sortedProperties) {
				buildPropertyRow(k);
			}
			layoutChildren();
			setAvailable();
		});
	}

	//
	@Override
	public void propertyChange(final PropertyChangeEvent evt) {
		if (isAdjusting()) {
			// Event was the result of a set from this property editor
			return;
		}

		runAdjusting(() -> {

			if (evt.getPropertyName().equals(AbstractProp.ATTR_SCENERY_ITEM)) {
				// || evt.getPropertyName().equals(AbstractProp.ATTR_ASSET))
				// {
				rebuild();
			} else {
				BaseElement el = propertyComponents.get(evt.getPropertyName());
				if (el instanceof CheckBox) {
					((CheckBox) el).runAdjusting(() -> ((CheckBox) el).setChecked((Boolean) evt.getNewValue()));
				} else if (el instanceof Vector4fControl) {
					((Vector4fControl) el).setValue((Vector4f) evt.getNewValue());
				} else if (el instanceof QuaternionControl) {
					((QuaternionControl) el).setValue((Quaternion) evt.getNewValue());
				} else if (el instanceof Vector3fControl) {
					if (evt.getNewValue() instanceof Quaternion) {
						((Vector3fControl) el).setValue(IceUI.toEulerDegrees((Quaternion) evt.getNewValue(),
								prefs.getFloat(SceneConfig.BUILD_EULER_ROTATION_SNAP,
										SceneConfig.BUILD_EULER_ROTATION_SNAP_DEFAULT)));
					} else {
						((Vector3fControl) el).setValue((Vector3f) evt.getNewValue());
					}
				} else if (el instanceof Vector2fControl) {
					((Vector2fControl) el).setValue((Vector2f) evt.getNewValue());
				} else if (el instanceof Spinner) {
					((Spinner) el).setSelectedValue(evt.getNewValue());
				} else if (el instanceof ColorFieldControl) {
					((ColorFieldControl) el).setValue((ColorRGBA) evt.getNewValue());
				} else if (el instanceof SoundFieldControl) {
					((SoundFieldControl) el).setValue((String) evt.getNewValue());
				} else if (el instanceof ComboBox) {
					((ComboBox) el).setSelectedByValue(evt.getNewValue());
				} else if (el instanceof TextField) {
					((TextField) el).setText(String.valueOf(evt.getNewValue()));
				} else if (el instanceof Label) {
					((Label) el).setText(String.valueOf(evt.getNewValue()));
				} else {
					LOG.warning(String.format("Unknown property update for %s (%s)", evt, el));
				}
				setAvailable();
			}
		});
	}

	abstract class SetPropertyCommand implements UndoableCommand {

		private static final long serialVersionUID = 1L;
		private final T object;
		private final PropertyInfo<T> info;
		private final Object value;
		private Object oldVal;

		public SetPropertyCommand(T object, PropertyInfo<T> info, Object value) {
			this.object = object;
			this.info = info;
			this.value = value;
		}

		public void undoCommand() {
			try {
				info.getSetter().invoke(object, oldVal);
				onPropertyChange(info, object, oldVal);
			} catch (Exception e) {
				LOG.log(Level.SEVERE, String.format("Failed to set old value %s on %s. %s", oldVal, object, info), e);
			}
			updateUIValue(oldVal);
		}

		public void doCommand() {
			try {
				Object ov = info.getGetter().invoke(object);
				if (ov == null) {
					oldVal = null;
				} else if (value instanceof Float) {
					oldVal = (Float) ov;
				} else if (value instanceof Integer) {
					oldVal = (Integer) ov;
				} else if (value instanceof Long) {
					oldVal = (Long) ov;
				} else if (value instanceof Boolean) {
					oldVal = (Boolean) ov;
				} else if (value instanceof Double) {
					oldVal = (Double) ov;
				} else if (value instanceof Character) {
					oldVal = (Character) ov;
				} else if (value instanceof String) {
					oldVal = (String) ov;
				} else if (value instanceof Cloneable) {
					oldVal = ov.getClass().getDeclaredMethod("clone").invoke(ov);
				} else {
					throw new RuntimeException(
							String.format("Don't know how to clone a %s (%s)", oldVal.getClass(), value));
				}
				setProperty(info, value);
				onPropertyChange(info, object, value);
			} catch (Exception e) {
				LOG.log(Level.SEVERE, String.format("Failed to set %s on %s. %s", value, object, info), e);
			}
			updateUIValue(value);
		}

		protected abstract void updateUIValue(Object value);
	}
}
