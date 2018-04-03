package com.afb.ml.rummikub.model;

import java.util.ArrayList;
import java.util.List;

public abstract class TileSet extends ArrayList<Tile> {

	private static final long serialVersionUID = 1L;

    public TileSet() {
        super();
    }

    public TileSet(List<Tile> tiles) {
        super(tiles);
    }

    /**
     * This method returns whether this is a valid {@code TileSet} or not. A {@code TileSet} is considered valid if it
     * has strictly more than 2 {@link Tile}. Each specific implementation of {@code TileSet} might have additional more
     * stringent conditions to be considered valid.
     * 
     * @return whether the {@code TileSet} is valid.
     */
	public boolean isValid() {
		return size() >= 3;
	}

    /**
     * This method returns whether the {@link Tile} {@code tileToAdd} can be added to the {@code TileSet}
     * 
     * @param tileToAdd
     *            the {@link Tile} to add
     * @return whether the {@link Tile} {@code tileToAdd} can be added
     */
    public abstract boolean canAddToSet(Tile tileToAdd);

    /**
     * This method insert a tile to the correct spot in the set and returns the index of the position it was inserted at
     * 
     * @param tileToAdd
     *            the {@link Tile} to add
     * @return the index of the position it was inserted at
     */
    public abstract int addToSet(Tile tileToAdd);

    /**
     * This method returns the score associated with this specific {@code TileSet}
     * 
     * @return the score of this {@code TileSet}
     */
	public abstract int getScore();
}
