package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;
import org.iceui.controls.ElementStyle;
import org.iceui.controls.SelectArea;

import com.jme3.font.BitmapFont;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.buttons.PushButton;
import icetone.controls.buttons.SelectableItem;
import icetone.controls.containers.Frame;
import icetone.controls.text.Label;
import icetone.core.StyledContainer;
import icetone.core.ZPriority;
import icetone.core.layout.mig.MigLayout;

public class TestSelectArea extends IcesceneApp {

	static {
		AppInfo.context = TestSelectArea.class;
	}

	public static void main(String[] args) {
		TestSelectArea app = new TestSelectArea();
		app.start();
	}

	public TestSelectArea() {
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {
		screen.setUseUIAudio(false);
		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);

		Box b = new Box(1, 1, 1);
		Geometry geom = new Geometry("Box", b);

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom.setMaterial(mat);
		rootNode.attachChild(geom);

		SelectArea scr = new SelectArea();
		for (int i = 0; i < 10; i++) {
			scr.addScrollableContent(createPanel("Item number " + i));
		}

		// Buttons
		StyledContainer buttons = new StyledContainer(screen);
		buttons.setLayoutManager(new MigLayout(screen));
		buttons.addElement(new PushButton(screen, "Create"), "");
		buttons.addElement(new PushButton(screen, "Edit"), "");
		buttons.addElement(new PushButton(screen, "Delete"), "");
		buttons.addElement(new PushButton(screen, "Exit"), "");

		// Panel for actions and character selection
		Frame panel = new Frame() {
			{
				setStyleClass("large");
			}
		};
		panel.getContentArea()
				.setLayoutManager(new MigLayout("gap 0, ins 0, wrap 1", "[fill, grow]", "[grow][shrink 0]"));
		panel.setMovable(false);
		panel.getContentArea().setResizable(false);
		panel.getContentArea().addElement(scr);
		panel.getContentArea().addElement(buttons);
		panel.sizeToContent();

		StyledContainer layer = new StyledContainer();
		layer.setLayoutManager(new MigLayout("fill", "[][fill, grow]", "[fill, grow]"));
		layer.addElement(panel, "growx, growy");
		layer.addElement(new Label("Right hand side label"));

		getLayers(ZPriority.NORMAL).addElement(layer);

	}

	SelectableItem createPanel(String n) {
		SelectableItem p = new SelectableItem();
		p.setLayoutManager(new MigLayout("ins 0, wrap 1, fill", "[][][grow, fill]", ""));
		p.addElement(new PushButton("Button!"));
		p.addElement(ElementStyle.medium(new Label(n)));
		Label l2 = new Label("Label 2 for " + n);
		l2.setTextVAlign(BitmapFont.VAlign.Top);
		p.addElement(l2);
		return p;
	}
}
