package org.icetests;

import org.icescene.IcesceneApp;
import org.icescene.SceneConstants;
import org.iceui.controls.XScreen;
import org.iceui.controls.XSeparator;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;

import icetone.controls.text.Label;
import icetone.controls.windows.Panel;
import icetone.controls.windows.Window;
import icetone.core.Element;
import icetone.core.ToolTipProvider;
import icetone.core.layout.mig.MigLayout;

public class TestDefaultExtendedTooltips extends IcesceneApp implements ToolTipProvider {

    public static void main(String[] args) {
        TestDefaultExtendedTooltips app = new TestDefaultExtendedTooltips();
        app.start();
    }
    
    public TestDefaultExtendedTooltips() {
        setUseUI(true);
    }

    @Override
    protected void createScreen() {
        screen = new XScreen(this, SceneConstants.UI_DEFAULT_THEME){
            @Override
            public ToolTipProvider createDefaultToolTipProvider(Element element) {
                if(element.getUID().equals("HoverOk")) {
                    return TestDefaultExtendedTooltips.this;
                }
                return null;
            }
        };
        screen.setUseToolTips(true);
    }
    @Override
    public void onSimpleInitApp() {
        flyCam.setDragToRotate(true);

        Window window = new Window(screen, new Vector2f(100, 100));
        window.getDragBar().setText("Drag Test");
        window.setIsMovable(true);
        window.setIsResizable(false);
        
        Element dropOk = new Element(screen, "HoverOk", new Vector2f(150, 60), new Vector2f(180, 20), Vector4f.ZERO, null);
        dropOk.setText("Hover here!"); 
        dropOk.setIsDragDropDropElement(true);
        
        window.addChild(dropOk);
        screen.addElement(window);
        guiNode.addControl(screen);

    }

    public Element createToolTip() {
        Panel extendedToolTip = new Panel(screen);
        extendedToolTip.setLayoutManager(new MigLayout(screen, "wrap 1", "[]", "[][][][]"));
        Label l1 = new Label(screen);
        l1.setText("Label 1");
        extendedToolTip.addChild(l1);
        extendedToolTip.addChild(new XSeparator(screen, Element.Orientation.HORIZONTAL));
        Label l2 = new Label(screen);
        l2.setText("Label 2");
        extendedToolTip.addChild(l2);
        extendedToolTip.addChild(new XSeparator(screen, Element.Orientation.HORIZONTAL));
        Label l3 = new Label(screen);
        l3.setText("Label 3");
        extendedToolTip.addChild(l3);
        extendedToolTip.addChild(new XSeparator(screen, Element.Orientation.HORIZONTAL));
        screen.addElement(extendedToolTip);
        extendedToolTip.pack(false);
        return extendedToolTip;
    }
}
