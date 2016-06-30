package org.icescene.tools;

import com.jme3.math.Vector2f;

public class DragContext {

    private Vector2f dragStart;
    private boolean dragging;
    private boolean dragCancel;

    public boolean isDragging() {
        return dragging;
    }

    public Vector2f getDragStart() {
        return dragStart;
    }

    public void setDragStart(Vector2f dragStart) {
        this.dragStart = dragStart;
    }
    
    public void cancel() {
        dragCancel = true;
    }

    public boolean isCancelled() {
        return dragCancel;
    }

    public void stop() {
        dragging = false;
        dragCancel = false;
    }

    public void start() {
        dragCancel = false;
        dragging = true;
    }
}
