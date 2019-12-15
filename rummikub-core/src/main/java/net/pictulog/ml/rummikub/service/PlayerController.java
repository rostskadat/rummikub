package net.pictulog.ml.rummikub.service;

import static java.lang.String.format;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import net.pictulog.ml.rummikub.model.Player;
import net.pictulog.ml.rummikub.model.Rack;
import net.pictulog.ml.rummikub.model.Tile;
import net.pictulog.ml.rummikub.service.strategy.IStrategy;

@Controller
public class PlayerController {

    private static final Log LOG = LogFactory.getLog(PlayerController.class);

    @Value("${numberOfPlayer:4}")
    private int numberOfPlayer;

    @Value("${numberOfTilesPerPlayer:14}")
    private int numberOfTilesPerPlayer;

    @Value("${jockerPenality:30}")
    private int jockerPenality;

    private Map<Player, IStrategy> players;

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
        LOG.debug("Creating players...");
        players = new HashMap<>();
        for (int i = 0; i < numberOfPlayer; i++) {
            players.put(new Player(format("Player_%d", i)), strategy);
        }
        LOG.debug("Drawing initial tiles...");
        for (Player player : players.keySet()) {
            for (int i = 0; i < numberOfTilesPerPlayer; i++) {
                player.addTileToRack(gameStateController.drawTile());
            }
        }

    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players.keySet());
    }

    public void play(int rounds) {
        for (int i = 0; i < rounds; i++) {
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
            gameStateController.saveGame();
            getWinner();
        }
    }

    private boolean playRound() {
        boolean hasPlayedInThisRound = false;
        for (Map.Entry<Player, IStrategy> entry : players.entrySet()) {
            Player player = entry.getKey();
            if (entry.getValue().play(player)) {
                if (player.isFinished()) {
                    LOG.debug(format("Player %s has finished!", player.getName()));
                    break;
                }
                hasPlayedInThisRound = true;
            } else {
                if (poolController.getPoolSize() > 0) {
                    player.getRack().add(gameStateController.drawTile());
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
        for (Player player : players.keySet()) {
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
        for (Player player : players.keySet()) {
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
