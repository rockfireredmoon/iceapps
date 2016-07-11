package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.buttons.Button;
import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.buttons.CheckBox;
import icetone.controls.text.Label;
import icetone.controls.text.XHTMLToolTipProvider;
import icetone.controls.util.ToolTip;
import icetone.controls.util.XHTMLToolTip;
import icetone.controls.windows.Panel;
import icetone.core.Element;
import icetone.core.ToolTipProvider;
import icetone.core.layout.mig.MigLayout;

public class TestToolTip extends IcesceneApp {

	static {
		AppInfo.context = TestToolTip.class;
	}

	public static void main(String[] args) {
		TestToolTip app = new TestToolTip();
		app.start();
	}

	public TestToolTip() {
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {
		screen.setUseToolTips(true);

		flyCam.setMoveSpeed(10);
		flyCam.setDragToRotate(true);

		Box b = new Box(1, 1, 1);
		Geometry geom = new Geometry("Box", b);

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom.setMaterial(mat);

		rootNode.attachChild(geom);

		Panel win = new Panel(new MigLayout());

		Button l1 = new ButtonAdapter("Standard");
		l1.setToolTipText("Standard tooltip");
		win.addChild(l1);

		Label l2 = new Label("Label Standard");
		l2.setToolTipText("Label Standard tooltip");
		win.addChild(l2);

		Button l3 = new ButtonAdapter("Extended");
		l3.setToolTipProvider(new ToolTipProvider() {

			@Override
			public Element createToolTip(Vector2f mouseXY, Element el) {
				ToolTip tt = new ToolTip(screen);
				tt.setLayoutManager(new MigLayout());
				tt.addChild(new ButtonAdapter("Button"), "wrap");
				tt.addChild(new Label("Label"), "wrap");
				tt.addChild(new CheckBox(), "wrap");
				tt.sizeToContent();
				return tt;
			}
		});
		win.addChild(l3);

		Label l4 = new Label("Large");
		l4.setToolTipText("Some thing\n\nThe Daemons have traveled a hard road to Earth, escaping the "
				+ "clutches of the Demon King Dagon to come here and facing the "
				+ "prejudices of those who view them as too alien to be Beasts. "
				+ "Despite their appearance, they are often fiercely dedicated to" + "fighting evil.");
		win.addChild(l4);

		final Label l5 = new Label("LrgXHTML");
		l5.setToolTipText("<h5>Some thing</h5><p>The Daemons have traveled a hard road to Earth, escaping the "
				+ "clutches of the Demon King Dagon to come here and facing the "
				+ "prejudices of those who view them as too alien to be Beasts. "
				+ "Despite their appearance, they are often fiercely dedicated to" + "fighting evil.</p>");

		l5.setToolTipProvider(new ToolTipProvider() {

			@Override
			public Element createToolTip(Vector2f mouseXY, Element el) {
				XHTMLToolTip tip = new XHTMLToolTip(l5.getToolTipText(), screen);
				// Vector2f mx = new Vector2f(screen.getWidth() - mouseXY.x,
				// screen.getHeight() - mouseXY.y);
				// System.out.println(mx);
				// tip.setMaxDimensions(mx);

				tip.sizeToContent();

				return tip;
			}
		});
		win.addChild(l5);
		final Label l6= new Label("SmlXHTML");
		l6.setToolTipText("<h5>Some thing</h5>");
		l6.setToolTipProvider(new XHTMLToolTipProvider());
		win.addChild(l6);

		screen.addElement(win);

	}

}
