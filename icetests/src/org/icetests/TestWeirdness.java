package org.icetests;

import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.renderer.RenderManager;

import icetone.controls.text.Label;
import icetone.controls.util.ToolTip;
import icetone.controls.windows.Window;
import icetone.core.Screen;

public class TestWeirdness extends SimpleApplication {
    //
    // Set a font other than default, any one will do. 
    //
    // VVVVVVVVVV    CHANGE THIS   VVVVVVVVVVV
    public static final String INTERFACE_STYLES_GOLD_FONTS_ARCADEPIXFNT = "Interface/Styles/Gold/Fonts/Maiandra_16.fnt";
    // ^^^^^^^^^^    CHANGE THIS   ^^^^^^^^^^^

    public static void main(String[] args) {
        TestWeirdness app = new TestWeirdness();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setDragToRotate(true);

        Screen screen = new Screen(this, "Interface/Styles/Gold/style_map.gui.xml");
//        Screen screen = new Screen(this, "icetone/style/def/style_map.gui.xml");
        screen.setUseToolTips(true);
        
        // Now we can get at tooltip and set the font. This is not exactly what I am
        // doing in my GUI, but thankfully the same bug occurs on standard tooltips using
        // this method
        ToolTip toolTip = (ToolTip)screen.getElementById("GlobalToolTip");
        toolTip.setFont(INTERFACE_STYLES_GOLD_FONTS_ARCADEPIXFNT);
        toolTip.setFontSize(16);
//        toolTip.setIsGlobalModal(false);
        
        // Window 1        
        Window window1 = new Window(screen, new Vector2f(400, 300));
        window1.getDragBar().setText("Window 1");
        
        Label label1 = new Label(screen, new Vector2f(25, 70), new Vector2f(300, 24));
        label1.setFont(INTERFACE_STYLES_GOLD_FONTS_ARCADEPIXFNT);
        label1.setFontSize(16);
        label1.setText("This text will start acting weird\nwhen tooltip is shown");
        window1.addChild(label1);
        
        // Window 2
        Window window2 = new Window(screen, new Vector2f(100, 100));
        window2.getDragBar().setText("Window 2");
        
        // Hover over the element to trigger the bug
        Label triggerBug = new Label(screen, "TriggerBug", new Vector2f(75, 90), new Vector2f(180, 20), Vector4f.ZERO, null);
        triggerBug.setText("Hover here to make text\nstart disappearing!");       
        triggerBug.setToolTipText("Now the text in the other window will appear or disappear when you hover");
        
        // Add and show
        window2.addChild(triggerBug);
        screen.addElement(window1);
        screen.addElement(window2);
        guiNode.addControl(screen);

    }

    @Override
    public void simpleUpdate(float tpf) {
    }

    @Override
    public void simpleRender(RenderManager rm) {
    }
}
