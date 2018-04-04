package com.afb.ml.rummikub.services;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import com.afb.ml.rummikub.common.GameStateUtils;
import com.afb.ml.rummikub.model.GameState;
import com.afb.ml.rummikub.model.Pool;
import com.afb.ml.rummikub.model.Tile;
import com.afb.ml.rummikub.model.TileColor;

@Controller
public class PoolController {

    private static final Log LOG = LogFactory.getLog(PoolController.class);

    @Value("${numberOfTilesPerColor:13}")
    private int numberOfTilesPerColor;

    @Value("${useGameStateFilename}")
    private boolean useGameStateFilename;

    @Value("${gameStateFilename}")
    private File gameStateFilename;

    private Pool pool;

    @PostConstruct
    private void postConstruct() {
        pool = new Pool();
        if (useGameStateFilename && GameStateUtils.isValidGameSeed(gameStateFilename)) {
            LOG.debug(String.format("Reading Pool content from %s...", gameStateFilename));
            readGameState();
        } else {
            LOG.debug("Creating tiles...");
            for (int i = 1; i < numberOfTilesPerColor; i++) {
                for (TileColor color : TileColor.values()) {
                    // I add 2 tiles of each number / color
                    pool.add(new Tile(i, color));
                    pool.add(new Tile(i, color));
                }
            }
            LOG.debug("Adding Jockers...");
            pool.add(new Tile(TileColor.RED));
            pool.add(new Tile(TileColor.BLACK));
        }
    }

    public Tile drawTileFromPool() {
        return pool.remove(ThreadLocalRandom.current().nextInt(0, pool.size()));
    }

    public int getPoolSize() {
        return pool.size();
    }

    public Pool getPool() {
        return pool;
    }

    private void readGameState() {
        try {
            GameState state = GameStateUtils.readGameState(gameStateFilename);
            pool.addAll(state.getPool());
        } catch (IOException e) {
            throw new BeanCreationException(e.getMessage(), e);
        }
    }
}
