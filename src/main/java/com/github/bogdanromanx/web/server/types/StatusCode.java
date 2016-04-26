package com.github.bogdanromanx.web.server.types;

import static java.util.Objects.requireNonNull;

/**
 * Data type representing the http status code of a {@link HttpResponse}.  It wraps around an 'int' value and its 'name'
 * (a human readable representation of the code).
 *
 * @see StatusCode#of(String, int)
 */
public final class StatusCode {

    private final String name;
    private final int value;

    private StatusCode(String name, int value) {
        this.name = requireNonNull(name, "StatusCode name cannot be null").trim();
        if (this.name.isEmpty()) {
            throw new IllegalArgumentException("StatusCode name cannot be constructed from an empty string");
        }
        this.value = value;
        if (this.value < 100 || this.value > 999) {
            throw new IllegalArgumentException("StatusCode value must be a within [100, 999]");
        }
    }

    /**
     * @return a human readable representation of <code>this</code> status code, i.e.: 'Not Found'.
     */
    public String name() {
        return name;
    }

    /**
     * @return the status code value, i.e.: '404'.
     */
    public int value() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "StatusCode{name='" + name + "', value=" + value + '}';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatusCode that = (StatusCode) o;
        return value == that.value && name.equals(that.name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + value;
        return result;
    }

    /**
     * Constructs {@link StatusCode} instances of arbitrary names and values.  Names must not be null or whitespace,
     * values must be between 100 and 999 inclusive.
     *
     * @param name  a human readable representation of the {@link StatusCode}, i.e.: 'Not Found'.
     * @param value the status code value, i.e.: '404'.
     * @return a new {@link StatusCode} instance constructed from the 'name' and 'value' arguments.
     * @throws NullPointerException for null 'name' arguments
     */
    public static StatusCode of(String name, int value) {
        return new StatusCode(name, value);
    }

    /**
     * HTTP 200 OK
     */
    public static final StatusCode OK = of("OK", 200);

    /**
     * HTTP 404 Not Found
     */
    public static final StatusCode NOT_FOUND = of("Not Found", 404);

    /**
     * Http 500 Internal Server Error
     */
    public static final StatusCode INTERNAL_SERVER_ERROR = of("Internal Server Error", 500);
}
