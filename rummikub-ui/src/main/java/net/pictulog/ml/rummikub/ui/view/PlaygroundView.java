package net.pictulog.ml.rummikub.ui.view;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.navigator.View;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Composite;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import net.pictulog.ml.rummikub.model.Tile;
import net.pictulog.ml.rummikub.service.PlayerController;
import net.pictulog.ml.rummikub.service.PoolController;
import net.pictulog.ml.rummikub.service.TableController;
import net.pictulog.ml.rummikub.ui.components.UiTile;

public class PlaygroundView extends Composite implements View {

    private static final long serialVersionUID = 1L;
    
    @Autowired
    TableController tableController;
    
    @Autowired
    PlayerController playerController;

    @Autowired
    PoolController poolController;
    
    public PlaygroundView() {

        Label poolSize = new Label(String.valueOf(poolController.getPoolSize()));
        List<UiTile> tiles = getTiles(poolController.getPool());
    	
    	
        GridLayout table = new GridLayout();
        table.setSizeFull();
        table.setStyleName("table", true);
        table.addComponents(tiles.toArray(new UiTile[] {}));

        Label playerBottom = new Label("You");
        playerBottom.setSizeFull();
        playerBottom.setStyleName("player-bottom", true);

        VerticalLayout playground = new VerticalLayout(table, playerBottom);
        playground.setComponentAlignment(table, Alignment.MIDDLE_CENTER);
        playground.setComponentAlignment(playerBottom, Alignment.MIDDLE_CENTER);
        playground.setSizeFull();
        setCompositionRoot(playground);
    }

    private List<UiTile> getTiles(List<Tile> tiles) {
    	List<UiTile> uiTiles = new ArrayList<>();
    	for (Tile tile : tiles) {
    		uiTiles.add(new UiTile(tile));
    	}
    	return uiTiles;
    }

}
