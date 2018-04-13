package net.pictulog.ml.rummikub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import net.pictulog.ml.rummikub.service.PlayerController;

/**
 * A simple implementation of <a href="https://en.wikipedia.org/wiki/Rummikub">Rummikub</a>
 * 
 * @author rostskadat
 *
 */
@SpringBootApplication
public class Rummikub implements ApplicationRunner {

    @Autowired
    private PlayerController rummikubController;

    public static void main(String[] args) {
        SpringApplication.run(Rummikub.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        int rounds = 1;
        if (args.containsOption("rounds")) {
            rounds = Integer.valueOf(args.getOptionValues("rounds").get(0));
        }
        rummikubController.play(rounds);
    }
}
