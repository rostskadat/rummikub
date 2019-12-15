package net.pictulog.ml.rummikub.service;

import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;

import lombok.Getter;
import lombok.Setter;

import com.fasterxml.jackson.databind.SerializationFeature;

import net.pictulog.ml.rummikub.model.GameState;
import net.pictulog.ml.rummikub.model.Pool;
import net.pictulog.ml.rummikub.model.Tile;

/**
 * The {@code GameStateController} is the main entry point to draw {@link Tile}s
 * and save a {@link GameState}
 * 
 * @author rostskadat
 *
 */
@Controller
public class GameStateController {

	private static final Log LOG = LogFactory.getLog(GameStateController.class);

	@Value("${useSavedGame}")
	private boolean useSavedGame;

	@Getter
	@Setter
	@Value("${gameStateFilename}")
	private File gameStateFilename;

	// The list of tiles to be drawn (from a previous game).
	@Getter
	@Setter
	private List<Integer> tileIndexesFromPreviousGame;

	// The list of tiles to be drawn (from a previous game).
	private List<Integer> drawnTileIndexes;

	// Lazy is required to make sure that the postConstruct method is called before
	// any of the others
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
			restaureGame();
		}
	}

	/**
	 * This method returns the next {@link Tile} from the pool. This {@link Tile}
	 * can be either drawn randomly or, when replaying a previously played game,
	 * drawn from the list of previously played {@link Tile}s.
	 * 
	 * @return the next {@link Tile} from the pool
	 */
	public Tile drawTile() {
		Pool pool = poolController.getPool();
		int nextTileIndex = -1;
		if (useSavedGame && !tileIndexesFromPreviousGame.isEmpty()) {
			// OK I had a valid previous game
			nextTileIndex = tileIndexesFromPreviousGame.remove(0);
		} else {
			// Otherwise I just do not save the game or it was not saved properly the last
			// time
			nextTileIndex = ThreadLocalRandom.current().nextInt(0, pool.size());
		}
		assert (nextTileIndex != -1);
		drawnTileIndexes.add(nextTileIndex);
		return pool.remove(nextTileIndex);
	}

	public Tile lookAhead(int offset) {
		Pool pool = poolController.getPool();
		int nextTileIndex = -1;
		if (useSavedGame && !tileIndexesFromPreviousGame.isEmpty() && offset < tileIndexesFromPreviousGame.size()) {
			nextTileIndex = tileIndexesFromPreviousGame.get(offset);
		} else {
			// I need to make sure that I'm not picking twice the same tile
			do {
				nextTileIndex = ThreadLocalRandom.current().nextInt(0, pool.size());
			} while (drawnTileIndexes.contains(nextTileIndex));
		}
		assert (nextTileIndex != -1);
		assert (nextTileIndex < pool.size());
		drawnTileIndexes.add(nextTileIndex);
		return pool.get(nextTileIndex);
	}

	public void saveGame() {
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

	public void restaureGame() {
		if (isValidGameSeed(gameStateFilename)) {
			LOG.debug(format("Restauring game state from %s...", gameStateFilename));
			try {
				GameState gameState = readGameState(gameStateFilename);
				tileIndexesFromPreviousGame.addAll(gameState.getDrawnTileIndexes());
			} catch (IOException e) {
				throw new BeanCreationException(e.getMessage(), e);
			}
		} else {
			LOG.warn(format("No game state found in %s", gameStateFilename));
		}

	}

	private boolean isValidGameSeed(File file) {
		return file.exists() && file.isFile() && file.canRead();
	}

	private void writeGameState(File gameStateFilename, GameState gameState) throws IOException {
		File parent = gameStateFilename.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}
		new ObjectMapper().enableDefaultTyping(DefaultTyping.NON_CONCRETE_AND_ARRAYS)
				.enable(SerializationFeature.INDENT_OUTPUT).writeValue(gameStateFilename, gameState);
	}

	private GameState readGameState(File gameStateFilename) throws IOException {
		return new ObjectMapper().enableDefaultTyping(DefaultTyping.NON_CONCRETE_AND_ARRAYS)
				.readValue(gameStateFilename, GameState.class);
	}

}
