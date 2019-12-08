package net.pictulog.ml.rummikub.ui.view;

import com.vaadin.navigator.View;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Composite;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class PlaygroundView extends Composite implements View {

    private static final long serialVersionUID = 1L;

    public PlaygroundView() {
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

        VerticalLayout playground = new VerticalLayout(table, playerBottom);
        playground.setComponentAlignment(table, Alignment.MIDDLE_CENTER);
        playground.setComponentAlignment(playerBottom, Alignment.MIDDLE_CENTER);
        playground.setSizeFull();
        setCompositionRoot(playground);
    }

}
