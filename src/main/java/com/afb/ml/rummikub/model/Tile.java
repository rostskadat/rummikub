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