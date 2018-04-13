package net.pictulog.ml.rummikub;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import net.pictulog.ml.rummikub.model.PlayerTest;
import net.pictulog.ml.rummikub.model.TileGroupTest;
import net.pictulog.ml.rummikub.model.TileRunTest;
import net.pictulog.ml.rummikub.service.GameStateControllerTest;
import net.pictulog.ml.rummikub.service.PlayerControllerTest;
import net.pictulog.ml.rummikub.service.PoolControllerTest;
import net.pictulog.ml.rummikub.service.strategy.RandomStrategyTest;
import net.pictulog.ml.rummikub.service.strategy.StrategyHelperTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        PlayerTest.class,
        TileGroupTest.class,
        TileRunTest.class,
        RandomStrategyTest.class,
        StrategyHelperTest.class,
        GameStateControllerTest.class,
        PlayerControllerTest.class,
        PoolControllerTest.class
})
public class MainTestSuite {

}
