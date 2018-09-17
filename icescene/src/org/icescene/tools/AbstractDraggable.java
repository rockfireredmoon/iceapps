package org.icescene.tools;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.icescene.scene.AnimSprite;

import com.jme3.asset.AssetNotFoundException;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.extras.DragElement;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Size;
import icetone.core.ToolKit;
import icetone.core.layout.BasicLayout;

public abstract class AbstractDraggable extends DragElement {

	private static final Logger LOG = Logger.getLogger(AbstractDraggable.class.getName());
	private BaseElement icon2;
	private BaseElement icon1;
	private AnimSprite activeAnim;

	public AbstractDraggable(DragContext dragContext, BaseScreen screen, String img1, String img2) {
		super(screen);
		init(dragContext, img1, img2);
	}

	public AbstractDraggable(DragContext dragContext, BaseScreen screen, String styleId, String img1,
			String img2) {
		super(screen, styleId);
		init(dragContext, img1, img2);
	}

	public AbstractDraggable(DragContext dragContext, BaseScreen screen, String styleId, Size dimensions,
			Vector4f resizeBorders, String img1, String img2) {
		super(screen, styleId);
		init(dragContext, img1, img2);
	}

	private void init(DragContext dragContext, String img1, String img2) {

		if (img2 != null) {
			try {
				icon2 = new BaseElement(screen, img2);
				addElement(icon2);
			} catch (AssetNotFoundException anfe) {
				LOG.log(Level.SEVERE, String.format("Failed to locate icon %s.", img1));
			}
		}
		if (img1 != null) {
			try {
				icon1 = new BaseElement(screen, img1);
				addElement(icon1);
			} catch (AssetNotFoundException anfe) {
				LOG.log(Level.SEVERE, String.format("Failed to locate icon %s.", img1));
			}
		}
		setLayoutManager(new BasicLayout());
		setUseSpringBack(true);
		setUseLockToDropElementEffect(true);
		setUseSpringBackEffect(true);
		onStart(evt -> {
			dragContext.setDragStart(getPixelPosition().clone());
			dragContext.start();
		});
		onEnd(evt -> {
			dragContext.stop();
			Vector2f pos = getPixelPosition();
			if (dragContext.isCancelled()) {
				LOG.warning("Drag was cancelled because an inventory refresh came in");
			} else if (dragContext.getDragStart() != null && pos.x == dragContext.getDragStart().x
					&& pos.y == dragContext.getDragStart().y) {
				// A click
				doOnClick(evt);
			} else {
				if (doOnDragEnd(evt, evt.getTarget()))
					evt.setConsumed();
			}
		});
	}

	public void setActive(boolean active) {
		if (active && activeAnim == null) {
			activeAnim = new AnimSprite(ToolKit.get().getApplication().getAssetManager(),
					"Interface/Styles/Gold/Button/ability-highlight.png", 32, 32);
			activeAnim.start(0.1f);
			attachChild(activeAnim);
		} else if (!active && activeAnim != null) {
			activeAnim.removeFromParent();
		}
	}

	protected abstract boolean doOnClick(MouseButtonEvent evt);

	protected abstract boolean doOnDragEnd(MouseButtonEvent mbe, BaseElement elmnt);
}
