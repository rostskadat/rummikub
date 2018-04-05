package com.afb.ml.rummikub.services;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import com.afb.ml.rummikub.model.Player;
import com.afb.ml.rummikub.model.Rack;
import com.afb.ml.rummikub.model.Tile;
import com.afb.ml.rummikub.services.strategy.IStrategy;

@Controller
public class PlayerController {

    private static final Log LOG = LogFactory.getLog(PlayerController.class);

    @Value("${numberOfPlayer:4}")
    private int numberOfPlayer;

    @Value("${numberOfTilesPerPlayer:14}")
    private int numberOfTilesPerPlayer;

    @Value("${jockerPenality:30}")
    private int jockerPenality;

    private List<Player> players;

    @Autowired
    private IStrategy strategy;

    @Autowired
    private PoolController poolController;

    @Autowired
    private TableController tableController;

    @Autowired
    private GameStateController gameStateController;

    @PostConstruct
    private void postConstruct() {
        players = new ArrayList<>();
        if (players.isEmpty()) {
            LOG.debug("Creating players...");
            for (int i = 0; i < numberOfPlayer; i++) {
                players.add(new Player(format("Player_%d", i)));
            }
            LOG.debug("Drawing initial tiles...");
            for (Player player : players) {
                for (int i = 0; i < numberOfTilesPerPlayer; i++) {
                    player.addTileToRack(poolController.drawTileFromPool());
                }
            }
        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void play() {
        LOG.info("Playing Rummikub now...");
        if (LOG.isDebugEnabled()) {
            LOG.debug(getGameStatus("Starting with:"));
        }
        int roundNumber = 0;
        boolean hasPlayedInThisRound = false;
        do {
            hasPlayedInThisRound = playRound();
            if (LOG.isDebugEnabled()) {
                LOG.debug(getGameStatus(format("Round %d:", roundNumber)));
            }
            roundNumber++;
        } while (!isGameBlocked(hasPlayedInThisRound));
        if (isGameBlocked(hasPlayedInThisRound)) {
            LOG.warn(format("Game blocked on round %d", roundNumber));
        }
        gameStateController.saveGameFinalState();
        getWinner();
    }

    private boolean playRound() {
        boolean hasPlayedInThisRound = false;
        for (Player player : players) {
            if (strategy.play(player)) {
                if (player.isFinished()) {
                    LOG.debug(format("Player %s has finished!", player.getName()));
                    break;
                }
                hasPlayedInThisRound = true;
            } else {
                if (poolController.getPoolSize() > 0) {
                    player.getRack().add(poolController.drawTileFromPool());
                }
            }
        }
        return hasPlayedInThisRound;
    }

    private boolean isGameBlocked(boolean hasPlayedInThisRound) {
        return !(hasPlayedInThisRound || poolController.getPoolSize() > 0);
    }

    private String getGameStatus(String header) {
        final String EOL = String.format("%n");
        StringBuilder sb = new StringBuilder();
        if (!StringUtils.isEmpty(header)) {
            sb.append(header);
        }
        sb.append(EOL).append("Pool (").append(poolController.getPoolSize()).append("): ")
                .append(poolController.getPool());
        for (Player player : players) {
            sb.append(EOL).append("Player ").append(player.getName()).append(": ")
                    .append(player.getRack());
        }
        sb.append(EOL).append("Table (").append(tableController.getTable().size()).append("): ")
                .append(tableController.getTable());
        return sb.toString();
    }

    private Player getWinner() {
        Player winner = null;
        int smallestScore = -1;
        for (Player player : players) {
            Rack rack = player.getRack();
            int score = 0;
            for (Tile tile : rack) {
                score += tile.isJoker() ? jockerPenality : tile.getNumber();
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
