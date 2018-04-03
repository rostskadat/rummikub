package com.afb.ml.rummikub.services.strategy;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

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
		Utils.addTileGroup(helper,rack, 6, TileColor.values());

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

		TileGroup expected = Utils.addTileGroup(helper,rack, 5, TileColor.values());

		assertThat(helper.getHighestTileSet(rack), equalTo(expected));
	}

	@Test
	public void testGetHighestTileSet_04() {
		Rack rack = new Rack();

		TileRun expected = Utils.addTileRun(helper, rack, 6, 9, TileColor.BLACK);
		Utils.addTileRun(helper, rack, 4, 6, TileColor.RED);
		Utils.addTileGroup(helper,rack, 6, TileColor.values());

		assertThat(helper.getHighestTileSet(rack), equalTo(expected));
	}

	@Test
	public void testCanAddTileGroup_01() {
		TileGroup group = new TileGroup();
		group.add(new Tile(3, TileColor.BLACK));
		assertThat(helper.canAddToSet(group, new Tile(3, TileColor.BLUE)), equalTo(true));
	}

	@Test
	public void testCanAddTileGroup_02() {
		TileGroup group = new TileGroup();
		group.add(new Tile(3, TileColor.BLACK));
		group.add(new Tile(3, TileColor.BLUE));
		group.add(new Tile(3, TileColor.ORANGE));
		assertThat(helper.canAddToSet(group, new Tile(3, TileColor.RED)), equalTo(true));
	}

	@Test
	public void testCanAddTileGroup_03() {
		TileGroup group = new TileGroup();
		group.add(new Tile(3, TileColor.BLACK));
		group.add(new Tile(3, TileColor.BLUE));
		group.add(new Tile(3, TileColor.ORANGE));
		group.add(new Tile(3, TileColor.RED));
		assertThat(helper.canAddToSet(group, new Tile(3, TileColor.RED)), equalTo(false));
	}

	@Test
	public void testCanAddTileGroup_04() {
		TileGroup group = new TileGroup();
		group.add(new Tile(3, TileColor.BLACK));
		group.add(new Tile(3, TileColor.BLUE));
		group.add(new Tile(3, TileColor.ORANGE));
		assertThat(helper.canAddToSet(group, new Tile(4, TileColor.RED)), equalTo(false));
	}

	@Test
	public void testCanAddTileGroup_05() {
		TileGroup group = new TileGroup();
		group.add(new Tile(3, TileColor.BLACK));
		group.add(new Tile(3, TileColor.BLUE));
		group.add(new Tile(3, TileColor.ORANGE));
		assertThat(helper.canAddToSet(group, new Tile(TileColor.RED)), equalTo(true));
	}

	@Test
	public void testCanAddTileGroup_06() {
		TileGroup group = new TileGroup();
		group.add(new Tile(3, TileColor.BLACK));
		group.add(new Tile(3, TileColor.BLUE));
		group.add(new Tile(3, TileColor.ORANGE));
		group.add(new Tile(3, TileColor.RED));
		assertThat(helper.canAddToSet(group, new Tile(TileColor.RED)), equalTo(false));
	}
	
	@Test
	public void testCanAddTileRun_00() {
		TileRun run = new TileRun();
		assertThat(helper.canAddToSet(run, new Tile(1, TileColor.BLACK)), equalTo(true));
	}

	@Test
	public void testCanAddTileRun_01() {
		TileRun run = new TileRun();
		run.add(new Tile(1, TileColor.BLACK));
		run.add(new Tile(2, TileColor.BLACK));
		assertThat(helper.canAddToSet(run, new Tile(3, TileColor.BLACK)), equalTo(true));
	}

	@Test
	public void testCanAddTileRun_02() {
		TileRun run = new TileRun();
		run.add(new Tile(2, TileColor.BLACK));
		run.add(new Tile(3, TileColor.BLACK));
		assertThat(helper.canAddToSet(run, new Tile(1, TileColor.BLACK)), equalTo(true));
	}

	@Test
	public void testCanAddTileRun_03() {
		TileRun run = new TileRun();
		run.add(new Tile(1, TileColor.BLACK));
		run.add(new Tile(2, TileColor.BLACK));
		assertThat(helper.canAddToSet(run, new Tile(4, TileColor.BLACK)), equalTo(false));
	}

	@Test
	public void testCanAddTileRun_04() {
		TileRun run = new TileRun();
		run.add(new Tile(3, TileColor.BLACK));
		run.add(new Tile(4, TileColor.BLACK));
		assertThat(helper.canAddToSet(run, new Tile(1, TileColor.BLACK)), equalTo(false));
	}

	@Test
	public void testCanAddTileRun_05() {
		TileRun run = new TileRun();
		run.add(new Tile(1, TileColor.BLACK));
		run.add(new Tile(2, TileColor.BLACK));
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

}
