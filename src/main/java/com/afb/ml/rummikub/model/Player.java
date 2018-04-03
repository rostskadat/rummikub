package com.afb.ml.rummikub.model;

import java.io.Serializable;
import java.util.List;

/**
 * A {@code Player} encapsulates the state of a physical player, and provides short hand methods to manipulate its
 * associated {@link Rack}.
 * 
 * @author rostskadat
 *
 */
public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private Rack rack = new Rack();

    private boolean started = false;

    public Player() {
        super();
    }

    public Player(String name) {
        super();
        this.name = name;
    }

    public Player(String name, Rack rack) {
        super();
        this.name = name;
        this.rack = rack;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Rack getRack() {
        return rack;
    }

    public void setRack(Rack rack) {
        this.rack = rack;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Player [");
        if (name != null) {
            builder.append("name=");
            builder.append(name);
            builder.append(", ");
        }
        if (rack != null) {
            builder.append("rack=");
            builder.append(rack);
        }
        builder.append("]");
        return builder.toString();
    }

}
