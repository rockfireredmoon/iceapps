package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;
import org.iceui.controls.BigButton;

import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapFont.VAlign;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.buttons.CheckBox;
import icetone.controls.windows.Panel;
import icetone.core.layout.FlowLayout;
import icetone.core.layout.mig.MigLayout;

public class TestButton extends IcesceneApp {

	static {
		AppInfo.context = TestButton.class;
	}

	public static void main(String[] args) {
		TestButton app = new TestButton();
		app.start();
	}

	public TestButton() {
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

		// Panel for actions and character selection
		Panel xcw = new Panel();
		xcw.setLayoutManager(new MigLayout());
		
//		//
		ButtonAdapter b1 = new ButtonAdapter("Standard Text");
		xcw.addChild(b1);
		b1.setToolTipText("Button with just text");
		
		//
		ButtonAdapter b2 = new ButtonAdapter(16, 16, screen.getStyle("Common").getString("arrowLeft"));
		xcw.addChild(b2);
		b2.setToolTipText("Button with just icon of fixed size");
		
		//
		ButtonAdapter b3 = new ButtonAdapter(16, 16, screen.getStyle("Common").getString("arrowLeft"), "With Icon");
		xcw.addChild(b3);
		b3.setToolTipText("Button with icon of fixed size, and text");
		
		//
		ButtonAdapter b4 = new ButtonAdapter(-1, -1, screen.getStyle("Common").getString("arrowLeft"));
		xcw.addChild(b4);
		b4.setToolTipText("Button with default sized icon");
		
		//
		ButtonAdapter b5 = new ButtonAdapter(screen.getStyle("Common").getString("arrowLeft"), "With Icon");
		xcw.addChild(b5);
		b5.setToolTipText("Button with default sized icon and text");

		//
		ButtonAdapter b6 = new ButtonAdapter(16, 16, screen.getStyle("Common").getString("arrowLeft"), "Right Align");
		xcw.addChild(b6);
		b6.setButtonIconAlign(Align.Right);
		b6.setToolTipText("Button with icon and text with icon right aligned");

		//
		ButtonAdapter b7 = new ButtonAdapter(32, 32, screen.getStyle("Common").getString("arrowLeft"), "CenTop");
		xcw.addChild(b7);
		b7.setTextVAlign(VAlign.Top);
		b7.setButtonIconAlign(Align.Center);
		b7.setToolTipText("Button with icon and text with icon center top aligned");
		
		ButtonAdapter b8 = new ButtonAdapter(32, 32, screen.getStyle("Common").getString("arrowLeft"), "CenBot");
		b8.setTextVAlign(VAlign.Bottom);
		b8.setButtonIconAlign(Align.Center);
		b8.setToolTipText("Button with icon and text with icon center bottom aligned");
		xcw.addChild(b8, "wrap");
		
		CheckBox c1 = new CheckBox();
		c1.setToolTipText("Checkbox with no text");
		xcw.addChild(c1);
		//
		CheckBox c2 = new CheckBox("Checkbox Text");
		c2.setToolTipText("Checkbox with some text");
		xcw.addChild(c2, "wrap");
		
		BigButton bb = new BigButton("Big Button!!!", screen);
		xcw.addChild(bb, "wrap");
		
		xcw.sizeToContent();
		xcw.setDimensions(500, 500);

		screen.addElement(xcw);

	}

}
