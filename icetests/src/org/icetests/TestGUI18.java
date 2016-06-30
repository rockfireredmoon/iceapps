package org.icetests;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapFont;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.windows.Window;
import icetone.core.Screen;

public class TestGUI18 extends SimpleApplication {

    public static void main(String[] args) {
        TestGUI18 app = new TestGUI18();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(10);
        flyCam.setDragToRotate(true);

        Box b = new Box(1, 1, 1);
        Geometry geom = new Geometry("Box", b);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);
        rootNode.attachChild(geom);

        // Do the tonegod gui stuff :)
        Screen screen = new Screen(this, "org/icemoon/tonegodgui/style/def/style_map.xml");
        guiNode.addControl(screen);


        // Panel for actions and character selection
        Window panel = new Window(screen, "Panel",
                new Vector2f(8, 8), new Vector2f(300f,200));
        panel.setText("Your characters");
        panel.setTextVAlign(BitmapFont.VAlign.Top);
        panel.setTextPadding(6);
        panel.setIsResizable(true);

        // Text
//        Label label = new Label(screen, "CharactersLabel", new Vector2f(0, 0), new Vector2f(200, 40));
//        label.setText("Your Characters");
////        label.setFontSize(40f);
//        panel.getDragBar().addChild(label);

        // Add window to screen (causes a layout)        
        screen.addElement(panel);

    }

    @Override
    public void simpleUpdate(float tpf) {
    }

    @Override
    public void simpleRender(RenderManager rm) {
    }

}
