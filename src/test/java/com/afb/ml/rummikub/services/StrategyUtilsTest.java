package com.afb.ml.rummikub.services;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import com.afb.ml.rummikub.model.Rack;
import com.afb.ml.rummikub.model.Tile;
import com.afb.ml.rummikub.model.TileColor;
import com.afb.ml.rummikub.model.TileRun;

@RunWith(SpringRunner.class)
public class StrategyUtilsTest {

	@Test
	public void testGetHighestTileSet() {
		TileRun run = new TileRun();
		run.add(new Tile(1, TileColor.BLACK));
		run.add(new Tile(2, TileColor.BLACK));
		run.add(new Tile(3, TileColor.BLACK));
		Rack rack = new Rack();
		rack.addAll(run);
		assertThat(StrategyUtils.getHighestTileSet(rack), equalTo(run));
	}

}
