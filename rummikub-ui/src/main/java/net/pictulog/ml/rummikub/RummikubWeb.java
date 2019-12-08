package net.pictulog.ml.rummikub;

import static java.lang.String.format;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * A simple implementation of
 * <a href="https://en.wikipedia.org/wiki/Rummikub">Rummikub</a>
 * 
 * @author rostskadat
 *
 */
@Component
@SpringBootApplication
public class RummikubWeb {

	private static final Logger LOG = LoggerFactory.getLogger(RummikubWeb.class);

	@Value("${server.address:127.0.0.1}")
	private String serverAddress;

	@Value("${server.port:8080}")
	private Integer serverPort;

	@Value("${server.ssl.enabled:false}")
	private Boolean serverSslEnable;

	public static void main(String[] args) {
		SpringApplication.run(RummikubWeb.class, args);
	}

	@EventListener({ ApplicationReadyEvent.class })
	private void applicationReadyEvent() {
		openBrowser();
	}

	@EventListener({ ApplicationFailedEvent.class })
	private void applicationFailedEvent() {
		openBrowser();
	}

	private void openBrowser() {
		String url = format("%s://%s:%d/rummikub", (serverSslEnable ? "https" : "http"), serverAddress,
				serverPort);
		if (LOG.isInfoEnabled()) {
			LOG.info(format("Application started ... Launching browser @ %s", url));
		}
		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			if (Desktop.getDesktop().isSupported(Action.BROWSE)) {
				try {
					desktop.browse(new URI(url));
				} catch (IOException | URISyntaxException e) {
					LOG.error(e.getMessage(), e);
					LOG.error(format("Try opening your browser at %s", url));
				}
			} else {
				LOG.error("Desktop Action Browser is not supported.");
				LOG.error(format("Try opening your browser at %s", url));
			}
		} else {
			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec("/usr/bin/firefox -new-window " + url);
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
				LOG.error(format("Try opening your browser at %s", url));
			}
		}
	}

}
