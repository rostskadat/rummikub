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

	public boolean isValid() {
		return size() >= 3;
	}

	public abstract int getScore();
}
