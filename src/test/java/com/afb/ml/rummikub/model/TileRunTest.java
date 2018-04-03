package com.afb.ml.rummikub.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import com.afb.ml.rummikub.AbstractUnitTest;

public class TileRunTest extends AbstractUnitTest {

	@Test
	public void testGetScoreNormal() {
		TileRun run = new TileRun();
		run.add(new Tile(1, TileColor.BLACK));
		run.add(new Tile(2, TileColor.BLACK));
		run.add(new Tile(3, TileColor.BLACK));
		assertThat(run.getScore(), equalTo(6));
	}

	@Test
	public void testGetScoreJokerFirst() {
		TileRun run = new TileRun();
		run.add(new Tile(TileColor.BLACK));
		run.add(new Tile(2, TileColor.BLACK));
		run.add(new Tile(3, TileColor.BLACK));
		assertThat(run.getScore(), equalTo(6));
	}

	@Test
	public void testGetScoreJokerMiddle() {
		TileRun run = new TileRun();
		run.add(new Tile(1, TileColor.BLACK));
		run.add(new Tile(TileColor.BLACK));
		run.add(new Tile(3, TileColor.BLACK));
		assertThat(run.getScore(), equalTo(6));
	}

	@Test
	public void testGetScoreJokerLast() {
		TileRun run = new TileRun();
		run.add(new Tile(1, TileColor.BLACK));
		run.add(new Tile(2, TileColor.BLACK));
		run.add(new Tile(TileColor.BLACK));
		assertThat(run.getScore(), equalTo(6));
	}

	@Test
	public void testGetScore2JokersFirstLast() {
		TileRun run = new TileRun();
		run.add(new Tile(TileColor.BLACK));
		run.add(new Tile(2, TileColor.BLACK));
		run.add(new Tile(TileColor.RED));
		assertThat(run.getScore(), equalTo(6));
	}

	@Test
	public void testGetScore2JokersFirstMiddle() {
		TileRun run = new TileRun();
		run.add(new Tile(TileColor.BLACK));
		run.add(new Tile(TileColor.RED));
		run.add(new Tile(3, TileColor.BLACK));
		assertThat(run.getScore(), equalTo(6));
	}

	@Test
	public void testGetScore2JokersMiddleLast() {
		TileRun run = new TileRun();
		run.add(new Tile(1, TileColor.BLACK));
		run.add(new Tile(TileColor.RED));
		run.add(new Tile(TileColor.BLACK));
		assertThat(run.getScore(), equalTo(6));
	}

	@Test
	public void testCanAdd_00() {
		TileRun run = new TileRun();
		assertThat(run.canAddToSet(new Tile(1, TileColor.BLACK)), equalTo(true));
	}

	@Test
	public void testCanAdd_01() {
		TileRun run = new TileRun();
		run.add(new Tile(1, TileColor.BLACK));
		run.add(new Tile(2, TileColor.BLACK));
		assertThat(run.canAddToSet(new Tile(3, TileColor.BLACK)), equalTo(true));
	}

	@Test
	public void testCanAdd_02() {
		TileRun run = new TileRun();
		run.add(new Tile(2, TileColor.BLACK));
		run.add(new Tile(3, TileColor.BLACK));
		assertThat(run.canAddToSet(new Tile(1, TileColor.BLACK)), equalTo(true));
	}

	@Test
	public void testCanAdd_03() {
		TileRun run = new TileRun();
		run.add(new Tile(1, TileColor.BLACK));
		run.add(new Tile(2, TileColor.BLACK));
		assertThat(run.canAddToSet(new Tile(4, TileColor.BLACK)), equalTo(false));
	}

	@Test
	public void testCanAdd_04() {
		TileRun run = new TileRun();
		run.add(new Tile(3, TileColor.BLACK));
		run.add(new Tile(4, TileColor.BLACK));
		assertThat(run.canAddToSet(new Tile(1, TileColor.BLACK)), equalTo(false));
	}

	@Test
	public void testCanAdd_05() {
		TileRun run = new TileRun();
		run.add(new Tile(1, TileColor.BLACK));
		run.add(new Tile(2, TileColor.BLACK));
		assertThat(run.canAddToSet(new Tile(TileColor.BLACK)), equalTo(true));
	}

    @Test
    public void testCanAdd_06() {
        TileRun run = new TileRun();
        run.add(new Tile(TileColor.BLACK));
        run.add(new Tile(2, TileColor.BLACK));
        assertThat(run.canAddToSet(new Tile(3, TileColor.BLACK)), equalTo(true));
    }

    @Test
    public void testCanAdd_07() {
        TileRun run = new TileRun();
        run.add(new Tile(1, TileColor.BLACK));
        run.add(new Tile(TileColor.BLACK));
        assertThat(run.canAddToSet(new Tile(3, TileColor.BLACK)), equalTo(true));
    }

    @Test
    public void testCanAdd_08() {
        TileRun run = new TileRun();
        run.add(new Tile(1, TileColor.BLACK));
        run.add(new Tile(TileColor.BLACK));
        assertThat(run.canAddToSet(new Tile(TileColor.BLACK)), equalTo(false));
    }

}