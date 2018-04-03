package com.afb.ml.rummikub;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.afb.ml.rummikub.model.TileGroupTest;
import com.afb.ml.rummikub.model.TileRunTest;
import com.afb.ml.rummikub.services.strategy.RandomStrategyTest;
import com.afb.ml.rummikub.services.strategy.StrategyUtilsTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        TileGroupTest.class,
        TileRunTest.class,
        RandomStrategyTest.class,
        StrategyUtilsTest.class
})
public class MainTestSuite {

}
