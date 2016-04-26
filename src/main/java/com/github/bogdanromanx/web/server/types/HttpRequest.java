package com.github.bogdanromanx.web.server.types;

import com.github.bogdanromanx.web.server.types.headers.RawHeader;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Data type representing an http message from a client to a server.  It wraps around a {@link RequestLine}, a list of
 * {@link RawHeader} and a {@link HttpEntity}.
 */
@SuppressWarnings("WeakerAccess")
public final class HttpRequest {
    /**
     * The {@link RequestLine} of this {@link HttpRequest}.
     */
    private final RequestLine requestLine;
    /**
     * The collection of {@link RawHeader} of this {@link HttpRequest}.
     */
    private final List<RawHeader> headers;
    /**
     * The {@link HttpEntity} of this {@link HttpRequest}.
     */
    private final HttpEntity entity;

    /**
     * Constructs a new {@link HttpRequest} instance from the 'requestLine', 'headers' and 'entity' arguments.
     *
     * @param requestLine the {@link RequestLine} of the newly constructed {@link HttpRequest}
     * @param headers     the list of {@link RawHeader} of the newly constructed {@link HttpRequest}; note: the factory
     *                    method creates a shallow copy of the headers.
     * @param entity      the {@link HttpEntity} of the newly constructed {@link HttpRequest}
     * @throws NullPointerException if any of the arguments are null.
     */
    private HttpRequest(RequestLine requestLine, Collection<RawHeader> headers, HttpEntity entity) {
        this.requestLine = requireNonNull(requestLine, "The RequestLine cannot be null");
        this.headers = new LinkedList<>(requireNonNull(headers, "The RawHeader collection cannot be null"));
        this.entity = requireNonNull(entity, "The HttpEntity cannot be null");
    }

    /**
     * @return the {@link RequestLine} of this {@link HttpRequest}.
     */
    public RequestLine requestLine() {
        return requestLine;
    }

    /**
     * @return the list of {@link RawHeader} of this {@link HttpRequest}.
     */
    public List<RawHeader> headers() {
        return Collections.unmodifiableList(headers);
    }

    /**
     * @return the {@link HttpEntity} of this {@link HttpRequest}.
     */
    public HttpEntity entity() {
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "HttpRequest{requestLine=" + requestLine + ", headers=" + headers + ", entity=" + entity + '}';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpRequest that = (HttpRequest) o;
        return requestLine.equals(that.requestLine) && headers.equals(that.headers) && entity.equals(that.entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = requestLine.hashCode();
        result = 31 * result + headers.hashCode();
        result = 31 * result + entity.hashCode();
        return result;
    }

    /**
     * Constructs a new {@link HttpRequest} instance from the 'requestLine', 'headers' and 'entity' arguments.
     *
     * @param requestLine the {@link RequestLine} of the newly constructed {@link HttpRequest}
     * @param headers     the list of {@link RawHeader} of the newly constructed {@link HttpRequest}; note: the factory
     *                    method creates a shallow copy of the headers.
     * @param entity      the {@link HttpEntity} of the newly constructed {@link HttpRequest}
     * @return a new {@link HttpRequest} instance from the 'requestLine', 'headers' and 'entity' arguments.
     * @throws NullPointerException if any of the arguments are null.
     */
    public static HttpRequest of(RequestLine requestLine, Collection<RawHeader> headers, HttpEntity entity) {
        return new HttpRequest(requestLine, headers, entity);
    }

    /**
     * @return a new mutable {@link HttpRequest.Builder} instance.
     */
    public static HttpRequest.Builder builder() {
        return new Builder();
    }

    /**
     * <p>
     * A builder for creating {@link HttpRequest} instances.  New instances of {@link HttpRequest.Builder} can be
     * obtained by calling the {@link HttpRequest#builder()} factory method.
     * </p>
     * <p>
     * <strong>Note:</strong> although methods of this class return {@link HttpRequest.Builder} types they mutate the
     * state of the instance.  Caution is recommended when passing instances of this {@link HttpRequest.Builder} outside
     * the local scope.
     * </p>
     */
    public static final class Builder {
        private RequestLine requestLine;
        private List<RawHeader> headers = new LinkedList<>();
        private HttpEntity entity;

        private Builder() {
        }

        /**
         * Sets the argument 'requestLine' as the new value for the future {@link HttpRequest} instance to be built.
         *
         * @param requestLine the request line to set as the new value on the future {@link HttpRequest} instance
         * @return <code>this</code> instance
         * @throws NullPointerException if the argument is null.
         */
        public Builder requestLine(RequestLine requestLine) {
            this.requestLine = requireNonNull(requestLine, "The RequestLine cannot be null");
            return this;
        }

        /**
         * Sets the argument 'headers' as the new value for the future {@link HttpRequest} instance to be built by
         * creating a shallow copy of the argument collection.  Any existing headers will be removed.
         *
         * @param headers the headers to set as the new value on the future {@link HttpRequest} instance
         * @return <code>this</code> instance
         * @throws NullPointerException if the argument is null.
         */
        public Builder headers(Collection<RawHeader> headers) {
            this.headers = new LinkedList<>(requireNonNull(headers, "The header collection cannot be null"));
            return this;
        }

        /**
         * Adds the argument 'header' to the collection of 'headers' stored in this builder.
         *
         * @param header the header to add to the future {@link HttpRequest} instance
         * @return <code>this</code> instance
         * @throws NullPointerException if the argument is null.
         */
        public Builder addHeader(RawHeader header) {
            this.headers.add(header);
            return this;
        }

        /**
         * Adds the argument 'headers' to the collection of 'headers' stored in this builder.
         *
         * @param headers the headers to add to the future {@link HttpRequest} instance
         * @return <code>this</code> instance
         * @throws NullPointerException if the argument is null.
         */
        public Builder addHeaders(RawHeader... headers) {
            this.headers.addAll(
                    Arrays.stream(requireNonNull(headers, "The header collection cannot be null"))
                            .collect(Collectors.toList()));
            return this;
        }

        /**
         * Sets the argument 'entity' as the new value for the future {@link HttpRequest} instance to be built.
         *
         * @param entity the http entity to set as the new value on the future {@link HttpRequest} instance
         * @return <code>this</code> instance
         * @throws NullPointerException if the argument is null.
         */
        public Builder entity(HttpEntity entity) {
            this.entity = requireNonNull(entity, "The HttpEntity cannot be null");
            return this;
        }

        /**
         * Builds a new {@link HttpRequest} instance using the 'requestLine', 'headers' and 'entity' values accumulated
         * within this instance's state.
         *
         * @return a new {@link HttpRequest} instance
         * @throws NullPointerException if any of the fields for the target {@link HttpRequest} are unset (null).
         */
        public HttpRequest build() {
            return of(requestLine, headers, entity);
        }
    }
}
