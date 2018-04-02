package com.afb.ml.rummikub.model;

import java.util.List;

public class TileRun extends TileSet {

	private static final long serialVersionUID = 1L;

	public TileRun() {
		super();
	}

	public TileRun(List<Tile> tiles) {
		super(tiles);
	}

	public boolean canAdd(Tile tile) {
		if (size() == 0) {
			return true;
		}
		if (tile.isJoker()) {
			return true;
		}
		if (get(0).getNumber() == tile.getNumber() + 1) {
			return true;
		} else if (get(size() - 1).getNumber() == tile.getNumber() - 1) {
			return true;
		}
		return false;
	}

	@Override
	public int getScore() {
		int index = 0;
		int lowestBound = -1;
		for (Tile tile : this) {
			if (!tile.isJoker()) {
				lowestBound = tile.getNumber() - index;
				break;
			}
			index++;
		}
		int highestBound = lowestBound + size() - 1;
		return (lowestBound + highestBound) * (highestBound - lowestBound + 1) / 2;
	}
}
