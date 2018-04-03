package com.afb.ml.rummikub.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import com.afb.ml.rummikub.AbstractUnitTest;

public class TileGroupTest extends AbstractUnitTest {

	@Test
	public void testGetScoreNormal() {
		TileGroup group = new TileGroup();
		group.add(new Tile(3, TileColor.BLACK));
		group.add(new Tile(3, TileColor.BLUE));
		group.add(new Tile(3, TileColor.ORANGE));
		assertThat(group.getScore(), equalTo(9));
	}

	@Test
	public void testGetScoreJokerFirst() {
		TileGroup group = new TileGroup();
		group.add(new Tile(TileColor.BLACK));
		group.add(new Tile(3, TileColor.BLUE));
		group.add(new Tile(3, TileColor.ORANGE));
		assertThat(group.getScore(), equalTo(9));
	}

	@Test
	public void testGetScoreJokerMiddle() {
		TileGroup group = new TileGroup();
		group.add(new Tile(3, TileColor.BLACK));
		group.add(new Tile(TileColor.BLACK));
		group.add(new Tile(3, TileColor.ORANGE));
		assertThat(group.getScore(), equalTo(9));
	}

	@Test
	public void testGetScoreJokerLast() {
		TileGroup group = new TileGroup();
		group.add(new Tile(3, TileColor.BLACK));
		group.add(new Tile(3, TileColor.BLUE));
		group.add(new Tile(TileColor.BLACK));
		assertThat(group.getScore(), equalTo(9));
	}

	@Test
	public void testGetScore2JokersFirstLast() {
		TileGroup group = new TileGroup();
		group.add(new Tile(TileColor.BLACK));
		group.add(new Tile(3, TileColor.BLACK));
		group.add(new Tile(TileColor.RED));
		assertThat(group.getScore(), equalTo(9));
	}

	@Test
	public void testGetScore2JokersFirstMiddle() {
		TileGroup group = new TileGroup();
		group.add(new Tile(TileColor.BLACK));
		group.add(new Tile(TileColor.RED));
		group.add(new Tile(3, TileColor.BLACK));
		assertThat(group.getScore(), equalTo(9));
	}

	@Test
	public void testGetScore2JokersMiddleLast() {
		TileGroup group = new TileGroup();
		group.add(new Tile(3, TileColor.BLACK));
		group.add(new Tile(TileColor.RED));
		group.add(new Tile(TileColor.BLACK));
		assertThat(group.getScore(), equalTo(9));
	}

	@Test
	public void testCanAdd_01() {
		TileGroup group = new TileGroup();
		group.add(new Tile(3, TileColor.BLACK));
		assertThat(group.canAddToSet(new Tile(3, TileColor.BLUE)), equalTo(true));
	}

	@Test
	public void testCanAdd_02() {
		TileGroup group = new TileGroup();
		group.add(new Tile(3, TileColor.BLACK));
		group.add(new Tile(3, TileColor.BLUE));
		group.add(new Tile(3, TileColor.ORANGE));
		assertThat(group.canAddToSet(new Tile(3, TileColor.RED)), equalTo(true));
	}

	@Test
	public void testCanAdd_03() {
		TileGroup group = new TileGroup();
		group.add(new Tile(3, TileColor.BLACK));
		group.add(new Tile(3, TileColor.BLUE));
		group.add(new Tile(3, TileColor.ORANGE));
		group.add(new Tile(3, TileColor.RED));
		assertThat(group.canAddToSet(new Tile(3, TileColor.RED)), equalTo(false));
	}

	@Test
	public void testCanAdd_04() {
		TileGroup group = new TileGroup();
		group.add(new Tile(3, TileColor.BLACK));
		group.add(new Tile(3, TileColor.BLUE));
		group.add(new Tile(3, TileColor.ORANGE));
		assertThat(group.canAddToSet(new Tile(4, TileColor.RED)), equalTo(false));
	}

	@Test
	public void testCanAdd_05() {
		TileGroup group = new TileGroup();
		group.add(new Tile(3, TileColor.BLACK));
		group.add(new Tile(3, TileColor.BLUE));
		group.add(new Tile(3, TileColor.ORANGE));
		assertThat(group.canAddToSet(new Tile(TileColor.RED)), equalTo(true));
	}

	@Test
	public void testCanAdd_06() {
		TileGroup group = new TileGroup();
		group.add(new Tile(3, TileColor.BLACK));
		group.add(new Tile(3, TileColor.BLUE));
		group.add(new Tile(3, TileColor.ORANGE));
		group.add(new Tile(3, TileColor.RED));
		assertThat(group.canAddToSet(new Tile(TileColor.RED)), equalTo(false));
	}
}