package net.pictulog.ml.rummikub.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;

import org.junit.Test;

import net.pictulog.ml.rummikub.AbstractUnitTest;

public class PlayerTest extends AbstractUnitTest {

    @Test
    public void testAddTileToRack() {
        Player player = new Player();
        player.addTileToRack(new Tile(1, TileColor.BLACK));
        player.addTileToRack(new Tile(2, TileColor.BLACK));
        player.addTileToRack(new Tile(3, TileColor.BLACK));
        player.addTileToRack(new Tile(4, TileColor.BLACK));
        Rack rack = player.getRack();
        assertThat(rack, notNullValue());
        assertThat(rack.size(), equalTo(4));
        for (int i = 1; i <= 4; i++) {
            assertThat(rack.contains(new Tile(i, TileColor.BLACK)), equalTo(true));
        }
        player.addTileToRack(new Tile(TileColor.BLACK));
        assertThat(rack.size(), equalTo(5));
        assertThat(rack.contains(new Tile(TileColor.BLACK)), equalTo(true));
        assertThat(rack.contains(new Tile(TileColor.RED)), equalTo(false));
        assertThat(rack.get(4), equalTo(new Tile(TileColor.BLACK)));
        assertThat(rack.indexOf(new Tile(TileColor.BLACK)), equalTo(4));

        player.addTileToRack(new Tile(TileColor.RED));
        assertThat(rack.size(), equalTo(6));
        assertThat(rack.contains(new Tile(TileColor.BLACK)), equalTo(true));
        assertThat(rack.contains(new Tile(TileColor.RED)), equalTo(true));
        assertThat(rack.get(5), equalTo(new Tile(TileColor.RED)));
        assertThat(rack.indexOf(new Tile(TileColor.RED)), equalTo(5));
    }

    @Test
    public void testRemoveTileFromRack() {
        Player player = new Player();
        player.addTileToRack(new Tile(1, TileColor.BLACK));
        player.addTileToRack(new Tile(2, TileColor.BLACK));
        player.addTileToRack(new Tile(3, TileColor.BLACK));
        player.addTileToRack(new Tile(4, TileColor.BLACK));
        Rack rack = player.getRack();
        assertThat(rack, notNullValue());
        assertThat(rack.size(), equalTo(4));
        for (int i = 1; i <= 4; i++) {
            assertThat(rack.contains(new Tile(i, TileColor.BLACK)), equalTo(true));
        }
        player.removeTileFromRack(new Tile(1, TileColor.BLACK));
        assertThat(rack.size(), equalTo(3));
        assertThat(rack.contains(new Tile(1, TileColor.BLACK)), equalTo(false));
        assertThat(rack.get(0), equalTo(new Tile(2, TileColor.BLACK)));
        assertThat(rack.indexOf(new Tile(2, TileColor.BLACK)), equalTo(0));

        player.addTileToRack(new Tile(TileColor.RED));
        assertThat(rack.size(), equalTo(4));
        assertThat(rack.contains(new Tile(TileColor.RED)), equalTo(true));
        assertThat(rack.get(3), equalTo(new Tile(TileColor.RED)));
        assertThat(rack.indexOf(new Tile(TileColor.RED)), equalTo(3));
        assertThat(rack.contains(new Tile(TileColor.BLACK)), equalTo(false));
    }

    @Test
    public void testRemoveAllTilesFromRack() {
        Player player = new Player();
        player.addTileToRack(new Tile(1, TileColor.BLACK));
        player.addTileToRack(new Tile(2, TileColor.BLACK));
        player.addTileToRack(new Tile(3, TileColor.BLACK));
        player.addTileToRack(new Tile(4, TileColor.BLACK));
        Rack rack = player.getRack();
        assertThat(rack, notNullValue());
        assertThat(rack.size(), equalTo(4));
        for (int i = 1; i <= 4; i++) {
            assertThat(rack.contains(new Tile(i, TileColor.BLACK)), equalTo(true));
        }
        player.removeAllTilesFromRack(Arrays.asList(new Tile(1, TileColor.BLACK)));
        assertThat(rack.size(), equalTo(3));
        assertThat(rack.contains(new Tile(1, TileColor.BLACK)), equalTo(false));
        assertThat(rack.get(0), equalTo(new Tile(2, TileColor.BLACK)));
        assertThat(rack.indexOf(new Tile(2, TileColor.BLACK)), equalTo(0));

        player.removeAllTilesFromRack(Arrays.asList(new Tile(TileColor.RED)));
        assertThat(rack.size(), equalTo(3));
        assertThat(rack.contains(new Tile(2, TileColor.BLACK)), equalTo(true));
        assertThat(rack.contains(new Tile(TileColor.RED)), equalTo(false));
        assertThat(rack.get(0), equalTo(new Tile(2, TileColor.BLACK)));
        assertThat(rack.indexOf(new Tile(2, TileColor.BLACK)), equalTo(0));
    }

    @Test
    public void testToString() {
        Player player = new Player();
        assertThat(player.toString(), notNullValue());
        player.setName("");
        assertThat(player.toString(), notNullValue());
        assertThat(player.isFinished(), equalTo(true));
    }

}
