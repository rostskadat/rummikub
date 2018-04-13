package net.pictulog.ml.rummikub.service.strategy;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import net.pictulog.ml.rummikub.AbstractUnitTest;
import net.pictulog.ml.rummikub.model.Player;
import net.pictulog.ml.rummikub.model.Rack;
import net.pictulog.ml.rummikub.model.Table;
import net.pictulog.ml.rummikub.model.TileColor;
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

    private StrategyHelper helper = new StrategyHelper();
	
    @Before
    public void before() {
        tableController.clearTable();
    }

    @Test
    public void testGetInitialTileSets_01() {
        Player player = new Player();
        Rack rack = player.getRack();
        playAndCheckStatus(player, !STARTED, !PLAYED);
        Utils.addTileRun(helper, rack, 1, 3, TileColor.BLACK);
        playAndCheckStatus(player, !STARTED, !PLAYED);
        Utils.addTileRun(helper, rack, 5, 7, TileColor.BLACK);
        playAndCheckStatus(player, !STARTED, !PLAYED);
        TileRun run = Utils.addTileRun(helper, rack, 10, 12, TileColor.BLACK);
        playAndCheckStatusAndTiles(player, STARTED, PLAYED, run);
    }

    @Test
    public void testGetInitialTileSets_02() {
        // Check that once started the player can plays the remaining tiles as whole sets
        Player player = new Player();
        Rack rack = player.getRack();
        playAndCheckStatus(player, !STARTED, !PLAYED);
        TileRun run1 = Utils.addTileRun(helper, rack, 1, 3, TileColor.BLACK);
        playAndCheckStatus(player, !STARTED, !PLAYED);
        TileRun run2 = Utils.addTileRun(helper, rack, 5, 7, TileColor.BLACK);
        playAndCheckStatus(player, !STARTED, !PLAYED);
        TileRun run3 = Utils.addTileRun(helper, rack, 10, 12, TileColor.BLACK);
        playAndCheckStatusAndTiles(player, STARTED, PLAYED, run3);
        playAndCheckStatusAndTiles(player, STARTED, PLAYED, run1, run2, run3);
        TileRun run5 = Utils.addTileRun(helper, rack, 10, 11, TileColor.RED);
        playAndCheckStatusAndTiles(player, STARTED, !PLAYED, run1, run2, run3);
        assertThat(rack.containsAll(run5), equalTo(true));
        assertThat(rack.size(), equalTo(run5.size()));
        TileRun run6 = Utils.addTileRun(helper, rack, 12, 12, TileColor.RED);
        run5.addAll(run6);
        playAndCheckStatusAndTiles(player, STARTED, PLAYED, run1, run2, run3, run5);
        assertThat(rack.size(), equalTo(0));
    }
    
    /*
     * Check different kind of moves, namely shift and then split.
     */
    @Test
    public void testGetInitialTileSets_03() {
        Player player = new Player();
        Rack rack = player.getRack();
        TileRun run1 = Utils.addTileRun(helper, rack, 10, 12, TileColor.BLACK);
        playAndCheckStatusAndTiles(player, STARTED, PLAYED, run1);
        // Checking the shift run
        TileRun run2 = Utils.addTileRun(helper, rack, 13, 13, TileColor.BLACK);
        run1.addAll(run2);
        playAndCheckStatusAndTiles(player, STARTED, PLAYED, run1);
        assertThat(rack.size(), equalTo(0));

        TileRun run3 = Utils.addTileRun(helper, rack, 1, 5, TileColor.RED);
        playAndCheckStatusAndTiles(player, STARTED, PLAYED, run1, run3);
        // Checking the split run
        Utils.addTileRun(helper, rack, 3, 3, TileColor.RED);
        TileRun expected1 = Utils.getTileRun(helper, 1, 3, TileColor.RED);
        TileRun expected2 = Utils.getTileRun(helper, 3, 5, TileColor.RED);
        playAndCheckStatusAndTiles(player, STARTED, PLAYED, run1, expected1, expected2);
        assertThat(rack.size(), equalTo(0));
        playAndCheckStatus(player, STARTED, !PLAYED);
    }    

    private void playAndCheckStatus(Player player, boolean started, boolean played) {
        boolean hasPlayed = strategy.play(player);
        assertThat(hasPlayed, equalTo(played));
        assertThat(player.isStarted(), equalTo(started));
    }

    private void playAndCheckStatusAndTiles(Player player, boolean started, boolean played, TileSet... expectedSets) {
        playAndCheckStatus(player, started, played);
        Table table = tableController.getTable();
        assertThat(table, notNullValue());
        assertThat(table.size(), equalTo(expectedSets.length));
        for (TileSet expectedSet : expectedSets) {
            assertThat(table.contains(expectedSet), equalTo(true));
        }
    }

}
