package net.pictulog.ml.rummikub.ui.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.vaadin.navigator.View;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.Composite;
import com.vaadin.ui.Label;

@Component
public class HelpView extends Composite implements View {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(HelpView.class);

    @Value("${rummikubDescription}")
    private String rummikubDescription;

    public HelpView() {
        LOG.info("Creating helpView with " + rummikubDescription);
        setCompositionRoot(new Label(rummikubDescription, ContentMode.HTML));
    }

}
