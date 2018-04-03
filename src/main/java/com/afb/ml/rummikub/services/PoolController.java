package com.afb.ml.rummikub.services;

import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

    @PostConstruct
    private void postConstruct() {
        pool = new Pool();
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

    public Tile drawTileFromPool() {
        return pool.remove(ThreadLocalRandom.current().nextInt(0, pool.size()));
    }

    public int getPoolSize() {
        return pool.size();
    }

    public Pool getPool() {
        return pool;
    }

}
