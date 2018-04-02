package com.afb.ml.rummikub.model;

import java.util.List;

public class TileGroup extends TileSet {

	private static final long serialVersionUID = 1L;

	public TileGroup() {
		super();
	}

	public TileGroup(List<Tile> tiles) {
		super(tiles);
	}

	public boolean canAdd(Tile tileToAdd) {
		if (size() >= 4) {
			return false;
		}
		if (tileToAdd.isJoker()) {
			return true;
		}
		TileColor colorToAdd = tileToAdd.getColor();
		boolean seenTileColorToAdd = false;
		int groupNumber = -1;
		for (Tile tile : this) {
			if (!tile.isJoker()) {
				groupNumber = tile.getNumber();
			}
			if (colorToAdd.equals(tile.getColor())) {
				seenTileColorToAdd = true;
			}
		}
		if (tileToAdd.getNumber() == groupNumber && !seenTileColorToAdd) {
			return true;
		}
		return false;
	}

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

}
