package com.github.bogdanromanx.web.server.types;

import com.github.bogdanromanx.web.server.types.headers.RawHeader;

/**
 * Typed http header contract.  An http header is a (key, value) pair, where keys are of type {@link String} and values
 * are of type 'T'.
 *
 * @param <T> the type of the header vallue.
 */
public interface HttpHeader<T> {
    /**
     * @return the name (key) of the header.
     */
    String name();

    /**
     * @return the value of the header.
     */
    T value();

    /**
     * @return an untyped header instance (its value is of type {@link String}).
     * @see RawHeader
     */
    RawHeader raw();

    /**
     * @return the name of the header in lower casing.
     */
    default String lowerCaseName() {
        return name().toLowerCase();
    }
}
