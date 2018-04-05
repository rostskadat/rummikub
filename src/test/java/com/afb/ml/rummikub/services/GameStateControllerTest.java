package com.afb.ml.rummikub.services;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import com.afb.ml.rummikub.AbstractUnitTest;

/*
 * I make sure I load a property file that do set the 'useSavedGame' property to true
 */
@TestPropertySource(locations = "classpath:gamestate-rummikub.properties")
public class GameStateControllerTest extends AbstractUnitTest {

    @Autowired
    private GameStateController gameStateController;

    @Test
    public void testGetTilesFromPreviousGame() {
        List<Integer> indexes = gameStateController.getTilesFromPreviousGame();
        assertThat(indexes, notNullValue());
        assertThat(indexes.isEmpty(), equalTo(false));
    }
}
