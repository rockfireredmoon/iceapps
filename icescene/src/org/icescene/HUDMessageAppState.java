package org.icescene;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import org.icescene.ui.XHTMLAlertBox;
import org.iceui.controls.UIUtil;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector2f;

import icetone.controls.buttons.Button;
import icetone.controls.text.Label;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.StyledContainer;
import icetone.core.Element;
import icetone.core.ZPriority;
import icetone.core.layout.mig.MigLayout;
import icetone.extras.windows.AlertBox.AlertType;

/**
 * Displays error, warning and information messages in the upper third of the
 * screen. These messages will be visible for a short amout of time, and them
 * will be scrolled offscreen.
 * <p>
 * When a new message comes in before previous messages are hidden, any current
 * messages are shift up to make room. Their timers are also reset so all
 * visible messages will scroll of at the same time.
 * <p>
 * If the {@link ChatAppState} is active, messges will be logged there.
 */
public class HUDMessageAppState extends AbstractAppState {

	public static final int MAX_MESSAGES_ON_SCREEN = 10;

	public enum Channel {
		INFORMATION, WARNING, ERROR, BROADCAST;

		public static Channel levelToChannel(Level level) {
			if (level.equals(Level.SEVERE)) {
				return Channel.ERROR;
			} else if (level.equals(Level.WARNING)) {
				return Channel.WARNING;
			}
			return Channel.INFORMATION;
		}

		public String getStyleClass() {
			switch (this) {
			case ERROR:
				return "color-error";
			case WARNING:
				return "color-warning";
			case BROADCAST:
				return "color-success";
			default:
				return "color-information";
			}
		}
	}

	private IcesceneApp app;
	private List<Message> messages = new ArrayList<Message>();
	private BaseScreen screen;
	private BaseElement layer;
	private List<Listener> listeners = new ArrayList<Listener>();
	private XHTMLAlertBox popupWindow;

	public interface Listener {
		void message(Channel channel, final String message, final Exception exception);
	}

	class Message extends Element {

		String message;
		Exception exception;
		float time = 0;

		Message(Channel channel, String message, Exception exception) {
			this.message = message;
			this.exception = exception;
			setStyleClass("hud-message hud-message-" + channel.name().toLowerCase());
			setLayoutManager(new MigLayout(screen, "ins 0, fill", "push[shrink 0][]push"));
			addElement(new Button(screen) {
				{
					setStyleClass("hud-message-action");
				}
			});
			addElement(new Label(getMessageText(), screen) {
				{
					setStyleClass("message-text " + channel.getStyleClass());
				}
			});
			setDestroyOnHide(true);
		}

		private String getMessageText() {
			return exception == null ? message : message + " " + exception.getMessage();
		}
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.add(listener);
	}

	@Deprecated
	public void message(Level level, String message) {
		message(level, message, null);
	}

	@Deprecated
	public void message(final Level level, final String message, final Exception exception) {
		message(Channel.levelToChannel(level), message, exception);
	}

	public void message(final Channel channel, final String message) {
		message(channel, message, null);
	}

	public void message(final Channel channel, final String message, final Exception exception) {
		if (!app.isSceneThread()) {
			app.enqueue(new Callable<Void>() {
				public Void call() throws Exception {
					message(channel, message, exception);
					return null;
				}
			});
			return;
		}
		Message msg = new Message(channel, message, exception);
		System.out.println("New message: " + message + " / "  +channel);

		// Create new message and add it to screen
		Vector2f pos = new Vector2f(0, screen.getHeight() * 0.33f);
		msg.setPosition(pos);
		Vector2f pref = msg.calcPreferredSize();
		pref.x = screen.getWidth();
		msg.setDimensions(pref);
		layer.addElement(msg);
		messages.add(msg);

		// Shift up any existing messages
		for (Iterator<Message> mIt = messages.iterator(); mIt.hasNext();) {
			Message m = mIt.next();
			float ny = m.getY() - pref.y;
			if(ny > 0)
				m.setY(ny);
			else {
				mIt.remove();
				m.destroy();
			}
		}

		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).message(channel, message, exception);
		}
	}

	public void closePopup() {
		if (!app.isSceneThread()) {
			app.enqueue(new Callable<Void>() {
				public Void call() throws Exception {
					closePopup();
					return null;
				}
			});
			return;
		}
		if (popupWindow != null) {
			popupWindow.hide();
			popupWindow = null;
		}
	}

	public void popupMessage(final String message) {
		if (!app.isSceneThread()) {
			app.enqueue(new Callable<Void>() {
				public Void call() throws Exception {
					popupMessage(message);
					return null;
				}
			});
			return;
		}
		BaseScreen screen = app.getScreen();
		popupWindow = XHTMLAlertBox.alert(screen, "Alert", message, AlertType.INFORMATION);
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		this.app = (IcesceneApp) app;
		this.screen = this.app.getScreen();

		// Layer
		layer = new StyledContainer(screen);
		this.app.getLayers(ZPriority.FOREGROUND).addElement(layer);
	}

	@Override
	public void cleanup() {
		super.cleanup();
		this.app.getLayers(ZPriority.FOREGROUND).removeElement(layer);
	}

	@Override
	public void update(float tpf) {
		super.update(tpf);
		if (!messages.isEmpty()) {
			for (Iterator<Message> mIt = messages.iterator(); mIt.hasNext();) {
				Message m = mIt.next();
				m.time += tpf;
				if (m.time > SceneConstants.HUD_MESSAGE_TIMEOUT) {
					mIt.remove();
					m.hide();
				}
			}
		}
	}
}
