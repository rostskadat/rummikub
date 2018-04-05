package com.afb.ml.rummikub.services;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.assertj.core.util.CheckReturnValue;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import com.afb.ml.rummikub.model.GameState;
import com.afb.ml.rummikub.model.Pool;
import com.afb.ml.rummikub.model.Tile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.SerializationFeature;

@Controller
public class GameStateController {

    private static final Log LOG = LogFactory.getLog(GameStateController.class);

    @Value("${useGameStateFilename}")
    private boolean useGameStateFilename;

    @Value("${gameStateFilename}")
    private File gameStateFilename;

    // The list of tiles to be drawn (from a previous game).
    private List<Tile> tilesFromPreviousGame;

    // The list of tiles to be drawn (from a previous game).
    private List<Tile> drawnTiles;

    // Lazy is required to make sure that the postConstruct method is called before any of the others
    @Lazy
    @Autowired
    PlayerController playerController;

    @Lazy
    @Autowired
    private PoolController poolController;

    @Lazy
    @Autowired
    private TableController tableController;

    @PostConstruct
    private void postConstruct() {
        tilesFromPreviousGame = new ArrayList<>();
        drawnTiles = new ArrayList<>();
        if (useGameStateFilename) {
            if (isValidGameSeed(gameStateFilename)) {
                LOG.debug(format("Reading game state from %s...", gameStateFilename));
                GameState gameState = readGameState();
                tilesFromPreviousGame.addAll(gameState.getDrawnTiles());
            } else {
                LOG.warn(format("No game state found in %s", gameStateFilename));
            }
        }
    }

    @CheckReturnValue
    public Tile getNextTile(Pool pool) {
        Tile nextTile = null;
        if (useGameStateFilename && !tilesFromPreviousGame.isEmpty()) {
            // OK I had a valid previous game
            nextTile = tilesFromPreviousGame.remove(0);
            pool.remove(nextTile);
        } else {
            // Otherwise I just do not save the game or it was not saved properly the last time
            nextTile = pool.remove(ThreadLocalRandom.current().nextInt(0, pool.size()));
        }
        drawnTiles.add(nextTile);
        return nextTile;

    }

    public void saveGameFinalState() {
        if (useGameStateFilename) {
            GameState gameState = new GameState();
            gameState.setFinalPool(poolController.getPool());
            gameState.setFinalTable(tableController.getTable());
            gameState.setFinalPlayers(playerController.getPlayers());
            gameState.setDrawnTiles(drawnTiles);
            try {
                writeGameState(gameStateFilename, gameState);
            } catch (IOException e) {
                LOG.error(e);
            }
        }
    }

    private boolean isValidGameSeed(File file) {
        return file.exists() && file.isFile() && file.canRead();
    }

    private GameState readGameState(File gameStateFilename) throws IOException {
        return new ObjectMapper().enableDefaultTyping(DefaultTyping.NON_CONCRETE_AND_ARRAYS)
                .readValue(gameStateFilename, GameState.class);
    }

    private void writeGameState(File gameStateFilename, GameState gameState) throws IOException {
        File parent = gameStateFilename.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        new ObjectMapper().enableDefaultTyping(DefaultTyping.NON_CONCRETE_AND_ARRAYS)
                .enable(SerializationFeature.INDENT_OUTPUT)
                .writeValue(gameStateFilename, gameState);
    }

    private GameState readGameState() {
        try {
            return readGameState(gameStateFilename);
        } catch (IOException e) {
            throw new BeanCreationException(e.getMessage(), e);
        }
    }

}
