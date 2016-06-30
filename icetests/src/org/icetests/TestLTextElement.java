package org.icetests;

import org.apache.commons.cli.CommandLine;
import org.icelib.AppInfo;
import org.icelib.Icelib;
import org.icescene.IcesceneApp;
import org.icescene.SceneConfig;
import org.iceui.controls.Vector4fControl;

import com.jme3.font.BitmapFont.VAlign;
import com.jme3.font.LineWrapMode;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector4f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icemoon.iceloader.ServerAssetManager;
import icetone.controls.buttons.CheckBox;
import icetone.controls.lists.ComboBox;
import icetone.controls.lists.IntegerRangeSpinnerModel;
import icetone.controls.lists.Spinner;
import icetone.controls.scrolling.ScrollPanel;
import icetone.controls.scrolling.ScrollPanel.ScrollBarMode;
import icetone.controls.text.Label;
import icetone.controls.text.TextElement;
import icetone.controls.windows.Panel;
import icetone.core.Container;
import icetone.core.Element;
import icetone.core.Element.Orientation;
import icetone.core.layout.WrappingLayout;
import icetone.core.layout.mig.MigLayout;

public class TestLTextElement extends IcesceneApp {

	static {
		AppInfo.context = TestLTextElement.class;
	}

	private String fnt;
	private Spinner<Integer> sp;
	private ComboBox<String> f;
	private ComboBox<VAlign> cb;
	private ScrollPanel ls;
	private ComboBox<LineWrapMode> wrap;
	private Spinner<Integer> rows;
	private int noRows = 5;
	private CheckBox debugRows;
	private Spinner<Integer> textLength;
	private Spinner<Integer> lineHeight;
	private Vector4fControl padding;

	public static void main(String[] args) throws Exception {
		defaultMain(args, TestLTextElement.class, "Icetest");
	}

	public TestLTextElement(CommandLine cli) {
		super(SceneConfig.get(), cli, "Icetest", "META-INF/TestAssets.cfg");
		setUseUI(true);
		// setStylePath("icetone/style/def/style_map.gui.xml");
	}

	@Override
	public void onSimpleInitApp() {

		// fnt = "Interface/Fonts/Maiiandra_16.fnt";
		// fnt = screen.getStyle("Font").getString("mediumFont");
		// if (fnt == null) {
		fnt = screen.getStyle("Font").getString("defaultFont");
		// }

		screen.setUseCustomCursors(true);
		screen.setUseUIAudio(false);

		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);

		Box b = new Box(1, 1, 1);
		Geometry geom = new Geometry("Box", b);

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);

		ls = new ScrollPanel();
		ls.setHorizontalScrollBarMode(ScrollBarMode.Never);

		// ((WrappingLayout)
		((WrappingLayout) ls.getScrollContentLayout()).setOrientation(Orientation.HORIZONTAL);
		((WrappingLayout) ls.getScrollContentLayout()).setFill(true);

		Container opts = new Container(screen);
		opts.setLayoutManager(new MigLayout("wrap 2", "[shrink 0][]", "[][][][][][]"));

		((ServerAssetManager) assetManager).getAssetNamesMatching(".*\\.fnt");

		// Font
		opts.addChild(new Label("Font"));
		f = new ComboBox<String>(screen) {
			@Override
			public void onChange(int selectedIndex, String value) {
				setFontOnScroller(value);
				setToolTipText(value);
			}
		};
		for (String fntName : ((ServerAssetManager) assetManager).getAssetNamesMatching(".*\\.fnt")) {
			f.addListItem(Icelib.getBaseFilename(fntName), fntName, false);
		}
		if (fnt != null) {
			f.setSelectedByValue(fnt, false);
		}
		opts.addChild(f);

		// Font size
		opts.addChild(new Label("Font Size", screen));
		sp = new Spinner<Integer>(screen, Spinner.Orientation.HORIZONTAL, false) {
			@Override
			public void onChange(Integer value) {
				setFontSizeOnScroller(value);
			}
		};
		sp.setSpinnerModel(new IntegerRangeSpinnerModel(1, 64, 1, (int) screen.getStyle("Common").getFloat("fontSize")));
		opts.addChild(sp, "");

		// Padding
		opts.addChild(new Label("Padding"));
		padding = new Vector4fControl(screen, 0, 100, 1, new Vector4f(), false) {
			@Override
			protected void onChangeVector(Vector4f newValue) {
				setPaddingOnScroller(newValue);
			}
		};
		opts.addChild(padding, "");

		// Wrap
		opts.addChild(new Label("Wrap"));
		wrap = new ComboBox<LineWrapMode>(LineWrapMode.values()) {
			@Override
			public void onChange(int selectedIndex, LineWrapMode value) {
				setWrapOnScroller(value);
			}
		};
		opts.addChild(wrap, "");

		// VAlign
		opts.addChild(new Label("VAlign"));
		cb = new ComboBox<VAlign>(VAlign.values()) {
			@Override
			public void onChange(int selectedIndex, VAlign value) {
				setVAlignOnScroller(value);
			}
		};
		cb.setSelectedByValue(VAlign.Center, false);
		opts.addChild(cb, "");

		// Number of rows
		opts.addChild(new Label("Rows", screen));
		rows = new Spinner<Integer>(screen, Spinner.Orientation.HORIZONTAL, false) {
			@Override
			public void onChange(Integer value) {
				noRows = (Integer) value;
				rebuildText();
			}
		};
		rows.setSpinnerModel(new IntegerRangeSpinnerModel(1, 64, 1, noRows));
		opts.addChild(rows, "");

		// Length of test text
		opts.addChild(new Label("Text Length"));
		textLength = new Spinner<Integer>(Spinner.Orientation.HORIZONTAL, false) {
			@Override
			public void onChange(Integer value) {
				rebuildText();
			}
		};
		textLength.setSpinnerModel(new IntegerRangeSpinnerModel(1, 256, 10, 32));
		opts.addChild(textLength);

		// Length of test text
		opts.addChild(new Label("Line Height"));
		lineHeight= new Spinner<Integer>(Spinner.Orientation.HORIZONTAL, false) {
			@Override
			public void onChange(Integer value) {
				rebuildText();
			}
		};
		lineHeight.setSpinnerModel(new IntegerRangeSpinnerModel(0, 256, 1, 0));
		lineHeight.setToolTipText("Fixed line height. Use zero for automatic");
		opts.addChild(lineHeight);

		// Debug bg
		debugRows = new CheckBox() {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				rebuildText();
			}
		};
		debugRows.setLabelText("Debug background");
		opts.addChild(debugRows, "span 2");

		// Panel
		Panel xcw = new Panel();
		xcw.setLayoutManager(new MigLayout("wrap 1", "[fill, grow]", "[shrink 0][:300:,fill, grow]"));
		xcw.addChild(opts);
		xcw.addChild(ls);
		xcw.sizeToContent();

		screen.addElement(xcw);

		rebuildText();
	}

	private void setFontOnScroller(String path) {
		rebuildText();
		//
		//
		// for (Element el : ls.getScrollableArea().getElements()) {
		// ((LTextElement)el).setFont(path);
		// }
		// ls.getLayoutManager().layout(ls);
	}

	private void rebuildText() {
		ls.getScrollableArea().removeAllChildren();
		for (int i = 0; i < noRows; i++) {
			createAndAddText(ls);
		}
	}

	private void setFontSizeOnScroller(int value) {
		for (Element el : ls.getScrollableArea().getElements()) {
			el.setFontSize(value);
		}
		ls.layoutChildren();
	}

	private void setVAlignOnScroller(VAlign value) {
		for (Element el : ls.getScrollableArea().getElements()) {
			el.setTextVAlign(value);
		}
		ls.layoutChildren();
	}

	private void setWrapOnScroller(LineWrapMode value) {
		for (Element el : ls.getScrollableArea().getElements()) {
			el.setTextWrap(value);
		}
		ls.layoutChildren();
	}

	private void setPaddingOnScroller(Vector4f value) {
		for (Element el : ls.getScrollableArea().getElements()) {
			el.setTextPadding(value);
		}
		ls.layoutChildren();
	}

	private void createAndAddText(ScrollPanel ls) {
		TextElement text = new TextElement(screen, assetManager.loadFont((String) f.getSelectedListItem().getValue())) {
			@Override
			public void onUpdate(float tpf) {
			}

			@Override
			public void onEffectStart() {
			}

			@Override
			public void onEffectStop() {
			}
		};
		text.setFontSize(((Integer) sp.getSpinnerModel().getCurrentValue()).floatValue());

		// try {
		// text.setText(IOUtils.toString(getClass().getResource("/META-INF/help.txt")).replace("\n",
		// "<br/>"));
		// } catch (Exception ex) {
		// text.setText("Failed to load help. " + ex.getMessage());
		// }
		String testString = "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod "
				+ "tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, "
				+ "quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. "
				+ "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu "
				+ "fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in "
				+ "culpa qui officia deserunt mollit anim id est laborum";
		testString = testString.substring(0, (Integer) textLength.getSpinnerModel().getCurrentValue());
		if (debugRows.getIsChecked()) {
			text.setColorMap("Interface/bgy.jpg");
		}
		text.setText(testString);
		text.setFixedLineHeight(((Integer)lineHeight.getSpinnerModel().getCurrentValue()).floatValue());
		text.setTextWrap(wrap.getSelectedListItem().getValue());
		text.setTextVAlign(cb.getSelectedListItem().getValue());
		text.setTextPadding(padding.getValue());
		ls.addScrollableContent(text, true);
	}

}
