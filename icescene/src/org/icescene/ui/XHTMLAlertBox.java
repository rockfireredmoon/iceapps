package org.icescene.ui;

import org.iceui.UIConstants;
import org.iceui.controls.FancyAlertBox.AlertType;
import org.iceui.controls.FancyButtonWindow;
import org.iceui.controls.FancyWindow;
import org.iceui.controls.UIUtil;
import org.xhtmlrenderer.simple.NoNamespaceHandler;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.buttons.ButtonAdapter;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.Screen;
import icetone.core.layout.LUtil;
import icetone.core.layout.LayoutManager;
import icetone.core.utils.UIDUtil;
import icetone.xhtml.TGGUserAgent;
import icetone.xhtml.TGGXHTMLRenderer;

public abstract class XHTMLAlertBox extends FancyButtonWindow<TGGXHTMLRenderer> {

	private ButtonAdapter btnOk;

	public static XHTMLAlertBox alert(ElementManager screen, String title, String text, final AlertType alert) {

		final XHTMLAlertBox dialog = new XHTMLAlertBox(screen, new Vector2f(15, 15), FancyWindow.Size.LARGE, true) {
			@Override
			public void onButtonOkPressed(MouseButtonEvent evt, boolean toggled) {
				hideWindow();
			}

			@Override
			public void createButtons(Element buttons) {
				if (!alert.equals(AlertType.PROGRESS)) {
					super.createButtons(buttons);
				}
			}
		};
		dialog.setDestroyOnHide(true);
		dialog.getDragBar().setFontColor(screen.getStyle("Common").getColorRGBA("errorColor"));
		dialog.setWindowTitle(title);
		if (!alert.equals(AlertType.PROGRESS)) {
			dialog.setButtonOkText("Close");
		}
		dialog.setMsg(text);
		dialog.setManagedHint(true);
		dialog.setIsResizable(false);
		dialog.setIsMovable(false);
		if (screen.getUseUIAudio()) {
			switch (alert) {
			case ERROR:
				((Screen) screen).playAudioNode(UIConstants.SOUND_WARNING, 1);
				break;
			}
		}
		UIUtil.center(screen, dialog);
		screen.addElement(dialog, null, true);
		dialog.showAsModal(true);
		return dialog;
	}

	/**
	 * Creates a new instance of the AlertBox control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 */
	public XHTMLAlertBox(ElementManager screen, Vector2f position, Size size, boolean closeable) {
		this(screen, UIDUtil.getUID(), position, LUtil.LAYOUT_SIZE,
				screen.getStyle(size.toStyleName()).getVector4f("resizeBorders"),
				screen.getStyle(size.toStyleName()).getString("defaultImg"), size, closeable);
	}

	/**
	 * Creates a new instance of the AlertBox control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 */
	public XHTMLAlertBox(ElementManager screen, Vector2f position, Vector2f dimensions, Size size, boolean closeable) {
		this(screen, UIDUtil.getUID(), position, dimensions, screen.getStyle(size.toStyleName()).getVector4f("resizeBorders"),
				screen.getStyle(size.toStyleName()).getString("defaultImg"), size, closeable);
	}

	/**
	 * Creates a new instance of the AlertBox control
	 *
	 * @param screen
	 *            The screen control the Element is to be added to
	 * @param position
	 *            A Vector2f containing the x/y position of the Element
	 * @param dimensions
	 *            A Vector2f containing the width/height dimensions of the
	 *            Element
	 * @param resizeBorders
	 *            A Vector4f containg the border information used when resizing
	 *            the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg
	 *            The default image to use for the AlertBox window
	 */
	public XHTMLAlertBox(ElementManager screen, Vector2f position, Vector2f dimensions, Vector4f resizeBorders, String defaultImg,
			Size size, boolean closeable) {
		this(screen, UIDUtil.getUID(), position, dimensions, resizeBorders, defaultImg, size, closeable);
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
	 */
	public XHTMLAlertBox(ElementManager screen, String UID, Vector2f position, Size size, boolean closeable) {
		this(screen, UID, position, LUtil.LAYOUT_SIZE, screen.getStyle(size.toStyleName()).getVector4f("resizeBorders"),
				screen.getStyle(size.toStyleName()).getString("defaultImg"), size, closeable);
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
	public XHTMLAlertBox(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Size size, boolean closeable) {
		this(screen, UID, position, dimensions, screen.getStyle(size.toStyleName()).getVector4f("resizeBorders"),
				screen.getStyle(size.toStyleName()).getString("defaultImg"), size, closeable);
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
	 * @param resizeBorders
	 *            A Vector4f containg the border information used when resizing
	 *            the default image (x = N, y = W, z = E, w = S)
	 * @param defaultImg
	 *            The default image to use for the AlertBox window
	 */
	public XHTMLAlertBox(ElementManager screen, String UID, Vector2f position, Vector2f dimensions, Vector4f resizeBorders,
			String defaultImg, Size size, boolean closeable) {
		super(screen, UID, position, dimensions, resizeBorders, defaultImg, size, closeable);
	}

	@Override
	protected TGGXHTMLRenderer createContent() {
		TGGXHTMLRenderer tggxhtmlRenderer = new TGGXHTMLRenderer(screen, Vector4f.ZERO, null, new TGGUserAgent(screen));
		tggxhtmlRenderer.setMinDimensions(new Vector2f(300, 1));
//		tggxhtmlRenderer.setPreferredDimensions(new Vector2f(300, 32));
//		tggxhtmlRenderer.setMaxDimensions(new Vector2f(600, 400));
		return tggxhtmlRenderer;
	}

	/**
	 * Sets the XHTML message to display.
	 *
	 * @param text
	 *            String The message
	 */
	public void setMsg(String text) {

		final StringBuilder bui = new StringBuilder();
		bui.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		bui.append("<!DOCTYPE html>\n");
		bui.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">\n");
		bui.append("<body style=\"text-align: center;\">\n");
		bui.append(text);
		bui.append("</body>\n");
		bui.append("</html>\n");
		contentArea.setDocumentFromString(bui.toString(), "alert://dialog.html");
		sizeToContent();
	}
}
