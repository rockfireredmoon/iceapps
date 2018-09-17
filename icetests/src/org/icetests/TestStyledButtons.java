package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapFont.Align;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.buttons.Button;
import icetone.controls.buttons.ButtonGroup;
import icetone.controls.buttons.CheckBox;
import icetone.controls.buttons.PushButton;
import icetone.controls.containers.Panel;
import icetone.controls.lists.ComboBox;
import icetone.controls.text.Label;
import icetone.core.StyledContainer;
import icetone.core.layout.Border;
import icetone.core.layout.BorderLayout;
import icetone.core.layout.mig.MigLayout;

public class TestStyledButtons extends IcesceneApp {

	static {
		AppInfo.context = TestStyledButtons.class;
	}

	public static void main(String[] args) {
		TestStyledButtons app = new TestStyledButtons();
		app.start();
	}

	private StyledContainer buttons;
	private CheckBox fill;
	private CheckBox icons;
	private ComboBox<Align> iconAlign;
	private boolean adjusting;
	private Panel p;

	public TestStyledButtons() {
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {
		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);
		adjusting = true;

		try {
			Box b = new Box(1, 1, 1);
			Geometry geom = new Geometry("Box", b);

			Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
			mat.setColor("Color", ColorRGBA.Blue);
			geom.setMaterial(mat);

			rootNode.attachChild(geom);

			ButtonGroup<PushButton> genders = new ButtonGroup<>();
			Button male = new Button(screen) {
				{
					setStyleClass("gender character-attribute");
					setStyleId("gender-male");
				}
			};
			genders.addButton(male);
			Button female = new Button(screen) {
				{
					setStyleClass("gender character-attribute");
					setStyleId("gender-female");
				}
			};
			genders.addButton(female);

			buttons = new StyledContainer(screen);
			buttons.setLayoutManager(new MigLayout(screen));
			buttons.addElement(male);
			buttons.addElement(female);

			// Panel
			p = new Panel(screen);
			p.setLayoutManager(new BorderLayout());
			p.addElement(buttons, Border.CENTER);
			screen.showElement(p);

		} finally {
			adjusting = false;
		}
	}

}
