package org.icescene.propertyediting;

import java.util.prefs.Preferences;

import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapFont.VAlign;

import icetone.core.BaseScreen;
import icetone.core.Size;
import icetone.core.layout.FillLayout;
import icetone.extras.windows.PersistentWindow;
import icetone.extras.windows.SaveType;

public class PropertiesWindow<T extends PropertyBean> extends PersistentWindow {

	private final PropertiesPanel<T> propertiesPanel;

	public PropertiesWindow(BaseScreen screen, String prefKey, Preferences prefs) {
		super(screen, prefKey, VAlign.Center, Align.Right, new Size(280, 700), false, SaveType.POSITION_AND_SIZE,
				prefs);
		content.setLayoutManager(new FillLayout());
		propertiesPanel = createPropertiesPanel(screen, prefs);
		content.addElement(propertiesPanel);
		setWindowTitle("Properties");
	}

	protected PropertiesPanel<T> createPropertiesPanel(BaseScreen screen, Preferences prefs) {
		return new PropertiesPanel<T>(screen, prefs);
	}

	@Override
	protected void onControlHideHook() {
		super.onControlHideHook();
		setObject(null);
	}

	@Override
	protected void onCloseWindow() {
		super.onCloseWindow();
		setObject(null);
	}

	public void setObject(T prop) {
		propertiesPanel.setObject(prop);
	}

	public void rebuild() {
		propertiesPanel.rebuild();
	}
}
