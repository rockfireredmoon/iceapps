package org.icescene.help;

import java.net.URI;
import java.util.logging.Level;

import org.icelib.XDesktop;
import org.icescene.HUDMessageAppState;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.extras.SplitPanel;
import icetone.core.ElementManager;
import icetone.core.layout.LUtil;
import icetone.core.utils.UIDUtil;
import icetone.xhtml.TGGUserAgent;
import icetone.xhtml.TGGXHTMLRenderer;

public class HelpPanel extends SplitPanel {

	private final TGGXHTMLRenderer index;
	private final TGGXHTMLRenderer xhtml;

	public HelpPanel(ElementManager screen) {
		this(screen, "/Interface/Help/help.xhtml", "/Interface/Help/index.xhtml");
	}

	public HelpPanel(ElementManager screen, String helpUri, String helpIndexUri) {
		super(screen, Vector2f.ZERO, LUtil.LAYOUT_SIZE, Vector4f.ZERO, null, Orientation.HORIZONTAL);

		index = new HelpRenderer(screen);
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

	class HelpRenderer extends TGGXHTMLRenderer {

		public HelpRenderer(ElementManager screen) {
			super(screen, UIDUtil.getUID(), Vector2f.ZERO, LUtil.LAYOUT_SIZE, Vector4f.ZERO, null, new HelpUserAgent(screen));
			getElementMaterial().setColor("Color", ColorRGBA.Black);
			innerBounds.getElementMaterial().setColor("Color", ColorRGBA.Black);
			scrollableArea.getElementMaterial().setColor("Color", ColorRGBA.Black);
			innerBounds.setTextPadding(0);
			setTextPadding(0);
		}

		@Override
		protected void linkClicked(Link link) {
			String uri = link.getUri();
			if (uri.startsWith("http://") || uri.startsWith("https://")) {
				// Open external links in the OS browser
				try {
					XDesktop.getDesktop().browse(new URI(uri));
				} catch (Exception e) {
					screen.getApplication().getStateManager().getState(HUDMessageAppState.class).message(Level.SEVERE,
							"Failed to open browser.", e);
				}
			} else {
				if ("helpMain".equals(link.getTarget())) {
					Link l = new Link(link.getUri());
					((HelpRenderer) xhtml).linkClicked(l);
				} else {
					super.linkClicked(link);
				}
			}
		}
	}

	class HelpUserAgent extends TGGUserAgent {

		public HelpUserAgent(ElementManager screen) {
			super(screen);
		}
	}
}
