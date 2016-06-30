package org.icescene;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import org.icescene.ui.XHTMLAlertBox;
import org.iceui.UIConstants;
import org.iceui.controls.ElementStyle;
import org.iceui.controls.FancyAlertBox.AlertType;
import org.iceui.controls.UIUtil;
import org.iceui.controls.XScreen;
import org.iceui.effects.EffectHelper;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.LineWrapMode;
import com.jme3.math.Vector2f;

import icetone.controls.text.Label;
import icetone.core.Container;
import icetone.core.Element;
import icetone.core.Screen;
import icetone.core.Element.ZPriority;
import icetone.effects.Effect;

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

	private IcesceneApp app;
	private List<Message> messages = new ArrayList<Message>();
	private Screen screen;
	private Element layer;
	private List<Listener> listeners = new ArrayList<Listener>();
	private XHTMLAlertBox popupWindow;

	public interface Listener {
		void message(final Level level, final String message, final Exception exception);
	}

	class Message {

		Level level;
		String message;
		Exception exception;
		Label label;
		float time = 0;

		Message(Level level, String message, Exception exception, Label label) {
			this.level = level;
			this.message = message;
			this.exception = exception;
			this.label = label;
		}
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.add(listener);
	}

	public void message(Level level, String message) {
		message(level, message, null);
	}

	public void message(final Level level, final String message, final Exception exception) {
		if (!app.isSceneThread()) {
			app.enqueue(new Callable<Void>() {
				public Void call() throws Exception {
					message(level, message, exception);
					return null;
				}
			});
			return;
		}

		// Create new message and add it to screen
		Label newLabel = new Label(getMessageText(message, exception), screen);
		ElementStyle.mediumOutline(screen, newLabel);
		if (Level.SEVERE.equals(level)) {
			newLabel.setFontColor(screen.getStyle("Common").getColorRGBA("errorColor"));
		} else if (Level.WARNING.equals(level)) {
			newLabel.setFontColor(screen.getStyle("Common").getColorRGBA("warningColor"));
		} else if (Level.INFO.equals(level)) {
			newLabel.setFontColor(screen.getStyle("Common").getColorRGBA("infoColor"));
		}
		newLabel.setTextAlign(BitmapFont.Align.Center);
		newLabel.setTextWrap(LineWrapMode.Word);
		Vector2f pref = new Vector2f(screen.getWidth(), screen.getStyle("Label").getVector2f("defaultSize").y);
		Vector2f pos = new Vector2f(0, screen.getHeight() * 0.25f);
		newLabel.setPosition(pos);
		newLabel.setDimensions(pref);
		layer.addChild(newLabel);
		UIUtil.toFront(layer);
		messages.add(new Message(level, message, exception, newLabel));

		// Shift up any existing messages
		for (Message m : messages) {
			m.label.setY(m.label.getY() + pref.y);
			// m.time = 0;
		}

		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).message(level, message, exception);
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
			popupWindow.hideWindow();
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
		XScreen screen = app.getScreen();
		popupWindow = XHTMLAlertBox.alert(screen, "Alert", message, AlertType.INFORMATION);
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		this.app = (IcesceneApp) app;
		this.screen = this.app.getScreen();

		// Layer
		layer = new Container(screen);
		this.app.getLayers(ZPriority.FOREGROUND).addChild(layer);
	}

	@Override
	public void cleanup() {
		super.cleanup();
		this.app.getLayers(ZPriority.FOREGROUND).removeChild(layer);
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
					new EffectHelper()
							.effect(m.label, Effect.EffectType.FadeOut, Effect.EffectEvent.Hide, UIConstants.UI_EFFECT_TIME * 5)
							.setDestroyOnHide(true);
				}
			}
		}
	}

	private String getMessageText(String text, Exception exception) {
		return exception == null ? text : text + " " + exception.getMessage();
	}
}
