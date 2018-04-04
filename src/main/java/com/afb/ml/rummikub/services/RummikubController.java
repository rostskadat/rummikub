package com.afb.ml.rummikub.services;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import com.afb.ml.rummikub.common.GameStateUtils;
import com.afb.ml.rummikub.model.GameState;
import com.afb.ml.rummikub.model.Player;
import com.afb.ml.rummikub.model.Rack;
import com.afb.ml.rummikub.model.Tile;
import com.afb.ml.rummikub.services.strategy.IStrategy;

@Controller
public class RummikubController {

    private static final Log LOG = LogFactory.getLog(RummikubController.class);

    @Value("${numberOfPlayer:4}")
    private int numberOfPlayer;

    @Value("${numberOfTilesPerPlayer:14}")
    private int numberOfTilesPerPlayer;

    @Value("${jockerPenality:30}")
    private int jockerPenality;

    @Value("${useGameStateFilename}")
    private boolean useGameStateFilename;

    @Value("${gameStateFilename}")
    private File gameStateFilename;

    private List<Player> players = new ArrayList<>();

    @Autowired
    private IStrategy strategy;

    @Autowired
    private PoolController poolController;

    @Autowired
    private TableController tableController;

    @PostConstruct
    private void postConstruct() {
        if (useGameStateFilename && GameStateUtils.isValidGameSeed(gameStateFilename)) {
            LOG.debug(String.format("Reading Players state from %s...", gameStateFilename));
            readGameState();
        } else {
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
        LOG.info("RummikubController initialization... OK");
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
        if (useGameStateFilename) {
            GameState gameState = new GameState();
            gameState.setPlayers(players);
            gameState.setPool(poolController.getPool());
            gameState.setTable(tableController.getTable());
            try {
                GameStateUtils.writeGameState(gameStateFilename, gameState);
            } catch (IOException e) {
                LOG.error(e);
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

    private void readGameState() {
        try {
            GameState state = GameStateUtils.readGameState(gameStateFilename);
            players.addAll(state.getPlayers());
        } catch (IOException e) {
            throw new BeanCreationException(e.getMessage(), e);
        }
    }
}
