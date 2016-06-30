package org.icescene.console;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.ParseException;
import org.icescene.IcemoonAppState;
import org.icescene.IcesceneApp.AppListener;
import org.iceui.XConsole;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;

import com.jme3.input.KeyInput;

import icetone.core.Element.ZPriority;

public class ConsoleAppState extends IcemoonAppState<IcemoonAppState<?>> {

	private static final Logger LOG = Logger.getLogger(ConsoleAppState.class.getName());
	private static XConsole console;
	private boolean showOnce;
	private Map<String, ConsoleCommand> commands = new LinkedHashMap<String, ConsoleCommand>();
	private AppListener appListener;
	private boolean showOnInit;

	public ConsoleAppState(Preferences prefs) {
		super(prefs);
	}

	@Override
	protected final void postInitialize() {
		app.addListener(appListener = new AppListener() {

			@Override
			public void reshape(int w, int h) {
				console.setWidth(w);
				console.setPosition(0, h - console.getHeight());
			}
		});

		// layer = hud.addLayer(consoleLayer());
		if (console == null) {
			console = new XConsole(screen, "Console", 400) {
				@Override
				public void onCommand(String command) {
					try {
						command(command);
					} catch (Exception e) {
						LOG.log(Level.SEVERE, "Failed to execute command.", e);
						if(e.getMessage() == null)  
							outputError(e.getClass().getName());
						else
							outputError(e.getMessage());
					}
				}
			};
			console.setPriority(ZPriority.POPUP);
			console.setExecuteKey(KeyInput.KEY_RETURN);
			screen.addElement(console, null, true);
		}
		// console.setHideOnLoseFocus(true);

		Reflections reflections = new Reflections(ClasspathHelper.forJavaClassPath(), new SubTypesScanner(),
				new TypeAnnotationsScanner());
		Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Command.class);
		for (Class<?> a : annotated) {
			try {
				ConsoleCommand cmd = (ConsoleCommand) a.newInstance();
				cmd.init(this);
				String[] names = a.getAnnotation(Command.class).names();
				for (String n : names) {
					registerCommand(n, cmd);
				}
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "Failed to register command.", e);
			}
		}

		output("Iceshell. Type /help");

		if (showOnInit) {
			show();
		}
	}

	public void registerCommand(String cmdName, ConsoleCommand cmd) {
		commands.put(cmdName, cmd);
	}

	// public void setVisible(boolean visible) {
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
		return console.getIsVisible();
	}

	@Override
	protected final void onCleanup() {
		app.removeListener(appListener);
		if (console.getIsVisible()) {
			console.hideWithEffect();
		}
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
			console.showWithEffect();
		}
	}

	public void showForOneCommand() {
		showOnce = true;
		console.showWithEffect();
	}

	public void hide() {
		clearText();
		if (console == null) {
			showOnInit = false;
		} else {
			console.hideWithEffect();
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
}
