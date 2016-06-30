package org.icescene;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.icelib.AppInfo;
import org.icescene.assets.Assets;

public class TestApp extends IcesceneApp {

	public static void main(String[] args, Class<? extends IcesceneApp> appClass) throws Exception {
		main(args, appClass, appClass.getSimpleName());
	}

	public static void main(String[] args, Class<? extends IcesceneApp> appClass, String appName) throws Exception {

		AppInfo.context = appClass;

		// Parse command line
		Options opts = createOptions();
		Assets.addOptions(opts);
		CommandLine cmdLine = parseCommandLine(opts, args);
		IcesceneApp app = appClass.getConstructor(CommandLine.class, String.class).newInstance(cmdLine, appName);
		startApp(app, cmdLine, "PlanetForever - " + AppInfo.getName() + " - " + AppInfo.getVersion(), appName);
	}

	protected TestApp(CommandLine commandLine, String appName) {
		super(SceneConfig.get(), commandLine, appName, "META-INF/SceneAssets_DEV.cfg");
	}

}
