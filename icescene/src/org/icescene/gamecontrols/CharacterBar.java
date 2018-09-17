package org.icescene.gamecontrols;

import icetone.controls.extras.Meter;
import icetone.core.BaseScreen;
import icetone.core.Orientation;

/**
 */
public class CharacterBar extends AbstractCharacterBar {

	private Meter will;
	private Meter might;

	public CharacterBar(final BaseScreen screen) {
		super(screen);

		will = new Meter(screen, Orientation.HORIZONTAL) {
			{
				setStyleClass("will-meter");
			}
		};
		will.setMaxValue(10);
		might = new Meter(screen, Orientation.HORIZONTAL) {
			{
				setStyleClass("might-meter");
			}
		};
		might.setMaxValue(10);

		addElement(might);
		addElement(will);
	}

	public CharacterBar setWill(int will) {
		this.will.setCurrentValue(will);
		return this;
	}

	public CharacterBar setMight(int might) {
		this.might.setCurrentValue(might);
		return this;
	}

}
