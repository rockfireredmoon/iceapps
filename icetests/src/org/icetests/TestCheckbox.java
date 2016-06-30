package org.icetests;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.icelib.AppInfo;
import org.icescene.IcesceneApp;
import org.icescene.SceneConfig;
import org.icescene.SceneConstants;
import org.icescene.assets.Assets;
import org.icescene.assets.MeshLoader;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.buttons.CheckBox;
import icetone.controls.windows.Panel;
import icetone.core.layout.mig.MigLayout;

public class TestCheckbox extends IcesceneApp {

	public static void main(String[] args) throws Exception {
		AppInfo.context = TestImageChooser.class;

		// Parse command line
		Options opts = createOptions();
		Assets.addOptions(opts);

		CommandLine cmdLine = parseCommandLine(opts, args);

		// A single argument must be supplied, the URL (which is used to
		// deterime router, which in turn locates simulator)
		TestCheckbox app = new TestCheckbox(cmdLine);
		startApp(app, cmdLine, "PlanetForever - " + AppInfo.getName() + " - " + AppInfo.getVersion(),
				SceneConstants.APPSETTINGS_NAME);
	}

	private CheckBox checkbox1;
	private CheckBox checkbox2;

	private TestCheckbox(CommandLine commandLine) {
		super(SceneConfig.get(), commandLine, SceneConstants.APPSETTINGS_NAME, null);
		setUseUI(true);
		MeshLoader.setTexturePathsRelativeToMesh(true);
		setPauseOnLostFocus(false);
	}

	@Override
	public void onSimpleInitApp() {

		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);

		Box b = new Box(1, 1, 1);
		Geometry geom = new Geometry("Box", b);

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);

		// Panel for actions and character selection
		final Panel panel = new Panel(screen, "Panel", new Vector2f(8, 8), new Vector2f(372f, 80));
		panel.setLayoutManager(new MigLayout(screen, "fill, wrap 1", "[]", "[][][]"));

		checkbox1 = new CheckBox("Some kind of checkbox") {

			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				checkbox2.setIsEnabled(toggled);
				if (!toggled)
					checkbox2.setIsChecked(false);
			}

		};
		checkbox1.setIsCheckedNoCallback(true);
		panel.addChild(checkbox1);

		checkbox2 = new CheckBox("Another kind of checkbox");
		checkbox2.setIsCheckedNoCallback(true);
		panel.addChild(checkbox2);

		// Add window to screen (causes a layout)
		screen.addElement(panel);

	}

}
