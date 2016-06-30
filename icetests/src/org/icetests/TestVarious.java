package org.icetests;

import org.apache.commons.cli.CommandLine;
import org.icelib.AppInfo;
import org.icescene.IcesceneApp;
import org.icescene.SceneConfig;
import org.iceui.controls.color.ColorFieldControl;
import org.iceui.controls.color.XColorSelector.ColorTab;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.form.Form;
import icetone.controls.lists.ComboBox;
import icetone.controls.lists.IntegerRangeSpinnerModel;
import icetone.controls.lists.Spinner;
import icetone.controls.text.Label;
import icetone.controls.text.TextField;
import icetone.controls.windows.Panel;
import icetone.core.Container;
import icetone.core.layout.mig.MigLayout;

public class TestVarious extends IcesceneApp {

	static {
		AppInfo.context = TestVarious.class;
	}

	public static void main(String[] args) throws Exception {
		defaultMain(args, TestVarious.class, "Icetest");
	}

	public TestVarious(CommandLine cli) {
		super(SceneConfig.get(), cli, "Icetest", "META-INF/TestAssets.cfg");
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {

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
		addLaidOutversion();

	}

	private void addLaidOutversion() {

		Container opts = new Container(new MigLayout());

		Form form = new Form();

		opts.addChild(new Label("Test Input"));
		TextField tx1 = new TextField("Some text");
		tx1.setMaxLength(30);
		opts.addChild(form.addFormElement(tx1), "wrap");

		opts.addChild(new Label("Test Input2"));
		opts.addChild(form.addFormElement(new TextField("Some more text")), "wrap");

		// Length of test text
		opts.addChild(new Label("Test Length"));
		Spinner<Integer> textLength = new Spinner<Integer>(new IntegerRangeSpinnerModel(1, 256, 10, 32));
		textLength.selectTextRangeAll();
		opts.addChild(form.addFormElement(textLength), "wrap");

		// Length of test text
		opts.addChild(new Label("Test Colour"));
		ColorFieldControl cfc = new ColorFieldControl(screen, new ColorRGBA(1.0f, 0f, 0f, 1.0f), true, true, true);
		cfc.setShowHexInChooser(true);
		opts.addChild(form.addFormElement(cfc), "wrap");

		// Length of test text
		opts.addChild(new Label("Test Colour 2"));
		ColorFieldControl cfc2 = new ColorFieldControl(screen, new ColorRGBA(1.0f, 0f, 0f, 1.0f), true, true, true);
		cfc2.setShowHexInChooser(true);
		cfc2.setAllowUnset(true);
		cfc2.setTabs(ColorTab.WHEEL, ColorTab.PALETTE);
		opts.addChild(form.addFormElement(cfc2), "wrap");

		// Length of test text
		Label child3 = new Label("Test Combo");
		opts.addChild(child3);
		ComboBox<String> textCombo = new ComboBox<String>("Option 1", "Longer option 2", "Option 3", "Another longer option 4");
		opts.addChild(form.addFormElement(textCombo), "wrap");

		// Panel
		Panel xcw = new Panel();
		xcw.addChild(opts);
		xcw.sizeToContent();

		screen.addElement(xcw);
		xcw.show();
	}

}
