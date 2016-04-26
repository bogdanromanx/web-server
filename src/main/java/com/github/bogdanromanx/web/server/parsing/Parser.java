package com.github.bogdanromanx.web.server.parsing;

import akka.util.ByteString;

import java.util.function.Function;

/**
 * Contract definitions for parsers that take a {@link ByteString} and produce a {@link Result} of an arbitrary type
 * <code>T</code>.
 *
 * @param <T> the type of the possible parsed value
 */
@SuppressWarnings("WeakerAccess")
public interface Parser<T> extends Function<ByteString, Result<T>> {
}
