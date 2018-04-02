package com.afb.ml.rummikub.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class TileRunTest {

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
		assertThat(run.canAdd(new Tile(1, TileColor.BLACK)), equalTo(true));
	}

	@Test
	public void testCanAdd_01() {
		TileRun run = new TileRun();
		run.add(new Tile(1, TileColor.BLACK));
		run.add(new Tile(2, TileColor.BLACK));
		assertThat(run.canAdd(new Tile(3, TileColor.BLACK)), equalTo(true));
	}

	@Test
	public void testCanAdd_02() {
		TileRun run = new TileRun();
		run.add(new Tile(2, TileColor.BLACK));
		run.add(new Tile(3, TileColor.BLACK));
		assertThat(run.canAdd(new Tile(1, TileColor.BLACK)), equalTo(true));
	}

	@Test
	public void testCanAdd_03() {
		TileRun run = new TileRun();
		run.add(new Tile(1, TileColor.BLACK));
		run.add(new Tile(2, TileColor.BLACK));
		assertThat(run.canAdd(new Tile(4, TileColor.BLACK)), equalTo(false));
	}

	@Test
	public void testCanAdd_04() {
		TileRun run = new TileRun();
		run.add(new Tile(3, TileColor.BLACK));
		run.add(new Tile(4, TileColor.BLACK));
		assertThat(run.canAdd(new Tile(1, TileColor.BLACK)), equalTo(false));
	}

	@Test
	public void testCanAdd_05() {
		TileRun run = new TileRun();
		run.add(new Tile(1, TileColor.BLACK));
		run.add(new Tile(2, TileColor.BLACK));
		assertThat(run.canAdd(new Tile(TileColor.BLACK)), equalTo(true));
	}

}