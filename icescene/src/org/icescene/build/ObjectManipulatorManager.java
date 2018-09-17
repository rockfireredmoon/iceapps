package org.icescene.build;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.icelib.Icelib;
import org.icescene.io.ModifierKeysAppState;
import org.icescene.io.MouseManager;
import org.icescene.scene.AbstractBuildableControl;
import org.icescene.scene.Buildable;

import com.jme3.app.SimpleApplication;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class ObjectManipulatorManager<T extends Buildable, S extends AbstractBuildableControl<T>> implements SelectionManager.Listener<T, S> {

    class NodeWrapper {

        private Vector3f ungroupedWorldLocation;
        private Quaternion ungroupedWorldRotation;
        private Node parent;
        private Vector3f ungroupedWorldScale;

        private NodeWrapper(Node parent) {
            this.parent = parent;
        }

        private boolean hasDragged() {
            return ungroupedWorldLocation != null;
        }

        private void dragged(Vector3f worldTranslation, Quaternion worldRotation, Vector3f worldScale) {
            ungroupedWorldLocation = worldTranslation;
            ungroupedWorldRotation = worldRotation;
            ungroupedWorldScale = worldScale;
        }
    }
    private final SelectionManager<T, S> selectionManager;
    private final List<S> selection = new ArrayList<S>();
    private final List<NodeWrapper> parents = new ArrayList<NodeWrapper>();
    private final Node rootNode;
    private final SimpleApplication app;
    private Spatial buildableSpatial;

    public ObjectManipulatorManager(Node rootNode, SimpleApplication app, SelectionManager<T, S> selectionManager) {
        this.selectionManager = selectionManager;
        this.rootNode = rootNode;
        this.app = app;

        selectionManager.addListener(this);
    }

    public void cleanup() {
        selectionManager.removeListener(this);
    }

    public void selectionChanged(SelectionManager<T, S> source) {
        // Clear up the previous selection
        if (!selection.isEmpty()) {
            if (buildableSpatial != null) {
                // If this was group selection, re-attach to their original parents
                if (buildableSpatial instanceof Node && buildableSpatial.getName().equals("SelectionGroup")) {
                    Iterator<NodeWrapper> pIt = parents.iterator();
                    for (Spatial s : ((Node) buildableSpatial).getChildren()) {
                        s.removeFromParent();
                        final NodeWrapper next = pIt.next();
                        next.parent.attachChild(s);
                        if (next.hasDragged()) {
                            s.setLocalTranslation(next.ungroupedWorldLocation.x, next.ungroupedWorldLocation.y, next.ungroupedWorldLocation.z);
                            s.setLocalRotation(next.ungroupedWorldRotation);
                            s.setLocalScale(next.ungroupedWorldScale);
                        }
                    }
                    buildableSpatial.removeFromParent();
                }
                buildableSpatial.removeControl(ObjectManipulatorControl.class);
            }
            parents.clear();
            selection.clear();
            buildableSpatial = null;
        }
        selection.addAll(source.getSelection());

        // Decide whether to work directly on single selection, or group a node for group ops
        if (selection.size() == 1) {
            final S buildable = selection.get(0);
            // Single selection, just attach the manipulator to the spatial and send
            // updates to its BuildControl
            buildableSpatial = buildable.getSpatial();
            buildableSpatial.addControl(new ObjectManipulatorControl(rootNode, selectionManager.getMouseManager(), app) {
                @Override
                protected void moveSpatial(float x, float y, float z) {
                    buildable.moveBuildable(new Vector3f(x, y, z));
                }

                @Override
                protected void rotateSpatial(float x, float y, float z) {
                	Icelib.removeMe("x: " + x + " y: " + y + " z: " +z);
                    buildable.rotateBuildable(x, y, z);
                }

				@Override
				protected void apply() {
					buildable.applyChanges();
				}
            });
        } else if (selection.size() > 1) {
            // Group selection, move everything that is selected into a new Node and attach
            // the manipulator to that instead
            Node node = new Node("SelectionGroup");
            for (S buildable : selection) {
                Spatial s = buildable.getSpatial();
                parents.add(new NodeWrapper(s.getParent()));
                s.removeFromParent();
                node.attachChild(s);
            }
            buildableSpatial = node;
            rootNode.attachChild(buildableSpatial);

            // For multiple selection, wait until drag ends to apply to the BuildControl
            buildableSpatial.addControl(new ObjectManipulatorControl(rootNode, selectionManager.getMouseManager(), app) {
                @Override
                public void dragEnd(MouseManager manager, Spatial spatial, ModifierKeysAppState mods, int startModsMask) {
                    Iterator<NodeWrapper> pIt = parents.iterator();
                    for (S buildable : selection) {
                        Spatial s = buildable.getSpatial();

                        // Current attributes of the spatial within the group (we put these values back shortly)
                        final Vector3f currentLocalTranslation = s.getLocalTranslation().clone();
                        final Quaternion currentLocalRotation = s.getLocalRotation().clone();
                        final Vector3f currentLocalScale = s.getLocalScale().clone();

                        // Get the the attributes of the spatial from the world perspective. These will
                        // be set back on the spatial when the group selection is disposed of
                        final Vector3f worldTranslation = s.getWorldTranslation().clone();
                        final Quaternion worldRotation = s.getWorldRotation().clone();
                        final Vector3f worldScale = s.getWorldScale().clone();
                        final NodeWrapper wrapper = pIt.next();

                        // Store the actual rotation, translate and scale. We then set
                        // this back on the spatials directly when they are detached from
                        // the selection group
                        wrapper.dragged(worldTranslation, worldRotation, worldScale);

                        // Inform the buildable about the new location, rotation and scale
                        buildable.rotateBuildableTo(worldRotation);
                        buildable.moveBuildableTo(worldTranslation);
                        buildable.scaleBuildableTo(worldScale.x, worldScale.y, worldScale.z);

                        // Put the spatial back to its current place in the group
                        s.setLocalTranslation(currentLocalTranslation);
                        s.setLocalRotation(currentLocalRotation);
                        s.setLocalScale(currentLocalScale);
                    }
                }
            });
        }

        // If this selection was the result of a "Drag Selection", put the new manipulator in
        // its dragging state 
        if (buildableSpatial != null && source.isSelectBecauseDragged()) {
            selectionManager.getMouseManager().startDrag(buildableSpatial, buildableSpatial.getControl(ObjectManipulatorControl.class));
        }

    }
}
