package org.icescene.tools;

import java.util.ArrayList;
import java.util.List;

import org.iceui.effects.EffectHelper;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.extras.Indicator;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.ToolTipProvider;
import icetone.core.layout.FillLayout;
import icetone.core.layout.XYLayoutManager;
import icetone.core.layout.mig.MigLayout;
import icetone.core.utils.UIDUtil;
import icetone.effects.Effect;
import icetone.style.Style;

/**
 * Abstract Component for the main tool area in game or build mode.
 */
public abstract class AbstractToolArea extends Element {

	protected final Element container;
	protected final Indicator barLayer;
	protected final ToolManager toolMgr;
	protected Style mainToolBarStyle;
	protected Element togglerLayer;
	protected Element graphicLayer;
	protected ButtonAdapter togglerRevealLayer;
	protected List<Element> toolbars = new ArrayList<Element>();
	protected Element overlayLayer;

	public AbstractToolArea(HudType hudType, ToolManager toolMgr, final ElementManager screen, String styleName,
			String quickBarPrefix, int noQuickBars) {
		// super(screen, UIDUtil.getUID(),
		// screen.getStyle(styleName).getVector2f("defaultSize"),
		// screen.getStyle(styleName)
		// .getVector4f("resizeBorders"),
		// screen.getStyle(styleName).getString("lowerImg"));
		super(screen, UIDUtil.getUID(), screen.getStyle(styleName).getVector2f("defaultSize"), Vector4f.ZERO, null);

		this.toolMgr = toolMgr;

		mainToolBarStyle = screen.getStyle(styleName);
		setMinDimensions(mainToolBarStyle.getVector2f("defaultSize"));

		// Container holds all the layers
		container = new Element(screen, UIDUtil.getUID(), mainToolBarStyle.getVector2f("defaultSize"),
				mainToolBarStyle.getVector4f("resizeBorders"), null);
		container.setLayoutManager(new XYLayoutManager());
		container.setIsMovable(false);
		container.setIsResizable(false);
		container.setIgnoreMouse(true);
		container.setAsContainerOnly();

		// The main graphic layer for the tool area
		graphicLayer = new Element(screen, UIDUtil.getUID(), screen.getStyle(styleName).getVector2f("defaultSize"),
				screen.getStyle(styleName).getVector4f("resizeBorders"), screen.getStyle(styleName).getString("lowerImg"));

		// The quickbar toggler layer
		togglerLayer = new Element(screen, name, mainToolBarStyle.getVector2f("quickBarTogglerPosition"),
				mainToolBarStyle.getVector2f("quickBarTogglerSize"), mainToolBarStyle.getVector4f("quickBarTogglerResizeBorders"),
				mainToolBarStyle.getString("quickBarTogglerImg"));
		togglerLayer.setLayoutManager(new MigLayout(screen, "ins 0, gap " + mainToolBarStyle.getFloat("quickBarTogglerButtonGap"),
				mainToolBarStyle.getVector2f("quickBarTogglerButtonsPosition").x + "[][][][][][][]",
				mainToolBarStyle.getVector2f("quickBarTogglerButtonsPosition").y + "[]"));
		for (int i = 1; i <= noQuickBars; i++) {
			final ToolBox tb = toolMgr.getToolBox(hudType, quickBarPrefix + i);
			ButtonAdapter qb = new ButtonAdapter(screen, screen.getStyle("QuickBarButton").getVector4f("resizeBorders"),
					screen.getStyle("QuickBarButton").getString("defaultImg")) {
				@Override
				public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
					tb.setVisible(toggled);
				}
			};
			qb.setPreferredDimensions(screen.getStyle("QuickBarButton").getVector2f("defaultSize"));
			qb.setStyles("QuickBarButton");
			qb.setText(String.valueOf(i));
			qb.setToolTipText(tb.getHelp());
			qb.setIsToggleButton(true);
			qb.setIsToggled(tb.isVisible());
			togglerLayer.addChild(qb);
		}

		// Quickbar toggle button
		togglerRevealLayer = new ButtonAdapter(screen, mainToolBarStyle.getVector2f("quickBarTogglerRevealAreaPosition"),
				mainToolBarStyle.getVector2f("quickBarTogglerRevealAreaSize"), Vector4f.ZERO, null) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				if (togglerLayer.getIsVisible()) {
					showQuickToggler();
				} else {
					hideQuickbarToggler();
				}
			}
		};
		togglerRevealLayer.setToolTipText("Toggle Quickbars");

		// Experience bar
		barLayer = new Indicator(screen, Vector2f.ZERO, mainToolBarStyle.getVector2f("barAreaSize"), Vector4f.ZERO, null,
				Orientation.HORIZONTAL) {
			@Override
			protected void onChange(float currentValue, float currentPercentage) {
			}
		};
		barLayer.setPosition(mainToolBarStyle.getVector2f("barAreaPosition"));
		barLayer.getTextDisplayElement().setFont(screen.getStyle("Font").getString(mainToolBarStyle.getString("barFontName")));
		barLayer.getTextDisplayElement().setFontSize(mainToolBarStyle.getFloat("barFontSize"));
		barLayer.getTextDisplayElement().setFontColor(mainToolBarStyle.getColorRGBA("barFontColor"));

		// The overlay sits on top
		overlayLayer = new Element(screen, UIDUtil.getUID(), mainToolBarStyle.getVector2f("defaultSize"),
				mainToolBarStyle.getVector4f("resizeBorders"), mainToolBarStyle.getString("defaultImg"));
		overlayLayer.setIgnoreMouse(true);

		// The lower container holds the tools and the overlay
		setIgnoreMouse(true);
		setLayoutManager(new FillLayout());
		rebuildContainer();
		addChild(container);

	}

	protected abstract void build();

	protected abstract void updateBarText();

	public final void destroy() {
		if (togglerLayer.getIsVisible()) {
			hideQuickbarToggler();
		}
		onDestroy();
	}

	protected abstract void onDestroy();

	public void addToolBar(ToolPanel element) {
		toolbars.add(element);
		rebuildContainer();
	}

	protected void rebuildContainer() {
		container.removeAllChildren();
		for (Element el : toolbars) {
			System.err.println("XX: " + el + " = " + el.getPosition());
			container.addChild(el, el.getPosition());
		}
		container.addChild(graphicLayer);
		container.addChild(togglerLayer);
		container.addChild(togglerRevealLayer);
		container.addChild(barLayer);
		build();
		container.addChild(overlayLayer);
		layoutChildren();
	}

	protected abstract class HoverArea extends Element implements ToolTipProvider {

		public HoverArea(ElementManager screen, Vector2f position, Vector2f dimensions) {
			super(screen, UIDUtil.getUID(), position, dimensions, Vector4f.ZERO, null);
		}
	}

	protected void hideQuickbarToggler() {
		// screen.updateZOrder(togglerLayer);
		new EffectHelper().reveal(togglerLayer, Effect.EffectType.SlideIn, Effect.EffectDirection.Bottom, 0.25f);
	}

	protected void showQuickToggler() {
		new EffectHelper().hide(togglerLayer, Effect.EffectType.SlideOut, Effect.EffectDirection.Bottom, 0.25f);
	}
}
