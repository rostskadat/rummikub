package net.pictulog.ml.rummikub.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.annotation.DirtiesContext;

import net.pictulog.ml.rummikub.AbstractUnitTest;
import net.pictulog.ml.rummikub.model.Player;

public class PlayerControllerTest extends AbstractUnitTest {

    @Value("${numberOfPlayer}")
    private int numberOfPlayer;

    @Autowired
    private PlayerController playerController;

    @Test
    public void testPlayers() {
        List<Player> players = playerController.getPlayers();
        assertThat(players, notNullValue());
        assertThat(players.isEmpty(), equalTo(false));
        assertThat(players.size(), equalTo(numberOfPlayer));
    }

    @Test
    @DirtiesContext
    public void testPlay() {
        playerController.play(1);
    }

}
