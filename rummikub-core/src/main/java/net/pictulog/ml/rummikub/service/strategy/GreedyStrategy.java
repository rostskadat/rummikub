package net.pictulog.ml.rummikub.service.strategy;

import static java.lang.String.format;
import static net.pictulog.ml.rummikub.service.strategy.StrategyHelper.canSubstituteInGroup;
import static net.pictulog.ml.rummikub.service.strategy.StrategyHelper.getInitialTileSets;
import static net.pictulog.ml.rummikub.service.strategy.StrategyHelper.getShiftRunIndexes;
import static net.pictulog.ml.rummikub.service.strategy.StrategyHelper.getSplitRunIndexes;
import static net.pictulog.ml.rummikub.service.strategy.StrategyHelper.getValidTileSets;
import static net.pictulog.ml.rummikub.service.strategy.StrategyHelper.shiftRun;
import static net.pictulog.ml.rummikub.service.strategy.StrategyHelper.splitRun;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import net.pictulog.ml.rummikub.model.Player;
import net.pictulog.ml.rummikub.model.Rack;
import net.pictulog.ml.rummikub.model.Table;
import net.pictulog.ml.rummikub.model.Tile;
import net.pictulog.ml.rummikub.model.TileGroup;
import net.pictulog.ml.rummikub.model.TileRun;
import net.pictulog.ml.rummikub.model.TileSet;
import net.pictulog.ml.rummikub.service.TableController;

/**
 * This strategy plays by maximizing the number of tile played at each turn.
 * 
 * @author rostskadat
 *
 */
@Component
public class GreedyStrategy implements IStrategy {

	private static final Log LOG = LogFactory.getLog(GreedyStrategy.class);

	@Value("${initialScoreThreshold:30}")
	private int initialScoreThreshold;

	@Autowired
	private TableController tableController;

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
		// I need to start with at least one TileRun or TileGroup worth
		// initialScoreThreshold points
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
		// A normal round is played as follow:
		// 1- Add all the TileSets I have in the rack.
		// 2- Check if I can add any of the remaining Tiles.
		// 2.1- If so add it, and repeat.
		// 2.2- If no tile can be added, exit
		boolean hasPlayed = false;

		Rack rack = player.getRack();
		List<TileSet> validTileSets = getValidTileSets(rack);
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
		// XXX: what is the best move? Take the first slot available or the one with the
		// best future prospect? For instance runs are easier to play with than groups.
		// If several options are valid how to select the best one?

		Table table = new Table();
		table.addAll(tableController.getTable());

		for (TileSet set : table) {
			if (set instanceof TileRun) {
				TileRun run = (TileRun) set;
				List<Integer> shifts = getShiftRunIndexes(run, tile);
				List<Integer> splits = getSplitRunIndexes(run, tile);
				if (!shifts.isEmpty()) {
					shiftRun(run, tile, shifts.get(0));
					return true;
				} else if (!splits.isEmpty()) {
					List<TileRun> newRuns = splitRun(run, tile, splits.get(0));
					tableController.removeTileSet(run);
					tableController.addAllTileSets(newRuns);
					return true;
				}
			} else if (set instanceof TileGroup) {
				TileGroup group = (TileGroup) set;
				if (canSubstituteInGroup(group, tile)) {
					// NA
					LOG.warn(format("STRATEGY NOT IMPLEMENTED: substitute %s is not implemented for group %s", tile, group));
					return true;
				}
			}
		}
		return false;
	}
}
