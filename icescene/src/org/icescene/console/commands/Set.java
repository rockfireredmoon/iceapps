package org.icescene.console.commands;

import java.util.prefs.Preferences;

import org.apache.commons.cli.CommandLine;
import org.icescene.console.AbstractCommand;
import org.icescene.console.Command;
import org.icescene.console.ConsoleAppState;

@Command(names = "set")
public class Set extends AbstractCommand {

	private Preferences preferences;

	@Override
	public void init(ConsoleAppState console) {
		super.init(console);
		options.addOption("d", "delete", true, "Delete settings");
		preferences = console.getApp().getPreferences();
	}

	public boolean run(String cmdName, CommandLine commandLine) {
		String[] args = commandLine.getArgs();
		if (commandLine.hasOption('d')) {
			preferences.remove(commandLine.getOptionValue('d'));
			return true;
		} else {
			if (args.length > 0) {
				preferences.put(args[0], args[1]);
				console.output(String.format("'%s' set to '%s'", args[0], args[1]));
				return true;
			} else {
				console.outputError("Requires two arguments, preference name and value.");
				return false;
			}
		}
	}
}
