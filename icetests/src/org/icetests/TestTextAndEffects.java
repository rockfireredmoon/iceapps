package org.icetests;

import com.jme3.app.SimpleApplication;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;

import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.text.Label;
import icetone.controls.windows.Window;
import icetone.core.Screen;

/**
 * Demonstrates text inside containers that use fade effect to show doesn't usually
 * show. This affects all sorts of windows, include things such as menus and combo boxes.
 */
public class TestTextAndEffects extends SimpleApplication {
    public static void main(String[] args) {
        TestTextAndEffects app = new TestTextAndEffects();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setDragToRotate(true);

        Screen screen = new Screen(this, "icetone/style/def/style_map.gui.xml");
        
        // Window 1 (show with effect)
        final Window window1 = new Window(screen, new Vector2f(200, 300));
        
        Label label1 = new Label(screen, new Vector2f(25, 70), new Vector2f(300, 24));
        label1.setText("This text usually won't show");
        window1.addChild(label1);
        
        screen.addElement(window1);
        window1.hide();
        window1.showWithEffect();
                
        // A button to allow window to be shown/hidden, showing that eventually text
        // will show if you click a few times
        ButtonAdapter button1 = new ButtonAdapter(screen, new Vector2f((screen.getWidth() / 2) - 100, screen.getHeight() - 50), new Vector2f(200, 32)) {
            @Override
            public void onButtonMouseLeftDown(MouseButtonEvent evt, boolean toggled) {
                if(window1.getIsVisible()) {
                    window1.hideWithEffect();
                }
                else {
                    window1.showWithEffect();
                }
            }
        };
        button1.setText("Toggle problem window");
        screen.addElement(button1);
        
        //
        guiNode.addControl(screen);

    }

    @Override
    public void simpleUpdate(float tpf) {
    }

    @Override
    public void simpleRender(RenderManager rm) {
    }
}
