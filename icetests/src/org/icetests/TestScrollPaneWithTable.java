/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.icetests;
 
import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector2f;

import icetone.controls.lists.Table;
import icetone.controls.scrolling.ScrollAreaAdapter;
import icetone.controls.text.Label;
import icetone.controls.windows.Window;
import icetone.core.Screen;
 
/**
*
* @author jo
*/
public class TestScrollPaneWithTable extends SimpleApplication {
 
    private Screen screen;
    private Window window;
    private ScrollAreaAdapter area;
 
    public static void main(String[] args) {
        new TestScrollPaneWithTable();
    }
 
    public TestScrollPaneWithTable(){
        this.start();
    }
 
    @Override
    public void simpleInitApp() {
        getFlyByCamera().setEnabled(false);
        getInputManager().setCursorVisible(true);
        initScreen();
        buildWindow();
        buildWindowContent();
 
        window.addChild(area);
        screen.addElement(window);
    }
 
    private void initScreen() {
        screen = new Screen(this);
        getGuiNode().addControl(screen);
    }
 
    public void buildWindow(){
        window = new Window(screen, "topWindow", new Vector2f(10, 10), new Vector2f(400, 400));
    }
 
    public void buildWindowContent(){
        area = new ScrollAreaAdapter(screen,
                "aScrollArea", new Vector2f(10, 40), new Vector2f(360, 300));
 
        addTitle(area);
        addTable(area);
    }
 
    public void addTitle(ScrollAreaAdapter area){
        Label titleLabel = new Label(screen, "titleLabel", new Vector2f(10, 10), new Vector2f(160, 30));
        titleLabel.setText("A random title");
        area.addScrollableChild(titleLabel);
    }
 
    public void addTable(ScrollAreaAdapter area){
        Table table = new Table(screen, new Vector2f(10, 110 + 100), new Vector2f(300, 140)) {
            @Override
            public void onChange() {
            }
        };
        table.setColumnResizeMode(Table.ColumnResizeMode.AUTO_FIRST);
        table.addColumn("Nbr :");
        table.addColumn("params");
 
        for(int i = 0; i < 10; i++){
            Table.TableRow row = new Table.TableRow(screen, table);
            row.addCell(i + "", i);
            row.addCell("coming", null);
            table.addRow(row);
        }
        area.addScrollableChild(table);
    }
}