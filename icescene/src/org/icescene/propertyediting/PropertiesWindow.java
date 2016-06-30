package org.icescene.propertyediting;

import java.util.prefs.Preferences;

import org.iceui.HPosition;
import org.iceui.VPosition;
import org.iceui.controls.FancyPersistentWindow;
import org.iceui.controls.SaveType;

import com.jme3.math.Vector2f;

import icetone.core.ElementManager;
import icetone.core.layout.FillLayout;

public class PropertiesWindow<T extends PropertyBean> extends FancyPersistentWindow {

    private final PropertiesPanel<T> propertiesPanel;

    public PropertiesWindow(ElementManager screen, String prefKey, Preferences prefs) {
        super(screen, prefKey, screen.getStyle("Common").getInt("defaultWindowOffset"), VPosition.MIDDLE, HPosition.RIGHT, new Vector2f(280, 700), Size.SMALL, false, SaveType.POSITION_AND_SIZE, prefs);
        content.setLayoutManager(new FillLayout());
        propertiesPanel = createPropertiesPanel(screen, prefs);
        content.addChild(propertiesPanel);
        setWindowTitle("Properties");
    }

	protected PropertiesPanel<T> createPropertiesPanel(ElementManager screen, Preferences prefs) {
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
