package net.pictulog.ml.rummikub.service.strategy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import net.pictulog.ml.rummikub.model.Player;
import net.pictulog.ml.rummikub.model.Pool;
import net.pictulog.ml.rummikub.model.Rack;
import net.pictulog.ml.rummikub.model.Tile;
import net.pictulog.ml.rummikub.service.PlayerController;
import net.pictulog.ml.rummikub.service.PoolController;
import net.pictulog.ml.rummikub.service.TableController;

/**
 * The {@code OracleStrategy} knows the {@link Rack} of all the players as well as the {@link Tile} found in the
 * {@link Pool}. It can therefore select the moves that maximize the score in the shortest number of rounds possible.
 * NOTE: that it does not control the randomness of the {@link Pool} drawing function.
 * 
 * @author rostskadat
 *
 */
public class OracleStrategy implements IStrategy {

    private static final Log LOG = LogFactory.getLog(OracleStrategy.class);

    @Autowired
    private TableController tableController;

    @Autowired
    private PlayerController playerController;

    @Autowired
    private PoolController poolController;

    @Autowired
    private StrategyHelper helper;

    @Override
    public boolean play(Player player) {
        boolean hasPlayed = false;
        return hasPlayed;
    }

}
