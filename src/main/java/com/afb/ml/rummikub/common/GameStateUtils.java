package com.afb.ml.rummikub.common;

import java.io.File;
import java.io.IOException;

import com.afb.ml.rummikub.model.GameState;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.SerializationFeature;

public class GameStateUtils {

    private GameStateUtils() {
        // NA
    }

    public static boolean isValidGameSeed(File file) {
        return file.exists() && file.isFile() && file.canRead();
    }

    public static GameState readGameState(File gameStateFilename) throws IOException {
        return new ObjectMapper().enableDefaultTyping(DefaultTyping.NON_CONCRETE_AND_ARRAYS)
                .readValue(gameStateFilename, GameState.class);
    }

    public static void writeGameState(File gameStateFilename, GameState gameState) throws IOException {
        File parent = gameStateFilename.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        new ObjectMapper().enableDefaultTyping(DefaultTyping.NON_CONCRETE_AND_ARRAYS)
                .enable(SerializationFeature.INDENT_OUTPUT)
                .writeValue(gameStateFilename, gameState);
    }

}
