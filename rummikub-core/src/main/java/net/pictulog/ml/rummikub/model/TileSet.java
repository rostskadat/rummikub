package net.pictulog.ml.rummikub.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@code TileSet} is the basic grouping of {@link Tile}.
 * 
 * @author rostskadat
 *
 */
public abstract class TileSet extends ArrayList<Tile> {

	private static final long serialVersionUID = 1L;

	public TileSet() {
		super();
	}

	public TileSet(List<Tile> tiles) {
		super(tiles);
	}

	/**
	 * This method returns whether this is a valid {@code TileSet} or not. A
	 * {@code TileSet} is considered valid if it has strictly more than 2
	 * {@link Tile}. Each specific implementation of {@code TileSet} might have
	 * additional more stringent conditions to be considered valid.
	 * 
	 * @return whether the {@code TileSet} is valid.
	 */
	public boolean isValid() {
		return size() >= 3;
	}

	/**
	 * This method returns the score associated with this specific {@code TileSet}
	 * 
	 * @return the score of this {@code TileSet}
	 */
	public abstract int getScore();

	/**
	 * This method returns whether the TileSet contains a specific Jocker
	 * 
	 * @param jocker
	 * @return
	 */
	public boolean containsJocker(Tile jocker) {
		assert (jocker.isJoker());
		for (Tile tile : this) {
			if (tile.isJoker() && tile.getColor() == jocker.getColor()) {
				return true;
			}
		}
		return false;
	}

}
