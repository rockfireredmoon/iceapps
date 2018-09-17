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
import org.iceui.controls.ImageFieldControl;
import org.iceui.controls.SoundFieldControl;
import org.iceui.controls.SoundFieldControl.Type;
import org.iceui.controls.chooser.SoundSourceDialog;
import org.iceui.controls.chooser.SoundSourceDialog.Source;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icemoon.iceloader.ServerAssetManager;
import icetone.controls.buttons.PushButton;
import icetone.controls.containers.Frame;
import icetone.core.BaseElement;
import icetone.core.ToolKit;
import icetone.core.layout.mig.MigLayout;
import icetone.extras.chooser.ChooserDialog;
import icetone.extras.chooser.StringChooserModel;

public class TestImageChooser extends IcesceneApp {

	public static void main(String[] args) throws Exception {
		AppInfo.context = TestImageChooser.class;
		Options opts = createOptions();
		Assets.addOptions(opts);
		CommandLine cmdLine = parseCommandLine(opts, args);
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

		Frame win = new Frame(screen, new Vector2f(200, 200), true);
		win.getDragBar().setText("Choosers");
		BaseElement el = win.getContentArea();
		el.setLayoutManager(new MigLayout(screen, "wrap 1", "[grow, fill]", "[][][]"));
		ImageFieldControl ifc = new ImageFieldControl(screen, null, new StringChooserModel(imageResources),
				Preferences.userRoot().node("icescenetests")) {
			@Override
			protected void onResourceChosen(String newResource) {
			}
		};
		el.addElement(ifc);
		SoundFieldControl sfc = new SoundFieldControl(screen, Type.ALL, "http://ai-radio.org/radio.opus.m3u?stream",
				new StringChooserModel(oggResources), Preferences.userRoot().node("icescenetests")) {
			@Override
			protected void onResourceChosen(String newResource) {
			}

			@Override
			protected void stopAudio() {
				((SoundSourceDialog) chooser).setStopAvailable(false);
				AudioAppState as = ToolKit.get().getApplication().getStateManager().getState(AudioAppState.class);
				as.stopAudio(false, AudioQueue.PREVIEWS);
			}

			@Override
			protected void playURL(Source source, String path) {
				AudioAppState as = ToolKit.get().getApplication().getStateManager().getState(AudioAppState.class);
				as.stopAudio(false, AudioQueue.PREVIEWS);
				final QueuedAudio queuedAudio = new QueuedAudio(this, path, 0, false, AudioQueue.PREVIEWS, 1f);
				queuedAudio.setStream(Source.STREAM_URL.equals(source));
				queuedAudio.setStreamCache(false);
				as.queue(queuedAudio);
				((SoundSourceDialog) chooser).setStopAvailable(true);
			}

		};
		el.addElement(sfc);

		final EntityFactory pf = new EntityFactory(this, rootNode);

		PushButton fb = new PushButton(screen);
		fb.onMouseReleased(evt -> {
			screen.addElement(
					new ChooserDialog<String>(screen, null, "Choose Model", new StringChooserModel(csmResources),
							Preferences.userRoot().node("icescenetests"), new PreviewModelView(screen, pf)));
		});
		fb.setText("Model");

		el.addElement(fb);

		stateManager.attach(new AudioAppState(prefs));

		//
		screen.addElement(win);

	}
}
