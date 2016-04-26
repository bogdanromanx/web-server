package com.github.bogdanromanx.web.server.framing;

import akka.util.ByteString;
import com.github.bogdanromanx.web.server.parsing.EmptyLineParser;
import com.github.bogdanromanx.web.server.parsing.HeaderParser;
import com.github.bogdanromanx.web.server.parsing.ParsingException;
import com.github.bogdanromanx.web.server.parsing.Result;
import com.github.bogdanromanx.web.server.settings.ParsingConfig;
import com.github.bogdanromanx.web.server.types.HttpHeader;
import com.github.bogdanromanx.web.server.types.headers.ContentLength;
import com.github.bogdanromanx.web.server.types.headers.RawHeader;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

/**
 * {@link FramingStageState} implementation that attempts to produce either a {@link RequestFrame.HeaderFrame} instance
 * from the input bytes or consume an empty line.  Upon successful completion, the next returned state will be:
 * <ul>
 * <li>a {@link ExpectingEmptyLineOrHeader} if there weren't sufficient bytes to produce a frame</li>
 * <li>a {@link ExpectingEmptyLineOrHeader} if a {@link RequestFrame.HeaderFrame} was produced</li>
 * <li>a {@link ExpectingEntityChunk} if a new line was consumed and the client sent a {@link ContentLength} header</li>
 * <li>a {@link ExpectingRequestLine} if a new line was consumed and the client did not send a {@link ContentLength} header</li>
 * </ul>
 */
class ExpectingEmptyLineOrHeader implements FramingStageState {

    private final ParsingConfig config;
    private final List<RawHeader> headers;
    private final Result<RequestFrame> result;

    /**
     * Constructs a new {@link ExpectingEmptyLineOrHeader} state from the argument parsing 'config', accumulated
     * 'buffer' and accumulated 'headers'.
     *
     * @param config  the parsing configuration
     * @param buffer  the accumulated buffer
     * @param headers the accumulated collection of headers
     * @throws NullPointerException of null config, buffer or collection of headers
     */
    ExpectingEmptyLineOrHeader(ParsingConfig config, ByteString buffer, Collection<RawHeader> headers) {
        this.config = requireNonNull(config);
        this.headers = new LinkedList<>(requireNonNull(headers));
        this.result = apply(requireNonNull(buffer));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FramingStageState next(ByteString bytes) {
        Optional<RequestFrame> frame = frame();
        ByteString remaining = result.remaining().concat(bytes);

        if (!frame.isPresent()) {
            return new ExpectingEmptyLineOrHeader(config, remaining, headers);
        }

        RequestFrame value = frame.get();
        if (value instanceof RequestFrame.HeaderFrame) {
            RequestFrame.HeaderFrame headerFrame = (RequestFrame.HeaderFrame) value;
            List<RawHeader> nextHeaders = new LinkedList<>(headers);
            nextHeaders.add(headerFrame.header());
            return new ExpectingEmptyLineOrHeader(config, remaining, nextHeaders);
        } else if (value instanceof RequestFrame.EntityStart) {
            RequestFrame.EntityStart entityStart = (RequestFrame.EntityStart) value;
            return new ExpectingEntityChunk(config, remaining, entityStart.length());
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
     * Attempts to apply the {@link EmptyLineParser} to the argument 'buffer', falling back on applying the
     * {@link HeaderParser} to the argument 'buffer' in case of a {@link ParsingException}.  Bubbles up all exceptions
     * thrown by the {@link HeaderParser}.
     *
     * @see EmptyLineParser
     * @see HeaderParser
     */
    private Result<RequestFrame> apply(ByteString buffer) {
        try {
            return applyEmptyLine(buffer);
        } catch (ParsingException e) {
            return applyHeader(buffer);
        }
    }

    /**
     * Attempts to apply the {@link EmptyLineParser} to the argument 'buffer'.  Bubbles up all exceptions.
     *
     * @see EmptyLineParser
     */
    private Result<RequestFrame> applyEmptyLine(ByteString buffer) {
        EmptyLineParser emptyLineParser = new EmptyLineParser();
        return emptyLineParser.apply(buffer).map(nu ->
                entityLength()
                        .<RequestFrame>map(RequestFrame::start)
                        .orElse(RequestFrame.end())
        );
    }

    /**
     * Attempts to apply the {@link HeaderParser} to the argument 'buffer'.  Bubbles up all exceptions.
     *
     * @see HeaderParser
     */
    private Result<RequestFrame> applyHeader(ByteString buffer) {
        HeaderParser parser = new HeaderParser(config.headerNameLength(), config.headerValueLength());
        return parser.apply(buffer).map(RequestFrame::header);
    }

    /**
     * Attempts to determine whether an {@link com.github.bogdanromanx.web.server.types.HttpEntity} is to be expected
     * and its length by examining the current headers.  If one of the currently accumulated headers is a
     * {@link ContentLength} its value is returned wrapped in an {@link Optional} instance, otherwise
     * <code>Optional.empty()</code> is returned.
     *
     * @return an inhabited {@link Optional} of {@link Long} if there's a {@link ContentLength} header present in the
     * list of accumulated headers (the value returned being that of the {@link ContentLength} header), or an
     * <code>Optional.empty()</code> otherwise.
     */
    private Optional<Long> entityLength() {
        return headers.stream()
                .flatMap(rh -> {
                    Optional<ContentLength> opt = ContentLength.of(rh);
                    if (opt.isPresent()) {
                        return Stream.of(opt.get());
                    }
                    return Stream.empty();
                })
                .findFirst()
                .map(HttpHeader::value)
                .filter(cl -> cl > 0);
    }
}
