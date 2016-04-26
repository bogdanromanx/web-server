package com.github.bogdanromanx.web.server.parsing;

import akka.util.ByteString;
import com.github.bogdanromanx.web.server.types.headers.RawHeader;

/**
 * <p>
 * Http Header parser implementation.  It attempts to produce a {@link RawHeader} instance from a {@link ByteString}.
 * </p>
 * <p>
 * The parsing result can either hold a {@link RawHeader} value and some remaining bytes in case of a successful parsing
 * attempt, hold no value if there are not enough bytes to produce a {@link RawHeader} or throw a
 * {@link ParsingException.IllegalFormat} if the bytes used in the parsing attempt do not represent a valid http header.
 * </p>
 */
public class HeaderParser extends AbstractParser<RawHeader> {

    /**
     * The maximum number of bytes to be considered when parsing the
     * {@link com.github.bogdanromanx.web.server.types.HttpHeader} 'name' segment.
     */
    private final int maxNameLength;

    /**
     * The maximum number of bytes to be considered when parsing the
     * {@link com.github.bogdanromanx.web.server.types.HttpHeader} 'value' segment.
     */
    private final int maxValueLength;

    /**
     * Constructs a new {@link HeaderParser} instance with the argument max byte sizes for name and value.
     *
     * @param maxNameLength  the maximum number of bytes allowed to form the header 'name'
     * @param maxValueLength the maximum number of bytes allowed to form the header 'value'
     */
    public HeaderParser(int maxNameLength, int maxValueLength) {
        this.maxNameLength = maxNameLength;
        this.maxValueLength = maxValueLength;
    }

    /**
     * Constant value for the bytes representing the ':' character.
     */
    private static final ByteString COLON = ByteString.fromString(":");

    /**
     * <p>
     * Attempts to parse the argument 'byteString' into a {@link RawHeader}.
     * </p>
     * <p>
     * The parsing result can either hold a {@link RawHeader} value and some remaining bytes in case
     * of a successful parsing attempt, hold no value if there are not enough bytes to produce a {@link RawHeader} or
     * throw a {@link ParsingException.IllegalFormat} if the bytes used in the parsing attempt do not represent a valid
     * http header.
     * </p>
     *
     * @param byteString the bytes to parse into a {@link RawHeader}
     * @return a {@link Result} instance that can either hold a {@link RawHeader} value and some remaining bytes in
     * case of a successful parsing attempt, hold no value if there are not enough bytes to produce a
     * {@link RawHeader} or throw a {@link ParsingException.IllegalFormat} if the bytes used in the parsing attempt do
     * not represent a valid http header.
     * @throws ParsingException.IllegalFormat if no http header can be produced within its computed max size; the max
     *                                        size is the determined by the max name and value lengths plus one for the
     *                                        ':' character.
     * @see Result
     */
    @Override
    public Result<RawHeader> apply(ByteString byteString) {
        try {
            return take(byteString, COLON, maxNameLength).andThen((name, afterName) ->
                    take(afterName, NEW_LINE, maxValueLength).andThen((value, remaining) ->
                            Result.of(RawHeader.of(asciiString(name).trim(), asciiString(value).trim()), remaining)
                    )
            );
        } catch (IllegalArgumentException e) {
            throw new ParsingException.IllegalHttpHeader(e);
        }
    }
}