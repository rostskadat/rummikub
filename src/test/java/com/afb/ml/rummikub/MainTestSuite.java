package com.afb.ml.rummikub;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.afb.ml.rummikub.model.PlayerTest;
import com.afb.ml.rummikub.model.TileGroupTest;
import com.afb.ml.rummikub.model.TileRunTest;
import com.afb.ml.rummikub.services.strategy.RandomStrategyTest;
import com.afb.ml.rummikub.services.strategy.StrategyHelperTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        PlayerTest.class,
        TileGroupTest.class,
        TileRunTest.class,
        RandomStrategyTest.class,
        StrategyHelperTest.class
})
public class MainTestSuite {

}
