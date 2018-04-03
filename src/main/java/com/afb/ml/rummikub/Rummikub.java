package com.afb.ml.rummikub;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private RummikubController rummikubController;

    public static void main(String[] args) {
        SpringApplication.run(Rummikub.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        rummikubController.play();
    }
}
