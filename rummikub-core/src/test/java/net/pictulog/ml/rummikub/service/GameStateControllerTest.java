package net.pictulog.ml.rummikub.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import net.pictulog.ml.rummikub.AbstractUnitTest;
import net.pictulog.ml.rummikub.model.Tile;
import net.pictulog.ml.rummikub.model.TileColor;

/*
 * I make sure I load a property file that do set the 'useSavedGame' property to true
 */
@TestPropertySource(locations = "classpath:gamestate-rummikub.properties")
public class GameStateControllerTest extends AbstractUnitTest {

	@Autowired
	private GameStateController gameStateController;

	@Test
	public void testGameStateController() {
		// The controller must have restaured the game from the gameState.json
		// found in the resources folder... for which we actually do know the 
		// value.
		Tile tile = gameStateController.drawTile();
		assertThat(tile, equalTo(new Tile(5, TileColor.ORANGE)));
		tile = gameStateController.drawTile();
		assertThat(tile, equalTo(new Tile(8, TileColor.BLACK)));
	}

}
