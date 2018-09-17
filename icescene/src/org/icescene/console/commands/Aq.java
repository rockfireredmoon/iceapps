package org.icescene.console.commands;

import org.apache.commons.cli.CommandLine;
import org.icescene.audio.AudioAppState;
import org.icescene.audio.AudioQueue;
import org.icescene.console.AbstractCommand;
import org.icescene.console.Command;

@Command(names = { "aq", "audioqueue" })
public class Aq extends AbstractCommand {
	
	@Override
	public boolean run(String cmdName, CommandLine commandLine) {
		AudioAppState as = app.getStateManager().getState(AudioAppState.class);
		String[] args = commandLine.getArgs();
		if (args.length > 1) {
			for (int i = 1; i < args.length; i++) {
				as.getQueue(AudioQueue.valueOf(args[i])).stopAudio(true);
			}
		} else {
			as.stopAudio(true);
		}
		return true;
	}

}
