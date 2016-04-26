package com.github.bogdanromanx.web.server.settings;

import java.nio.file.Path;

/**
 * Data type representing the mandatory configuration for binding a
 * {@link com.github.bogdanromanx.web.server.vhost.VHostHandler} to an interface:port.
 */
public final class VHostConfig {

    private final String host;
    private final int port;
    private final Path path;
    private final String dispatcher;

    /**
     * Constructs a new {@link VHostConfig} instance from the arguments 'host', 'port', 'path' and 'dispatcher'.
     *
     * @param host       the host to bind the {@link com.github.bogdanromanx.web.server.vhost.VHostHandler} to
     * @param port       the port to bind the {@link com.github.bogdanromanx.web.server.vhost.VHostHandler} to
     * @param path       the root location of the static resources
     * @param dispatcher a config reference to a dispatcher configuration
     */
    private VHostConfig(String host, int port, Path path, String dispatcher) {
        this.host = host;
        this.port = port;
        this.path = path;
        this.dispatcher = dispatcher;
    }

    /**
     * @return the host to bind the {@link com.github.bogdanromanx.web.server.vhost.VHostHandler} to
     */
    public String host() {
        return host;
    }

    /**
     * @return the port to bind the {@link com.github.bogdanromanx.web.server.vhost.VHostHandler} to
     */
    public int port() {
        return port;
    }

    /**
     * @return the root location of the static resources
     */
    public Path path() {
        return path;
    }

    /**
     * @return a config reference to a dispatcher configuration
     */
    public String dispatcher() {
        return dispatcher;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VHostConfig that = (VHostConfig) o;
        if (port != that.port) return false;
        if (!host.equals(that.host)) return false;
        if (!path.equals(that.path)) return false;
        return dispatcher.equals(that.dispatcher);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = host.hashCode();
        result = 31 * result + port;
        result = 31 * result + path.hashCode();
        result = 31 * result + dispatcher.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "VHostConfig{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", path=" + path +
                ", dispatcher='" + dispatcher + '\'' +
                '}';
    }

    /**
     * Constructs a new {@link VHostConfig} instance from the arguments 'host', 'port', 'path' and 'dispatcher'.
     *
     * @param host       the host to bind the {@link com.github.bogdanromanx.web.server.vhost.VHostHandler} to
     * @param port       the port to bind the {@link com.github.bogdanromanx.web.server.vhost.VHostHandler} to
     * @param path       the root location of the static resources
     * @param dispatcher a config reference to a dispatcher configuration
     * @return a new {@link VHostConfig} instance from the arguments 'host', 'port', 'path' and 'dispatcher'.
     */
    public static VHostConfig of(String host, int port, Path path, String dispatcher) {
        return new VHostConfig(host, port, path, dispatcher);
    }
}
