package com.github.bogdanromanx.web.server.settings;

import akka.actor.Extension;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Akka extension implementation that provides access to the application configuration.
 * @see Extension
 */
public final class Settings implements Extension {

    private final Duration systemTerminateTimeout;
    private final ParsingConfig parsingConfig;
    private final List<VHostConfig> vHostConfigs;

    /**
     * Constructs a new {@link Settings} instance from the arguments 'systemTerminateTimeout', 'parsingConfig' and
     * 'vHostConfigs' collection.
     *
     * @param systemTerminateTimeout the maximum duration to wait until the {@link akka.actor.ActorSystem} terminates
     * @param parsingConfig          the {@link com.github.bogdanromanx.web.server.types.HttpRequest} parsing configuration
     * @param vHostConfigs           a collection of {@link VHostConfig} instances
     */
    private Settings(Duration systemTerminateTimeout, ParsingConfig parsingConfig, Collection<VHostConfig> vHostConfigs) {
        this.systemTerminateTimeout = systemTerminateTimeout;
        this.parsingConfig = parsingConfig;
        this.vHostConfigs = new LinkedList<>(vHostConfigs);
    }

    /**
     * @return the maximum duration to wait until the {@link akka.actor.ActorSystem} terminates
     */
    public Duration systemTerminateTimeout() {
        return systemTerminateTimeout;
    }

    /**
     * @return the {@link com.github.bogdanromanx.web.server.types.HttpRequest} parsing configuration
     */
    public ParsingConfig parsingConfig() {
        return parsingConfig;
    }

    /**
     * @return a collection of {@link VHostConfig} instances
     */
    public List<VHostConfig> vHostConfigs() {
        return Collections.unmodifiableList(vHostConfigs);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Settings settings = (Settings) o;
        if (!systemTerminateTimeout.equals(settings.systemTerminateTimeout)) return false;
        if (!parsingConfig.equals(settings.parsingConfig)) return false;
        return vHostConfigs.equals(settings.vHostConfigs);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = systemTerminateTimeout.hashCode();
        result = 31 * result + parsingConfig.hashCode();
        result = 31 * result + vHostConfigs.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Settings{" +
                "systemTerminateTimeout=" + systemTerminateTimeout +
                ", parsingConfig=" + parsingConfig +
                ", vHostConfigs=" + vHostConfigs +
                '}';
    }

    /**
     * Constructs a new {@link Settings} instance from the arguments 'systemTerminateTimeout', 'parsingConfig' and
     * 'vHostConfigs' collection.
     *
     * @param systemTerminateTimeout the maximum duration to wait until the {@link akka.actor.ActorSystem} terminates
     * @param parsingConfig          the {@link com.github.bogdanromanx.web.server.types.HttpRequest} parsing configuration
     * @param vHostConfigs           a collection of {@link VHostConfig} instances
     * @return a new {@link Settings} instance from the arguments 'systemTerminateTimeout', 'parsingConfig' and
     * 'vHostConfigs' collection.
     */
    public static Settings of(Duration systemTerminateTimeout, ParsingConfig parsingConfig, Collection<VHostConfig> vHostConfigs) {
        return new Settings(systemTerminateTimeout, parsingConfig, vHostConfigs);
    }
}
