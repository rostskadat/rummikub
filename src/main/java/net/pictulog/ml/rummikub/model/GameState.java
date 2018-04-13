package net.pictulog.ml.rummikub.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameState implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Integer> drawnTileIndexes = new ArrayList<>();

    private Table finalTable;

    private Pool finalPool;

    private List<Player> finalPlayers = new ArrayList<>();

    public List<Integer> getDrawnTileIndexes() {
        return drawnTileIndexes;
    }

    public void setDrawnTileIndexes(List<Integer> drawnTileIndexes) {
        this.drawnTileIndexes = drawnTileIndexes;
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
