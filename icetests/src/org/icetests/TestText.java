package org.icetests;

import java.util.Arrays;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import com.jme3.font.BitmapFont.Align;
import com.jme3.font.BitmapFont.VAlign;
import com.jme3.font.LineWrapMode;
import com.jme3.math.Vector4f;

import icetone.controls.containers.Panel;
import icetone.controls.text.Label;
import icetone.core.Size;
import icetone.core.layout.mig.MigLayout;

public class TestText extends IcesceneApp {

	static {
		AppInfo.context = TestText.class;
	}

	public static void main(String[] args) {
		TestText app = new TestText();
		app.start();
	}

	public TestText() {
		setUseUI(true);
	}

	@Override
	public void onSimpleInitApp() {
		Panel panel = new Panel(new MigLayout());

		panel.addElement(new Label("Ordinary label"));
		panel.addElement(new Label("Hello world!").setTexture("/bgw.jpg").setTextWrap(LineWrapMode.NoWrap), "wrap");

		for (VAlign val : VAlign.values()) {
			for (Align hal : Align.values()) {
				panel.addElement(new Label(String.format("%s,%s", val, hal)));
				panel.addElement(new Label("Hello world!").setTexture("/bgw.jpg")
						.setPreferredDimensions(new Size(200, 32)).setTextVAlign(val).setTextAlign(hal), "wrap");
			}
		}

		for (Vector4f v : Arrays.asList(new Vector4f(20, 0, 0, 0), new Vector4f(0, 20, 0, 0), new Vector4f(0, 0, 20, 0),
				new Vector4f(0, 0, 0, 20))) {
			panel.addElement(new Label(String.format("%s", v)));
			panel.addElement(new Label(v.toString()).setTexture("/bgw.jpg").setTextPadding(v), "wrap");
		}

		// panel.addElement(new Label("Anim text (char wrap)"));
		// panel.addElement(new TElement(LineWrapMode.Character), "wrap");
		// panel.addElement(new Label("Anim text (word wrap)"));
		// panel.addElement(new TElement(LineWrapMode.Word), "wrap");
		// panel.addElement(new Label("Anim text (no wrap)"));
		// panel.addElement(new TElement(LineWrapMode.NoWrap), "wrap");
		//
		// panel.addElement(new Label("Anim text in larger bounds"));
		// panel.addElement(new
		// TElement(LineWrapMode.Word).setPreferredDimensions(new Vector2f(200,
		// 32)));
		//
		// panel.addElement(new Button("Layout").bindReleased(evt -> {
		// panel.dirtyLayout(true, LayoutType.all);
		// panel.layoutChildren();
		// }), "wrap");

		screen.addElement(panel);

	}

}
