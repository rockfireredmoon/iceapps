package org.icetests;

import org.apache.commons.cli.CommandLine;
import org.icelib.AppInfo;
import org.icemoon.audio.AudioAppState;
import org.icescene.IcesceneApp;
import org.icescene.SceneConfig;
import org.icescene.audio.AudioQueue;
import org.icescene.controls.Rotator;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.buttons.PushButton;
import icetone.controls.containers.Panel;
import icetone.core.layout.mig.MigLayout;

public class TestAudio extends IcesceneApp {

	static {
		AppInfo.context = TestAudio.class;
	}

	public static void main(String[] args) throws Exception {
		defaultMain(args, TestAudio.class, "Icetest");
	}

	public TestAudio(CommandLine cli) {
		super(SceneConfig.get(), cli, "Icetest", "META-INF/TestAssets.cfg");
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {
		screen.setUseUIAudio(false);
		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);
		flyCam.setEnabled(true);

		Box b = new Box(1, 1, 1);
		Geometry geom = new Geometry("Box", b);

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);
		geom.addControl(new Rotator());

		AudioAppState aas = new AudioAppState(prefs);
		getStateManager().attach(aas);

		Panel p = new Panel(screen);
		p.setLayoutManager(new MigLayout(screen, "wrap 1"));
		p.addElement(new PushButton(screen, "Start").onMouseReleased(evt -> {
			aas.queue(AudioQueue.MUSIC, this, "Music/Music-Corsica_Ambient1/Music-Corsica_Ambient1.ogg", 180, 1f);
			aas.queue(AudioQueue.MUSIC, this, "Music/Music-Corsica_Ambient2/Music-Corsica_Ambient2.ogg", 180, 1f);
			aas.queue(AudioQueue.MUSIC, this, "Music/Music-Corsica_Ambient3/Music-Corsica_Ambient3.ogg", 180, 1f);
			aas.queue(AudioQueue.AMBIENT, this, "Sound/Sound-Ambient-Cloudyday/Sound-Ambient-Cloudyday.ogg", 0, 1f);
			aas.queue(AudioQueue.AMBIENT2, this, "Sound/Sound-Ambient-Cloudyday/Sound-Ambient-Cloudyday2.ogg", 0, 1f);
		}));
		p.addElement(new PushButton(screen, "Stop").onMouseReleased(evt -> {
			aas.fadeAndRemove(AudioQueue.MUSIC, "Music/Music-Corsica_Ambient1/Music-Corsica_Ambient1.ogg");
			aas.fadeAndRemove(AudioQueue.MUSIC, "Music/Music-Corsica_Ambient2/Music-Corsica_Ambient2.ogg");
			aas.fadeAndRemove(AudioQueue.MUSIC, "Music/Music-Corsica_Ambient3/Music-Corsica_Ambient3.ogg");
			aas.fadeAndRemove(AudioQueue.AMBIENT, "Sound/Sound-Ambient-Cloudyday/Sound-Ambient-Cloudyday.ogg");
			aas.fadeAndRemove(AudioQueue.AMBIENT2, "Sound/Sound-Ambient-Cloudyday/Sound-Ambient-Cloudyday2.ogg");
		}));
		screen.addElement(p);

	}

}
