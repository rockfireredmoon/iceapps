package org.icetests;

import org.apache.commons.cli.CommandLine;
import org.icelib.AppInfo;
import org.icescene.IcesceneApp;
import org.icescene.SceneConfig;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.buttons.CheckBox;
import icetone.controls.containers.Panel;
import icetone.controls.extras.DragElement;
import icetone.core.BaseElement;
import icetone.core.Layout.LayoutType;
import icetone.core.StyledContainer;
import icetone.core.layout.mig.MigLayout;

public class TestDnd extends IcesceneApp {

	static {
		AppInfo.context = TestDnd.class;
	}

	public static void main(String[] args) throws Exception {
		defaultMain(args, TestDnd.class, "Icetest");
	}

	private Panel window;

	public TestDnd(CommandLine cli) {
		super(SceneConfig.get(), cli, "Icetest", "META-INF/TestAssets.cfg");
		setUseUI(true);
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

		// A place to drop (we will accept drop here). I think a dropable can be
		// anything that extends Element as has setIsDragDropDropElement(true)
		BaseElement dropOk = new BaseElement();
		dropOk.setText("Drop OK here!");
		dropOk.setDragDropDropElement(true);

		// And now another drop element. This one we reject dynamically at end
		// of drag,
		// but still need to make droppable
		final BaseElement dropNotOk = new BaseElement();
		dropNotOk.setText("Don't drop here!");
		dropNotOk.setDragDropDropElement(true);

		// Draggable thing
		DragElement draggable = new DragElement(screen);
		draggable.onStart(evt -> System.out.println("Drag started"));
		draggable.onEnd(evt -> {
			if (evt.getTarget() == null) {
				System.out.println("Don't drop nowhere!");
			} else if (evt.getTarget() == dropNotOk) {
				evt.getTarget().setText("I said DONT!");
				window.dirtyLayout(false, LayoutType.boundsChange());
				window.sizeToContent();
			} else {
				evt.getElement().setText("");
				evt.getTarget().setText("Thanks for dropping!");
				window.dirtyLayout(false, LayoutType.boundsChange());
				window.sizeToContent();
				evt.setConsumed();
			}
		});
		draggable.setUseSpringBack(true);
		draggable.setText("Drag me!");

		// Options
		StyledContainer c = new StyledContainer(new MigLayout("wrap 1"));
		c.addElement(new CheckBox("Use spring back", draggable.getUseSpringBack())
				.onChange(evt -> draggable.setUseSpringBack(evt.getNewValue())));
		c.addElement(new CheckBox("Spring back effect", draggable.getUseSpringBackEffect())
				.onChange(evt -> draggable.setUseSpringBackEffect(evt.getNewValue())));

		// Add and show
		window = new Panel(new MigLayout("gap 20"));
		window.addElement(draggable);
		window.addElement(dropOk);
		window.addElement(dropNotOk, "wrap");
		window.addElement(c, "wrap");
		screen.addElement(window);

	}

}
