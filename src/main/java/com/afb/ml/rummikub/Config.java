package com.afb.ml.rummikub;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@EnableAutoConfiguration
@ComponentScan("com.afb.ml.rummikub")
@PropertySource("classpath:rummikub.properties")
public class Config {

}
