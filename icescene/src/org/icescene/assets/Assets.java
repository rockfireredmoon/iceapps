package org.icescene.assets;

import icemoon.iceloader.AssetIndex;
import icemoon.iceloader.ServerAssetManager;
import icemoon.iceloader.locators.AssetCacheLocator;
import icemoon.iceloader.locators.ServerLocator;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.vfs2.AllFileSelector;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;
import org.icescene.IcesceneApp;

import com.jme3.asset.AssetLoadException;

/**
 * Configures the custom asset locators / loaders and contains helpers for
 * dealing with assets, particularly saving them in build mode to local
 * locations.
 */
public class Assets {

	private static final Logger LOG = Logger.getLogger(Assets.class.getName());
	private static final String DEFAULT_ASSET_URL = System.getProperty("icescene.defaultAssetUrl",
			"http://assets.theanubianwar.com/iceclient-assets/");

	public static void addOptions(Options opts) {
		opts.addOption("x", "clear-cache", false, "When present, asset cache will be cleared before starting.");
		opts.addOption("l", "cache-location", true, "VFS location of the asset cache.");
	}

	private String externalAssetsDir;
	private IcesceneApp app;
	private Boolean updateIndexes = null;

	public Assets(IcesceneApp app, String externalAssetsDir, CommandLine commandLine) {
		this.app = app;
		setAssetsExternalLocation(externalAssetsDir);

		try {
			if (commandLine != null && !commandLine.getArgList().isEmpty()) {
				// Set root for HTTP asset requests
				String assetUrl = commandLine.getArgs()[0];
				ServerLocator.setServerRoot(new URL(assetUrl));
			} else {
				ServerLocator.setServerRoot(new URL(DEFAULT_ASSET_URL));
			}
		} catch (MalformedURLException ex) {
			throw new RuntimeException("Invalid server URL.", ex);
		}

		MeshLoader.setTexturePathsRelativeToMesh(true);

		try {
			// Cache location
			if (commandLine != null && commandLine.hasOption('l')) {
				AssetCacheLocator.setVFSRoot(VFS.getManager().resolveFile(commandLine.getOptionValue('l')));
			} else {
				// Default single file cache
				// TODO still seems to be corrupting
				// FileSystemOptions fsOpts = new FileSystemOptions();
				// File file = new File(System.getProperty("user.home")
				// + File.separator + ".cache" + File.separator + "icescene"
				// + File.separator + "assets" + File.separator + "main.dsk");
				// Fat32FileSystemConfigBuilder.getInstance().setContainerFile(fsOpts,
				// file);
				// AssetCacheLocator.setVFSRoot(VFS.getManager().resolveFile("fat32:///",
				// fsOpts));
				setCacheLocationForServerLocator();
			}

			// Clear local cache if requested to do so
			if (commandLine != null && commandLine.hasOption('x')) {
				AssetCacheLocator.getVFSRoot().delete(new AllFileSelector());
			}
		} catch (Exception ex) {
			throw new RuntimeException("Failed to configure cache options.", ex);
		}
	}

	public void setCacheLocationForServerLocator() throws IOException, FileSystemException {
		String hostname = "default";
		URL url = ServerLocator.getServerRoot();
		if (url != null) {
			hostname = url.getHost();
			if ((url.getProtocol().equals("http") && url.getPort() != 80 && url.getPort() > -1)
					|| (url.getProtocol().equals("https") && url.getPort() != 443 && url.getPort() > -1)) {
				hostname += ":" + url.getPort();
			}
		}
		setCacheLocationForHostname(hostname);
	}

	public void setCacheLocationForHostname(String hostname) throws IOException, FileSystemException {
		File file = new File(System.getProperty("user.home") + File.separator + ".cache" + File.separator + "icescene"
				+ File.separator + hostname);
		if (!file.exists() && !file.mkdirs()) {
			throw new IOException("Failed to create cache directory");
		}
		AssetCacheLocator.setVFSRoot(VFS.getManager().resolveFile(file.toURI().toString()));
	}

	public File getExternalAssetFile(String resourceName) {
		return getExternalAssetFile(resourceName, getExternalAssetsFolder());
	}

	public File getExternalAssetFile(String resourceName, File parentDir) {
		File file = new File(parentDir, resourceName.replace('/', File.separatorChar));
		File dir = file.getParentFile();
		return file;
	}

	public final File getExternalAssetsFolder() {
		String dir = externalAssetsDir;
		if (dir == null || dir.equals("")) {
			dir = System.getProperty("user.dir") + File.separator + "assets";
		}
		File dirf = new File(dir);
		if (!dirf.exists() && !dirf.mkdirs()) {
			throw new AssetLoadException(String.format("Invalid external assets location. Failed to create %s", dir));
		}
		return dirf;
	}

	public final void setAssetsExternalLocation(String externalAssetsDir) {
		this.externalAssetsDir = externalAssetsDir;
		try {
			// Set the local storage location
			icemoon.iceloader.locators.FileLocator
					.setDefaultStoreRoot(VFS.getManager().resolveFile(getExternalAssetsFolder().toURI().toString()));
		} catch (IOException ex) {
			LOG.log(Level.SEVERE,
					"Failed to configure local asset storage, you may not be able to save if you have permission to use build tools.",
					ex);
		}
	}

	public boolean isExternal(String assetPath) {
		return getExternalAssetFile(assetPath).exists();
	}

	public boolean isUpdateIndexes() {
		if (updateIndexes == null) {
			ServerAssetManager mgr = (ServerAssetManager) app.getAssetManager();
			Preferences node = app.getPreferences().node("indexes");
			for (AssetIndex idx : mgr.getIndexes()) {
				String key = String.valueOf(idx.getId());
				long lm = node.getLong(key, -1);
				long am = idx.getLastModified();
				if (lm != am) {
					node.putLong(key, am);
					updateIndexes = true;
				}
			}
			if (updateIndexes == null)
				updateIndexes = false;
		}
		return updateIndexes;
	}
}
