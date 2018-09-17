package org.icescene.debug;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import org.icelib.QueueExecutor;
import org.icescene.IcemoonAppState;
import org.icescene.IcesceneApp;
import org.iceui.controls.ElementStyle;

import com.jme3.asset.AssetEventListener;
import com.jme3.asset.AssetKey;
import com.jme3.font.BitmapFont;
import com.jme3.font.LineWrapMode;
import com.jme3.math.ColorRGBA;

import icemoon.iceloader.ServerAssetManager;
import icetone.controls.containers.Panel;
import icetone.controls.extras.Indicator;
import icetone.controls.text.Label;
import icetone.core.BaseElement;
import icetone.core.Orientation;
import icetone.core.Size;
import icetone.core.StyledContainer;
import icetone.core.ZPriority;
import icetone.core.layout.Border;
import icetone.core.layout.BorderLayout;
import icetone.core.layout.mig.MigLayout;
import icetone.core.utils.Alarm;
import icetone.extras.controls.BusySpinner;

public class LoadScreenAppState extends IcemoonAppState<IcemoonAppState<?>>
		implements ServerAssetManager.DownloadingListener, AssetEventListener, QueueExecutor.Listener {

	private Alarm.AlarmTask hideTask;

	public enum AutoShow {

		NEVER, TASKS, DOWNLOADS, BOTH
	}

	private final static Logger LOG = Logger.getLogger(LoadScreenAppState.class.getName());

	/**
	 * Convenience method to show the load screen.
	 *
	 * @param stateManager
	 *            state manager
	 */
	public static void show(final IcesceneApp app) {
		show(app, true);
	}

	public static void show(final IcesceneApp app, final boolean sendMessage) {
		if (app.isSceneThread()) {
			LoadScreenAppState state = app.getStateManager().getState(LoadScreenAppState.class);
			state.show(sendMessage);
		} else {
			app.run(new Runnable() {
				public void run() {
					show(app, sendMessage);
				}
			});
		}
	}

	/**
	 * Convenience method to hide the load screen.
	 *
	 * @param stateManager
	 *            state manager
	 */
	public static void queueHide(final IcesceneApp app) {
		app.run(new Runnable() {
			public void run() {
				LoadScreenAppState state = app.getStateManager().getState(LoadScreenAppState.class);
				state.maybeHide();
			}
		});
	}

	private boolean showing;
	private BaseElement loadScreen;
	private Label loadText;
	private Indicator overallProgress;
	private Indicator fileProgress;
	private int maxRun;
	private final Map<String, Download> downloading = Collections.synchronizedMap(new HashMap<String, Download>());
	private float targetOverall;
	private boolean autoShowOnTasks = true;
	private boolean autoShowOnDownloads = false;

	public LoadScreenAppState(Preferences prefs) {
		super(prefs);
	}

	@Override
	protected void onCleanup() {
		hide();
		((ServerAssetManager) app.getAssetManager()).removeDownloadingListener(this);
	}

	@Override
	protected void postInitialize() {
		app.getAssetManager().addAssetEventListener(this);
		((ServerAssetManager) app.getAssetManager()).addDownloadingListener(this);
		app.getWorldLoaderExecutorService().addListener(this);
		if (showing) {
			showing = false;
			show();
		}
	}

	public boolean isAutoShowOnDownloads() {
		return autoShowOnDownloads;
	}

	public void setAutoShowOnDownloads(boolean autoShowOnDownloads) {
		this.autoShowOnDownloads = autoShowOnDownloads;
	}

	public boolean isAutoShowOnTasks() {
		return autoShowOnTasks;
	}

	public void setAutoShowOnTasks(boolean autoShow) {
		this.autoShowOnTasks = autoShow;
	}

	public void show() {
		show(true);
	}

	public void show(boolean sendMessage) {
		cancelHide();
		if (!showing) {
			LOG.info("Showing load screen");
			if (app != null) {

				loadScreen = new BaseElement(screen);
				loadScreen.setIgnoreGlobalAlpha(true);
				loadScreen.setGlobalAlpha(1);
				loadScreen.setLayoutManager(
						new MigLayout(screen, "fill, wrap 1", "push[400:600:800]push", "push[:140:]"));

				// Progress title bar
				StyledContainer progressTitle = new StyledContainer(screen);
				progressTitle.setLayoutManager(new BorderLayout());
				Label l = new Label("Loading", screen);
				ElementStyle.altColor(l);
				ElementStyle.medium(l);
				l.setTextAlign(BitmapFont.Align.Left);
				l.setTextWrap(LineWrapMode.Word);
				progressTitle.addElement(l, Border.WEST);
				final BusySpinner busySpinner = new BusySpinner(screen);
				busySpinner.setSpeed(BusySpinner.DEFAULT_SPINNER_SPEED);
				progressTitle.addElement(busySpinner, Border.EAST);

				// Progress window
				Panel progress = new Panel(screen);
				progress.setLayoutManager(new MigLayout(screen, "fill, wrap 1", "[]", "[][]"));
				progress.setResizable(false);
				progress.setMovable(false);
				progress.addElement(progressTitle, "growx, wrap");
				overallProgress = new Indicator(screen, Orientation.HORIZONTAL);
				overallProgress.setMaxValue(0);
				overallProgress.setCurrentValue(0);
				progress.addElement(overallProgress, "shrink 0, growx, wrap");
				loadScreen.addElement(progress, "growx");

				// Bottom progress
				StyledContainer bottom = new StyledContainer(screen);
				bottom.setLayoutManager(new BorderLayout());
				loadText = new Label("Busy", screen);
				loadText.setTextAlign(BitmapFont.Align.Left);
				ElementStyle.normal(loadText, false, false, true);
				bottom.addElement(loadText, Border.CENTER);
				fileProgress = new Indicator(screen, Orientation.HORIZONTAL);
				fileProgress.setIndicatorColor(ColorRGBA.Green);
				fileProgress.setPreferredDimensions(new Size(150, 20));
				fileProgress.setMaxValue(100);
				fileProgress.setCurrentValue(0);
				bottom.addElement(fileProgress, Border.EAST);
				progress.addElement(bottom, "growx");

				app.getLayers(ZPriority.FOREGROUND).addElement(loadScreen);

			}
			showing = true;
		}
	}

	public void hide() {
		if (showing) {
			LOG.info("Hiding load screen");
			if (app != null) {
				app.getLayers(ZPriority.FOREGROUND).removeElement(loadScreen);
			}
			overallProgress.setMaxValue(0);
			loadScreen.setElementParent(null);
			showing = false;
		}
	}

	public void downloadStarting(final AssetKey key, final long size) {
		app.enqueue(new Callable<Void>() {
			public Void call() throws Exception {
				if (autoShowOnDownloads && !showing) {
					show();
				}

				LOG.info(String.format("Download of %s started for size of %d", key, size));
				downloading.put(key.getName(), new Download(key, size));
				if (loadText != null) {
					String n = key.toString();
					int idx = n.lastIndexOf("/");
					if (idx != -1) {
						n = n.substring(idx + 1);
					}
					loadText.setText(n);
				}
				setProgressValues();
				return null;
			}
		});

	}

	public void downloadProgress(final AssetKey key, final long progress) {
		app.enqueue(new Callable<Void>() {
			public Void call() throws Exception {
				final Download d = downloading.get(key.getName());
				d.progress = progress;
				setProgressValues();
				return null;
			}
		});
	}

	public void downloadComplete(final AssetKey key) {
		app.enqueue(new Callable<Void>() {
			public Void call() throws Exception {
				LOG.info(String.format("Download of %s complete", key));
				downloading.remove(key.getName());
				LOG.info(String.format("downloadComplete(%s, %d, %d)", showing, downloading.size(),
						app.getWorldLoaderExecutorService().getTotal()));
				if ((autoShowOnDownloads || autoShowOnTasks) && showing && downloading.isEmpty()
						&& app.getWorldLoaderExecutorService().getTotal() == 0) {
					maybeHide();
				}
				setProgressValues();
				return null;
			}
		});
	}

	public void assetLoaded(final AssetKey key) {
		app.enqueue(new Callable<Void>() {
			public Void call() throws Exception {
				if (loadText != null) {
					loadText.setText(key.toString());
				}
				return null;
			}
		});
	}

	public void assetRequested(AssetKey key) {
	}

	public void assetDependencyNotFound(AssetKey parentKey, AssetKey dependentAssetKey) {
	}

	public void submitted(final QueueExecutor queue, final Runnable r) {
		final int total = queue.getTotal();
		app.enqueue(new Callable<Void>() {
			public Void call() throws Exception {
				if (autoShowOnTasks && !showing) {
					show();
				}

				if (maxRun == 0) {
					overallProgress.setCurrentValue(0);
					maxRun = total;
					targetOverall = 0f;
				} else {
					if (total > maxRun) {
						maxRun = total;
					}
				}
				overallProgress.setIndicatorText(r.toString());
				overallProgress.setMaxValue(maxRun);
				return null;
			}
		});
	}

	public void executed(QueueExecutor queue, Runnable r) {
		final int total = queue.getTotal();
		app.enqueue(new Callable<Void>() {
			public Void call() throws Exception {
				targetOverall = maxRun - total;
				if (total == 0) {
					maxRun = 0;
					if ((autoShowOnDownloads || autoShowOnTasks) && showing && downloading.isEmpty()) {
						maybeHide();
					}
				}
				return null;
			}
		});
	}

	@Override
	public void update(float tpf) {
		super.update(tpf);
		if (showing && targetOverall != -1) {
			updateOverall();
		}
	}

	private void updateOverall() {
		overallProgress.setCurrentValue(targetOverall);
		targetOverall = -1;
	}

	private void cancelHide() {
		if (hideTask != null) {
			hideTask.cancel();
		}
	}

	private void maybeHide() {
		cancelHide();
		hideTask = app.getAlarm().timed(new Callable<Void>() {
			public Void call() throws Exception {
				if (overallProgress.getMaxValue() > 0
						&& overallProgress.getCurrentValue() < overallProgress.getMaxValue()) {
					maybeHide();
					return null;
				} else {
					try {
						hide();
						return null;
					} finally {
						hideTask = null;
					}
				}
			}
		}, 1);
	}

	private void setProgressValues() {
		long total = 0;
		long progress = 0;
		for (Map.Entry<String, Download> en : downloading.entrySet()) {
			total += en.getValue().size;
			progress += en.getValue().progress;
		}
		fileProgress.setMaxValue(total);
		fileProgress.setCurrentValue(progress);
	}

	class Download {

		AssetKey<?> key;
		long size;
		long progress;

		public Download(AssetKey<?> key, long size) {
			this.key = key;
			this.size = size;
		}
	}

	@Override
	public void assetSupplied(AssetKey key) {
	}
}