package org.icescene.io;

import java.util.prefs.Preferences;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;

public class UserKeyMapping {
	final static Log LOG = LogFactory.getLog(UserKeyMapping.class);

	private Preferences node;
	private KeyMapping source;
	private int modifiers;
	private Trigger trigger;

	public UserKeyMapping() {
		super();
	}

	public UserKeyMapping(KeyMapping source, Preferences node) {
		super();
		this.source = source;
		this.node = node;
		this.trigger = source.getTrigger();
		this.modifiers = source.getModifiers();

		String userConfiguredTrigger = node.get(source.getMapping(), "");
		if (StringUtils.isNotBlank(userConfiguredTrigger)) {
			try {
				parseTrigger(userConfiguredTrigger);
			} catch (IllegalArgumentException iae) {
				LOG.warn("Could not parse user defined key.", iae);
			}
		}
	}

	public void update() {
		node.put(source.getMapping(), getDescription());
	}

	public KeyMapping getSource() {
		return source;
	}

	public void reset() {
		node.remove(source.getMapping());
	}

	public int getModifiers() {
		return modifiers;
	}

	public void setModifiers(int modifiers) {
		this.modifiers = modifiers;
	}

	public Trigger getTrigger() {
		return trigger;
	}

	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
	}

	public void parseTrigger(String text) {
		Trigger sourceTrigger = source.getTrigger();
		if (text == null) {
			int modifiers = source.getModifiers();
			if (sourceTrigger instanceof KeyTrigger)
				trigger = new KeyTrigger(((KeyTrigger) sourceTrigger).getKeyCode());
			else if (sourceTrigger instanceof MouseButtonTrigger)
				trigger = new MouseButtonTrigger(((MouseButtonTrigger) sourceTrigger).getMouseButton());
			this.modifiers = modifiers;
		} else {
			String[] a = text.split("\\s+");
			int modifiers = 0;
			for (String s : a) {
				s = s.trim();
				if (s.equals("LCTRL")) {
					modifiers = modifiers | ModifierKeysAppState.L_CTRL_MASK;
				} else if (s.equals("RCTRL")) {
					modifiers = modifiers | ModifierKeysAppState.R_CTRL_MASK;
				} else if (s.equals("LALT")) {
					modifiers = modifiers | ModifierKeysAppState.L_ALT_MASK;
				} else if (s.equals("RALT")) {
					modifiers = modifiers | ModifierKeysAppState.R_ALT_MASK;
				} else if (s.equals("LSHIFT")) {
					modifiers = modifiers | ModifierKeysAppState.L_SHIFT_MASK;
				} else if (s.equals("RSHIFT")) {
					modifiers = modifiers | ModifierKeysAppState.R_SHIFT_MASK;
				} else if (s.equals("LMETA")) {
					modifiers = modifiers | ModifierKeysAppState.L_META_MASK;
				} else if (s.equals("RSHIFT")) {
					modifiers = modifiers | ModifierKeysAppState.R_META_MASK;
				} else {
					trigger = new KeyTrigger(KeyNames.getKeyCode(s));
				}
			}
			this.modifiers = modifiers;
		}
	}

	public String getDescription() {
		if (trigger instanceof KeyTrigger) {
			return getKeyDescription(modifiers, ((KeyTrigger) trigger).getKeyCode());
		} else if (trigger instanceof MouseButtonTrigger) {
			return getMouseButtonDescription(modifiers, ((MouseButtonTrigger) trigger).getMouseButton());
		} else {
			throw new UnsupportedOperationException();
		}
	}

	public static String getKeyDescription(int modifiers, int keycode) {
		StringBuilder bui = new StringBuilder();
		appendModifiers(modifiers, bui);
		if (keycode != -1) {
			bui.append(KeyNames.getKeyName(keycode));
		}
		return bui.toString().trim();
	}

	public static String getMouseButtonDescription(int modifiers, int button) {
		StringBuilder bui = new StringBuilder();
		appendModifiers(modifiers, bui);
		if (button != -1) {
			bui.append("BUTTON" + button);
		}
		return bui.toString().trim();
	}

	protected static void appendModifiers(int modifiers, StringBuilder bui) {
		if ((modifiers & ModifierKeysAppState.L_CTRL_MASK) != 0) {
			bui.append("LCTRL ");
		}
		if ((modifiers & ModifierKeysAppState.R_CTRL_MASK) != 0) {
			bui.append("RCTRL ");
		}
		if ((modifiers & ModifierKeysAppState.L_SHIFT_MASK) != 0) {
			bui.append("LSHIFT ");
		}
		if ((modifiers & ModifierKeysAppState.R_SHIFT_MASK) != 0) {
			bui.append("RSHIFT ");
		}
		if ((modifiers & ModifierKeysAppState.L_ALT_MASK) != 0) {
			bui.append("LALT ");
		}
		if ((modifiers & ModifierKeysAppState.R_ALT_MASK) != 0) {
			bui.append("RALT ");
		}

		if ((modifiers & ModifierKeysAppState.L_META_MASK) != 0) {
			bui.append("LMETA ");
		}

		if ((modifiers & ModifierKeysAppState.R_ALT_MASK) != 0) {
			bui.append("RMETA ");
		}
	}

	@Override
	public String toString() {
		return "UserKeyMapping [node=" + node + ", source=" + source + ", modifiers=" + modifiers + ", trigger=" + trigger + "]";
	}

}
