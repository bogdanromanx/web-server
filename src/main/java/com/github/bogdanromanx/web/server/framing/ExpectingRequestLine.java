package com.github.bogdanromanx.web.server.framing;

import akka.util.ByteString;
import com.github.bogdanromanx.web.server.parsing.RequestLineParser;
import com.github.bogdanromanx.web.server.parsing.Result;
import com.github.bogdanromanx.web.server.settings.ParsingConfig;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * {@link FramingStageState} implementation that attempts to produce a {@link RequestFrame.RequestLineFrame} instance
 * from the input bytes.  Upon successful completion, the next returned state will be an instance of
 * {@link ExpectingHeader}.
 */
class ExpectingRequestLine implements FramingStageState {

    private final ParsingConfig config;
    private final Result<RequestFrame> result;

    /**
     * Constructs a new {@link ExpectingRequestLine} state from the argument parsing 'config' and accumulated 'buffer'.
     *
     * @param config the parsing configuration
     * @param buffer the accumulated buffer
     * @throws NullPointerException of null config or buffer
     */
    ExpectingRequestLine(ParsingConfig config, ByteString buffer) {
        this.config = requireNonNull(config);
        this.result = apply(requireNonNull(buffer));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FramingStageState next(ByteString bytes) {
        Optional<RequestFrame> frame = frame();
        ByteString remaining = result.remaining().concat(bytes);
        if (frame.isPresent()) {
            return new ExpectingHeader(config, remaining);
        } else {
            return new ExpectingRequestLine(config, remaining);
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
     * Applies the {@link RequestLineParser} to the argument 'buffer'.  Bubbles up all parsing exceptions.
     *
     * @see RequestLineParser
     */
    private Result<RequestFrame> apply(ByteString buffer) {
        RequestLineParser parser = new RequestLineParser(config.methodLength(), config.uriLength(), config.protocolLength());
        return parser.apply(buffer).map(RequestFrame::requestLine);
    }
}
