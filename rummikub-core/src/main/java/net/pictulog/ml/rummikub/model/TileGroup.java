package net.pictulog.ml.rummikub.model;

import java.util.List;

/**
 * A {@code TileGroup} is a specific {@link TileSet} where each {@link Tile} has
 * a different {@link TileColor} but identical number.
 * 
 * @author rostskadat
 *
 */
public class TileGroup extends TileSet {

	private static final long serialVersionUID = 1L;

	public TileGroup() {
		super();
	}

	public TileGroup(List<Tile> tiles) {
		super(tiles);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getScore() {
		int score = 0;
		int jockerScore = 0;
		int jockerCount = 0;
		for (Tile tile : this) {
			if (tile.isJoker()) {
				jockerCount++;
			} else {
				score += tile.getNumber();
				jockerScore = tile.getNumber();
			}
		}
		return score + jockerCount * jockerScore;
	}

	@Override
	public boolean equals(Object o) {
		return (o != null && o instanceof TileGroup && ((TileGroup) o).size() == size() && containsAll((TileGroup) o));
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
