package com.afb.ml.rummikub.model;

import java.util.ArrayList;
import java.util.List;

public class Pool extends ArrayList<Tile> {

    private static final long serialVersionUID = 1L;

    public Pool() {
        super();
    }

    public Pool(List<Tile> tiles) {
        super(tiles);
    }
}
