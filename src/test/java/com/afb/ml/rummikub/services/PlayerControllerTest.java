package com.afb.ml.rummikub.services;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.annotation.DirtiesContext;

import com.afb.ml.rummikub.AbstractUnitTest;
import com.afb.ml.rummikub.model.Player;

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
        players.remove(0);
        playerController.setPlayers(players);
        players = playerController.getPlayers();
        assertThat(players, notNullValue());
        assertThat(players.isEmpty(), equalTo(false));
        assertThat(players.size(), equalTo(numberOfPlayer - 1));
    }

    @Test
    @DirtiesContext
    public void testPlay() {
        playerController.play();
    }

}
