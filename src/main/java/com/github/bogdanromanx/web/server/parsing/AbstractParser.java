package com.github.bogdanromanx.web.server.parsing;

import akka.util.ByteString;

@SuppressWarnings("WeakerAccess")
public abstract class AbstractParser<T> implements Parser<T> {

    static char charAt(ByteString input, int idx) {
        return (char) (input.apply(idx) & 0xFF);
    }

    static String asciiString(ByteString input) {
        StringBuilder builder = new StringBuilder();
        int length = input.length();
        if (length == 0) {
            return "";
        } else {
            int idx = 0;
            while (idx < length) {
                builder.append(charAt(input, idx));
                idx++;
            }
            return builder.toString();
        }
    }

    static Result<ByteString> take(ByteString input, ByteString sep, int maxLength, boolean dropSep) {
        // todo slice before computing index
        int idx = input.indexOfSlice(sep);
        if (idx > maxLength || (idx == -1 && maxLength < input.length())) {
            throw new ParsingException.IllegalFormat();
        } else if (idx == -1) {
            return Result.of(input);
        } else {
            int drop = dropSep ? idx + sep.length() : idx;
            return Result.of(input.slice(0, idx), input.drop(drop));
        }
    }

    static Result<ByteString> take(ByteString input, ByteString sep, int maxLength) {
        return take(input, sep, maxLength, true);
    }

    static ByteString NEW_LINE = ByteString.fromString("\r\n");
}
