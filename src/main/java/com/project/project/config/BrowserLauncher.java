package com.project.project.config;

import java.awt.Desktop;
import java.net.URI;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class BrowserLauncher {

    private static final Logger log = LoggerFactory.getLogger(BrowserLauncher.class);

    @Autowired
    private Environment env;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady(ApplicationReadyEvent event) {
        try {
            String[] profiles = env.getActiveProfiles();
            if (profiles != null && Arrays.asList(profiles).contains("test")) {
                log.info("Test profile active - skipping browser launch");
                return;
            }

            int port = 8080;
            if (event.getApplicationContext() instanceof WebServerApplicationContext) {
                WebServerApplicationContext wc = (WebServerApplicationContext) event.getApplicationContext();
                port = wc.getWebServer().getPort();
            } else {
                String p = env.getProperty("server.port");
                if (p != null) port = Integer.parseInt(p);
            }

            // Open index.html explicitly so the app shows the main index (not the /login redirect)
            String url = "http://localhost:" + port + "/index.html";
            log.info("Opening browser to {}", url);

            // Try Desktop API first
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                // Fallback to platform-specific command (Windows)
                Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start", url});
            }
        } catch (Throwable ex) {
            log.warn("Could not open browser automatically", ex);
        }
    }
}
