package com.afb.ml.rummikub.services;

import java.util.ArrayList;
import java.util.List;

import com.afb.ml.rummikub.model.Rack;
import com.afb.ml.rummikub.model.Tile;
import com.afb.ml.rummikub.model.TileGroup;
import com.afb.ml.rummikub.model.TileRun;
import com.afb.ml.rummikub.model.TileSet;

public class StrategyUtils {

	private static final int INITIAL_MIN_SCORE = 30;

	private StrategyUtils() {
		// NA
	}

	public static TileSet getHighestTileSet(Rack rack) {
		int size = rack.size();
		List<TileRun> runs = new ArrayList<>(size);
		List<TileGroup> groups = new ArrayList<>(size);
		int index = 0;
		// Finding the Highest scoring TileSet consist in creating all of them
		// and then find out which one has the
		// highest score
		for (Tile tile : rack) {
			runs.add(new TileRun());
			runs.get(index).add(tile);
			groups.add(new TileGroup());
			groups.get(index).add(tile);
			index++;
		}

		// Then add them to each tileSet if possible
		// XXX: This part really depends on the strategy...
		rack.forEach(tile -> {
			runs.forEach(run -> {
				if (run.canAdd(tile)) {
					run.add(tile);
				}
			});
			groups.forEach(group -> {
				if (group.canAdd(tile)) {
					group.add(tile);
				}
			});
		});

		// Finally I score each one of them and take the highest
		int highestScore = 0;
		List<TileSet> validTileSets = new ArrayList<>();

		for (TileSet set : runs) {
			int score = set.getScore();
			// score >= INITIAL_MIN_SCORE
			if (score > highestScore && set.isValid()) {
				validTileSets.add(set);
			}
		}
		for (TileSet set : groups) {
			int score = set.getScore();
			// score >= INITIAL_MIN_SCORE &&
			if (score > highestScore && set.isValid()) {
				validTileSets.add(set);
			}
		}
		// TODO: what about when I have INITIAL_MIN_SCORE in 2 or more tileSet
		// instead of just one?
		TileSet highestTileSet = validTileSets.get(0);
		return highestTileSet;
	}

	/**
	 * This method returns whether a specific {@link Tile} can be added to the
	 * given {@link TileRun}
	 * 
	 * @param tileRun
	 * @param tile
	 * @return
	 */
	public static boolean canShiftRun(TileRun tileRun, Tile tile) {
		return getShiftRunIndex(tileRun, tile) != -1;
	}

	public static void shiftRun(TileRun tileRun, Tile tile) {
		int index = getShiftRunIndex(tileRun, tile);
		if (index != -1) {
			tileRun.add(index, tile);
		}
	}

	private static int getShiftRunIndex(TileRun tileRun, Tile tile) {
		if (tile.isJoker()) {
			return 0;
		}
		if (tileRun.get(0).getNumber() == (tile.getNumber() + 1)) {
			return 0;
		} else if (tileRun.get(tileRun.size() - 1).getNumber() == (tile.getNumber() - 1)) {
			return tileRun.size();
		}
		return -1;
	}

	/**
	 * This method returns whether a specific {@link Tile} can be added to the
	 * given {@link TileRun}
	 * 
	 * @param tileRun
	 * @param tile
	 * @return
	 */
	public static boolean canSplitRun(TileRun tileRun, Tile tile) {
		return getSplitRunIndex(tileRun, tile) != -1;
	}

	public static void splitRun(TileRun tileRun, Tile tile) {
		int index = getSplitRunIndex(tileRun, tile);
		if (index != -1) {
			tileRun.add(index, tile);
		}
	}

	private static int getSplitRunIndex(TileRun tileRun, Tile tile) {
		if (tile.isJoker() && tileRun.size() >= 5) {
			return 2;
		}
		if (tileRun.size() >= 5) {
			if (tileRun.get(2).getNumber() == (tile.getNumber() + 1)) {
				return 2;
			} else if (tileRun.get(tileRun.size() - 3).getNumber() == (tile.getNumber() - 1)) {
				return tileRun.size() - 2;
			}
		}
		return -1;
	}

	/**
	 * 
	 * @param tileGroup
	 * @param tile
	 * @return
	 */
	public static boolean canSubstituteInGroup(TileGroup tileGroup, Tile tile) {
		if (tile.isJoker()) {
			return true;
		}
		for (Tile tileInGroup : tileGroup) {
			if (tileInGroup.getNumber() != tile.getNumber()) {
				return false;
			}
			if (tileInGroup.getColor() == tile.getColor()) {
				return false;
			}
		}
		return true;
	}

	public static void substituteInGroup(TileGroup tileGroup, Tile tile) {
		if (canSubstituteInGroup(tileGroup, tile)) {
			tileGroup.add(tile);
		}
	}

}
