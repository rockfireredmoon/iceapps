package org.icetests;

import java.util.ArrayList;
import java.util.List;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;
import org.iceui.controls.ElementStyle;

import com.jme3.font.BitmapFont;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.buttons.CheckBox;
import icetone.controls.buttons.PushButton;
import icetone.controls.containers.Frame;
import icetone.controls.containers.Panel;
import icetone.controls.lists.ComboBox;
import icetone.controls.lists.FloatRangeSpinnerModel;
import icetone.controls.lists.IntegerRangeSpinnerModel;
import icetone.controls.lists.Spinner;
import icetone.controls.scrolling.ScrollPanel;
import icetone.controls.text.Label;
import icetone.core.BaseElement;
import icetone.core.Layout.LayoutType;
import icetone.core.Orientation;
import icetone.core.Size;
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
		screen.setUseUIAudio(false);
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
		Frame scrollPanel = new Frame();
		scrollPanel.setResizable(true);
		scrollPanel.getContentArea().setLayoutManager(new MigLayout(screen, "", "[fill, grow]", "[fill, grow]"));
		scrollPanel.setDimensions(600, 400);
		final ScrollPanel scroller = new ScrollPanel();
		// scr2.setUseVerticalWrap(true);
		final WrappingLayout wrapLayout = (WrappingLayout) scroller.getScrollContentLayout();
		scrollPanel.getContentArea().addElement(scroller);

		final List<BaseElement> buttons = new ArrayList<>();
		final List<BaseElement> buttons2 = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			PushButton b1 = new PushButton("B" + i);
			b1.setPreferredDimensions(new Size(64, 32));
			buttons.add(b1);
			scroller.addScrollableContent(b1);
			PushButton b2 = new PushButton("S" + i);
			b2.setPreferredDimensions(new Size(48, 14));
			scroller.addScrollableContent(b2);
			buttons2.add(b2);

			// Label b1 = new Label("B" + i);
			// b1.setPreferredDimensions(new Vector2f(64, 32));
			// buttons.add(b1);
			// scroller.addScrollableContent(b1);
			// Label b2 = new Label("S" + i);
			// b2.setPreferredDimensions(new Vector2f(48, 14));
			// scroller.addScrollableContent(b2);
			// buttons2.add(b2);
		}

		// Options
		Panel opts = new Panel(screen, new Vector2f(600, 20));
		opts.setLayoutManager(new MigLayout(screen, "wrap 2", "[][fill,grow]"));
		// Equal
		CheckBox paging = new CheckBox();
		paging.setChecked(scroller.getUseContentPaging());
		paging.onChange(evt -> scroller.setUseContentPaging(evt.getNewValue()));
		paging.setText("Paging");
		opts.addElement(paging, "span 2");

		// Equal
		CheckBox equalSizeCells = new CheckBox();
		equalSizeCells.setChecked(wrapLayout.isEqualSizeCells());
		equalSizeCells.onChange(evt -> {
			wrapLayout.setEqualSizeCells(evt.getNewValue());
			scroller.dirtyLayout(false, LayoutType.boundsChange());
			scroller.layoutChildren();
		});
		equalSizeCells.setText("Equals size cells");
		opts.addElement(equalSizeCells, "span 2");

		// Fill
		CheckBox fill = new CheckBox();
		fill.setChecked(wrapLayout.isFill());
		fill.onChange(evt -> {
			wrapLayout.setFill(evt.getNewValue());
			scroller.dirtyLayout(false, LayoutType.boundsChange());
			scroller.layoutChildren();
		});
		fill.setText("Fill");
		opts.addElement(fill, "span 2");

		// Cells
		opts.addElement(new Label("Cells"));
		Spinner<Integer> cells = new Spinner<Integer>(screen);
		cells.onChange(evt -> {
			wrapLayout.setWidth(evt.getNewValue());
			scroller.dirtyLayout(false, LayoutType.boundsChange());
			scroller.layoutChildren();
		});
		cells.setSpinnerModel(new IntegerRangeSpinnerModel(0, 1000, 1, wrapLayout.getWidth()));
		opts.addElement(cells);

		// Orient
		opts.addElement(new Label("Orientation"));
		ComboBox<Orientation> orient = new ComboBox<Orientation>(screen);
		for (Orientation o : Orientation.values()) {
			orient.addListItem(o.name(), o);
		}
		orient.setSelectedByValue(wrapLayout.getOrientation());
		orient.onChange(evt -> {
			wrapLayout.setOrientation(evt.getNewValue());
			scroller.dirtyLayout(false, LayoutType.boundsChange());
			scroller.layoutChildren();
		});
		opts.addElement(orient);

		// HBar
		opts.addElement(new Label("HBar"));
		ComboBox<ScrollPanel.ScrollBarMode> hbar = new ComboBox<ScrollPanel.ScrollBarMode>(
				ScrollPanel.ScrollBarMode.values());
		hbar.setSelectedByValue(scroller.getHorizontalScrollBarMode());
		hbar.onChange(evt -> scroller.setHorizontalScrollBarMode(evt.getNewValue()));
		opts.addElement(hbar);

		// VBar
		opts.addElement(new Label("VBar"));
		ComboBox<ScrollPanel.ScrollBarMode> vbar = new ComboBox<ScrollPanel.ScrollBarMode>(
				ScrollPanel.ScrollBarMode.values());
		vbar.setSelectedByValue(scroller.getVerticalScrollBarMode());
		vbar.onChange(evt -> scroller.setVerticalScrollBarMode(evt.getNewValue()));
		opts.addElement(vbar);

		// Valign
		opts.addElement(new Label("Valign"));
		ComboBox<BitmapFont.VAlign> valign = new ComboBox<BitmapFont.VAlign>(BitmapFont.VAlign.values());
		valign.setSelectedByValue(wrapLayout.getVAlign());
		valign.onChange(evt -> {
			wrapLayout.setVAlign(evt.getNewValue());
			scroller.dirtyLayout(false, LayoutType.boundsChange());
			scroller.layoutChildren();
		});
		opts.addElement(valign);

		// Halign
		opts.addElement(new Label("Halign"));
		ComboBox<BitmapFont.Align> halign = new ComboBox<BitmapFont.Align>(BitmapFont.Align.values());
		halign.setSelectedByValue(wrapLayout.getAlign());
		halign.onChange(evt -> {
			wrapLayout.setAlign(evt.getNewValue());
			scroller.dirtyLayout(false, LayoutType.boundsChange());
			scroller.layoutChildren();
		});
		opts.addElement(halign);

		// Gap
		opts.addElement(new Label("Scroll Size"));
		Spinner<Float> scrollSize = new Spinner<Float>(screen, Orientation.HORIZONTAL, false);
		scrollSize.setSpinnerModel(new FloatRangeSpinnerModel(-1, 100, 1, 0));
		// scrollSize.setSelectedValue(scroller.getScrollSize());
		opts.addElement(scrollSize);

		// Gap
		opts.addElement(new Label("Gap"));
		Spinner<Integer> gap = new Spinner<Integer>(screen, Orientation.HORIZONTAL, false);
		gap.onChange(evt -> {
			scroller.getScrollableArea().setIndent(evt.getNewValue());
			scroller.dirtyLayout(false, LayoutType.boundsChange());
			scroller.layoutChildren();
		});
		gap.setSpinnerModel(new IntegerRangeSpinnerModel(0, 100, 1, 0));
		gap.setSelectedValue((int) scroller.getScrollableArea().getIndent());
		opts.addElement(gap);

		// Margin X
		opts.addElement(new Label("Padding Left"));
		Spinner<Float> paddingX = new Spinner<Float>(screen, Orientation.HORIZONTAL, false);
		paddingX.onChange(evt -> {
			Vector4f mar = scroller.getScrollableArea().getTextPadding();
			mar.setX(evt.getNewValue());
			scroller.getScrollableArea().setTextPadding(mar);
			scroller.dirtyLayout(false, LayoutType.boundsChange());
			scroller.layoutChildren();
		});
		paddingX.setSpinnerModel(new FloatRangeSpinnerModel(0, 100, 1, 0));
		paddingX.setSelectedValue(scroller.getScrollBounds().getTextPadding().getX());
		opts.addElement(paddingX);

		// Margin Y
		opts.addElement(new Label("Padding Right"));
		Spinner<Float> paddingY = new Spinner<Float>(screen, Orientation.HORIZONTAL, false);
		paddingY.onChange(evt -> {
			Vector4f mar = scroller.getScrollableArea().getTextPadding();
			mar.setY(evt.getNewValue());
			scroller.getScrollableArea().setTextPadding(mar);
			scroller.dirtyLayout(false, LayoutType.boundsChange());
			scroller.layoutChildren();
		});
		paddingY.setSpinnerModel(new FloatRangeSpinnerModel(0, 100, 1, 0));
		paddingY.setSelectedValue(scroller.getScrollBounds().getTextPadding().getY());
		opts.addElement(paddingY);

		// Margin Z
		opts.addElement(new Label("Padding Top"));
		Spinner<Float> paddingZ = new Spinner<Float>(screen, Orientation.HORIZONTAL, false);
		paddingZ.onChange(evt -> {
			Vector4f mar = scroller.getScrollBounds().getTextPadding();
			mar.setZ(evt.getNewValue());
			scroller.getScrollBounds().setTextPadding(mar);
			scroller.dirtyLayout(false, LayoutType.boundsChange());
			scroller.layoutChildren();
		});
		paddingZ.setSpinnerModel(new FloatRangeSpinnerModel(0, 100, 1, 0));
		paddingZ.setSelectedValue(scroller.getScrollBounds().getTextPadding().getZ());
		opts.addElement(paddingZ);

		// Margin W
		opts.addElement(new Label("Padding Bottom"));
		Spinner<Float> paddingW = new Spinner<Float>(screen, Orientation.HORIZONTAL, false);
		paddingW.onChange(evt -> {
			Vector4f mar = scroller.getScrollableArea().getTextPadding();
			mar.setW(evt.getNewValue());
			scroller.getScrollableArea().setTextPadding(mar);
			scroller.dirtyLayout(false, LayoutType.boundsChange());
			scroller.layoutChildren();
		});
		paddingW.setSpinnerModel(new FloatRangeSpinnerModel(0, 100, 1, 0));
		paddingW.setSelectedValue(scroller.getScrollBounds().getTextPadding().getW());
		opts.addElement(paddingW);

		// Clip padding
		opts.addElement(new Label("Clip Pad"));
		Spinner<Float> clipPad = new Spinner<Float>(screen, Orientation.HORIZONTAL, false);
		clipPad.onChange(evt -> {
			scroller.getScrollBounds().setClipPadding(evt.getNewValue());
			scroller.dirtyLayout(false, LayoutType.boundsChange());
			scroller.layoutChildren();
		});
		clipPad.setSpinnerModel(new FloatRangeSpinnerModel(0, 100, 1, 0));
		clipPad.setSelectedValue(scroller.getScrollBounds().getClipPadding());
		opts.addElement(clipPad);

		// Button
		opts.addElement(new Label("Button Scale"));
		Spinner<Float> bs = new Spinner<Float>(screen, Orientation.HORIZONTAL, false);
		bs.onChange(evt -> {
			for (BaseElement b1 : buttons) {
				b1.setPreferredDimensions(new Size(64 * evt.getNewValue(), 32 * evt.getNewValue()));
			}
			for (BaseElement b1 : buttons2) {
				b1.setPreferredDimensions(new Size(48 * evt.getNewValue(), 14 * evt.getNewValue()));
			}
			scroller.dirtyLayout(false, LayoutType.boundsChange());
			scroller.layoutChildren();
		});
		bs.setSpinnerModel(new FloatRangeSpinnerModel(0.1f, 10f, 0.1f, 1f));
		bs.setSelectedValue(1f);
		opts.addElement(bs);

		opts.addElement(new PushButton("Pack").onMouseReleased(evt -> scrollPanel.sizeToContent()),
				"span 2, ax 50%");

		opts.addElement(new PushButton("Add").onMouseReleased(evt -> {
			PushButton b1 = new PushButton("B" + buttons.size());
			b1.setPreferredDimensions(new Size(64, 32));
			buttons.add(b1);
			scroller.addScrollableContent(b1);
		}), "span 2, ax 50%");

		// Add window to screen (causes a layout)
		screen.addElement(scrollPanel);
		screen.addElement(opts);

	}

	BaseElement createPanel(String n) {
		BaseElement p = new BaseElement();
		p.setIgnoreMouse(true);
		MigLayout l = new MigLayout("ins 0, wrap 1, fill", "[grow, fill]", "");
		p.setLayoutManager(l);

		Label l1 = new Label(n);
		ElementStyle.medium(l1);
		p.addElement(l1);

		Label l2 = new Label("Label 2 for " + n);
		l2.setTextVAlign(BitmapFont.VAlign.Top);
		p.addElement(l2, "growx");
		return p;
	}
}
