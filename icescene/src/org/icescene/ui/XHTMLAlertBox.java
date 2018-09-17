package org.icescene.ui;

import org.iceui.controls.ElementStyle;

import com.jme3.input.event.MouseButtonEvent;

import icetone.controls.text.XHTMLLabel;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.layout.ScreenLayoutConstraints;
import icetone.extras.windows.ButtonWindow;
import icetone.extras.windows.AlertBox.AlertType;
import icetone.xhtml.XHTMLDisplay;
import icetone.xhtml.XHTMLUserAgent;

public abstract class XHTMLAlertBox extends ButtonWindow<XHTMLDisplay> {

	public static XHTMLAlertBox alert(BaseScreen screen, String title, String text, final AlertType alert) {

		final XHTMLAlertBox dialog = new XHTMLAlertBox(screen, true) {
			{
				setStyleClass("large");
			}

			@Override
			public void onButtonOkPressed(MouseButtonEvent evt, boolean toggled) {
				hide();
			}

			@Override
			public void createButtons(BaseElement buttons) {
				if (!alert.equals(AlertType.PROGRESS)) {
					super.createButtons(buttons);
				}
			}
		};
		dialog.addStyleClass(alert.name().toLowerCase());
		dialog.setDestroyOnHide(true);
		ElementStyle.errorColor(dialog.getDragBar());
		dialog.setWindowTitle(title);
		if (!alert.equals(AlertType.PROGRESS)) {
			dialog.setButtonOkText("Close");
		}
		dialog.contentArea.setDocumentFromString(XHTMLLabel.wrapTextInXHTML(text, dialog.getFontColor()),
				"alert://dialog.html");
		// dialog.setMsg(text);
		dialog.setManagedHint(true);
		dialog.setResizable(false);
		dialog.setMovable(false);
		dialog.setModal(true);
		screen.showElement(dialog, ScreenLayoutConstraints.center);
		return dialog;
	}

	/**
	 * Creates a new instance of the AlertBox control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param UID
	 *            A unique String identifier for the Element
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 */
	public XHTMLAlertBox(BaseScreen screen, boolean closeable) {
		super(screen, closeable);
	}

	@Override
	protected XHTMLDisplay createContent() {
		XHTMLDisplay tggxhtmlRenderer = new XHTMLDisplay(screen, new XHTMLUserAgent(screen));
		// tggxhtmlRenderer.setMinDimensions(new Vector2f(300, 1));
		// tggxhtmlRenderer.setPreferredDimensions(new Vector2f(300, 32));
		// tggxhtmlRenderer.setMaxDimensions(new Vector2f(600, 400));
		return tggxhtmlRenderer;
	}

	/**
	 * Sets the XHTML message to display.
	 *
	 * @param text
	 *            String The message
	 */
	public void setMsg(String text) {
		contentArea.setDocumentFromString(XHTMLLabel.wrapTextInXHTML(text, getFontColor()), "alert://dialog.html");
		sizeToContent();
	}
}
