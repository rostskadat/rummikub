package com.afb.ml.rummikub.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameState implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Tile> drawnTiles = new ArrayList<>();

    private Table finalTable;

    private Pool finalPool;

    private List<Player> finalPlayers = new ArrayList<>();

    public List<Tile> getDrawnTiles() {
        return drawnTiles;
    }

    public void setDrawnTiles(List<Tile> drawnTiles) {
        this.drawnTiles = drawnTiles;
    }

    public Table getFinalTable() {
        return finalTable;
    }

    public void setFinalTable(Table finalTable) {
        this.finalTable = finalTable;
    }

    public Pool getFinalPool() {
        return finalPool;
    }

    public void setFinalPool(Pool finalPool) {
        this.finalPool = finalPool;
    }

    public List<Player> getFinalPlayers() {
        return finalPlayers;
    }

    public void setFinalPlayers(List<Player> finalPlayers) {
        this.finalPlayers = finalPlayers;
    }

}
