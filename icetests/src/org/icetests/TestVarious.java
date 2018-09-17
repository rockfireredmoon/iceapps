package org.icetests;

import org.apache.commons.cli.CommandLine;
import org.icelib.AppInfo;
import org.icescene.IcesceneApp;
import org.icescene.SceneConfig;
import org.icescene.ServiceRef;
import org.icescene.configuration.ColourPalettes;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.containers.Panel;
import icetone.controls.lists.IntegerRangeSpinnerModel;
import icetone.controls.lists.Spinner;
import icetone.controls.text.Label;
import icetone.controls.text.Password;
import icetone.controls.text.TextArea;
import icetone.controls.text.TextField;
import icetone.core.ElementContainer;
import icetone.core.Form;
import icetone.core.StyledContainer;
import icetone.core.layout.mig.MigLayout;
import icetone.extras.chooser.ColorButton;
import icetone.extras.chooser.ColorFieldControl;
import icetone.extras.chooser.ColorSelector.ColorTab;

public class TestVarious extends IcesceneApp {

	static {
		AppInfo.context = TestVarious.class;
	}

	@ServiceRef
	private static ColourPalettes colourPalettes;

	public static void main(String[] args) throws Exception {
		defaultMain(args, TestVarious.class, "Icetest");
	}

	public TestVarious(CommandLine cli) {
		super(SceneConfig.get(), cli, "Icetest", "META-INF/TestAssets.cfg");
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {

		getScripts().eval("Scripts/ColourPalettes.js");

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

		StyledContainer opts = new StyledContainer(new MigLayout("") {

			@Override
			protected Vector2f calcPreferredSize(ElementContainer<?,?> parent) {
				// TODO Auto-generated method stub
				return super.calcPreferredSize(parent);
			}

		});

		Form form = new Form();

		opts.addElement(new Label("Test Input"));
		opts.addElement(new TextField().setMaxLength(30).setForm(form), "wrap");

		opts.addElement(new Label("Test Input2"));
		opts.addElement(new TextField("Some more text").setForm(form), "wrap");

		opts.addElement(new Label("Test Password"));
		opts.addElement(new Password().setMaxLength(20).setForm(form), "wrap");

		opts.addElement(new Label("Test Area"));
		opts.addElement(new TextArea().setRows(5).setCharacterLength(20).setForm(form), "wrap");

		// Length of test text
		opts.addElement(new Label("Test Length"));
		opts.addElement(new Spinner<Integer>(new IntegerRangeSpinnerModel(1, 256, 10, 32)).getTextField()
				.selectTextRangeAll().setForm(form), "wrap");

		// Length of test text
		opts.addElement(new Label("Test Colour 1"));
		opts.addElement(new ColorFieldControl(screen, new ColorRGBA(1.0f, 0f, 0f, 1.0f), true, true)
				.setShowHexInChooser(true).setForm(form), "wrap");

		opts.addElement(new Label("Test Colour 2"));
		opts.addElement(new ColorFieldControl(screen, new ColorRGBA(1.0f, 0f, 0f, 1.0f), true, true)
				.setTabs(ColorTab.PALETTE).setShowHexInChooser(true).setForm(form), "wrap");

		opts.addElement(new Label("Test Colour 3"));
		opts.addElement(new ColorButton(screen, new ColorRGBA(1.0f, 0f, 0f, 1.0f)).setForm(form), "wrap");

		// Length of test text
		opts.addElement(new Label("Test Colour 4"));
		opts.addElement(
				new ColorFieldControl(screen, new ColorRGBA(1.0f, 0f, 0f, 1.0f), true, true)
						.setShowHexInChooser(true).setAllowUnset(true).setTabs(ColorTab.WHEEL, ColorTab.PALETTE)
						.setPalette(ColourPalettes.toColourList(colourPalettes.get("furs2d"))).setForm(form),
				"wrap");

		// cfc2.setPalettes(Arrays.asList(ColourPalettes.toColourList(colourPalettes.get("eyes")),
		// ColourPalettes.toColourList(colourPalettes.get("grayscale2d")),
		// ColourPalettes.toColourList(colourPalettes.get("skin")),
		// ColourPalettes.toColourList(colourPalettes.get("wood")),
		// ColourPalettes.toColourList(colourPalettes.get("metal"))));
		// cfc2.setPalettes(Arrays.asList(ColourPalettes.toColourList(colourPalettes.get("eyes"))));
		// cfc2.setPalettes(Arrays.asList(ColourPalettes.toColourList(colourPalettes.get("rainbow"))));

		// cfc2.setPalettes(Arrays.asList(ColourPalettes.toColourList(colourPalettes.get("rainbow2d"))));

		// Length of test text
		// opts.addElement(new Label("Test Combo"));
		// opts.addElement(new ComboBox<String>("Option 1", "Longer option 2",
		// "Option 3", "Another longer option 4")
		// .bindChanged(evt ->
		// System.out.println(evt.getNewValue())).setForm(form), "wrap");

		//
		// // Length of test text
		// opts.addElement(new Label("Test XHTML Text"));
		// opts.addElement(new XHTMLTextField().setForm(form), "wrap");

		// Panel
		screen.addElement(new Panel().addElement(opts).sizeToContent());
	}

}
