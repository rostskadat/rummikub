package com.afb.ml.rummikub.services.strategy;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;

import com.afb.ml.rummikub.AbstractUnitTest;
import com.afb.ml.rummikub.model.Player;
import com.afb.ml.rummikub.model.Rack;
import com.afb.ml.rummikub.model.TileColor;
import com.afb.ml.rummikub.model.TileRun;
import com.afb.ml.rummikub.model.TileSet;
import com.afb.ml.rummikub.services.TableController;

@DirtiesContext
public class RandomStrategyTest extends AbstractUnitTest {

    private static final boolean PLAYED = true;
    private static final boolean STARTED = true;

    @Autowired
    IStrategy strategy;

    @Autowired
    TableController tableController;

	@Autowired
	private StrategyHelper helper;
	
    @Before
    public void before() {
        tableController.clearTable();
    }

    @Test
    public void testGetInitialTileSets_01() {
        Player player = new Player();
        Rack rack = player.getRack();
        checkPlayer(player, !STARTED, !PLAYED);
        Utils.addTileRun(helper, rack, 1, 3, TileColor.BLACK);
        checkPlayer(player, !STARTED, !PLAYED);
        Utils.addTileRun(helper, rack, 5, 7, TileColor.BLACK);
        checkPlayer(player, !STARTED, !PLAYED);
    }

    @Test
    public void testGetInitialTileSets_02() {
        Player player = new Player();
        Rack rack = player.getRack();
        checkPlayer(player, !STARTED, !PLAYED);
        Utils.addTileRun(helper, rack, 1, 3, TileColor.BLACK);
        checkPlayer(player, !STARTED, !PLAYED);
        Utils.addTileRun(helper, rack, 5, 7, TileColor.BLACK);
        checkPlayer(player, !STARTED, !PLAYED);
        TileRun run = Utils.addTileRun(helper, rack, 10, 12, TileColor.BLACK);
        checkPlayer(player, STARTED, PLAYED, run);
    }

    @Test
    public void testGetInitialTileSets_03() {
        Player player = new Player();
        Rack rack = player.getRack();
        checkPlayer(player, !STARTED, !PLAYED);
        TileRun run1 = Utils.addTileRun(helper, rack, 1, 3, TileColor.BLACK);
        checkPlayer(player, !STARTED, !PLAYED);
        TileRun run2 = Utils.addTileRun(helper, rack, 5, 7, TileColor.BLACK);
        checkPlayer(player, !STARTED, !PLAYED);
        TileRun run3 = Utils.addTileRun(helper, rack, 10, 12, TileColor.BLACK);
        checkPlayer(player, STARTED, PLAYED, run3);
        checkPlayer(player, STARTED, PLAYED, run1, run2, run3);
        TileRun run5 = Utils.addTileRun(helper, rack, 10, 11, TileColor.RED);
        checkPlayer(player, STARTED, !PLAYED, run1, run2, run3);
        assertThat(rack.containsAll(run5), equalTo(true));
        assertThat(rack.size(), equalTo(run5.size() + 1));
        TileRun run6 = Utils.addTileRun(helper, rack, 12, 12, TileColor.RED);
        run5.addAll(run6);
        checkPlayer(player, STARTED, PLAYED, run1, run2, run3, run5);
    }
    
    @Test
    public void testGetInitialTileSets_04() {
        Player player = new Player();
        Rack rack = player.getRack();
        TileRun run1 = Utils.addTileRun(helper, rack, 10, 12, TileColor.BLACK);
        checkPlayer(player, STARTED, PLAYED, run1);
        // Checking the shift run
        TileRun run2 = Utils.addTileRun(helper, rack, 13, 13, TileColor.BLACK);
        run1.addAll(run2);
        checkPlayer(player, STARTED, PLAYED, run1);
        TileRun run3 = Utils.addTileRun(helper, rack, 1, 5, TileColor.RED);
        checkPlayer(player, STARTED, PLAYED, run1, run3);
        // Checking the shift run
        Utils.addTileRun(helper, rack, 3, 3, TileColor.RED);
        TileRun expected1 = Utils.getTileRun(helper, 1, 3, TileColor.RED);
        TileRun expected2 = Utils.getTileRun(helper, 3, 5, TileColor.RED);
        checkPlayer(player, STARTED, PLAYED, run1, expected1, expected2);
    }    

    private void checkPlayer(Player player, boolean started, boolean played) {
        boolean hasPlayed = strategy.play(player);
        assertThat(hasPlayed, equalTo(played));
        assertThat(player.isStarted(), equalTo(started));
    }

    private void checkPlayer(Player player, boolean started, boolean played, TileSet... expectedSets) {
        checkPlayer(player, started, played);
        List<TileSet> tileSets = tableController.getTileSets();
        assertThat(tileSets, notNullValue());
        assertThat(tileSets.size(), equalTo(expectedSets.length));
        for (TileSet expectedSet : expectedSets) {
            assertThat(tileSets.contains(expectedSet), equalTo(true));
        }
    }

}
