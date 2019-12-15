package net.pictulog.ml.rummikub.service.strategy;

import static net.pictulog.ml.rummikub.service.strategy.StrategyHelper.canAddToSet;
import static net.pictulog.ml.rummikub.service.strategy.StrategyHelper.getHighestTileSet;
import static net.pictulog.ml.rummikub.service.strategy.StrategyHelper.getInitialTileSets;
import static net.pictulog.ml.rummikub.service.strategy.StrategyHelper.getRunAndGroupMoves;
import static net.pictulog.ml.rummikub.service.strategy.StrategyHelper.getTileRunMove;
import static net.pictulog.ml.rummikub.service.strategy.StrategyHelper.shiftRun;
import static net.pictulog.ml.rummikub.service.strategy.StrategyHelper.splitRun;
import static net.pictulog.ml.rummikub.service.strategy.StrategyHelper.substituteInGroup;
import static net.pictulog.ml.rummikub.service.strategy.TileSetUtils.addTileGroup;
import static net.pictulog.ml.rummikub.service.strategy.TileSetUtils.addTileRun;
import static net.pictulog.ml.rummikub.service.strategy.TileSetUtils.getTileGroup;
import static net.pictulog.ml.rummikub.service.strategy.TileSetUtils.getTileRun;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import net.pictulog.ml.rummikub.AbstractUnitTest;
import net.pictulog.ml.rummikub.model.Moves;
import net.pictulog.ml.rummikub.model.Move;
import net.pictulog.ml.rummikub.model.Rack;
import net.pictulog.ml.rummikub.model.Tile;
import net.pictulog.ml.rummikub.model.TileColor;
import net.pictulog.ml.rummikub.model.TileGroup;
import net.pictulog.ml.rummikub.model.TileRun;
import net.pictulog.ml.rummikub.model.TileSet;

public class StrategyHelperTest extends AbstractUnitTest {

    @Value("${initialScoreThreshold:30}")
    private int initialScoreThreshold;

    @Test
    public void testGetInitialTileSets_01() {
        Rack rack = new Rack();

        addTileRun(rack, 1, 3, TileColor.BLACK);

        List<TileSet> initialTileSets = getInitialTileSets(rack, initialScoreThreshold);
        assertThat(initialTileSets.size(), equalTo(0));
    }

    @Test
    public void testGetInitialTileSets_02() {
        Rack rack = new Rack();

        TileRun expected = addTileRun(rack, 6, 9, TileColor.BLACK);
        addTileRun(rack, 4, 6, TileColor.RED);
        addTileGroup(rack, 6, TileColor.values());

        List<TileSet> initialTileSets = getInitialTileSets(rack, initialScoreThreshold);
        assertThat(initialTileSets.size(), equalTo(1));
        assertThat(initialTileSets.get(0), equalTo(expected));
    }

    @Test
    public void testGetHighestTileSet_01() {
        Rack rack = new Rack();

        TileRun expected = addTileRun(rack, 1, 3, TileColor.BLACK);

        assertThat(getHighestTileSet(rack), equalTo(expected));
    }

    @Test
    public void testGetHighestTileSet_02() {
        Rack rack = new Rack();

        addTileRun(rack, 1, 3, TileColor.BLACK);
        TileRun expected = addTileRun(rack, 3, 5, TileColor.RED);

        assertThat(getHighestTileSet(rack), equalTo(expected));
    }

    @Test
    public void testGetHighestTileSet_03() {
        Rack rack = new Rack();

        addTileRun(rack, 1, 3, TileColor.BLACK);
        addTileRun(rack, 4, 6, TileColor.RED);

        TileGroup expected = addTileGroup(rack, 5, TileColor.values());

        assertThat(getHighestTileSet(rack), equalTo(expected));
    }

    @Test
    public void testGetHighestTileSet_04() {
        Rack rack = new Rack();

        TileRun expected = addTileRun(rack, 6, 9, TileColor.BLACK);
        addTileRun(rack, 4, 6, TileColor.RED);
        addTileGroup(rack, 6, TileColor.values());

        assertThat(getHighestTileSet(rack), equalTo(expected));
    }

    @Test
    public void testCanAddTileGroup_01() {
        TileGroup group = getTileGroup(3, TileColor.BLACK);
        assertThat(canAddToSet(group, new Tile(3, TileColor.BLUE)), equalTo(true));
    }

    @Test
    public void testCanAddTileGroup_02() {
        TileGroup group = getTileGroup(3, TileColor.BLACK, TileColor.BLUE, TileColor.ORANGE);
        assertThat(canAddToSet(group, new Tile(3, TileColor.RED)), equalTo(true));
    }

    @Test
    public void testCanAddTileGroup_03() {
        TileGroup group = getTileGroup(3, TileColor.BLACK, TileColor.BLUE, TileColor.ORANGE,
                TileColor.RED);
        assertThat(canAddToSet(group, new Tile(3, TileColor.RED)), equalTo(false));
    }

    @Test
    public void testCanAddTileGroup_04() {
        TileGroup group = getTileGroup(3, TileColor.BLACK, TileColor.BLUE, TileColor.ORANGE);
        assertThat(canAddToSet(group, new Tile(4, TileColor.RED)), equalTo(false));
    }

    @Test
    public void testCanAddTileGroup_05() {
        TileGroup group = getTileGroup(3, TileColor.BLACK, TileColor.BLUE, TileColor.ORANGE);
        assertThat(canAddToSet(group, new Tile(TileColor.RED)), equalTo(true));
    }

    @Test
    public void testCanAddTileGroup_06() {
        TileGroup group = getTileGroup(3, TileColor.BLACK, TileColor.BLUE, TileColor.ORANGE,
                TileColor.RED);
        assertThat(canAddToSet(group, new Tile(TileColor.RED)), equalTo(false));
    }

    @Test
    public void testCanAddTileRun_00() {
        TileRun run = new TileRun();
        assertThat(canAddToSet(run, new Tile(1, TileColor.BLACK)), equalTo(true));
    }

    @Test
    public void testCanAddTileRun_01() {
        TileRun run = getTileRun(1, 2, TileColor.BLACK);
        assertThat(canAddToSet(run, new Tile(3, TileColor.BLACK)), equalTo(true));
    }

    @Test
    public void testCanAddTileRun_02() {
        TileRun run = getTileRun(2, 3, TileColor.BLACK);
        assertThat(canAddToSet(run, new Tile(1, TileColor.BLACK)), equalTo(true));
    }

    @Test
    public void testCanAddTileRun_03() {
        TileRun run = getTileRun(1, 2, TileColor.BLACK);
        assertThat(canAddToSet(run, new Tile(4, TileColor.BLACK)), equalTo(false));
    }

    @Test
    public void testCanAddTileRun_04() {
        TileRun run = getTileRun(3, 4, TileColor.BLACK);
        assertThat(canAddToSet(run, new Tile(1, TileColor.BLACK)), equalTo(false));
    }

    @Test
    public void testCanAddTileRun_05() {
        TileRun run = getTileRun(1, 2, TileColor.BLACK);
        assertThat(canAddToSet(run, new Tile(TileColor.BLACK)), equalTo(true));
    }

    @Test
    public void testCanAddTileRun_06() {
        TileRun run = new TileRun();
        run.add(new Tile(TileColor.BLACK));
        run.add(new Tile(2, TileColor.BLACK));
        assertThat(canAddToSet(run, new Tile(3, TileColor.BLACK)), equalTo(true));
    }

    @Test
    public void testCanAddTileRun_07() {
        TileRun run = new TileRun();
        run.add(new Tile(1, TileColor.BLACK));
        run.add(new Tile(TileColor.BLACK));
        assertThat(canAddToSet(run, new Tile(3, TileColor.BLACK)), equalTo(true));
    }

    @Test
    public void testCanAddTileRun_08() {
        TileRun run = new TileRun();
        run.add(new Tile(1, TileColor.BLACK));
        run.add(new Tile(TileColor.BLACK));
        assertThat(canAddToSet(run, new Tile(TileColor.BLACK)), equalTo(false));
    }

    @Test
    public void testShiftRun_01() {
        TileRun run = getTileRun(2, 4, TileColor.BLACK);
        TileRun expected = getTileRun(1, 4, TileColor.BLACK);
        assertThat(shiftRun(run, new Tile(1, TileColor.BLACK), 0), equalTo(expected));
    }

    @Test
    public void testShiftRun_02() {
        TileRun run = getTileRun(2, 4, TileColor.BLACK);
        TileRun expected = getTileRun(2, 5, TileColor.BLACK);
        assertThat(shiftRun(run, new Tile(5, TileColor.BLACK), 3), equalTo(expected));
    }

    @Test
    public void testShiftRun_03() {
        TileRun run = getTileRun(2, 4, TileColor.BLACK);
        TileRun expected = getTileRun(2, 4, TileColor.BLACK);
        expected.add(0, new Tile(TileColor.BLACK));
        // XXX How does that work with a jocker in between
        assertThat(shiftRun(run, new Tile(TileColor.BLACK), 0), equalTo(expected));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShiftRun_04() {
        TileRun run = getTileRun(3, 5, TileColor.BLACK);
        shiftRun(run, new Tile(1, TileColor.BLACK), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShiftRun_05() {
        TileRun run = getTileRun(3, 5, TileColor.BLACK);
        shiftRun(run, new Tile(7, TileColor.BLACK), run.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testShiftRun_06() {
        TileRun run = getTileRun(3, 5, TileColor.BLACK);
        shiftRun(run, new Tile(4, TileColor.BLACK), run.size() - 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSplitRun_01() {
        TileRun run = getTileRun(2, 5, TileColor.BLACK);
        splitRun(run, new Tile(4, TileColor.BLACK), run.size() - 1);
    }

    @Test
    public void testSplitRun_02() {
        TileRun run = getTileRun(2, 6, TileColor.BLACK);
        TileRun expected1 = getTileRun(2, 4, TileColor.BLACK);
        TileRun expected2 = getTileRun(4, 6, TileColor.BLACK);
        List<TileRun> splits = splitRun(run, new Tile(4, TileColor.BLACK), 2);
        assertThat(splits.containsAll(Arrays.asList(new TileRun[] { expected1, expected2 })), equalTo(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSplitRun_03() {
        TileRun run = getTileRun(2, 7, TileColor.BLACK);
        splitRun(run, new Tile(2, TileColor.BLACK), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSplitRun_04() {
        TileRun run = getTileRun(2, 7, TileColor.BLACK);
        splitRun(run, new Tile(3, TileColor.BLACK), run.size() - 1);
    }

    @Test
    public void testSplitRun_05() {
        TileRun run = getTileRun(2, 7, TileColor.BLACK);
        TileRun expected1 = getTileRun(2, 4, TileColor.BLACK);
        TileRun expected2 = getTileRun(4, 7, TileColor.BLACK);
        List<TileRun> splits = splitRun(run, new Tile(4, TileColor.BLACK), 2);
        assertThat(splits.containsAll(Arrays.asList(new TileRun[] { expected1, expected2 })), equalTo(true));
    }

    @Test
    public void testSplitRun_06() {
        TileRun run = getTileRun(2, 7, TileColor.BLACK);
        TileRun expected1 = getTileRun(2, 5, TileColor.BLACK);
        TileRun expected2 = getTileRun(5, 7, TileColor.BLACK);
        List<TileRun> splits = splitRun(run, new Tile(5, TileColor.BLACK), 3);
        assertThat(splits.containsAll(Arrays.asList(new TileRun[] { expected1, expected2 })), equalTo(true));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSplitRun_07() {
        TileRun run = getTileRun(2, 7, TileColor.BLACK);
        splitRun(run, new Tile(6, TileColor.BLACK), run.size() - 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSplitRun_08() {
        TileRun run = getTileRun(2, 7, TileColor.BLACK);
        splitRun(run, new Tile(7, TileColor.BLACK), run.size() - 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSplitRun_09() {
        TileRun run = getTileRun(2, 7, TileColor.BLACK);
        splitRun(run, new Tile(8, TileColor.BLACK), run.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSplitRun_10() {
        TileRun run = getTileRun(2, 7, TileColor.BLACK);
        splitRun(run, new Tile(4, TileColor.RED), 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSplitRun_11() {
        TileRun run = getTileRun(2, 5, TileColor.BLACK);
        splitRun(run, new Tile(TileColor.BLACK), 2);
    }

    @Test
    public void testSplitRun_12() {
        TileRun run = getTileRun(2, 6, TileColor.BLACK);
        TileRun expected1 = getTileRun(2, 3, TileColor.BLACK);
        expected1.add(new Tile(TileColor.BLACK));
        TileRun expected2 = getTileRun(4, 6, TileColor.BLACK);
        List<TileRun> splits = splitRun(run, new Tile(TileColor.BLACK), 2);
        assertThat(splits.containsAll(Arrays.asList(new TileRun[] { expected1, expected2 })), equalTo(true));
    }

    @Test
    public void testSubstituteInGroup_01() {
        TileGroup group = getTileGroup(2, TileColor.BLACK);
        TileGroup expected = getTileGroup(2, TileColor.BLACK, TileColor.BLUE);
        assertThat(substituteInGroup(group, new Tile(2, TileColor.BLUE)), equalTo(expected));
    }

    @Test
    public void testSubstituteInGroup_02() {
        TileGroup group = getTileGroup(2, TileColor.BLACK, TileColor.ORANGE, TileColor.RED);
        TileGroup expected = getTileGroup(2, TileColor.BLACK, TileColor.ORANGE, TileColor.RED,
                TileColor.BLUE);
        assertThat(substituteInGroup(group, new Tile(2, TileColor.BLUE)), equalTo(expected));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSubstituteInGroup_03() {
        TileGroup group = getTileGroup(2, TileColor.BLACK, TileColor.ORANGE, TileColor.RED,
                TileColor.BLUE);
        substituteInGroup(group, new Tile(2, TileColor.BLUE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSubstituteInGroup_04() {
        TileGroup group = getTileGroup(2, TileColor.BLACK, TileColor.ORANGE, TileColor.RED);
        substituteInGroup(group, new Tile(1, TileColor.BLUE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSubstituteInGroup_05() {
        TileGroup group = getTileGroup(2, TileColor.BLACK, TileColor.ORANGE, TileColor.RED);
        substituteInGroup(group, new Tile(2, TileColor.BLACK));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSubstituteInGroup_06() {
        TileGroup group = getTileGroup(2, TileColor.BLACK, TileColor.ORANGE, TileColor.RED,
                TileColor.BLUE);
        substituteInGroup(group, new Tile(TileColor.BLUE));
    }

    @Test
    public void testSubstituteInGroup_07() {
        TileGroup group = getTileGroup(2, TileColor.BLACK, TileColor.ORANGE, TileColor.RED);
        TileGroup expected = getTileGroup(2, TileColor.BLACK, TileColor.ORANGE, TileColor.RED);
        expected.add(new Tile(TileColor.BLUE));
        assertThat(substituteInGroup(group, new Tile(TileColor.BLUE)), equalTo(expected));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSubstituteInGroup_08() {
        TileGroup group = getTileGroup(2, TileColor.BLACK, TileColor.ORANGE);
        group.add(new Tile(TileColor.BLUE));
        substituteInGroup(group, new Tile(TileColor.BLUE));
    }

    @Test
    public void testGetTileRunMove_01() {
        Move move = getMove(null, 1, 3, TileColor.BLACK);
        assertThat(move, notNullValue());
        assertThat(move.getFromTileSet(), nullValue());
        assertThat(move.getTiles(), notNullValue());
        assertThat(move.getTiles().size(), equalTo(3));
        assertThat(move.getTiles().get(0), equalTo(new Tile(1, TileColor.BLACK)));
        assertThat(move.getToTileSets(), notNullValue());
        assertThat(move.getToTileSets().size(), equalTo(1));
        assertThat(move.getToTileSets().get(0), equalTo(getTileRun(1, 3, TileColor.BLACK)));

    }

    @Test
    public void testGetTileRunMove_02() {
        Move move = getMove(getTileRun(1, 5, TileColor.BLACK), 6, 6, TileColor.BLACK);
        assertThat(move, notNullValue());
        assertThat(move.getFromTileSet(), equalTo(getTileRun(1, 5, TileColor.BLACK)));
        assertThat(move.getTiles(), notNullValue());
        assertThat(move.getTiles().size(), equalTo(1));
        assertThat(move.getTiles().get(0), equalTo(new Tile(6, TileColor.BLACK)));
        assertThat(move.getToTileSets(), notNullValue());
        assertThat(move.getToTileSets().size(), equalTo(1));
        assertThat(move.getToTileSets().get(0), equalTo(getTileRun(1, 6, TileColor.BLACK)));

    }

    @Test
    public void testGetTileRunMove_03() {
        Move move = getMove(getTileRun(1, 5, TileColor.BLACK), 3, 3, TileColor.BLACK);
        assertThat(move, notNullValue());
        assertThat(move.getFromTileSet(), equalTo(getTileRun(1, 5, TileColor.BLACK)));
        assertThat(move.getTiles(), notNullValue());
        assertThat(move.getTiles().size(), equalTo(1));
        assertThat(move.getTiles().get(0), equalTo(new Tile(3, TileColor.BLACK)));
        assertThat(move.getToTileSets(), notNullValue());
        assertThat(move.getToTileSets().size(), equalTo(2));
        assertThat(move.getToTileSets().get(0), equalTo(getTileRun(1, 3, TileColor.BLACK)));
        assertThat(move.getToTileSets().get(1), equalTo(getTileRun(3, 5, TileColor.BLACK)));
    }

    /**
     * This method returns a {@link Move} available from a given {@link TileRun}
     * 
     * @param fromTileRun
     * @param from the lower limit of the {@link TileRun} to create
     * @param to the upper limit of the {@link TileRun} to create
     * @param color the {@link TileColor} of the {@link TileRun} to create
     * @return the newly created {@link Move} 
     */
    private Move getMove(TileRun fromTileRun, int from, int to, TileColor color) {
        TileRun toTileRun = getTileRun(from, to, color);
        if (fromTileRun == null) {
            return getTileRunMove(toTileRun);
        }
        assert (from == to);
        return getTileRunMove(fromTileRun, toTileRun.get(0));
    }

    /*
     * Check the no Move cases
     */
    @Test
    public void testGetRunsAndGroups_01() {
        Rack rack = new Rack();
        addTileRun(rack, 3, 4, TileColor.BLACK);
        addTileRun(rack, 5, 5, TileColor.RED);

        List<Moves> movesList = getRunAndGroupMoves(rack);
        assertThat(movesList, notNullValue());
        assertThat(movesList.size(), equalTo(0));

        addTileRun(rack, 6, 6, TileColor.BLACK);
        movesList = getRunAndGroupMoves(rack);
        assertThat(movesList, notNullValue());
        assertThat(movesList.size(), equalTo(0));

        addTileRun(rack, 2, 2, TileColor.RED);
        movesList = getRunAndGroupMoves(rack);
        assertThat(movesList, notNullValue());
        assertThat(movesList.size(), equalTo(0));

        addTileRun(rack, 1, 1, TileColor.BLACK);
        movesList = getRunAndGroupMoves(rack);
        assertThat(movesList, notNullValue());
        assertThat(movesList.size(), equalTo(0));

        rack.clear();
        addTileGroup(rack, 2, TileColor.BLACK, TileColor.BLUE);
        addTileGroup(rack, 3, TileColor.ORANGE, TileColor.RED);
        movesList = getRunAndGroupMoves(rack);
        assertThat(movesList, notNullValue());
        assertThat(movesList.size(), equalTo(0));

        rack.clear();
        addTileGroup(rack, 2, TileColor.BLACK, TileColor.BLUE);
        addTileRun(rack, 1, 2, TileColor.BLACK);
        movesList = getRunAndGroupMoves(rack);
        assertThat(movesList, notNullValue());
        assertThat(movesList.size(), equalTo(0));
    }

    /*
     * Check the move case
     */
    @Test
    public void testGetRunsAndGroups_02() {
        Rack rack = new Rack();
        TileRun expectedRun = addTileRun(rack, 1, 3, TileColor.BLACK);
        rack.add(new Tile(4, TileColor.RED));
        rack.add(new Tile(6, TileColor.BLACK));

        List<Moves> movesList = getRunAndGroupMoves(rack);
        assertThat(movesList, notNullValue());
        assertThat(movesList.size(), equalTo(1));
        assertThat(movesList.get(0), notNullValue());
        assertThat(movesList.get(0), equalTo(getMoves(expectedRun)));

        addTileRun(rack, 4, 4, TileColor.BLACK);
        expectedRun.add(new Tile(4, TileColor.BLACK));
        movesList = getRunAndGroupMoves(rack);
        assertThat(movesList, notNullValue());
        assertThat(movesList.size(), equalTo(1));
        assertThat(movesList.get(0), notNullValue());
        assertThat(movesList.get(0), equalTo(getMoves(expectedRun)));

        addTileRun(rack, 4, 4, TileColor.BLUE);
        movesList = getRunAndGroupMoves(rack);
        assertThat(movesList, notNullValue());
        assertThat(movesList.size(), equalTo(2));
        assertThat(movesList.get(0), notNullValue());
        assertThat(movesList.get(0), equalTo(getMoves(expectedRun)));

        TileGroup expectedGroup = getTileGroup(4, TileColor.BLACK, TileColor.RED, TileColor.BLUE);
        assertThat(movesList.get(0), equalTo(getMoves(expectedRun)));
        assertThat(movesList.get(1), equalTo(getMoves(expectedGroup)));
    }
    
    private Moves getMoves(TileSet...expectedTileSets) {
        Moves expectedMoves = new Moves();
        for (TileSet expectedTileSet : expectedTileSets) {
            Move expectedMove = new Move();
            expectedMove.getTiles().addAll(expectedTileSet);
            expectedMove.getToTileSets().add(expectedTileSet);
            expectedMoves.add(expectedMove);
        }
        return expectedMoves;
    }

    
}
