package net.pictulog.ml.rummikub.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * A {@code Player} encapsulates the state of a physical player, and provides short hand methods to manipulate its
 * associated {@link Rack}.
 * 
 * @author rostskadat
 *
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    @NonNull
    private String name;

    private Rack rack = new Rack();

    private boolean started = false;

    @JsonIgnore
    public boolean isFinished() {
        return rack.isEmpty();
    }

    public void addTileToRack(Tile tile) {
        rack.add(tile);
    }

    public void removeTileFromRack(Tile tile) {
        rack.remove(tile);
    }

    public void removeAllTilesFromRack(List<Tile> tiles) {
        rack.removeAll(tiles);
    }
}
