package net.pictulog.ml.rummikub.service.strategy;

import static java.lang.String.format;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import net.pictulog.ml.rummikub.model.Player;
import net.pictulog.ml.rummikub.model.Pool;
import net.pictulog.ml.rummikub.model.Rack;
import net.pictulog.ml.rummikub.model.Tile;
import net.pictulog.ml.rummikub.model.TileSet;
import net.pictulog.ml.rummikub.service.PlayerController;
import net.pictulog.ml.rummikub.service.PoolController;
import net.pictulog.ml.rummikub.service.TableController;

/**
 * The {@code OracleStrategy} knows the {@link Rack} of all the players as well as the {@link Tile} found in the
 * {@link Pool}. It can therefore select the moves that maximize the score in the shortest number of rounds possible.
 * NOTE: that it also control the "randomness" of the {@link Pool} drawing function.<br/>
 * It works by evaluating all possible move from the {@link Rack} of the current player and then simulating playing the
 * next player in turn.
 * 
 * @author rostskadat
 *
 */
// @Component
public class OracleStrategy implements IStrategy {

    private static final Log LOG = LogFactory.getLog(OracleStrategy.class);

    @Value("${initialScoreThreshold:30}")
    private int initialScoreThreshold;

    @Autowired
    private TableController tableController;

    @Autowired
    private PlayerController playerController;

    @Autowired
    private PoolController poolController;

    private StrategyHelper helper = new StrategyHelper();

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
        List<TileSet> initialTileSets = helper.getInitialTileSets(player.getRack(), initialScoreThreshold);
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
        return hasPlayed;
    }

}
