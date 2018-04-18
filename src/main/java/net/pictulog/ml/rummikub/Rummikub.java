package net.pictulog.ml.rummikub;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * A simple implementation of <a href="https://en.wikipedia.org/wiki/Rummikub">Rummikub</a>
 * 
 * @author rostskadat
 *
 */
@Component
@SpringBootApplication
public class Rummikub {

    private static final Logger LOG = LoggerFactory.getLogger(Rummikub.class);

    @Value("${server.address:127.0.0.1}")
    private String serverAddress;

    @Value("${server.port:8080}")
    private Integer serverPort;

    @Value("${server.ssl.enabled:false}")
    private Boolean serverSslEnable;

    public static void main(String[] args) {
        SpringApplication.run(Rummikub.class, args);
    }

    @EventListener({ ApplicationReadyEvent.class })
    private void applicationReadyEvent() {
        openBrowser();
    }

    private void openBrowser() {
        String url = String.format("%s://%s:%d/rummikub", (serverSslEnable ? "https" : "http"),
                serverAddress, serverPort);
        if (LOG.isInfoEnabled()) {
            LOG.info(String.format("Application started ... Launching browser @ %s", url));
        }
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                LOG.error(e.getMessage(), e);
                LOG.error(String.format("Try opening your browser at %s", url));
            }
        } else {
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("rundll32 url.dll,FileProtocolHandler " + url);
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
                LOG.error(String.format("Try opening your browser at %s", url));
            }
        }
    }

}
