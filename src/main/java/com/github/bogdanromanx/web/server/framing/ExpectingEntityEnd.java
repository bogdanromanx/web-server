package com.github.bogdanromanx.web.server.framing;

import akka.util.ByteString;
import com.github.bogdanromanx.web.server.parsing.Result;
import com.github.bogdanromanx.web.server.settings.ParsingConfig;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * {@link FramingStageState} implementation that produces a {@link RequestFrame.EntityEnd} instance regardless of the
 * input bytes.  The next returned state will be an instance of {@link ExpectingRequestLine} preserving the accumulated
 * buffer.
 */
class ExpectingEntityEnd implements FramingStageState {

    private final ParsingConfig config;
    private final Result<RequestFrame> result;

    /**
     * Constructs a new {@link ExpectingEntityEnd} state from the argument parsing 'config' and accumulated 'buffer'.
     *
     * @param config the parsing configuration
     * @param buffer the accumulated buffer
     * @throws NullPointerException of null config or buffer
     */
    ExpectingEntityEnd(ParsingConfig config, ByteString buffer) {
        this.config = requireNonNull(config);
        this.result = Result.of(RequestFrame.end(), requireNonNull(buffer));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FramingStageState next(ByteString bytes) {
        return new ExpectingRequestLine(config, result.remaining().concat(bytes));
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
}
