package org.icetests;

import org.icelib.AppInfo;
import org.icescene.IcesceneApp;

import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapFont.Align;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

import icetone.controls.buttons.Button;
import icetone.controls.buttons.ButtonAdapter;
import icetone.controls.buttons.CheckBox;
import icetone.controls.lists.ComboBox;
import icetone.controls.text.Label;
import icetone.controls.windows.Panel;
import icetone.core.Container;
import icetone.core.Element;
import icetone.core.layout.BorderLayout;
import icetone.core.layout.FlowLayout;
import icetone.core.layout.mig.MigLayout;

public class TestFancyButtons extends IcesceneApp {
	
	static {
		AppInfo.context = TestFancyButtons.class;
	}

    public static void main(String[] args) {
        TestFancyButtons app = new TestFancyButtons();
        app.start();
    }
    private Container buttons;
    private CheckBox fill;
    private CheckBox icons;
    private ComboBox<Align> iconAlign;
    private boolean adjusting;

    public TestFancyButtons() {
        setUseUI(true);
    }

    @Override
    public void onSimpleInitApp() {
        flyCam.setMoveSpeed(10);
        flyCam.setDragToRotate(true);
        adjusting = true;

        try {
            Box b = new Box(1, 1, 1);
            Geometry geom = new Geometry("Box", b);

            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", ColorRGBA.Blue);
            geom.setMaterial(mat);

            rootNode.attachChild(geom);

            buttons = new Container(screen);

            // Tools
            Container tools = new Container(screen);
            tools.setLayoutManager(new FlowLayout());
            fill = new CheckBox(screen) {
                @Override
                public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                    rebuildButtons();
                }
            };
            fill.setLabelText("Fill");
            tools.addChild(fill);
            icons = new CheckBox(screen) {
                @Override
                public void onButtonMouseLeftUp(MouseButtonEvent evt, boolean toggled) {
                    rebuildButtons();
                }
            };
            icons.setLabelText("Icons");
            tools.addChild(icons);
            iconAlign = new ComboBox<Align>(screen) {
                @Override
                public void onChange(int selectedIndex, Align value) {
                    if (!adjusting) {
                        rebuildButtons();
                    }
                }
            };
            for (BitmapFont.Align a : BitmapFont.Align.values()) {
                iconAlign.addListItem(a.name(), a);
            }
            tools.addChild(iconAlign);

            // Panel
            Panel p = new Panel(screen, Vector2f.ZERO, new Vector2f(500, 500));
            p.setLayoutManager(new BorderLayout());
            p.addChild(buttons, BorderLayout.Border.CENTER);
            p.addChild(tools, BorderLayout.Border.SOUTH);
            screen.addElement(p);

            rebuildButtons();
            p.sizeToContent();
        } finally {
            adjusting = false;
        }
    }

    private void rebuildButtons() {
        buttons.removeAllChildren();
        buttons.setLayoutManager(new MigLayout(screen, "wrap 3", fill.getIsChecked() ? "[fill,grow][fill,grow][fill,grow]" : "[][][]", "[]"));
        Label l1;
        for (String s : new String[]{"FancyButton", "Button", "BigButton"}) {
            l1 = new Label(s, screen);
            buttons.addChild(l1, "span 3");
            createAndAddButton(buttons, s, "LeftTop", BitmapFont.Align.Left, BitmapFont.VAlign.Top);
            createAndAddButton(buttons, s, "LeftCenter", BitmapFont.Align.Left, BitmapFont.VAlign.Center);
            createAndAddButton(buttons, s, "LeftBottom", BitmapFont.Align.Left, BitmapFont.VAlign.Bottom);
            createAndAddButton(buttons, s, "CenterTop", BitmapFont.Align.Center, BitmapFont.VAlign.Top);
            createAndAddButton(buttons, s, "CenterCenter", BitmapFont.Align.Center, BitmapFont.VAlign.Center);
            createAndAddButton(buttons, s, "CenterBottom", BitmapFont.Align.Center, BitmapFont.VAlign.Bottom);
            createAndAddButton(buttons, s, "RightTop", BitmapFont.Align.Right, BitmapFont.VAlign.Top);
            createAndAddButton(buttons, s, "RightCenter", BitmapFont.Align.Right, BitmapFont.VAlign.Center);
            createAndAddButton(buttons, s, "RightBottom", BitmapFont.Align.Right, BitmapFont.VAlign.Bottom);
        }
    }

    private Button createAndAddButton(Element p, String style, String text, BitmapFont.Align h, BitmapFont.VAlign v) {
        ButtonAdapter b1;
        b1 = new ButtonAdapter(screen);
        b1.setText(text);
        b1.setStyles(style);
        b1.setTextAlign(h);
        b1.setTextVAlign(v);
        if (icons.getIsChecked()) {
            b1.setButtonIconAlign((BitmapFont.Align) iconAlign.getSelectedListItem().getValue());
            b1.setButtonIcon(16, 16, screen.getStyle("Common").getString("arrowRight"));
        }
        p.addChild(b1);
        return b1;
    }
}
