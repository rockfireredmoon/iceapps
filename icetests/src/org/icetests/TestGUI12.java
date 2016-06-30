package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import com.jme3.font.BitmapFont;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.lists.SelectList;
import icetone.controls.text.Label;
import icetone.controls.windows.Panel;
import icetone.core.Container;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.layout.LUtil;
import icetone.core.layout.mig.MigLayout;

public class TestGUI12 extends IcesceneApp {
	
	static {
		AppInfo.context = TestGUI12.class;
	}

	public static void main(String[] args) {
		TestGUI12 app = new TestGUI12();
		app.start();
	}

	public TestGUI12() {
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
		panel.setLayoutManager(new MigLayout(screen, "", "[grow, fill]", "[][][][fill, grow]"));

		// Buttons
		Container buttons = new Container();
		buttons.setLayoutManager(
				new MigLayout("fill", "[align center, shrink]4!:push[align center, shrink]4!:push[align center, shrink]"));
		buttons.addChild(new ButtonAdapter("Create"), "");
		buttons.addChild(new ButtonAdapter("Edit"), "");
		buttons.addChild(new ButtonAdapter("Delete"), "");
		panel.addChild(buttons, "wrap");

		// Text
		Label label = new Label("CharactersLabel");
		label.setText("Your Characters");
		label.setFontSize(40f);
		panel.addChild(label, "wrap");

		// Text
		label = new Label("CharactersLabel");
		label.setText("Select one ..");
		label.setFontSize(20f);
		panel.addChild(label, "wrap, growy");

		// Selection list
		SelectList<Integer> list = new SelectList<Integer>(screen);
		list.setTextVAlign(BitmapFont.VAlign.Top);
		panel.addChild(list, "");
		list.addListItem("List item 1", 1);

		// Add window to screen (causes a layout)
		screen.addElement(panel);

	}

	Element createPanel(ElementManager screen, String n) {
		Element p = new Element(screen, n, Vector2f.ZERO, LUtil.LAYOUT_SIZE, new Vector4f(15, 15, 15, 15), null);
		MigLayout l = new MigLayout(screen, "ins 0, gap 0, fill", "", "");
		p.setLayoutManager(l);

		Label l1 = new Label(screen, "Label1" + n, Vector2f.ZERO, Vector2f.ZERO);
		l1.setText("Label 1");
		p.addChild(l1, "wrap, height 20, width 100%, shrink");

		Label l2 = new Label(screen, "Label1" + n, Vector2f.ZERO, Vector2f.ZERO);
		l2.setTextVAlign(BitmapFont.VAlign.Top);
		l2.setText("Label 2");
		p.addChild(l2, "grow, height 100%, width 100%, aligny top");
		return p;
	}

}
