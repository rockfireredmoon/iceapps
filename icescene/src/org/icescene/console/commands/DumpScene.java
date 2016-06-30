package org.icescene.console.commands;

import org.apache.commons.cli.CommandLine;
import org.icescene.console.AbstractCommand;
import org.icescene.console.Command;
import org.iceui.controls.UIUtil;

import com.jme3.scene.Node;

@Command(names = {"dump", "ds"})
public class DumpScene extends AbstractCommand {

    @Override
    public boolean run(String cmdName, CommandLine commandLine) {
	Node nd = app.getRootNode();
	String[] args = commandLine.getArgs();
	if (args.length == 1) {
	    if (args[0].equals("gui")) {
		nd = app.getGuiNode();
	    } else {
		console.outputError("Expected no arguments, or 'gui'");
		return false;
	    }
	}
	UIUtil.dump(nd, 0);
	return true;
    }

}
