package com.afb.ml.rummikub.services.strategy;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.afb.ml.rummikub.AbstractUnitTest;
import com.afb.ml.rummikub.model.Rack;
import com.afb.ml.rummikub.model.Tile;
import com.afb.ml.rummikub.model.TileColor;
import com.afb.ml.rummikub.model.TileGroup;
import com.afb.ml.rummikub.model.TileRun;
import com.afb.ml.rummikub.model.TileSet;
import com.afb.ml.rummikub.services.strategy.StrategyHelper;

public class StrategyUtilsTest extends AbstractUnitTest {

    @Autowired
    private StrategyHelper helper;

    @Test
    public void testGetInitialTileSets_01() {
        Rack rack = new Rack();

        addTileRun(rack, 1, 3, TileColor.BLACK);

        List<TileSet> initialTileSets = helper.getInitialTileSets(rack);
        assertThat(initialTileSets.size(), equalTo(0));
    }

    @Test
    public void testGetInitialTileSets_02() {
        Rack rack = new Rack();

        TileRun expected = addTileRun(rack, 6, 9, TileColor.BLACK);
        addTileRun(rack, 4, 6, TileColor.RED);
        addTileGroup(rack, 6, TileColor.values());

        List<TileSet> initialTileSets = helper.getInitialTileSets(rack);
        assertThat(initialTileSets.size(), equalTo(1));
        assertThat(initialTileSets.get(0), equalTo(expected));
    }

	@Test
    public void testGetHighestTileSet_01() {
        Rack rack = new Rack();

        TileRun expected = addTileRun(rack, 1, 3, TileColor.BLACK);

        assertThat(helper.getHighestTileSet(rack), equalTo(expected));
	}

    @Test
    public void testGetHighestTileSet_02() {
        Rack rack = new Rack();

        addTileRun(rack, 1, 3, TileColor.BLACK);
        TileRun expected = addTileRun(rack, 3, 5, TileColor.RED);

        assertThat(helper.getHighestTileSet(rack), equalTo(expected));
    }

    @Test
    public void testGetHighestTileSet_03() {
        Rack rack = new Rack();

        addTileRun(rack, 1, 3, TileColor.BLACK);
        addTileRun(rack, 4, 6, TileColor.RED);

        TileGroup expected = addTileGroup(rack, 5, TileColor.values());

        assertThat(helper.getHighestTileSet(rack), equalTo(expected));
    }

    @Test
    public void testGetHighestTileSet_04() {
        Rack rack = new Rack();

        TileRun expected = addTileRun(rack, 6, 9, TileColor.BLACK);
        addTileRun(rack, 4, 6, TileColor.RED);
        addTileGroup(rack, 6, TileColor.values());

        assertThat(helper.getHighestTileSet(rack), equalTo(expected));
    }

    public static TileRun addTileRun(Rack rack, int from, int to, TileColor color) {
        TileRun run = new TileRun();
        for (int i = to; i >= from; i--) {
            run.addToSet(new Tile(i, color));
        }
        rack.addAll(run);
        return run;
    }

    public static TileGroup addTileGroup(Rack rack, int number, TileColor... colors) {
        TileGroup group = new TileGroup();
        // Make sure that the order is not important
        List<TileColor> list = Arrays.asList(TileColor.values());
        Collections.reverse(list);
        Collections.rotate(list, 2);
        list.forEach(color -> {
            group.addToSet(new Tile(number, color));
        });
        rack.addAll(group);
        return group;
    }

}
