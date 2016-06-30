package org.icescene.console;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public interface ConsoleCommand {

    Options getOptions();

    void execute(String cmdName, CommandLine commandLine);
    
    void init(ConsoleAppState console);

	String getArgHelp();

	String getDescription();
    
}
