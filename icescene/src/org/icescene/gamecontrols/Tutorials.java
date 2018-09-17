package org.icescene.gamecontrols;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import icetone.controls.buttons.Button;
import icetone.core.BaseScreen;
import icetone.core.Orientation;
import icetone.core.Element;
import icetone.core.event.MouseUIButtonEvent;
import icetone.core.layout.WrappingLayout;

public class Tutorials extends Element {
	private Preferences prefs;
	private Preferences triggeredNode;
	private Preferences readNode;

	public Tutorials(BaseScreen screen, Preferences prefs) {
		super(screen);
		this.prefs = prefs;

		triggeredNode = prefs.node("triggered");
		readNode = prefs.node("read");

		setLayoutManager(new WrappingLayout().setOrientation(Orientation.HORIZONTAL).setWidth(1));
		try {
			for (String triggered : triggeredNode.keys()) {
				if (readNode.getLong(triggered, -1) == -1) {
					addTutorialButton(triggered);
				}
			}
		} catch (BackingStoreException bse) {
			throw new IllegalStateException("Failed to get tutorial triggers.", bse);
		}
	}

	protected void addTutorialButton(String triggered) {
		addElement(new Button(screen) {
			{
				setStyleId("tutorial-" + triggered);
				setStyleClass("tutorial-button");
			}
		}.setDestroyOnHide(true).onMousePressed(evt -> {
			evt.getElement().hide();

		}, MouseUIButtonEvent.LEFT));
	}
}
