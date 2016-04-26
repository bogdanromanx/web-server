package com.github.bogdanromanx.web.server.types.headers;

import com.github.bogdanromanx.web.server.types.HttpHeader;

import static java.util.Objects.requireNonNull;

/**
 * A {@link String} valued {@link HttpHeader} implementation that represents a raw (un-parsed) http header.
 */
public final class RawHeader implements HttpHeader<String> {

    private final String name;
    private final String value;

    /**
     * Constructs a new {@link RawHeader} instance from the 'name' and 'value' arguments.
     *
     * @param name  the name (key) of the header
     * @param value the raw (un-parsed) value of the header
     * @throws NullPointerException     for null 'name' or 'value' arguments
     * @throws IllegalArgumentException for whitespace only 'name' or 'value' arguments
     */
    private RawHeader(String name, String value) {
        this.name = requireNonNull(name, "Header name cannot be null").trim();
        this.value = requireNonNull(value, "Header value cannot be null").trim();
        if (this.name.isEmpty()) {
            throw new IllegalArgumentException("Header name cannot be a whitespace only string");
        }
        if (this.value.isEmpty()) {
            throw new IllegalArgumentException("Header value cannot be a whitespace only string");
        }
    }

    /**
     * @return the name of <code>this</code> {@link RawHeader}
     */
    @Override
    public String name() {
        return name;
    }

    /**
     * @return the name of <code>this</code> {@link RawHeader}
     */
    @Override
    public String value() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RawHeader raw() {
        return this;
    }

    /**
     * Constructs a new {@link RawHeader} instance from the 'name' and 'value' arguments.
     *
     * @param name  the name (key) of the header
     * @param value the raw (un-parsed) value of the header
     * @throws NullPointerException     for null 'name' or 'value' arguments
     * @throws IllegalArgumentException for whitespace only 'name' or 'value' arguments
     */
    public static RawHeader of(String name, String value) {
        return new RawHeader(name, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RawHeader rawHeader = (RawHeader) o;
        return name.equals(rawHeader.name) && value.equals(rawHeader.value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "RawHeader{name='" + name + "', value='" + value + "'}";
    }
}
