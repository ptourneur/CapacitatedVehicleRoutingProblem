package com.polytech.ui;

import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class CustomerScope implements Scope {
    private BooleanProperty displayLabel = new SimpleBooleanProperty();

    public BooleanProperty displayLabel() {
        return displayLabel;
    }
}
