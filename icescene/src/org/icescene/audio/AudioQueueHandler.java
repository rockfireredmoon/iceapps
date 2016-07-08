package org.icescene.audio;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.icelib.Icelib;
import org.icescene.HUDMessageAppState;
import org.icescene.IcesceneApp;
import org.icescene.SceneConstants;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoadException;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioKey;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource;
import com.jme3.audio.plugins.OGGLoader;
import com.jme3.scene.Node;

public class AudioQueueHandler {

	static final Logger LOG = Logger.getLogger(AudioQueueHandler.class.getName());

	/**
	 * 
	 */
	private List<QueuedAudio> queuedAudio = Collections.synchronizedList(new ArrayList<QueuedAudio>());
	private List<XAudioNode> audioNodes = Collections.synchronizedList(new ArrayList<XAudioNode>());
	private List<XAudioNode> fadingOutNodes = Collections.synchronizedList(new ArrayList<XAudioNode>());
	private float playTimestamp;
	private float nextPlay = -1;
	private final AudioQueue queue;
	private QueuedAudio loopPlay;
	private IcesceneApp app;
	private ExecutorService executor;

	AudioQueueHandler(IcesceneApp app, ExecutorService executor, AudioQueue queue) {
		this.app = app;
		this.executor = executor;
		this.queue = queue;
	}

	public boolean isQueued(String resource) {
		synchronized (queuedAudio) {
			for (QueuedAudio n : queuedAudio) {
				if (n.getPath().equals(resource)) {
					return true;
				}
			}
			return false;

		}
	}

	public boolean isPlaying(String resource) {
		synchronized (audioNodes) {
			for (XAudioNode n : audioNodes) {
				if (n.getAudioKey().getName().equals(resource)) {
					return true;
				}
			}
			return false;
		}
	}

	public boolean isLoop(String resource) {
		synchronized (audioNodes) {
			for (XAudioNode n : audioNodes) {
				if (n.getAudioKey().getName().equals(resource)) {
					return n.isLooping();
				}
			}
			synchronized (queuedAudio) {
				for (QueuedAudio n : queuedAudio) {
					if (n.getPath().equals(resource)) {
						return n.isLoop();
					}
				}
			}
		}
		return false;
	}

	public void setLoop(String resource, boolean loop) {
		synchronized (audioNodes) {
			for (XAudioNode n : audioNodes) {
				if (n.getAudioKey().getName().equals(resource)) {
					n.setLooping(loop);
					return;
				}
			}
		}
		synchronized (queuedAudio) {
			for (QueuedAudio n : queuedAudio) {
				if (n.getPath().equals(resource)) {
					n.setLoop(loop);
					return;
				}
			}
		}
	}

	public void setGain(String resource, float gain) {
		synchronized (audioNodes) {
			for (XAudioNode n : audioNodes) {
				if (n.getAudioKey().getName().equals(resource)) {
					n.setVolume(gain);
					return;
				}
			}
		}
		synchronized (queuedAudio) {
			for (QueuedAudio n : queuedAudio) {
				if (n.getPath().equals(resource)) {
					n.setGain(gain);
					return;
				}
			}
		}
	}

	public float getGain(String resource) {
		synchronized (audioNodes) {
			for (XAudioNode n : audioNodes) {
				if (n.getAudioKey().getName().equals(resource)) {
					return n.getVolume();
				}
			}

		}
		synchronized (queuedAudio) {
			for (QueuedAudio n : queuedAudio) {
				if (n.getPath().equals(resource)) {
					return n.getGain();
				}
			}
		}
		return 0;
	}

	public void update(float tpf) {
		if (nextPlay != -1) {
			playTimestamp += tpf;
			if (playTimestamp >= nextPlay) {
				if (LOG.isLoggable(Level.FINE)) {
					LOG.fine(String.format("Playing next music in queue as %4.2f has elapsed", playTimestamp));
				}
				playTimestamp = 0;
				if (loopPlay != null) {
					queuedAudio.add(loopPlay);
				}
				playNextInQueue();
			}
		}

		// Fade anything that needs fading
		if (!fadingOutNodes.isEmpty()) {
			synchronized (fadingOutNodes) {
				for (Iterator<XAudioNode> it = fadingOutNodes.iterator(); it.hasNext();) {
					XAudioNode n = it.next();
					if (n.getVolume() > 0) {
						n.setVolume(Math.max(0, n.getVolume() - (SceneConstants.AUDIO_FADE_AMOUNT * tpf)));
						if (LOG.isLoggable(Level.FINE)) {
							LOG.fine(String.format("Fading audio %s to %f", n, n.getVolume()));
						}
					} else {
						it.remove();
					}
				}
			}
		}

	}

	public boolean isIdle() {
		if (nextPlay == -1 || audioNodes.isEmpty()) {
			return true;
		}
		synchronized (audioNodes) {
			for (AudioNode n : audioNodes) {
				if (n.getStatus().equals(AudioSource.Status.Playing)) {
					return false;
				}
			}
		}
		return true;
	}

	boolean queue(QueuedAudio qa) {
		synchronized (audioNodes) {
			synchronized (queuedAudio) {
				if (queuedAudio.contains(qa)) {
					throw new IllegalStateException("Already queued " + qa);
				}
				qa.init(this);

				int qaSize = queuedAudio.size();
				int anSize = audioNodes.size();
				boolean first = qaSize + anSize == 0;
				queuedAudio.add(qa);
				LOG.fine(String.format("Queued %s (queue was %d in size, is now %d with %d audio nodes)", qa.getPath(),
						qaSize, queuedAudio.size(), anSize));
				if (nextPlay == -1)
					nextPlay = 0;
				return first;
			}
		}
	}

	void playNextInQueue() {
		if (!executor.isShutdown())
			executor.submit(new Runnable() {
				public void run() {
					try {
						doPlayNextInQueue();
					} catch (Exception e) {
						LOG.log(Level.SEVERE, "Failed to play next audio in queue.", e);
					}
				}
			});
		else
			LOG.log(Level.WARNING, "Failed to play next audio, the executor was not running.");
	}

	void doPlayNextInQueue() {
		if (LOG.isLoggable(Level.FINE))
			LOG.fine("Playing next in queue of " + queuedAudio.size());

		// Remove any existing audio nodes
		// synchronized (audioNodes) {
		// for (AudioNode n : audioNodes) {
		// n.removeFromParent();
		// }
		// audioNodes.clear();
		// }

		if (queuedAudio.size() > 0) {
			QueuedAudio qm = queuedAudio.get(0);
			String path = qm.getPath();

			// Work out if this is a stream
			if (Icelib.isUrl(path)) {
				boolean stream = false;
				// Detect our special 'stream' URL
				int idx = path.indexOf("?stream");
				if (idx == -1) {
					idx = path.indexOf("&stream");
				}
				if (idx != -1) {
					stream = true;
					path = path.substring(0, idx) + path.substring(idx + 7);
				}

				URL u;
				try {
					u = new URL(path);
				} catch (MalformedURLException murle) {
					throw new IllegalArgumentException("Must be a valid URL. '" + path + "' is not.");
				}

				if (!u.getPath().endsWith(".m3u")) {
					LOG.info(String.format("Loading audio %s", path));
					// Just a normal audio resource
					if (playAsset(qm)) {
						return;
					}
				} else {
					LOG.info(String.format("Loading playlist %s", path));
					Object playlist = app.getAssetManager().loadAsset(path);
					if (playlist instanceof M3ULoader.M3UPlaylist) {
						M3ULoader.M3UPlaylist m3u = (M3ULoader.M3UPlaylist) playlist;
						final M3ULoader.M3UInfo track = m3u.get(0);
						// TODO for now just play the first found
						// (QueuedData needs multiple resource support)
						HUDMessageAppState ham = app.getStateManager().getState(HUDMessageAppState.class);
						if (ham != null)
							ham.message(Level.INFO, String.format("Now Playing - %s",
									track.getTitle() == null ? track.getLocation() : track.getTitle()));
						qm.setPath(track.getLocation().toString());
						qm.setStreamCache(!stream);
						qm.setLoop(false);
						if (playAsset(qm)) {
							return;
						}
						;
					} else {
						throw new UnsupportedOperationException();
					}
				}

			} else {

				// Just a normal audio resource
				if (playAsset(qm)) {
					return;
				}
			}
		} else {
			if (LOG.isLoggable(Level.FINE)) {
				LOG.fine("There is nothing left in the queue");
			}
		}

		// Play next in queue immediately if there is anything
		nextPlay = -1;
	}

	public void fadeAndRemove(String soundPath) {
		synchronized (queuedAudio) {

			synchronized (audioNodes) {

				QueuedAudio q = null;
				for (QueuedAudio n : queuedAudio) {
					if (n.getPath().equals(soundPath)) {
						q = n;
						break;
					}
				}
				if (q != null) {
					if (q.equals(loopPlay)) {
						loopPlay = null;
					}
					queuedAudio.remove(q);
				}

				XAudioNode a = null;
				for (XAudioNode n : audioNodes) {
					if (n.getAudioKey().getName().equals(soundPath)) {
						a = n;
						break;
					}
				}
				if (a != null) {
					audioNodes.remove(a);
					fadingOutNodes.add(a);
				}

				if (audioNodes.isEmpty() && !executor.isShutdown()) {
					playNextInQueue();
				}
			}
		}
	}

	public void stopAudio(boolean fade) {
		if (fade) {
			if (LOG.isLoggable(Level.FINE)) {
				LOG.fine(String.format("Fading %d nodes", audioNodes.size()));
			}
			fadingOutNodes.addAll(audioNodes);
		} else {
			if (LOG.isLoggable(Level.FINE)) {
				LOG.fine(String.format("Stopping %d nodes", audioNodes.size()));
			}
			synchronized (audioNodes) {
				for (AudioNode node : audioNodes) {
					node.stop();
					node.removeFromParent();
				}
			}
		}
		audioNodes.clear();
	}

	void handlePrefUpdate(PreferenceChangeEvent evt) {
		synchronized (audioNodes) {
			for (AudioNode an : audioNodes) {
				QueueData qd = (QueueData) an.getUserData(AudioAppState.QUEUE);
				an.setVolume(qd.qa.getActualGain());
			}
		}
	}

	void clearQueue() {
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine(String.format("Clearing queue for %s (has %d items)", queue, queuedAudio.size()));
		}
		nextPlay = -1;
		playTimestamp = 0;
		queuedAudio.clear();
	}

	private boolean playAsset(final QueuedAudio qm) {
		LOG.info(String.format("Preparing %s to play", qm));
		try {
			queuedAudio.remove(qm);

			LOG.info(String.format("%s removed from queue.", qm));
			AudioData ad = null;
			final AudioKey ak = new AudioKey(qm.getPath(), qm.isStream(), qm.isStreamCache());
			final AssetInfo inf;
			if (qm.getPath().startsWith("http:") || qm.getPath().startsWith("https")) {
				inf = new AssetInfo(app.getAssetManager(), ak) {

					@Override
					public InputStream openStream() {
						CloseableHttpClient httpclient = HttpClients.createDefault();
						try {
							HttpGet httpget = new HttpGet(getKey().getName());
							System.err.println("Executing request " + httpget.getRequestLine());
							CloseableHttpResponse response = httpclient.execute(httpget);
							System.err.println("----------------------------------------");
							System.err.println(response.getStatusLine());
							HttpEntity entity = response.getEntity();
							if (entity != null) {
								return new FilterInputStream(entity.getContent()) {

									@Override
									public int read(byte[] b) throws IOException {
										int r = super.read(b);
										System.err.println("Read! " + r + " " + b.length);
										return r;
									}

									@Override
									public int read(byte[] b, int off, int len) throws IOException {
										int r = super.read(b, off, len);
										System.err.println("Read! " + off + " /" + len + "  = " + r);
										return r;
									}

									@Override
									public long skip(long n) throws IOException {
										long a = super.skip(n);
										System.err.println("Skip! " + n + " = " + a);
										return a;
									}

									@Override
									public int available() throws IOException {
										int a = super.available();
										System.err.println("Available! " + a);
										return a;
									}

									@Override
									public void close() throws IOException {
										super.close();
										System.err.println("Close!");
									}

									@Override
									public synchronized void reset() throws IOException {
										super.reset();
										System.err.println("Reset!");
									}
								};
							}
						} catch (Exception ioe) {
							throw new AssetLoadException("Failed to load stream.", ioe);
						}
						throw new AssetNotFoundException("Could not find " + getKey());
					}
				};
			} else {
				inf = app.getAssetManager().locateAsset(ak);
			}
			if (inf == null) {
				throw new AssetNotFoundException("Could not find " + ak);
			}
			OGGLoader ol = new OGGLoader();
			try {
				ad = (AudioData) ol.load(inf);
			} catch (IOException ex) {
				throw new AssetLoadException(String.format("Failed to load audio data for %s.", qm.getPath()), ex);
			}
			final AudioData fad = ad;
			final AudioKey fak = ak;
			app.enqueue(new Callable<Void>() {

				public Void call() {
					LOG.info(String.format("Playing %s", qm));
					try {

						final XAudioNode music = new XAudioNode(fad, fak);
						music.setUserData(AudioAppState.QUEUE, new QueueData(qm));
						if (LOG.isLoggable(Level.FINE)) {
							LOG.fine(String.format("Playing %s for %4.2f gain of %2.3f", qm.getPath(),
									fad.getDuration(), qm.getActualGain()));
						}

						if (qm.isPositional() && music.getAudioData().getChannels() == 2) {
							LOG.warning(String.format(
									"%s is stereo, so cannot be positional. It will be played as non-positional",
									qm.getPath()));
							qm.setPositional(false);
						}

						music.setPositional(qm.isPositional());
						music.setVolume(qm.getActualGain());
						music.setLooping(qm.isLoop());
						if (qm.isPositional()) {
							music.setRefDistance(qm.getRefDistance());
							music.setMaxDistance(qm.getMaxDistance());
							((Node) qm.getOwner()).attachChild(music);
						}
						music.play();

						LOG.info(String.format("Audio %s ready.", fak));
						// Make sure
						audioNodes.add(music);
						loopPlay = qm.isLoop() ? qm : null;
						playTimestamp = 0;
						nextPlay = loopPlay == null ? fad.getDuration() + qm.getInterval() : -1;
						if (LOG.isLoggable(Level.FINE)) {
							LOG.fine(String.format("Next play is at %3.2f", nextPlay));
						}
					} catch (Exception e) {
						LOG.log(Level.SEVERE, String.format("Failed to load audio %s.", qm.getPath()), e);
					}
					return null;
				}
			});
		} catch (Exception e) {
			LOG.log(Level.SEVERE, "Failed to load audio.", e);
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "AudioQueueHandler [queuedAudio=" + queuedAudio + ", audioNodes=" + audioNodes + ", fadingOutNodes="
				+ fadingOutNodes + ", playTimestamp=" + playTimestamp + ", nextPlay=" + nextPlay + ", queue=" + queue
				+ ", loopPlay=" + loopPlay + "]";
	}
}