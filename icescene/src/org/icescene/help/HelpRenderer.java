package org.icescene.help;

import java.net.URI;
import java.util.logging.Level;

import org.icelib.XDesktop;
import org.icescene.HUDMessageAppState;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.core.ElementManager;
import icetone.core.layout.LUtil;
import icetone.core.utils.UIDUtil;
import icetone.xhtml.TGGXHTMLRenderer;

public class HelpRenderer extends TGGXHTMLRenderer {

	public HelpRenderer(ElementManager screen) {
		super(screen, UIDUtil.getUID(), Vector2f.ZERO, LUtil.LAYOUT_SIZE, Vector4f.ZERO, null,
				new HelpUserAgent(screen));
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
			super.linkClicked(link);
		}
	}
}