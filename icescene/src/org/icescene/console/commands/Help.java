package org.icescene.console.commands;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.icescene.console.AbstractCommand;
import org.icescene.console.Command;
import org.icescene.console.ConsoleCommand;

@Command(names = "help")
public class Help extends AbstractCommand {

	@Override
	public boolean run(String cmdName, CommandLine commandLine) {
		String[] args = commandLine.getArgs();
		if (args.length == 0) {
			console.output("Available commands :-");
			for (String s : console.getCommands().keySet()) {
				console.output(String.format("    %s", s));
			}
		} else {
			for (String c : args) {
				ConsoleCommand cmd = console.getCommands().get(c);
				if (cmd == null) {
					console.outputError(String.format("No command named %s", c));
				} else {
					HelpFormatter formatter = new HelpFormatter();
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw);
					console.output(String.format("\nHelp for '%s'", c));
					String argDesc = cmd.getArgHelp() == null ? c : String.format("%s %s", c, cmd.getArgHelp());
					formatter.printHelp(pw, 132, argDesc, null, cmd.getOptions(), 2, 4, cmd.getDescription(), true);
					console.output(sw.toString());
				}
			}
		}
		return false;
	}

}
