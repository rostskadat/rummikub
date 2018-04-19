package net.pictulog.ml.rummikub.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Game implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Move> moves = new ArrayList<>();

    public boolean isEnd() {
        return false;
    }

    public void playMove(Player player, Move move) {
        moves.add(move);
    }

    public void undoMove(Player player, Move move) {
        moves.remove(moves.size() - 1);
    }

    public List<Move> getPossibleMoves(Player player) {
        return new ArrayList<>();
    }

    public double evaluate(Player player) {
        return 0D;
    }

    public Player getNextPlayer(Player player) {
        return player;
    }

}
