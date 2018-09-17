package org.icescene;

import java.util.prefs.Preferences;

import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapFont.VAlign;

import icetone.controls.buttons.Button;
import icetone.controls.text.Label;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Size;
import icetone.core.Element;
import icetone.core.layout.FillLayout;
import icetone.core.layout.RadialLayout;
import icetone.extras.windows.PersistentWindow;
import icetone.extras.windows.SaveType;

public abstract class MiniMapWindow extends PersistentWindow {

	public MiniMapWindow(BaseScreen screen, Preferences prefs) {
		super(screen, null, 0, VAlign.Top, Align.Right, null, false, SaveType.POSITION, prefs);
		
		setStyleClass("minimap");

		Element buttons = new Element(screen, new RadialLayout()).setStyleClass("map-buttons");
		createDirectionButton(buttons, "north", "N");
		createDirectionButton(buttons, "south", "S");
		createDirectionButton(buttons, "east", "E");
		createDirectionButton(buttons, "west", "W");

		// Zoon out
		Button home = new Button(screen);
		home.setStyleClass("home");
		home.onMouseReleased(evt -> onHome());
		home.setToolTipText("Zoom Out");
		buttons.addElement(home);

		// Zoon in
		Button zoomIn = new Button(screen);
		zoomIn.setStyleClass("zoom-in");
		zoomIn.onMouseReleased(evt -> onZoomIn());
		zoomIn.setToolTipText("Zoom In");
		buttons.addElement(zoomIn);

		// Zoon out
		Button zoomOut = new Button(screen);
		zoomOut.setStyleClass("zoom-out");
		zoomOut.onMouseReleased(evt -> onZoomOut());
		zoomOut.setToolTipText("Zoom Out");
		buttons.addElement(zoomOut);

		// Open world map
		Button openWorldMap = new Button(screen);
		openWorldMap.setStyleClass("world-map");
		openWorldMap.onMouseReleased(evt -> onOpenWorldMap());
		openWorldMap.setToolTipText("Show World Map");
		buttons.addElement(openWorldMap);

		setWindowTitle("Mini Map");
		setMovable(true);
		setResizable(false);
		getContentArea().setLayoutManager(new FillLayout());
		getContentArea().addElement(new Element(screen).setStyleClass("overlay"));
		getContentArea().addElement(buttons);
	}

	protected abstract void onOpenWorldMap();

	protected abstract void onZoomIn();

	protected abstract void onHome();

	protected abstract void onZoomOut();

	protected void createDirectionButton(BaseElement container, String positionStyle, String text) {
		Label b = new Label(text, screen);
		b.setStyleClass("icon compass-point " + positionStyle);
		container.addElement(b);
	}
}
