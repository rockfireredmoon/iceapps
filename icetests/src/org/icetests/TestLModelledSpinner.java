package org.icetests;

import org.apache.commons.cli.CommandLine;
import org.icelib.AppInfo;
import org.icescene.IcesceneApp;
import org.icescene.SceneConfig;
import org.iceui.controls.color.ColorFieldControl;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.buttons.CheckBox;
import icetone.controls.lists.ComboBox;
import icetone.controls.lists.IntegerRangeSpinnerModel;
import icetone.controls.lists.Spinner;
import icetone.controls.text.Label;
import icetone.controls.text.TextField;
import icetone.controls.windows.Panel;
import icetone.core.Container;
import icetone.core.layout.BorderLayout;
import icetone.core.layout.mig.MigLayout;

public class TestLModelledSpinner extends IcesceneApp {

	static {
		AppInfo.context = TestLModelledSpinner.class;
	}

	private Spinner textLength;
	private TextField testTest;
	private ColorFieldControl textColour;
	private ComboBox<String> textChoice;

	public static void main(String[] args) throws Exception {
		defaultMain(args, TestLModelledSpinner.class, "Icetest");
	}

	public TestLModelledSpinner(CommandLine cli) {
		super(SceneConfig.get(), cli, "Icetest", "META-INF/TestAssets.cfg");
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {
		
		screen.setUseToolTips(false);

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


		Container opts = new Container();
		opts.setLayoutManager(new MigLayout("wrap 2", "[shrink 0][fill, grow]", "[][][]"));

		opts.addChild(new Label("Text Test"));
		testTest = new TextField();
		testTest.setText("Stuff!");
		opts.addChild(testTest);

		// Length of test text
		opts.addChild(new Label("Text Length"));
		textLength = new Spinner();
		textLength.setSpinnerModel(new IntegerRangeSpinnerModel(1, 256, 10, 32));
		textLength.selectTextRangeAll();
		opts.addChild(textLength, "");

		// Length of test text
		opts.addChild(new Label("Text Colour"));
		textColour = new ColorFieldControl(ColorRGBA.White);
		opts.addChild(textColour);

		// Length of test text
		opts.addChild(new Label("Text Choice", screen));
		textChoice = new ComboBox<String>();
		opts.addChild(textChoice);
		
		CheckBox en = new CheckBox("Enable") {

			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				super.onButtonMouseLeftUp(evt, toggled);
				setAllEnabled(toggled);
			}
			
		};
		opts.addChild(en, "span 2");
		
		setAllEnabled(false);

		// Panel
		Panel xcw = new Panel(screen);
		xcw.setLayoutManager(new BorderLayout());
		xcw.addChild(opts, BorderLayout.Border.SOUTH);
		xcw.pack(true);

		screen.addElement(xcw);
		xcw.show();
	}


	protected void setAllEnabled(boolean toggled) {
		testTest.setIsEnabled(toggled);
		textChoice.setIsEnabled(toggled);
		textLength.setIsEnabled(toggled);
		textColour.setIsEnabled(toggled);
	}
}
