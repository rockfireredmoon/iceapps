package org.icescene.audio;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.Preferences;

import org.apache.commons.io.IOUtils;
import org.icelib.QueueExecutor;
import org.icescene.IcemoonAppState;
import org.icescene.SceneConfig;
import org.icescene.configuration.AudioConfiguration;
import org.icescene.configuration.AudioConfiguration.Sound;
import org.iceui.HPosition;
import org.iceui.VPosition;
import org.iceui.controls.BusySpinner;
import org.iceui.controls.FancyPositionableWindow;
import org.iceui.controls.FancyWindow;
import org.iceui.controls.UIUtil;

import com.jme3.math.Vector2f;

import icetone.controls.text.Label;
import icetone.controls.windows.Panel;
import icetone.core.Element.ZPriority;
import icetone.core.layout.BorderLayout;
import icetone.core.layout.LUtil;

public class AudioAppState extends IcemoonAppState<IcemoonAppState<?>> {

	static final Logger LOG = Logger.getLogger(AudioAppState.class.getName());
	final static String QUEUE = "QueueItem";
	private Map<AudioQueue, AudioQueueHandler> queueHandlers = new EnumMap<AudioQueue, AudioQueueHandler>(AudioQueue.class);
	private static AudioAppState instance;
	ExecutorService executor;

	public static AudioAppState get() {
		return instance;
	}

	public AudioAppState(Preferences prefs) {
		super(prefs);
		addPrefKeyPattern(SceneConfig.AUDIO + ".*");
		instance = this;
	}

	public void fadeAndRemove(AudioQueue queue, String soundName) {
		Sound sound = AudioConfiguration.getSound(soundName, assetManager);
		if (sound == null) {
			LOG.log(Level.SEVERE, String.format("Could not find sound configuration for %s, no audio will be removed", soundName));
		} else {
			if (queue == null) {
				queue = sound.getChannel();
			}
			AudioQueueHandler qh = getQueue(queue);
			qh.fadeAndRemove(sound.getPath());
		}
	}

	public void queue(AudioQueue queue, Object owner, String soundName, float interval, float gain) {

		// Get the path for the audio resource configuration
		if (LOG.isLoggable(Level.FINE))
			LOG.fine(String.format("Loading audio configuration for %s", soundName));

		Sound sound = AudioConfiguration.getSound(soundName, assetManager);
		if (sound == null) {
			LOG.log(Level.SEVERE, String.format("Could not find sound configuration for %s, no audio will be played", soundName));
		} else {
			if (queue == null) {
				queue = sound.getChannel();
			}
			QueuedAudio music = new QueuedAudio(owner, sound.getPath(), interval, sound.isLoop(), queue, sound.getGain() * gain);
			music.setStream(sound.isStream());
			music.setStream(false);
			music.setStreamCache(sound.isStreamCache());
			queue(music);
		}
	}

	public boolean isAnyPlaying(AudioQueue q) {
		AudioQueueHandler qh = queueHandlers.get(q);
		if (qh != null) {
			return !qh.isIdle();
		}
		return false;
	}

	public void clearQueues() {
		clearQueues(AudioQueue.values());
	}

	public void clearQueues(AudioQueue... queues) {
		for (AudioQueue q : queues) {
			AudioQueueHandler qh = queueHandlers.get(q);
			if (qh != null) {
				qh.clearQueue();
			}
		}
	}

	public void stopAudio(boolean fade, AudioQueue... queues) {
		for (AudioQueue q : queues) {
			AudioQueueHandler qh = queueHandlers.get(q);
			if (qh != null) {
				qh.stopAudio(fade);
			}
		}
	}

	public void clearQueuesAndStopAudio(boolean fade, AudioQueue... queues) {
		clearQueues(queues);
		stopAudio(fade, queues);
	}

	public void stopAudio(boolean fade) {
		stopAudio(fade, AudioQueue.values());
	}

	public void queue(QueuedAudio music) {
		LOG.info("Queueing " + music);
		AudioQueueHandler qh = getQueue(music.getQueue());
		if (qh.queue(music)) {
			LOG.info("Queue is idle, playing (" + qh + ")");
			qh.playNextInQueue();
		}
	}

	public boolean isMute() {
		return prefs.getBoolean(SceneConfig.AUDIO_MUTE, SceneConfig.AUDIO_MUTE_DEFAULT);
	}

	public float getMasterVolume() {
		return prefs.getFloat(SceneConfig.AUDIO_MASTER_VOLUME, SceneConfig.AUDIO_MASTER_VOLUME_DEFAULT);
	}

	public float getActualMasterVolume() {
		return isMute() ? 0 : prefs.getFloat(SceneConfig.AUDIO_MASTER_VOLUME, SceneConfig.AUDIO_MASTER_VOLUME_DEFAULT);
	}

	public static void main(String[] args) throws Exception {
		IOUtils.copy(new URL("http://listen.ai-radio.org:8000/radio.opus").openStream(),
				new FileOutputStream(new File("/tmp/x.ogg")));
	}

	public float getActualUIVolume() {
		return prefs.getFloat(SceneConfig.AUDIO_UI_VOLUME, SceneConfig.AUDIO_UI_VOLUME_DEFAULT) * getActualMasterVolume();
	}

	public float getAmbientVolume() {
		return prefs.getFloat(SceneConfig.AUDIO_AMBIENT_VOLUME, SceneConfig.AUDIO_AMBIENT_VOLUME_DEFAULT);
	}

	public float getUIVolume() {
		return prefs.getFloat(SceneConfig.AUDIO_UI_VOLUME, SceneConfig.AUDIO_UI_VOLUME_DEFAULT);
	}

	public float getActualAmbientVolume() {
		return prefs.getFloat(SceneConfig.AUDIO_AMBIENT_VOLUME, SceneConfig.AUDIO_AMBIENT_VOLUME_DEFAULT) * getActualMasterVolume();
	}

	public float getMusicVolume() {
		return prefs.getFloat(SceneConfig.AUDIO_MUSIC_VOLUME, SceneConfig.AUDIO_MUSIC_VOLUME_DEFAULT);
	}

	public float getActualMusicVolume() {
		return prefs.getFloat(SceneConfig.AUDIO_MUSIC_VOLUME, SceneConfig.AUDIO_MUSIC_VOLUME_DEFAULT) * getActualMasterVolume();
	}

	@Override
	public void update(float tpf) {
		for (AudioQueueHandler qh : queueHandlers.values()) {
			qh.update(tpf);
		}
	}

	public AudioQueueHandler getQueue(AudioQueue queue) {
		AudioQueueHandler qh = queueHandlers.get(queue);
		if (qh == null) {
			LOG.info("Creating new queue handler for " + queue);
			qh = new AudioQueueHandler(app, executor, queue);
			queueHandlers.put(queue, qh);
		}
		return qh;
	}

	@Override
	protected void postInitialize() {

		executor = Executors.newSingleThreadExecutor(new QueueExecutor.DaemonThreadFactory("AudioQueue"));
		boolean reindex = app.getAssets().isUpdateIndexes();
		if (!reindex) {
			try {
				AudioConfiguration.loadIndex(assetManager);
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "Failed to load audio index. Attempt to build now", e);
				reindex = true;
			}
		}
		if (reindex) {

			FancyPositionableWindow c = new FancyPositionableWindow(screen, "ExitPopup", 0, VPosition.MIDDLE, HPosition.CENTER,
					LUtil.LAYOUT_SIZE, FancyWindow.Size.SMALL, true) {
			};
			c.setWindowTitle("Re-indexing");
			c.setIsMovable(false);
			c.setIsResizable(false);
			c.setIsModal(true);
			c.getContentArea().setLayoutManager(new BorderLayout());
			c.getContentArea().addChild(new Label("Indexing audio. Please wait ...", screen));
	        c.getContentArea().addChild(new BusySpinner(screen, new Vector2f(31,31)).setSpeed(10f), BorderLayout.Border.EAST);
			c.sizeToContent();
			UIUtil.center(screen, c);
			app.getScreen().addElement(c, null, true);
			c.showAsModal(true);
			executor.execute(new Runnable() {

				@Override
				public void run() {
					try {
						AudioConfiguration.reindex(assetManager);
					} finally {
						app.enqueue(new Callable<Void>() {

							@Override
							public Void call() throws Exception {
								screen.removeElement(c);
								return null;
							}
						});
					}
				}
			});
		}
	}

	@Override
	protected void onCleanup() {
		super.onCleanup();
		executor.shutdown();
		stopAudio(false);
	}

	@Override
	protected void handlePrefUpdateSceneThread(PreferenceChangeEvent evt) {
		if (evt.getKey().startsWith(SceneConfig.AUDIO)) {
			for (AudioQueueHandler q : queueHandlers.values()) {
				q.handlePrefUpdate(evt);
			}
		}
	}
}
