package net.pictulog.ml.rummikub.service.strategy;

import static net.pictulog.ml.rummikub.model.TileColor.BLACK;
import static net.pictulog.ml.rummikub.model.TileColor.RED;
import static net.pictulog.ml.rummikub.service.strategy.TileSetUtils.addTileRun;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.pictulog.ml.rummikub.AbstractUnitTest;
import net.pictulog.ml.rummikub.model.Player;
import net.pictulog.ml.rummikub.model.Rack;
import net.pictulog.ml.rummikub.model.Table;
import net.pictulog.ml.rummikub.model.Tile;
import net.pictulog.ml.rummikub.model.TileRun;
import net.pictulog.ml.rummikub.model.TileSet;
import net.pictulog.ml.rummikub.service.PoolController;
import net.pictulog.ml.rummikub.service.TableController;

public class RandomStrategyTest extends AbstractUnitTest {

	private static final boolean PLAYED = true;
	private static final boolean STARTED = true;

	@Autowired
	IStrategy strategy;

	@Autowired
	TableController tableController;

	@Autowired
	PoolController poolController;

	@Before
	public void before() {
		tableController.clearTable();
	}

	@Test
	public void testGetInitialTileSets_01() {
		// Check that the player only starts after the initial round
		Player player = new Player();
		Rack rack = player.getRack();
		playAndCheckStatus(player, !STARTED, !PLAYED);
		addTileRun(rack, 1, 3, BLACK);
		playAndCheckStatus(player, !STARTED, !PLAYED);
		addTileRun(rack, 5, 7, BLACK);
		playAndCheckStatus(player, !STARTED, !PLAYED);
		TileRun run = addTileRun(rack, 10, 12, BLACK);
		playAndCheckStatusAndTiles(player, STARTED, PLAYED, run);
		assertThat(rack, notNullValue());
		assertThat(rack.size(), equalTo(6));
		assertTrue(rack.contains(new Tile(1, BLACK)));
		assertTrue(rack.contains(new Tile(2, BLACK)));
		assertTrue(rack.contains(new Tile(3, BLACK)));
		assertTrue(rack.contains(new Tile(5, BLACK)));
		assertTrue(rack.contains(new Tile(6, BLACK)));
		assertTrue(rack.contains(new Tile(7, BLACK)));
	}

	@Test
	public void testGetInitialTileSets_02() {
		// Check that once started the player can plays the remaining tiles as whole
		// sets
		Player player = new Player();
		Rack rack = player.getRack();
		playAndCheckStatus(player, !STARTED, !PLAYED);
		TileRun run1 = addTileRun(rack, 1, 3, BLACK);
		playAndCheckStatus(player, !STARTED, !PLAYED);
		TileRun run2 = addTileRun(rack, 5, 7, BLACK);
		playAndCheckStatus(player, !STARTED, !PLAYED);
		TileRun run3 = addTileRun(rack, 10, 12, BLACK);
		playAndCheckStatusAndTiles(player, STARTED, PLAYED, run3);
		playAndCheckStatusAndTiles(player, STARTED, PLAYED, run1, run2, run3);
		TileRun run5 = addTileRun(rack, 10, 11, RED);
		playAndCheckStatusAndTiles(player, STARTED, !PLAYED, run1, run2, run3);
		assertTrue(rack.containsAll(run5));
		assertThat(rack.size(), equalTo(run5.size()));
		TileRun run6 = addTileRun(rack, 12, 12, RED);
		run5.addAll(run6);
		playAndCheckStatusAndTiles(player, STARTED, PLAYED, run1, run2, run3, run5);
		assertTrue(rack.isEmpty());
	}

	/*
	 * Check different kind of moves, namely shift and then split.
	 */
	@Test
	public void testGetInitialTileSets_03() {
		Player player = new Player();
		Rack rack = player.getRack();
		TileRun run1 = addTileRun(rack, 10, 12, BLACK);
		playAndCheckStatusAndTiles(player, STARTED, PLAYED, run1);
		assertTrue(rack.isEmpty());
		// Checking the shift run
		TileRun run2 = addTileRun(rack, 13, 13, BLACK);
		run1.addAll(run2);
		playAndCheckStatusAndTiles(player, STARTED, PLAYED, run1);
		assertTrue(rack.isEmpty());

		TileRun run3 = addTileRun(rack, 1, 5, RED);
		playAndCheckStatusAndTiles(player, STARTED, PLAYED, run1, run3);
		assertTrue(rack.isEmpty());
		// Checking the split run
		addTileRun(rack, 3, 3, RED);
		TileRun expected1 = TileSetUtils.getTileRun(1, 3, RED);
		TileRun expected2 = TileSetUtils.getTileRun(3, 5, RED);
		playAndCheckStatusAndTiles(player, STARTED, PLAYED, run1, expected1, expected2);
		assertTrue(rack.isEmpty());
		playAndCheckStatus(player, STARTED, !PLAYED);
	}

	/**
	 * This method will run the {@code strategy} for the given {@link Player} and
	 * assert on its started and played status
	 * 
	 * @param player  the {@link Player} to play
	 * @param started whether the player should be in the started state or not
	 * @param played  whether the player could have played during that turn
	 */
	private void playAndCheckStatus(Player player, boolean started, boolean played) {
		boolean hasPlayed = strategy.play(player);
		assertThat(hasPlayed, equalTo(played));
		assertThat(player.isStarted(), equalTo(started));
	}

	/**
	 * @see RandomStrategyTest#playAndCheckStatus(Player, boolean, boolean). Further
	 *      more this method will check that the {@link Table} contains each of the
	 *      given {@code expectedSets}
	 * @param player       the {@link Player} to play
	 * @param started      whether the player should be in the started state or not
	 * @param played       whether the player could have played during that turn
	 * @param expectedSets the list of {@link TileSet} to be found on the
	 *                     {@link Table}
	 */
	private void playAndCheckStatusAndTiles(Player player, boolean started, boolean played, TileSet... expectedSets) {
		playAndCheckStatus(player, started, played);
		Table table = tableController.getTable();
		assertThat(table, notNullValue());
		assertThat(table.size(), equalTo(expectedSets.length));
		assertTrue(table.containsAll(Arrays.asList(expectedSets)));
	}

}
