package org.icescene.console.commands;

import org.apache.commons.cli.CommandLine;
import org.icescene.console.AbstractCommand;
import org.icescene.console.Command;

import icemoon.iceloader.AssetIndex;
import icemoon.iceloader.ServerAssetManager;
import icemoon.iceloader.locators.AssetCacheLocator;

@Command(names = { "assets", "as" })
public class Assets extends AbstractCommand {

	@Override
	public boolean run(String cmdName, CommandLine commandLine) {
		String[] args = commandLine.getArgs();
		ServerAssetManager assmgr = (ServerAssetManager) app.getAssetManager();
		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				for (String path : assmgr.getAssetNamesMatching(args[i])) {
					console.output(path);
				}
			}
		} else {
			console.output(String.format("Cache : %s", AssetCacheLocator.isInUse() ? "enabled" : "disabled"));
			console.output(String.format("Cache dir: %s", AssetCacheLocator.getDefaultStoreRoot()));
			console.output(String.format("VFS root: %s", AssetCacheLocator.getVFSRoot()));
			console.output(String.format("External assets: %s", app.getAssets().getExternalAssetsFolder()));
			for (AssetIndex idx : assmgr.getIndexes()) {
				console.output(String.format("Index: %s", idx.getId()));
				console.output(String.format("  Path: %s", idx.getAbsoluteAssetPath()));
				console.output(String.format("  Items: %d", idx.getBackingObject().size()));
			}
		}
		return true;
	}

}
