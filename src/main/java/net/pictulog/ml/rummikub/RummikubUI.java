package net.pictulog.ml.rummikub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Component
@Theme("custom")
@SpringUI(path = "/rummikub")
public class RummikubUI extends UI {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(RummikubUI.class);

    @Value("${rummikubDescription}")
    private String rummikubDescription;

    public RummikubUI() {

    }

    @Override
    protected void init(VaadinRequest request) {

        Label description = new Label(rummikubDescription, ContentMode.HTML);

        Label playerTop = new Label("playerTop");
        playerTop.setSizeFull();
        playerTop.setStyleName("player-top", true);

        Label playerLeft = new Label("playerLeft");
        playerLeft.setSizeFull();
        playerLeft.setStyleName("player-left", true);
        Label table = new Label("Table");
        table.setSizeFull();
        table.setStyleName("table", true);
        Label playerRight = new Label("playerRight");
        playerRight.setSizeFull();
        playerRight.setStyleName("player-right", true);

        Label playerBottom = new Label("You");
        playerBottom.setSizeFull();
        playerBottom.setStyleName("player-bottom", true);

        HorizontalLayout hl = new HorizontalLayout(playerLeft, table, playerRight);
        hl.setSizeFull();
        hl.setComponentAlignment(playerLeft, Alignment.MIDDLE_LEFT);
        hl.setComponentAlignment(table, Alignment.MIDDLE_CENTER);
        hl.setComponentAlignment(playerRight, Alignment.MIDDLE_RIGHT);

        VerticalLayout playground = new VerticalLayout(description, playerTop, hl, playerBottom);
        playground.setComponentAlignment(playerTop, Alignment.MIDDLE_CENTER);
        playground.setComponentAlignment(hl, Alignment.MIDDLE_CENTER);
        playground.setComponentAlignment(playerBottom, Alignment.MIDDLE_CENTER);
        playground.setSizeFull();

        VerticalLayout universe = new VerticalLayout(description, playground);

        setContent(universe);
    }
}
