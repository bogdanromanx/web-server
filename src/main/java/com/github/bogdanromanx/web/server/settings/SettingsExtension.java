package com.github.bogdanromanx.web.server.settings;

import akka.actor.AbstractExtensionId;
import akka.actor.ExtendedActorSystem;
import akka.actor.ExtensionIdProvider;
import com.typesafe.config.Config;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provider for the {@link Settings} extension, ensuring a single instance across the entire
 * {@link akka.actor.ActorSystem}.
 *
 * @see ExtensionIdProvider
 */
public class SettingsExtension extends AbstractExtensionId<Settings> implements ExtensionIdProvider {
    public final static SettingsExtension SettingsExtensionProvider = new SettingsExtension();
    private final static String NS = "web.server";

    private SettingsExtension() {
    }

    @Override
    public SettingsExtension lookup() {
        return SettingsExtensionProvider;
    }

    /**
     * Constructs the {@link Settings} extension instance using the configuration loaded within the argument
     * {@link ExtendedActorSystem}.
     *
     * @param system the target actor system for this extension
     * @return the {@link Settings} extension instance using the configuration loaded within the argument
     * {@link ExtendedActorSystem}.
     */
    @Override
    public Settings createExtension(ExtendedActorSystem system) {
        Config config = system.settings().config().getConfig(NS);

        Duration systemTerminateTimeout = config.getDuration("system-terminate-timeout");

        ParsingConfig parsingConfig = parsingConfig(config.getConfig("parsing"));

        List<VHostConfig> vHostConfigs = config
                .getConfigList("vhosts")
                .stream()
                .map(this::vHostConfig)
                .collect(Collectors.toList());

        return Settings.of(systemTerminateTimeout, parsingConfig, vHostConfigs);
    }

    private ParsingConfig parsingConfig(Config config) {
        return ParsingConfig.of(
                config.getInt("header-name-length"),
                config.getInt("header-value-length"),
                config.getInt("method-length"),
                config.getInt("uri-length"),
                config.getInt("protocol-length"));
    }

    private VHostConfig vHostConfig(Config config) {
        return VHostConfig.of(
                config.getString("host"),
                config.getInt("port"),
                Paths.get(config.getString("path")).toAbsolutePath().normalize(),
                config.getString("dispatcher"));
    }
}
