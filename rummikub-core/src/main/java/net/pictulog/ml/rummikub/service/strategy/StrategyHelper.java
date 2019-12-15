package net.pictulog.ml.rummikub.service.strategy;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.pictulog.ml.rummikub.model.Move;
import net.pictulog.ml.rummikub.model.Moves;
import net.pictulog.ml.rummikub.model.Rack;
import net.pictulog.ml.rummikub.model.Table;
import net.pictulog.ml.rummikub.model.Tile;
import net.pictulog.ml.rummikub.model.TileColor;
import net.pictulog.ml.rummikub.model.TileGroup;
import net.pictulog.ml.rummikub.model.TileRun;
import net.pictulog.ml.rummikub.model.TileSet;

/**
 * This utility class provides methods that are used by all {@link IStrategy}
 * implementation. It can tells you whether a specific {@link Tile} can be
 * shifted or a {@link TileGroup} created. Its goal is to expose all the
 * underlying details to a specific {@link IStrategy} implementation.
 * 
 * @author rostskadat
 *
 */
public class StrategyHelper {

	private StrategyHelper() {
		// NA
	}

	/**
	 * This method returns the sets that are valid for an Initial move (whose score
	 * is more than {@code initialScoreThreshold} (30)).
	 * 
	 * @param rack the {@link Rack} from which to draw the {@link Tile}
	 * @return The {@link List} (possibly empty) of valid initial {@link TileSet}
	 */
	public static List<TileSet> getInitialTileSets(Rack rack, int initialScoreThreshold) {
		final List<TileSet> initialTileSets = new ArrayList<>();
		// XXX: What about the group of sets whose combined score reaches the threshold?
		getValidTileSets(rack).forEach(set -> {
			if (set.getScore() >= initialScoreThreshold) {
				// Duplicate should have been eliminated in getValidTileSet
				assert (!initialTileSets.contains(set));
				initialTileSets.add(set);
			}
		});
		return initialTileSets;
	}

	/**
	 * This method returns the highest scored {@link TileSet} in the {@link Rack}.
	 * 
	 * @param rack the Rack to extract the {@link TileSet} from
	 * @return The highest scored {@link TileSet}
	 */
	public static TileSet getHighestTileSet(Rack rack) {
		List<TileSet> validTileSets = getValidTileSets(rack);
		int highestScore = 0;
		TileSet highestTileSet = null;
		for (TileSet tileSet : validTileSets) {
			int score = tileSet.getScore();
			if (score > highestScore) {
				highestTileSet = tileSet;
				highestScore = score;
			}
		}
		return highestTileSet;
	}

	/**
	 * This method returns all the valid {@link TilSet} found in the {@link Rack}. A
	 * {@link TileSet} is considered valid if it has at least 3 {@link Tile}. <br/>
	 * <b>NOTE:</b> a specific {@link Tile} from the {@link Rack} can be found in
	 * different {@link TilSet}.
	 * 
	 * @param rack The {@link Rack} containing the player's {@link Tile}s.
	 * @return a {@link List} of valid {@link TilSet}
	 */
	public static List<TileSet> getValidTileSets(Rack rack) {
		int size = rack.size();
		Map<Tile, TileRun> runs = new HashMap<>(size);
		Map<Tile, TileGroup> groups = new HashMap<>(size);

		// Finding the Highest scoring TileSet consist in creating all of the possible
		// TileSet and then find out which
		// one has the highest score. First let's create a stub TileSet for each tile.
		for (Tile tile : rack) {
			runs.put(tile, new TileRun(Arrays.asList(tile)));
			groups.put(tile, new TileGroup(Arrays.asList(tile)));
		}

		// and then loop through all the Tiles in order to add them, if possible to each
		// TileSet. Not sure if it's the
		// most efficient way to do it.
		for (Tile tile : rack) {
			TileSet tileSet = runs.get(tile);
			int tileSetSize = 0;
			do {
				tileSetSize = tileSet.size();
				addTileFromRackToTileSet(rack, 0, tileSet);
			} while (tileSetSize != tileSet.size());
			tileSet = groups.get(tile);
			do {
				tileSetSize = tileSet.size();
				addTileFromRackToTileSet(rack, 0, tileSet);
			} while (tileSetSize != tileSet.size());
		}

		// Finally I only add the valid TileSet
		final List<TileSet> validTileSets = new ArrayList<>();
		runs.values().forEach(set -> {
			if (set.isValid() && !validTileSets.contains(set)) {
				validTileSets.add(set);
			}
		});
		groups.values().forEach(set -> {
			if (set.isValid() && !validTileSets.contains(set)) {
				validTileSets.add(set);
			}
		});
		// XXX: What about 1,2,3,4 BLACK? Should it be 1 or 3 TileRuns (1,2,3 BLACK /
		// 1,2,3,4 BLACK / 2,3,4 BLACK)?
		return validTileSets;
	}

	private static void addTileFromRackToTileSet(Rack rack, int index, TileSet tileSet) {
		assert (index < rack.size());

		Tile tile = rack.get(index);
		if (canAddToSet(tileSet, tile)) {
			addToSet(tileSet, tile);
		}
		if (index == rack.size() - 1) {
			return;
		}
		// And keep on going with the rest of the rack
		addTileFromRackToTileSet(rack, index + 1, tileSet);
	}

	/**
	 * This method returns whether the {@link Tile} {@code tileToAdd} can be added
	 * to the {@code TileSet}
	 * 
	 * @param tileToAdd the {@link Tile} to add
	 * @return whether the {@link Tile} {@code tileToAdd} can be added
	 */
	public static boolean canAddToSet(TileSet tileSet, Tile tileToAdd) {
		return (tileSet instanceof TileGroup) ? canAddToTileGroup(tileSet, tileToAdd)
				: canAddToTileRun(tileSet, tileToAdd);
	}

	private static boolean canAddToTileGroup(TileSet tileSet, Tile tileToAdd) {
		if (!(tileSet instanceof TileGroup)) {
			return false;
		}
		TileGroup group = (TileGroup) tileSet;
		if (group.size() >= 4) {
			return false;
		}
		if (group.isEmpty()) {
			return true;
		}
		if (tileToAdd.isJoker()) {
			return !group.containsJocker(tileToAdd);
		}
		TileColor colorToAdd = tileToAdd.getColor();
		boolean seenTileColorToAdd = false;
		int groupNumber = -1;
		for (Tile tile : group) {
			if (!tile.isJoker()) {
				groupNumber = tile.getNumber();
			}
			if (colorToAdd.equals(tile.getColor())) {
				seenTileColorToAdd = true;
			}
		}
		return (tileToAdd.getNumber() == groupNumber && !seenTileColorToAdd);
	}

	private static boolean canAddToTileRun(TileSet tileSet, Tile tileToAdd) {
		if (!(tileSet instanceof TileRun)) {
			return false;
		}
		TileRun run = (TileRun) tileSet;
		if (run.isEmpty()) {
			return true;
		}
		// I can add any of the jocker once without constraints
		if (tileToAdd.isJoker()) {
			return !run.containsJocker(tileToAdd);
		}
		TileColor tileSetColor = run.getColor();
		Integer lowestBound = run.getLowerBound();
		Integer upperBound = run.getUpperBound();

		TileColor tileColor = tileToAdd.getColor();
		int tileNumber = tileToAdd.getNumber();

		// XXX: what happend in case of a jocker tiles?
		return ((tileSetColor == null || tileSetColor == tileColor)
				&& (lowestBound == null || (lowestBound == tileNumber + 1) || (upperBound == tileNumber - 1)));
	}

	/**
	 * This method insert a tile to the correct spot in the set and returns the
	 * index of the position it was inserted at
	 * 
	 * @param tileToAdd the {@link Tile} to add
	 * @return the index of the position it was inserted at
	 */
	public static int addToSet(TileSet tileSet, Tile tileToAdd) {
		return (tileSet instanceof TileGroup) ? addToTileGroup(tileSet, tileToAdd) : addToTileRun(tileSet, tileToAdd);
	}

	private static int addToTileGroup(TileSet tileSet, Tile tileToAdd) {
		if (!canAddToSet(tileSet, tileToAdd)) {
			throw new IllegalArgumentException("Tile can't be added to set");
		}
		TileGroup group = (TileGroup) tileSet;
		group.add(tileToAdd);
		return 0;
	}

	private static int addToTileRun(TileSet tileSet, Tile tileToAdd) {
		if (!canAddToSet(tileSet, tileToAdd)) {
			throw new IllegalArgumentException("Tile can't be added to set");
		}
		TileRun run = (TileRun) tileSet;
		if (run.isEmpty() || tileToAdd.isJoker()) {
			// XXX: Does it depend on the strategy when dealing with a Joker?
			run.add(0, tileToAdd);
			return 0;
		}
		Integer lowestBound = run.getLowerBound();
		Integer tileNumber = tileToAdd.getNumber();
		if (lowestBound == null || lowestBound == tileNumber + 1) {
			run.add(0, tileToAdd);
			return 0;
		}
		run.add(tileToAdd);
		return run.size() - 1;
	}

	/**
	 * This method returns whether a specific {@link Tile} can be added to the given
	 * {@link TileRun} by shifting the run. Shifting a run means adding a
	 * {@link Tile} either at the beginning or the end of the {@link TileRun}.
	 * 
	 * @param tileRun
	 * @param tile
	 * @return
	 */
	public static List<Integer> getShiftRunIndexes(TileRun set, Tile tile) {
		if (tile.isJoker()) {
			// XXX: Is that correct when the TileRun already contains a Jocker. Check
			// definition of Tile.equals
			return Arrays.asList(0, set.size());
		}
		if (set.contains(tile)) {
			return Collections.emptyList();
		}

		TileColor tileSetColor = set.getColor();
		if (tileSetColor == tile.getColor()) {
			if (set.getLowerBound() == (tile.getNumber() + 1)) {
				return Arrays.asList(0);
			} else if (set.getUpperBound() == (tile.getNumber() - 1)) {
				return Arrays.asList(set.size());
			}
		}
		return Collections.emptyList();
	}

	public static TileRun shiftRun(TileRun set, Tile tile, Integer index) {
		if (getShiftRunIndexes(set, tile).contains(index)) {
			set.add(index, tile);
			return set;
		}
		throw new IllegalArgumentException(format("Can't shift %s with %s @ %d", set, tile, index));
	}

	/**
	 * This method returns whether a specific {@link Tile} can be added to the given
	 * {@link TileRun}
	 * 
	 * @param tileRun
	 * @param tile
	 * @return
	 */
	public static List<Integer> getSplitRunIndexes(TileRun set, Tile tile) {
		int setSize = set.size();
		if (tile.isJoker() && setSize >= 5) {
			List<Integer> indexes = new ArrayList<>();
			for (int i = 2; i < setSize - 1; i++) {
				indexes.add(i);
			}
			return indexes;
		}
		TileColor tileSetColor = set.getColor();
		if (tileSetColor == tile.getColor() && setSize >= 5) {
			Integer lowerBound = set.getLowerBound();
			Integer upperBound = set.getUpperBound();
			if (lowerBound + 2 <= tile.getNumber() && tile.getNumber() + 2 <= upperBound) {
				return Arrays.asList(tile.getNumber() - lowerBound);
			}
		}
		return Collections.emptyList();
	}

	public static List<TileRun> splitRun(TileRun set, Tile tile, Integer index) {
		if (getSplitRunIndexes(set, tile).contains(index)) {
			TileRun lowerTileRun = new TileRun(set.subList(0, index));
			TileRun upperTileRun = new TileRun(set.subList(index, set.size()));
			lowerTileRun.add(tile);
			assert (set.size() + 1 == lowerTileRun.size() + upperTileRun.size());
			return Arrays.asList(lowerTileRun, upperTileRun);
		}
		throw new IllegalArgumentException(format("Can't split %s with %s @ %d", set, tile, index));
	}

	/**
	 * 
	 * @param set
	 * @param tile
	 * @return
	 */
	public static boolean canSubstituteInGroup(TileGroup set, Tile tile) {
		if (set.size() >= 4) {
			return false;
		}
		if (tile.isJoker()) {
			return !set.containsJocker(tile);
		}
		for (Tile tileInGroup : set) {
			if (tileInGroup.getNumber() != tile.getNumber()) {
				return false;
			}
			if (tileInGroup.getColor() == tile.getColor()) {
				return false;
			}
		}
		return true;
	}

	public static TileGroup substituteInGroup(TileGroup set, Tile tile) {
		if (canSubstituteInGroup(set, tile)) {
			set.add(tile);
		} else {
			throw new IllegalArgumentException(format("Tile %s can't substitute in TileGroup %s", tile, set));
		}
		return set;
	}

	/**
	 * This method returns a {@link Move} resulting from the given {@link List} of
	 * {@link Tile}
	 * 
	 * @param tiles the {@link List} of {@link Tile} to create the {@link TileRun}
	 *              from
	 * @return the newly created {@link Move}
	 */
	public static Move getTileRunMove(List<Tile> tiles) {
		assert (tiles != null && tiles.size() >= 3);
		Move move = new Move();
		move.getTiles().addAll(tiles);
		move.getToTileSets().add(new TileRun(tiles));
		return move;
	}

	/**
	 * This method returns a {@link Move} resulting from adding the {@link Tile} to
	 * the given {@link TileRun}.
	 * 
	 * @param tileRun the {@link TileRun} to add the {@link Tile} to
	 * @param tile    the {@link Tile} to add
	 * @return the newly created {@link Move}
	 */
	public static Move getTileRunMove(TileRun tileRun, Tile tile) {
		List<Tile> tiles = Arrays.asList(tile);
		Move move = new Move();
		move.setFromTileSet(new TileRun(tileRun));
		move.getTiles().addAll(tiles);
		List<Integer> shifts = getShiftRunIndexes(tileRun, tile);
		List<Integer> splits = getSplitRunIndexes(tileRun, tile);
		if (!shifts.isEmpty()) {
			assert (splits.isEmpty() && shifts.size() == 1);
			move.getToTileSets().add(shiftRun(tileRun, tile, shifts.get(0)));
		} else if (!splits.isEmpty()) {
			assert (splits.size() == 1);
			move.getToTileSets().addAll(splitRun(tileRun, tile, splits.get(0)));
		}
		return move;
	}

	/**
	 * This method returns a {@link List} of {@link Move} resulting from adding the
	 * given {@code tile} {@link Tile} to the given {@code tileGroup}
	 * {@link TileRun} .
	 * 
	 * @param tileRun
	 * @param tile
	 * @return
	 */
	public static Moves getTileGroupMove(TileGroup tileGroup, Tile tile) {
		Moves moves = new Moves();
		if (canSubstituteInGroup(tileGroup, tile)) {
			List<Tile> tiles = Arrays.asList(tile);
			Move move = new Move();
			move.setFromTileSet(new TileGroup(tileGroup));
			move.getTiles().addAll(tiles);
			move.getToTileSets().add(substituteInGroup(tileGroup, tile));
			moves.add(move);
		}
		return moves;
	}

	/**
	 * This method returns all the valid moves for the given {@link Table} and
	 * {@link Rack}
	 * 
	 * @param table The {@link Table} containing the {@link List} of {@link TileSet}
	 * @param rack  The {@link Rack} containing the {@link List} of {@link Tile} for
	 *              a specific player
	 * @return a {@link List} of {@link Moves}
	 */
	public static List<Moves> getValidMoves(Table table, Rack rack) {
		assert (table != null);
		assert (rack != null);

		if (rack.isEmpty()) {
			return Collections.emptyList();
		}

		List<Moves> validMovesList = new ArrayList<>();
		// I first add all the moves that can be made directly from what is seen in the
		// rack
		validMovesList.addAll(getRunAndGroupMoves(rack));

		// Then for all the move found I check whether I can some more move using the
		// TileSet from the Table.
		for (Tile tile : rack) {
			Moves moves = new Moves();
			for (TileSet tileSet : table) {
				if (canAddToTileRun(tileSet, tile)) {
					getTileRunMove((TileRun) tileSet, tile);
				} else if (canAddToTileGroup(tileSet, tile)) {
					getTileRunMove((TileRun) tileSet, tile);
				}
			}
			if (!moves.isEmpty()) {
				validMovesList.add(moves);
			}
		}
		return validMovesList;
	}

	/**
	 * This method returns a {@link List} of {@link Moves} containing all the
	 * possible {@link TileSet} available in the given {@link Rack}.<br/>
	 * We first take all the valid {@link TileSet} found in the {@link Rack}. Each
	 * one will be the "seed" of a valid {@link Moves}. We then loop through the
	 * different {@link Tile} found in the player's {@link Rack} and see if any
	 * further {@link Moves} are available, with that new {@link Tile} and
	 * {@link TileSet}. <b>Note</b> that it does not actually remove the
	 * {@link Tile}s from the {@link Rack}. This is left to the strategy (the object
	 * that will actually pick the highest scored {@link Move})
	 * 
	 * @param rack The {@link Rack} containing the {@link List} of {@link Tile}
	 * @return a {@link List} of {@link Moves}
	 */
	public static List<Moves> getRunAndGroupMoves(Rack rack) {
		List<TileSet> validTileSets = getValidTileSets(rack);
		List<Moves> movesList = new ArrayList<>(validTileSets.size());
		for (TileSet validTileSet : validTileSets) {
			// There is a set of Move for each of the valid TileSet.
			Move validInitialMove = new Move();
			validInitialMove.getTiles().addAll(validTileSet);
			validInitialMove.getToTileSets().add(validTileSet);
			Moves moves = new Moves();
			moves.add(validInitialMove);
			for (Tile tile : rack) {
				if (canAddToTileRun(validTileSet, tile)) {
					moves.add(getTileRunMove((TileRun) validTileSet, tile));
				} else if (canAddToTileGroup(validTileSet, tile)) {
					moves.addAll(getTileGroupMove((TileGroup) validTileSet, tile));
				}
			}
			movesList.add(moves);
		}
		return movesList;
	}

}
