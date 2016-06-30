package org.icescene.propertyediting;

import java.beans.PropertyChangeListener;

public interface PropertyBean  {
    void addPropertyChangeListener(PropertyChangeListener listener);
    void removePropertyChangeListener(PropertyChangeListener listener);
    Object clone();
}
