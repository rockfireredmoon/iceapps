package org.icescene.help;

import java.net.URI;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.extras.SplitPanel;
import icetone.core.ElementManager;
import icetone.core.layout.LUtil;
import icetone.xhtml.TGGXHTMLRenderer;

public class HelpPanel extends SplitPanel {

	private final TGGXHTMLRenderer index;
	private final TGGXHTMLRenderer xhtml;

	public HelpPanel(ElementManager screen) {
		this(screen, "/Interface/Help/help.xhtml", "/Interface/Help/index.xhtml");
	}

	public HelpPanel(ElementManager screen, String helpUri, String helpIndexUri) {
		super(screen, Vector2f.ZERO, LUtil.LAYOUT_SIZE, Vector4f.ZERO, null, Orientation.HORIZONTAL);

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
		index.setUseContentPaging(true);
		index.setDocument(helpIndexUri);
		index.scrollToTop();

		xhtml = new HelpRenderer(screen);
		xhtml.setUseContentPaging(true);
		xhtml.setDocument(helpUri);

		setDefaultDividerLocationRatio(0.35f);
		setLeftOrTop(index);
		setRightOrBottom(xhtml);
		getElementMaterial().setColor("Color", ColorRGBA.Black);
		setIgnoreGlobalAlpha(true);
	}
}
