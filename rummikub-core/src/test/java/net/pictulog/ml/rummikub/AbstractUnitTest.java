package net.pictulog.ml.rummikub;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:test-rummikub.properties")
@ContextConfiguration(classes = { Config.class })
@TestExecutionListeners(listeners = { DependencyInjectionTestExecutionListener.class })
public abstract class AbstractUnitTest {

}
