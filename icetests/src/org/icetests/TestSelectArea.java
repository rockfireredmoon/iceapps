package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;
import org.iceui.controls.ElementStyle;
import org.iceui.controls.FancyButton;
import org.iceui.controls.FancyWindow;
import org.iceui.controls.SelectArea;
import org.iceui.controls.SelectableItem;

import com.jme3.font.BitmapFont;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.text.Label;
import icetone.core.Container;
import icetone.core.Element.ZPriority;
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
		Container buttons = new Container(screen);
		buttons.setLayoutManager(new MigLayout(screen));
		buttons.addChild(new FancyButton("Create", screen), "");
		buttons.addChild(new FancyButton("Edit", screen), "");
		buttons.addChild(new FancyButton("Delete", screen), "");
		buttons.addChild(new FancyButton("Exit", screen), "");

		// Panel for actions and character selection
		FancyWindow panel = new FancyWindow(FancyWindow.Size.LARGE);
		panel.getContentArea().setLayoutManager(new MigLayout("gap 0, ins 0, wrap 1", "[fill, grow]", "[grow][shrink 0]"));
		panel.setIsMovable(false);
		panel.getContentArea().setIsResizable(false);
		panel.getContentArea().addChild(scr);
		panel.getContentArea().addChild(buttons);
		panel.sizeToContent();

		Container layer = new Container();
		layer.setLayoutManager(new MigLayout("fill", "[][fill, grow]", "[fill, grow]"));
		layer.addChild(panel, "growx, growy");
		layer.addChild(new Label("Right hand side label"));

		getLayers(ZPriority.NORMAL).addChild(layer);

	}

	SelectableItem createPanel(String n) {
		SelectableItem p = new SelectableItem();
		p.setLayoutManager(new MigLayout("ins 0, wrap 1, fill", "[][][grow, fill]", ""));
		p.addChild(new ButtonAdapter("Button!"));
		p.addChild(ElementStyle.medium(new Label(n)));
		Label l2 = new Label("Label 2 for " + n);
		l2.setTextVAlign(BitmapFont.VAlign.Top);
		p.addChild(l2);
		return p;
	}
}
