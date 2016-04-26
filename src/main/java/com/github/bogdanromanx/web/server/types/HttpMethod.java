package com.github.bogdanromanx.web.server.types;

import java.util.Arrays;

import static java.util.Objects.requireNonNull;

/**
 * Http method contract definition.
 */
public interface HttpMethod {

    /**
     * @return a {@link String} representation of this {@link HttpMethod}
     */
    String value();

    /**
     * Attempts to find a {@link HttpMethod} in the {@link HttpMethod.Standard} enumeration with the same value.  If no
     * matches are found, it will construct a new {@link HttpMethod.Custom} instance with the argument value.
     *
     * @param value the {@link String} representation of the returned {@link HttpMethod}
     * @return either {@link HttpMethod.Standard} instance if the argument value matches one of the values in the
     * enumeration, or a {@link HttpMethod.Custom} instance constructed from the argument 'value' otherwise.
     * @throws NullPointerException     for null 'value' argument
     * @throws IllegalArgumentException if the 'value' argument is empty or consists only of whitespace
     */
    static HttpMethod of(String value) {
        return Arrays.<HttpMethod>stream(Standard.values())
                .filter(v -> v.value().equals(value))
                .findFirst()
                .orElse(new Custom(value));
    }

    /**
     * Enumeration of standard http methods.
     */
    @SuppressWarnings("unused")
    enum Standard implements HttpMethod {
        CONNECT("CONNECT"),
        DELETE("DELETE"),
        GET("GET"),
        HEAD("HEAD"),
        OPTIONS("OPTIONS"),
        PATCH("PATCH"),
        POST("POST"),
        PUT("PUT"),
        TRACE("TRACE");

        private final String value;

        Standard(String value) {
            this.value = value;
        }

        /**
         * {@inheritDoc}
         */
        public String value() {
            return value;
        }

        /**
         * @return a human readable {@link String} representation of <code>this</code> instance.
         */
        @Override
        public String toString() {
            return "HttpMethod.Standard{value='" + value + '\'' + '}';
        }
    }

    /**
     * Data type representing a {@link HttpMethod} instance unlisted in the {@link HttpMethod.Standard} enumeration.
     */
    final class Custom implements HttpMethod {
        private final String value;

        private Custom(String value) {
            this.value = requireNonNull(value).trim();
            if (this.value.isEmpty()) {
                throw new IllegalArgumentException("HttpMethod.Custom cannot be constructed from an empty string");
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String value() {
            return value;
        }

        /**
         * @return a human readable {@link String} representation of <code>this</code> instance.
         */
        @Override
        public String toString() {
            return "HttpMethod.Custom{value='" + value + '\'' + '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Custom custom = (Custom) o;
            return value.equals(custom.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }
}
