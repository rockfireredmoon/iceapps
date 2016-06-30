package org.icetests;

import java.util.Set;
import java.util.logging.Logger;

import org.icelib.Icelib;
import org.icescene.IcesceneApp;
import org.icescene.ogreparticle.OGREParticleConfiguration;
import org.icescene.ogreparticle.OGREParticleEmitter;
import org.icescene.ogreparticle.OGREParticleScript;

import com.jme3.math.Vector2f;
import com.jme3.scene.Node;

import emitter.Emitter;
import icemoon.iceloader.ServerAssetManager;
import icetone.controls.lists.Table;
import icetone.controls.windows.Panel;
import icetone.core.layout.mig.MigLayout;

public class TestParticles extends IcesceneApp {

    public static void main(String[] args) {
        TestParticles app = new TestParticles();
        app.start();
    }
    private Table table;
    private Node node;

    public TestParticles() {
        setUseUI(true);
//        setStylePath("icetone/style/def/style_map.gui.xml");


    }

    @Override
    public void onSimpleInitApp() {

        flyCam.setMoveSpeed(10);
        flyCam.setDragToRotate(true);

        Panel panel = new Panel(screen, "Panel",
                new Vector2f(8, 8), new Vector2f(300f, 500));
        panel.setLayoutManager(new MigLayout(screen, "fill", "[grow]", "[grow]"));

        table = new Table(screen) {
            @Override
            public void onChange() {
                TableRow selRow = getSelectedRow();
                if (selRow != null && selRow.getParentRow() != null) {
                    OGREParticleScript cfg = (OGREParticleScript) selRow.getValue();
                    setConfig(cfg);
                }
            }
        };
        table.addColumn("Effect");
        table.setHeadersVisible(false);

        final Set<String> particleFiles = ((ServerAssetManager) getAssetManager()).getAssetNamesMatching(".*/TEST\\.particle");
        for (String s : particleFiles) {
            Table.TableRow row = new Table.TableRow(screen, table, s);
            row.setLeaf(false);
            Table.TableCell cell = new Table.TableCell(screen, Icelib.getBasename(Icelib.getFilename(s)), s);
            row.addChild(cell);
            table.addRow(row, false);

            OGREParticleConfiguration cgh = OGREParticleConfiguration.get(assetManager, s);
            for (OGREParticleScript g : cgh.getBackingObject().values()) {
                Table.TableRow crow = new Table.TableRow(screen, table, g);
                Table.TableCell ccell = new Table.TableCell(screen, g.getName(), g);
                crow.addChild(ccell);
                row.addRow(crow);
            }
        }
        table.pack();

        table.setColumnResizeMode(Table.ColumnResizeMode.AUTO_FIRST);
        panel.addChild(table, "growx, growy");

        screen.addElement(panel);

    }

    private void setConfig(OGREParticleScript cfg) {
        if (node != null) {
            node.removeFromParent();
        }
        node = new Node();
        for (OGREParticleEmitter i : cfg.getEmitters()) {
            final Emitter emitter = i.createEmitter(assetManager);
            emitter.setEnabled(true);
            node.addControl(emitter);
        }
        rootNode.attachChild(node);
    }
}
