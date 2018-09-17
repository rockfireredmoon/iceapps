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

import org.icescene.HUDMessageAppState.Channel;
import org.iceui.controls.ElementStyle;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.system.AppSettings;

import icetone.controls.buttons.CheckBox;
import icetone.controls.buttons.PushButton;
import icetone.controls.lists.ComboBox;
import icetone.controls.text.Label;
import icetone.core.BaseScreen;
import icetone.core.ToolKit;
import icetone.core.layout.ScreenLayoutConstraints;
import icetone.core.layout.mig.MigLayout;
import icetone.extras.windows.DialogBox;
import icetone.extras.windows.PositionableFrame;

public class DisplaySettingsWindow extends PositionableFrame {
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

	public DisplaySettingsWindow(final BaseScreen screen, Vector2f position, String appSettingsPreferenceKey) {
		super(screen, null, position, null, true);
		this.appSettingsPreferenceKey = appSettingsPreferenceKey;
		setMovable(false);
		setResizable(false);
		content.setLayoutManager(new MigLayout(screen, "wrap 2", "[][fill, grow]", "[]"));
		setWindowTitle("Display");
		settings = ToolKit.get().getApplication().getContext().getSettings();

		// Resolution
		resolution = new ComboBox<Object>(screen);
		resolution.onMouseReleased(evt -> {
			if (!adjusting) {
				rebuildRatesAndDepths();
			}
		});
		((IcesceneApp) ToolKit.get().getApplication()).getContext().getSettings();
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

		content.addElement(new Label("Size", screen));
		content.addElement(resolution);

		// Refresh rate (determined from selected resolution)
		refreshRate = new ComboBox<Integer>(screen);

		content.addElement(new Label("Refresh Rate", screen));
		content.addElement(refreshRate);

		// Colour Depth (determined from selected resolution)
		colourDepth = new ComboBox<Integer>(screen);

		content.addElement(new Label("Colour Depth", screen));
		content.addElement(colourDepth);

		// Antialias (determined from selected resolution)
		antiAliasing = new ComboBox<Object>(screen);
		antiAliasing.addListItem("None", Boolean.FALSE);
		for (int i : new int[] { 2, 4, 6, 8, 16 }) {
			antiAliasing.addListItem(i + "x", i);
		}

		content.addElement(new Label("Anti-aliasing", screen));
		content.addElement(antiAliasing);

		// Vsync
		vsync = new CheckBox(screen);
		vsync.setChecked(settings.isVSync());
		vsync.setText("Vsync");
		content.addElement(vsync, "span 2, growx");

		// Save
		PushButton save = new PushButton(screen) {
			{
				setStyleClass("fancy");
			}
		};
		save.onMouseReleased(evt -> save());
		save.setText("Apply");
		save.setToolTipText("Apply the video settings");
		content.addElement(save, "gaptop 10, span 2, ax 50%");

		//
		rebuildRatesAndDepths();
		adjusting = false;
		sizeToContent();
		screen.showElement(this);
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
								refreshRate.setSelectedByValue(m.getFrequency());
							}
						}
						if (!b.contains(m.getFrequency())) {
							colourDepth.addListItem(m.getBitsPerPixel() + "bpp", m.getBitsPerPixel());
							b.add(m.getBitsPerPixel());
							if (settings.getBitsPerPixel() == m.getBitsPerPixel()) {
								refreshRate.setSelectedByValue(m.getFrequency());
							}
						}
					}
				}

				refreshRate.layoutChildren();
				colourDepth.layoutChildren();

				refreshRate.setEnabled(true);
				colourDepth.setEnabled(true);
				return;
			}

		}

		refreshRate.setEnabled(false);
		colourDepth.setEnabled(false);
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
		settings.setVSync(vsync.isChecked());

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
			ToolKit.get().getApplication().getStateManager().getState(HUDMessageAppState.class).message(Channel.ERROR,
					"Failed to save preferences.", ex);
			LOG.log(Level.SEVERE, "Failed to save preferences.", ex);
		}

		// Hide
		onSave();

		// Restart context now
		ToolKit.get().getApplication().restart();

		final DialogBox dialog = new DialogBox(screen, new Vector2f(15, 15), true) {
			{
				setStyleClass("large");
			}

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
		ElementStyle.warningColor(dialog.getDragBar());
		dialog.setWindowTitle("Restart");
		dialog.setButtonOkText("Use These Settings");
		final String fmt = "Do you want to keep these settings? You will be reverted to the previous settings automatically in %d seconds";
		dialog.setMsg(String.format(fmt, 10));
		dialog.setResizable(false);
		dialog.setMovable(false);
		dialog.sizeToContent();
		dialog.setModal(true);
		screen.showElement(dialog, ScreenLayoutConstraints.center);

		resetSettings = false;
		restartThread = new Thread("SettingsChangeTimer") {
			@Override
			public void run() {
				try {
					for (int i = 10; i > 0; i--) {
						final int fi = i;
						ToolKit.get().getApplication().enqueue(new Callable<Void>() {
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
					ToolKit.get().getApplication().enqueue(new Callable<Void>() {
						public Void call() throws Exception {
							dialog.hide();
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
						ToolKit.get().getApplication().enqueue(new Callable<Void>() {
							public Void call() throws Exception {
								ToolKit.get().getApplication().restart();
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