package net.pictulog.ml.rummikub.service.strategy;

import static java.lang.String.format;
import static net.pictulog.ml.rummikub.service.strategy.StrategyHelper.getInitialTileSets;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import net.pictulog.ml.rummikub.algo.Negamax;
import net.pictulog.ml.rummikub.model.Game;
import net.pictulog.ml.rummikub.model.Moves;
import net.pictulog.ml.rummikub.model.Player;
import net.pictulog.ml.rummikub.model.Pool;
import net.pictulog.ml.rummikub.model.Rack;
import net.pictulog.ml.rummikub.model.Tile;
import net.pictulog.ml.rummikub.model.TileSet;
import net.pictulog.ml.rummikub.service.PlayerController;
import net.pictulog.ml.rummikub.service.PoolController;
import net.pictulog.ml.rummikub.service.TableController;

/**
 * The {@code OracleStrategy} knows the {@link Rack} of all the players as well
 * as the {@link Tile} found in the {@link Pool}. It can therefore select the
 * moves that maximize the score in the shortest number of rounds possible.
 * NOTE: that it also control the "randomness" of the {@link Pool} drawing
 * function.<br/>
 * It works by evaluating all possible move from the {@link Rack} of the current
 * player and then simulating playing the next player in turn.
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
		List<TileSet> initialTileSets = getInitialTileSets(player.getRack(), initialScoreThreshold);
		if (!initialTileSets.isEmpty()) {
			// XXX: what is the best initial move? lot of small tiles or the big tiles
			// first?
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
		Game game = new Game();
		List<Moves> moves = game.getPossibleMoves(player);
		if (moves.size() == 1) {
			game.playMove(player, moves.get(0));
		} else {
			Negamax.Pair pair = Negamax.negamax(game, moves.get(0), 0, player);
			game.playMove(player, pair.getMove());
		}
		return true;
	}

}
