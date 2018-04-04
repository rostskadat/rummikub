package com.afb.ml.rummikub.services.strategy;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.afb.ml.rummikub.model.Rack;
import com.afb.ml.rummikub.model.Tile;
import com.afb.ml.rummikub.model.TileColor;
import com.afb.ml.rummikub.model.TileGroup;
import com.afb.ml.rummikub.model.TileRun;
import com.afb.ml.rummikub.model.TileSet;

@Service
public class StrategyHelper {

    @Value("${initialScoreThreshold:30}")
    private int initialScoreThreshold;

    /**
     * This method returns the sets that are valid for an Initial move (whose score is more than
     * {@code initialScoreThreshold} (30)).
     * 
     * @param rack
     *            the {@link Rack} from which to draw the {@link Tile}
     * @return The {@link List} (possibly empty) of valid initial {@link TileSet}
     */
    public List<TileSet> getInitialTileSets(Rack rack) {
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
     * @param rack
     *            the Rack to extract the {@link TileSet} from
     * @return The highest scored {@link TileSet}
     */
    public TileSet getHighestTileSet(Rack rack) {
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
     * This method returns all the valid {@link TilSet} found in the {@link Rack}. A {@link TileSet} is considered valid
     * if it has at least 3 {@link Tile}.
     * 
     * @param rack
     *            The {@link Rack} containing the {@link Tile}
     * @return a {@link List} of valid {@link TilSet}
     */
    public List<TileSet> getValidTileSets(Rack rack) {
        int size = rack.size();
        Map<Tile, TileRun> runs = new HashMap<>(size);
        Map<Tile, TileGroup> groups = new HashMap<>(size);

        // Finding the Highest scoring TileSet consist in creating all of the possible TileSet and then find out which
        // one has the highest score. First let's create a stub TileSet for each tile.
        for (Tile tile : rack) {
            runs.put(tile, new TileRun(Arrays.asList(tile)));
            groups.put(tile, new TileGroup(Arrays.asList(tile)));
        }

        // and then loop through all the Tiles in order to add them, if possible to each TileSet. Not sure if it's the
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
        return validTileSets;
    }

    private void addTileFromRackToTileSet(Rack rack, int index, TileSet tileSet) {
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
     * This method returns whether the {@link Tile} {@code tileToAdd} can be added to the {@code TileSet}
     * 
     * @param tileToAdd
     *            the {@link Tile} to add
     * @return whether the {@link Tile} {@code tileToAdd} can be added
     */
    public boolean canAddToSet(TileSet tileSet, Tile tileToAdd) {
    	return (tileSet instanceof TileGroup) ? canAddToSet((TileGroup) tileSet, tileToAdd) : canAddToSet((TileRun) tileSet, tileToAdd);
    }
    

    private boolean canAddToSet(TileGroup group, Tile tileToAdd) {
    	if (group.size() >= 4) {
			return false;
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

    private boolean canAddToSet(TileRun run, Tile tileToAdd) {
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
     * This method insert a tile to the correct spot in the set and returns the index of the position it was inserted at
     * 
     * @param tileToAdd
     *            the {@link Tile} to add
     * @return the index of the position it was inserted at
     */
    public int addToSet(TileSet tileSet, Tile tileToAdd) {
    	return (tileSet instanceof TileGroup) ? addToSet((TileGroup) tileSet, tileToAdd) : addToSet((TileRun) tileSet, tileToAdd);
    }
    
    private int addToSet(TileGroup group, Tile tileToAdd) {
        group.add(tileToAdd);
        return 0;
    }

    private int addToSet(TileRun run, Tile tileToAdd) {
        if (!canAddToSet(run, tileToAdd)) {
            throw new IllegalArgumentException("Tile can't be added to set");
        }
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
     * This method returns whether a specific {@link Tile} can be added to the given {@link TileRun} by shifting the
     * run. Shifting a run means adding a Tile either at the beginning or the end of the run.
     * 
     * @param tileRun
     * @param tile
     * @return
     */
    public boolean canShiftRun(TileRun tileRun, Tile tile) {
        return !getShiftRunIndexes(tileRun, tile).isEmpty();
    }

    public TileRun shiftRun(TileRun tileRun, Tile tile) {
        List<Integer> indexes = getShiftRunIndexes(tileRun, tile);
        if (!indexes.isEmpty()) {
            // XXX The strategy should be the one to decide where to put it
            tileRun.add(indexes.get(0), tile);
        } else {
            throw new IllegalArgumentException(format("Tile %s can't be used to shift TileRun %s", tile, tileRun));
        }
        return tileRun;
    }

    private List<Integer> getShiftRunIndexes(TileRun tileRun, Tile tile) {
        if (tile.isJoker()) {
            // XXX: Is that correct when the TileRun already contains a Jocker. Check definition of Tile.equals
            return Arrays.asList(0, tileRun.size());
        }
        if (tileRun.contains(tile)) {
            return Collections.emptyList();
        }

        TileColor tileSetColor = tileRun.getColor();
        if (tileSetColor == tile.getColor()) {
            if (tileRun.getLowerBound() == (tile.getNumber() + 1)) {
                return Arrays.asList(0);
            } else if (tileRun.getUpperBound() == (tile.getNumber() - 1)) {
                return Arrays.asList(tileRun.size());
            }
        }
        return Collections.emptyList();
    }

    /**
     * This method returns whether a specific {@link Tile} can be added to the given {@link TileRun}
     * 
     * @param tileRun
     * @param tile
     * @return
     */
    public boolean canSplitRun(TileRun tileRun, Tile tile) {
        return !getSplitRunIndexes(tileRun, tile).isEmpty();
    }

    public List<TileRun> splitRun(TileRun tileRun, Tile tile) {
        List<Integer> indexes = getSplitRunIndexes(tileRun, tile);
        if (!indexes.isEmpty()) {
            // XXX The strategy should be the one to decide where to put it
            int index = indexes.get(0);
            TileRun lowerTileRun = new TileRun(tileRun.subList(0, index));
            TileRun upperTileRun = new TileRun(tileRun.subList(index + 1, tileRun.size()));
            lowerTileRun.add(tile);
            upperTileRun.add(0, tile);
            return Arrays.asList(lowerTileRun, upperTileRun);
        } else {
            throw new IllegalArgumentException(format("Tile %s can't be used to split TileRun %s", tile, tileRun));
        }
    }

    private List<Integer> getSplitRunIndexes(TileRun tileRun, Tile tile) {
        int tileRunSize = tileRun.size();
        if (tile.isJoker() && tileRunSize >= 5) {
            List<Integer> indexes = new ArrayList<>();
            for (int i = 2; i < tileRunSize - 1; i++) {
                indexes.add(i);
            }
            return indexes;
        }
        TileColor tileSetColor = tileRun.getColor();
        if (tileSetColor == tile.getColor() && tileRunSize >= 5) {
            Integer lowerBound = tileRun.getLowerBound();
            Integer upperBound = tileRun.getUpperBound();
            if (lowerBound + 2 <= tile.getNumber() && tile.getNumber() + 2 <= upperBound) {
                return Arrays.asList(tile.getNumber() - lowerBound);
            }
        }
        return Collections.emptyList();
    }

    /**
     * 
     * @param tileGroup
     * @param tile
     * @return
     */
    public boolean canSubstituteInGroup(TileGroup tileGroup, Tile tile) {
        int tileSetSize = tileGroup.size();
        if (tileSetSize >= 4) {
            return false;
        }
        if (tile.isJoker()) {
            return !tileGroup.containsJocker(tile);
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

    public TileGroup substituteInGroup(TileGroup tileGroup, Tile tile) {
        if (canSubstituteInGroup(tileGroup, tile)) {
            tileGroup.add(tile);
        } else {
            throw new IllegalArgumentException(format("Tile %s can't substitute in TileGroup %s", tile, tileGroup));
        }
        return tileGroup;
    }

}
