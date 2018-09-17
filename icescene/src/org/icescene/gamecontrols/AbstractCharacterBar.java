package org.icescene.gamecontrols;

import org.icelib.CreatureCategory;

import icetone.controls.extras.Indicator;
import icetone.controls.extras.Indicator.BarMode;
import icetone.controls.extras.Indicator.DisplayMode;
import icetone.controls.extras.Meter;
import icetone.controls.text.Label;
import icetone.core.BaseScreen;
import icetone.core.Element;
import icetone.core.Orientation;
import icetone.core.layout.XYLayout;
import icetone.framework.animation.Interpolation;

/**
 */
public class AbstractCharacterBar extends Element {

	private Indicator health;
	private Label level;
	private Label displayName;
	private Meter willCharge;
	private Meter mightCharge;
	private Element category;
	private CreatureCategory creatureCategory;

	public AbstractCharacterBar(final BaseScreen screen) {
		super(screen);
		setMovable(true);
		setLockToParentBounds(true);
		setLayoutManager(new XYLayout());

		health = new Indicator(screen, Orientation.HORIZONTAL) {
			{
				setStyleClass("health-bar");
			}

			@Override
			protected void refactorText() {
				getOverlayElement().setText(getHealthText());
			}
		};
		health.setDisplayMode(DisplayMode.none);
		health.setBarMode(BarMode.resize);
		health.setInterpolation(Interpolation.bounce);
		health.setCurrentValue(50f);
		health.setAnimationTime(0.5f);

		level = new Label(screen) {
			{
				setStyleClass("level");
			}
		};

		displayName = new Label(screen) {
			{
				setStyleClass("display-name");
			}
		};

		willCharge = new Meter(screen, Orientation.VERTICAL) {
			{
				setStyleClass("will-charge-meter");
			}
		};
		willCharge.setMaxValue(5);
		mightCharge = new Meter(screen, Orientation.VERTICAL) {
			{
				setStyleClass("might-charge-meter");
			}
		};
		mightCharge.setMaxValue(5);

		category = new Element(screen) {
			{
				setStyleClass("creature-category ");
			}
		};
		setCreatureCategory(CreatureCategory.PLAYER);

		Element background = new Element(screen) {
			{
				setStyleClass("background");
			}
		};

		addElement(background);
		addElement(category);
		addElement(level);
		addElement(health);
		addElement(displayName);
		addElement(willCharge);
		addElement(mightCharge);
	}

	public CreatureCategory getCreatureCategory() {
		return creatureCategory;
	}

	public void setCreatureCategory(CreatureCategory creatureCategory) {
		this.creatureCategory = creatureCategory;
		category.setTexture(String.format("Icons/%s", creatureCategory.getIcon()));
	}

	public AbstractCharacterBar setDisplayName(String displayName) {
		this.displayName.setText(displayName);
		return this;
	}

	public AbstractCharacterBar setLevel(int level) {
		this.level.setText(String.format("%d", level));
		return this;
	}

	public AbstractCharacterBar setHealth(float health) {
		this.health.setCurrentValue(health);
		return this;
	}

	public AbstractCharacterBar setWillCharge(int will) {
		this.willCharge.setCurrentValue(will);
		return this;
	}

	public AbstractCharacterBar setMightCharge(int might) {
		this.mightCharge.setCurrentValue(might);
		return this;
	}

	public AbstractCharacterBar setHealthDisplayMode(DisplayMode displayMode) {
		health.setDisplayMode(displayMode);
		return this;
	}

	public AbstractCharacterBar setMaxHealth(float maxHealth) {
		health.setMaxValue(maxHealth);
		return this;
	}

	protected String getHealthText() {
		return String.format("%d/%d", (long) health.getCurrentValue(), (long) health.getMaxValue());
	}

	public float getMaxHealth() {
		return health.getMaxValue();
	}
}
