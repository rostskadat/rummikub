package com.afb.ml.rummikub.services;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import com.afb.ml.rummikub.common.GameStateUtils;
import com.afb.ml.rummikub.model.GameState;
import com.afb.ml.rummikub.model.Table;
import com.afb.ml.rummikub.model.TileSet;

@Controller
public class TableController {

    private static final Log LOG = LogFactory.getLog(TableController.class);

    private Table table = new Table();

    @Value("${useGameStateFilename}")
    private boolean useGameStateFilename;

    @Value("${gameStateFilename}")
    private File gameStateFilename;

    @PostConstruct
    private void postConstruct() {
        if (useGameStateFilename && GameStateUtils.isValidGameSeed(gameStateFilename)) {
            LOG.debug(String.format("Reading Table state from %s...", gameStateFilename));
            readGameState();
        }
        LOG.info("TableController initialization... OK");
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

    private void readGameState() {
        try {
            GameState state = GameStateUtils.readGameState(gameStateFilename);
            table.addAll(state.getTable());
        } catch (IOException e) {
            throw new BeanCreationException(e.getMessage(), e);
        }
    }
}
