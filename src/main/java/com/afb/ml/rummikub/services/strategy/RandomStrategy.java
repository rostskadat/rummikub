package com.afb.ml.rummikub.services.strategy;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.afb.ml.rummikub.model.Player;
import com.afb.ml.rummikub.model.Rack;
import com.afb.ml.rummikub.model.Table;
import com.afb.ml.rummikub.model.Tile;
import com.afb.ml.rummikub.model.TileGroup;
import com.afb.ml.rummikub.model.TileRun;
import com.afb.ml.rummikub.model.TileSet;
import com.afb.ml.rummikub.services.TableController;

/**
 * This strategy plays at "random"
 * 
 * @author rostskadat
 *
 */
@Service
public class RandomStrategy implements IStrategy {

    private static final Log LOG = LogFactory.getLog(RandomStrategy.class);

    @Autowired
    private TableController tableController;

    @Autowired
    private StrategyHelper helper;

    @Override
    public boolean play(Player player) {
        boolean hasPlayed = false;
        if (!player.isStarted()) {
            hasPlayed = playInitialRound(player);
        } else {
            hasPlayed = playNormalRound(player);
        }
        return hasPlayed;
    }

    protected boolean playInitialRound(Player player) {
        // I need to start with at least one TileRun or TileGroup worth 30 points
        List<TileSet> initialTileSets = helper.getInitialTileSets(player.getRack());
        if (!initialTileSets.isEmpty()) {
            // XXX: what is the best initial move? lot of small tiles or the big tiles first?
            TileSet initialTileSet = initialTileSets.get(0);
            player.removeAllTilesFromRack(initialTileSet);
            tableController.addTileSet(initialTileSet);
            LOG.debug(format("Player %s has played initial round: %s", player.getName(), initialTileSet));
            player.setStarted(true);
            return true;
        }
        LOG.debug(format("Player %s can not play initial round", player.getName()));
        return false;
    }

    protected boolean playNormalRound(Player player) {
        // A normal round is played as follow:
        // 1- Add all the TileSets I have in the rack.
        // 2- Check if I can add any of the remaining Tiles.
        // 2.1- If so add it, and repeat.
        // 2.2- If no tile can be added, exit
        boolean hasPlayed = false;

        Rack rack = player.getRack();
        List<TileSet> validTileSets = helper.getValidTileSets(rack);
        validTileSets.forEach(set -> {
            LOG.debug(format("Player %s added %s", player.getName(), set));
            player.removeAllTilesFromRack(set);
            tableController.addTileSet(set);
        });
        if (!validTileSets.isEmpty()) {
            hasPlayed = true;
        }

        if (playTiles(player)) {
            hasPlayed = true;
        }
        return hasPlayed;
    }

    private boolean playTiles(Player player) {

        boolean hasPlayed = false;
        boolean hasPlayedOneTile = false;

        do {
            // make a copy in order to avoid concurrent modification exceptions
            List<Tile> tilesToPlay = new ArrayList<>(player.getRack());
            hasPlayedOneTile = false;
            for (Tile tile : tilesToPlay) {
                if (playOneTile(tile)) {
                    player.removeTileFromRack(tile);
                    hasPlayedOneTile = true;
                    LOG.debug(format("Player %s played Tile %s", player.getName(), tile));
                }
            }
            hasPlayed |= hasPlayedOneTile;
        } while (hasPlayedOneTile);

        return hasPlayed;
    }

    private boolean playOneTile(Tile tile) {
        // XXX: what is the best move? Take the first slot available or the one with the best future prospect? For
        // instance runs are easier to play with than groups. If several options are valid how to select the best one?

        Table table = new Table();
        table.addAll(tableController.getTable());

        for (TileSet set : table) {
            if (set instanceof TileRun) {
                TileRun run = (TileRun) set;
                if (helper.canShiftRun(run, tile)) {
                    helper.shiftRun(run, tile);
                    // Not touching tableSets because these are the same underlying tileSet
                    return true;
                } else if (helper.canSplitRun(run, tile)) {
                    List<TileRun> newRuns = helper.splitRun(run, tile);
                    tableController.removeTileSet(run);
                    tableController.addAllTileSets(newRuns);
                    return true;
                }
            } else if (set instanceof TileGroup) {
                TileGroup group = (TileGroup) set;
                if (helper.canSubstituteInGroup(group, tile)) {
                    // NA
                    return true;
                }
            }
        }
        return false;
    }
}
