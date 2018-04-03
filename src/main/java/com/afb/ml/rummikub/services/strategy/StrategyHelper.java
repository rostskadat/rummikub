package com.afb.ml.rummikub.services.strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.afb.ml.rummikub.model.Rack;
import com.afb.ml.rummikub.model.Tile;
import com.afb.ml.rummikub.model.TileGroup;
import com.afb.ml.rummikub.model.TileRun;
import com.afb.ml.rummikub.model.TileSet;

@Service
public class StrategyHelper {

    @Value("${initialScoreThreshold:30}")
    private int initialScoreThreshold;

    /**
     * This method returns the sets that are valid for an Initial move (whose score is more than
     * {@code INITIAL_MIN_SCORE} (30)).
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
        if (tileSet.canAddToSet(tile)) {
            tileSet.addToSet(tile);
        }
        if (index == rack.size() - 1) {
            return;
        }
        // And keep on going with the rest of the rack
        addTileFromRackToTileSet(rack, index + 1, tileSet);
    }

    /**
     * This method returns whether a specific {@link Tile} can be added to the given {@link TileRun}
     * 
     * @param tileRun
     * @param tile
     * @return
     */
    public boolean canShiftRun(TileRun tileRun, Tile tile) {
        return getShiftRunIndex(tileRun, tile) != -1;
    }

    public void shiftRun(TileRun tileRun, Tile tile) {
        int index = getShiftRunIndex(tileRun, tile);
        if (index != -1) {
            tileRun.add(index, tile);
        }
    }

    private int getShiftRunIndex(TileRun tileRun, Tile tile) {
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
     * This method returns whether a specific {@link Tile} can be added to the given {@link TileRun}
     * 
     * @param tileRun
     * @param tile
     * @return
     */
    public boolean canSplitRun(TileRun tileRun, Tile tile) {
        return getSplitRunIndex(tileRun, tile) != -1;
    }

    public void splitRun(TileRun tileRun, Tile tile) {
        int index = getSplitRunIndex(tileRun, tile);
        if (index != -1) {
            tileRun.add(index, tile);
        }
    }

    private int getSplitRunIndex(TileRun tileRun, Tile tile) {
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
    public boolean canSubstituteInGroup(TileGroup tileGroup, Tile tile) {
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

    public void substituteInGroup(TileGroup tileGroup, Tile tile) {
        if (canSubstituteInGroup(tileGroup, tile)) {
            tileGroup.addToSet(tile);
        }
    }

}
