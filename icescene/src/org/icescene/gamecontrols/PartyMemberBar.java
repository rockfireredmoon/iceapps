package org.icescene.gamecontrols;

import com.google.common.base.Objects;

import icetone.controls.extras.Indicator;
import icetone.controls.extras.Indicator.BarMode;
import icetone.controls.extras.Indicator.DisplayMode;
import icetone.controls.text.Label;
import icetone.core.BaseScreen;
import icetone.core.Orientation;
import icetone.core.Element;
import icetone.core.layout.XYLayout;
import icetone.framework.animation.Interpolation;

/**
 */
public class PartyMemberBar extends Element {

	public enum Status {
		LEADER, MEMBER
	}

	private Indicator health;
	private Label displayName;
	private Status status;
	private Element statusElement;
	private BuffBar buffBar;

	public PartyMemberBar(final BaseScreen screen) {
		super(screen);
		setLayoutManager(new XYLayout());

		health = new Indicator(screen, Orientation.HORIZONTAL) {
			{
				setStyleClass("health-bar");
			}
		};
		health.setDisplayMode(DisplayMode.none);
		health.setBarMode(BarMode.resize);
		health.setInterpolation(Interpolation.bounce);
		health.setCurrentValue(50f);
		health.setAnimationTime(0.5f);

		displayName = new Label(screen) {
			{
				setStyleClass("display-name");
			}
		};

		Element background = new Element(screen) {
			{
				setStyleClass("background");
			}
		};

		statusElement = new Element(screen) {
			{
				setStyleClass("status");
			}
		};
		setStatus(Status.MEMBER);
		
		buffBar = new BuffBar(screen);

		addElement(background);
		addElement(health);
		addElement(displayName);
		addElement(buffBar);
		addElement(statusElement);
	}

	public BuffBar getBuffBar() {
		return buffBar;
	}

	public PartyMemberBar setStatus(Status status) {
		if (!Objects.equal(status, this.status)) {
			if (this.status != null)
				removeStyleClass("status-" + this.status.name().toLowerCase());
			this.status = status;
			if (this.status != null)
				addStyleClass("status-" + this.status.name().toLowerCase());
		}
		return this;
	}

	public PartyMemberBar setDisplayName(String displayName) {
		this.displayName.setText(displayName);
		return this;
	}

	public PartyMemberBar setHealth(float health) {
		this.health.setCurrentValue(health);
		return this;
	}

	public PartyMemberBar setHealthDisplayMode(DisplayMode displayMode) {
		health.setDisplayMode(displayMode);
		return this;
	}

	public PartyMemberBar setMaxHealth(float maxHealth) {
		health.setMaxValue(maxHealth);
		return this;
	}

	public float getMaxHealth() {
		return health.getMaxValue();
	}
}
