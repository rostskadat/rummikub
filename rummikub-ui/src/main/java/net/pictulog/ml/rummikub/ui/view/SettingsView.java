package net.pictulog.ml.rummikub.ui.view;

import com.vaadin.navigator.View;
import com.vaadin.ui.Composite;
import com.vaadin.ui.Label;

public class SettingsView extends Composite implements View {

    private static final long serialVersionUID = 1L;

    public SettingsView() {
        setCompositionRoot(new Label("SettingsView"));
    }

}
