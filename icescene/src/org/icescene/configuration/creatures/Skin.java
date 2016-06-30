package org.icescene.configuration.creatures;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.icelib.ColourPalette;
import org.icelib.RGB;

public class Skin implements Serializable {

	private static final long serialVersionUID = 1L;
	private final String name;
	private RGB defaultColour;
	private boolean grays;
	private ColourPalette palette;
	private String title;
	private boolean primary;

	public Skin(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public boolean isPrimary() {
		return primary;
	}

	public RGB getDefaultColour() {
		return defaultColour;
	}

	public boolean isGrays() {
		return grays;
	}

	public void setDefaultColour(RGB defaultColour) {
		this.defaultColour = defaultColour;
	}

	public void setGrays(boolean grays) {
		this.grays = grays;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	public void setPalette(ColourPalette palette) {
		this.palette = palette;
	}

	public ColourPalette getPalette() {
		return palette;
	}

	public String getName() {
		return name;
	}

	public String getDisplayName() {
		return StringUtils.isBlank(title) ? name : title;
	}

	@Override
	public String toString() {
		return "Skin{" + "name=" + name + ", defaultColor=" + defaultColour + ", grays=" + grays + ", palette=" + palette
				+ ", title=" + title + ", primary=" + primary + '}';
	}
}