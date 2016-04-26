package com.github.bogdanromanx.web.server.framing;

import akka.util.ByteString;
import com.github.bogdanromanx.web.server.types.RequestLine;
import com.github.bogdanromanx.web.server.types.headers.RawHeader;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * Data type definition for an http request frame.  Frames represent parts of
 * {@link com.github.bogdanromanx.web.server.types.HttpRequest}.
 *
 * @see RequestLineFrame
 * @see HeaderFrame
 * @see EntityStart
 * @see EntityFrame
 * @see EntityEnd
 */
@SuppressWarnings("WeakerAccess")
public abstract class RequestFrame {

    // seals this class
    private RequestFrame() {
    }

    /**
     * Constructs a new {@link RequestLineFrame} from the argument 'requestLine'.
     *
     * @param requestLine the {@link RequestLine} to wrap into a {@link RequestLineFrame}
     * @return a new {@link RequestLineFrame} from the argument 'requestLine'
     * @throws NullPointerException for null 'requestLine' argument
     */
    public static RequestLineFrame requestLine(RequestLine requestLine) {
        return new RequestLineFrame(requestLine);
    }

    /**
     * Constructs a new {@link HeaderFrame} from the argument 'header'.
     *
     * @param header the {@link RawHeader} to wrap into a {@link HeaderFrame}
     * @return a new {@link HeaderFrame} from the argument 'header'
     * @throws NullPointerException for null 'header' argument
     */
    public static HeaderFrame header(RawHeader header) {
        return new HeaderFrame(header);
    }

    /**
     * Constructs a new {@link EntityStart} from the argument 'length'.
     *
     * @param length the expected number of bytes of the {@link com.github.bogdanromanx.web.server.types.HttpEntity}
     * @return a new {@link EntityStart} from the argument 'length'
     */
    public static EntityStart start(long length) {
        return new EntityStart(length);
    }

    /**
     * Constructs a new {@link EntityFrame} from the argument 'bytes'.
     *
     * @param bytes a part of the {@link com.github.bogdanromanx.web.server.types.HttpEntity}
     * @return a new {@link EntityFrame} from the argument 'bytes'
     */
    public static EntityFrame bytes(ByteString bytes) {
        return new EntityFrame(bytes);
    }

    /**
     * @return a reference to the {@link EntityEnd} singleton instance
     */
    public static EntityEnd end() {
        return EntityEnd.instance();
    }

    /**
     * {@link RequestFrame} that wraps a {@link RequestLine}.
     */
    public static final class RequestLineFrame extends RequestFrame {
        private final RequestLine requestLine;

        /**
         * Constructs a new {@link RequestLineFrame} from the argument 'requestLine'.
         *
         * @param requestLine the {@link RequestLine} of the newly constructed {@link RequestLineFrame}
         * @throws NullPointerException for a null 'requestLine' argument
         */
        private RequestLineFrame(RequestLine requestLine) {
            this.requestLine = requireNonNull(requestLine, "RequestLine cannot be null");
        }

        /**
         * @return the {@link RequestLine} within this frame.
         */
        public RequestLine requestLine() {
            return requestLine;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RequestLineFrame that = (RequestLineFrame) o;
            return requestLine.equals(that.requestLine);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return requestLine.hashCode();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "RequestLineFrame{requestLine=" + requestLine + '}';
        }
    }

    /**
     * {@link RequestFrame} that wraps a {@link RawHeader}.
     */
    public static final class HeaderFrame extends RequestFrame {

        private final RawHeader header;

        /**
         * Constructs a new {@link HeaderFrame} from the argument 'header'.
         *
         * @param header the {@link RawHeader} of the newly constructed {@link HeaderFrame}
         * @throws NullPointerException for a null 'header' argument
         */
        private HeaderFrame(RawHeader header) {
            this.header = requireNonNull(header, "Header argument cannot be null");
        }

        /**
         * @return the {@link RawHeader} within this frame.
         */
        public RawHeader header() {
            return header;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            HeaderFrame that = (HeaderFrame) o;
            return header.equals(that.header);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return header.hashCode();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "HeaderFrame{header=" + header + '}';
        }
    }

    /**
     * {@link RequestFrame} that wraps a {@link Long} value representing the total size of the
     * {@link com.github.bogdanromanx.web.server.types.HttpEntity}.
     */
    public static final class EntityStart extends RequestFrame {
        private final long length;

        /**
         * Constructs a new {@link EntityStart} from the argument 'length'.
         *
         * @param length the total size of the {@link com.github.bogdanromanx.web.server.types.HttpEntity}
         */
        private EntityStart(long length) {
            this.length = length;
        }

        /**
         * @return the total size of the {@link com.github.bogdanromanx.web.server.types.HttpEntity}
         */
        public long length() {
            return length;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EntityStart that = (EntityStart) o;
            return length == that.length;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return (int) (length ^ (length >>> 32));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "EntityStart{length=" + length + '}';
        }
    }

    /**
     * {@link RequestFrame} that wraps a {@link ByteString} value representing the a part of the
     * {@link com.github.bogdanromanx.web.server.types.HttpEntity}.
     */
    public static final class EntityFrame extends RequestFrame {

        private final ByteString bytes;

        /**
         * Constructs a new {@link EntityFrame} from the argument 'bytes'.
         *
         * @param bytes a part of the {@link com.github.bogdanromanx.web.server.types.HttpEntity}
         */
        private EntityFrame(ByteString bytes) {
            this.bytes = Optional.ofNullable(bytes).orElse(ByteString.empty());
        }

        /**
         * @return the bytes wrapped within this {@link EntityFrame}
         */
        public ByteString bytes() {
            return bytes;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            EntityFrame that = (EntityFrame) o;
            return bytes.equals(that.bytes);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return bytes.hashCode();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "EntityFrame{bytesLength=" + bytes.length() + '}';
        }
    }

    /**
     * {@link RequestFrame} that signals the end of an {@link com.github.bogdanromanx.web.server.types.HttpEntity}.
     */
    public static final class EntityEnd extends RequestFrame {
        private static EntityEnd instance = new EntityEnd();

        private EntityEnd() {
        }

        private static EntityEnd instance() {
            return instance;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "EntityEnd{}";
        }
    }
}
