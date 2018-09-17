package org.icescene.help;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.containers.SplitPanel;
import icetone.core.BaseScreen;
import icetone.core.Orientation;
import icetone.core.layout.GUIConstants;
import icetone.xhtml.XHTMLDisplay;

public class HelpPanel extends SplitPanel {

	private final XHTMLDisplay index;
	private final XHTMLDisplay xhtml;

	public HelpPanel(BaseScreen screen) {
		this(screen, "/Interface/Help/help.xhtml", "/Interface/Help/index.xhtml");
	}

	public HelpPanel(BaseScreen screen, String helpUri, String helpIndexUri) {
		super(screen, Orientation.HORIZONTAL);

		index = new HelpRenderer(screen) {
			@Override
			protected void linkClicked(Link link) {
				if ("helpMain".equals(link.getTarget())) {
					xhtml.setDocumentRelative(link.getUri());
				} else {
					super.linkClicked(link);
				}
			}
		};
		index.setDocument(helpIndexUri);
		index.scrollToTop();

		xhtml = new HelpRenderer(screen);
		xhtml.setDocument(helpUri);

		setDefaultDividerLocationRatio(0.35f);
		setLeftOrTop(index);
		setRightOrBottom(xhtml);
		getElementMaterial().setColor("Color", ColorRGBA.Black);
		setIgnoreGlobalAlpha(true);
	}
}
