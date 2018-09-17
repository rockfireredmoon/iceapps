package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Box;

import icetone.controls.containers.Frame;
import icetone.controls.containers.Panel;
import icetone.controls.extras.Indicator;
import icetone.controls.extras.Indicator.BarMode;
import icetone.controls.extras.Indicator.DisplayMode;
import icetone.controls.text.Label;
import icetone.core.Orientation;
import icetone.core.layout.mig.MigLayout;

public class TestIndicator extends IcesceneApp {

	static {
		AppInfo.context = TestIndicator.class;
	}

	public static void main(String[] args) {
		TestIndicator app = new TestIndicator();
		app.start();
	}

	private Indicator i;

	public TestIndicator() {
		setUseUI(true);
		// setStylePath("icetone/style/def/style_map.gui.xml");

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

		Panel panel = new Panel(new MigLayout("fill, wrap 1", "[]", "[][]"));
		panel.addElement(
				(i = new Indicator().setDisplayMode(DisplayMode.percentages).setMaxValue(100).setCurrentValue(75))
						.setIndicatorText("Indicator Text"),
				"growx");
		i.addControl(new UpdateControl());
		panel.setPosition(200, 200);
		panel.addElement(new Label("Above is a progress bar type thing", screen), "ax 50%");

		Panel panel2 = new Panel(new MigLayout("fill, wrap 1", "[]", "[][]"));
		panel2.addElement((i = new Indicator().setReverseDirection(true).setDisplayMode(DisplayMode.percentages)
				.setMaxValue(100).setCurrentValue(75)).setIndicatorText("Indicator Text"), "growx");
		panel2.addElement(new Label("Above is a progress bar type thing", screen), "ax 50%");
		panel2.setPosition(300, 300);
		i.addControl(new UpdateControl());

		Frame win3 = new Frame(screen, false) {
			{
				setStyleClass("large");
			}
		};
		win3.getDragBar().setText("3 Fancy Window 3!");
		win3.getContentArea().setLayoutManager(new MigLayout(screen, "fill, wrap 1", "[]", "[][]"));
		Label l = new Label("This is a 2nd line");
		win3.getContentArea().addElement(l, "growx");
		win3.getContentArea().addElement((i = new Indicator().setMaxValue(100).setCurrentValue(25)), "growx");
		panel2.setPosition(400, 400);
		i.addControl(new UpdateControl());

		Panel panel3 = new Panel(new MigLayout("fill, wrap 1", "[]", "[][]"));
		panel3.addElement(
				(i = new Indicator().setOrientation(Orientation.VERTICAL).setDisplayMode(DisplayMode.percentages)
						.setMaxValue(100).setCurrentValue(75)).setIndicatorText("Indicator Text"),
				"growy, ax 50%");
		i.addControl(new UpdateControl());
		panel3.setPosition(500, 500);
		panel3.addElement(new Label("Vertical!", screen), "ax 50%");

		Panel panel4 = new Panel(new MigLayout("fill, wrap 1", "[]", "[][]"));
		panel4.addElement((i = new Indicator().setOrientation(Orientation.VERTICAL)
				.setDisplayMode(DisplayMode.percentages).setMaxValue(100).setCurrentValue(75))
						.setIndicatorText("Indicator Text").setReverseDirection(true),
				"growy, ax 50%");
		i.addControl(new UpdateControl());
		panel4.setPosition(600, 500);
		panel4.addElement(new Label("VerticalR", screen), "ax 50%");

		Panel panel5 = new Panel(new MigLayout("fill, wrap 1", "[]", "[][]"));
		panel5.addElement((i = new Indicator() {
			{
				setStyleClass("test-red-indicator");
			}
		}.setDisplayMode(DisplayMode.percentages).setMaxValue(100).setCurrentValue(75))
				.setIndicatorText("Indicator Text").setBarMode(BarMode.resize), "growx");
		panel5.addElement(new Label("Above is a progress bar type thing", screen), "ax 50%");
		panel5.setPosition(150, 1550);
		i.addControl(new UpdateControl());

		screen.showElement(panel);
		screen.showElement(panel2);
		screen.showElement(win3);
		screen.showElement(panel3);
		screen.showElement(panel4);
		screen.showElement(panel5);

	}

	class UpdateControl extends AbstractControl {

		@Override
		protected void controlUpdate(float tpf) {
			Indicator i = (Indicator) spatial;
			i.setCurrentValue(i.getCurrentValue() + tpf);
			if (i.getCurrentValue() >= i.getMaxValue()) {
				i.setCurrentValue(0);
			}
		}

		@Override
		protected void controlRender(RenderManager rm, ViewPort vp) {
		}

	}

}
