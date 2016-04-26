package com.github.bogdanromanx.web.server;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Convenience class for bootstrapping the web server.  It loads the classpath configuration and starts off a new
 * {@link WebServer} instance.
 */
public class Bootstrap {
    public static void main(String[] args) throws Exception {
        new WebServer(loadConfig()).start();
    }

    /**
     * Builds a Typesafe Config instance from multiple sources prioritised as follows:
     * <ol>
     * <li>system properties</li>
     * <li>application.conf files found on the classpath</li>
     * <li>reference.conf files found on the classpath</li>
     * </ol>
     */
    private static Config loadConfig() {
        return ConfigFactory.systemProperties()
                .withFallback(ConfigFactory.defaultApplication())
                .withFallback(ConfigFactory.defaultReference())
                .resolve();
    }
}