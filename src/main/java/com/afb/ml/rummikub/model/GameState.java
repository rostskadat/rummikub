package com.afb.ml.rummikub.model;

import java.io.Serializable;
import java.util.List;

public class GameState implements Serializable {

    private static final long serialVersionUID = 1L;

    private Table table;

    private Pool pool;

    private List<Player> players;

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public Pool getPool() {
        return pool;
    }

    public void setPool(Pool pool) {
        this.pool = pool;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

}
