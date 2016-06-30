package org.icetests;

import org.icescene.IcesceneApp;
import org.iceui.XTabPanelContent;
import org.iceui.controls.IconTabControl;
import org.iceui.controls.SelectableItem;
import org.iceui.controls.TabPanelContent;

import com.jme3.font.BitmapFont;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.scrolling.ScrollArea;
import icetone.controls.text.Label;
import icetone.controls.windows.Panel;
import icetone.core.ElementManager;
import icetone.core.layout.FillLayout;
import icetone.core.layout.LUtil;
import icetone.core.layout.mig.MigLayout;

public class TestIconTabs extends IcesceneApp {

    public static void main(String[] args) {
        TestIconTabs app = new TestIconTabs();
        app.start();
    }
    
    public TestIconTabs() {
        setUseUI(true);
    }

    @Override
    public void onSimpleInitApp() {
        screen.setUseCustomCursors(true);
        
        flyCam.setMoveSpeed(10);
        flyCam.setDragToRotate(true);

        Box b = new Box(1, 1, 1);
        Geometry geom = new Geometry("Box", b);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);
        rootNode.attachChild(geom);


        ScrollArea s1 = new ScrollArea(screen, Vector2f.ZERO, LUtil.LAYOUT_SIZE, Vector4f.ZERO, null,  false);
        ScrollArea s2 = new ScrollArea(screen, Vector2f.ZERO, LUtil.LAYOUT_SIZE, Vector4f.ZERO, null,  false);

        for (int i = 0; i < 10; i++) {
            Label l1 = new Label(screen);
            l1.setText("Label A-" + i);
            s1.addScrollableChild(l1);
            Label l2 = new Label(screen);
            l2.setText("Label B-" + i);
            s2.addScrollableChild(l2);
        }

        TabPanelContent content1 = new XTabPanelContent(screen, "TabContent1");
        content1.addChild(s1);

        TabPanelContent content2 = new XTabPanelContent(screen, "TabContent2");
        content2.addChild(s2);

        IconTabControl tabs = new IconTabControl(screen);
        tabs.addTabWithIcon("Some tooltip", "Interface/Styles/Gold/Common/Icons/select.png");
        tabs.addTabChild(0, content1);
        tabs.addTabWithIcon("Some tooltip", "Interface/Styles/Gold/Common/Icons/brush.png");
        tabs.addTabChild(1, content2);

        // Panel for actions and character selection
        Panel xcw = new Panel(screen, new Vector2f(100, 100), new Vector2f(400, 400));
        xcw.setLayoutManager(new FillLayout());
        xcw.addChild(tabs);
     
//        xcw.pack(false);
        // Add window to screen (causes a layout)  
        screen.addElement(xcw); 
        xcw.show();

    }

    SelectableItem createPanel(ElementManager screen, String n) {
        SelectableItem p = new SelectableItem(screen, n);
        p.setIgnoreMouse(true);
        MigLayout l = new MigLayout(screen, "ins 0, wrap 1, fill", "[grow, fill]", "");
        p.setLayoutManager(l);

        Label l1 = new Label(screen, "Label1" + n);
        l1.setText(n);
        l1.setIgnoreMouse(true);
        l1.setFontSize(screen.getStyle("Common").getFloat("mediumFontSize"));
        p.addChild(l1);

        Label l2 = new Label(screen, "Label1X" + n);
        l2.setTextVAlign(BitmapFont.VAlign.Top);
        l2.setText("Label 2 for " + n);
        l2.setIgnoreMouse(true);
        p.addChild(l2);
        return p;
    }
}
