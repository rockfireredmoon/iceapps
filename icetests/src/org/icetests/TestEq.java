package org.icetests;

import org.icescene.IcesceneApp;
import org.iceui.XTabPanelContent;
import org.iceui.controls.IconTabControl;
import org.iceui.controls.SelectableItem;

import com.jme3.font.BitmapFont;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.text.Label;
import icetone.controls.windows.Panel;
import icetone.core.Element;
import icetone.core.ElementManager;
import icetone.core.layout.FillLayout;
import icetone.core.layout.XYLayoutManager;
import icetone.core.layout.mig.MigLayout;

public class TestEq extends IcesceneApp {

    public static void main(String[] args) {
        TestEq app = new TestEq();
        app.start();
    }
    
    public TestEq() {
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

        XTabPanelContent el = new XTabPanelContent(screen, "EquipmentContainer");
        el.setLayoutManager(new MigLayout(screen, "gap 0, ins 0, fill", "[align center]", "[align center]"));
//        el.setLayoutManager(new FillLayout());
        el.setIsMovable(false);
        el.setIsResizable(false);
        Element equipPanel = new Element(screen, "EquipmentPanel", Vector2f.ZERO,
                screen.getStyle("Equipment").getVector2f("defaultSize"),
                screen.getStyle("Equipment").getVector4f("resizeBorders"),
                screen.getStyle("Equipment").getString("defaultImg")) {
            @Override
	    public void setDimensions(float w, float h) {
		// TODO Auto-generated method stub
		super.setDimensions(w, h);
		System.err.println("setDimension " + w+ "," + h);
		try {
		    throw new Exception();
		}
		catch(Exception e) {
		    e.printStackTrace();
		}
	    }

	    @Override
	    public void setDimensions(Vector2f dimensions) {
		// TODO Auto-generated method stub
		super.setDimensions(dimensions);
		System.err.println("setDimension " + dimensions);
	    }

	    @Override
	    public void setWidth(float width) {
		// TODO Auto-generated method stub
		super.setWidth(width);
		System.err.println("setWidth " + width);
	    }

	    @Override
	    public void setHeight(float height) {
		// TODO Auto-generated method stub
		super.setHeight(height);
		System.err.println("setHeight " + height);
	    }
        };
        
//        Element equipPanel = new Element(screen, UIDUtil.getUID(), new Vector2f(100, 100), Vector4f.ZERO, "Interface/bgx.jpg") {
//
//	    @Override
//	    public void setDimensions(float w, float h) {
//		// TODO Auto-generated method stub
//		super.setDimensions(w, h);
//		System.err.println("setDimension " + w+ "," + h);
//	    }
//
//	    @Override
//	    public void setDimensions(Vector2f dimensions) {
//		// TODO Auto-generated method stub
//		super.setDimensions(dimensions);
//		System.err.println("setDimension " + dimensions);
//	    }
//
//	    @Override
//	    public void setWidth(float width) {
//		// TODO Auto-generated method stub
//		super.setWidth(width);
//		System.err.println("setWidth " + width);
//	    }
//
//	    @Override
//	    public void setHeight(float height) {
//		// TODO Auto-generated method stub
//		super.setHeight(height);
//		System.err.println("setHeight " + height);
//	    }
//            
//            
//        };
        el.addChild(equipPanel);
//      el.addChild(new Label("TESTTTT", screen));
        equipPanel.setLayoutManager(new XYLayoutManager());

        IconTabControl tabs = new IconTabControl(screen);
        tabs.addTabWithIcon("Some tooltip", "Interface/Styles/Gold/Common/Icons/select.png");
        tabs.addTabChild(0, el);

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
