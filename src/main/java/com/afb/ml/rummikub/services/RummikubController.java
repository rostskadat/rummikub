package com.afb.ml.rummikub.services;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.afb.ml.rummikub.model.Player;
import com.afb.ml.rummikub.model.Rack;
import com.afb.ml.rummikub.model.Tile;

@Controller
public class RummikubController {

    private static final Log LOG = LogFactory.getLog(RummikubController.class);

    private static final int NUMBER_OF_PLAYERS = 4;
    private static final int NUMBER_OF_TILES = 14;
    private static final int JOCKER_PENALITY = 30;

    private List<Player> players = new ArrayList<>();

    @Autowired
    private IStrategy strategy;

    @Autowired
    private PoolController poolController;

    @PostConstruct
    private void postConstruct() {
        LOG.debug("Creating players...");
        for (int i = 0; i < NUMBER_OF_PLAYERS; i++) {
            players.add(new Player(format("Player_%d", i)));
        }
        LOG.debug("Drawing initial tiles...");
        for (Player player : players) {
            for (int i = 0; i < NUMBER_OF_TILES; i++) {
                player.addTileToRack(poolController.drawTileFromPool());
            }
            LOG.debug(player.toString());
        }
        LOG.info("RummikubService initialization... OK");
    }

    public void play() {
        LOG.info("Playing Rummikub now...");
        int roundNumber = 0;
        boolean hasPlayedInThisRound = false;
        do {
            hasPlayedInThisRound = false;
            for (Player player : players) {
                LOG.info(format("Round %d, Player %s is playing...", roundNumber, player.getName()));
                hasPlayedInThisRound |= strategy.play(player);
                if (player.isFinished()) {
                    LOG.debug(format("Player %s is finished!", player.getName()));
                    break;
                }
            }
            roundNumber++;
        } while (!isGameBlocked(hasPlayedInThisRound));
        if (isGameBlocked(hasPlayedInThisRound)) {
            LOG.warn(format("Game blocked on round %d", roundNumber));
        }
        getWinner();
    }

    private boolean isGameBlocked(boolean hasPlayedInThisRound) {
        return !(hasPlayedInThisRound || poolController.getPoolSize() > 0);
    }

    private Player getWinner() {
        Player winner = null;
        int smallestScore = -1;
        for (Player player : players) {
            Rack rack = player.getRack();
            int score = 0;
            for (Tile tile : rack) {
                score += tile.isJoker() ? JOCKER_PENALITY : tile.getNumber();
            }
            if (smallestScore == -1 || score < smallestScore) {
                winner = player;
                smallestScore = score;
            }
            LOG.debug(format("Player %s has %d points (%d tile(s))", player.getName(), score, rack.size()));
        }
        LOG.info(format("And the winner is %s", winner.getName()));
        return winner;
    }
}
