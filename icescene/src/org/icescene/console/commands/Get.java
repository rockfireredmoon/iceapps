package org.icescene.console.commands;

import java.util.prefs.Preferences;

import org.apache.commons.cli.CommandLine;
import org.icescene.console.AbstractCommand;
import org.icescene.console.Command;
import org.icescene.console.ConsoleAppState;

@Command(names = "get")
public class Get extends AbstractCommand {

	private Preferences preferences;

	@Override
	public void init(ConsoleAppState console) {
		super.init(console);
		preferences = console.getApp().getPreferences();
	}

	public boolean run(String cmdName, CommandLine commandLine) {
		String[] args = commandLine.getArgs();
		if (args.length > 0) {
			console.output(String.format("%s=%s", args[0], preferences.get(args[0], String.valueOf("<unset>"))));
		} else {
			console.outputError("Requires one arguments, preference name.");
		}
		return false;
	}
}
