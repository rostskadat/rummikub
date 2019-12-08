package net.pictulog.ml.rummikub.model;

import java.util.List;

/**
 * A {@code TileRun} is a specific {@link TileSet} where each {@link Tile} has
 * successive number but identical {@link TileColor}.
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
	public int getScore() {
		Integer lowestBound = getLowerBound();
		if (lowestBound == null) {
			// only jockers
			return 30 * size();
		}
		int highestBound = lowestBound + size() - 1;
		return (lowestBound + highestBound) * (highestBound - lowestBound + 1) / 2;
	}

	public Integer getLowerBound() {
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

	public Integer getUpperBound() {
		Integer upperBound = getLowerBound();
		if (upperBound != null) {
			upperBound += size() - 1;
		}
		return upperBound;
	}

	public TileColor getColor() {
		TileColor tileSetColor = null;
		for (Tile tile : this) {
			if (!tile.isJoker()) {
				tileSetColor = tile.getColor();
				break;
			}
		}
		return tileSetColor;
	}

	@Override
	public boolean equals(Object o) {
		return (o != null && o instanceof TileRun && ((TileRun) o).size() == size() && containsAll((TileRun) o));
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

}
