package com.github.bogdanromanx.web.server.parsing;

import akka.util.ByteString;
import com.github.bogdanromanx.web.server.types.HttpMethod;
import com.github.bogdanromanx.web.server.types.HttpProtocol;
import com.github.bogdanromanx.web.server.types.RequestLine;

import java.net.URI;

import static com.github.bogdanromanx.web.server.parsing.ParsingException.*;

/**
 * <p>
 * Http RequestLine parser implementation.  It attempts to produce a {@link RequestLine} instance from a
 * {@link ByteString}.
 * </p>
 * <p>
 * The parsing result can either hold a {@link RequestLine} value and some remaining bytes in case
 * of a successful parsing attempt, hold no value if there are not enough bytes to produce a {@link RequestLine} or
 * throw a {@link ParsingException} if the bytes used in the parsing attempt do not represent a valid http request line.
 * </p>
 */
public class RequestLineParser extends AbstractParser<RequestLine> {

    /**
     * The maximum number of bytes to be considered when parsing the {@link HttpMethod} segment.
     */
    private final int maxMethodLength;

    /**
     * The maximum number of bytes to be considered when parsing the {@link URI} segment.
     */
    private final int maxURILength;

    /**
     * The maximum number of bytes to be considered when parsing the {@link HttpProtocol} segment.
     */
    private final int maxProtocolLength;

    /**
     * Constructs a new {@link RequestLineParser} instance with the argument max byte sizes for method, uri and
     * protocol.
     *
     * @param maxMethodLength   the maximum number of bytes allowed to form a {@link HttpMethod}
     * @param maxURILength      the maximum number of bytes allowed to form a {@link URI}
     * @param maxProtocolLength the maximum number of bytes allowed to form a {@link HttpProtocol}
     */
    public RequestLineParser(int maxMethodLength, int maxURILength, int maxProtocolLength) {
        this.maxMethodLength = maxMethodLength;
        this.maxURILength = maxURILength;
        this.maxProtocolLength = maxProtocolLength;
    }

    /**
     * Single whitespace {@link ByteString} representation.
     */
    private static ByteString SP = ByteString.fromString(" ");

    /**
     * <p>
     * Attempts to parse the argument 'byteString' into a {@link RequestLine}.
     * </p>
     * <p>
     * The parsing result can either hold a {@link RequestLine} value and some remaining bytes in case
     * of a successful parsing attempt, hold no value if there are not enough bytes to produce a {@link RequestLine} or
     * throw a {@link ParsingException} if the bytes used in the parsing attempt do not represent a valid http request
     * line.
     * </p>
     *
     * @param byteString the bytes to parse into a {@link RequestLine}
     * @return a {@link Result} instance that can either hold a {@link RequestLine} value and some remaining bytes in
     * case of a successful parsing attempt, hold no value if there are not enough bytes to produce a
     * {@link RequestLine} or throw a {@link ParsingException} if the bytes used in the parsing attempt do not represent
     * a valid http request line.
     * @throws IllegalFormat       if no http method, uri or protocol can be produced within the their respective max
     *                             allowed sizes
     * @throws IllegalHttpMethod   if an http method cannot be constructed from the first non whitespace bytes
     * @throws IllegalURI          if an {@link URI} instance cannot be constructed from the uri segment bytes
     * @throws IllegalHttpProtocol if an {@link HttpProtocol} instance cannot be constructed from the protocol segment
     *                             bytes
     * @see Result
     */
    @Override
    public Result<RequestLine> apply(ByteString byteString) {
        return Result.of(RequestLine.builder(), byteString)
                .andThen((builder, input) -> parseMethod(input).map(builder::method))
                .andThen((builder, input) -> parseURI(input).map(builder::uri))
                .andThen((builder, input) -> parseProtocol(input).map(builder::protocol))
                .map(RequestLine.Builder::build);
    }

    /**
     * Attempts to parse the argument 'input' bytes into a {@link HttpMethod} instance.
     *
     * @param input the bytes to parse
     * @return a {@link Result} instance that can either hold a {@link HttpMethod} value and some remaining bytes in
     * case of a successful parsing attempt, hold no value if there are not enough bytes to produce a
     * {@link HttpMethod} or throw a {@link IllegalFormat} if the bytes used in the parsing attempt do not represent
     * a valid http method.
     * @throws IllegalFormat     if there's no whitespace to be found within the first
     *                           {@link RequestLineParser#maxMethodLength} bytes
     * @throws IllegalHttpMethod if the bytes consumed from the input (until the first whitespace) cannot form an
     *                           {@link HttpMethod}
     */
    private Result<HttpMethod> parseMethod(ByteString input) {
        return take(input, SP, maxMethodLength).map(bytes -> {
            try {
                return HttpMethod.of(asciiString(bytes));
            } catch (IllegalArgumentException e) {
                throw new IllegalHttpMethod(e);
            }
        });
    }

    /**
     * Attempts to parse the argument 'input' bytes into a {@link URI} instance.
     *
     * @param input the bytes to parse
     * @return a {@link Result} instance that can either hold a {@link URI} value and some remaining bytes in case of a
     * successful parsing attempt or hold no value if there are not enough bytes to produce an {@link URI} instance.
     * @throws IllegalFormat if the bytes consumed from the input (until the first whitespace) cannot form an URI or
     *                       there's no whitespace to be found within the first {@link RequestLineParser#maxURILength}
     *                       bytes.
     * @throws IllegalURI    if the bytes consumed from the input cannot be transformed into a valid {@link URI}
     */
    private Result<URI> parseURI(ByteString input) {
        return take(input, SP, maxURILength).map(bytes -> {
            try {
                return URI.create(asciiString(bytes));
            } catch (IllegalArgumentException e) {
                throw new ParsingException.IllegalURI(e);
            }
        });
    }

    /**
     * Attempts to parse the argument 'input' bytes into a {@link HttpProtocol} instance.
     *
     * @param input the bytes to parse
     * @return a {@link Result} instance that can either hold a {@link HttpProtocol} value and some remaining bytes in
     * case of a successful parsing attempt or hold no value if there are not enough bytes to produce an
     * {@link HttpProtocol} instance.
     * @throws IllegalFormat       if the bytes consumed from the input (until the first whitespace) cannot form an
     *                             {@link HttpProtocol} instance or there's no new line to be found within the first
     *                             {@link RequestLineParser#maxProtocolLength} bytes.
     * @throws IllegalHttpProtocol if the bytes consumed from the input cannot be transformed into a valid
     *                             {@link HttpProtocol} instance
     */
    private Result<HttpProtocol> parseProtocol(ByteString input) {
        return take(input, NEW_LINE, maxProtocolLength).map(bytes ->
                HttpProtocol.of(asciiString(bytes))
                        .<ParsingException.IllegalHttpProtocol>orElseThrow(ParsingException.IllegalHttpProtocol::new)
        );
    }
}
