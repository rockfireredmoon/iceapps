package org.icescene;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;

import org.iceui.UIConstants;
import org.iceui.controls.FancyButton;
import org.iceui.controls.FancyDialogBox;
import org.iceui.controls.FancyPositionableWindow;
import org.iceui.controls.FancyWindow;
import org.iceui.controls.UIUtil;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.system.AppSettings;

import icetone.controls.buttons.CheckBox;
import icetone.controls.lists.ComboBox;
import icetone.controls.text.Label;
import icetone.core.ElementManager;
import icetone.core.layout.LUtil;
import icetone.core.layout.mig.MigLayout;
import icetone.core.utils.UIDUtil;
import icetone.effects.Effect;

public class DisplaySettingsWindow extends FancyPositionableWindow {
    private static final Logger LOG = Logger.getLogger(DisplaySettingsWindow.class.getName());

    private boolean adjusting = true;
    private final ComboBox<Object> resolution;
    private final ComboBox<Integer> refreshRate;
    private final ComboBox<Integer> colourDepth;
    private DisplayMode[] availableDisplayModes;
    private final ComboBox<Object> antiAliasing;
    private final CheckBox vsync;
    private final AppSettings settings;
    private Thread restartThread;
    private boolean resetSettings;
    private final String appSettingsPreferenceKey;

    public DisplaySettingsWindow(final ElementManager screen, Vector2f position, String appSettingsPreferenceKey) {
        super(screen, UIDUtil.getUID(), position, LUtil.LAYOUT_SIZE, FancyWindow.Size.SMALL, true);
        this.appSettingsPreferenceKey = appSettingsPreferenceKey;
        setIsMovable(false);
        setIsResizable(false);
        content.setLayoutManager(new MigLayout(screen, "wrap 2", "[][fill, grow]", "[]"));
        setWindowTitle("Display");
        settings = app.getContext().getSettings();

        // Resolution
        resolution = new ComboBox<Object>(screen) {
            @Override
            public void onChange(int selectedIndex, Object value) {
                if (!adjusting) {
                    rebuildRatesAndDepths();
                }
            }
        };
        ((IcesceneApp) app).getContext().getSettings();
        resolution.addListItem("Resizable Window", Boolean.FALSE);
        resolution.addListItem("Full Screen", Boolean.TRUE);
        Set<String> s = new HashSet<String>();
        int selectedResIndex = 0;
        try {
            availableDisplayModes = Display.getAvailableDisplayModes();
            List<DisplayMode> m = new ArrayList<DisplayMode>(Arrays.asList(availableDisplayModes));
            Collections.sort(m, new Comparator<DisplayMode>() {
                public int compare(DisplayMode o1, DisplayMode o2) {
                    int co1 = new Integer(o1.getWidth()).compareTo(o2.getWidth()) * -1;
                    return co1 == 0 ? new Integer(o1.getHeight()).compareTo(o2.getHeight()) * -1 : co1;
                }
            });
            int idx = 2;
            for (DisplayMode dm : m) {
                final String res = dm.getWidth() + "x" + dm.getHeight();
                if (!s.contains(res)) {
                    resolution.addListItem(res, dm);
                    if (dm.getWidth() == settings.getWidth() && dm.getHeight() == settings.getHeight()) {
                        selectedResIndex = idx;
                    }
                    s.add(res);
                    idx++;
                }
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Could not determine display modes.", e);
        }
        if (settings.getBoolean(SceneConstants.APPSETTINGS_RESIZABLE)) {
            resolution.setSelectedIndex(0);
        } else if (settings.getWidth() <= 0 || settings.getHeight() <= 0) {
            resolution.setSelectedIndex(1);
        } else {
            resolution.setSelectedIndex(selectedResIndex);
        }

        content.addChild(new Label("Size", screen));
        content.addChild(resolution);

        // Refresh rate (determined from selected resolution)
        refreshRate = new ComboBox<Integer>(screen);

        content.addChild(new Label("Refresh Rate", screen));
        content.addChild(refreshRate);

        // Colour Depth (determined from selected resolution)
        colourDepth = new ComboBox<Integer>(screen);

        content.addChild(new Label("Colour Depth", screen));
        content.addChild(colourDepth);

        // Antialias (determined from selected resolution)
        antiAliasing = new ComboBox<Object>(screen);
        antiAliasing.addListItem("None", Boolean.FALSE);
        for (int i : new int[]{2, 4, 6, 8, 16}) {
            antiAliasing.addListItem(i + "x", i);
        }

        content.addChild(new Label("Anti-aliasing", screen));
        content.addChild(antiAliasing);

        // Vsync
        vsync = new CheckBox(screen);
        vsync.setIsCheckedNoCallback(settings.isVSync());
        vsync.setLabelText("Vsync");
        content.addChild(vsync, "span 2, growx");

        // Save
        FancyButton save = new FancyButton(screen) {
            @Override
            public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                super.onButtonMouseLeftUp(evt, toggled);
                save();

            }
        };
        save.setText("Apply");
        save.setToolTipText("Apply the video settings");
        content.addChild(save, "gaptop 10, span 2, ax 50%");


        // Show effect
        Effect slideIn = new Effect(Effect.EffectType.SlideIn,
                Effect.EffectEvent.Show, UIConstants.UI_EFFECT_TIME);
        slideIn.setEffectDirection(Effect.EffectDirection.Top);
        addEffect(Effect.EffectEvent.Show, slideIn);

        // Hide effect        
        Effect slideOut = new Effect(Effect.EffectType.SlideOut,
                Effect.EffectEvent.Hide, UIConstants.UI_EFFECT_TIME);
        slideOut.setEffectDirection(Effect.EffectDirection.Top);
        addEffect(Effect.EffectEvent.Hide, slideOut);

        // 
        rebuildRatesAndDepths();
        adjusting = false;
        sizeToContent();
        screen.addElement(this, null, true);
    }

    private void rebuildRatesAndDepths() {
        refreshRate.removeAllListItems();
        colourDepth.removeAllListItems();

        if (availableDisplayModes != null) {
            Object sel = resolution.getSelectedListItem().getValue();
            if (!(sel instanceof Boolean)) {
                DisplayMode selMode = (DisplayMode) sel;
                Set<Integer> r = new HashSet<Integer>();
                Set<Integer> b = new HashSet<Integer>();
                for (DisplayMode m : availableDisplayModes) {
                    if (m.getWidth() == selMode.getWidth() && m.getHeight() == selMode.getHeight()) {
                        if (!r.contains(m.getFrequency())) {
                            refreshRate.addListItem(m.getFrequency() + "Hz", m.getFrequency());
                            r.add(m.getFrequency());
                            if (settings.getFrequency() == m.getFrequency()) {
                                refreshRate.setSelectedByValue(m.getFrequency(), false);
                            }
                        }
                        if (!b.contains(m.getFrequency())) {
                            colourDepth.addListItem(m.getBitsPerPixel() + "bpp", m.getBitsPerPixel());
                            b.add(m.getBitsPerPixel());
                            if (settings.getBitsPerPixel() == m.getBitsPerPixel()) {
                                refreshRate.setSelectedByValue(m.getFrequency(), false);
                            }
                        }
                    }
                }

                refreshRate.setIsEnabled(true);
                colourDepth.setIsEnabled(true);
                return;
            }

        }

        refreshRate.setIsEnabled(false);
        colourDepth.setIsEnabled(false);
        // Rates

    }
    
    protected void onSave() {
        
    }

    private void save() {

        resetSettings = false;

        final boolean oldFs = settings.isFullscreen();
        final int oldW = settings.getWidth();
        final int oldH = settings.getHeight();
        final int oldR = settings.getFrequency();
        final int oldC = settings.getBitsPerPixel();
        final boolean oldRS = settings.getBoolean(SceneConstants.APPSETTINGS_RESIZABLE);

        // Options
        settings.setVSync(vsync.getIsChecked());

        // Resolution, refresh and depth
        final Object selRes = resolution.getSelectedListItem().getValue();
        if (selRes.equals(Boolean.FALSE)) {
            settings.putBoolean(SceneConstants.APPSETTINGS_RESIZABLE, true);
            settings.setFullscreen(false);
            settings.setWidth(Display.getWidth());
            settings.setHeight(Display.getHeight());
        } else {
            settings.putBoolean(SceneConstants.APPSETTINGS_RESIZABLE, false);
            settings.setFullscreen(true);
            if (!selRes.equals(Boolean.TRUE)) {
                DisplayMode selMode = (DisplayMode) selRes;
                settings.setWidth(selMode.getWidth());
                settings.setHeight(selMode.getHeight());
                settings.setBitsPerPixel((Integer) colourDepth.getSelectedListItem().getValue());
                settings.setFrequency((Integer) refreshRate.getSelectedListItem().getValue());
            } else {
                settings.setWidth(0);
                settings.setHeight(0);
            }
        }

        // Save
        try {
            settings.save(appSettingsPreferenceKey);
        } catch (BackingStoreException ex) {
            app.getStateManager().getState(HUDMessageAppState.class).message(Level.SEVERE, "Failed to save preferences.", ex);
            LOG.log(Level.SEVERE, "Failed to save preferences.", ex);
        }

        // Hide
        onSave();

        // Restart context now
        app.restart();

        final FancyDialogBox dialog = new FancyDialogBox(screen, new Vector2f(15, 15), FancyWindow.Size.LARGE, true) {
            @Override
            public void onButtonCancelPressed(MouseButtonEvent evt, boolean toggled) {
                if (restartThread != null) {
                    resetSettings = true;
                    restartThread.interrupt();
                }
            }

            @Override
            public void onButtonOkPressed(MouseButtonEvent evt, boolean toggled) {
                if (restartThread != null) {
                    restartThread.interrupt();
                }
            }
        };
        dialog.setDestroyOnHide(true);
        dialog.getDragBar().setFontColor(screen.getStyle("Common").getColorRGBA("warningColor"));
        dialog.setWindowTitle("Restart");
        dialog.setButtonOkText("Use These Settings");
        final String fmt = "Do you want to keep these settings? You will be reverted to the previous settings automatically in %d seconds";
        dialog.setMsg(String.format(fmt, 10));
        dialog.setIsResizable(false);
        dialog.setIsMovable(false);
        dialog.sizeToContent();
        UIUtil.center(screen, dialog);
		screen.addElement(dialog, null, true);
        dialog.showAsModal(true);

        resetSettings = false;
        restartThread = new Thread("SettingsChangeTimer") {
            @Override
            public void run() {
                try {
                    for (int i = 10; i > 0; i--) {
                        final int fi = i;
                        app.enqueue(new Callable<Void>() {
                            public Void call() throws Exception {
                                dialog.setMsg(String.format(fmt, fi));
                                return null;
                            }
                        });
                        Thread.sleep(1000);
                    }
                    resetSettings = true;
                } catch (InterruptedException ie) {
                } finally {
                    app.enqueue(new Callable<Void>() {
                        public Void call() throws Exception {
                            dialog.hideWithEffect();
                            return null;
                        }
                    });
                    if (resetSettings) {
                        LOG.info("Resetting to previous video settings");
                        settings.setFullscreen(oldFs);
                        settings.setBitsPerPixel(oldC);
                        settings.setFrequency(oldR);
                        settings.setWidth(oldW);
                        settings.setHeight(oldH);
                        settings.putBoolean(SceneConstants.APPSETTINGS_RESIZABLE, oldRS);
                        app.enqueue(new Callable<Void>() {
                            public Void call() throws Exception {
                                app.restart();
                                return null;
                            }
                        });
                    }
                    restartThread = null;
                }
            }
        };
        restartThread.start();
    }
}