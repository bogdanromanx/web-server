package com.github.bogdanromanx.web.server.types;

import com.github.bogdanromanx.web.server.types.headers.ContentLength;
import com.github.bogdanromanx.web.server.types.headers.RawHeader;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static java.util.Objects.requireNonNull;

/**
 * Data type representing an http message from a server to a client.  It wraps around a {@link StatusCode}, a list of
 * {@link RawHeader} and a {@link HttpEntity}.
 */
public final class HttpResponse {
    /**
     * The {@link StatusCode} of this {@link HttpResponse}.
     */
    private final StatusCode statusCode;
    /**
     * The collection of {@link RawHeader} of this {@link HttpResponse}.
     */
    private final List<RawHeader> headers;
    /**
     * The {@link HttpEntity} of this {@link HttpResponse}.
     */
    private final HttpEntity entity;

    /**
     * Constructs a new {@link HttpResponse} instance from the 'statusCode', 'headers' and 'entity' arguments.
     *
     * @param statusCode the {@link StatusCode} of the newly constructed {@link HttpResponse}
     * @param headers    the list of {@link RawHeader} of the newly constructed {@link HttpResponse}; note: the factory
     *                   method creates a shallow copy of the headers.
     * @param entity     the {@link HttpEntity} of the newly constructed {@link HttpResponse}
     * @throws NullPointerException if any of the arguments is null.
     */
    private HttpResponse(StatusCode statusCode, Collection<RawHeader> headers, HttpEntity entity) {
        this.statusCode = requireNonNull(statusCode, "The StatusCode cannot be null");
        this.headers = new LinkedList<>(requireNonNull(headers, "The RawHeader collection cannot be null"));
        this.entity = requireNonNull(entity, "The HttpEntity cannot be null");
    }

    /**
     * @return the {@link StatusCode} of this {@link HttpResponse}.
     */
    public StatusCode statusCode() {
        return statusCode;
    }

    /**
     * @return the collection of {@link RawHeader} of this {@link HttpResponse}.
     */
    public List<RawHeader> headers() {
        return Collections.unmodifiableList(headers);
    }

    /**
     * @return the {@link HttpEntity} of this {@link HttpResponse}.
     */
    public HttpEntity entity() {
        return entity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "HttpResponse{statusCode=" + statusCode + ", headers=" + headers + ", entity=" + entity + '}';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpResponse response = (HttpResponse) o;
        return statusCode.equals(response.statusCode)
                && headers.equals(response.headers)
                && entity.equals(response.entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = statusCode.hashCode();
        result = 31 * result + headers.hashCode();
        result = 31 * result + entity.hashCode();
        return result;
    }

    /**
     * Constructs a new successful {@link HttpResponse} instance (200 OK) with an empty {@link HttpEntity}
     * and its corresponding 'Content-Length' header.
     */
    public static HttpResponse of() {
        return of(StatusCode.OK);
    }

    /**
     * Constructs a new {@link HttpResponse} instance with the argument 'statusCode' and an empty {@link HttpEntity}
     * and its corresponding 'Content-Length' header.
     *
     * @param statusCode the {@link StatusCode} of the newly constructed {@link HttpResponse}
     * @return a new {@link HttpResponse} instance with the argument 'statusCode' and an empty {@link HttpEntity}.
     * @throws NullPointerException if the argument is null.
     */
    public static HttpResponse of(StatusCode statusCode) {
        return of(statusCode, Collections.singletonList(ContentLength.of(0).raw()), HttpEntity.empty());
    }

    /**
     * Constructs a new {@link HttpResponse} instance from the 'statusCode', 'headers' and 'entity' arguments.
     *
     * @param statusCode the {@link StatusCode} of the newly constructed {@link HttpResponse}
     * @param headers    the list of {@link RawHeader} of the newly constructed {@link HttpResponse}; note: the factory
     *                   method creates a shallow copy of the headers.
     * @param entity     the {@link HttpEntity} of the newly constructed {@link HttpResponse}
     * @throws NullPointerException if any of the arguments is null.
     */
    public static HttpResponse of(StatusCode statusCode, Collection<RawHeader> headers, HttpEntity entity) {
        return new HttpResponse(statusCode, headers, entity);
    }
}
