package com.afb.ml.rummikub.services.strategy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.afb.ml.rummikub.model.Rack;
import com.afb.ml.rummikub.model.Tile;
import com.afb.ml.rummikub.model.TileColor;
import com.afb.ml.rummikub.model.TileGroup;
import com.afb.ml.rummikub.model.TileRun;

public class Utils {

	private Utils() {

	}

	public static TileRun addTileRun(StrategyHelper helper, Rack rack, int from, int to, TileColor color) {
		TileRun run = getTileRun(helper,  from,  to,  color);
		rack.addAll(run);
		return run;
	}

	public static TileRun getTileRun(StrategyHelper helper, int from, int to, TileColor color) {
		TileRun run = new TileRun();
		for (int i = to; i >= from; i--) {
			helper.addToSet(run, new Tile(i, color));
		}
		return run;
	}

	public static TileGroup addTileGroup(StrategyHelper helper, Rack rack, int number, TileColor... colors) {
		TileGroup group = getTileGroup( helper,  number, colors);
		rack.addAll(group);
		return group;
	}

	public static TileGroup getTileGroup(StrategyHelper helper, int number, TileColor... colors) {
		TileGroup group = new TileGroup();
		// Make sure that the order is not important
        List<TileColor> list = Arrays.asList(colors);
		Collections.reverse(list);
		Collections.rotate(list, 2);
		list.forEach(color -> {
			helper.addToSet(group, new Tile(number, color));
		});
		return group;
	}
}
