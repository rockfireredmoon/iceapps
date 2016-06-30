package org.icescene.io;

import java.io.Serializable;

import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;

public class KeyMapping implements Serializable {
	private static final long serialVersionUID = 1L;
	private Trigger trigger;
	private int modifiers;
	private String category = "Default";
	protected String mapping;

	public KeyMapping(String mapping) {
		this.mapping = mapping;
	}

	public String getMapping() {
		return mapping;
	}

	public Trigger getTrigger() {
		return trigger;
	}

	public int getModifiers() {
		return modifiers;
	}

	public void setModifiers(int modifiers) {
		this.modifiers = modifiers;
	}

	public String getCategory() {
		return category;
	}

	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setMapping(String mapping) {
		this.mapping = mapping;
	}

	public String toString() {
		if (trigger instanceof KeyTrigger) {
			return "Mapping [trigger=" + (trigger == null ? "null" : KeyNames.getKeyName(((KeyTrigger) trigger).getKeyCode()))
					+ ", modifiers=" + modifiers + ", category=" + category + ", mapping=" + mapping + "]";
		} else if (trigger instanceof KeyTrigger) {
			return "Mapping [trigger=" + (trigger == null ? "null" : "Button" + (((MouseButtonTrigger) trigger).getMouseButton()))
					+ ", modifiers=" + modifiers + ", category=" + category + ", mapping=" + mapping + "]";
		} else {
			return "Mapping [trigger=" + trigger + ", modifiers=" + modifiers + ", category=" + category + ", mapping=" + mapping
					+ "]";
		}
	}

}