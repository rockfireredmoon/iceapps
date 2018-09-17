package org.icescene.propertyediting;

import java.beans.PropertyChangeListener;

public interface PropertyBean extends Cloneable {
    void addPropertyChangeListener(PropertyChangeListener listener);
    void removePropertyChangeListener(PropertyChangeListener listener);
}
