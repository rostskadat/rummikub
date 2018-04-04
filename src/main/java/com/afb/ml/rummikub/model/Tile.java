package com.afb.ml.rummikub.model;

import java.io.Serializable;

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

    public Tile(TileColor color) {
        super();
        this.number = null;
        this.color = color;
        this.joker = true;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public TileColor getColor() {
        return color;
    }

    public void setColor(TileColor color) {
        this.color = color;
    }

    public boolean isJoker() {
        return joker;
    }

    public void setJoker(boolean joker) {
        this.joker = joker;
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
                && (isJoker() || ((Tile) o).isJoker() || ((Tile) o).getNumber() == getNumber()
                        && ((Tile) o).getColor() == getColor()));
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
