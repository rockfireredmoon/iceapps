package org.icetests;

import org.icescene.IcesceneApp;
import org.iceui.controls.ElementStyle;
import org.iceui.controls.SelectArea;
import org.iceui.controls.SelectableItem;

import com.jme3.font.BitmapFont;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.text.Label;
import icetone.controls.windows.Panel;
import icetone.core.ElementManager;
import icetone.core.layout.mig.MigLayout;

public class TestSelectAreaNonLayout extends IcesceneApp {

    public static void main(String[] args) {
        TestSelectAreaNonLayout app = new TestSelectAreaNonLayout();
        app.start();
    }
    
    public TestSelectAreaNonLayout() {
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
        Panel panel = new Panel(screen, "Panel",
                new Vector2f(8, 8), new Vector2f(300f, screen.getHeight() - 16));

        SelectArea scr = new SelectArea(screen) {

            @Override
            public void onChange() {
            }

            public void onMouseWheelPressed(MouseButtonEvent evt) {
            }

            public void onMouseWheelReleased(MouseButtonEvent evt) {
            }

            public void onMouseWheelUp(MouseMotionEvent evt) {
            }

            public void onMouseWheelDown(MouseMotionEvent evt) {
            }
            
        };
//        scr.setPadding(0);
//        scr.setScrollContentLayout(new MigLayout(screen, "wrap 1, ins 0, gap 0", "[grow, fill]", "[]"));
        panel.addChild(scr);

        for (int i = 0; i < 10; i++) {
            final SelectableItem createPanel = createPanel(screen, "Item number " + i);
            scr.addListItem(createPanel);
            scr.getScrollContentLayout().constrain(createPanel, "");
        }

        // Add window to screen (causes a layout)        
        screen.addElement(panel);

    }

    SelectableItem createPanel(ElementManager screen, String n) {
        SelectableItem p = new SelectableItem(screen, n);
        p.setIgnoreMouse(true);
        MigLayout l = new MigLayout(screen, "ins 0, wrap 1, fill", "[grow, fill]", "");
        p.setLayoutManager(l);

        Label l1 = new Label(screen, "Label1" + n);
        l1.setText(n);
        l1.setIgnoreMouse(true);
        ElementStyle.medium(screen, l1);
        p.addChild(l1);

        Label l2 = new Label(screen, "Label1X" + n);
        l2.setTextVAlign(BitmapFont.VAlign.Top);
        l2.setText("Label 2 for " + n);
        l2.setIgnoreMouse(true);
        p.addChild(l2);
        return p;
    }
}
