package net.pictulog.ml.rummikub.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tile implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer number;

    private TileColor color;

    private boolean joker;

    public Tile() {
        super();
        this.joker = false;
    }

    public Tile(Integer number, TileColor color) {
        super();
        this.number = number;
        this.color = color;
        this.joker = false;
    }

    /**
     * This constructor will create a special {@code Tile} called a Jocker. The
     * Jocker does not have a specific value, but take its numerical value from 
     * the other {@code Tile}s present in the {@link TileRun} or 
     * {@link TileGroup} it is in.
     * 
     * @param color The {@link TileColor} of this Jocker
     */
    public Tile(TileColor color) {
        super();
        this.number = null;
        this.color = color;
        this.joker = true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (!joker) {
            builder.append(number);
            builder.append("/");
        } else {
            builder.append("JOCKER/");
        }
        builder.append(color);
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        return (o != null && o instanceof Tile
                && (sameJocker(this, (Tile) o) || (sameNumber(this, (Tile) o) && sameColor(this, (Tile) o))));
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    private static final boolean sameJocker(Tile t1, Tile t2) {
        return t1.isJoker() && t2.isJoker() && sameColor(t1, t2);
    }

    private static final boolean sameColor(Tile t1, Tile t2) {
        return t1.getColor() == t2.getColor();
    }

    private static final boolean sameNumber(Tile t1, Tile t2) {
        return t1.getNumber() == t2.getNumber();
    }

}
