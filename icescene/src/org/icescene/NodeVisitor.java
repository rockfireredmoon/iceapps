package org.icescene;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

public class NodeVisitor {
    public interface Visit {
        void visit(Spatial node);
    }
    private final Node root;
    
    public NodeVisitor(Node root) {
        this.root = root;
    }
    
    public void visit(Visit visit) {
        visit(root, visit);
    }
    
    void visit(Spatial node, Visit visit) {
        visit.visit(node);
        if(node instanceof Node) {
            for(Spatial n : ((Node)node).getChildren()) {
                visit(n, visit);
            }
        }
    }
}
