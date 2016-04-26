package com.github.bogdanromanx.web.server.types;

import akka.NotUsed;
import akka.stream.Materializer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.util.ByteString;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.Objects.requireNonNull;

/**
 * Data type definition for the http message entity (message body).
 *
 * @see HttpEntity.Strict
 * @see HttpEntity.Streaming
 * @see HttpEntity.Empty
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class HttpEntity {

    // seals this class
    private HttpEntity() {
    }

    /**
     * @return the number of bytes of <code>this</code> entity.
     */
    public abstract long length();

    /**
     * Constructs a new {@link HttpEntity.Strict} instance from the argument bytes.
     *
     * @param bytes the bytes of the newly created {@link HttpEntity.Strict} entity
     * @return a new {@link HttpEntity.Strict} instance from the argument bytes.
     * @see HttpEntity.Strict
     */
    public static HttpEntity.Strict strict(ByteString bytes) {
        return new Strict(bytes);
    }

    /**
     * Constructs a new {@link HttpEntity.Streaming} instance from the 'source' and 'length' arguments.
     *
     * @param source the source of bytes of the newly created {@link HttpEntity.Streaming} instance; should NOT be
     *               consumed
     * @param length the total number of bytes that the 'source' will produce
     * @return a new {@link HttpEntity.Streaming} instance from the from the 'source' and 'length' arguments.
     * @throws NullPointerException     if the source argument is null
     * @throws IllegalArgumentException if the length value is smaller than 0
     * @see HttpEntity.Streaming
     */
    public static HttpEntity.Streaming streaming(Source<ByteString, ?> source, long length) {
        return new Streaming(source, length);
    }

    /**
     * @return a reference to the {@link HttpEntity.Empty} singleton.
     * @see HttpEntity.Empty
     */
    public static HttpEntity.Empty empty() {
        return Empty.instance();
    }

    /**
     * {@link HttpEntity} implementation for entities that are small enough to be fully read in memory.  Instances can
     * be constructed via {@link HttpEntity#strict(ByteString)}.  The length value is directly read off of the
     * {@link ByteString} provided when constructed.
     */
    public static final class Strict extends HttpEntity {

        private final ByteString bytes;
        private final long length;

        /**
         * Constructs a new {@link HttpEntity.Strict} instance from the 'bytes' argument.
         *
         * @param bytes the bytes that form the {@link HttpEntity}
         */
        private Strict(ByteString bytes) {
            this.bytes = Optional.ofNullable(bytes).orElse(ByteString.empty());
            length = this.bytes.length();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long length() {
            return length;
        }

        /**
         * @return the full bytes of <code>this</code> entity.
         */
        public ByteString bytes() {
            return bytes;
        }

        @Override
        public String toString() {
            return "HttpEntity.Strict{length=" + length + '}';
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Strict strict = (Strict) o;
            return bytes.equals(strict.bytes);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return bytes.hashCode();
        }
    }

    /**
     * <p>
     * {@link HttpEntity} implementation for entities that aren't small enough to be fully read in memory.  Instead, the
     * {@link HttpEntity.Streaming} entity provides a source to consume its bytes incrementally, back-pressured.
     * </p>
     * <p>
     * Instances can be constructed via {@link HttpEntity#streaming(Source, long)}.  Since the full number of bytes
     * cannot be determined without consuming the source, it needs to be provided when constructing the instance.
     * </p>
     * <p>
     * <strong>Note:</strong> it's important that {@link HttpEntity.Streaming} entities are consumed, otherwise
     * they can create back-pressure upstream.
     * </p>
     */
    public final static class Streaming extends HttpEntity {

        private final Source<ByteString, ?> source;
        private final long length;

        /**
         * Constructs a new {@link HttpEntity.Streaming} instance from the 'source' and 'length' arguments.
         *
         * @param source a source that emits chunks of {@link ByteString} on demand
         * @param length the full length of bytes of this entity
         * @throws NullPointerException     if the source argument is null
         * @throws IllegalArgumentException if the length value is smaller than 0
         */
        private Streaming(Source<ByteString, ?> source, long length) {
            this.source = requireNonNull(source, "HttpEntity.Streaming source argument cannot be null");
            this.length = length;
            if (this.length < 0) {
                throw new IllegalArgumentException("HttpEntity length value must be >= 0");
            }
        }

        /**
         * @return a reference to the source of bytes of this entity.
         */
        public Source<ByteString, ?> source() {
            return source;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long length() {
            return length;
        }

        /**
         * <p>Consumes the source enclosed in <code>this</code> entity, ignoring the bytes.  The returned
         * {@link CompletionStage} will be completed when all the bytes are successfully emitted by the source.
         * </p>
         * <p>
         * <strong>Note:</strong> {@link HttpEntity.Streaming} entities can be consumed only once.
         * </p>
         *
         * @param materializer the materializer used for running the flow created by applying an ignore sink to this
         *                     source
         * @return a {@link CompletionStage} that will be completed when all the bytes are successfully emitted by the
         * source.
         */
        public CompletionStage<NotUsed> consume(Materializer materializer) {
            return source.runWith(Sink.ignore(), materializer).thenApply(d -> NotUsed.getInstance());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "HttpEntity.Streaming{length=" + length + '}';
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Streaming entity = (Streaming) o;
            return length == entity.length && source.equals(entity.source);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            int result = source.hashCode();
            result = 31 * result + (int) (length ^ (length >>> 32));
            return result;
        }
    }

    /**
     * {@link HttpEntity} implementation of no bytes and zero length.
     */
    public static final class Empty extends HttpEntity {

        private static final Empty instance = new Empty();

        private Empty() {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long length() {
            return 0;
        }

        /**
         * @return a reference to the {@link HttpEntity.Empty} singleton instance.
         */
        public static Empty instance() {
            return instance;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "HttpEntity.Empty{length=0}";
        }
    }
}