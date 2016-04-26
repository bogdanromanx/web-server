package com.github.bogdanromanx.web.server.parsing;

import akka.util.ByteString;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Monadic type definition to bundle possible successful parsed values and remaining bytes.  It allows parsers to easily
 * compose parsing attempts using the {@link Result#map(Function)}, {@link Result#flatMap(Function)} and
 * {@link Result#andThen(BiFunction)} functions.
 *
 * @param <T> the type of the optional parsing result
 */
@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "WeakerAccess"})
public final class Result<T> {

    private final Optional<T> value;
    private final ByteString remaining;

    private Result(Optional<T> value, ByteString remaining) {
        this.value = value;
        this.remaining = remaining;
    }

    /**
     * @return an optional parsed value.
     */
    public Optional<T> value() {
        return value;
    }

    /**
     * @return the remaining bytes after parsing the resulting value.
     */
    public ByteString remaining() {
        return remaining;
    }

    /**
     * Applies the function to the parsed value, if available, producing a new result of a possible different value
     * type.
     *
     * @param mapper the function to apply to produce a new Result instance
     * @param <S>    the generic type of the new Result instance
     * @return a new Result instance created by applying the function to the value, if the value exists or a new empty
     * Result of type S containing the remaining bytes of this instance.
     */
    public <S> Result<S> flatMap(Function<? super T, Result<S>> mapper) {
        return value.map(mapper).orElse(of(remaining));
    }

    /**
     * Applies the function to the parsed value, if available, producing a new result of a possible different value
     * type.  The new Result instance will contain the same remaining available bytes.
     *
     * @param mapper the function to apply to produce a new Result instance
     * @param <S>    the generic type of the new Result instance
     * @return a new Result instance created by applying the function to the value, if the value exists or a new empty
     * Result of type S containing the remaining bytes of this instance.
     */
    public <S> Result<S> map(Function<? super T, S> mapper) {
        return flatMap(v -> of(mapper.apply(v), remaining));
    }

    /**
     * Constructs a new Result instance by applying the argument BiFunction to the underlying value and remaining
     * available bytes if a value exists.  If there isn't a value available calls to this function will return an empty
     * Result of generic type S containing the remaining bytes of this instance.
     *
     * @param next the function to apply to the possible value and remaining bytes to produce a new Result
     * @param <S>  the generic type of the new Result instance
     * @return a new Result instance created by applying the function to the value and the remaining bytes, if the value
     * exists or a new empty Result of type S containing the remaining bytes of this instance.
     */
    public <S> Result<S> andThen(BiFunction<T, ByteString, Result<S>> next) {
        return flatMap(v -> next.apply(v, remaining));
    }

    /**
     * Constructs a new Result instance from the argument value and ByteString.  Calls to this function are Null safe
     * in that null values are wrapped in an Optional instance, and null remaining bytes are transformed in an empty
     * ByteString instance.
     *
     * @param value     the value of the result
     * @param remaining the remaining available bytes
     * @param <S>       the generic type of the value
     * @return a new Result instance constructed from the argument value and ByteString
     */
    public static <S> Result<S> of(S value, ByteString remaining) {
        return new Result<>(Optional.ofNullable(value), Optional.ofNullable(remaining).orElse(ByteString.empty()));
    }

    /**
     * Constructs a new Result instance with no value and zero remaining available bytes.
     *
     * @param <S> the generic type of the missing value
     * @return a new Result instance with no value and zero remaining available bytes.
     */
    public static <S> Result<S> of() {
        return of(ByteString.empty());
    }

    /**
     * Constructs a new Result instance with no value and the argument remaining available bytes.
     *
     * @param <S> the generic type of the missing value
     * @return a new Result instance with no value and the argument remaining available bytes.
     */
    public static <S> Result<S> of(ByteString remaining) {
        return new Result<>(Optional.empty(), remaining);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result<?> result = (Result<?>) o;
        return value.equals(result.value) && remaining.equals(result.remaining);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = value.hashCode();
        result = 31 * result + remaining.hashCode();
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Result{value=" + value + ", remainingLength=" + remaining.length() + '}';
    }
}