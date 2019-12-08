package net.pictulog.ml.rummikub.service;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Controller;

import net.pictulog.ml.rummikub.model.Table;
import net.pictulog.ml.rummikub.model.TileSet;

/**
 * The {@code TableController} is in charge of the {@link Table}.
 * 
 * @author rostskadat
 *
 */
@Controller
public class TableController {

    private Table table;

    @PostConstruct
    private void postConstruct() {
        table = new Table();
    }

    public Table getTable() {
        return table;
    }

    public void addTileSet(TileSet tileSet) {
        table.add(tileSet);
    }

    public void addAllTileSets(List<? extends TileSet> tileSets) {
        table.addAll(tileSets);
    }

    public void removeTileSet(TileSet tileSet) {
        table.remove(tileSet);
    }

    public void clearTable() {
        table.clear();
    }
}
