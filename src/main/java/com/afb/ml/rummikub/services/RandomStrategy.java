package com.afb.ml.rummikub.services;

import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.afb.ml.rummikub.model.Player;
import com.afb.ml.rummikub.model.Rack;
import com.afb.ml.rummikub.model.Tile;
import com.afb.ml.rummikub.model.TileGroup;
import com.afb.ml.rummikub.model.TileRun;
import com.afb.ml.rummikub.model.TileSet;

/**
 * This strategy plays at random.
 * 
 * @author N090536
 *
 */
@Controller
public class RandomStrategy implements IStrategy {

    @Autowired
    private TableController tableController;

    @Autowired
    private PoolController poolController;

    @Override
    public boolean play(Player player) {
        if (!player.isStarted()) {
            return playInitialRound(player);
        }

        Rack rack = player.getRack();
        // take a random tile...
        Tile tile = rack.remove(ThreadLocalRandom.current().nextInt(0, rack.size()));
        for (TileSet tileSet : tableController.getTileSets()) {
            boolean hasPlayed = false;
            if (tileSet instanceof TileRun) {
                hasPlayed = playTileRun((TileRun) tileSet, tile);
            } else if (tileSet instanceof TileGroup) {
                hasPlayed = playTileGroup((TileGroup) tileSet, tile);
            } else {
                throw new IllegalStateException("TileSet is neither a TileRun nor a TileGroup");
            }
            if (hasPlayed) {
                return hasPlayed;
            }
        }
        rack.add(tile);
        if (poolController.getPoolSize() > 0) {
            rack.add(poolController.drawTileFromPool());
        }
        return false;
    }

    private boolean playInitialRound(Player player) {
        // I need to start with at least one TileRun or TileGroup worth 30 points
        TileSet initialTileSet = StrategyUtils.getHighestTileSet(player.getRack());
        if (initialTileSet != null) {
            player.removeTileSetFromRack(initialTileSet);
            tableController.addTileSet(initialTileSet);
            player.setStarted(true);
            return true;
        }
        return false;
    }

    private boolean playTileRun(TileRun tileRun, Tile tile) {
        if (StrategyUtils.canShiftRun(tileRun, tile)) {
            StrategyUtils.shiftRun(tileRun, tile);
            return true;
        } else if (StrategyUtils.canSplitRun(tileRun, tile)) {
            StrategyUtils.splitRun(tileRun, tile);
            return true;
        }
        return false;
    }

    private boolean playTileGroup(TileGroup tileGroup, Tile tile) {
        if (StrategyUtils.canSubstituteInGroup(tileGroup, tile)) {
            StrategyUtils.substituteInGroup(tileGroup, tile);
            return true;
        }
        return false;
    }
}
