package com.github.bogdanromanx.web.server;

import akka.actor.ActorSystem;
import com.github.bogdanromanx.web.server.settings.Settings;
import com.github.bogdanromanx.web.server.settings.SettingsExtension;
import com.github.bogdanromanx.web.server.vhost.AkkaTcpHandler;
import com.github.bogdanromanx.web.server.vhost.VHostHandler;
import com.typesafe.config.Config;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Convenience class for bootstrapping the web server.
 */
@SuppressWarnings("WeakerAccess")
public final class WebServer {

    private final Config config;
    private ActorSystem system;
    private List<VHostHandler> vHostHandlers;

    /**
     * Constructs a new {@link WebServer} instance from the argument 'config'.
     *
     * @param config the Typesafe {@link Config} instance that stores the required configuration
     */
    public WebServer(Config config) {
        this.config = config;
    }

    /**
     * Starts up the 'WebServer', binding all vhosts to their interface:port configuration.
     */
    public void start() {
        system = ActorSystem.create("http-server", config);

        Settings settings = SettingsExtension.SettingsExtensionProvider.get(system);

        vHostHandlers = settings.vHostConfigs()
                .stream()
                .map(vc -> new AkkaTcpHandler(system, vc))
                .collect(Collectors.toList());

        vHostHandlers.forEach(VHostHandler::start);

        Runtime.getRuntime().addShutdownHook(new Thread(this::terminate));
    }

    /**
     * Terminates the 'WebServer', shutting down all {@link VHostHandler}s and freeing up the resources.
     */
    public void terminate() {
        Settings settings = SettingsExtension.SettingsExtensionProvider.get(system);
        vHostHandlers.forEach(VHostHandler::terminate);
        try {
            Duration duration = Duration.fromNanos(settings.systemTerminateTimeout().toNanos());
            Await.result(system.terminate(), duration);
        } catch (Exception e) {
            throw new RuntimeException("Exception caught while waiting for the actor system to terminate", e);
        }
    }
}
