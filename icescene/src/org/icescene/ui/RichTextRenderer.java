package org.icescene.ui;

import java.net.URI;
import java.util.logging.Level;

import org.icelib.XDesktop;
import org.icescene.HUDMessageAppState;
import org.icescene.help.HelpUserAgent;

import com.jme3.math.ColorRGBA;

import icetone.core.ElementManager;
import icetone.xhtml.TGGXHTMLRenderer;

public class RichTextRenderer extends TGGXHTMLRenderer {
	
	public RichTextRenderer(ElementManager screen) {
		super(screen, new HelpUserAgent(screen));
//		getElementMaterial().setColor("Color", ColorRGBA.Black);
//		innerBounds.getElementMaterial().setColor("Color", ColorRGBA.Black);
//		scrollableArea.getElementMaterial().setColor("Color", ColorRGBA.Black);
//		innerBounds.setTextPadding(0);
//		setTextPadding(0);
	}

	@Override
	protected void linkClicked(Link link) {
		String uri = link.getUri();
		if (uri.startsWith("http://") || uri.startsWith("https://") || uri.startsWith("mailto://")) {
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
				linkClicked(l);
			} else {
				super.linkClicked(link);
			}
		}
	}
}