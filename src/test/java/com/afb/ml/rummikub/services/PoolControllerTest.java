package com.afb.ml.rummikub.services;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.afb.ml.rummikub.AbstractUnitTest;
import com.afb.ml.rummikub.model.Pool;

public class PoolControllerTest extends AbstractUnitTest {

    @Value("${numberOfTilesPerColor:13}")
    private int numberOfTilesPerColor;

    @Autowired
    private PoolController poolController;

    @Test
    public void testGetSize() {
        Pool pool = poolController.getPool();
        assertThat(pool, notNullValue());
        assertThat(pool.isEmpty(), equalTo(false));
        assertThat(poolController.getPoolSize(), equalTo(pool.size()));
        // 13 tiles * 2 * 4 colors + 2 jockers - 14 Tiles * 4 players
        assertThat(pool.size(), equalTo(numberOfTilesPerColor * 4 * 2 + 2 - 14 * 4));
    }
}
