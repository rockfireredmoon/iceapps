package org.icescene.console.commands;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.icelib.AppInfo;
import org.icescene.console.AbstractCommand;
import org.icescene.console.Command;

@Command(names = { "dev" })
public class Dev extends AbstractCommand {

	public Dev() {
		getOptions().addOption(
				new Option("d", "disable", false, "Disable dev mode (may not be re-enabled without restarting)"));
	}

	@Override
	public boolean run(String cmdName, CommandLine commandLine) {
		if (AppInfo.isDev()) {
			if (commandLine.hasOption('d')) {
				AppInfo.setDev(false);
				console.info("Dev mode is now disabled.");
			} else
				console.info("Enabled.");
		} else
			console.info("Disabled.");
		return true;
	}

}
