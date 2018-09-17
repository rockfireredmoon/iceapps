package org.icescene.gamecontrols;

import icetone.core.BaseScreen;

/**
 */
public class TargetBar extends AbstractCharacterBar {
	
	private BuffBar buffBar;

	public TargetBar(final BaseScreen screen) {
		super(screen);
		buffBar = new BuffBar(screen);
		addElement(buffBar);
	}
	public BuffBar getBuffBar() {
		return buffBar;
	}
}
