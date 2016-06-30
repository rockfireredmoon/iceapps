package org.icetests;

import org.icescene.IcesceneApp;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.text.Label;
import icetone.controls.windows.Panel;
import icetone.core.Container;
import icetone.core.layout.mig.MigLayout;

public class TestGUI14 extends IcesceneApp {

	public static void main(String[] args) {
		TestGUI14 app = new TestGUI14();
		app.start();
	}

	public TestGUI14() {
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

		// Panel for actions and character selection
		Panel panel = new Panel();
		// panel.setIsResizable(false);
		panel.setLayoutManager(new MigLayout("fill", "[grow]", "[][][]"));

		// Buttons
		Container buttons = new Container();
		buttons.setLayoutManager(
				new MigLayout("fill", "[align center, shrink]4!:push[align center, shrink]4!:push[align center, shrink]"));
		ButtonAdapter create = new ButtonAdapter("Create") {
			@Override
			public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
			}
		};
		buttons.addChild(create, "");
		ButtonAdapter edit = new ButtonAdapter("Edit");
		edit.setText("Edit");
		buttons.addChild(edit, "");
		ButtonAdapter delete = new ButtonAdapter("Delete");
		buttons.addChild(delete, "");
		panel.addChild(buttons, "wrap, growx");

		// Text
		Label label = new Label("Your Characters");
		label.setFontSize(40f);
		panel.addChild(label, "wrap, growx");

		// Selection list
		Container list = new Container();
		list.setLayoutManager(new MigLayout("fill"));
		panel.addChild(list, "growy, growx");

		final Label label1 = new Label();
		label1.setText("Some text");
		list.addChild(label1, "growx, growy");

		// Add window to screen (causes a layout)
		screen.addElement(panel);

	}

}
