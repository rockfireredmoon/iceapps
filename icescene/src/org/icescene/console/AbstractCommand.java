package org.icescene.console;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.icescene.IcesceneApp;

public abstract class AbstractCommand implements ConsoleCommand {

	protected Options options = new Options();
	protected ConsoleAppState console;
	protected IcesceneApp app;
	protected String argHelp;
	protected String description;

	public void init(final ConsoleAppState console) {
		this.console = console;

		app = console.getApp();
	}

	public String getDescription() {
		return description;
	}

	public String getArgHelp() {
		return argHelp;
	}

	public Options getOptions() {
		return options;
	}

	@Override
	public final void execute(String cmdName, CommandLine commandLine) {
		if (run(cmdName, commandLine)) {
			console.checkHide();
		}
	}

	public abstract boolean run(String cmdName, CommandLine commandLine);
}
