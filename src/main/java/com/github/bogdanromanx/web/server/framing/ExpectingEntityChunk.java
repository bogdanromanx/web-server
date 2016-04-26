package com.github.bogdanromanx.web.server.framing;

import akka.util.ByteString;
import com.github.bogdanromanx.web.server.parsing.Result;
import com.github.bogdanromanx.web.server.settings.ParsingConfig;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * {@link FramingStageState} implementation that attempts to produce a {@link RequestFrame.EntityFrame} instance
 * from the input bytes.  Upon successful completion, the next returned state will be either an instance of
 * {@link ExpectingEntityChunk} if the entity bytes are not fully read or a {@link ExpectingEntityEnd} otherwise.
 */
class ExpectingEntityChunk implements FramingStageState {

    private final ParsingConfig config;
    private final Result<RequestFrame> result;
    private final long length;

    /**
     * Constructs a new {@link ExpectingEntityChunk} state from the argument parsing 'config', accumulated 'buffer' and
     * remaining entity 'length'.
     *
     * @param config the parsing configuration
     * @param buffer the accumulated buffer
     * @param length the remaining number of bytes of the entity
     * @throws NullPointerException of null config or buffer
     */
    ExpectingEntityChunk(ParsingConfig config, ByteString buffer, long length) {
        this.config = requireNonNull(config);
        this.result = apply(requireNonNull(buffer), length);
        this.length = length - buffer.length();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FramingStageState next(ByteString bytes) {
        if (length > 0) {
            return new ExpectingEntityChunk(config, result.remaining().concat(bytes), length);
        } else {
            return new ExpectingEntityEnd(config, result.remaining().concat(bytes));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasBytes() {
        return !result.remaining().isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<RequestFrame> frame() {
        return result.value();
    }

    /**
     * Attempts to extract a maximum of 'length' bytes from the 'buffer' and return them wrapped within a {@link Result}
     * instance.
     *
     * @param buffer the accumulated bytes
     * @param length the remaining number of bytes of this entity
     * @return an empty {@link Result} for an empty 'buffer', the full buffer if the remaining number of bytes of this
     * entity is higher that the length of the 'buffer' or the first 'length' bytes from the buffer.
     */
    private Result<RequestFrame> apply(ByteString buffer, long length) {
        int bufferLength = buffer.length();
        if (bufferLength == 0) {
            return Result.of();
        }

        int chunkSize;
        if (length >= bufferLength) {
            chunkSize = bufferLength;
        } else {
            chunkSize = (int) length; // safe
        }

        ByteString chunkBytes = buffer.slice(0, chunkSize);
        ByteString remaining = buffer.slice(chunkSize, bufferLength);
        RequestFrame.EntityFrame frame = RequestFrame.bytes(chunkBytes);
        return Result.of(frame, remaining);
    }
}
