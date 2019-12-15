package net.pictulog.ml.rummikub.service.strategy;

import static net.pictulog.ml.rummikub.service.strategy.StrategyHelper.addToSet;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.pictulog.ml.rummikub.model.Table;
import net.pictulog.ml.rummikub.model.Tile;
import net.pictulog.ml.rummikub.model.TileColor;
import net.pictulog.ml.rummikub.model.TileGroup;
import net.pictulog.ml.rummikub.model.TileRun;

public class TileSetUtils {

    private TileSetUtils() {
        // NA
    }

    public static TileRun addTileRun(Table table, int from, int to, TileColor color) {
        TileRun set = getTileRun(from, to, color);
        table.add(set);
        return set;
    }

    /**
     * Add a {@link TileRun} from {@code from} lower limit to {@code to} upper 
     * limit.
     * 
     * @param list the {@link Rack} to add the {@link Tile} to
     * @param from the lower limit of the {@link TileRun}
     * @param to the upper limit of the {@link TileRun}
     * @param color the {@link TileColor} of the {@link TileRun}
     * @return the newly created {@link TileRun}
     */
    public static TileRun addTileRun(List<Tile> list, int from, int to, TileColor color) {
        TileRun set = getTileRun(from, to, color);
        list.addAll(set);
        return set;
    }

    public static TileRun getTileRun(int from, int to, TileColor color) {
        assert (from <= to);
        TileRun set = new TileRun();
        for (int i = to; i >= from; i--) {
            addToSet(set, new Tile(i, color));
        }
        return set;
    }

    public static TileRun getTileRunWithJocker(int from, int to, TileColor color, Tile jocker, int jockerIndex) {
        assert (from <= to);
        TileRun set = new TileRun();
        for (int i = to; i >= from; i--) {
            addToSet(set, (i == jockerIndex) ? jocker : new Tile(i, color));
        }
        return set;
    }

    public static TileGroup addTileGroup(Table table, int number, TileColor... colors) {
        TileGroup set = getTileGroup(number, colors);
        table.add(set);
        return set;
    }

    public static TileGroup addTileGroup(List<Tile> list, int number, TileColor... colors) {
        TileGroup set = getTileGroup(number, colors);
        list.addAll(set);
        return set;
    }

    public static TileGroup getTileGroup(int number, TileColor... colors) {
        TileGroup set = new TileGroup();
        // Make sure that the order is not important
        List<TileColor> list = Arrays.asList(colors);
        Collections.shuffle(list);
        list.forEach(color -> {
            addToSet(set, new Tile(number, color));
        });
        return set;
    }

    public static TileGroup getTileGroupWithJocker(int number, Tile jocker, TileColor... colors) {
        TileGroup set = new TileGroup();
        set.add(jocker);
        // Make sure that the order is not important
        List<TileColor> list = Arrays.asList(colors);
        Collections.shuffle(list);
        list.forEach(color -> {
            addToSet(set, new Tile(number, color));
        });
        return set;
    }
}
