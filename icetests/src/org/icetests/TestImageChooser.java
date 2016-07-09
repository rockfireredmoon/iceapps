package org.icetests;

import java.util.Set;
import java.util.prefs.Preferences;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.icelib.AppInfo;
import org.icescene.IcesceneApp;
import org.icescene.SceneConfig;
import org.icescene.SceneConstants;
import org.icescene.assets.Assets;
import org.icescene.assets.MeshLoader;
import org.icescene.audio.AudioAppState;
import org.icescene.audio.AudioQueue;
import org.icescene.audio.QueuedAudio;
import org.icescene.props.EntityFactory;
import org.icescene.ui.PreviewModelView;
import org.iceui.controls.FancyButton;
import org.iceui.controls.FancyWindow;
import org.iceui.controls.FancyWindow.Size;
import org.iceui.controls.ImageFieldControl;
import org.iceui.controls.SoundFieldControl;
import org.iceui.controls.SoundFieldControl.Type;
import org.iceui.controls.chooser.ChooserDialog;
import org.iceui.controls.chooser.SoundSourceDialog;
import org.iceui.controls.chooser.SoundSourceDialog.Source;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icemoon.iceloader.ServerAssetManager;
import icetone.core.Element;
import icetone.core.layout.mig.MigLayout;

public class TestImageChooser extends IcesceneApp {

	public static void main(String[] args) throws Exception {
		AppInfo.context = TestImageChooser.class;

		// Parse command line
		Options opts = createOptions();
		Assets.addOptions(opts);

		CommandLine cmdLine = parseCommandLine(opts, args);

		// A single argument must be supplied, the URL (which is used to
		// deterime router, which in turn locates simulator)
		TestImageChooser app = new TestImageChooser(cmdLine);
		startApp(app, cmdLine, AppInfo.getName() + " - " + AppInfo.getVersion(), SceneConstants.APPSETTINGS_NAME);
	}

	private TestImageChooser(CommandLine commandLine) {
		super(SceneConfig.get(), commandLine, SceneConstants.APPSETTINGS_NAME, null);
		setUseUI(true);
		MeshLoader.setTexturePathsRelativeToMesh(true);
		setPauseOnLostFocus(false);
	}

	@Override
	public void onSimpleInitApp() {
		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);

		final Set<String> imageResources = ((ServerAssetManager) assetManager).getAssetNamesMatching(".*\\.png");

		final Set<String> csmResources = ((ServerAssetManager) assetManager).getAssetNamesMatching(".*\\.csm.xml");
		final Set<String> oggResources = ((ServerAssetManager) assetManager).getAssetNamesMatching(".*\\.ogg");

		Box b = new Box(1, 1, 1);
		Geometry geom = new Geometry("Box", b);

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom.setMaterial(mat);

		rootNode.attachChild(geom);

		FancyWindow win = new FancyWindow(screen, new Vector2f(200, 200), Size.LARGE, true);
		win.getDragBar().setText("Fancy Window!");
		Element el = win.getContentArea();
		el.setLayoutManager(new MigLayout(screen, "gap 0, ins 0 0 0 0, wrap 1", "[grow, fill]", "[][][]"));
		ImageFieldControl ifc = new ImageFieldControl(screen, null, imageResources,
				Preferences.userRoot().node("icescenetests")) {
			@Override
			protected void onResourceChosen(String newResource) {
			}
		};
		el.addChild(ifc);
		SoundFieldControl sfc = new SoundFieldControl(screen, Type.ALL, "http://ai-radio.org/radio.opus.m3u?stream",
				oggResources, Preferences.userRoot().node("icescenetests")) {
			@Override
			protected void onResourceChosen(String newResource) {
			}

			@Override
			protected void stopAudio() {
				((SoundSourceDialog) chooser).setStopAvailable(false);
				AudioAppState as = app.getStateManager().getState(AudioAppState.class);
				as.stopAudio(false, AudioQueue.PREVIEWS);
			}

			@Override
			protected void playURL(Source source, String path) {
				AudioAppState as = app.getStateManager().getState(AudioAppState.class);
				as.stopAudio(false, AudioQueue.PREVIEWS);
				final QueuedAudio queuedAudio = new QueuedAudio(this, path, 0, false, AudioQueue.PREVIEWS, 1f);
				queuedAudio.setStream(Source.STREAM_URL.equals(source));
				queuedAudio.setStreamCache(false);
				as.queue(queuedAudio);
				((SoundSourceDialog) chooser).setStopAvailable(true);
			}

		};
		el.addChild(sfc);

		final EntityFactory pf = new EntityFactory(this, rootNode);

		FancyButton fb = new FancyButton(screen) {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
				ChooserDialog chooser = new ChooserDialog(screen, "Choose Model", csmResources,
						Preferences.userRoot().node("icescenetests"), new PreviewModelView(screen, pf)) {
					@Override
					public boolean onChosen(String path) {
						return true;
					}
				};

				chooser.pack(false);
				screen.addElement(chooser);
			}
		};
		fb.setText("Model");

		el.addChild(fb);

		stateManager.attach(new AudioAppState(prefs));

		//
		screen.addElement(win);

	}
}
