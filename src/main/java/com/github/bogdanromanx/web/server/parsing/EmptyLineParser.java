package com.github.bogdanromanx.web.server.parsing;

import akka.NotUsed;
import akka.util.ByteString;

/**
 * Parser implementation that attempts to consume either "\r\n" or "\n" from the input bytes, producing no usable
 * value.
 */
public class EmptyLineParser extends AbstractParser<NotUsed> {

    /**
     * Attempts to consume either "\r\n" or "\n" from the input bytes.
     *
     * @param input the bytes to consume the head empty line from
     * @return a {@link Result} inhabited by a {@link NotUsed} instance in case of a successful parse, or uninhabited
     * if there are no sufficient bytes in the input.
     * @throws ParsingException.ExpectingEmptyLine if the head of the input does not match either "\r\n" or "\n"
     */
    @Override
    public Result<NotUsed> apply(ByteString input) {
        int length = input.length();
        if (length > 1 && charAt(input, 0) == '\r' && charAt(input, 1) == '\n') {
            return Result.of(NotUsed.getInstance(), input.drop(2));
        } else if (length > 0 && (charAt(input, 0) == '\n')) {
            return Result.of(NotUsed.getInstance(), input.drop(1));
        } else if (length == 0) {
            return Result.of();
        }
        throw new ParsingException.ExpectingEmptyLine();
    }
}
