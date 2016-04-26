package com.github.bogdanromanx.web.server.settings;

import com.github.bogdanromanx.web.server.types.HttpHeader;
import com.github.bogdanromanx.web.server.types.RequestLine;

/**
 * Data type representing the predefined parsing configuration for inbound
 * {@link com.github.bogdanromanx.web.server.types.HttpRequest} instances.
 */
public final class ParsingConfig {

    private final int headerNameLength;
    private final int headerValueLength;
    private final int methodLength;
    private final int uriLength;
    private final int protocolLength;

    /**
     * Constructs a new {@link ParsingConfig} instance from the argument values.
     *
     * @param headerNameLength  the maximum number of bytes allowed for a {@link HttpHeader#name()}
     * @param headerValueLength the maximum number of bytes allowed for a {@link HttpHeader#value()}
     * @param methodLength      the maximum number of bytes allowed for a {@link RequestLine#method()}
     * @param uriLength         the maximum number of bytes allowed for a {@link RequestLine#uri()}
     * @param protocolLength    the maximum number of bytes allowed for a {@link RequestLine#protocol()}
     */
    private ParsingConfig(int headerNameLength, int headerValueLength, int methodLength, int uriLength, int protocolLength) {
        this.headerNameLength = headerNameLength;
        this.headerValueLength = headerValueLength;
        this.methodLength = methodLength;
        this.uriLength = uriLength;
        this.protocolLength = protocolLength;
    }

    /**
     * @return the maximum number of bytes allowed for a {@link HttpHeader#name()}
     */
    public int headerNameLength() {
        return headerNameLength;
    }

    /**
     * @return the maximum number of bytes allowed for a {@link HttpHeader#value()}
     */
    public int headerValueLength() {
        return headerValueLength;
    }

    /**
     * @return the maximum number of bytes allowed for the {@link RequestLine#method()}
     */
    public int methodLength() {
        return methodLength;
    }

    /**
     * @return the maximum number of bytes allowed for the {@link RequestLine#uri()}
     */
    public int uriLength() {
        return uriLength;
    }

    /**
     * @return the maximum number of bytes allowed for the {@link RequestLine#protocol()}
     */
    public int protocolLength() {
        return protocolLength;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParsingConfig that = (ParsingConfig) o;
        if (headerNameLength != that.headerNameLength) return false;
        if (headerValueLength != that.headerValueLength) return false;
        if (methodLength != that.methodLength) return false;
        if (uriLength != that.uriLength) return false;
        return protocolLength == that.protocolLength;
    }

    @Override
    public int hashCode() {
        int result = headerNameLength;
        result = 31 * result + headerValueLength;
        result = 31 * result + methodLength;
        result = 31 * result + uriLength;
        result = 31 * result + protocolLength;
        return result;
    }

    @Override
    public String toString() {
        return "ParsingConfig{" +
                "headerNameLength=" + headerNameLength +
                ", headerValueLength=" + headerValueLength +
                ", methodLength=" + methodLength +
                ", uriLength=" + uriLength +
                ", protocolLength=" + protocolLength +
                '}';
    }

    /**
     * Constructs a new {@link ParsingConfig} instance from the argument values.
     *
     * @param headerNameLength  the maximum number of bytes allowed for a {@link HttpHeader#name()}
     * @param headerValueLength the maximum number of bytes allowed for a {@link HttpHeader#value()}
     * @param methodLength      the maximum number of bytes allowed for a {@link RequestLine#method()}
     * @param uriLength         the maximum number of bytes allowed for a {@link RequestLine#uri()}
     * @param protocolLength    the maximum number of bytes allowed for a {@link RequestLine#protocol()}
     * @return a new {@link ParsingConfig} instance from the argument values.
     */
    public static ParsingConfig of(int headerNameLength, int headerValueLength, int methodLength, int uriLength, int protocolLength) {
        return new ParsingConfig(headerNameLength, headerValueLength, methodLength, uriLength, protocolLength);
    }
}
