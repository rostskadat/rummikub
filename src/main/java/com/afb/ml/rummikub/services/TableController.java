package com.afb.ml.rummikub.services;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Controller;

import com.afb.ml.rummikub.model.Table;
import com.afb.ml.rummikub.model.TileSet;

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
