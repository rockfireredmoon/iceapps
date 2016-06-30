package org.icescene.tools;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.icescene.scene.AnimSprite;

import com.jme3.asset.AssetNotFoundException;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.extras.DragElement;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.layout.LUtil;
import icetone.core.utils.UIDUtil;

public abstract class AbstractDraggable extends DragElement {

	private static final Logger LOG = Logger.getLogger(AbstractDraggable.class.getName());
	private DragContext dragContext;
	private Vector2f maxDimensions;
	private Vector2f prefDimensions;
	private Element icon2;
	private Element icon1;
	private AnimSprite activeAnim;

	public AbstractDraggable(DragContext dragContext, ElementManager screen, Vector2f dimensions, Vector4f resizeBorders,
			String img1, String img2) {
		super(screen, Vector2f.ZERO, dimensions, resizeBorders, null);
		init(dragContext, img1, img2);
	}

	public AbstractDraggable(DragContext dragContext, ElementManager screen, String img1, String img2) {
		super(screen, Vector4f.ZERO, null);
		init(dragContext, img1, img2);
	}

	public AbstractDraggable(DragContext dragContext, ElementManager screen, String UID, String img1, String img2) {
		super(screen, UID, Vector2f.ZERO, LUtil.LAYOUT_SIZE, Vector4f.ZERO, null);
		init(dragContext, img1, img2);
	}

	public AbstractDraggable(DragContext dragContext, ElementManager screen, String UID, Vector2f dimensions,
			Vector4f resizeBorders, String img1, String img2) {
		super(screen, UID, Vector2f.ZERO, dimensions, resizeBorders, null);
		init(dragContext, img1, img2);
	}

	private void init(DragContext dragContext, String img1, String img2) {
		this.dragContext = dragContext;

		if (img2 != null) {
			try {
				icon2 = new Element(screen, UIDUtil.getUID(), LUtil.LAYOUT_SIZE, Vector4f.ZERO, img2);
				icon2.setIgnoreMouse(true);
				addChild(icon2);
			} catch (AssetNotFoundException anfe) {
				LOG.log(Level.SEVERE, String.format("Failed to locate icon %s.", img1));
			}
		}
		if (img1 != null) {
			try {
				icon1 = new Element(screen, UIDUtil.getUID(), LUtil.LAYOUT_SIZE, Vector4f.ZERO, img1);
				icon1.setIgnoreMouse(true);
				addChild(icon1);
			} catch (AssetNotFoundException anfe) {
				LOG.log(Level.SEVERE, String.format("Failed to locate icon %s.", img1));
			}
		}
		setUseSpringBack(true);
		setUseLockToDropElementEffect(true);
		setUseSpringBackEffect(true);
	}

	public void setActive(boolean active) {
		if (active && activeAnim == null) {
			activeAnim = new AnimSprite(app.getAssetManager(), "Interface/Styles/Gold/Button/ability-highlight.png", 32, 32);
			activeAnim.start(0.1f);
			attachChild(activeAnim);
		} else if (!active && activeAnim != null) {
			activeAnim.removeFromParent();
		}
	}

	@Override
	public void onDragStart(MouseButtonEvent mbe) {
		dragContext.setDragStart(getPosition().clone());
		dragContext.start();
	}

	@Override
	public boolean onDragEnd(MouseButtonEvent mbe, Element elmnt) {
		dragContext.stop();
		Vector2f pos = getPosition();
		if (dragContext.isCancelled()) {
			LOG.warning("Drag was cancelled because an inventory refresh came in");
			return false;
		} else if (pos.x == dragContext.getDragStart().x && pos.y == dragContext.getDragStart().y) {
			// A click
			doOnClick(mbe);
			return false;
		} else {
			return doOnDragEnd(mbe, elmnt);
		}
	}

	public Vector2f getMaxDimensions() {
		return maxDimensions;
	}

	public Vector2f getPreferredDimensions() {
		return prefDimensions;
	}

	public void setMaxDimensions(Vector2f maxDimensions) {
		this.maxDimensions = maxDimensions;
	}

	public void setPreferredDimensions(Vector2f prefDimensions) {
		this.prefDimensions = prefDimensions;
	}

	protected abstract boolean doOnClick(MouseButtonEvent evt);

	protected abstract boolean doOnDragEnd(MouseButtonEvent mbe, Element elmnt);
}
