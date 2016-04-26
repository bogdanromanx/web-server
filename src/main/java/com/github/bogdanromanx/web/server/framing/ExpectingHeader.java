package com.github.bogdanromanx.web.server.framing;

import akka.util.ByteString;
import com.github.bogdanromanx.web.server.parsing.HeaderParser;
import com.github.bogdanromanx.web.server.parsing.Result;
import com.github.bogdanromanx.web.server.settings.ParsingConfig;

import java.util.Collections;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

/**
 * {@link FramingStageState} implementation that attempts to produce a {@link RequestFrame.HeaderFrame} instance
 * from the input bytes.  Upon successful completion, the next returned state will be an instance of
 * {@link ExpectingEmptyLineOrHeader}.
 */
class ExpectingHeader implements FramingStageState {

    private final ParsingConfig config;
    private final Result<RequestFrame.HeaderFrame> result;

    /**
     * Constructs a new {@link ExpectingHeader} state from the argument parsing 'config' and accumulated 'buffer'.
     *
     * @param config the parsing configuration
     * @param buffer the accumulated buffer
     * @throws NullPointerException of null config or buffer
     */
    ExpectingHeader(ParsingConfig config, ByteString buffer) {
        this.config = requireNonNull(config);
        this.result = apply(requireNonNull(buffer));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FramingStageState next(ByteString bytes) {
        Optional<RequestFrame.HeaderFrame> frame = result.value();
        ByteString remaining = result.remaining().concat(bytes);
        if (frame.isPresent()) {
            return new ExpectingEmptyLineOrHeader(config, remaining, Collections.singletonList(frame.get().header()));
        } else {
            return new ExpectingHeader(config, remaining);
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
        return result.value().map(hf -> hf);
    }

    /**
     * Applies the {@link HeaderParser} to the argument 'buffer'.  Bubbles up all parsing exceptions.
     *
     * @see HeaderParser
     */
    private Result<RequestFrame.HeaderFrame> apply(ByteString buffer) {
        HeaderParser parser = new HeaderParser(config.headerNameLength(), config.headerValueLength());
        return parser.apply(buffer).map(RequestFrame::header);
    }
}
