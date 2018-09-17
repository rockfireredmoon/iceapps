package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import com.jme3.font.BitmapFont;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.containers.Panel;
import icetone.controls.text.Label;
import icetone.core.layout.Border;
import icetone.core.layout.BorderLayout;
import icetone.core.layout.FlowLayout;
import icetone.extras.chooser.ColorFieldControl;

public class TestScreenBorderLayout extends IcesceneApp {
	
	static {
		AppInfo.context = TestScreenBorderLayout.class;
	}

    public static void main(String[] args) {
        TestScreenBorderLayout app = new TestScreenBorderLayout();
        app.start();
    }
    private ColorFieldControl color;

    public TestScreenBorderLayout() {
        setUseUI(true);
        setResizable(true);
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

        screen.setLayoutManager(new BorderLayout());

        Panel center = new Panel();
        center.setLayoutManager(new FlowLayout(0, BitmapFont.Align.Center));
        center.addElement(new Label("Some label text"));

        // Add window to screen (causes a layout)        
        screen.addElement(center, Border.CENTER);
        screen.addElement(new Label("North"), Border.NORTH);
        screen.addElement(new Label("East"), Border.EAST);
        screen.addElement(new Label("West"), Border.WEST);
        screen.addElement(new Label("South"), Border.SOUTH);

    }
}
