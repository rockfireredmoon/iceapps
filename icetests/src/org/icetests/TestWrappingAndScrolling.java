package org.icetests;

import java.util.ArrayList;
import java.util.List;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;
import org.iceui.controls.FancyWindow;

import com.jme3.font.BitmapFont;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.buttons.Button;
import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.buttons.CheckBox;
import icetone.controls.lists.ComboBox;
import icetone.controls.lists.FloatRangeSpinnerModel;
import icetone.controls.lists.IntegerRangeSpinnerModel;
import icetone.controls.lists.Spinner;
import icetone.controls.scrolling.ScrollPanel;
import icetone.controls.text.Label;
import icetone.controls.windows.Panel;
import icetone.core.Element;
import icetone.core.Element.Orientation;
import icetone.core.layout.WrappingLayout;
import icetone.core.layout.mig.MigLayout;

public class TestWrappingAndScrolling extends IcesceneApp {

	static {
		AppInfo.context = TestWrappingAndScrolling.class;
	}

	public static void main(String[] args) {
		TestWrappingAndScrolling app = new TestWrappingAndScrolling();
		app.start();
	}

	public TestWrappingAndScrolling() {
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {
		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);

		// Example 3D Object
		Box b = new Box(1, 1, 1);
		Geometry geom = new Geometry("Box", b);

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);

		// Example scroll content
		FancyWindow scrollPanel = new FancyWindow();
		scrollPanel.setIsResizable(true);
		scrollPanel.getContentArea().setLayoutManager(new MigLayout(screen, "", "[fill, grow]", "[fill, grow]"));
		scrollPanel.setDimensions(600, 400);
		final ScrollPanel scroller = new ScrollPanel();
		// scr2.setUseVerticalWrap(true);
		final WrappingLayout wrapLayout = (WrappingLayout) scroller.getScrollContentLayout();
		scrollPanel.getContentArea().addChild(scroller);

		final List<Button> buttons = new ArrayList<Button>();
		final List<Button> buttons2 = new ArrayList<Button>();
		for (int i = 0; i < 10; i++) {
			Button b1 = new ButtonAdapter("B" + i);
			b1.setPreferredDimensions(new Vector2f(64, 32));
			buttons.add(b1);
			scroller.addScrollableContent(b1);
			Button b2 = new ButtonAdapter("S" + i);
			b2.setPreferredDimensions(new Vector2f(48, 14));
			scroller.addScrollableContent(b2);
			buttons2.add(b2);
		}

		// Options
		Panel opts = new Panel(screen, new Vector2f(600, 20));
		opts.setLayoutManager(new MigLayout(screen, "wrap 2", "[][fill,grow]"));
		// Equal
		CheckBox paging = new CheckBox() {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				scroller.setUseContentPaging(toggled);
			}
		};
		paging.setIsCheckedNoCallback(scroller.getUseContentPaging());
		paging.setLabelText("Paging");
		opts.addChild(paging, "span 2");

		// Equal
		CheckBox equalSizeCells = new CheckBox() {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				wrapLayout.setEqualSizeCells(toggled);
				scroller.dirtyLayout(true);
				scroller.layoutChildren();
			}
		};
		equalSizeCells.setIsCheckedNoCallback(wrapLayout.isEqualSizeCells());
		equalSizeCells.setLabelText("Equals size cells");
		opts.addChild(equalSizeCells, "span 2");

		// Fill
		CheckBox fill = new CheckBox() {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				wrapLayout.setFill(toggled);
				scroller.dirtyLayout(true);
				scroller.layoutChildren();
			}
		};
		fill.setLabelText("Fill");
		fill.setIsCheckedNoCallback(wrapLayout.isFill());
		opts.addChild(fill, "span 2");

		// Cells
		opts.addChild(new Label("Cells"));
		Spinner<Integer> cells = new Spinner<Integer>(screen) {
			@Override
			public void onChange(Integer value) {
				wrapLayout.setWidth(value);
				scroller.dirtyLayout(true);
				scroller.layoutChildren();
			}
		};
		cells.setSpinnerModel(new IntegerRangeSpinnerModel(0, 1000, 1, wrapLayout.getWidth()));
		opts.addChild(cells);

		// Orient
		opts.addChild(new Label("Orientation"));
		ComboBox<Element.Orientation> orient = new ComboBox<Element.Orientation>(screen) {
			@Override
			public void onChange(int selectedIndex, Element.Orientation value) {
				wrapLayout.setOrientation((Element.Orientation) value);
				scroller.dirtyLayout(true);
				scroller.layoutChildren();
			}
		};
		for (Element.Orientation o : Element.Orientation.values()) {
			orient.addListItem(o.name(), o);
		}
		orient.setSelectedByValue(wrapLayout.getOrientation(), false);
		opts.addChild(orient);

		// HBar
		opts.addChild(new Label("HBar"));
		ComboBox<ScrollPanel.ScrollBarMode> hbar = new ComboBox<ScrollPanel.ScrollBarMode>(ScrollPanel.ScrollBarMode.values()) {
			@Override
			public void onChange(int selectedIndex, ScrollPanel.ScrollBarMode value) {
				scroller.setHorizontalScrollBarMode(value);
			}
		};
		hbar.setSelectedByValue(scroller.getHorizontalScrollBarMode(), false);
		opts.addChild(hbar);

		// VBar
		opts.addChild(new Label("VBar"));
		ComboBox<ScrollPanel.ScrollBarMode> vbar = new ComboBox<ScrollPanel.ScrollBarMode>(ScrollPanel.ScrollBarMode.values()) {
			@Override
			public void onChange(int selectedIndex, ScrollPanel.ScrollBarMode value) {
				scroller.setVerticalScrollBarMode(value);
			}
		};
		vbar.setSelectedByValue(scroller.getVerticalScrollBarMode(), false);
		opts.addChild(vbar);

		// Valign
		opts.addChild(new Label("Valign"));
		ComboBox<BitmapFont.VAlign> valign = new ComboBox<BitmapFont.VAlign>(BitmapFont.VAlign.values()) {
			@Override
			public void onChange(int selectedIndex, BitmapFont.VAlign value) {
				wrapLayout.setVAlign(value);
				scroller.dirtyLayout(true);
				scroller.layoutChildren();
			}
		};
		for (BitmapFont.VAlign o : BitmapFont.VAlign.values()) {
			valign.addListItem(o.name(), o);
		}
		valign.setSelectedByValue(wrapLayout.getVAlign(), false);
		opts.addChild(valign);

		// Halign
		opts.addChild(new Label("Halign"));
		ComboBox<BitmapFont.Align> halign = new ComboBox<BitmapFont.Align>(BitmapFont.Align.values()) {
			@Override
			public void onChange(int selectedIndex, BitmapFont.Align value) {
				wrapLayout.setAlign(value);
				scroller.dirtyLayout(true);
				scroller.layoutChildren();
			}
		};
		halign.setSelectedByValue(wrapLayout.getAlign(), false);
		opts.addChild(halign);

		// Gap
		opts.addChild(new Label("Scroll Size"));
		Spinner<Float> scrollSize = new Spinner<Float>(screen, Orientation.HORIZONTAL, false) {
			@Override
			public void onChange(Float value) {
				scroller.setScrollSize(value);
			}
		};
		scrollSize.setSpinnerModel(new FloatRangeSpinnerModel(-1, 100, 1, 0));
		scrollSize.setSelectedValue(scroller.getScrollSize());
		opts.addChild(scrollSize);

		// Gap
		opts.addChild(new Label("Gap"));
		Spinner<Integer> gap = new Spinner<Integer>(screen, Orientation.HORIZONTAL, false) {
			@Override
			public void onChange(Integer value) {
				wrapLayout.setGap(value);
				scroller.dirtyLayout(true);
				scroller.layoutChildren();
			}
		};
		gap.setSpinnerModel(new IntegerRangeSpinnerModel(0, 100, 1, 0));
		gap.setSelectedValue(wrapLayout.getGap());
		opts.addChild(gap);

		// Margin X
		opts.addChild(new Label("Margin Top"));
		Spinner<Float> marginX = new Spinner<Float>(screen, Orientation.HORIZONTAL, false) {
			@Override
			public void onChange(Float value) {
				scroller.getScrollBounds().getTextPaddingVec().setX(value);
				scroller.dirtyLayout(true);
				scroller.layoutChildren();
			}
		};
		marginX.setSpinnerModel(new FloatRangeSpinnerModel(0, 100, 1, 0));
		marginX.setSelectedValue(scroller.getScrollBounds().getTextPaddingVec().getX());
		opts.addChild(marginX);

		// Margin Y
		opts.addChild(new Label("Margin Left"));
		Spinner<Float> marginY = new Spinner<Float>(screen, Orientation.HORIZONTAL, false) {
			@Override
			public void onChange(Float value) {
				scroller.getScrollBounds().getTextPaddingVec().setY(value);
				scroller.dirtyLayout(true);
				scroller.layoutChildren();
			}
		};
		marginY.setSpinnerModel(new FloatRangeSpinnerModel(0, 100, 1, 0));
		marginY.setSelectedValue(scroller.getScrollBounds().getTextPaddingVec().getY());
		opts.addChild(marginY);

		// Margin Z
		opts.addChild(new Label("Margin Right"));
		Spinner<Float> marginZ = new Spinner<Float>(screen, Orientation.HORIZONTAL, false) {
			@Override
			public void onChange(Float value) {
				scroller.getScrollBounds().getTextPaddingVec().setZ(value);
				scroller.dirtyLayout(true);
				scroller.layoutChildren();
			}
		};
		marginZ.setSpinnerModel(new FloatRangeSpinnerModel(0, 100, 1, 0));
		marginZ.setSelectedValue(scroller.getScrollBounds().getTextPaddingVec().getZ());
		opts.addChild(marginZ);

		// Margin Y
		opts.addChild(new Label("Margin Bottom"));
		Spinner<Float> marginW = new Spinner<Float>(screen, Orientation.HORIZONTAL, false) {
			@Override
			public void onChange(Float value) {
				scroller.getScrollBounds().getTextPaddingVec().setW(value);
				scroller.dirtyLayout(true);
				scroller.layoutChildren();
			}
		};
		marginW.setSpinnerModel(new FloatRangeSpinnerModel(0, 100, 1, 0));
		marginW.setSelectedValue(scroller.getScrollBounds().getTextPaddingVec().getW());
		opts.addChild(marginW);

		// Clip padding
		opts.addChild(new Label("Clip Pad"));
		Spinner<Float> clipPad = new Spinner<Float>(screen, Orientation.HORIZONTAL, false) {
			@Override
			public void onChange(Float value) {
				scroller.getScrollBounds().setClipPadding(value);
				scroller.dirtyLayout(true);
				scroller.layoutChildren();
			}
		};
		clipPad.setSpinnerModel(new FloatRangeSpinnerModel(0, 100, 1, 0));
		clipPad.setSelectedValue(scroller.getScrollBounds().getClipPadding());
		opts.addChild(clipPad);

		// Button
		opts.addChild(new Label("Button Scale"));
		Spinner<Float> bs = new Spinner<Float>(screen, Orientation.HORIZONTAL, false) {
			@Override
			public void onChange(Float s) {
				for (Button b : buttons) {
					b.setPreferredDimensions(new Vector2f(64 * s, 32 * s));
				}
				for (Button b : buttons2) {
					b.setPreferredDimensions(new Vector2f(48 * s, 14 * s));
				}
				scroller.dirtyLayout(true);
				scroller.layoutChildren();
			}
		};
		bs.setSpinnerModel(new FloatRangeSpinnerModel(0.1f, 10f, 0.1f, 1f));
		bs.setSelectedValue(1f);
		opts.addChild(bs);
		
		opts.addChild(new ButtonAdapter("Pack") {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				scrollPanel.sizeToContent();
			}
			
		}, "span 2, ax 50%");
		
		opts.addChild(new ButtonAdapter("Add") {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {

				Button b1 = new ButtonAdapter("B" + buttons.size());
				b1.setPreferredDimensions(new Vector2f(64, 32));
				buttons.add(b1);
				scroller.addScrollableContent(b1);
			}
			
		}, "span 2, ax 50%");

		// Add window to screen (causes a layout)
		screen.addElement(scrollPanel);
		screen.addElement(opts);

	}

	Element createPanel(String n) {
		Element p = new Element();
		p.setIgnoreMouse(true);
		MigLayout l = new MigLayout("ins 0, wrap 1, fill", "[grow, fill]", "");
		p.setLayoutManager(l);

		Label l1 = new Label(n);
		l1.setFont(screen.getStyle("Font").getString("mediumFont"));
		l1.setFontSize(screen.getStyle("Common").getFloat("mediumFontSize"));
		p.addChild(l1);

		Label l2 = new Label("Label 2 for " + n);
		l2.setTextVAlign(BitmapFont.VAlign.Top);
		p.addChild(l2, "growx");
		return p;
	}
}
