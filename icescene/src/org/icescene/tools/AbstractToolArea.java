package org.icescene.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.jme3.math.Vector2f;

import icetone.controls.buttons.Button;
import icetone.controls.buttons.ToggleButton;
import icetone.controls.extras.Indicator;
import icetone.controls.extras.Indicator.BarMode;
import icetone.controls.extras.Indicator.DisplayMode;
import icetone.core.BaseElement;
import icetone.core.BaseScreen;
import icetone.core.Layout.LayoutType;
import icetone.core.Orientation;
import icetone.core.StyledContainer;
import icetone.core.Element;
import icetone.core.ToolTipProvider;
import icetone.core.layout.FillLayout;
import icetone.core.layout.XYLayout;
import icetone.core.layout.mig.MigLayout;

/**
 * Abstract Component for the main tool area in game or build mode.
 */
public abstract class AbstractToolArea extends Element {

	public final static int MAX_VISIBLE_LUCK = 500;

	public enum Heroism {
		IRON, COPPER, BRONZE, GOLD, STEEL
	}

	public enum LuckStage {
		RED, IRON, COPPER, BRONZE, GOLD, STEEL
	}

	public static final String PLAYER_LUCK = "player-luck";
	public static final String PLAYER_REAGENTS = "player-reagents";
	public static final String PLAYER_CREDITS = "player-credits";
	public static final String PLAYER_COIN = "player-coin";

	protected final StyledContainer container;
	protected final Indicator xpBar;
	protected final ToolManager toolMgr;
	protected Element togglerLayer;
	protected Element graphicLayer;
	protected Button togglerRevealLayer;
	protected List<BaseElement> toolbars = new ArrayList<BaseElement>();
	protected Element overlayLayer;
	private HoverArea coin;
	private HoverArea credits;
	private HoverArea reagents;
	private HoverArea luckHover;
	private Button creditShop;
	private int luck = -1;
	private Element blade;
	private Indicator luckBar;
	private Heroism heroism;
	private int heroismFactor = MAX_VISIBLE_LUCK / (Heroism.values().length - 1);
	private long maxXp;
	private long xp;
	private LuckStage luckStage;

	public AbstractToolArea(HudType hudType, ToolManager toolMgr, final BaseScreen screen, String styleName,
			String quickBarPrefix, int noQuickBars) {
		super(screen);
		addStyleClass(styleName);

		this.toolMgr = toolMgr;

		// Container holds all the layers
		container = new StyledContainer(screen) {
			{
				setStyleClass("container");
			}
		};
		container.setLayoutManager(new XYLayout());
		container.setMovable(false);
		container.setResizable(false);
		container.setIgnoreMouse(true);

		// The main graphic layer for the tool area
		graphicLayer = new StyledContainer(screen) {
			{
				setStyleClass("graphic");
			}
		};

		// The quickbar toggler layer
		togglerLayer = new Element(screen) {
			{
				setStyleClass("toggler");
			}
		};
		togglerLayer.hide();
		togglerLayer.setLayoutManager(new MigLayout(screen, "ins 0", "[][][][][][][]", "[]"));
		for (int i = 1; i <= noQuickBars; i++) {
			final ToolBox tb = toolMgr.getToolBox(hudType, quickBarPrefix + i);
			if (tb == null) {
				LOG.warning(String.format("No quickbar %d", i));
			} else {
				ToggleButton qb = new ToggleButton(screen);
				qb.onChange(evt -> {
					System.out.println(evt.getNewValue() + " / " + tb);
					tb.setVisible(evt.getNewValue());
				});
				qb.setText(String.valueOf(i));
				qb.setToolTipText(tb.getHelp());
				qb.setIsToggled(tb.isVisible());
				togglerLayer.addElement(qb);
			}
		}

		// Quickbar toggle button
		togglerRevealLayer = new Button(screen) {
			{
				setStyleClass("toggler-revealer");
			}
		};
		togglerRevealLayer.onMouseReleased(evt -> {
			if (togglerLayer.isVisible()) {
				hideQuickbarToggler();
			} else {
				showQuickToggler();
			}
		});
		togglerRevealLayer.setToolTipText("Toggle Quickbars");

		// Experience bar
		xpBar = new Indicator(screen, Orientation.HORIZONTAL) {
			@Override
			protected void onChange(float currentValue, float currentPercentage) {
			}

			protected void refactorText() {
				getOverlayElement().setText(getXpText());
			}
		};
		xpBar.setBarMode(BarMode.resize);
		xpBar.setDisplayMode(DisplayMode.none);
		xpBar.setStyleClass("xp-bar");
		xpBar.setMaxValue(1000);

		// Coin hover
		coin = new HoverArea(screen, PLAYER_COIN) {
			public BaseElement createToolTip(Vector2f mouseXY, BaseElement el) {
				return createInfoToolTip(el);
			}
		};

		// Credits hover
		credits = new HoverArea(screen, PLAYER_CREDITS) {
			public BaseElement createToolTip(Vector2f mouseXY, BaseElement el) {
				return createInfoToolTip(el);
			}
		};

		// Reagents hover
		reagents = new HoverArea(screen, PLAYER_REAGENTS) {
			public BaseElement createToolTip(Vector2f mouseXY, BaseElement el) {
				return createInfoToolTip(el);
			}
		};

		// Reagents hover
		luckHover = new HoverArea(screen, PLAYER_LUCK) {
			public BaseElement createToolTip(Vector2f mouseXY, BaseElement el) {
				return createInfoToolTip(el);
			}
		};

		// Credit shop button
		creditShop = new Button(screen) {
			{
				setStyleClass("credit-shop");
			}
		};
		creditShop.onMouseReleased(evt -> {
			openCreditShop();
		});

		// Blade
		blade = new Element(screen) {
			{
				setStyleClass("blade");
			}
		};

		// The overlay sits on top
		overlayLayer = new Element(screen) {
			{
				setStyleClass("bar-overlay");
			}
		};
		overlayLayer.setIgnoreMouse(true);

		// Luck bar
		luckBar = new Indicator(screen, Orientation.VERTICAL) {
			@Override
			protected void onChange(float currentValue, float currentPercentage) {
			}
		};
		luckBar.setDisplayMode(DisplayMode.none);
		luckBar.setMaxValue(heroismFactor);
		luckBar.setStyleClass("luck-bar");

		// The lower container holds the tools and the overlay
		setIgnoreMouse(true);
		setLayoutManager(new FillLayout());
		buildContainer();
		addElement(container);

		//
		setLuck(0);
	}

	public void setMaxXp(long maxXp) {
		this.maxXp = maxXp;
		updateXp();
	}

	public void setXp(long xp) {
		this.xp = xp;
		updateXp();
	}

	public void setLuck(int luck) {
		if (luck != this.luck) {
			this.luck = luck;
			int f = Math.min(luck, MAX_VISIBLE_LUCK) % heroismFactor;
			luckBar.setCurrentValue(f);
			Heroism heroism = Heroism
					.values()[(int) ((float) Math.min(MAX_VISIBLE_LUCK, luck) / (float) heroismFactor)];
			LuckStage luckStage = luck == 0 ? LuckStage.RED
					: LuckStage.values()[1 + (int) ((float) Math.min(MAX_VISIBLE_LUCK, luck) / (float) heroismFactor)];
			if (!Objects.equals(heroism, this.heroism) || !Objects.equals(luckStage, this.luckStage)) {
				this.heroism = heroism;
				this.luckStage = luckStage;
				blade.setStyleClass("blade blade-" + heroism.name().toLowerCase());
				luckBar.setStyleClass("luck-bar luck-" + luckStage.name().toLowerCase());
			}
		}

	}

	protected String getXpText() {
		return String.valueOf(xp) + " Exp.";
	}

	protected void openCreditShop() {
	}

	protected abstract BaseElement createInfoToolTip(BaseElement el);

	protected abstract void updateBarText();

	public final void destroy() {
		if (togglerLayer.isVisible()) {
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
		buildContainer();
		dirtyLayout(true, LayoutType.all);
		layoutChildren();
	}

	protected void buildContainer() {
		container.invalidate();
		container.addElement(graphicLayer);
		for (BaseElement el : toolbars) {
//			el.setPosition(0, 0);
			container.addElement(el);
		}
		container.addElement(togglerLayer);
		container.addElement(togglerRevealLayer);
		container.addElement(xpBar);
		container.addElement(luckBar);
		container.addElement(blade);
		container.addElement(coin);
		container.addElement(credits);
		container.addElement(reagents);
		container.addElement(luckHover);
		container.addElement(creditShop);
		container.validate();
	}

	protected abstract class HoverArea extends Element implements ToolTipProvider {

		public HoverArea(BaseScreen screen, String styleId) {
			super(screen, styleId);
		}
	}

	protected void hideQuickbarToggler() {
		togglerLayer.hide();
	}

	protected void showQuickToggler() {
		togglerLayer.setVisibilityAllowed(true);
		togglerLayer.show();
	}

	protected void updateXp() {
		xpBar.setCurrentValue((float) ((double) xp / (double) maxXp) * xpBar.getMaxValue());
	}
}
