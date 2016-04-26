package com.github.bogdanromanx.web.server.types.headers;

import com.github.bogdanromanx.web.server.types.HttpHeader;

import java.util.Optional;

/**
 * Http 'Content-Length' header type definition.
 */
@SuppressWarnings("WeakerAccess")
public final class ContentLength implements HttpHeader<Long> {

    /**
     * The constant name of the {@link ContentLength} header.
     */
    public static final String NAME = "Content-Length";

    /**
     * The constant lower cased name of the {@link ContentLength} header.
     */
    public static final String LOWERCASE_NAME = NAME.toLowerCase();

    private final long value;
    private final RawHeader raw;

    /**
     * Constructs a new {@link ContentType} instance from the 'value' argument.
     *
     * @param value the value of the header
     * @throws IllegalArgumentException for values smaller than Zero
     */
    private ContentLength(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("Content-Length value must have a positive value");
        }
        this.value = value;
        raw = RawHeader.of(NAME, Long.toString(value));
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
    public Long value() {
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
        ContentLength that = (ContentLength) o;
        return value == that.value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return (int) (value ^ (value >>> 32));
    }

    @Override
    public String toString() {
        return "ContentLength{value=" + value + '}';
    }

    /**
     * Constructs a new {@link ContentType} instance from the 'value' argument.
     *
     * @param value the value of the header
     * @throws IllegalArgumentException for values smaller than Zero
     */
    public static ContentLength of(long value) {
        return new ContentLength(value);
    }

    /**
     * Attempts to construct a new {@link ContentLength} header from the argument {@link RawHeader}.
     *
     * @param raw the source {@link RawHeader}
     * @return an {@link Optional} {@link ContentLength}, inhabited if the lowercase name of the argument header equals
     * to the lowercase name constant of the {@link ContentLength} header and the value can be parsed into a positive
     * {@link Long} value, uninhabited otherwise.
     */
    public static Optional<ContentLength> of(RawHeader raw) {
        if (LOWERCASE_NAME.equals(raw.lowerCaseName())) {
            try {
                return Optional.of(ContentLength.of(Long.parseLong(raw.value())));
            } catch (Exception e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }
}