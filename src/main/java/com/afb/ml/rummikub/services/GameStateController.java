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

    @Value("${useSavedGame}")
    private boolean useSavedGame;

    @Value("${gameStateFilename}")
    private File gameStateFilename;

    // The list of tiles to be drawn (from a previous game).
    private List<Integer> tileIndexesFromPreviousGame;

    // The list of tiles to be drawn (from a previous game).
    private List<Integer> drawnTileIndexes;

    // Lazy is required to make sure that the postConstruct method is called before any of the others
    @Lazy
    @Autowired
    private PlayerController playerController;

    @Lazy
    @Autowired
    private PoolController poolController;

    @Lazy
    @Autowired
    private TableController tableController;

    @PostConstruct
    private void postConstruct() {
        tileIndexesFromPreviousGame = new ArrayList<>();
        drawnTileIndexes = new ArrayList<>();
        if (useSavedGame) {
            if (isValidGameSeed(gameStateFilename)) {
                LOG.debug(format("Reading game state from %s...", gameStateFilename));
                GameState gameState = readGameState();
                tileIndexesFromPreviousGame.addAll(gameState.getDrawnTileIndexes());
            } else {
                LOG.warn(format("No game state found in %s", gameStateFilename));
            }
        }
    }

    @CheckReturnValue
    public Tile getNextTile(Pool pool) {
        int nextTileIndex = -1;
        if (useSavedGame && !tileIndexesFromPreviousGame.isEmpty()) {
            // OK I had a valid previous game
            nextTileIndex = tileIndexesFromPreviousGame.remove(0);
        } else {
            // Otherwise I just do not save the game or it was not saved properly the last time
            nextTileIndex = ThreadLocalRandom.current().nextInt(0, pool.size());
        }
        drawnTileIndexes.add(nextTileIndex);
        assert (nextTileIndex != -1);
        return pool.remove(nextTileIndex);
    }

    public void saveGameFinalState() {
        if (useSavedGame) {
            GameState gameState = new GameState();
            gameState.setFinalPool(poolController.getPool());
            gameState.setFinalTable(tableController.getTable());
            gameState.setFinalPlayers(playerController.getPlayers());
            gameState.setDrawnTileIndexes(drawnTileIndexes);
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

    public List<Integer> getTilesFromPreviousGame() {
        return tileIndexesFromPreviousGame;
    }

    public void setTilesFromPreviousGame(List<Integer> tileIndexesFromPreviousGame) {
        this.tileIndexesFromPreviousGame = tileIndexesFromPreviousGame;
    }

}
