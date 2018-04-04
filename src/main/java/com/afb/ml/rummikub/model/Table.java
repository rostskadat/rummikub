package com.afb.ml.rummikub.model;

import java.util.ArrayList;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A {@code Table} represents all the {@link TileSet} played so far by the different {@link Player}
 * 
 * @author rostskadat
 *
 */
@JsonSerialize(as = ArrayList.class)
public class Table extends ArrayList<TileSet> {

    private static final long serialVersionUID = 1L;

}
