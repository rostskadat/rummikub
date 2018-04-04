package com.afb.ml.rummikub.services.strategy;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.assertj.core.util.Arrays;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.afb.ml.rummikub.AbstractUnitTest;
import com.afb.ml.rummikub.model.Rack;
import com.afb.ml.rummikub.model.Tile;
import com.afb.ml.rummikub.model.TileColor;
import com.afb.ml.rummikub.model.TileGroup;
import com.afb.ml.rummikub.model.TileRun;
import com.afb.ml.rummikub.model.TileSet;

public class StrategyHelperTest extends AbstractUnitTest {

    @Autowired
    private StrategyHelper helper;

    @Test
    public void testGetInitialTileSets_01() {
        Rack rack = new Rack();

        Utils.addTileRun(helper, rack, 1, 3, TileColor.BLACK);

        List<TileSet> initialTileSets = helper.getInitialTileSets(rack);
        assertThat(initialTileSets.size(), equalTo(0));
    }

    @Test
    public void testGetInitialTileSets_02() {
        Rack rack = new Rack();

        TileRun expected = Utils.addTileRun(helper, rack, 6, 9, TileColor.BLACK);
        Utils.addTileRun(helper, rack, 4, 6, TileColor.RED);
        Utils.addTileGroup(helper, rack, 6, TileColor.values());

        List<TileSet> initialTileSets = helper.getInitialTileSets(rack);
        assertThat(initialTileSets.size(), equalTo(1));
        assertThat(initialTileSets.get(0), equalTo(expected));
    }

    @Test
    public void testGetHighestTileSet_01() {
        Rack rack = new Rack();

        TileRun expected = Utils.addTileRun(helper, rack, 1, 3, TileColor.BLACK);

        assertThat(helper.getHighestTileSet(rack), equalTo(expected));
    }

    @Test
    public void testGetHighestTileSet_02() {
        Rack rack = new Rack();

        Utils.addTileRun(helper, rack, 1, 3, TileColor.BLACK);
        TileRun expected = Utils.addTileRun(helper, rack, 3, 5, TileColor.RED);

        assertThat(helper.getHighestTileSet(rack), equalTo(expected));
    }

    @Test
    public void testGetHighestTileSet_03() {
        Rack rack = new Rack();

        Utils.addTileRun(helper, rack, 1, 3, TileColor.BLACK);
        Utils.addTileRun(helper, rack, 4, 6, TileColor.RED);

        TileGroup expected = Utils.addTileGroup(helper, rack, 5, TileColor.values());

        assertThat(helper.getHighestTileSet(rack), equalTo(expected));
    }

    @Test
    public void testGetHighestTileSet_04() {
        Rack rack = new Rack();

        TileRun expected = Utils.addTileRun(helper, rack, 6, 9, TileColor.BLACK);
        Utils.addTileRun(helper, rack, 4, 6, TileColor.RED);
        Utils.addTileGroup(helper, rack, 6, TileColor.values());

        assertThat(helper.getHighestTileSet(rack), equalTo(expected));
    }

    @Test
    public void testCanAddTileGroup_01() {
        TileGroup group = Utils.getTileGroup(helper, 3, TileColor.BLACK);
        assertThat(helper.canAddToSet(group, new Tile(3, TileColor.BLUE)), equalTo(true));
    }

    @Test
    public void testCanAddTileGroup_02() {
        TileGroup group = Utils.getTileGroup(helper, 3, TileColor.BLACK, TileColor.BLUE, TileColor.ORANGE);
        assertThat(helper.canAddToSet(group, new Tile(3, TileColor.RED)), equalTo(true));
    }

    @Test
    public void testCanAddTileGroup_03() {
        TileGroup group = Utils.getTileGroup(helper, 3, TileColor.BLACK, TileColor.BLUE, TileColor.ORANGE,
                TileColor.RED);
        assertThat(helper.canAddToSet(group, new Tile(3, TileColor.RED)), equalTo(false));
    }

    @Test
    public void testCanAddTileGroup_04() {
        TileGroup group = Utils.getTileGroup(helper, 3, TileColor.BLACK, TileColor.BLUE, TileColor.ORANGE);
        assertThat(helper.canAddToSet(group, new Tile(4, TileColor.RED)), equalTo(false));
    }

    @Test
    public void testCanAddTileGroup_05() {
        TileGroup group = Utils.getTileGroup(helper, 3, TileColor.BLACK, TileColor.BLUE, TileColor.ORANGE);
        assertThat(helper.canAddToSet(group, new Tile(TileColor.RED)), equalTo(true));
    }

    @Test
    public void testCanAddTileGroup_06() {
        TileGroup group = Utils.getTileGroup(helper, 3, TileColor.BLACK, TileColor.BLUE, TileColor.ORANGE,
                TileColor.RED);
        assertThat(helper.canAddToSet(group, new Tile(TileColor.RED)), equalTo(false));
    }

    @Test
    public void testCanAddTileRun_00() {
        TileRun run = new TileRun();
        assertThat(helper.canAddToSet(run, new Tile(1, TileColor.BLACK)), equalTo(true));
    }

    @Test
    public void testCanAddTileRun_01() {
        TileRun run = Utils.getTileRun(helper, 1, 2, TileColor.BLACK);
        assertThat(helper.canAddToSet(run, new Tile(3, TileColor.BLACK)), equalTo(true));
    }

    @Test
    public void testCanAddTileRun_02() {
        TileRun run = Utils.getTileRun(helper, 2, 3, TileColor.BLACK);
        assertThat(helper.canAddToSet(run, new Tile(1, TileColor.BLACK)), equalTo(true));
    }

    @Test
    public void testCanAddTileRun_03() {
        TileRun run = Utils.getTileRun(helper, 1, 2, TileColor.BLACK);
        assertThat(helper.canAddToSet(run, new Tile(4, TileColor.BLACK)), equalTo(false));
    }

    @Test
    public void testCanAddTileRun_04() {
        TileRun run = Utils.getTileRun(helper, 3, 4, TileColor.BLACK);
        assertThat(helper.canAddToSet(run, new Tile(1, TileColor.BLACK)), equalTo(false));
    }

    @Test
    public void testCanAddTileRun_05() {
        TileRun run = Utils.getTileRun(helper, 1, 2, TileColor.BLACK);
        assertThat(helper.canAddToSet(run, new Tile(TileColor.BLACK)), equalTo(true));
    }

    @Test
    public void testCanAddTileRun_06() {
        TileRun run = new TileRun();
        run.add(new Tile(TileColor.BLACK));
        run.add(new Tile(2, TileColor.BLACK));
        assertThat(helper.canAddToSet(run, new Tile(3, TileColor.BLACK)), equalTo(true));
    }

    @Test
    public void testCanAddTileRun_07() {
        TileRun run = new TileRun();
        run.add(new Tile(1, TileColor.BLACK));
        run.add(new Tile(TileColor.BLACK));
        assertThat(helper.canAddToSet(run, new Tile(3, TileColor.BLACK)), equalTo(true));
    }

    @Test
    public void testCanAddTileRun_08() {
        TileRun run = new TileRun();
        run.add(new Tile(1, TileColor.BLACK));
        run.add(new Tile(TileColor.BLACK));
        assertThat(helper.canAddToSet(run, new Tile(TileColor.BLACK)), equalTo(false));
    }

    @Test
    public void testShiftRun_01() {
        TileRun run = Utils.getTileRun(helper, 2, 4, TileColor.BLACK);
        TileRun expected = Utils.getTileRun(helper, 1, 4, TileColor.BLACK);
        assertThat(helper.shiftRun(run, new Tile(1, TileColor.BLACK)), equalTo(expected));
    }

    @Test
    public void testShiftRun_02() {
        TileRun run = Utils.getTileRun(helper, 2, 4, TileColor.BLACK);
        TileRun expected = Utils.getTileRun(helper, 2, 5, TileColor.BLACK);
        assertThat(helper.shiftRun(run, new Tile(5, TileColor.BLACK)), equalTo(expected));
    }

    @Test
    public void testShiftRun_03() {
        TileRun run = Utils.getTileRun(helper, 2, 4, TileColor.BLACK);
        TileRun expected = Utils.getTileRun(helper, 2, 4, TileColor.BLACK);
        expected.add(0, new Tile(TileColor.BLACK));
        // XXX How does that work with a jocker in between
        assertThat(helper.shiftRun(run, new Tile(TileColor.BLACK)), equalTo(expected));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShiftRun_04() {
        TileRun run = Utils.getTileRun(helper, 3, 5, TileColor.BLACK);
        helper.shiftRun(run, new Tile(1, TileColor.BLACK));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShiftRun_05() {
        TileRun run = Utils.getTileRun(helper, 3, 5, TileColor.BLACK);
        helper.shiftRun(run, new Tile(7, TileColor.BLACK));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShiftRun_06() {
        TileRun run = Utils.getTileRun(helper, 3, 5, TileColor.BLACK);
        helper.shiftRun(run, new Tile(4, TileColor.BLACK));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSplitRun_01() {
        TileRun run = Utils.getTileRun(helper, 2, 5, TileColor.BLACK);
        helper.splitRun(run, new Tile(4, TileColor.BLACK));
    }

    @Test
    public void testSplitRun_02() {
        TileRun run = Utils.getTileRun(helper, 2, 6, TileColor.BLACK);
        TileRun expected1 = Utils.getTileRun(helper, 2, 4, TileColor.BLACK);
        TileRun expected2 = Utils.getTileRun(helper, 4, 6, TileColor.BLACK);
        List<TileRun> splits = helper.splitRun(run, new Tile(4, TileColor.BLACK));
        assertThat(splits.containsAll(Arrays.asList(new TileRun[] { expected1, expected2 })), equalTo(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSplitRun_03() {
        TileRun run = Utils.getTileRun(helper, 2, 7, TileColor.BLACK);
        helper.splitRun(run, new Tile(2, TileColor.BLACK));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSplitRun_04() {
        TileRun run = Utils.getTileRun(helper, 2, 7, TileColor.BLACK);
        helper.splitRun(run, new Tile(3, TileColor.BLACK));
    }

    @Test
    public void testSplitRun_05() {
        TileRun run = Utils.getTileRun(helper, 2, 7, TileColor.BLACK);
        TileRun expected1 = Utils.getTileRun(helper, 2, 4, TileColor.BLACK);
        TileRun expected2 = Utils.getTileRun(helper, 4, 7, TileColor.BLACK);
        List<TileRun> splits = helper.splitRun(run, new Tile(4, TileColor.BLACK));
        assertThat(splits.containsAll(Arrays.asList(new TileRun[] { expected1, expected2 })), equalTo(true));
    }

    @Test
    public void testSplitRun_06() {
        TileRun run = Utils.getTileRun(helper, 2, 7, TileColor.BLACK);
        TileRun expected1 = Utils.getTileRun(helper, 2, 5, TileColor.BLACK);
        TileRun expected2 = Utils.getTileRun(helper, 5, 7, TileColor.BLACK);
        List<TileRun> splits = helper.splitRun(run, new Tile(5, TileColor.BLACK));
        assertThat(splits.containsAll(Arrays.asList(new TileRun[] { expected1, expected2 })), equalTo(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSplitRun_07() {
        TileRun run = Utils.getTileRun(helper, 2, 7, TileColor.BLACK);
        helper.splitRun(run, new Tile(6, TileColor.BLACK));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSplitRun_08() {
        TileRun run = Utils.getTileRun(helper, 2, 7, TileColor.BLACK);
        helper.splitRun(run, new Tile(7, TileColor.BLACK));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSplitRun_09() {
        TileRun run = Utils.getTileRun(helper, 2, 7, TileColor.BLACK);
        helper.splitRun(run, new Tile(8, TileColor.BLACK));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSplitRun_10() {
        TileRun run = Utils.getTileRun(helper, 2, 7, TileColor.BLACK);
        helper.splitRun(run, new Tile(4, TileColor.RED));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSplitRun_11() {
        TileRun run = Utils.getTileRun(helper, 2, 5, TileColor.BLACK);
        helper.splitRun(run, new Tile(TileColor.BLACK));
    }

    @Test
    public void testSplitRun_12() {
        TileRun run = Utils.getTileRun(helper, 2, 6, TileColor.BLACK);
        TileRun expected1 = Utils.getTileRun(helper, 2, 3, TileColor.BLACK);
        expected1.add(new Tile(TileColor.BLACK));
        TileRun expected2 = Utils.getTileRun(helper, 4, 6, TileColor.BLACK);
        List<TileRun> splits = helper.splitRun(run, new Tile(TileColor.BLACK));
        assertThat(splits.containsAll(Arrays.asList(new TileRun[] { expected1, expected2 })), equalTo(true));
    }

    @Test
    public void testSubstituteInGroup_01() {
        TileGroup group = Utils.getTileGroup(helper, 2, TileColor.BLACK);
        TileGroup expected = Utils.getTileGroup(helper, 2, TileColor.BLACK, TileColor.BLUE);
        assertThat(helper.substituteInGroup(group, new Tile(2, TileColor.BLUE)), equalTo(expected));
    }

    @Test
    public void testSubstituteInGroup_02() {
        TileGroup group = Utils.getTileGroup(helper, 2, TileColor.BLACK, TileColor.ORANGE, TileColor.RED);
        TileGroup expected = Utils.getTileGroup(helper, 2, TileColor.BLACK, TileColor.ORANGE, TileColor.RED,
                TileColor.BLUE);
        assertThat(helper.substituteInGroup(group, new Tile(2, TileColor.BLUE)), equalTo(expected));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSubstituteInGroup_03() {
        TileGroup group = Utils.getTileGroup(helper, 2, TileColor.BLACK, TileColor.ORANGE, TileColor.RED,
                TileColor.BLUE);
        helper.substituteInGroup(group, new Tile(2, TileColor.BLUE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSubstituteInGroup_04() {
        TileGroup group = Utils.getTileGroup(helper, 2, TileColor.BLACK, TileColor.ORANGE, TileColor.RED);
        helper.substituteInGroup(group, new Tile(1, TileColor.BLUE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSubstituteInGroup_05() {
        TileGroup group = Utils.getTileGroup(helper, 2, TileColor.BLACK, TileColor.ORANGE, TileColor.RED);
        helper.substituteInGroup(group, new Tile(2, TileColor.BLACK));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSubstituteInGroup_06() {
        TileGroup group = Utils.getTileGroup(helper, 2, TileColor.BLACK, TileColor.ORANGE, TileColor.RED,
                TileColor.BLUE);
        helper.substituteInGroup(group, new Tile(TileColor.BLUE));
    }

    @Test
    public void testSubstituteInGroup_07() {
        TileGroup group = Utils.getTileGroup(helper, 2, TileColor.BLACK, TileColor.ORANGE, TileColor.RED);
        TileGroup expected = Utils.getTileGroup(helper, 2, TileColor.BLACK, TileColor.ORANGE, TileColor.RED);
        expected.add(new Tile(TileColor.BLUE));
        assertThat(helper.substituteInGroup(group, new Tile(TileColor.BLUE)), equalTo(expected));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSubstituteInGroup_08() {
        TileGroup group = Utils.getTileGroup(helper, 2, TileColor.BLACK, TileColor.ORANGE);
        group.add(new Tile(TileColor.BLUE));
        helper.substituteInGroup(group, new Tile(TileColor.BLUE));
    }
}
