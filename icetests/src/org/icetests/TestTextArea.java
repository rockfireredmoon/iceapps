package org.icetests;

import org.apache.commons.cli.CommandLine;
import org.icelib.AppInfo;
import org.icescene.IcesceneApp;
import org.icescene.SceneConfig;
import org.icescene.ServiceRef;
import org.icescene.configuration.ColourPalettes;

import com.jme3.font.LineWrapMode;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.containers.Panel;
import icetone.controls.lists.ComboBox;
import icetone.controls.scrolling.ScrollPanel;
import icetone.controls.text.Label;
import icetone.controls.text.TextArea;
import icetone.core.layout.mig.MigLayout;

public class TestTextArea extends IcesceneApp {

	static {
		AppInfo.context = TestTextArea.class;
	}

	@ServiceRef
	private static ColourPalettes colourPalettes;

	public static void main(String[] args) throws Exception {
		defaultMain(args, TestTextArea.class, "Icetest");
	}

	public TestTextArea(CommandLine cli) {
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

		Panel opts = new Panel(new MigLayout("wrap 2, fill", "[shrink 0][grow]", "[grow][shrink 0][][][]"));

		TextArea textArea = new TextArea();
		// textArea.setRows(5);
		textArea.setCharacterLength(20);

		opts.addElement(new Label("Test Area"));
		opts.addElement(new ScrollPanel(textArea), "growy");
		opts.addElement(new Label("Wrap Mode"));
		opts.addElement(new ComboBox<>(LineWrapMode.values()).onChange(evt -> textArea.setTextWrap(evt.getNewValue())));
		opts.addElement(new Label("Test Area 2"));
		opts.addElement(new TextArea(screen) {

			@Override
			protected String onValidationError(String text, IllegalArgumentException ae) {
				ae.printStackTrace();
				return null;
			}
			
		}.setRows(3).setMaxRows(3).setMaxLength(80).setCharacterLength(30).setTextWrap(LineWrapMode.Word));
		opts.addElement(new Label("Upper Area 2"));
		opts.addElement(new TextArea(screen).setCharacterLength(15).setRows(1)
				.addTextParser(new TextArea.UpperCaseProcessor()));
		opts.addElement(new Label("Password Area"));
		opts.addElement(new TextArea(screen).setRows(1).setCharacterLength(15)
				.addTextFormatter(new TextArea.PasswordFormatter()));

		screen.addElement(opts);
	}

}
