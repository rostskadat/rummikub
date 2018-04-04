package com.afb.ml.rummikub.model;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A {@code Pool} represent the list of {@link Tile} available at the start of the game and from which each
 * {@link Player} draws its own {@link Tile}
 * 
 * @author rostskadat
 *
 */
@JsonSerialize(as = ArrayList.class)
public class Pool extends ArrayList<Tile> {

    private static final long serialVersionUID = 1L;

}
