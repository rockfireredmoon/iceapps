package org.icescene.tools;

import java.util.prefs.Preferences;

import icetone.core.BaseScreen;


public interface HudType {
    Preferences preferenceNode();
    String name();
    
    AbstractToolArea createToolArea(ToolManager toolManager, BaseScreen screen);
}
