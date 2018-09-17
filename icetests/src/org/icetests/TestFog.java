package org.icetests;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.icelib.AppInfo;
import org.icescene.IcemoonAppState;
import org.icescene.IcesceneApp;
import org.icescene.SceneConfig;
import org.icescene.SceneConstants;
import org.icescene.assets.Assets;
import org.icescene.assets.MeshLoader;
import org.icescene.environment.EnvironmentLight;
import org.icescene.environment.PostProcessAppState;
import org.icescene.fog.FogFilter;
import org.icescene.fog.FogFilter.FogMode;

import com.jme3.app.state.AppStateManager;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.containers.Panel;
import icetone.core.layout.BorderLayout;

public class TestFog extends IcesceneApp {

	static {
		AppInfo.context = TestFog.class;
	}

	public static void main(String[] args) throws Exception {
		AppInfo.context = TestImageChooser.class;

		// Parse command line
		Options opts = createOptions();
		Assets.addOptions(opts);

		CommandLine cmdLine = parseCommandLine(opts, args);

		// A single argument must be supplied, the URL (which is used to
		// deterime router, which in turn locates simulator)
		TestFog app = new TestFog(cmdLine);
		startApp(app, cmdLine, AppInfo.getName() + " - " + AppInfo.getVersion(), SceneConstants.APPSETTINGS_NAME);
	}

	private TestFog(CommandLine commandLine) {
		super(SceneConfig.get(), commandLine, SceneConstants.APPSETTINGS_NAME, null);
		setUseUI(true);
		MeshLoader.setTexturePathsRelativeToMesh(true);
		setPauseOnLostFocus(false);
	}

	@Override
	public void onSimpleInitApp() {
		EnvironmentLight el = new EnvironmentLight(getCamera(), rootNode, prefs);
		stateManager.attach(new PostProcessAppState(prefs, el) {

			@Override
			protected IcemoonAppState<?> onInitialize(AppStateManager stateManager, IcesceneApp app) {
				IcemoonAppState<?> onInitialize = super.onInitialize(stateManager, app);
				
				setLightBeams(false);
				setSSAO(false);
				setBloom(false);
				setShadows(false);

				// Works
//				setFogFilterMode(FogFilterMode.JME3);
//				((com.jme3.post.filters.FogFilter)getFogFilter()).setFogColor(ColorRGBA.Red);
//				((com.jme3.post.filters.FogFilter)getFogFilter()).setFogDensity(0.5f);
//				((com.jme3.post.filters.FogFilter)getFogFilter()).setFogDistance(100);
//				getFogFilter().setEnabled(true);
				
//				setFogFilterMode(FogFilterMode.OGL);
//				((org.icescene.fog.FogFilter)getFogFilter()).setFogColor(ColorRGBA.Red);
//				((org.icescene.fog.FogFilter)getFogFilter()).setFogDensity(0.1f);
//				((org.icescene.fog.FogFilter)getFogFilter()).setFogMode(FogMode.LINEAR);
//				((org.icescene.fog.FogFilter)getFogFilter()).setFogStartDistance(100);
//				((org.icescene.fog.FogFilter)getFogFilter()).setFogStartDistance(700);
//				((org.icescene.fog.FogFilter)getFogFilter()).setExcludeSky(false);
//				getFogFilter().setEnabled(true);
				
				setFogFilterMode(FogFilterMode.OGL2);
				((org.icescene.fog.FogFilter2)getFogFilter()).setFogColor(ColorRGBA.Red);
				((org.icescene.fog.FogFilter2)getFogFilter()).setFogDensity(0.1f);
				((org.icescene.fog.FogFilter2)getFogFilter()).setFogMode(org.icescene.fog.FogFilter2.FogMode.LINEAR);
				((org.icescene.fog.FogFilter2)getFogFilter()).setFogStartDistance(100);
				((org.icescene.fog.FogFilter2)getFogFilter()).setFogEndDistance(700);
				((org.icescene.fog.FogFilter2)getFogFilter()).setExcludeSky(false);
				getFogFilter().setEnabled(true);
				
				return onInitialize;
			}

		});

		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);

		Box b = new Box(1, 1, 1);
		Geometry geom = new Geometry("Box", b);

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom.setMaterial(mat);

		rootNode.attachChild(geom);

		// Panel
		Panel p = new Panel(screen);
		p.setLayoutManager(new BorderLayout());
		// p.addElement(buttons, Border.CENTER);
		screen.showElement(p);

		// Need light for props
		AmbientLight al = new AmbientLight();
		al.setColor(ColorRGBA.White);
		rootNode.addLight(al);

	}

}
