package com.afb.ml.rummikub;

import static java.lang.String.format;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.afb.ml.rummikub.services.RummikubController;

/**
 * A simple implementation of <a href="https://en.wikipedia.org/wiki/Rummikub">Rummikub</a>
 * 
 * @author rostskadat
 *
 */
@SpringBootApplication
public class Rummikub implements ApplicationRunner {

    private static final Log LOG = LogFactory.getLog(Rummikub.class);

    @Autowired
    private RummikubController rummikubController;

    @Value("${useGameStateFilename}")
    private boolean useGameStateFilename;

    @Value("${gameStateFilename}")
    private File gameStateFilename;

    public static void main(String[] args) {
        SpringApplication.run(Rummikub.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (useGameStateFilename) {
            LOG.info(format("GameState dump file: %s", gameStateFilename.getAbsolutePath()));
        }

        rummikubController.play();
    }
}
