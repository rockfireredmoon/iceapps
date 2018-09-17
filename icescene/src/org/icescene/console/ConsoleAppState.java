package org.icescene.console;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.prefs.Preferences;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.ParseException;
import org.icescene.IcemoonAppState;

import com.jme3.app.state.AppStateManager;
import com.jme3.input.KeyInput;

import icetone.core.ZPriority;
import icetone.core.event.ScreenEvent;
import icetone.core.event.ScreenEventListener;
import icetone.extras.windows.Console;

public class ConsoleAppState extends IcemoonAppState<IcemoonAppState<?>> {

	private static Console console;
	private boolean showOnce;
	private Map<String, ConsoleCommand> commands = new LinkedHashMap<String, ConsoleCommand>();
	private boolean showOnInit;
	private boolean autoHide = true;
	private ScreenEventListener screenEventListener;

	public ConsoleAppState(Preferences prefs) {
		super(prefs);
	}

	@Override
	protected final void postInitialize() {

		// layer = hud.addLayer(consoleLayer());
		if (console == null) {

			console = new Console(screen);
			console.onChange(evt -> {
				try {
					command(evt.getNewValue());
				} catch (Exception e) {
					LOG.log(Level.SEVERE, "Failed to execute command.", e);
					if (e.getMessage() == null)
						outputError(e.getClass().getName());
					else
						outputError(e.getMessage());
				}
			});
			console.setHideOnLoseFocus(autoHide);
			console.setPriority(ZPriority.POPUP);
			console.setExecuteKey(KeyInput.KEY_RETURN);
			screen.attachElement(console);
			screen.addScreenListener(screenEventListener = new ScreenEventListener() {

				@Override
				public void onScreenEvent(ScreenEvent evt) {
					console.setWidth(evt.getSource().getWidth());

				}
			});
		}
		// console.setHideOnLoseFocus(true);

		ServiceLoader<ConsoleCommand> loader = ServiceLoader.load(ConsoleCommand.class);
		loader.forEach(cmd -> {
			cmd.init(this);
			String[] names = cmd.getClass().getAnnotation(Command.class).names();
			for (String n : names)
				registerCommand(n, cmd);
		});

		output("Iceshell. Type /help");

		if (showOnInit) {
			show();
		}
	}

	public boolean isAutoHide() {
		return autoHide;
	}

	public void setAutoHide(boolean autoHide) {
		this.autoHide = autoHide;
		if (console != null)
			console.setHideOnLoseFocus(autoHide);
	}

	public void registerCommand(String cmdName, ConsoleCommand cmd) {
		LOG.info(String.format("Registering comment %s (%s)", cmdName, cmd.getDescription()));
		commands.put(cmdName, cmd);
	}

	// if (visible && !console.getIsVisible()) {
	// parent.unregisterAllInput();
	// gameInputRegistered = false;
	// inputManager.addMapping(MAPPING_CLOSE_CONSOLE, new
	// KeyTrigger(KeyInput.KEY_ESCAPE));
	// inputManager.addListener(actionListener, MAPPING_CLOSE_CONSOLE);
	// console.showWithEffect();
	// } else if (!visible && console != null && console.getIsVisible()) {
	// inputManager.deleteMapping(MAPPING_CLOSE_CONSOLE);
	// inputManager.removeListener(actionListener);
	// if (!gameInputRegistered) {
	// parent.registerAllInput();
	// gameInputRegistered = true;
	// }
	// console.hideWithEffect();
	// }
	// }
	public boolean isVisible() {
		return console.isVisible();
	}

	@Override
	protected final void onCleanup() {
		screen.removeScreenListener(screenEventListener);
		console.hide();
		showOnInit = false;
	}

	private String[] parseArgs(String text) {
		List<String> args = new ArrayList<>();
		StringBuilder bui = new StringBuilder();
		boolean inQuotes = false;
		boolean escaped = false;
		for (char c : text.toCharArray()) {
			if (c == '"') {
				if (escaped) {
					bui.append(c);
				}
				inQuotes = !inQuotes;
			} else if (c == '\\') {
				escaped = !escaped;
				if (!escaped) {
					bui.append(c);
				}
			} else if ((c == ' ' || c == '\t') && !inQuotes) {
				if (bui.length() > 0) {
					args.add(bui.toString());
					bui.setLength(0);
				}
			} else {
				bui.append(c);
			}
		}
		if (bui.length() > 0) {
			args.add(bui.toString());
		}

		return args.toArray(new String[0]);
	}

	public void command(String text) {
		while (text.startsWith("/")) {
			text = text.substring(1);
		}
		String[] args = parseArgs(text);
		if (args.length > 0) {
			CommandLineParser parser = new GnuParser();
			try {
				// parse the command line arguments
				ConsoleCommand cmd = commands.get(args[0]);
				if (cmd == null) {
					outputError(String.format("No such command as '%s'.", args[0]));
				} else {
					String[] rem = new String[args.length - 1];
					System.arraycopy(args, 1, rem, 0, rem.length);
					CommandLine line = parser.parse(cmd.getOptions(), rem);
					if (line == null) {
						throw new IllegalArgumentException("No command '" + text + "'");
					}
					cmd.execute(args[0], line);
				}
			} catch (ParseException exp) {
				outputError(String.format("Parsing command failed. %s", exp.getMessage()));
			}
		}
	}

	public void clearText() {
		console.clearInput();
	}

	void checkHide() {
		if (showOnce) {
			hide();
		}
	}

	public void show() {
		showOnce = false;
		if (console == null) {
			showOnInit = true;
		} else {
			console.show();
		}
	}

	public void showForOneCommand() {
		showOnce = true;
		console.show();
	}

	public void hide() {
		clearText();
		if (console == null) {
			showOnInit = false;
		} else {
			console.hide();
		}
	}

	public void output(String text) {
		console.output(text);
	}

	public void outputError(String text) {
		console.outputError(text);
	}

	public Map<String, ConsoleCommand> getCommands() {
		return commands;
	}

	public static void toggle(Preferences prefs, AppStateManager stateManager) {

		ConsoleAppState state = stateManager.getState(ConsoleAppState.class);
		if (state == null) {
			state = new ConsoleAppState(prefs);
			state.show();
			stateManager.attach(state);

		} else {
			if (state.isVisible())
				state.hide();
			else
				state.show();
		}

	}
}
