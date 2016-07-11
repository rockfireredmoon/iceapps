package org.icescene.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.vfs2.FileObject;
import org.icescene.audio.AudioQueue;

import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;

import icemoon.iceloader.AbstractConfiguration;
import icemoon.iceloader.ServerAssetManager;
import icemoon.iceloader.locators.AssetCacheLocator;

public class AudioConfiguration extends AbstractConfiguration<INIFile> {
	final static Logger LOG = Logger.getLogger(AudioConfiguration.class.getName());

	public static class Sound {
		private final String name;
		private final AudioQueue channel;
		private final float refDistance;
		private final float maxDistance;
		private final float gain;
		private final int priority;
		private final boolean loop;
		private final boolean stream;
		private final boolean streamCache;
		private final AudioConfiguration configuration;

		public Sound(String name, AudioQueue channel, float refDistance, float maxDistance, float gain, int priority,
				boolean loop, AudioConfiguration configuration, boolean stream, boolean streamCache) {
			this.name = name;
			this.channel = channel;
			this.refDistance = refDistance;
			this.maxDistance = maxDistance;
			this.gain = gain;
			this.priority = priority;
			this.loop = loop;
			this.configuration = configuration;
			this.stream = stream;
			this.streamCache = streamCache;
		}

		public boolean isStream() {
			return stream;
		}

		public boolean isStreamCache() {
			return streamCache;
		}

		public AudioConfiguration getConfiguration() {
			return configuration;
		}

		public AudioQueue getChannel() {
			return channel;
		}

		public String getName() {
			return name;
		}

		public float getRefDistance() {
			return refDistance;
		}

		public float getMaxDistance() {
			return maxDistance;
		}

		public float getGain() {
			return gain;
		}

		public int getPriority() {
			return priority;
		}

		public boolean isLoop() {
			return loop;
		}

		public String getPath() {
			return configuration.getAssetFolder() + "/" + name;
		}

	}

	private final static Map<String, AudioConfiguration> cfgs = new HashMap<String, AudioConfiguration>();
	private Map<String, Sound> sounds = new HashMap<String, AudioConfiguration.Sound>();
	private final static Map<String, Sound> allSounds = new HashMap<String, AudioConfiguration.Sound>();
	private static Properties index;
	private final static boolean INDEX_ENABLED = "true".equals(System.getProperty("icescene.indexedAudio"));

	public static void loadIndex(AssetManager assetManager) throws IOException {
		if (index == null) {
			index = new Properties();
			if (INDEX_ENABLED) {
				FileObject vfsRoot = AssetCacheLocator.getVFSRoot();
				if (vfsRoot != null) {
					InputStream in = vfsRoot.resolveFile("audio.idx").getContent().getInputStream();
					try {
						index.load(in);
					} finally {
						in.close();
					}
				} else {
					LOG.log(Level.WARNING, "No audio index loaded as an AssetCacheLocation is not configured.");
				}
			}
		}
	}

	public static void reindex(AssetManager assetManager) {
		if (!INDEX_ENABLED) {
			LOG.log(Level.WARNING, "No audio index created as it is not enabled.");
			return;
		}
		FileObject vfsRoot = AssetCacheLocator.getVFSRoot();
		if (vfsRoot == null) {
			LOG.log(Level.WARNING, "No audio index created as an AssetCacheLocation is not configured.");
			return;
		}
		LOG.info("Re-indexing audio configuration");
		ServerAssetManager mgr = (ServerAssetManager) assetManager;
		Set<String> assetNamesMatching = mgr.getAssetNamesMatching(".*\\.sound\\.cfg");
		for (String p : assetNamesMatching) {
			LOG.info(String.format("Indexing %s", p));
			getAudioConfiguration(p, assetManager);
		}
		index = new Properties();
		for (Map.Entry<String, AudioConfiguration> c : cfgs.entrySet()) {
			for (Sound s : c.getValue().getSounds().values()) {
				index.put(s.getName(), c.getValue().getAssetPath());
			}
		}
		try {
			OutputStream out = vfsRoot.resolveFile("audio.idx").getContent().getOutputStream();
			try {
				index.store(out, "Audio Index");
			} finally {
				out.close();
			}
		} catch (Exception e) {
			LOG.log(Level.SEVERE,
					"Failed to generate audio index. Index will be recreatred on every startup causing delays.", e);
		}
	}

	public static AudioConfiguration getAudioConfiguration(String name, AssetManager assetManager) {
		if (cfgs.containsKey(name)) {
			return cfgs.get(name);
		} else {
			AudioConfiguration cfg = new AudioConfiguration(name, assetManager);
			cfgs.put(name, cfg);
			return cfg;
		}
	}

	public static Sound getSound(String soundName, AssetManager assetManager) {
		String baseSoundName = FilenameUtils.getBaseName(soundName);
		String name = FilenameUtils.getName(soundName);
		if (allSounds.containsKey(name)) {
			return allSounds.get(name);
		} else {
			if (INDEX_ENABLED) {

				if (index == null) {
					throw new AssetNotFoundException(
							String.format("%s cannot be located as audio indexes are not loaded.", baseSoundName));
				}
				// loadIndex(assetManager);
				if (index.containsKey(name)) {
					AudioConfiguration cfg = AudioConfiguration.getAudioConfiguration(index.getProperty(name),
							assetManager);
					if (!cfg.getSounds().containsKey(name)) {
						throw new AssetNotFoundException(String.format(
								"No sound configuration named %s. This should not happen! It means the audio index is out of sync with the audio resources.",
								baseSoundName));
					}
				} else {
					throw new AssetNotFoundException(String.format("No sound configuration named %s.", baseSoundName));
				}
				return allSounds.get(name);
			} else {
				Set<String> oggAsset = ((ServerAssetManager) assetManager)
						.getAssetNamesMatching(String.format(".*/%s\\.ogg", baseSoundName));
				if (oggAsset.size() > 0) {
					if (oggAsset.size() > 1) {
						LOG.warning(String.format("%s matches %d audio file.", name, oggAsset.size()));
					}
					String path = oggAsset.iterator().next();
					AudioConfiguration cfg = null;

					/*
					 * Try and load two different sound configurations, one in
					 * the folder the OGG is found having the same name as the
					 * basename of the parent (with .sound.cfg append), and one
					 * for the individual OGG file
					 * 
					 * TODO revert back to single file for boss/horde etc now i
					 * know how it works :) *
					 */

					try {
						cfg = AudioConfiguration.getAudioConfiguration(
								path.substring(0, path.length() - 4) + ".sound.cfg", assetManager);
					} catch (AssetNotFoundException anfe) {
						if (LOG.isLoggable(Level.FINE))
							LOG.fine(String.format("%s does not have it's own sound configuration file.", path));
					}

					// Now the folder one
					if (cfg == null) {
						String folder = FilenameUtils.getPathNoEndSeparator(path);
						if (!folder.equals("")) {
							String folderName = FilenameUtils.getBaseName(folder);
							try {
								cfg = AudioConfiguration.getAudioConfiguration(folder + "/" + folderName + ".sound.cfg",
										assetManager);
							} catch (AssetNotFoundException anfe) {
								if (LOG.isLoggable(Level.FINE))
									LOG.fine(String.format("%s does not have a folder configuration file.", path));
							}
						}
					}

					if (cfg != null) {
						Sound snd = cfg.getSounds().get(name);
						if (snd != null) {
							allSounds.put(name, snd);
							return snd;
						}
					}

				}

				throw new AssetNotFoundException(String.format(
						"No sound configuration named %s. This should not happen! It means the audio index is out of sync with the audio resources.",
						baseSoundName));
			}
		}
	}

	public AudioConfiguration(String resourceName, AssetManager assetManager) {
		super(resourceName, assetManager, new INIFile());
		INIFile file = getBackingObject();
		String[] allSectionNames = file.getAllSectionNames();
		if (allSectionNames == null)
			throw new AssetNotFoundException(
					String.format("Audio configuration %s has no section names..", resourceName));
		for (String section : allSectionNames) {
			float refDistance = file.getProperties(section).containsKey("refDistance")
					? Float.parseFloat(file.getStringProperty(section, "refDistance")) : 0;
			float maxDistance = file.getProperties(section).containsKey("maxDistance")
					? Float.parseFloat(file.getStringProperty(section, "maxDistance")) : 1920;
			String gainString = file.getStringProperty(section, "gain");
			float gain = gainString == null ? 1 : Float.parseFloat(gainString);
			int priority = file.getProperties(section).containsKey("priority")
					? Integer.parseInt(file.getStringProperty(section, "priority")) : 0;
			boolean loop = Boolean.parseBoolean(file.getStringProperty(section, "loop"));
			boolean stream = Boolean.parseBoolean(file.getStringProperty(section, "stream"));
			boolean streamCache = Boolean.parseBoolean(file.getStringProperty(section, "streamCache"));
			String channelName = file.getStringProperty(section, "channel");
			AudioQueue channel = channelName == null ? AudioQueue.INTERFACE
					: AudioQueue.valueOf(channelName.toUpperCase());
			Sound value = new Sound(section, channel, refDistance, maxDistance, gain, priority, loop, this, stream,
					streamCache);
			sounds.put(section, value);
			allSounds.put(section, value);
		}
	}

	public Map<String, Sound> getSounds() {
		return Collections.unmodifiableMap(sounds);
	}

	@Override
	protected void load(InputStream in, INIFile backingObject) throws IOException {
		backingObject.load(in);
	}

}
