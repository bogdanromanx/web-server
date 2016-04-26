package com.github.bogdanromanx.web.server.vhost;

/**
 * Contract definition for {@link VHostHandler} implementations.
 */
public interface VHostHandler {
    /**
     * Bootstraps this handler, binding it to the configured interface and port.
     */
    void start();

    /**
     * Terminates this handler freeing all resources.
     */
    void terminate();
}
