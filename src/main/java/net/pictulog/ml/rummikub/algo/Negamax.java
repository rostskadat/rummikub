package net.pictulog.ml.rummikub.algo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.pictulog.ml.rummikub.model.Game;
import net.pictulog.ml.rummikub.model.Move;
import net.pictulog.ml.rummikub.model.Player;

/**
 * cf https://en.wikipedia.org/wiki/Negamax
 * 
 * @author rostskadat
 *
 */
public class Negamax {

    private Negamax() {
        // NA
    }

    public static Pair negamax(Game game, Move move, int depth, Player player) {
        if (depth == 0 || game.isEnd()) {
            return new Pair(game.evaluate(player), null);
        }
        Pair bestMove = new Pair(Double.NEGATIVE_INFINITY, null);
        for (Move nextMove : game.getPossibleMoves(player)) {
            game.playMove(player, nextMove);
            Pair v = negamax(game, nextMove, depth - 1, game.getNextPlayer(player));
            v.score = -v.score;
            bestMove.score = Math.max(bestMove.score, v.score);
            game.undoMove(player, nextMove);
        }
        return bestMove;
    }

    public static Pair negaMaxAB(Game game, Move move, int depth, double alpha, double beta, Player player) {
        if (depth == 0 || game.isEnd()) {
            return new Pair(game.evaluate(player), null);
        }
        Pair bestMove = new Pair(Double.NEGATIVE_INFINITY, null);
        for (Move nextMove : orderMoves(generateMoves(game, player, move))) {
            game.playMove(player, nextMove);
            Pair v = negaMaxAB(game, nextMove, depth - 1, -beta, -alpha, game.getNextPlayer(player));
            v.score = -v.score;
            bestMove.score = Math.max(bestMove.score, v.score);
            alpha = Math.max(alpha, v.score);
            game.undoMove(player, nextMove);
            if (alpha >= beta) {
                break;
            }
        }
        return bestMove;

    }

    private static List<Move> generateMoves(Game game, Player player, Move move) {
        return new ArrayList<>();
    }

    private static List<Move> orderMoves(List<Move> moves) {
        Collections.sort(moves);
        return moves;
    }

    public static void probabilisticNegaMax() {
        // NA
    }

    public static void probabilisticNegaMaxABPruning() {
        // NA
    }

    @Data
    @RequiredArgsConstructor
    public static class Pair {
        @NonNull
        Double score;
        @NonNull
        Move move;
    }

}
