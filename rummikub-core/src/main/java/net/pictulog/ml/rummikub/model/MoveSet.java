package net.pictulog.ml.rummikub.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import net.pictulog.ml.rummikub.model.MoveSet.Move;

/**
 * A {@code Move} capture the "after-state" of a specific move by a {@link Player}. There are different kind of
 * {@code Move} that can be represented. a {@code Move} can originated from a specific {@link TileSet} and result in one
 * or more {@link TileSet}. For instance by splitting a {@link TileRun}. A {@code Move} can also have no originating
 * {@link TileSet}. As for instance when creating a new {@link TileRun} or {@link TileGroup} from scratch. Note that
 * some situation require the use of a specific LIFO
 * 
 * @author rostskadat
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MoveSet extends ArrayList<Move> {

    private static final long serialVersionUID = 1L;

    private List<Tile> heap = new ArrayList<>();

    private Player player;

    /**
     * A
     * 
     * @author N090536
     *
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Move implements Serializable {

        private static final long serialVersionUID = 1L;

        private TileSet fromTileSet;

        @NonNull
        private List<Tile> tiles = new ArrayList<>();

        @NonNull
        private List<TileSet> toTileSets = new ArrayList<>();

    }

}
