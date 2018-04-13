package net.pictulog.ml.rummikub.model;

import lombok.Data;

/**
 * A {@code Move} represent a valid move for that specific tuple of {@link TileSet}, {@link Tile}, and its index in the
 * {@link TileSet}
 * 
 * @author rostskadat
 *
 */
@Data
public class Move {

    private TileSet tileSet;

    private Tile tile;

    private Integer indexInSet;
}
