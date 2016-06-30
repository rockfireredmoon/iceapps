package org.icetests;

import org.apache.commons.cli.CommandLine;
import org.icelib.AppInfo;
import org.icescene.IcesceneApp;
import org.icescene.SceneConfig;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector4f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.extras.DragElement;
import icetone.controls.windows.Panel;
import icetone.core.Element;
import icetone.core.layout.mig.MigLayout;

public class TestDnd extends IcesceneApp {

	static {
		AppInfo.context = TestDnd.class;
	}

	public static void main(String[] args) throws Exception {
		defaultMain(args, TestDnd.class, "Icetest");
	}

	public TestDnd(CommandLine cli) {
		super(SceneConfig.get(), cli, "Icetest", "META-INF/TestAssets.cfg");
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {
		screen.setUseCustomCursors(true);

		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);

		Box b = new Box(1, 1, 1);
		Geometry geom = new Geometry("Box", b);

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);

		// A place to drop (we will accept drop here). I think a dropable can be
		// anything that extends Element as has setIsDragDropDropElement(true)
		Element dropOk = new Element ();
		dropOk.setText("Drop OK here!");
		dropOk.setIsDragDropDropElement(true);

		// And now another drop element. This one we reject dynamically at end
		// of drag,
		// but still need to make droppable
		final Element dropNotOk = new Element();
		dropNotOk.setText("Don't drop here!");
		dropNotOk.setIsDragDropDropElement(true);

		// Draggable thing
		DragElement draggable = new DragElement(screen, Vector4f.ZERO, null) {
			@Override
			public void onDragStart(MouseButtonEvent evt) {
				System.out.println("Drag started");
			}

			@Override
			public boolean onDragEnd(MouseButtonEvent evt, Element dropElement) {
				if (dropElement == null) {
					System.out.println("Don't drop nowhere!");
					return false;
				} else if (dropElement == dropNotOk) {
					dropElement.setText("I said DONT!");
					return false;
				} else {
					setText("");
					dropElement.setText("Thanks for dropping!");
					return true;
				}
			}
		};
		draggable.setUseSpringBack(true);
		draggable.setText("Drag me!");

		// Add and show
		Panel window = new Panel(new MigLayout());
		window.addChild(draggable);
		window.addChild(dropOk);
		window.addChild(dropNotOk);
		screen.addElement(window);

	}

}
