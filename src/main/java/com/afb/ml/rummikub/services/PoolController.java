package com.afb.ml.rummikub.services;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import com.afb.ml.rummikub.model.Pool;
import com.afb.ml.rummikub.model.Tile;
import com.afb.ml.rummikub.model.TileColor;

@Controller
public class PoolController {

    private static final Log LOG = LogFactory.getLog(PoolController.class);

    @Value("${numberOfTilesPerColor:13}")
    private int numberOfTilesPerColor;

    private Pool pool;

    @Autowired
    private GameStateController gameStateController;

    @PostConstruct
    private void postConstruct() {
        resetPool();
    }

    public Tile drawTileFromPool() {
        // I delegate to the game state controller in order to make sure that the tile are drawn in the same order as
        // the previous game if I'm in replay mode
        return gameStateController.getNextTile(pool);
    }

    public int getPoolSize() {
        return pool.size();
    }

    public Pool getPool() {
        return pool;
    }

    public void resetPool() {
        pool = new Pool();
        LOG.debug("Creating tiles...");
        for (TileColor color : TileColor.values()) {
            for (int i = 1; i <= numberOfTilesPerColor; i++) {
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
