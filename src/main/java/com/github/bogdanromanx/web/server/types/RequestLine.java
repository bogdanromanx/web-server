package com.github.bogdanromanx.web.server.types;

import java.net.URI;

import static java.util.Objects.requireNonNull;

/**
 * Data type representing the first line of an {@link HttpRequest} that bundles the {@link HttpMethod} to be applied
 * to the resource, the identifier of the resource expressed as an {@link java.net.URI} and the {@link HttpProtocol}
 * version in use.
 */
@SuppressWarnings("WeakerAccess")
public final class RequestLine {
    /**
     * The {@link HttpMethod} of this {@link RequestLine}.
     */
    private final HttpMethod method;

    /**
     * The {@link URI} of this {@link RequestLine}.
     */
    private final URI uri;

    /**
     * The {@link HttpProtocol} of this {@link RequestLine}.
     */
    private final HttpProtocol protocol;

    /**
     * Constructs a new {@link RequestLine} instance from the 'method', 'uri' and 'protocol' arguments.
     *
     * @param method   the {@link HttpMethod} of the newly constructed {@link RequestLine}
     * @param uri      the {@link URI} of the newly constructed {@link RequestLine}
     * @param protocol the {@link HttpProtocol} of the newly constructed {@link RequestLine}
     * @throws NullPointerException if any of the arguments are null.
     */
    private RequestLine(HttpMethod method, URI uri, HttpProtocol protocol) {
        this.method = requireNonNull(method, "HttpMethod cannot be null");
        this.uri = requireNonNull(uri, "URI cannot be null");
        this.protocol = requireNonNull(protocol, "HttpProtocol cannot be null");
    }

    /**
     * @return the method of this RequestLine
     */
    public HttpMethod method() {
        return method;
    }

    /**
     * @return the URI of this RequestLine
     */
    public URI uri() {
        return uri;
    }

    /**
     * @return the protocol of this RequestLine
     */
    public HttpProtocol protocol() {
        return protocol;
    }

    /**
     * Constructs a new RequestLine instance from the 'method', 'uri' and 'protocol' arguments.
     *
     * @param method   the {@link HttpMethod} of the newly constructed {@link RequestLine}
     * @param uri      the {@link URI} of the newly constructed {@link RequestLine}
     * @param protocol the {@link HttpProtocol} of the newly constructed {@link RequestLine}
     * @return a new RequestLine instance constructed from the 'method', 'uri' and 'protocol' arguments
     * @throws NullPointerException if any of the arguments are null.
     */
    public static RequestLine of(HttpMethod method, URI uri, HttpProtocol protocol) {
        return new RequestLine(method, uri, protocol);
    }

    /**
     * @return a new mutable {@link HttpRequest.Builder} instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "RequestLine{method=" + method + ", uri=" + uri + ", protocol=" + protocol + '}';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestLine that = (RequestLine) o;
        return method.equals(that.method) && uri.equals(that.uri) && protocol == that.protocol;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = method.hashCode();
        result = 31 * result + uri.hashCode();
        result = 31 * result + protocol.hashCode();
        return result;
    }

    /**
     * <p>
     * A builder for creating {@link RequestLine} instances.  New instances of {@link Builder} can be obtained by
     * calling the {@link RequestLine#builder()} factory method.
     * </p>
     * <p>
     * <strong>Note:</strong> although methods of this class return {@link Builder} types they mutate the state of
     * the instance.  Caution is recommended when passing instances of this {@link Builder} outside the local scope.
     * </p>
     */
    public static final class Builder {
        private HttpMethod method;
        private URI uri;
        private HttpProtocol protocol;

        private Builder() {
        }

        /**
         * Sets the argument 'method' as the new value for the future {@link RequestLine} instance to be built.
         *
         * @param method the method to set as the new value on the future {@link RequestLine} instance
         * @return <code>this</code> instance
         * @throws NullPointerException if the argument is null.
         */
        public Builder method(HttpMethod method) {
            this.method = requireNonNull(method, "The HttpMethod cannot be null");
            return this;
        }

        /**
         * Sets the argument 'uri' as the new value for the future {@link RequestLine} instance to be built.
         *
         * @param uri the uri to set as the new value on the future {@link RequestLine} instance
         * @return <code>this</code> instance
         * @throws NullPointerException if the argument is null.
         */
        public Builder uri(URI uri) {
            this.uri = requireNonNull(uri, "The URI cannot be null");
            return this;
        }

        /**
         * Sets the argument 'protocol' as the new value for the future {@link RequestLine} instance to be built.
         *
         * @param protocol the method to set as the new value on the future {@link RequestLine} instance
         * @return <code>this</code> instance
         * @throws NullPointerException if the argument is null.
         */
        public Builder protocol(HttpProtocol protocol) {
            this.protocol = requireNonNull(protocol, "The HttpProtocol cannot be null");
            return this;
        }

        /**
         * Builds a new {@link RequestLine} instance using the 'method', 'uri' and 'protocol' values accumulated within
         * this instance's state.
         *
         * @return a new {@link RequestLine} instance
         * @throws NullPointerException if any of the fields for the target {@link RequestLine} are unset (null).
         */
        public RequestLine build() {
            return new RequestLine(method, uri, protocol);
        }
    }
}
