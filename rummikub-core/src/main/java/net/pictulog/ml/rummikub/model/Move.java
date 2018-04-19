package net.pictulog.ml.rummikub.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * A {@code Move} capture the "after-state" of the game.
 * 
 * @author rostskadat
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Move implements Serializable, Comparable<Move> {

    private static final long serialVersionUID = 1L;

    private TileSet fromTileSet;

    @NonNull
    private List<Tile> tiles = new ArrayList<>();

    @NonNull
    private List<TileSet> toTileSets = new ArrayList<>();

    @Override
    public int compareTo(Move o) {
        return 0;
    }
}
