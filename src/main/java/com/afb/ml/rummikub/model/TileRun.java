package com.afb.ml.rummikub.model;

import java.util.List;

/**
 * A {@code TileRun} is a specific {@link TileSet} where each {@link Tile} has successive number but identical
 * {@link TileColor}.
 * 
 * @author rostskadat
 *
 */
public class TileRun extends TileSet {

    private static final long serialVersionUID = 1L;

    public TileRun() {
        super();
    }

    public TileRun(List<Tile> tiles) {
        super(tiles);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canAddToSet(Tile tileToAdd) {
        if (size() == 0) {
            return true;
        }
        // I can add any of the jocker once without constraints
        if (tileToAdd.isJoker()) {
            return !contains(tileToAdd);
        }
        TileColor tileSetColor = getColor();
        Integer lowestBound = getLowestBound();
        Integer upperBound = getUpperBound();

        TileColor tileColor = tileToAdd.getColor();
        int tileNumber = tileToAdd.getNumber();

        // XXX: what happend in case of a jocker tiles?
        return ((tileSetColor == null || tileSetColor == tileColor)
                && (lowestBound == null || (lowestBound == tileNumber + 1) || (upperBound == tileNumber - 1)));
    }

    @Override
    public int addToSet(Tile tileToAdd) {
        if (!canAddToSet(tileToAdd)) {
            throw new IllegalArgumentException("Tile can't be added to set");
        }
        if (size() == 0 || tileToAdd.isJoker()) {
            // XXX: Does it depend on the strategy when dealing with a Joker?
            add(0, tileToAdd);
            return 0;
        }
        Integer lowestBound = getLowestBound();
        Integer tileNumber = tileToAdd.getNumber();
        if (lowestBound == null || lowestBound == tileNumber + 1) {
            add(0, tileToAdd);
            return 0;
        }
        add(tileToAdd);
        return size() - 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getScore() {
        Integer lowestBound = getLowestBound();
        if (lowestBound == null) {
            // only jockers
            return 30 * size();
        }
        int highestBound = lowestBound + size() - 1;
        return (lowestBound + highestBound) * (highestBound - lowestBound + 1) / 2;
    }

    @Override
    public boolean equals(Object o) {
        return (o != null && o instanceof TileRun && ((TileRun) o).size() == size() && containsAll((TileRun) o));
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    private Integer getLowestBound() {
        Integer lowestBound = null;
        int index = 0;
        for (Tile tile : this) {
            if (!tile.isJoker()) {
                lowestBound = tile.getNumber() - index;
                break;
            }
            index++;
        }
        return lowestBound;
    }

    private Integer getUpperBound() {
        Integer upperBound = getLowestBound();
        if (upperBound != null) {
            upperBound += size() - 1;
        }
        return upperBound;
    }

    private TileColor getColor() {
        TileColor tileSetColor = null;
        for (Tile tile : this) {
            if (!tile.isJoker()) {
                tileSetColor = tile.getColor();
                break;
            }
        }
        return tileSetColor;
    }

}
