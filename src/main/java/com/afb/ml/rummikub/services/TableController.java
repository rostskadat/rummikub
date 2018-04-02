package com.afb.ml.rummikub.services;

import java.util.List;

import org.springframework.stereotype.Controller;

import com.afb.ml.rummikub.model.Table;
import com.afb.ml.rummikub.model.Tile;
import com.afb.ml.rummikub.model.TileSet;

@Controller
public class TableController {

    private Table table = new Table();

    public List<TileSet> getTileSets() {
        return table;
    }

    public void addTileSet(TileSet tileSet) {
        table.add(tileSet);
    }

    public void play(TileSet tileSet, Tile tile, int index) {
        tileSet.add(index, tile);
    }

}
