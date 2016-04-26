package com.github.bogdanromanx.web.server.parsing;

import akka.util.ByteString;
import com.github.bogdanromanx.web.server.types.headers.RawHeader;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.junit.MatcherAssert.assertThat;

public class HeaderParserTest {

    private static final HeaderParser parser = new HeaderParser(10, 10);

    @Test
    public void validHeader() {
        ByteString input = ByteString.fromString("name:value\r\n");
        Result<RawHeader> expected = Result.of(RawHeader.of("name", "value"), ByteString.empty());
        assertThat(parser.apply(input), equalTo(expected));
    }

    @Test
    public void emptyHeader() {
        ByteString input = ByteString.fromString("name");
        assertThat(parser.apply(input), equalTo(Result.of(input)));
    }

    @Test(expected = ParsingException.IllegalHttpHeader.class)
    public void missingName() {
        ByteString input = ByteString.fromString(":value\r\n");
        parser.apply(input);
    }

    @Test(expected = ParsingException.IllegalHttpHeader.class)
    public void missingValue() {
        ByteString input = ByteString.fromString("name:\r\n");
        parser.apply(input);
    }

    @Test(expected = ParsingException.IllegalFormat.class)
    public void longName() {
        ByteString input = ByteString.fromString("TOO_LONG_HEADER_NAME:value\r\n");
        parser.apply(input);
    }

    @Test(expected = ParsingException.IllegalFormat.class)
    public void longValue() {
        ByteString input = ByteString.fromString("name:TOO_LONG_HEADER_VALUE\r\n");
        parser.apply(input);
    }
}
