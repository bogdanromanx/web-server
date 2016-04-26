package com.github.bogdanromanx.web.server.types.headers;

import com.github.bogdanromanx.web.server.types.HttpHeader;

import static java.util.Objects.requireNonNull;

/**
 * Http 'Content-Type' header type definition.
 */
@SuppressWarnings("WeakerAccess")
public final class ContentType implements HttpHeader<String> {

    /**
     * The constant name of the {@link ContentType} header.
     */
    public static final String NAME = "Content-Type";

    /**
     * The constant lower cased name of the {@link ContentType} header.
     */
    public static final String LOWERCASE_NAME = NAME.toLowerCase();

    private final String value;
    private final RawHeader raw;

    /**
     * Constructs a new {@link ContentType} instance from the 'value' argument.
     *
     * @param value the value of the header
     * @throws NullPointerException     for null value argument
     * @throws IllegalArgumentException for values consisting of only whitespace
     */
    private ContentType(String value) {
        this.value = requireNonNull(value, "Content-Type value cannot be null").trim();
        if (this.value.isEmpty()) {
            throw new IllegalArgumentException("Content-Type value cannot be a whitespace only string");
        }
        raw = RawHeader.of(NAME, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String lowerCaseName() {
        return LOWERCASE_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RawHeader raw() {
        return raw;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContentType that = (ContentType) o;
        return value.equals(that.value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return value.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "ContentType{value='" + value + "'}";
    }

    /**
     * Constructs a new {@link ContentType} instance from the 'value' argument.
     *
     * @param value the value of the header
     * @throws NullPointerException     for null value argument
     * @throws IllegalArgumentException for values consisting of only whitespace
     */
    public static ContentType of(String value) {
        return new ContentType(value);
    }

    /**
     * Constant {@link ContentType} instance for 'application/octet-stream'.
     */
    public static final ContentType APPLICATION_OCTET_STREAM = ContentType.of("application/octet-stream");

    /**
     * Constant {@link ContentType} instance for 'text/plain'.
     */
    public static final ContentType TEXT_PLAIN = ContentType.of("text/plain");
}
