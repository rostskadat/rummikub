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
import com.afb.ml.rummikub.model.Tile;
import com.afb.ml.rummikub.model.TileGroup;
import com.afb.ml.rummikub.model.TileRun;
import com.afb.ml.rummikub.model.TileSet;
import com.afb.ml.rummikub.services.PoolController;
import com.afb.ml.rummikub.services.TableController;

/**
 * This strategy plays at random.
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
	private PoolController poolController;

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
		Rack rack = player.getRack();
		// A normal round is played as follow:
		// 1- Add all the set I have in the rack.
		// 2- Check if I can add any of the remaining tile.
		// 2.1- If so add it.
		// 2.2- If no tile can be added, exit
		List<TileSet> validTileSets = helper.getValidTileSets(rack);
		validTileSets.forEach(set -> {
			LOG.debug(format("Player %s added %s", player.getName(), set));
			player.removeAllTilesFromRack(set);
			tableController.addTileSet(set);
		});
		List<TileSet> tableSets = new ArrayList<>(tableController.getTileSets());
		List<TileSet> finalSets = new ArrayList<>();
		List<Tile> tileToRemoveFromRack = new ArrayList<>();

		rack.forEach(tile -> {
			tableSets.stream().filter(set -> set instanceof TileRun).forEach(set -> {
				if (helper.canShiftRun((TileRun) set, tile)) {
					TileRun tileRun = helper.shiftRun((TileRun) set, tile);
					tileToRemoveFromRack.add(tile);
					finalSets.add(tileRun);
				}
				if (helper.canSplitRun((TileRun) set, tile)) {
					List<TileRun> tileRuns = helper.splitRun((TileRun) set, tile);
					tileToRemoveFromRack.add(tile);
					finalSets.addAll(tileRuns);
				}
			});
			tableSets.stream().filter(set -> set instanceof TileGroup).forEach(set -> {
				if (helper.canSubstituteInGroup((TileGroup) set, tile)) {
					// NA
				}
			});
		});
		boolean hasPlayed = false;
		boolean hasAddedTiles = false;

		if (!tileToRemoveFromRack.isEmpty()) {
			player.removeAllTilesFromRack(tileToRemoveFromRack);
			hasAddedTiles = true;
			hasPlayed = true;
		}
		if (validTileSets.isEmpty() && !hasAddedTiles) {
			LOG.debug(format("Player %s has not played this round...", player.getName()));
			if (poolController.getPoolSize() > 0) {
				rack.add(poolController.drawTileFromPool());
			}
		} else {
			hasPlayed = true;
		}
		return hasPlayed;
	}
}
