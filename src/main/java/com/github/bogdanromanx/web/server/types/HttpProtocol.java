package com.github.bogdanromanx.web.server.types;

import java.util.Arrays;
import java.util.Optional;

/**
 * An enumeration of the available http protocols and their {@link String} representations.
 */
public enum HttpProtocol {
    HTTP_1_0("HTTP/1.0"), HTTP_1_1("HTTP/1.1");

    private final String value;

    HttpProtocol(String value) {
        this.value = value;
    }

    /**
     * @return a {@link String} representation of this protocol value.
     */
    public String value() {
        return value;
    }

    /**
     * @return a {@link String} representation of this protocol value.
     */
    @Override
    public String toString() {
        return value;
    }

    /**
     * Attempts to retrieve a {@link HttpProtocol} instance from its available instances enumeration based on value
     * equality.
     *
     * @param value the value of a {@link HttpProtocol} instance
     * @return an optional {@link HttpProtocol} instance, inhabited if the argument value matches one of the known
     * values or uninhabited if there's no known {@link HttpProtocol} with that value.
     */
    public static Optional<HttpProtocol> of(String value) {
        return Arrays.stream(values()).filter(v -> v.value.equals(value)).findFirst();
    }
}
